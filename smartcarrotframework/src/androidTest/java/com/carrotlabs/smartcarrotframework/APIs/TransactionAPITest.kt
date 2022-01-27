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

@RunWith(AndroidJUnit4::class)
internal class TransactionAPITest {
    private var carrotContext = CarrotContext()
    private var transactionAPI: TransactionAPI? = null

    fun getContext() : Context {
        return InstrumentationRegistry.getInstrumentation().targetContext
    }

    @Before
    fun setUp() {
        carrotContext.setEncryption(GlobalsTests.PASSWORD, GlobalsTests.IV)
        carrotContext.setLicense(GlobalsTests.LICENSE_VALID_FULL, getContext())
        transactionAPI = carrotContext.getTransactionAPI()

        transactionAPI!!.delete(transactionAPI!!.getAll())
    }

    private fun getTransactions() : List<Transaction> {
        return APITestData().getTransactions()
    }

    @Test
    fun testAddUpdateWithCategorisation() {
        val txs = getTransactions()
        transactionAPI!!.upsert(txs)

        val getAll = transactionAPI!!.getAll()
        Assert.assertEquals(3, getAll.count())

        val index0 = getAll.indexOfFirst { it.id == txs[0].id }
        val index1 = getAll.indexOfFirst { it.id == txs[1].id }
        val index2 = getAll.indexOfFirst { it.id == txs[2].id }

        Assert.assertEquals(getAll[index0].externalId, txs[0].externalId)
        Assert.assertEquals(getAll[index1].externalId, txs[1].externalId)
        Assert.assertEquals(getAll[index2].externalId, txs[2].externalId)
        Assert.assertNotEquals(getAll[index0].categoryId, txs[0].categoryId)
        Assert.assertNotEquals(getAll[index1].categoryId, txs[1].categoryId)
        Assert.assertNotEquals(getAll[index2].categoryId, txs[2].categoryId)
    }

    @Test
    fun testUpsert() {
        var txs = getTransactions()
        transactionAPI!!.upsert(listOf(txs[0], txs[1]))

        val getAll = transactionAPI!!.getAll()
        Assert.assertEquals(2, getAll.count())

        txs[2].id = txs[0].id
        transactionAPI!!.upsert(listOf(txs[2]))
        Assert.assertEquals(2, getAll.count())

        val getById = transactionAPI!!.getById(txs[2].id)
        Assert.assertNotNull(getById)
        Assert.assertEquals(getById!!.externalId, txs[2].externalId)
    }

    @Test
    fun testGetAllOrdered() {
        val txs = getTransactions()

        transactionAPI!!.upsert(txs)
        val getAllSorted = transactionAPI!!.getAllOrderByDateDescTextId()

        Assert.assertEquals(getAllSorted.count(), 3)
        Assert.assertEquals(getAllSorted[0].id, txs[1].id)
        Assert.assertEquals(getAllSorted[1].id, txs[2].id)
        Assert.assertEquals(getAllSorted[2].id, txs[0].id)
    }

    @Test(expected = InvalidLicenseCarrotContextError::class)
    fun testLicenseDoesNotHaveModule() {
        val categoriseOnlyContext = CarrotContext(PersistenceType.ephemeral)
        categoriseOnlyContext.setLicense(GlobalsTests.LICENSE_CATEGORISE_ONLY, getContext())

        // should throw
        categoriseOnlyContext.getTransactionAPI()
    }

    @Test
    fun testNoTransactions() {
        val txn = transactionAPI!!.getAll()

        Assert.assertEquals(txn.size, 0)
    }

}
