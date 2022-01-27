package com.carrotlabs.smartcarrotframework.db

import com.carrotlabs.smartcarrotframework.Budget
import com.carrotlabs.smartcarrotframework.NotificationCenter
import java.math.BigDecimal

internal class BudgetContainer internal constructor(budget: Budget, runningTotal: BigDecimal) {
    internal var budget: Budget
    internal var runningTotal: BigDecimal

    init {
        this.budget = budget
        this.runningTotal = BigDecimal(0)

        validateAndPostBudgetOverspent(runningTotal)
    }

    internal fun validateAndPostBudgetOverspent(newRunningTotal: BigDecimal) {
        if (runningTotal != newRunningTotal) {
            runningTotal = newRunningTotal

            if (runningTotal > budget.budgeted) {
                NotificationCenter.post(NotificationCenter.NotificationName.budgetDidOverSpend, this.budget)
            } else if(runningTotal > budget.alert) {
                NotificationCenter.post(NotificationCenter.NotificationName.budgetDidAlert, this.budget)
            }
        }
    }
}