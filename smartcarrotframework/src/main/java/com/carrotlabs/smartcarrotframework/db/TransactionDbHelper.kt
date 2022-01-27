package com.carrotlabs.smartcarrotframework.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.carrotlabs.smartcarrotframework.*

internal class TransactionDbHelper(context: Context) :
    DbHelperBase<Transaction>(context,
        TransactionContract.TransactionEntry.TABLE_NAME,
        TransactionContract.TransactionEntry.COLUMN_NAME_ID
    ) {

    override fun cursorToEntityType(c: Cursor) : Transaction {
        return c.toTransaction()
    }

    override fun putRow(values: ContentValues, entity: Transaction) {
        putOptionalEncoded(values, TransactionContract.TransactionEntry.COLUMN_NAME_ID, entity.id)
        putOptionalEncoded(values, TransactionContract.TransactionEntry.COLUMN_NAME_EXTERNAL_ID, entity.externalId)

        putOptionalEncoded(values, TransactionContract.TransactionEntry.COLUMN_NAME_ACCOUNT_ID, entity.accountId)
        putOptionalEncoded(values, TransactionContract.TransactionEntry.COLUMN_NAME_AMOUNT, entity.amount)
        putOptionalEncoded(values, TransactionContract.TransactionEntry.COLUMN_NAME_BOOKING_DATE, entity.bookingDate)
        putOptionalEncoded(values, TransactionContract.TransactionEntry.COLUMN_NAME_BOOKING_TEXT, entity.bookingText)
        putOptionalEncoded(values, TransactionContract.TransactionEntry.COLUMN_NAME_CATEGORY_ID, entity.categoryId)
        putOptionalEncoded(values, TransactionContract.TransactionEntry.COLUMN_NAME_CREATED, entity.created)
        putOptionalEncoded(values, TransactionContract.TransactionEntry.COLUMN_NAME_CURRENCY, entity.currency)
        putOptionalEncoded(values, TransactionContract.TransactionEntry.COLUMN_NAME_CUSTOM_PROPERTY1, entity.customProperty1)
        putOptionalEncoded(values, TransactionContract.TransactionEntry.COLUMN_NAME_CUSTOM_PROPERTY2, entity.customProperty2)
        putOptionalEncoded(values, TransactionContract.TransactionEntry.COLUMN_NAME_MODIFIED, entity.modified)
        putOptionalEncoded(values, TransactionContract.TransactionEntry.COLUMN_NAME_SOURCE_AMOUNT, entity.sourceAmount)
        putOptionalEncoded(values, TransactionContract.TransactionEntry.COLUMN_NAME_SOURCE_CURRENCY, entity.sourceCurrency)
        putOptionalEncoded(values, TransactionContract.TransactionEntry.COLUMN_NAME_SOURCE_EXCHANGE_RATE, entity.sourceExchangeRate)
        putOptionalEncoded(values, TransactionContract.TransactionEntry.COLUMN_NAME_USER_CATEGORY_ID, entity.userCategoryId)
        putOptionalEncoded(values, TransactionContract.TransactionEntry.COLUMN_NAME_USER_NOTE, entity.userNote)
        putOptionalEncoded(values, TransactionContract.TransactionEntry.COLUMN_NAME_VALUE_DATE, entity.valueDate)
    }

    override fun postNotification() {
        NotificationCenter.post(
            NotificationCenter.NotificationName.transactionListDidUpdate, null)
    }
}