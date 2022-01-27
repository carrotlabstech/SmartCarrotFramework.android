package com.carrotlabs.smartcarrotframework.models

import com.carrotlabs.smartcarrotframework.*
import com.carrotlabs.smartcarrotframework.utils.StringSanitizer
import org.tensorflow.lite.Interpreter
import java.io.File
import java.net.URI
import java.nio.ByteBuffer
import java.nio.ByteOrder

// TODO: fix optionals, ? & !! for the model size
internal class OptimizedPrimeModel :
    ModelProtocol {
    private var _tensorCount: Int = 1
    private var _weightsTensor: IntArray? = null

    private var _interpreter: Interpreter? = null

    @Throws(LoadErrorCarrotContextError::class)
    constructor(tensorCount: Int = 1, weightsTensor: IntArray? = null) {
        val options = Interpreter.Options()

        _tensorCount = tensorCount
        _weightsTensor = weightsTensor

        if (weightsTensor != null && weightsTensor.count() > 0) {
            options.setWeightsTensor(_weightsTensor)
        }

        try {
            // Load model file from resouces, convert it to a byte buffer
            val classLoader = this.javaClass.classLoader
            val resource = classLoader?.getResourceAsStream("bumble.be")

            val modelByteArray = resource?.buffered()?.use { it.readBytes() }
            var byteBuffer = ByteBuffer.allocateDirect(modelByteArray?.size!!)
            byteBuffer.order(ByteOrder.nativeOrder())
            byteBuffer.put(modelByteArray)
            byteBuffer.position(0)

            _interpreter = Interpreter(byteBuffer, options)
        } catch (e: Exception) {
            _interpreter = null
            throw LoadErrorCarrotContextError()
        }
    }


    override fun categorise(
        transactionDescription: String,
        transactionAmount: Double,
        personal: CategorisationType
    ): Int {
        var result : Int

        val outputTensor = getOutputTensor(transactionDescription, transactionAmount)

        val winnerIndex =  outputTensor.indexOfMax()!!
        val winnerCertainty = outputTensor[winnerIndex]

        if (winnerCertainty > ModelContext.CERTAINTY_THRESHOLD) {
            // category id starts with 1, not 0
            result = winnerIndex.inc()
        } else {
            result = TransactionCategory.UNCATEGORISED_INT_ID
        }

        return result
    }

    override fun getOutputTensor(
        transactionDescription: String,
        transactionAmount: Double
    ): CategorizationOutputTensor {
        var result = FloatArray(TransactionCategory.NUMBER_OF_CATEGORIES)
        val description = StringSanitizer.cleanseString(transactionDescription)

        //let tensor = InputTensorBuilder().build(description: description)
        val tensor = ComplexEnglishInputTensorBuilder().build(description)
        var tensorInput = Array(1) { FloatArray(tensor.size) }
        tensorInput[0] = tensor
        val outputTensor = Array(1) { FloatArray(TransactionCategory.NUMBER_OF_CATEGORIES) }

        try {
            _interpreter?.run(tensorInput, outputTensor)
            result = outputTensor[0]
        } catch (e:Exception) {
            println("Output tensor exception happened")
        }

        return result
    }

    override fun learn(
        transactionDescription: String,
        transactionAmount: Double,
        personalCategoryId: Int
    ) {
        // not implemented / not required
    }

    override fun load() {
        // not implemented / not required
    }
}

internal fun FloatArray.indexOfMax(): Int? {
    return this.withIndex().maxBy { it.value }?.index
}
