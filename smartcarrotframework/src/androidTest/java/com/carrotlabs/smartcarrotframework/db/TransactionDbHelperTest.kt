package com.carrotlabs.smartcarrotframework.db

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.carrotlabs.smartcarrotframework.EmptyEntityIdCarrotContextError
import com.carrotlabs.smartcarrotframework.GlobalsTests
import com.carrotlabs.smartcarrotframework.utils.SharedContext
import com.carrotlabs.smartcarrotframework.Transaction
import com.jakewharton.threetenabp.AndroidThreeTen
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset
import java.lang.Exception
import java.lang.RuntimeException
import java.math.BigDecimal
import java.util.*
import kotlin.collections.ArrayList

@RunWith(AndroidJUnit4::class)
internal class TransactionDbHelperTest {
    @Before
    fun cleanDb() {
        SharedContext.key = GlobalsTests.PASSWORD
        SharedContext.iv = GlobalsTests.IV

        AndroidThreeTen.init(getContext())

        val repo = TransactionDbHelper(getContext())
        repo.wipe()
    }

    @Test
    fun testSaveAndLoadTransaction() {
        val repo = TransactionDbHelper(getContext())
        val newId = UUID.randomUUID().toString()

        val tx = Transaction()
        tx.id = newId
        tx.amount = BigDecimal(-100)
        tx.currency = "CHF"
        tx.bookingText = "some text here"
        tx.bookingDate = LocalDateTime.of(2020, 1,1, 23, 10, 10, 10)
        tx.valueDate = LocalDateTime.of(2020, 1,1, 23, 10, 10, 10)
        tx.created = LocalDateTime.of(2020, 1,1, 23, 10, 10, 10)
        tx.modified = LocalDateTime.of(2020, 2,1, 23, 10, 10, 10)

        val txs = ArrayList<Transaction>()
        txs.add(tx)

        repo.addUpdate(txs)

        // load via getAll
        val loadedTxs = repo.getAll()
        Assert.assertEquals(txs.count(), loadedTxs.count())
        Assert.assertEquals(txs[0].id, loadedTxs[0].id)
        Assert.assertEquals(txs[0].currency, loadedTxs[0].currency)
        Assert.assertEquals(txs[0].bookingText, loadedTxs[0].bookingText)
        Assert.assertEquals(txs[0].bookingDate.toEpochSecond(ZoneOffset.ofTotalSeconds(0)),
            loadedTxs[0].bookingDate.toEpochSecond(ZoneOffset.ofTotalSeconds(0)))

        // load via getById
        val loadedTx = repo.getById(newId)
        Assert.assertNotNull(loadedTx)
        Assert.assertEquals(txs[0].id, loadedTx!!.id)
        Assert.assertEquals(txs[0].currency, loadedTx.currency)
    }

    @Test
    fun testMainFeatures() {
        val repo = TransactionDbHelper(getContext())
        val now = LocalDateTime.now()

        val tx0 = Transaction()
        tx0.id = UUID.randomUUID().toString()
        tx0.externalId = "201"
        tx0.amount = BigDecimal(-100.34)
        tx0.currency = "CHF"
        tx0.sourceAmount = BigDecimal(-90.12)
        tx0.sourceCurrency = "EUR"
        tx0.sourceExchangeRate = BigDecimal(1.12)
        tx0.bookingText =  "this is a great booking text"
        tx0.bookingDate = now
        tx0.valueDate = now
        tx0.categoryId = 45
        tx0.userCategoryId = 0
        tx0.accountId = "301"
        tx0.userNote =  null
        tx0.customProperty1 = """{ value: 100, name: \ohoho\ }"""
        tx0.customProperty2 = ""
        tx0.created = now
        tx0.modified = now

        val tx2 = Transaction()
        tx2.id = UUID.randomUUID().toString()
        tx2.externalId = "202"
        tx2.amount = BigDecimal(-1000.00)
        tx2.currency = "CHF"
        tx2.sourceAmount = BigDecimal(0)
        tx2.sourceCurrency = ""
        tx2.sourceExchangeRate = BigDecimal(0)
        tx2.bookingText = "this is a great booking text tx 2"
        tx2.bookingDate = now
        tx2.valueDate = now
        tx2.categoryId = 101
        tx2.userCategoryId =  88
        tx2.accountId = "1301"
        tx2.userNote = "a beautiful transaction tx2"
        tx2.customProperty1 = ""
        tx2.customProperty2 = """{ value: 10000, name: \"ohoho\" }"""
        tx2.created = now
        tx2.modified = now

        val tx3 = Transaction()
        tx3.id = tx0.id
        tx3.externalId = "2202"
        tx3.amount = BigDecimal(-10000.00)
        tx3.currency = "CHF"
        tx3.sourceAmount = null
        tx3.sourceCurrency = null
        tx3.sourceExchangeRate = null
        tx3.bookingText = "this is a great booking text tx 2"
        tx3.bookingDate = now
        tx3.valueDate = now
        tx3.categoryId = 101
        tx3.userCategoryId =  88
        tx3.accountId = "1301"
        tx3.userNote = "a beautiful transaction tx2"
        tx3.customProperty1 = ""
        tx3.customProperty2 = """{ value: 10000, name: \"ohoho\" }"""
        tx3.created = now
        tx3.modified = now

        val array1 = ArrayList<Transaction>()
        array1.add(tx0)
        repo.addUpdate(array1)

        val array2 = ArrayList<Transaction>()
        array2.add(tx2)
        array2.add(tx3)
        repo.addUpdate(array2)

        val all = repo.getAll()

        // todo: this is a shame to use this sort order, should be lookup actually
        Assert.assertEquals(2, all.count())
        Assert.assertEquals(tx2.id, all[0].id)
        Assert.assertEquals(tx3.id, all[1].id)

        // optionals
        Assert.assertNull(all[1].sourceExchangeRate)
        Assert.assertNull(all[1].sourceCurrency)
        Assert.assertNull(all[1].sourceAmount)

        // upsert
        Assert.assertEquals(tx3.externalId, all[1].externalId)
    }

    @Test
    fun testThrowEmptyId() {
        val repo = TransactionDbHelper(getContext())
        var tx = Transaction()
        var tx1 = Transaction()

        tx.id = "something"

        // TODO: potentially should be updated, this is an ungly way to check if an exception has been thrown
        try {
            repo.addUpdate(listOf(tx, tx1))
            throw Exception("EmptyEntityIdCarrotContextError exception has not been thrown")
        } catch (e: EmptyEntityIdCarrotContextError) {
            Assert.assertTrue(true)
        } catch (e: Exception) {
            throw Exception("EmptyEntityIdCarrotContextError exception has not been thrown")
        }
    }

    fun getContext() : Context {
        return InstrumentationRegistry.getInstrumentation().targetContext
    }
}