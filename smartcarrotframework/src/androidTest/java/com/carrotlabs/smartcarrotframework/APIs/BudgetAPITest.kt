package com.carrotlabs.smartcarrotframework.APIs

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.carrotlabs.smartcarrotframework.*
import com.carrotlabs.smartcarrotframework.GlobalsTests
import com.carrotlabs.smartcarrotframework.models.PersistenceType
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.math.BigDecimal

@RunWith(AndroidJUnit4::class)
internal class BudgetAPITest {
    private var carrotContext = CarrotContext()
    private var transactionAPI: TransactionAPI? = null
    private var budgetAPI: BudgetAPI? = null

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
    }

    private fun getBudgets() : List<Budget> {
        return APITestData().getBudgets()
    }

    private fun prepareTxs() {
        val result = APITestData().getTransactionsForBudgeting()

        transactionAPI!!.upsert(result, false)
    }

    private fun prepareTxsForRunningTotal() {
        val result = APITestData().getTransactionsForBudgetRunningTotal()

        transactionAPI!!.upsert(result, false)
    }

    @Test
    fun testAddUpdateWithCategorisation() {
        val bs = getBudgets()
        budgetAPI!!.upsert(bs)

        val getAll = budgetAPI!!.getAll()

        val index1 = getAll.indexOfFirst { it.id == bs[0].id }
        val index2 = getAll.indexOfFirst { it.id == bs[1].id }
        val index3 = getAll.indexOfFirst { it.id == bs[2].id }

        Assert.assertEquals(3, getAll.count())
        Assert.assertEquals(getAll[index1].categoryId, bs[0].categoryId)
        Assert.assertEquals(getAll[index2].categoryId, bs[1].categoryId)
        Assert.assertEquals(getAll[index3].categoryId, bs[2].categoryId)
    }

    @Test
    fun testUpsert() {
        val bs = getBudgets()
        budgetAPI!!.upsert(listOf(bs[0], bs[1]))

        val getAll = budgetAPI!!.getAll()
        Assert.assertEquals(2, getAll.count())

        bs[2].id = bs[0].id
        budgetAPI!!.upsert(listOf(bs[2]))
        Assert.assertEquals(2, getAll.count())

        val getById = budgetAPI!!.getById(bs[2].id)
        Assert.assertEquals(getById!!.externalId, bs[2].externalId)
    }

    @Test
    fun testGetAllOrderByName() {
        val bs = getBudgets()
        budgetAPI!!.upsert(bs)

        val getAllByName =  budgetAPI!!.getAllOrderByNameId()

        Assert.assertEquals(3, getAllByName.count())
        Assert.assertEquals(getAllByName[2].name, bs[0].name)
        Assert.assertEquals(getAllByName[2].id, bs[0].id)
        Assert.assertEquals(getAllByName[0].name, bs[1].name)
        Assert.assertEquals(getAllByName[0].id, bs[1].id)
    }

    @Test
    fun testBudgetRunningTotals() {
        prepareTxs()

        val bs = getBudgets()

        budgetAPI!!.upsert(bs)

        // montly 2020-02
        var runningTotalB1 = budgetAPI!!.getRunningTotal(bs[0], 2020, 2)
        var txCount = budgetAPI!!.getTransactionsCount(bs[0], 2020, 2)
        Assert.assertEquals(3, txCount)
        Assert.assertEquals(BigDecimal(550), runningTotalB1)

        // annual 2020
        txCount = budgetAPI!!.getTransactionsCount(bs[2], 2020, 2)
        runningTotalB1 = budgetAPI!!.getRunningTotal(bs[2], 2020, 2)
        Assert.assertEquals(4, txCount)
        Assert.assertEquals(BigDecimal(1550), runningTotalB1)

        // annual 2019
        txCount = budgetAPI!!.getTransactionsCount(bs[2], 2019, 1)
        runningTotalB1 = budgetAPI!!.getRunningTotal(bs[2], 2019, 2)
        Assert.assertEquals(1, txCount)
        Assert.assertEquals(BigDecimal(7), runningTotalB1)

        // annual 2018
        txCount = budgetAPI!!.getTransactionsCount(bs[2], 2018, 1)
        runningTotalB1 = budgetAPI!!.getRunningTotal(bs[2], 2018, 2)
        Assert.assertEquals(0, txCount)
        Assert.assertEquals(BigDecimal(0), runningTotalB1)
    }

    @Test
    fun testSubcategoriesNotAccountedRunningTotal() {
        prepareTxsForRunningTotal()

        val bs = APITestData().getBudgetsForRunningTotal()

        Assert.assertEquals(budgetAPI!!.getRunningTotal(bs[0]), BigDecimal(111))
        Assert.assertEquals(budgetAPI!!.getRunningTotal(bs[1]), BigDecimal(200))
        Assert.assertEquals(budgetAPI!!.getRunningTotal(bs[2]), BigDecimal(350))
    }

    @Test(expected = InvalidLicenseCarrotContextError::class)
    fun testLicenseDoesNotHaveModule() {
        val categoriseOnlyContext = CarrotContext(PersistenceType.ephemeral)
        categoriseOnlyContext.setLicense(GlobalsTests.LICENSE_CATEGORISE_ONLY, getContext())
        categoriseOnlyContext.getBudgetAPI()
    }

    @Test
    fun noBudgetsListEmpty() {
        val budgets = budgetAPI!!.getAll()

        Assert.assertEquals(0, budgets.size)
    }
}