package com.carrotlabs.smartcarrotframework.scoring

import com.carrotlabs.smartcarrotframework.*
import org.threeten.bp.Duration
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.YearMonth
import kotlin.math.exp
import java.math.BigDecimal
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.pow

internal object ZenUtils {
    internal fun r(x: Double, alpha: Double, beta: Double, gamma: Double) : Double {
        return 1.0 / (abs(1.0 - alpha * x)).pow(gamma) - exp(beta * (x-1.0)) - 1.0 + exp(-beta)
    }

    internal fun sigmoid(x: Double) : Double {
        return 1.0 / (1.0 + exp( -x))
    }

    internal fun budgetsIncludeCategory(budgets: List<Budget>, categoryId: Int?) : Boolean {
        if (categoryId == null)
            return false

        return budgets.indexOfFirst { it.categoryId == categoryId } >= 0
    }

    internal fun daySince1970(date: LocalDate) : Int {
        val one = LocalDate.of(1970, 1, 1).atStartOfDay()
        val two = date.plusDays(1) .atStartOfDay()
        return Duration.between(one, two).toDays().toInt()
    }

    internal fun daysPerMonth(monthSince1970: Int) : Int {
        return getYearMonthFromMonth1970(monthSince1970).lengthOfMonth()
    }

    internal fun getYearMonthFromMonth1970(monthSince1970: Int) : YearMonth {
        var year = monthSince1970 / 12
        var month = monthSince1970 % 12

        if (month == 0) {
            year = year - 1
            month = 12
        }

        return YearMonth.of(year + 1970, month)
    }

    internal fun monthSince1970(daySince1970: Int) : Int {
        val today = LocalDate.ofEpochDay(daySince1970.toLong() - 1)

        return (today.year - 1970) * 12 + today.monthValue
    }

    internal fun dayOfMonth(daySince1970: Int) : Int {
        return LocalDate.ofEpochDay(daySince1970.toLong() - 1).dayOfMonth
    }

    internal fun date(daySince1970: Int) : LocalDate {
        return LocalDate.ofEpochDay(daySince1970.toLong() - 1)
    }
}