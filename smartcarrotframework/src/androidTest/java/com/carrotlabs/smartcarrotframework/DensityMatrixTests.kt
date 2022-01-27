package com.carrotlabs.smartcarrotframework

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.carrotlabs.smartcarrotframework.models.DensityMatrixBuilder
import com.carrotlabs.smartcarrotframework.models.DensityMatrixType
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*
import kotlin.math.abs

@RunWith(AndroidJUnit4::class)
internal class DensityMatrixTests {
    val _eps:Float = 0.0000001f
    val estimator = DensityMatrixBuilder(DensityMatrixType())

    @Test
    fun testLeftBoundaryIndex() {
        val xVal = arrayOf(-5f, 0f, 3f, 10f).toCollection(ArrayList())

        assertEquals(estimator.getLeftBoundaryIndex(-10f, xVal), 0)
        assertEquals(estimator.getLeftBoundaryIndex(-3f, xVal), 0)
        assertEquals(estimator.getLeftBoundaryIndex(0f, xVal), 0)
        assertEquals(estimator.getLeftBoundaryIndex(1f, xVal), 1)
        assertEquals(estimator.getLeftBoundaryIndex(11f, xVal), 2)
    }

    @Test
    fun testRightBoundaryIndex() {
        val xVal = arrayOf(-5f, 0f, 3f, 10f).toCollection(ArrayList())

        assertEquals(estimator.getRightBoundaryIndex(-10f, xVal), 1)
        assertEquals(estimator.getRightBoundaryIndex(-3f, xVal), 1)
        assertEquals(estimator.getRightBoundaryIndex(0f, xVal), 1)
        assertEquals(estimator.getRightBoundaryIndex(1f, xVal), 2)
        assertEquals(estimator.getRightBoundaryIndex(11f, xVal), 3)
    }

    @Test
    fun testGetInterpolation() {
        val xVal = arrayOf(-5f, 0f, 3f, 10f).toCollection(ArrayList())
        val yVal = arrayOf(10f,-10f,15f,-15f).toCollection(ArrayList())
        val xCoordinates = arrayOf(-10f, -3f, 0f, 1.5f, 5f, 500f).toCollection(ArrayList())

        val results = estimator.getInterpolation(xCoordinates, xVal, yVal)

        assertTrue(results[0] - 30 < _eps)
        assertTrue(results[1] - 2 < _eps)
        assertTrue(results[2] + 10 < _eps)
        assertTrue(results[3] - 2.5 < _eps)
        assertTrue(results[4] - 6.4285717 < _eps)
        assertTrue(results[5] + 2115 < _eps)
    }

    @Test
    fun testPrebuiltXCoordindates() {
        val estimator = DensityMatrixBuilder(DensityMatrixType())

        val xBasisCoordinates = estimator.buildXBasisCoordinates()

        assertTrue(xBasisCoordinates.count() == 251 * 2 + 1)
        assertTrue(xBasisCoordinates[4] + 801678.563 < _eps * 10000000)
        // ios
        //assertTrue(xBasisCoordinates[4] + 801678.563 , _eps)
        assertTrue(xBasisCoordinates[399] - 3372.87402 < _eps)
    }

    @Test
    fun testCleanDensityMatrix() {
        val dm = DensityMatrixBuilder(DensityMatrixType())
        val sum = abs(dm.DensityMatrix.sumByDouble { it.sumByDouble { it.toDouble() } }) - 8148.6003237

        assertTrue(sum < _eps)

        // ios
        //assertTrue(dm.DensityMatrix.sumByDouble { it.sumByDouble { it.toDouble() } } - 8144.8635 < _eps )
    }

    @Test
    fun testDensityMatrix() {
        val dm = DensityMatrixBuilder(DensityMatrixType())

        dm.learn(-200.0, TransactionCategory("groceries"))
        dm.learn(-250.0, TransactionCategory("groceries"))
        dm.learn(-120.0, TransactionCategory("groceries"))
        dm.learn(-60000.0, TransactionCategory("car_buy"))
        dm.learn(-80000.0, TransactionCategory("car_buy"))

        val sum = abs(dm.DensityMatrix.sumByDouble { it.sumByDouble { it.toDouble() } } - 8190.651307833)
        assertTrue(sum < _eps )

        // ios value is slightly different
    }

    @Test
    fun testLiklihood() {
        val dm = DensityMatrixBuilder(DensityMatrixType())

        dm.learn(-200.0, TransactionCategory("groceries"))
        dm.learn(-250.0, TransactionCategory("groceries"))
        dm.learn(-120.0, TransactionCategory("groceries"))
        dm.learn(-60000.0, TransactionCategory("car_buy"))
        dm.learn(-80000.0, TransactionCategory("car_buy"))

        assertEquals(dm.getLikelihood(100f).count(), 108)
        assertTrue(abs( dm.getLikelihood(-100000f)[75] - 0.810547) < _eps * 10000000)
        assertTrue(abs( dm.getLikelihood(-90000f )[75] - 1.0) < _eps * 100000)
        assertTrue(abs( dm.getLikelihood(-210f )[75] - 0.13854942) < _eps * 1000000 )

        assertTrue(abs( dm.getLikelihood(-500000f).sum() - 16.130833) < _eps * 10)
        assertTrue(abs( dm.getLikelihood(-90000f).sum() - 17.009333) < _eps * 10)
    }

    @Test
    fun testLikliHoodOfChildren() {
        val dm = DensityMatrixBuilder(DensityMatrixType())

        dm.learn(-1600.0, TransactionCategory("children"))
        dm.learn(-1600.0, TransactionCategory("children"))
        assertEquals(dm.getLikelihood(-1600f)[20], 1)
    }
}