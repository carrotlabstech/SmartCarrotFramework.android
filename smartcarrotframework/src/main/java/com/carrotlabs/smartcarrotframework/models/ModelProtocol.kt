package com.carrotlabs.smartcarrotframework.models

import com.carrotlabs.smartcarrotframework.CategorisationType

internal typealias CategorizationOutputTensor = FloatArray

internal interface ModelProtocol {
    fun categorise(transactionDescription: String, transactionAmount: Double, personal: CategorisationType) : Int
    fun getOutputTensor(transactionDescription: String, transactionAmount: Double) : CategorizationOutputTensor
    fun learn(transactionDescription: String, transactionAmount: Double, personalCategoryId: Int)
    fun load()
}