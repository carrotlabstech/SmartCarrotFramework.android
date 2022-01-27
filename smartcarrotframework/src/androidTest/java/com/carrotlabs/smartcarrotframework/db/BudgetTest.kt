package com.carrotlabs.smartcarrotframework.db

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.carrotlabs.smartcarrotframework.*
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.math.BigDecimal
import java.util.*

@RunWith(AndroidJUnit4::class)
internal class BudgetTest {
    @Before
    fun initLocalZone() {
        var carrotContext = CarrotContext()
        carrotContext.setLicense(GlobalsTests.LICENSE_VALID_FULL, getContext(), GlobalsTests.PASSWORD, GlobalsTests.IV)
    }

    fun getContext() : Context {
        return InstrumentationRegistry.getInstrumentation().targetContext
    }

    @Test
    fun testNegativeNumbers() {
        val budget = Budget(id = "someid", name = "some name", budgeted = BigDecimal( -100), alert = BigDecimal(-200.0), categoryId =-18, frequencyType = BudgetFrequency.annual)

        Assert.assertEquals(budget.budgeted, BigDecimal(0))
        Assert.assertEquals(budget.alert, BigDecimal(0))
        Assert.assertEquals(budget.categoryId, TransactionCategory.UNCATEGORISED_INT_ID)
    }

    @Test
    fun testCopy() {
        val b1 = Budget(id = UUID.randomUUID().toString(),
        externalId = "weirdExtId",
        accountId = "account one",
        currency = "CHF",
        name = "thisisthename",
        budgeted = BigDecimal(1000),
        alert = BigDecimal(700),
        frequencyType = BudgetFrequency.annual)

        val b2 = b1.copy()
        Assert.assertEquals(b1.id, b2.id)
        Assert.assertEquals(b1.externalId, b2.externalId)
        Assert.assertEquals(b1.accountId, b2.accountId)
        Assert.assertEquals(b1.currency, b2.currency)
        Assert.assertEquals(b1.name, b2.name)
        Assert.assertEquals(b1.alert, b2.alert)
        Assert.assertEquals(b1.budgeted, b2.budgeted)
        Assert.assertEquals(b1.frequencyType, b2.frequencyType)
    }

    @Test(expected = EmptyEntityIdCarrotContextError::class)
    fun testEmptyEntityId() {
        val b = Budget()
        b.id = "something"

        val b1 = Budget()

        val repo = BudgetDbHelper(getContext())
        repo.addUpdate(listOf(b, b1))
    }

}