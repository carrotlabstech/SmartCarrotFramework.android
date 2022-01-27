package com.carrotlabs.smartcarrotframework.db

import android.database.Cursor
import com.carrotlabs.smartcarrotframework.*
import com.carrotlabs.smartcarrotframework.utils.*

internal object TransactionContract {
    object TransactionEntry {
        var TABLE_NAME = String().t.r.a.n.s.a.c.t.i.o.n.s // "transactions"
        var COLUMN_NAME_ACCOUNT_ID = String().a.c.c.o.u.n.t.II.d // "accountId"
        var COLUMN_NAME_AMOUNT = String().a.m.o.u.n.t // "amount"
        var COLUMN_NAME_BOOKING_DATE = String().b.o.o.k.i.n.g.DD.a.t.e // "bookingDate"
        var COLUMN_NAME_BOOKING_TEXT = String().b.o.o.k.i.n.g.TT.e.x.t // "bookingText"
        var COLUMN_NAME_CATEGORY_ID = String().c.a.t.e.g.o.r.y.II.d // "categoryId"
        var COLUMN_NAME_CREATED = String().c.r.e.a.t.e.d // "created"
        var COLUMN_NAME_CURRENCY = String().c.u.r.r.e.n.c.y // "currency"
        var COLUMN_NAME_CUSTOM_PROPERTY1 = String().c.u.s.t.o.m.PP.r.o.p.e.r.t.y._1 // "customProperty2"
        var COLUMN_NAME_CUSTOM_PROPERTY2 = String().c.u.s.t.o.m.PP.r.o.p.e.r.t.y._2 // "customProperty2"
        var COLUMN_NAME_EXTERNAL_ID = String().e.x.t.e.r.n.a.l.II.d // "externalId"
        var COLUMN_NAME_ID = String().II.d
        var COLUMN_NAME_MODIFIED = String().m.o.d.i.f.i.e.d // "modified"
        var COLUMN_NAME_SOURCE_AMOUNT = String().s.o.u.r.c.e.AA.m.o.u.n.t // "sourceAmount"
        var COLUMN_NAME_SOURCE_CURRENCY = String().s.o.u.r.c.e.CC.u.r.r.e.n.c.y // "sourceCurrency"
        var COLUMN_NAME_SOURCE_EXCHANGE_RATE = String().s.o.u.r.c.e.EE.x.c.h.a.n.g.e.RR.a.t.e // "sourceExchangeRate"
        var COLUMN_NAME_USER_CATEGORY_ID = String().u.s.e.r.CC.a.t.e.g.o.r.y.II.d // "userCategoryId"
        var COLUMN_NAME_USER_NOTE = String().u.s.e.r.NN.o.t.e // "userNote"
        var COLUMN_NAME_VALUE_DATE = String().v.a.l.u.e.DD.a.t.e
    }
}

internal fun Cursor.toTransaction() : Transaction {
    val tx = Transaction()

    // don't encrypt / decrypt id values
    tx.id = this.getString(this.getColumnIndex(TransactionContract.TransactionEntry.COLUMN_NAME_ID))

    if (!this.isNull(this.getColumnIndex(TransactionContract.TransactionEntry.COLUMN_NAME_EXTERNAL_ID)))
        tx.externalId = this.getString(this.getColumnIndex(TransactionContract.TransactionEntry.COLUMN_NAME_EXTERNAL_ID)).aesDecrypt()

    tx.amount = unwrapBigDecimal(this.getString(this.getColumnIndex(TransactionContract.TransactionEntry.COLUMN_NAME_AMOUNT)).aesDecryptBigDecimal())
    tx.currency = unwrapString(this.getString(this.getColumnIndex(TransactionContract.TransactionEntry.COLUMN_NAME_CURRENCY)).aesDecrypt())

    if (!this.isNull(this.getColumnIndex(TransactionContract.TransactionEntry.COLUMN_NAME_SOURCE_AMOUNT)))
        tx.sourceAmount = this.getString(this.getColumnIndex(TransactionContract.TransactionEntry.COLUMN_NAME_SOURCE_AMOUNT)).aesDecryptBigDecimal()
    if (!this.isNull(this.getColumnIndex(TransactionContract.TransactionEntry.COLUMN_NAME_SOURCE_CURRENCY)))
        tx.sourceCurrency = this.getString(this.getColumnIndex(TransactionContract.TransactionEntry.COLUMN_NAME_SOURCE_CURRENCY)).aesDecrypt()
    if (!this.isNull(this.getColumnIndex(TransactionContract.TransactionEntry.COLUMN_NAME_SOURCE_EXCHANGE_RATE)))
        tx.sourceExchangeRate = this.getString(this.getColumnIndex(TransactionContract.TransactionEntry.COLUMN_NAME_SOURCE_EXCHANGE_RATE)).aesDecryptBigDecimal()

    tx.bookingText = unwrapString(this.getString(this.getColumnIndex(TransactionContract.TransactionEntry.COLUMN_NAME_BOOKING_TEXT)).aesDecrypt())
    tx.bookingDate = unwrapLocalDateTime(this.getString(this.getColumnIndex(TransactionContract.TransactionEntry.COLUMN_NAME_BOOKING_DATE)).aesDecryptLocalDate())

    if (!this.isNull(this.getColumnIndex(TransactionContract.TransactionEntry.COLUMN_NAME_VALUE_DATE)))
        tx.valueDate = this.getString(this.getColumnIndex(TransactionContract.TransactionEntry.COLUMN_NAME_VALUE_DATE)).aesDecryptLocalDate()

    if (!this.isNull(this.getColumnIndex(TransactionContract.TransactionEntry.COLUMN_NAME_CATEGORY_ID)))
        tx.categoryId = this.getString(this.getColumnIndex(TransactionContract.TransactionEntry.COLUMN_NAME_CATEGORY_ID)).aesDecryptInt()
    if (!this.isNull(this.getColumnIndex(TransactionContract.TransactionEntry.COLUMN_NAME_USER_CATEGORY_ID)))
        tx.userCategoryId = this.getString(this.getColumnIndex(TransactionContract.TransactionEntry.COLUMN_NAME_USER_CATEGORY_ID)).aesDecryptInt()

    if (!this.isNull(this.getColumnIndex(TransactionContract.TransactionEntry.COLUMN_NAME_ACCOUNT_ID)))
        tx.accountId = this.getString(this.getColumnIndex(TransactionContract.TransactionEntry.COLUMN_NAME_ACCOUNT_ID)).aesDecrypt()

    if (!this.isNull(this.getColumnIndex(TransactionContract.TransactionEntry.COLUMN_NAME_CUSTOM_PROPERTY1)))
        tx.customProperty1 = this.getString(this.getColumnIndex(TransactionContract.TransactionEntry.COLUMN_NAME_CUSTOM_PROPERTY1)).aesDecrypt()
    if (!this.isNull(this.getColumnIndex(TransactionContract.TransactionEntry.COLUMN_NAME_CUSTOM_PROPERTY2)))
        tx.customProperty1 = this.getString(this.getColumnIndex(TransactionContract.TransactionEntry.COLUMN_NAME_CUSTOM_PROPERTY2)).aesDecrypt()

    if (!this.isNull(this.getColumnIndex(TransactionContract.TransactionEntry.COLUMN_NAME_CREATED)))
        tx.created = this.getString(this.getColumnIndex(TransactionContract.TransactionEntry.COLUMN_NAME_CREATED)).aesDecryptLocalDate()
    if (!this.isNull(this.getColumnIndex(TransactionContract.TransactionEntry.COLUMN_NAME_MODIFIED)))
        tx.modified = this.getString(this.getColumnIndex(TransactionContract.TransactionEntry.COLUMN_NAME_MODIFIED)).aesDecryptLocalDate()

    return tx
}