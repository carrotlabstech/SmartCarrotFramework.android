package com.carrotlabs.smartcarrotframework.APIs

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.carrotlabs.smartcarrotframework.*
import com.carrotlabs.smartcarrotframework.GlobalsTests
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.math.BigDecimal

@RunWith(AndroidJUnit4::class)
internal class NotificationTest : Notifiable {
    private var carrotContext = CarrotContext()
    private var transactionAPI: TransactionAPI? = null
    private var budgetAPI: BudgetAPI? = null
    private var event: NotificationCenter.NotificationName? = null

    fun getContext() : Context {
        return InstrumentationRegistry.getInstrumentation().targetContext
    }

    @Before
    fun setUp() {
        carrotContext.setEncryption(GlobalsTests.PASSWORD, GlobalsTests.IV)
        carrotContext.setLicense(GlobalsTests.LICENSE_VALID_FULL, getContext())
        transactionAPI = carrotContext.getTransactionAPI()
        budgetAPI = carrotContext.getBudgetAPI()

        transactionAPI!!.delete(transactionAPI!!.getAll())
        budgetAPI!!.delete(budgetAPI!!.getAll())

        event = null
        NotificationCenter.removeObserver(this)
    }

    @Test
    fun testCateforisationFiresTransactionListDidUpdateEvents() {
        NotificationCenter.addObserver(NotificationCenter.NotificationName.transactionListDidUpdate, this)

        val txs = APITestData().getTransactionsForBudgeting()
        transactionAPI!!.upsert(txs, false)

        assertWaitForExpectations(NotificationCenter.NotificationName.transactionListDidUpdate)

        val tx = Transaction()
        tx.id = "yohohoho-id"
        tx.amount = BigDecimal(-900)
        tx.currency = "CHF"
        tx.bookingText = "booking sample text"
        transactionAPI!!.upsert(listOf(tx))
        assertWaitForExpectations(NotificationCenter.NotificationName.transactionListDidUpdate)
    }

    @Test
    fun testBudgetsFireBudgetListDidChangeEvents() {
        NotificationCenter.addObserver(NotificationCenter.NotificationName.budgetListDidUpdate, this)

        val budgets = APITestData().getBudgets()
        budgetAPI!!.upsert(listOf(budgets[0], budgets[1]))
        assertWaitForExpectations(NotificationCenter.NotificationName.budgetListDidUpdate)

        budgetAPI!!.delete(budgetAPI!!.getAll())
        assertWaitForExpectations(NotificationCenter.NotificationName.budgetListDidUpdate)
    }

    @Test
    fun testBudgetAlertEvents() {
        NotificationCenter.addObserver(NotificationCenter.NotificationName.budgetDidAlert, this)

        val budgets = APITestData().getBudgets()
        budgetAPI!!.upsert(listOf(budgets[0]))

        val txs = APITestData().getTransactionsForBudgetingEvents()
        transactionAPI!!.upsert(txs, false)

        assertWaitForExpectations(NotificationCenter.NotificationName.budgetDidAlert)
    }

    @Test
    fun testBudgetRecalEvents() {
        NotificationCenter.addObserver(NotificationCenter.NotificationName.budgetDidOverSpend, this)

        val budgets = APITestData().getBudgets()
        budgetAPI!!.upsert(listOf(budgets[1]))

        val txs = APITestData().getTransactionsForBudgetingEvents()
        transactionAPI!!.upsert(txs, false)

        assertWaitForExpectations(NotificationCenter.NotificationName.budgetDidOverSpend)
    }

    @Test
    fun testBudgetAlertEventsIfBudgetsUpdated() {
        NotificationCenter.addObserver(NotificationCenter.NotificationName.budgetDidAlert, this)

        val budgets = APITestData().getBudgets()
        budgetAPI!!.upsert(listOf(budgets[0]))

        val txs = APITestData().getTransactionsForBudgetingEvents()
        transactionAPI!!.upsert(txs, false)

        budgetAPI!!.upsert(listOf(budgets[0]))

        assertWaitForExpectations(NotificationCenter.NotificationName.budgetDidAlert)
    }

    fun assertWaitForExpectations(name: NotificationCenter.NotificationName) {
        var counter = 100
        while (counter > 0) {
            if (event == name) {
                event = null
                Assert.assertTrue(true)
                return
            }

            Thread.sleep(10)
            counter = counter - 1
        }

        event = null
        Assert.assertTrue(false)
    }

    override fun onNotification(notify: Notification?) {
        event = notify!!.name
    }
}