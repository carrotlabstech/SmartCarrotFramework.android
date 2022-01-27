package com.carrotlabs.smartcarrotframework.scoring

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.jakewharton.threetenabp.AndroidThreeTen
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.threeten.bp.LocalDate

@RunWith(AndroidJUnit4::class)
internal class ZenUtilsTest {
    @Before
    fun setup() {
        AndroidThreeTen.init(getContext())
    }

    fun getContext() : Context {
        return InstrumentationRegistry.getInstrumentation().targetContext
    }

    @Test
    fun testNumberOfDays() {
        Assert.assertEquals(31, ZenUtils.daysPerMonth(1))
        Assert.assertEquals(31, ZenUtils.daysPerMonth(13))
        Assert.assertEquals(28, ZenUtils.daysPerMonth(14))
        Assert.assertEquals(29, ZenUtils.daysPerMonth(26))

        Assert.assertEquals(30, ZenUtils.daysPerMonth(585))
        Assert.assertEquals(31, ZenUtils.daysPerMonth(586))
        Assert.assertEquals(30, ZenUtils.daysPerMonth(587))
        Assert.assertEquals(31, ZenUtils.daysPerMonth(588))
        Assert.assertEquals(31, ZenUtils.daysPerMonth(589))
        Assert.assertEquals(28, ZenUtils.daysPerMonth(590))
    }

    @Test
    fun testGetDayNoSince1970() {
        Assert.assertEquals(2, ZenUtils.daySince1970(LocalDate.of(1970,1,2)))

        val theDate = LocalDate.of(1971, 2, 1)
        val daysNumber = 365 + 31 + 1

        Assert.assertEquals(daysNumber, ZenUtils.daySince1970(theDate))
        Assert.assertEquals(14, ZenUtils.monthSince1970(theDate.toEpochDay().toInt() + 1))
    }

    @Test
    fun testGetMonthSince1970() {
        Assert.assertEquals(1, ZenUtils.monthSince1970(5))
        Assert.assertEquals(4, ZenUtils.monthSince1970(105))
        Assert.assertEquals(13, ZenUtils.monthSince1970(370))
        Assert.assertEquals(585, ZenUtils.monthSince1970(17805))
    }

    @Test
    fun testYearMonthSince1970() {
        var pair = ZenUtils.getYearMonthFromMonth1970(13)
        Assert.assertEquals(1971, pair.year)
        Assert.assertEquals(1, pair.month.value)

        pair = ZenUtils.getYearMonthFromMonth1970(11)
        Assert.assertEquals(1970, pair.year)
        Assert.assertEquals(11, pair.month.value)

        pair = ZenUtils.getYearMonthFromMonth1970(12)
        Assert.assertEquals(1970, pair.year)
        Assert.assertEquals(12, pair.month.value)

        pair = ZenUtils.getYearMonthFromMonth1970(24)
        Assert.assertEquals(1971, pair.year)
        Assert.assertEquals(12, pair.month.value)

        val monthSince1970 = ZenUtils.monthSince1970(17885)
        Assert.assertEquals(588, monthSince1970)

        pair = ZenUtils.getYearMonthFromMonth1970(monthSince1970)
        Assert.assertEquals(2018, pair.year)
        Assert.assertEquals(12, pair.month.value)
    }

    @Test
    fun testDayOfMonth() {
        Assert.assertEquals(1, ZenUtils.dayOfMonth(1))
        Assert.assertEquals(5, ZenUtils.dayOfMonth(36))
        Assert.assertEquals(25, ZenUtils.dayOfMonth(17800))
    }

    @Test
    fun testGetDate() {
        var date = ZenUtils.date(1)
        Assert.assertTrue(date.year == 1970 && date.month.value == 1 && date.dayOfMonth == 1)

        date = ZenUtils.date(365 + 31 + 5)
        Assert.assertTrue(date.year == 1971 && date.month.value == 2 && date.dayOfMonth == 5)

        date = ZenUtils.date(17803)
        Assert.assertTrue(date.year == 2018 && date.month.value == 9 && date.dayOfMonth == 28)
    }
}