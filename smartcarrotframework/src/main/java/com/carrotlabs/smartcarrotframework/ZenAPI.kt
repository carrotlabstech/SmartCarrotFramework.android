package com.carrotlabs.smartcarrotframework

import com.carrotlabs.smartcarrotframework.scoring.Zen
import com.carrotlabs.smartcarrotframework.scoring.ZenScore
import com.carrotlabs.smartcarrotframework.utils.CarrotCredentials
import com.carrotlabs.smartcarrotframework.utils.LicenseDecoder
import org.threeten.bp.LocalDate

/**
[ZenAPI] class is responsible for building Zen Scores.

The object of the class cannot be explicitly instantiated, please call [CarrotContext.getZenAPI] for that.
 */
public final class ZenAPI : ModuleAPI {
    private var _transactionAPI: TransactionAPI? = null
    private var _budgetAPI: BudgetAPI? = null

    @Throws
    internal constructor(transactionAPI: TransactionAPI, budgetAPI: BudgetAPI, credentials: CarrotCredentials?) :
            super(listOf(LicenseDecoder.MODULE_ZEN), credentials) {
        _transactionAPI = transactionAPI
        _budgetAPI = budgetAPI

        validateSettings()
    }

    /**
     * Builds an array of [ZenScore]s based on user's saved financial information.
     *
     * @throws [ZenScoreNoBudgetsCarrotContextError] if there is no one budget found.
     *
     * @return A list of user's [ZenScore].
     * The list includes all the days from the first saved transaction date to the last saved transaction date consequently.
     * The maximum interval is limited by [ZenParams.MAX_INTERVAL] days, and if it is exceeded then the last date in the [ZenScore] sequence is the closest saved transaction date (day).
     */
    @Throws
    public fun buildZen() : List<ZenScore> {
        return buildZen(transactions = _transactionAPI!!.getAll(), budgets = _budgetAPI!!.getAll())
    }

    /**
     * Builds an array of `ZenScore`s based on user's saved financial information.
     *
     * @Param startDate the first date for the generated [ZenScore] sequence.
     * @Param endDate the last date for the generated [ZenScore] sequence.
     *
     * @throws [ZenScoreNoBudgetsCarrotContextError] if there is no one budget found.
     *
     * @return A list of user's [ZenScore]. The list includes all the days from the [startDate] to the [endDate] consequently.
     * The maximum interval is limited by [ZenParams.MAX_INTERVAL] days, and if it is exceeded then the last date in the [ZenScore] sequence is the [endDate] date.
     */
    @Throws
    public fun buildZen(startDate: LocalDate, endDate: LocalDate) : List<ZenScore> {
        val transactions = _transactionAPI!!.getAll().filter { it.bookingDate.toLocalDate() >= startDate && it.bookingDate.toLocalDate() <= endDate}
        return buildZen(transactions = transactions, budgets = _budgetAPI!!.getAll())
    }

    internal fun buildZen(transactions: List<Transaction>, budgets: List<Budget>) : List<ZenScore> {
        if (budgets.count() == 0) {
            throw ZenScoreNoBudgetsCarrotContextError()
        }

        val zenBuilder = Zen(transactions = transactions, budgets = budgets)
        return zenBuilder.build()
    }
}