package com.carrotlabs.smartcarrotframework.scoring

import com.carrotlabs.smartcarrotframework.Budget
import com.carrotlabs.smartcarrotframework.BudgetFrequency
import com.carrotlabs.smartcarrotframework.Transaction
import com.carrotlabs.smartcarrotframework.utils.doubleValue
import org.threeten.bp.LocalDate
import java.math.BigDecimal
import kotlin.math.abs
import kotlin.math.exp

internal class Zen internal constructor(budgets: List<Budget>, transactions: List<Transaction>) {
    private val _budgets: List<Budget>
    private val _transactions: List<Transaction>
    private var _startDaySince1970: Int
    private val _endDaySince1970: Int

    init {
        this._budgets = budgets
        this._transactions = transactions

        _startDaySince1970 = ZenUtils.daySince1970(_transactions.map { it.bookingDate }.min()!!.toLocalDate())
        _endDaySince1970 = ZenUtils.daySince1970(_transactions.map { it.bookingDate }.max()!!.toLocalDate())

        if (_endDaySince1970 - _startDaySince1970 > ZenParams.MAX_INTERVAL) {
            _startDaySince1970 = _endDaySince1970 - ZenParams.MAX_INTERVAL
        }
    }

    internal fun build() : List<ZenScore> {
        if (_transactions.count() == 0) {
            return emptyList()
        }

        if (_transactions.count() == 1) {
            return listOf(ZenScore(ZenUtils.daySince1970(_transactions[0].bookingDate.toLocalDate()), 0.0))
        }

        val outputFrame = buildOutputFrame()
        val incomeVector = buildIncomeVector()
        return buildZen(outputFrame, incomeVector).toList()
    }

    internal fun buildZen(outputFrame: Array<ZenItem>, incomeVector: DoubleArray) : Array<ZenScore> {
        val months = ZenUtils.monthSince1970(_endDaySince1970) - ZenUtils.monthSince1970(_startDaySince1970)

        var averageMonthlyIncome = incomeVector.sumByDouble { it }  / months
        averageMonthlyIncome = if (averageMonthlyIncome > ZenParams.minSalary) averageMonthlyIncome else ZenParams.minSalary

        val budgetsMax = _budgets
                .map { monthlyBudgeted(it) / averageMonthlyIncome - ZenParams.getStrike( it.categoryId) }
                .map { if (it > 0.0) it else 0.0 }

        // sum ( budgetsMax.elementN * budgetWeights.elementN)
        //val P =  zip(budgetsMax, budgetWeights()).map(*).reduce(0, +)
        val budgetWeights = budgetWeights()
        val PArr = DoubleArray (budgetsMax.size) { _ -> 0.0 }
        for (i in 0..budgetsMax.count() - 1) {
            PArr[i] = budgetsMax[i] * budgetWeights[i]
        }
        val P =  PArr.sumByDouble { it }
        val f_P = 1.0 - 0.5 * (1.0 - exp(-ZenParams.delta * P))

        val RawSpendingScore = outputFrame.map { it.UnbudgetedSpendingFactor * it.DailyUtilizationScore * f_P }

        val m = (ZenParams.finalValue - ZenParams.lambda) / (1.0 - ZenParams.kickInPoint)
        val b = (ZenParams.lambda - ZenParams.finalValue) / (1.0 - ZenParams.kickInPoint) + ZenParams.finalValue

        var zenScores = ArrayList<ZenScore>()
        zenScores.add(ZenScore(_startDaySince1970, 0.0))

        for (day in 1..RawSpendingScore.size - 1) {
            zenScores.add(ZenScore(day + _startDaySince1970, 0.0))

            if (RawSpendingScore[day] >= zenScores[day - 1].Score) {
                zenScores[day].Score = ZenParams.lambda * zenScores[day-1].Score + (1.0-ZenParams.lambda) * RawSpendingScore[day]
                //zenScores[day].Score = ZenParams.lambda.multiply(zenScores[day-1].Score).add((BigDecimal.ONE-ZenParams.lambda).multiply(RawSpendingScore[day]))
            } else {
                //val penaltyFactor = m * zenScores[day - 1].Score + b
                val penaltyFactor = m * (zenScores[day - 1].Score) + b
                zenScores[day].Score = penaltyFactor * zenScores[day-1].Score + (1.0-penaltyFactor) * RawSpendingScore[day]
            }
        }

        return zenScores.toTypedArray()
    }

    internal fun buildOutputFrame() : Array<ZenItem> {
        val spendingMatrix = buildSpendingMatrix()
        val unbudgetedSpendingVector = buildUnbudgetedSpendingVector()
        val utilisationMatrix = buildUtilisationMatrix(spendingMatrix)
        val averageUtilisationAggregated = buildUtilisationMatrixWithBudgetWeights(utilisationMatrix)

        val outputFrame = ArrayList<ZenItem>()
        for (daySince1970 in 0..averageUtilisationAggregated.size - 1) {
            val rawUtilisatinScore = ZenUtils.r(averageUtilisationAggregated[daySince1970], ZenParams.alpha, ZenParams.beta, ZenParams.gamma)

            val rowSum = spendingMatrix[daySince1970].sumByDouble() { it }
            var unbudgetedSpendingFactor = rowSum + unbudgetedSpendingVector[daySince1970]
            unbudgetedSpendingFactor = if (unbudgetedSpendingFactor == 0.0) 1.0 else rowSum / unbudgetedSpendingFactor

            val zenScoreItem = ZenItem( daySince1970 + _startDaySince1970,
                averageUtilisationAggregated[daySince1970],
                rawUtilisatinScore,
                ZenUtils.sigmoid(rawUtilisatinScore),
                unbudgetedSpendingFactor,
                rowSum + unbudgetedSpendingVector[daySince1970])

            outputFrame.add(zenScoreItem)
        }

        return outputFrame.toTypedArray()
    }

    internal fun buildUtilisationMatrixWithBudgetWeights(utilisaitonMatrix: Array<DoubleArray>) : DoubleArray {
        val budgetWeigts = budgetWeights()

        var averageUtilisationMatrix = utilisaitonMatrix

        // from other calculations we assume that utilisationMatrix has Budgets.count columns
        for (i in 0..averageUtilisationMatrix.size - 1) {
            for (j in 0..averageUtilisationMatrix[i].size - 1) {
            averageUtilisationMatrix[i][j] = averageUtilisationMatrix[i][j] * budgetWeigts[j]
        }
        }

        return averageUtilisationMatrix.map { it.sumByDouble { it } }.toDoubleArray()
    }

    private fun budgetWeights() : DoubleArray {
        val budgetWeigts = _budgets.map { monthlyBudgeted( it ) }
        val budgetSum = budgetWeigts.sumByDouble { it }
        return budgetWeigts.map { it / budgetSum }.toDoubleArray()
    }

    private fun monthlyBudgeted(budget: Budget) : Double {
        if (budget.frequencyType == BudgetFrequency.monthly) {
            return budget.budgeted.doubleValue()
        } else {
            return budget.budgeted.doubleValue() / 12.0
        }
    }

    internal fun buildUtilisationMatrix(spendingMatrix: Array<DoubleArray>) : Array<DoubleArray> {
        var utilisationMatrix = Array(spendingMatrix.count()) { DoubleArray(spendingMatrix[0].size) { 0.0 } }
        Array(_endDaySince1970 - _startDaySince1970 + 1) { DoubleArray( _budgets.size) { 0.0 }}

        var monthSince1970 = ZenUtils.monthSince1970(_startDaySince1970)

        // cumulative per day within a month (within a budget)
        // unlist(cumsum( LMatrix[wRow,iCol] )
        for (currentDay in 0..(_endDaySince1970 - _startDaySince1970)) {
            for (bugetCategoryNo in 0..spendingMatrix[currentDay].size - 1) {
                utilisationMatrix[currentDay][bugetCategoryNo] = spendingMatrix[currentDay][bugetCategoryNo]
            }

            val currentMonthSince1970 = ZenUtils.monthSince1970(currentDay + _startDaySince1970)

            if (currentDay > 0 && currentMonthSince1970 == monthSince1970) {
                for (bugetCategoryNo in 0..spendingMatrix[currentDay].size - 1) {
                    utilisationMatrix[currentDay][bugetCategoryNo] = utilisationMatrix[currentDay - 1][bugetCategoryNo] + spendingMatrix[currentDay][bugetCategoryNo]
                }
            }

            monthSince1970 = currentMonthSince1970
        }

        // multiply by (count_days / current_day)
        // divide by budget's monthly budgeted
        // LMatrix <- LMatrix / xBudget
        for (currentDay in 0..(_endDaySince1970 - _startDaySince1970)) {
            val currentMonthSince1970 = ZenUtils.monthSince1970(currentDay + _startDaySince1970)

            for (bugetCategoryNo in 0..spendingMatrix[currentDay].size - 1) {
                utilisationMatrix[currentDay][bugetCategoryNo] =
                    utilisationMatrix[currentDay][bugetCategoryNo] * ZenUtils.daysPerMonth(currentMonthSince1970) / ZenUtils.dayOfMonth(currentDay + _startDaySince1970)

                var budgetMonthlyBudgeted = _budgets[bugetCategoryNo].budgeted.doubleValue()
                if (_budgets[bugetCategoryNo].frequencyType == BudgetFrequency.annual) {
                    budgetMonthlyBudgeted = budgetMonthlyBudgeted / 12.0
                }

                // This part should be commented to test UtilisationMatrixMid
                utilisationMatrix[currentDay][bugetCategoryNo] = utilisationMatrix[currentDay][bugetCategoryNo] * (1.0 / budgetMonthlyBudgeted)
            }
        }

        return utilisationMatrix
    }

    internal fun buildSpendingMatrix() : Array<DoubleArray> {
        val spendingMatrix = Array(_endDaySince1970 - _startDaySince1970 + 1) { DoubleArray( _budgets.size) { 0.0 }}

        for (daySince1970 in _startDaySince1970.._endDaySince1970) {
            val spendingRow = spendingMatrix[daySince1970 - _startDaySince1970]

            for (i in 0.._budgets.size - 1) {
                spendingRow[i] = abs(_transactions
                    .filter { it.categoryId != null
                            && it.categoryId == _budgets[i].categoryId
                            && ZenUtils.daySince1970(it.bookingDate.toLocalDate()) == daySince1970 }
                    .map { it.amount.doubleValue() }
                    .sumByDouble { it })
            }
        }

        return spendingMatrix
    }

    internal fun buildIncomeVector() : DoubleArray {
        var income = DoubleArray(_endDaySince1970 - _startDaySince1970 + 1) { _ -> 0.0 }

        for (daySince1970 in _startDaySince1970.._endDaySince1970) {
            income[daySince1970 - _startDaySince1970] = _transactions
                .filter { it.amount > BigDecimal.ZERO }
                .map { it.amount.doubleValue() }
                .sumByDouble { it }
        }

        return income
    }

    internal fun buildUnbudgetedSpendingVector() : DoubleArray {
        var unspent = DoubleArray(_endDaySince1970 - _startDaySince1970 + 1) { _ -> 0.0 }

        for (daySince1970 in _startDaySince1970.._endDaySince1970) {
            unspent[daySince1970 - _startDaySince1970] = _transactions.filter {
                    it.amount.compareTo(BigDecimal.ZERO) < 0 &&
                            ZenUtils.daySince1970(it.bookingDate.toLocalDate()) == daySince1970 &&
                            !ZenUtils.budgetsIncludeCategory(_budgets, it.categoryId)  }
                .map { it.amount.doubleValue() }
                .sumByDouble { it }

            unspent[daySince1970 - _startDaySince1970] = abs(unspent[daySince1970 - _startDaySince1970])
        }

        return unspent
    }
}

internal class ZenItem internal constructor(DateSince1970: Int,
                                            AverageUtilisation: Double,
                                            RawUtilisatinScore: Double,
                                            DailyUtilizationScore:Double,
                                            UnbudgetedSpendingFactor: Double,
                                            TotalSpending: Double) {
    companion object {
        internal var _eps = 0.0001
    }

    internal var DateSince1970: Int
    internal var AverageUtilisation: Double
    internal var RawUtilisatinScore: Double
    internal var DailyUtilizationScore: Double
    internal var UnbudgetedSpendingFactor: Double
    internal var TotalSpending: Double
    internal var ZenScore = 0.0

    init {
        this.DateSince1970 = DateSince1970
        this.AverageUtilisation = AverageUtilisation
        this.RawUtilisatinScore = RawUtilisatinScore
        this.DailyUtilizationScore = DailyUtilizationScore
        this.UnbudgetedSpendingFactor = UnbudgetedSpendingFactor
        this.TotalSpending = TotalSpending
    }

    internal constructor(DateSince1970: Int,
                         AverageUtilisation: Double,
                         RawUtilisatinScore: Double,
                         DailyUtilizationScore:Double,
                         UnbudgetedSpendingFactor: Double,
                         TotalSpending: Double,
                         ZenScore: Double) : this(
                            DateSince1970,
                            AverageUtilisation,
                            RawUtilisatinScore,
                            DailyUtilizationScore,
                            UnbudgetedSpendingFactor,
                            TotalSpending) {
        this.ZenScore = ZenScore
    }

    override fun equals(other: Any?): Boolean {
        val theOther = other as? ZenItem
        if (theOther == null) {
            return false
        }

        return this.DateSince1970 == theOther.DateSince1970 &&
                abs(this.AverageUtilisation - theOther.AverageUtilisation) < ZenItem._eps &&
                abs(this.RawUtilisatinScore - theOther.RawUtilisatinScore) < ZenItem._eps &&
                abs(this.DailyUtilizationScore - theOther.DailyUtilizationScore) < ZenItem._eps &&
                abs(this.UnbudgetedSpendingFactor - theOther.UnbudgetedSpendingFactor) < ZenItem._eps &&
                abs(this.TotalSpending - theOther.TotalSpending) < ZenItem._eps &&
                abs(this.ZenScore - theOther.ZenScore) < ZenItem._eps
    }
}

/**
 * Zen Score. Represents user's financial health for a given date.
 */
public final class ZenScore internal constructor(DaySince1970: Int, Score: Double) {
    /**
    * Date (day)
    */
    public val Date: LocalDate
        get() = ZenUtils.date(DaySince1970)

    internal var DaySince1970: Int

    /**
    * Score. Falls in range (0; 1). The higher the score is the better user's financial health on the given day.
    */
    public var Score: Double

    init {
        this.DaySince1970 = DaySince1970
        this.Score = Score
    }
}