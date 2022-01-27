package com.carrotlabs.smartcarrotframework

import com.carrotlabs.smartcarrotframework.db.EntityTypeWithId
import org.threeten.bp.LocalDateTime
import java.math.BigDecimal

/**
 * Transaction
 */
public final class Transaction (
    id: String = "",
    externalId: String? = null,

    amount: BigDecimal = BigDecimal.ZERO,
    currency: String = "CHF",

    sourceAmount: BigDecimal? = null,
    sourceCurrency: String? = null,
    sourceExchangeRate: BigDecimal? = null,

    bookingText: String = "",

    bookingDate: LocalDateTime = LocalDateTime.now(),
    valueDate: LocalDateTime? = null,

    categoryId: Int? = null,
    userCategoryId: Int? = null,

    accountId: String? = null,

    userNote: String? = null,

    customProperty1: String? = null,
    customProperty2: String? = null,

    created: LocalDateTime? = null,
    modified: LocalDateTime? = null) :
    EntityTypeWithId {
    /**
     * Transaction Id. Should be set and controlled by the customer.
     */
    override var id: String = ""

    /**
     * External Id
     */
    var externalId: String? = null

    /**
     * Amount. Used by budgets and budget alerts
     */
    var amount: BigDecimal = BigDecimal.ZERO

    /**
     * Currency
     */
    var currency: String = "CHF"

    /**
     * Source Amount
     */
    var sourceAmount: BigDecimal? = null

    /**
     * Source Currency
     */
    var sourceCurrency: String? = null

    /**
     * Source Exchange Rate
     */
    var sourceExchangeRate: BigDecimal? = null

    /**
     * Booking Text
     */
    var bookingText: String = ""

    /**
     * Booking Date. Used by budgets and budget alerts, Zen scores
     */
    var bookingDate: LocalDateTime = LocalDateTime.now()

    /**
     * Value date
     */
    var valueDate: LocalDateTime? = null

    /**
     * Category Id
     */
    var categoryId: Int? = null

    /**
     * User category Id
     */
    var userCategoryId: Int? = null

    /**
     * Transaction Account id
     */
    var accountId: String? = null

    /**
     * User notes
     */
    var userNote: String? = null

    /**
     * Customer property one, reserved for customer's use.
     */
    var customProperty1: String? = null

    /**
     * Customer property two, reserved for customer's use.
     */
    var customProperty2: String? = null

    /**
     * Transactions creation date
     */
    var created: LocalDateTime? = null

    /**
     * Transaction modification date
     */
    var modified: LocalDateTime? = null

    init {
        this.id = id
        this.externalId = externalId

        this.amount = amount
        this.currency = currency

        this.sourceAmount = sourceAmount
        this.sourceCurrency = sourceCurrency
        this.sourceExchangeRate = sourceExchangeRate

        this.bookingText = bookingText

        this.bookingDate = bookingDate
        this.valueDate = valueDate

        this.categoryId = categoryId
        this.userCategoryId = userCategoryId

        this.accountId = accountId

        this.userNote = userNote

        this.customProperty1 = customProperty1
        this.customProperty2 = customProperty2

        this.created = created
        this.modified = modified
    }

    /**
     * Copies a transaction.
     *
     * @return a new transaction object, the exact copy of the transaction
     */
    public fun copy() : Transaction {
        return Transaction(
        id = this.id,
        externalId = this.externalId,

        amount = this.amount,
        currency= this.currency,

        sourceAmount = this.sourceAmount,
        sourceCurrency = sourceCurrency,
        sourceExchangeRate = sourceExchangeRate,

        bookingText = bookingText,

        bookingDate = this.bookingDate,
        valueDate = this.valueDate,

        categoryId = this.categoryId,
        userCategoryId = this.userCategoryId,

        accountId = this.accountId,

        userNote = this.userNote,

        customProperty1 = this.customProperty1,
        customProperty2 = this.customProperty2,

        created = this.created,
        modified = this.modified)
    }
}