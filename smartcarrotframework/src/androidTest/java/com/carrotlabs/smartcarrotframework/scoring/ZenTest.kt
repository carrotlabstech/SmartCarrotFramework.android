package com.carrotlabs.smartcarrotframework.scoring

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.carrotlabs.smartcarrotframework.Budget
import com.carrotlabs.smartcarrotframework.BudgetFrequency
import com.carrotlabs.smartcarrotframework.Transaction
import com.jakewharton.threetenabp.AndroidThreeTen
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.threeten.bp.LocalDateTime
import java.math.BigDecimal

@RunWith(AndroidJUnit4::class)
internal class ZenTest {
    internal val _eps = 0.0002
    internal var _zenTestData : ZenTestData

    init {
        AndroidThreeTen.init(getContext())
        _zenTestData = ZenTestData()
    }

    fun getContext() : Context {
        return InstrumentationRegistry.getInstrumentation().targetContext
    }

    @Test
    fun testSpendingMatrix() {
        AndroidThreeTen.init(getContext())

        val zenBuilder = Zen(_zenTestData.Budgets, _zenTestData.Transactions)

        val spendingMatrix = zenBuilder.buildSpendingMatrix()

        for (i in 0..spendingMatrix.size - 1) {
            for (j in 0..spendingMatrix[i].size - 1) {
                carrotAssertEqual(spendingMatrix[i][j], _zenTestData.SpendingMatrix[i][j])
            }
        }

        // Control check :)
        carrotAssertEqual(spendingMatrix[0][1], _zenTestData.SpendingMatrix[0][1])
    }

    @Test
    fun testUtilisationMatrix() {
        val zenBuilder = Zen(transactions = _zenTestData.Transactions, budgets = _zenTestData.Budgets)

        val spendingMatrix = zenBuilder.buildSpendingMatrix()
        val utilisationMatrix = zenBuilder.buildUtilisationMatrix(spendingMatrix = spendingMatrix)

        for (i in 0..utilisationMatrix.size - 1) {
            for (j in 0..utilisationMatrix[i].size - 1) {
                carrotAssertEqual(utilisationMatrix[i][j], _zenTestData.UtilisationMatrix[i][j])
            }
        }

        // Control check
        carrotAssertEqual(utilisationMatrix[0][1], _zenTestData.UtilisationMatrix[0][1])
    }

    @Test
    fun testBuild() {
        val zenBuilder = Zen(transactions = _zenTestData.Transactions, budgets =_zenTestData.Budgets)

        val outputFrame = zenBuilder.buildOutputFrame()

        for (i in 0..outputFrame.size - 1) {
            Assert.assertTrue(outputFrame[i] ==_zenTestData.ZenItemsMid[i])
        }

        // control check ))
        Assert.assertEquals(outputFrame[1], _zenTestData.ZenItemsMid[1])
    }

    @Test
    fun testBuildUnbudgetedSpendingVector() {

        val transactions = listOf(
            Transaction(id ="1-1", amount = BigDecimal(-101), currency = "CHF", bookingText = "text", bookingDate = LocalDateTime.of(2010, 1,20, 10, 10, 10), categoryId = 50),
            Transaction(id ="1-2", amount = BigDecimal(-102), currency= "CHF", bookingText= "text", bookingDate = LocalDateTime.of(2010, 1,20, 10, 10, 10), categoryId= 77),
            Transaction(id = "1-3", amount = BigDecimal(-103), currency= "CHF", bookingText= "text", bookingDate =LocalDateTime.of(2010, 1,20, 10, 10, 10), categoryId= null),
            Transaction(id ="2-1", amount = BigDecimal(-103), currency= "CHF", bookingText= "text", bookingDate = LocalDateTime.of(2010, 1,21, 10, 10, 10), categoryId = null),
            Transaction(id="3-1", amount = BigDecimal(-222), currency= "CHF", bookingText= "text", bookingDate = LocalDateTime.of(2010, 1,22, 10, 10, 10), categoryId= 50),
            Transaction(id="3-1", amount = BigDecimal(111), currency= "CHF", bookingText= "text", bookingDate = LocalDateTime.of(2010, 1,22, 10, 10, 10), categoryId= 100)
        )

        val budgets = listOf(
            Budget(id= "1", name= "one", budgeted =BigDecimal(1000), alert= BigDecimal(80), categoryId= 50, frequencyType= BudgetFrequency.monthly),
            Budget(id= "2", name= "one", budgeted =BigDecimal(1000), alert= BigDecimal(80), categoryId= 100, frequencyType= BudgetFrequency.monthly))

        val zenBuilder = Zen(transactions= transactions, budgets= budgets)
        val unspent = zenBuilder.buildUnbudgetedSpendingVector()

        Assert.assertEquals(unspent.count(), 3)
        carrotAssertEqual(unspent[0], 205.0)
        carrotAssertEqual(unspent[1], 103.0)
        carrotAssertEqual(unspent[2], 0.0)

    }

    @Test
    fun testBuildZen() {
        val zenBuilder = Zen(transactions= _zenTestData.Transactions, budgets= _zenTestData.Budgets)

        val outputFrame = zenBuilder.buildOutputFrame()
        val incomeVector = zenBuilder.buildIncomeVector()

        val zen = zenBuilder.buildZen(outputFrame= outputFrame, incomeVector= incomeVector)

        Assert.assertEquals(zen.count(), _zenTestData.ZenItemsFull.count())

        for (i in 0..zen.count() - 1) {
            Assert.assertEquals(zen[i].DaySince1970, _zenTestData.ZenItemsFull[i].DateSince1970)
            carrotAssertEqual(zen[i].Score, _zenTestData.ZenItemsFull[i].ZenScore)
        }
    }

    @Test
    fun testBuildZenFull() {
        val zenBuilder = Zen(transactions= _zenTestData.Transactions, budgets= _zenTestData.Budgets)
        val zen = zenBuilder.build()

        Assert.assertEquals(zen.count(), _zenTestData.ZenItemsFull.count())

        for (i in 0..zen.count() - 1) {
            Assert.assertEquals(zen[i].DaySince1970, _zenTestData.ZenItemsFull[i].DateSince1970)
            carrotAssertEqual(zen[i].Score, _zenTestData.ZenItemsFull[i].ZenScore)
        }
    }

    // There are few reasons for having this method:
    // a) there is a weird exception, when using regular assert: java.lang.AssertionError: expected:<0.0> but was:<0>
    // b) these values are still a bit different
    fun carrotAssertEqual(val1: Double, val2: Double) {
        var zeroValue = val1 - val2
        zeroValue = if (zeroValue >= 0.0) zeroValue else -zeroValue

        Assert.assertTrue("Carrot Assert Equals failed: $val1 is not equal $val2", zeroValue < _eps)
    }

}