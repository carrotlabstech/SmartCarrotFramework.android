package com.carrotlabs.smartcarrotframework.APIs

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.carrotlabs.smartcarrotframework.*
import com.carrotlabs.smartcarrotframework.models.PersistenceType
import com.carrotlabs.smartcarrotframework.scoring.ZenParams
import com.carrotlabs.smartcarrotframework.scoring.ZenUtils
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import java.math.BigDecimal
import java.util.*

@RunWith(AndroidJUnit4::class)
internal class ZenAPITest {
    private var budgetAPI: BudgetAPI? = null
    private var transactionAPI: TransactionAPI? = null
    private var zenAPI: ZenAPI? = null
    private var carrotContext: CarrotContext = CarrotContext(PersistenceType.ephemeral)

    fun getContext() : Context {
        return InstrumentationRegistry.getInstrumentation().targetContext
    }

    @Before
    fun setUp() {
        carrotContext.setEncryption(GlobalsTests.PASSWORD, GlobalsTests.IV)
        carrotContext.setLicense(GlobalsTests.LICENSE_VALID_FULL, getContext())

        transactionAPI = carrotContext.getTransactionAPI()
        budgetAPI = carrotContext.getBudgetAPI()
        zenAPI = carrotContext.getZenAPI()

        budgetAPI!!.delete(budgets= budgetAPI!!.getAll())
        transactionAPI!!.delete(transactions= transactionAPI!!.getAll())
    }

    @Test(expected = ZenScoreNoBudgetsCarrotContextError::class)
    fun testNoBudget() {
        val tx1 = Transaction()
        tx1.id = UUID.randomUUID().toString()
        tx1.amount = BigDecimal(-111)
        tx1.currency = "CHF"
        tx1.bookingText = "this is a great booking text"
        tx1.bookingDate = LocalDateTime.of(1970, 1,1,1,1,1)
        tx1.categoryId = TransactionCategory("leisure_shopping").getIntId()

        transactionAPI!!.upsert(transactions = listOf(tx1))

        zenAPI!!.buildZen()
    }

    @Test
    fun testMaxInterval() {
        var txs = ArrayList<Transaction>()

        var tx1 = Transaction()
        tx1.id = UUID.randomUUID().toString()
        tx1.amount = BigDecimal(-111)
        tx1.currency ="CHF"
        tx1.bookingText = "this is a great booking text"
        tx1.bookingDate = LocalDateTime.of(1970, 1, 1, 1, 1, 1 )
        tx1.categoryId = TransactionCategory("leisure_shopping").getIntId()
        txs.add(tx1)

        tx1 = Transaction()
        tx1.id = UUID.randomUUID().toString()
        tx1.amount = BigDecimal(-200)
        tx1.currency = "CHF"
        tx1.bookingText = "this is a great booking text"
        tx1.bookingDate = LocalDateTime.of(2000, 1, 1, 1, 1, 1)
        tx1.categoryId = TransactionCategory("electronics").getIntId()
        txs.add(tx1)

        tx1 = Transaction()
        tx1.id = UUID.randomUUID().toString()
        tx1.amount = BigDecimal(-350)
        tx1.currency = "CHF"
        tx1.bookingText = "this is a great booking text"
        tx1.bookingDate = LocalDateTime.now()
        tx1.categoryId = TransactionCategory.UNCATEGORISED_INT_ID
        txs.add(tx1)

        transactionAPI!!.upsert(transactions= txs)

        val bs = APITestData().getBudgets()
        budgetAPI!!.upsert(budgets = listOf(bs[0], bs[1]))

        val zen = zenAPI!!.buildZen()
        Assert.assertTrue(zen.size <= ZenParams.MAX_INTERVAL + 1)

        Assert.assertEquals(zen[zen.size - 1].DaySince1970, ZenUtils.daySince1970(date = LocalDate.now()))
    }

    @Test(expected = InvalidLicenseCarrotContextError::class)
    fun testLicenseDoesNotHaveModule() {
        val context = CarrotContext(PersistenceType.ephemeral)
        context.setLicense(GlobalsTests.LICENSE_CATEGORISE_ONLY, getContext())

        context.getZenAPI()
    }



}