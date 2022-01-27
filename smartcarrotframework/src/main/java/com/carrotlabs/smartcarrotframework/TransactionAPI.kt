package com.carrotlabs.smartcarrotframework

import android.content.Context
import com.carrotlabs.smartcarrotframework.db.TransactionDbHelper
import com.carrotlabs.smartcarrotframework.utils.CarrotCredentials
import com.carrotlabs.smartcarrotframework.utils.LicenseDecoder
import com.carrotlabs.smartcarrotframework.utils.SharedContext

/**
 * A [TransactionAPI] implements all the storage and categorisation functions on Transaction.
 *
 * The object of the class cannot be explicitly instantiated, please call [CarrotContext.getTransactionAPI] for that.
*/
public final class TransactionAPI internal constructor(modules: List<String>, credentials: CarrotCredentials?)
    : ModuleAPI(modules, credentials) {
    private var _repository : TransactionDbHelper? = null
    private var _categoriser: CategorisationAPI? = null

    @Throws
    internal constructor(categoriser: CategorisationAPI, credentials: CarrotCredentials?, context: Context) :
            this (listOf(LicenseDecoder.MODULE_TRANSACTIONS), credentials) {
        _categoriser = categoriser
        _repository =
            TransactionDbHelper(context)

        validateSettings()
    }

    /**
     * Adds or updates an array of [Transaction]s.
     *
     * if a transaction is already categorised, then it will be still recategorised. The method uses [Transaction.id] to determine whether the transaction is already present in the persistent storage and must be updated.
     *
     * @param transactions a list of transactions to be updated or inserted.
     * @param categorise whether the transactions must be categorised before saving into the persistent storage.
     * @throws
     */
    @Throws
    public fun upsert(transactions: List<Transaction>, categorise: Boolean = true) {
        validateSettings()
        if (categorise) {
            categorise(transactions)
        } else {
            _repository!!.addUpdate(transactions)
        }
    }

    /**
     * Deletes an array of [Transaction]s from the persistent storage. The method uses [Transaction.id] to identify a transaction to be deleted.
     *
     * @param transactions a list of transactions to be deleted from the persistent storage.
     * @throws
    */
    @Throws
    public fun delete(transactions: List<Transaction>) {
        validateSettings()
        return _repository!!.delete(transactions)
    }

    /**
     * Returns all transactions. [Transaction]s list is unsorted.
     * @throws
      */
    @Throws
    public fun getAll() : ArrayList<Transaction> {
        validateSettings()
        return _repository!!.getAll()
    }

    /**
     * Returns all transactions from the persistent storage, sorted.
     *
     * Returns all transactions, ordered by: (in order of priority)
     * - [Transaction.BookingDate] desc,
     * - [Transaction.BookingText] asc,
     * - [Transaction.id] asc
     * @throws
     */
    @Throws
    public fun getAllOrderByDateDescTextId() : Array<Transaction> {
        return getAll().sortedWith(compareBy( { it.bookingDate}, {it.bookingText}, {it.id}) ).toTypedArray()
    }

    /**
     * Returns a transaction by it's id. Returns [null] if there is no transaction with such an id in the persistent storage.
     *
     * @param id transaction id to look for.
     * @throws
     */
    @Throws
    public fun getById(id: String) : Transaction? {
        validateSettings()
        return _repository!!.getById(id)
    }

    /**
     * Categorises an array of transactions and updates them in the persistent storage
     *
     * @param transactions an array of transactions to be categorised.
     *
     * @return An array of categorised transactions, saved into the persistent storage.
     * @throws
     */
    @Throws
    public fun categorise(transactions: List<Transaction>) : ArrayList<Transaction> {
        validateSettings()

        val categorisedTxs = ArrayList<Transaction>()

        for (transaction in transactions) {
            val categorisedTx = transaction.copy()
            categorisedTx.categoryId = _categoriser!!.categorise(transaction.bookingText, transaction.amount, CategorisationType.personal)
            categorisedTxs.add(categorisedTx)
        }

        _repository!!.addUpdate(categorisedTxs)

        val idsList = categorisedTxs.map { it.id }
        return _repository!!.getByIds(idsList)
    }

    /**
     * Categorises a transaction and updates it in the persistent storage
     *
     * @param transaction a transaction to be categorised
     *
     * @return a categorised transaction from the persistent storage
     */
    @Throws
    public fun categorise(transaction: Transaction) : Transaction {
        validateSettings()

        return categorise(arrayListOf(transaction))[0]
    }

    internal override fun validateSettings() {
        super.validateSettings()

        if (_categoriser == null) {
            throw FailedToLoadModelCarrotContextError()
        }

        if (SharedContext.key.trim().count() == 0) {
            throw EncryptionKeyNotSetCarrotContextError()
        }

        if (SharedContext.iv.trim().count() != 16) {
            throw EncryptionInvalidInitialisationVectorError()
        }
    }
}