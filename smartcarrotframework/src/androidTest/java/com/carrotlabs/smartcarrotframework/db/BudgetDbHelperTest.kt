package com.carrotlabs.smartcarrotframework.db

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.carrotlabs.smartcarrotframework.Budget
import com.carrotlabs.smartcarrotframework.BudgetFrequency
import com.carrotlabs.smartcarrotframework.GlobalsTests
import com.carrotlabs.smartcarrotframework.utils.SharedContext
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.math.BigDecimal
import java.util.*
import kotlin.collections.ArrayList

@RunWith(AndroidJUnit4::class)
internal class BudgetDbHelperTest {
    @Before
    fun cleanDb() {
        SharedContext.key = GlobalsTests.PASSWORD
        SharedContext.iv = GlobalsTests.IV

        val repo = BudgetDbHelper(getContext())
        repo.wipe()
    }

    @Test
    fun testSaveAndLoadBudget() {
        val repo = BudgetDbHelper(getContext())

        val newId = UUID.randomUUID().toString()

        val budget = Budget()
        budget.id = newId
        budget.externalId = "5600"
        budget.budgeted = BigDecimal(10000)
        budget.alert = BigDecimal(7000)
        budget.categoryId = 80
        budget.name = "a wonderfule budget name"
        budget.frequencyType = BudgetFrequency.annual

        val budgets = ArrayList<Budget>()
        budgets.add(budget)

        repo.addUpdate(budgets)

        // check via getAll()
        val loadedTxs = repo.getAll()
        Assert.assertEquals(budgets.count(), loadedTxs.count())
        Assert.assertEquals(budgets[0].id, loadedTxs[0].id)
        Assert.assertEquals(budgets[0].name, loadedTxs[0].name)

        // check via getById()
        val loadedTx = repo.getById(newId)
        Assert.assertNotNull(loadedTx);
        Assert.assertEquals(budgets[0].id, loadedTx!!.id)
        Assert.assertEquals(budgets[0].name, loadedTx.name)
    }

    @Test
    fun testMainFeatures() {
        val repo = BudgetDbHelper(getContext())

        val budget0 = Budget()
        budget0.id = UUID.randomUUID().toString()
        budget0.externalId = "201"
        budget0.budgeted = BigDecimal(5000)
        budget0.alert = BigDecimal(4000)
        budget0.categoryId = 45
        budget0.name = "wonderful. budget"
        budget0.frequencyType = BudgetFrequency.monthly


        val budget2 = Budget()
        budget2.id = UUID.randomUUID().toString()
        budget2.externalId = "2201"
        budget2.budgeted = BigDecimal(8000)
        budget2.alert = BigDecimal(7000)
        budget2.categoryId = 5
        budget2.name = "wonderful. budget 2"
        budget2.frequencyType = BudgetFrequency.annual

        val budget3 = Budget()
        budget3.id = budget0.id
        budget3.externalId = "201222"
        budget3.budgeted = BigDecimal(1000)
        budget3.alert = BigDecimal(800)
        budget3.categoryId = 18
        budget3.name = "hehehehe"
        budget3.frequencyType = BudgetFrequency.monthly

        val array1 = ArrayList<Budget>()
        array1.add(budget0)
        repo.addUpdate(array1)

        val array2 = ArrayList<Budget>()
        array2.add(budget2)
        array2.add(budget3)
        repo.addUpdate(array2)

        val all = repo.getAll()

        // todo: this is a shame to use this sort order, should be lookup actually
        Assert.assertEquals(2, all.count())
        Assert.assertEquals(budget2.id, all[0].id)
        Assert.assertEquals(budget3.id, all[1].id)

        // upsert
        Assert.assertEquals(budget3.externalId, all[1].externalId)

        // enum
        Assert.assertEquals(BudgetFrequency.monthly, all[1].frequencyType)
    }

    fun getContext() : Context {
        return InstrumentationRegistry.getInstrumentation().targetContext
    }
}