package com.carrotlabs.smartcarrotframework.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.carrotlabs.smartcarrotframework.EmptyEntityIdCarrotContextError
import com.carrotlabs.smartcarrotframework.utils.aesEncrypt
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset
import kotlin.collections.ArrayList

internal interface DBHelperInterface {

}

internal interface EntityTypeWithId {
    var id: String
}

internal abstract class DbHelperBase<EntityType> (context: Context, TABLE_NAME: String, COLUMN_NAME_ID: String)
    : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION), DBHelperInterface
                                            where EntityType : EntityTypeWithId {

    companion object {
        // If you change the database schema, you must increment the database version.
        internal const val DATABASE_VERSION = 1
        internal const val DATABASE_NAME = "cldata.db"

        internal val SQL_CREATE_TABLES = listOf(
            "CREATE TABLE IF NOT EXISTS ${TransactionContract.TransactionEntry.TABLE_NAME} (" +
                "${TransactionContract.TransactionEntry.COLUMN_NAME_ID} TEXT PRIMARY KEY, " +
                "${TransactionContract.TransactionEntry.COLUMN_NAME_ACCOUNT_ID} TEXT, " +
                "${TransactionContract.TransactionEntry.COLUMN_NAME_AMOUNT} TEXT, " +
                "${TransactionContract.TransactionEntry.COLUMN_NAME_BOOKING_DATE} TEXT, " +
                "${TransactionContract.TransactionEntry.COLUMN_NAME_BOOKING_TEXT} TEXT, " +
                "${TransactionContract.TransactionEntry.COLUMN_NAME_CATEGORY_ID} TEXT, " +
                "${TransactionContract.TransactionEntry.COLUMN_NAME_CREATED} TEXT, " +
                "${TransactionContract.TransactionEntry.COLUMN_NAME_CURRENCY} TEXT, " +
                "${TransactionContract.TransactionEntry.COLUMN_NAME_CUSTOM_PROPERTY1} TEXT, " +
                "${TransactionContract.TransactionEntry.COLUMN_NAME_CUSTOM_PROPERTY2} TEXT, " +
                "${TransactionContract.TransactionEntry.COLUMN_NAME_EXTERNAL_ID} TEXT, " +
                "${TransactionContract.TransactionEntry.COLUMN_NAME_MODIFIED} TEXT, " +
                "${TransactionContract.TransactionEntry.COLUMN_NAME_SOURCE_AMOUNT} TEXT, " +
                "${TransactionContract.TransactionEntry.COLUMN_NAME_SOURCE_CURRENCY} TEXT, " +
                "${TransactionContract.TransactionEntry.COLUMN_NAME_SOURCE_EXCHANGE_RATE} TEXT, " +
                "${TransactionContract.TransactionEntry.COLUMN_NAME_USER_CATEGORY_ID} TEXT, " +
                "${TransactionContract.TransactionEntry.COLUMN_NAME_USER_NOTE} TEXT, " +
                "${TransactionContract.TransactionEntry.COLUMN_NAME_VALUE_DATE} TEXT)",
            "CREATE TABLE IF NOT EXISTS ${BudgetContract.BudgetEntry.TABLE_NAME} (" +
                "${BudgetContract.BudgetEntry.COLUMN_NAME_ID} TEXT PRIMARY KEY, " +
                "${BudgetContract.BudgetEntry.COLUMN_NAME_EXTERNAL_ID} TEXT, " +
                "${BudgetContract.BudgetEntry.COLUMN_NAME_ACCOUNT_ID} TEXT, " +
                "${BudgetContract.BudgetEntry.COLUMN_NAME_CURRENCY} TEXT, " +
                "${BudgetContract.BudgetEntry.COLUMN_NAME_NAME} TEXT, " +
                "${BudgetContract.BudgetEntry.COLUMN_NAME_LIMIT} TEXT, " +
                "${BudgetContract.BudgetEntry.COLUMN_NAME_ALERT} TEXT, " +
                "${BudgetContract.BudgetEntry.COLUMN_NAME_CATEGORY_ID} TEXT, " +
                "${BudgetContract.BudgetEntry.COLUMN_NAME_FREQUENCY} TEXT) "
        )
    }

    private val TABLE_NAME: String
    private val COLUMN_NAME_ID : String

    init {
        this.TABLE_NAME = TABLE_NAME
        this.COLUMN_NAME_ID = COLUMN_NAME_ID
    }

    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    override fun onCreate(db: SQLiteDatabase) {
        for (query in SQL_CREATE_TABLES) {
            db.execSQL(query)
        }
    }

    /**
     * Called when the database needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything else it
     * needs to upgrade to the new schema version.
     *
     *
     *
     * The SQLite ALTER TABLE documentation can be found
     * [here](http://sqlite.org/lang_altertable.html). If you add new columns
     * you can use ALTER TABLE to insert them into a live table. If you rename or remove columns
     * you can use ALTER TABLE to rename the old table, then create the new table and then
     * populate the new table with the contents of the old table.
     *
     *
     * This method executes within a transaction.  If an exception is thrown, all changes
     * will automatically be rolled back.
     *
     *
     * @param db The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        onCreate(db!!)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    internal fun addUpdate(entities: List<EntityType>) {
        if (entities.indexOfFirst { it.id.trim() == "" } >= 0 ) {
            throw EmptyEntityIdCarrotContextError()
        }

        val db = this.writableDatabase
        val values = ContentValues()

        for (tx in entities) {
            putRow(values, tx)
            db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE)
        }

        db.close()

        postNotification()
    }

    internal fun delete(items: List<EntityType>) {
        val db = this.writableDatabase
        for (item in items) {
            db.delete(TABLE_NAME, "$COLUMN_NAME_ID = ?", arrayOf(item.id))
        }
        db.close()

        postNotification()
    }

    internal fun update(entity: EntityType) {
        val db = this.writableDatabase
        val values = ContentValues()

        putRow(values, entity)
        db.update(TABLE_NAME, values, "$COLUMN_NAME_ID = ?", arrayOf(entity.id))

        db.close()
    }

    internal fun getById(id: String) : EntityType? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COLUMN_NAME_ID = ? ", arrayOf(id))
        var result : MutableList<EntityType> = mutableListOf<EntityType>()

        cursor?.use {
            result = cursorToEntityArrayList(it)
        }

        if (result.count() == 0) {
            return null
        } else {
            return result[0]
        }
    }

    internal fun getByIds(ids: List<String>) : ArrayList<EntityType> {
        val cursor = getAllRows()
        var result : MutableList<EntityType> = mutableListOf<EntityType>()

        cursor.use {
            result = cursorToEntityArrayList(cursor).filter { ids.contains(it.id) }.toMutableList()
        }

        return result as ArrayList<EntityType>
    }

    internal fun getAll() : ArrayList<EntityType> {
        val cursor = getAllRows()
        var result : MutableList<EntityType> = mutableListOf<EntityType>()

        cursor.use {
            result = cursorToEntityArrayList(cursor)
        }

        return result as ArrayList<EntityType>
    }

    private fun cursorToEntityArrayList(cursor: Cursor) : ArrayList<EntityType> {
        val result = generateSequence { if (cursor.moveToNext()) cursor else null }
            .map { cursorToEntityType(it) }
            .toMutableList()

        return result as ArrayList<EntityType>
    }

    internal fun wipe() {
        val db = this.writableDatabase
        val sqlDeleteStatement = "DELETE FROM $TABLE_NAME WHERE EXISTS (SELECT 1 FROM sqlite_master WHERE type='table' AND name='$TABLE_NAME') "
        db.execSQL(sqlDeleteStatement)

        postNotification()
    }

    protected abstract fun putRow(values: ContentValues, entity: EntityType)
    protected abstract fun cursorToEntityType(c: Cursor) : EntityType
    protected abstract fun postNotification()


    // Encodes the value to be put into the database
    protected fun putOptionalEncoded(values: ContentValues, key: String, param : Any?) {
        if (param == null) {
            values.putNull(key)
        } else {
            var str = param.toString()
            if (param is LocalDateTime) {
                str = param.toEpochSecond(ZoneOffset.UTC).toString()
            }

            // encode everything but not Id values
            str = if (isKeyId(key)) str else str.aesEncrypt()
            values.put(key, str)
        }
    }

    private fun getAllRows() : Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_NAME", null)
    }

    private fun isKeyId(key: String) : Boolean {
        return key.trim().toLowerCase() == "id"
    }
}