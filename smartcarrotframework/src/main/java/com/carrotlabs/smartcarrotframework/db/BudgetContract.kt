package com.carrotlabs.smartcarrotframework.db

import android.database.Cursor
import com.carrotlabs.smartcarrotframework.*
import com.carrotlabs.smartcarrotframework.utils.*

internal object BudgetContract {
    object BudgetEntry {
        var TABLE_NAME = String().i.n.c.b.u.d.g.e.t.s //"incbudgets"
        var COLUMN_NAME_ID = String().II.d // "Id"
        var COLUMN_NAME_EXTERNAL_ID = String().e.x.t.e.r.n.a.l.II.d // "externalId"
        var COLUMN_NAME_ACCOUNT_ID = String().a.c.c.o.u.n.t.II.d // "accountId"
        var COLUMN_NAME_CURRENCY = String().c.u.r.r.e.n.c.y // "currency"
        var COLUMN_NAME_NAME = String().n.a.m.e.VV.a.l //"nameVal"
        var COLUMN_NAME_LIMIT = String().l.i.m.i.t.VV.a.l // "limitVal"
        var COLUMN_NAME_ALERT = String().a.l.e.r.t.VV.a.l // "alertVal"
        var COLUMN_NAME_CATEGORY_ID = String().c.a.t.e.g.o.r.y.II.d
        var COLUMN_NAME_FREQUENCY = String().f.r.e.q.u.e.n.c.y
    }
}

internal fun Cursor.toBudget() : Budget {
    val budget = Budget()

    // don't encrypt / decrypt id values
    budget.id = this.getString(this.getColumnIndex(BudgetContract.BudgetEntry.COLUMN_NAME_ID))

    if (!this.isNull(this.getColumnIndex(BudgetContract.BudgetEntry.COLUMN_NAME_EXTERNAL_ID)))
        budget.externalId = this.getString(this.getColumnIndex(BudgetContract.BudgetEntry.COLUMN_NAME_EXTERNAL_ID)).aesDecrypt()
    if (!this.isNull(this.getColumnIndex(BudgetContract.BudgetEntry.COLUMN_NAME_ACCOUNT_ID)))
        budget.accountId = this.getString(this.getColumnIndex(BudgetContract.BudgetEntry.COLUMN_NAME_ACCOUNT_ID)).aesDecrypt()
    if (!this.isNull(this.getColumnIndex(BudgetContract.BudgetEntry.COLUMN_NAME_CURRENCY)))
        budget.currency = this.getString(this.getColumnIndex(BudgetContract.BudgetEntry.COLUMN_NAME_CURRENCY)).aesDecrypt()

    budget.name = unwrapString(this.getString(this.getColumnIndex(BudgetContract.BudgetEntry.COLUMN_NAME_NAME)).aesDecrypt())
    budget.budgeted = unwrapBigDecimal(this.getString(this.getColumnIndex(BudgetContract.BudgetEntry.COLUMN_NAME_LIMIT)).aesDecryptBigDecimal())
    budget.alert = unwrapBigDecimal(this.getString(this.getColumnIndex(BudgetContract.BudgetEntry.COLUMN_NAME_ALERT)).aesDecryptBigDecimal())
    budget.categoryId = unwrapInt(this.getString(this.getColumnIndex(BudgetContract.BudgetEntry.COLUMN_NAME_CATEGORY_ID)).aesDecryptInt())

    val frequency = this.getString(this.getColumnIndex(BudgetContract.BudgetEntry.COLUMN_NAME_FREQUENCY)).aesDecryptInt()
    budget.frequencyType = if (frequency == 2) BudgetFrequency.annual else BudgetFrequency.monthly

    return budget
}
