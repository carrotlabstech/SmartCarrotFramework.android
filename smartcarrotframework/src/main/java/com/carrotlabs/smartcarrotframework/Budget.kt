package com.carrotlabs.smartcarrotframework

import com.carrotlabs.smartcarrotframework.db.EntityTypeWithId
import java.math.BigDecimal

// Ideally this should be a data class with non-modifiable properties.
// However, we need to validate few fields in-between
// then the best solution will be this one
// https://stackoverflow.com/questions/38492103/override-getter-for-kotlin-data-class

/**
 * Incremental Budget
 */
public class Budget(
    id: String = "",
    externalId: String? = null,
    accountId: String? = null,
    currency: String? = null,
    name: String = "",
    budgeted: BigDecimal = BigDecimal(0),
    alert: BigDecimal = BigDecimal(0),
    categoryId: Int = TransactionCategory.UNCATEGORISED_INT_ID,
    frequencyType: BudgetFrequency = BudgetFrequency.monthly
) : EntityTypeWithId {
    /**
     * Budget Id, maintained by the customer
     */
    override var id: String = ""

    /**
     * External Id
     */
    var externalId: String? = null

    /**
     * Account Id. Is not taken into account for calculations, reserved for future use.
     */
    var accountId: String? = null

    /**
     * Currency. Is not taken into account for calculations, reserved for future use.
     */
    var currency: String? = null

    /**
     * Budget Name
     */
    var name: String = ""

    /**
     * Budgeted Amount, always positive
     */
    var budgeted: BigDecimal = BigDecimal(0)
        get() = if (field < BigDecimal.ZERO) BigDecimal.ZERO else field

    /**
     * Budget Alert Threshold, a notification will be sent if it has been reached
     */
    var alert: BigDecimal = BigDecimal(0)
        get() = if (field < BigDecimal.ZERO) BigDecimal.ZERO else field

    /**
     * Budget Category Id
     */
    var categoryId: Int = TransactionCategory.UNCATEGORISED_INT_ID
        get() = if (field < 0) TransactionCategory.UNCATEGORISED_INT_ID else field

    /**
     * Budget Frequency Type
     */
    var frequencyType: BudgetFrequency = BudgetFrequency.monthly

    /**
     * @constructor
     * Initialises a new incremental budget.
     * @param id id of the new budget
     * @param externalId external id of the new budget, can be nil
     * @param currency budget currency. Is not taken inoto account for calculations, reserved for future use.
     * @param accountId accountId id of the new budget, can be nil. Reserved for future use.
     * @param name name of the new budget
     * @param budgeted the budgeted amount, can't be negative. Will be set to zero if a negative amount passed
     * @param alert the alert threshold level amount, can't be negative. Will be set to zero if a negative amount passed
     * @param categoryId the budget category, can't be negative. Will be set to [TransactionCategory.UNCATEGORISED_IDNAME] if a negative amount passed
     * @param frequencyType the budget frequency (e.g. monthly / annual)
     */
    init {
        this.id = id
        this.externalId = externalId
        this.accountId = accountId
        this.currency = currency
        this.name = name
        this.budgeted = budgeted
        this.alert = alert
        this.categoryId = categoryId
        this.frequencyType = frequencyType
    }

    /**
     * Copies a budget.
     *
     * @return a new budget with identical properties.
     */
    public fun copy() : Budget {
        return Budget(
            id = this.id,
            externalId = this.externalId,
            accountId = this.accountId,
            currency = this.currency,
            name = this.name,
            budgeted = this.budgeted,
            alert = this.alert,
            categoryId = this.categoryId,
            frequencyType = this.frequencyType
        )
    }
}
