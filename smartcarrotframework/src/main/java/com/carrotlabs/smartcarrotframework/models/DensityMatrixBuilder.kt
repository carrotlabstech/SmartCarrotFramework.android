package com.carrotlabs.smartcarrotframework.models

import com.carrotlabs.smartcarrotframework.TransactionCategory
import java.lang.Math.pow
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.sqrt

internal typealias DensityMatrixType = ArrayList<ArrayList<Float>>

internal class DensityMatrixBuilder {
    private val X_BASIS_COORDINATES_NUMBER_ONESIGN = 251
    private val X_BASIS_COORDINATES_NUMBER = 251 * 2 + 1
    private var _xBasisCoordinates = ArrayList<Float>()
    private var _densityMatrix = DensityMatrixType()
    private var _forgettingLambda = 0.1f

    internal val DensityMatrix: DensityMatrixType
        get() = _densityMatrix

    constructor(densityMatrix: DensityMatrixType) {
        _densityMatrix = densityMatrix

        if (_densityMatrix.count() == 0) {
            _densityMatrix = buildAndFillNewDensityMatrix()
        }

        _xBasisCoordinates = buildXBasisCoordinates()
    }

    internal fun buildXBasisCoordinates() : ArrayList<Float> {
        var positiveCoordinates:ArrayList<Float> = (MutableList(X_BASIS_COORDINATES_NUMBER_ONESIGN) { 0f }) as ArrayList<Float>

        for (index in 0..X_BASIS_COORDINATES_NUMBER_ONESIGN - 1) {
            positiveCoordinates[index] = pow(10.0, (index.toDouble() * (6.0 / (X_BASIS_COORDINATES_NUMBER_ONESIGN - 1).toDouble()))).toFloat()
        }

        var result = positiveCoordinates.map{ -it }.toMutableList()
        result.sort()
        result.add(0f)
        result.addAll(positiveCoordinates)

        return result as ArrayList<Float>
    }

    /// Finds the right upper boundary for a value. If the value exceeds all the elements in the array then
    /// the last index of the array is taken
    internal fun getRightBoundaryIndex(x:Float, xFuncCoordinates:ArrayList<Float>) : Int {
        for (i in 1..xFuncCoordinates.count() - 1 - 1) {
            if (x > xFuncCoordinates[xFuncCoordinates.count() - 1 - i]) {
                return xFuncCoordinates.count() - i
            }
        }

        return 1
    }

    /// Finds the left low boundary for a value. If the value is lesser than all the elements in the array then
    /// the first index of the array is taken
    internal fun getLeftBoundaryIndex(x:Float, xFuncCoordinates:ArrayList<Float>) : Int {
        for (i in 0..xFuncCoordinates.count() - 1 - 1) {

            // greater than OR equal is important so that we have different left and right indexes for a value
            // which equals one of the elements of the array
            if (x <= xFuncCoordinates[i + 1]) {
                return i
            }
        }

        return xFuncCoordinates.count() - 2
    }

    internal fun getInterpolation(xCoordindates: ArrayList<Float>, xFuncCoordinates:ArrayList<Float>, yFuncCoordindates:ArrayList<Float>) : ArrayList<Float> {
        var result:ArrayList<Float> = ArrayList<Float>()

        for (x in xCoordindates) {
            val leftIndex = getLeftBoundaryIndex(x, xFuncCoordinates)
            val rightIndex = getRightBoundaryIndex(x, xFuncCoordinates)

            var slope = (yFuncCoordindates[rightIndex] - yFuncCoordindates[leftIndex]) / (xFuncCoordinates[rightIndex] - xFuncCoordinates[leftIndex])
            slope = slope * (x - xFuncCoordinates[leftIndex]) + yFuncCoordindates[leftIndex]

            result.add(slope)
        }

        return result
    }

    internal fun getLikelihood(amount:Float) : ArrayList<Float> {
        var result = ArrayList<Float>()

        for (i in 0..TransactionCategory.NUMBER_OF_CATEGORIES - 1) {
            result.addAll(getInterpolation(arrayListOf(amount), _xBasisCoordinates, _densityMatrix[i]))
        }

        return result.map{ if (it > 1) 1 else it }.toMutableList() as ArrayList<Float>
    }

    // Updates internal density matrix. Does not save it in the persistent storage
    internal fun learn(amount:Double, category: TransactionCategory) {
        val sigma = abs(amount) / 5

        var additions = ArrayList<Float>()
        for (x in _xBasisCoordinates) {
            additions.add(normalDistribution(amount.toFloat(), sigma.toFloat(), x))
        }

        val binWidth = 0.06f
        var binMids = ArrayList<Float>()
        for (i in 0.._xBasisCoordinates.count() - 2) {
            binMids.add((_xBasisCoordinates[i] + _xBasisCoordinates[i + 1])/2)
        }

        val areaAdditions = getInterpolation(binMids, _xBasisCoordinates, additions).sum() * binWidth
                    // List -> ArrayList
        additions = ArrayList(additions.map({it / areaAdditions}))

        val categoryIndex = category.getIntId() - 1
                                        // List -> ArrayList
        _densityMatrix[categoryIndex] = ArrayList(_densityMatrix[categoryIndex].map({ it * ((1 - _forgettingLambda).toFloat()) }))
        for (i in 0..additions.count() - 1) {
            _densityMatrix[categoryIndex][i] += additions[i]
        }
    }

    private fun buildAndFillNewDensityMatrix() : ArrayList<ArrayList<Float>> {
        var result = ArrayList<ArrayList<Float>>()

        val noCols = X_BASIS_COORDINATES_NUMBER
        val noRows = TransactionCategory.NUMBER_OF_CATEGORIES
        for (i in 0..noRows - 1) {
            // add a new list of size noCols with repeatable value 0.15
            result.add(MutableList<Float>(noCols) { index -> 0.15f} as ArrayList<Float>)
        }

        return result
    }

    // Normal distribution
    // taken from here https://github.com/mbcltd/swift-normal-distribution/blob/master/swift-normal-distribution/NormalDistribution.swift
    private fun normalDistribution(μ:Float, σ:Float, x:Float) : Float {
        val a = exp( -1 * pow((x-μ).toDouble(), 2.0) / ( 2 * pow(σ.toDouble(),2.0) ) )
        val b = σ * sqrt( 2 * PI )
        return (a / b).toFloat()
    }
}