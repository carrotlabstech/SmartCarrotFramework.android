package com.carrotlabs.smartcarrotframework.models

import com.carrotlabs.smartcarrotframework.CategorisationType
import com.carrotlabs.smartcarrotframework.utils.StringSanitizer
import com.carrotlabs.smartcarrotframework.TransactionCategory

/** @suppress **/
internal class MasterModel(persistence: PersistenceType, weightsTensor: IntArray? = null) :
    ModelProtocol {
    private var _optimizedPrimeModel: OptimizedPrimeModel
    private var _personalModel: PersonalModel

    // @Throws(FailedToLoadModelCategorisationContextError::class, LoadErrorCategorisationContextError::class)
    // these exceptions may happen
    init {
        _optimizedPrimeModel =
            OptimizedPrimeModel(
                1,
                weightsTensor
            )
        _personalModel =
            PersonalModel(persistence)
    }

    override fun categorise(
        transactionDescription: String,
        transactionAmount: Double,
        personal: CategorisationType
    ): Int {
        if (StringSanitizer.cleanseString(transactionDescription) == "") {
            return TransactionCategory.UNCATEGORISED_INT_ID
        }

        var outputTensor : CategorizationOutputTensor

        if (personal == CategorisationType.personal) {
            outputTensor = getOutputTensor(transactionDescription, transactionAmount)
        } else {
            outputTensor = _optimizedPrimeModel.getOutputTensor(transactionDescription, transactionAmount)
        }

        // Filter expenses/income out depending on the amount sign
        for (category in 0..outputTensor.count() - 1) {
            if (transactionAmount > 0 && !TransactionCategory.INCOME_CATEGORY_INT_IDS.contains(category)) {
                outputTensor[category] = 0f
            }

            if (transactionAmount < 0 && TransactionCategory.INCOME_CATEGORY_INT_IDS.contains(category)) {
                outputTensor[category] = 0f
            }
        }

        val winnerIndex = outputTensor.indexOfMax()!!
        val winnerCertainty = outputTensor[winnerIndex]

        val result : Int

        if (winnerCertainty > ModelContext.CERTAINTY_THRESHOLD) {
            // category id starts with 1, not 0
            result = winnerIndex + 1
        } else {
            result = TransactionCategory.UNCATEGORISED_INT_ID
        }

        return result
    }

    @Throws
    override fun getOutputTensor(
        transactionDescription: String,
        transactionAmount: Double
    ): CategorizationOutputTensor {
        val opModelTensor = _optimizedPrimeModel.getOutputTensor(transactionDescription, transactionAmount)
        val personalModelTensor = _personalModel.getOutputTensor(transactionDescription, transactionAmount)

        // this just sums elements of two arrays
        return opModelTensor.zip(personalModelTensor, Float::plus).toFloatArray()
    }

    @Throws
    override fun learn(
        transactionDescription: String,
        transactionAmount: Double,
        personalCategoryId: Int
    ) {
       _personalModel.learn(transactionDescription, transactionAmount, personalCategoryId)
    }

    override fun load() {
        _optimizedPrimeModel.load()
        _personalModel.load()
    }

}