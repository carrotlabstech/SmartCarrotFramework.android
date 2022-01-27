package com.carrotlabs.smartcarrotframework.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.carrotlabs.smartcarrotframework.*

internal class BudgetDbHelper(context: Context) :
    DbHelperBase<Budget>(context,
        BudgetContract.BudgetEntry.TABLE_NAME,
        BudgetContract.BudgetEntry.COLUMN_NAME_ID
    ) {

    override fun putRow(values: ContentValues, entity: Budget) {
        putOptionalEncoded(values, BudgetContract.BudgetEntry.COLUMN_NAME_ID, entity.id)
        putOptionalEncoded(values, BudgetContract.BudgetEntry.COLUMN_NAME_EXTERNAL_ID, entity.externalId)
        putOptionalEncoded(values, BudgetContract.BudgetEntry.COLUMN_NAME_ACCOUNT_ID, entity.accountId)
        putOptionalEncoded(values, BudgetContract.BudgetEntry.COLUMN_NAME_CURRENCY, entity.currency)
        putOptionalEncoded(values, BudgetContract.BudgetEntry.COLUMN_NAME_NAME, entity.name)
        putOptionalEncoded(values, BudgetContract.BudgetEntry.COLUMN_NAME_LIMIT, entity.budgeted)
        putOptionalEncoded(values, BudgetContract.BudgetEntry.COLUMN_NAME_ALERT, entity.alert)
        putOptionalEncoded(values, BudgetContract.BudgetEntry.COLUMN_NAME_CATEGORY_ID, entity.categoryId)
        putOptionalEncoded(values, BudgetContract.BudgetEntry.COLUMN_NAME_FREQUENCY, if (entity.frequencyType == BudgetFrequency.monthly) 1 else 2)
    }

    override fun cursorToEntityType(c: Cursor) : Budget {
        return c.toBudget()
    }

    override fun postNotification() {
        NotificationCenter.post(NotificationCenter.NotificationName.budgetListDidUpdate, null)
    }
}
