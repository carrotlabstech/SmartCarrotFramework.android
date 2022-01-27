package com.carrotlabs.smartcarrotframework.APIs

import com.carrotlabs.smartcarrotframework.*
import java.math.BigDecimal
import org.threeten.bp.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList

internal final class APITestData {
    companion object {
        val category1 = 100
        val category2 = 18
    }

    internal fun getTransactions() : List<Transaction> {
        val txDate = LocalDateTime.now().minusSeconds(-3600*24*10)
        val now = LocalDateTime.now()

        val tx1 = Transaction()
        tx1.id = UUID.randomUUID().toString()
        tx1.externalId = "111"
        tx1.amount = BigDecimal(-100.34)
        tx1.currency = "CHF"
        tx1.sourceAmount = BigDecimal(-90.12)
        tx1.sourceCurrency = "EUR"
        tx1.sourceExchangeRate = BigDecimal(1.12)
        tx1.bookingText = "this is a great booking text3"
        tx1.bookingDate = txDate
        tx1.valueDate = txDate
        tx1.categoryId = 45
        tx1.userCategoryId = null
        tx1.accountId = "301"
        tx1.userNote = null
        tx1.customProperty1 = "{ value: 100, name: \"ohoho\" }"
        tx1.customProperty2 = ""
        tx1.created = now
        tx1.modified = now

        val tx2 = Transaction()
        tx2.id = UUID.randomUUID().toString()
        tx2.externalId = "222"
        tx2.amount = BigDecimal(-300.34)
        tx2.currency = "CHF"
        tx2.sourceAmount = null
        tx2.sourceCurrency = null
        tx2.sourceExchangeRate = null
        tx2.bookingText = "this is a great booking text1"
        tx2.bookingDate = txDate
        tx2.valueDate = txDate
        tx2.categoryId = 99
        tx2.userCategoryId = 0
        tx2.accountId = "301"
        tx2.userNote = null
        tx2.customProperty1 = "{ value: 100, name: \"ohoho\" }"
        tx2.customProperty2 = ""
        tx2.created = now
        tx2.modified = now

        val tx3 = Transaction()
        tx3.id = UUID.randomUUID().toString()
        tx3.externalId = "333"
        tx3.amount = BigDecimal(20.34)
        tx3.currency = "CHF"
        tx3.sourceAmount = BigDecimal(2.12)
        tx3.sourceCurrency = "GBP"
        tx3.sourceExchangeRate = BigDecimal(10.12)
        tx3.bookingText = "this is a great booking text2"
        tx3.bookingDate = txDate
        tx3.valueDate = txDate
        tx3.categoryId = 22
        tx3.userCategoryId = 0
        tx3.accountId = "303"
        tx3.userNote = null
        tx3.customProperty1 = "{ value: 100, name: \"ohoho\" }"
        tx3.customProperty2 = ""
        tx3.created = now
        tx3.modified = now

        return listOf(tx1, tx2, tx3)
    }

    internal fun getBudgets() : List<Budget> {
        val b1 = Budget()
        b1.id = UUID.randomUUID().toString()
        b1.externalId = "201"
        b1.name = "sweets"
        b1.budgeted = BigDecimal(1000)
        b1.alert = BigDecimal(430)
        b1.categoryId =
            category1
        b1.frequencyType = BudgetFrequency.monthly
        val b2 = Budget()
        b2.id = UUID.randomUUID().toString()
        b2.externalId = "202"
        b2.name = "houses"
        b2.budgeted = BigDecimal(1000)
        b2.alert = BigDecimal(800)
        b2.categoryId =
            category2
        b2.frequencyType = BudgetFrequency.annual
        val b3 = Budget()
        b3.id = UUID.randomUUID().toString()
        b3.externalId = "203"
        b3.name = "restaurants"
        b3.budgeted = BigDecimal(300)
        b3.alert = BigDecimal(200)
        b3.categoryId =
            category1
        b3.frequencyType = BudgetFrequency.annual

        return listOf(b1, b2, b3)
    }

    internal fun getTransactionsForBudgeting() : List<Transaction> {
        val result = ArrayList<Transaction>()

        var tx1 = Transaction()
        tx1.id = UUID.randomUUID().toString()
        tx1.amount = BigDecimal(-100)
        tx1.currency = "CHF"
        tx1.bookingText = "this is a great booking text"
        tx1.bookingDate = LocalDateTime.of(2020, 2, 5, 10, 10, 10)
        tx1.categoryId =
            category1
        result.add(tx1)

        tx1 = Transaction()
        tx1.id = UUID.randomUUID().toString()
        tx1.amount = BigDecimal(-300)
        tx1.currency = "CHF"
        tx1.bookingText = "this is a great booking text"
        tx1.bookingDate = LocalDateTime.of(2020, 2, 7, 10, 10, 10)
        tx1.categoryId =
            category1
        result.add(tx1)

        tx1 = Transaction()
        tx1.id = UUID.randomUUID().toString()
        tx1.amount = BigDecimal(-150)
        tx1.currency = "CHF"
        tx1.bookingText = "this is a great booking text"
        tx1.bookingDate = LocalDateTime.of(2020, 2, 14, 10, 10, 10)
        tx1.categoryId =
            category1
        result.add(tx1)

        // different category
        tx1 = Transaction()
        tx1.id = UUID.randomUUID().toString()
        tx1.amount = BigDecimal(-333)
        tx1.currency = "CHF"
        tx1.bookingText = "this is a great booking text"
        tx1.bookingDate = LocalDateTime.of(2020, 2, 5, 10, 10, 10)
        tx1.categoryId =
            category2
        result.add(tx1)

        // different months of the same year
        tx1 = Transaction()
        tx1.id = UUID.randomUUID().toString()
        tx1.amount = BigDecimal(-1000)
        tx1.currency = "CHF"
        tx1.bookingText = "this is a great booking text"
        tx1.bookingDate = LocalDateTime.of(2020, 1, 7, 10, 10, 10)
        tx1.categoryId =
            category1
        result.add(tx1)

        // different year
        tx1 = Transaction()
        tx1.id = UUID.randomUUID().toString()
        tx1.amount = BigDecimal(-7)
        tx1.currency = "CHF"
        tx1.bookingText = "this is a great booking text"
        tx1.bookingDate = LocalDateTime.of(2019, 5, 5, 10, 10, 10)
        tx1.categoryId =
            category1
        result.add(tx1)

        return result
    }

    internal fun getTransactionsForBudgetRunningTotal() : List<Transaction> {
        val result = ArrayList<Transaction>()

        // Date() - always belongs to the current month, what is important

        // parent category
        var tx1 = Transaction()
        tx1.id = UUID.randomUUID().toString()
        tx1.amount = BigDecimal(-111)
        tx1.currency = "CHF"
        tx1.bookingText = "this is a great booking text"
        tx1.bookingDate = LocalDateTime.now()
        tx1.categoryId = TransactionCategory.getIntId("leisure_shopping")
        result.add(tx1)

        // sub category - should not be accounted
        tx1 = Transaction()
        tx1.id = UUID.randomUUID().toString()
        tx1.amount = BigDecimal(-200)
        tx1.currency = "CHF"
        tx1.bookingText = "this is a great booking text"
        tx1.bookingDate = LocalDateTime.now()
        tx1.categoryId = TransactionCategory.getIntId("electronics")
        result.add(tx1)

        // uncategorised
        tx1 = Transaction()
        tx1.id = UUID.randomUUID().toString()
        tx1.amount = BigDecimal(-350)
        tx1.currency = "CHF"
        tx1.bookingText = "this is a great booking text"
        tx1.bookingDate = LocalDateTime.now()
        tx1.categoryId = TransactionCategory.UNCATEGORISED_INT_ID
        result.add(tx1)

        // just a different one to bring in some randomness
        tx1 = Transaction()
        tx1.id = UUID.randomUUID().toString()
        tx1.amount = BigDecimal(-333)
        tx1.currency = "CHF"
        tx1.bookingText = "this is a great booking text"
        tx1.bookingDate = LocalDateTime.now()
        tx1.categoryId = TransactionCategory.getIntId("cash")
        result.add(tx1)

        return result
    }

    internal fun getBudgetsForRunningTotal() : List<Budget> {
        val b_leisure_shopping = Budget()
        b_leisure_shopping.id = UUID.randomUUID().toString()
        b_leisure_shopping.externalId = "201"
        b_leisure_shopping.name = "sweets"
        b_leisure_shopping.budgeted = BigDecimal(1000)
        b_leisure_shopping.alert = BigDecimal(430)
        b_leisure_shopping.categoryId = TransactionCategory.getIntId("leisure_shopping")
        b_leisure_shopping.frequencyType = BudgetFrequency.monthly

        val b_electronics = Budget()
        b_electronics.id = UUID.randomUUID().toString()
        b_electronics.externalId ="202"
        b_electronics.name = "houses"
        b_electronics.budgeted = BigDecimal("1000")
        b_electronics.alert = BigDecimal("800")
        b_electronics.categoryId = TransactionCategory.getIntId("electronics")
        b_electronics.frequencyType = BudgetFrequency.monthly

        val b_default = Budget()
        b_default.id = UUID.randomUUID().toString()
        b_default.externalId = "203"
        b_default.name = "restaurants"
        b_default.budgeted = BigDecimal("300")
        b_default.alert = BigDecimal("200")
        b_default.categoryId = TransactionCategory.UNCATEGORISED_INT_ID
        b_default.frequencyType = BudgetFrequency.monthly

        return listOf(b_leisure_shopping, b_electronics, b_default)
    }

    internal fun getTransactionsForBudgetingEvents() : List<Transaction> {
        var result = ArrayList<Transaction>()

        // Date() - always belongs to the current month, what is important

        var tx1 = Transaction()
        tx1.id = UUID.randomUUID().toString()
        tx1.amount = BigDecimal(-100)
        tx1.currency = "CHF"
        tx1.bookingText = "this is a great booking text"
        tx1.bookingDate = LocalDateTime.now()
        tx1.categoryId =
            category1
        result.add(tx1)

        tx1 = Transaction()
        tx1.id = UUID.randomUUID().toString()
        tx1.amount = BigDecimal(-300)
        tx1.currency = "CHF"
        tx1.bookingText = "this is a great booking text"
        tx1.bookingDate = LocalDateTime.now()
        tx1.categoryId =
            category1
        result.add(tx1)

        tx1 = Transaction()
        tx1.id = UUID.randomUUID().toString()
        tx1.amount = BigDecimal(-150)
        tx1.currency = "CHF"
        tx1.bookingText = "this is a great booking text"
        tx1.bookingDate = LocalDateTime.now()
        tx1.categoryId =
            category1
        result.add(tx1)

        // different category
        tx1 = Transaction()
        tx1.id = UUID.randomUUID().toString()
        tx1.amount = BigDecimal(-333)
        tx1.currency = "CHF"
        tx1.bookingText = "this is a great booking text"
        tx1.bookingDate = LocalDateTime.now()
        tx1.categoryId =
            category2
        result.add(tx1)

        // different months of the same year
        tx1 = Transaction()
        tx1.id = UUID.randomUUID().toString()
        tx1.amount = BigDecimal(-1000)
        tx1.currency = "CHF"
        tx1.bookingText = "this is a great booking text"
        tx1.bookingDate = LocalDateTime.now()
        tx1.categoryId =
            category2
        result.add(tx1)

        // different year
        tx1 = Transaction()
        tx1.id = UUID.randomUUID().toString()
        tx1.amount = BigDecimal(-7)
        tx1.currency = "CHF"
        tx1.bookingText = "this is a great booking text"
        tx1.bookingDate = LocalDateTime.now()
        tx1.categoryId =
            category2
        result.add(tx1)

        return result
    }
}