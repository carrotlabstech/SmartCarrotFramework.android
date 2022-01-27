package com.carrotlabs.smartcarrotframework

import android.content.Context
import com.carrotlabs.smartcarrotframework.db.BudgetContainer
import com.carrotlabs.smartcarrotframework.db.BudgetDbHelper
import com.carrotlabs.smartcarrotframework.utils.CarrotCredentials
import com.carrotlabs.smartcarrotframework.utils.LicenseDecoder
import com.carrotlabs.smartcarrotframework.utils.SharedContext
import org.threeten.bp.LocalDate
import java.lang.Exception
import java.math.BigDecimal


/**
 * The [BudgetAPI] class provides access to various incremental budget related functionality.
 *
 * The object of the class cannot be explicitly instantiated, please call [CarrotContext.getBudgetAPI] for that
 */
public final class BudgetAPI : ModuleAPI, Notifiable {
    private var _repository: BudgetDbHelper? = null
    private var _budgetContainers = ArrayList<BudgetContainer>()
    private var _transactionAPI: TransactionAPI? = null

    @Throws
    internal constructor(
        transactionAPI: TransactionAPI,
        credentials: CarrotCredentials?,
        context: Context
    ) : super(listOf(LicenseDecoder.MODULE_BUDGETS), credentials) {
        _transactionAPI = transactionAPI
        _repository =
            BudgetDbHelper(context)

        validateSettings()

        buildBudgetContainers()

        NotificationCenter.addObserver(NotificationCenter.NotificationName.transactionListDidUpdate, this)
        NotificationCenter.addObserver(NotificationCenter.NotificationName.budgetListDidUpdate, this)
    }

    /**
     * Must be closed when no more used.
     * Intended to remove all observers.
     */
    public final fun close() {
        NotificationCenter.removeObserver(this)
    }

    /**
     * Updates or inserts an array of budgets in the persistent storage. Identifies budgets by their id.
     *
     * @param budgets a list of [Budget] to add or update.
     * @throws
     */
    @Throws
    public fun upsert(budgets: List<Budget>) {
        validateSettings()
        _repository!!.addUpdate(budgets)
    }

    /**
    Deletes an array of budgets from the persistent storage. Identifies budgets by their `id`.

    - Parameter budgets: an array of `Budget` to be deleted.
     */
    /**
     * Deletes an array of budgets from the persistent storage. Identifies budgets by their `id`.
     *
     * @param budgets an array of [Budget] to be deleted.
     * @throws
     */
    @Throws
    public fun delete(budgets: List<Budget>) {
        validateSettings()
        return _repository!!.delete(budgets)
    }

    /**
     * Finds all budgets from the persistent storage.
     */
    @Throws
    public fun getAll(): ArrayList<Budget> {
        validateSettings()
        return _repository!!.getAll()
    }

    /**
     * Finds all budgets from the persistent storage, sorted.
     *
     * Ordered by (in order of priority):
     * - name asc,
     * - id asc.
     */
    @Throws
    public fun getAllOrderByNameId(): Array<Budget> {
        return getAll().sortedWith(compareBy({ it.name }, { it.id })).toTypedArray()
    }

    /**
     * Finds a [Budget] by its id, [null] if the [Budget] was not found.
     *
     * @param id a [Budget] id.
     *
     */
    @Throws
    public fun getById(id: String) : Budget?
    {
        validateSettings()
        return _repository!!.getById(id)
    }

    /**
     * Finds a count of transactions, which match the [Budget] by:
     * - current month if the [budget] is [BudgetFrequency.monthly]
     * - current month and year if the budget is [BudgetFrequency.annual]
     * - category
     * - currency (currently not implemented, reserved for future use)
     * - accountId (currently not implemented, reserved for future use)
     *
     * # Note:
     * If a budget matches a category, then category's sub-categories doesn't match the budget.
     *
     * @param budget a [Budget] to find the matching transactions count for.
     *
     * @returns count of transactions
     * @throws
     */
    @Throws
    public fun getTransactionsCount(budget: Budget): Int {
        val today = LocalDate.now()

        val year = today.year
        val month = today.month.value

        return getTransactionsCount(budget, year, month)
    }

    /**
     *
     * Finds a count of transactions, which match the [Budget] by:
     * - `month` if the [budget] is [BudgetFrequency.monthly]
     * - `month` and `year` if the [budget] is [BudgetFrequency.annual]
     * - category
     * - currency (currently not implemented, reserved for future use)
     * - accountId (currently not implemented, reserved for future use)
     *
     * # Note:
     * If a budget matches a category, then category's sub-categories doesn't match the budget.
     *
     * @param budget a [Budget] to find the matching transactions count for.
     * @param year a year to find the matching transactions count for.
     * @param month a month to find the matching transactions count for.
     *
     * @returns count of transactions
     * @throws
     */
    @Throws
    public fun getTransactionsCount(budget: Budget, year: Int, month: Int): Int {
        var total: Int = 0

        for (transaction in _transactionAPI!!.getAll()) {
            if (TransactionBelongsToBudget(transaction, budget, year, month)) {
                total += 1
            }
        }

        return total
    }

    /**
     *
     * Calculates budget running total as per `today`.
     *
     * @param budget a [Budget] to find the running total for.
     *
     * @returns budget running total.
     *
     * # Note:
     * Running total is always positive, it's an absolute value of all the transaction values.
     * @throws
     */
    @Throws
    public fun getRunningTotal(budget: Budget): BigDecimal {
        val today = LocalDate.now()

        val year = today.year
        val month = today.month.value

        return getRunningTotal(budget, year, month)
    }

    /**
     * Calculates budget running total as per selected year and month.
     *
     * @Param budget a [Budget] to find the running total for.
     * @Param year chosen year to calculate the running total for.
     * @Param month chosen month to calculate the running total for.
     *
     * @returns budget running total.
     *
     * # Note:
     * Running total is always positive, it's an absolute value of all the transaction values.
     * @throws
     */
    @Throws
    public fun getRunningTotal(budget: Budget, year: Int, month: Int): BigDecimal {
        var total: BigDecimal = BigDecimal(0)

        for (transaction in _transactionAPI!!.getAll()) {
            if (TransactionBelongsToBudget(transaction, budget, year, month)) {
                total += transaction.amount
            }
        }

        return total.abs()
    }

    /**
     * Calculates budget balance as per `today`.
     *
     * @param budget a [Budget] to find the balance.
     *
     * @returns budget balance.
    */
    @Throws
    public fun getBalance(budget: Budget): BigDecimal {
        return budget.budgeted - getRunningTotal(budget)
    }

    /**
     * Calculates budget balance as per selected year and month.
     *
     * @param budget a [Budget] to find the balance for.
     * @param year chosen year to calculate the balance for.
     * @param month chosen month to calculate the balance for.
     *
     * @return budget balance.
     */
    @Throws
    public fun getBalance(budget: Budget, year: Int, month: Int): BigDecimal {
        return budget.budgeted - getRunningTotal(budget, year, month)
    }

    @Throws
    internal override fun validateSettings() {
        super.validateSettings()

        if (SharedContext.key.trim().count() == 0) {
            throw EncryptionKeyNotSetCarrotContextError()
        }

        if (SharedContext.iv.trim().count() != 16) {
            throw EncryptionInvalidInitialisationVectorError()
        }
    }

    private fun TransactionBelongsToBudget(
        transaction: Transaction,
        budget: Budget,
        year: Int,
        month: Int
    ): Boolean {
        val txCategoryId = if (transaction.categoryId != null) transaction.categoryId else TransactionCategory.UNCATEGORISED_INT_ID


        val txYear = transaction.bookingDate.year
        val txMonth = transaction.bookingDate.month.value

        return txCategoryId == budget.categoryId && (txYear == year && (txMonth == month || budget.frequencyType == BudgetFrequency.annual))
    }

    public override fun onNotification(notify: Notification?) {
        if (notify != null) {
            if (notify.name == NotificationCenter.NotificationName.transactionListDidUpdate) {
                transactionListDidUpdateHandler()
            } else if (notify.name == NotificationCenter.NotificationName.budgetListDidUpdate) {
                budgetListDidUpdateHandler()
            }
        }
    }

    // budgets didn't change, only transactions
    // in this case is enough to recalc them and fire events
    private fun transactionListDidUpdateHandler() {
        for (budgetContainer in _budgetContainers) {
            try {
                val runningTotal = getRunningTotal(budgetContainer.budget)
                budgetContainer.validateAndPostBudgetOverspent(runningTotal)
            } catch (e: Exception) {
                // do nothing, we just don't recalc and send notificaitons
            }
        }
    }

    // budgets changed
    private fun budgetListDidUpdateHandler() {
        try {
            buildBudgetContainers()
        } catch (e:Exception) {
                // do nothing, we just don't recalc and send notificaitons
        }
    }


    private fun buildBudgetContainers() {
        val budgets = getAll()

        _budgetContainers = ArrayList<BudgetContainer>()
        for (budget in budgets) {
            _budgetContainers.add(BudgetContainer(budget, getRunningTotal(budget)))
        }
    }
}