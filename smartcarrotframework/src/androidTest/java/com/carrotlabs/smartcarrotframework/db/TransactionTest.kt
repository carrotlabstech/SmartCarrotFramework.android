package com.carrotlabs.smartcarrotframework.db

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.carrotlabs.smartcarrotframework.*
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.threeten.bp.LocalDateTime
import java.math.BigDecimal
import java.util.*

@RunWith(AndroidJUnit4::class)
internal class TransactionTest {
    @Before
    fun initLocalZone() {
        var carrotContext = CarrotContext()
        carrotContext.setLicense(GlobalsTests.LICENSE_VALID_FULL, getContext(), GlobalsTests.PASSWORD, GlobalsTests.IV)
    }

    fun getContext() : Context {
        return InstrumentationRegistry.getInstrumentation().targetContext
    }

    @Test
    fun testImmutableFieldsCopy() {
        val tx = Transaction()
        tx.bookingDate = LocalDateTime.of(2000, 1,1,1,1,1)
        tx.amount = BigDecimal(-5000)

        val tx2 = tx.copy()
        tx2.bookingDate = tx2.bookingDate.minusYears(10)
        tx2.amount = tx.amount.minus(BigDecimal(5000))

        Assert.assertNotEquals(tx.bookingDate, LocalDateTime.of(1990, 1,1,1,1,1))
        Assert.assertEquals(tx.bookingDate, LocalDateTime.of(2000, 1,1,1,1,1))

        Assert.assertNotEquals(tx.amount, BigDecimal(-10000))
        Assert.assertEquals(tx.amount, BigDecimal(-5000))
    }

    @Test
    fun testCopy() {
        val tx1 = Transaction()
        tx1.id = UUID.randomUUID().toString()
        tx1.externalId = "ldsafha"

        tx1.amount = BigDecimal(-345)
        tx1.currency = "USD"

        tx1.sourceAmount = BigDecimal(-300)
        tx1.sourceCurrency = "EUR"
        tx1.sourceExchangeRate = BigDecimal(1.12)

        tx1.bookingText = "simpel boook teex"

        tx1.bookingDate = LocalDateTime.of(2019, 5,5, 5, 5, 5)
        tx1.valueDate = tx1.bookingDate.minusDays(10)

        tx1.categoryId = 55
        tx1.userCategoryId = 33

        tx1.accountId = "100500"

        tx1.userNote = "userNote"

        tx1.customProperty1 = null
        tx1.customProperty2 = "status: approved"

        tx1.created = LocalDateTime.now()
        tx1.modified = LocalDateTime.now()

        val tx2 = tx1.copy()

        Assert.assertEquals(tx1.id, tx2.id)
        Assert.assertEquals(tx1.externalId, tx2.externalId)
        Assert.assertEquals(tx1.amount, tx2.amount)
        Assert.assertEquals(tx1.currency, tx2.currency)
        Assert.assertEquals(tx1.sourceAmount, tx2.sourceAmount)
        Assert.assertEquals(tx1.sourceExchangeRate, tx2.sourceExchangeRate)
        Assert.assertEquals(tx1.sourceCurrency, tx2.sourceCurrency)
        Assert.assertEquals(tx1.bookingText, tx2.bookingText)
        Assert.assertEquals(tx1.bookingDate, tx2.bookingDate)
        Assert.assertEquals(tx1.valueDate, tx2.valueDate)
        Assert.assertEquals(tx1.categoryId, tx2.categoryId)
        Assert.assertEquals(tx1.userCategoryId, tx2.userCategoryId)
        Assert.assertEquals(tx1.accountId, tx2.accountId)
        Assert.assertEquals(tx1.userNote, tx2.userNote)
        Assert.assertEquals(tx1.customProperty1, tx2.customProperty1)
        Assert.assertEquals(tx1.customProperty2, tx2.customProperty2)
        Assert.assertEquals(tx1.created, tx2.created)
        Assert.assertEquals(tx1.modified, tx2.modified)
    }

    @Test(expected = EmptyEntityIdCarrotContextError::class)
    fun testEmptyEntityId() {
        val tx = Transaction()
        tx.id = "something"

        val tx1 = Transaction()

        val repo = TransactionDbHelper(getContext())
        repo.addUpdate(listOf(tx, tx1))
    }
}