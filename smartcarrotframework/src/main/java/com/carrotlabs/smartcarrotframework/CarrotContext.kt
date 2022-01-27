package com.carrotlabs.smartcarrotframework

import android.content.Context
import com.carrotlabs.smartcarrotframework.models.MasterModel
import com.carrotlabs.smartcarrotframework.models.PersistenceType
import com.carrotlabs.smartcarrotframework.models.PersistentStorage
import com.carrotlabs.smartcarrotframework.utils.CarrotCredentials
import com.carrotlabs.smartcarrotframework.utils.LicenseDecoder
import com.carrotlabs.smartcarrotframework.utils.SharedContext
import com.jakewharton.threetenabp.AndroidThreeTen
import org.json.JSONException

/**
 * Base class for all [CarrotContext] exceptions.
 */
public open class CarrotContextError : Exception {
    constructor() : super()
    constructor(message: String): super(message)
}

/**
 * Invalid license.
 */
public final class InvalidLicenseCarrotContextError : CarrotContextError()

/**
 * License has expired.
 */
public final class LicenseHasExpiredCarrotContextError : CarrotContextError()

/**
 * The license has been issued for a different application bundle id.
 */
public final class WrongBundleIdCarrotContextError : CarrotContextError()

/**
 * Could not load application bundle id.
 */
public final class CouldNotLoadBundleIdCarrotContextError : CarrotContextError()

/**
 * Could not load AI categorisation model
 */
public final class FailedToLoadModelCarrotContextError : CarrotContextError()

/**
 * Invalid encryption initialisation vector, it should be of 16 characters long.
 */
public final class InvalidInitialisationVectorCarrotContextError : CarrotContextError()

/**
 * There was no one [Budget] found. Zen Scores can be produced if there is at least one [Budget] created.
 */
public final class ZenScoreNoBudgetsCarrotContextError : CarrotContextError()

/**
 * Invalid encryption key, it is too short.
 */
public final class TooShortKeyCarrotContextError : CarrotContextError()

/**
 * The module customer tries to use is not licensed.
 */
public final class NotAuthorisedCarrotContextError : CarrotContextError()

/**
 * Could not save user's personalised data.
 */
public final class SaveErrorCarrotContextError : CarrotContextError()

/**
 * Could not load user's personalised data.
 */
public final class LoadErrorCarrotContextError : CarrotContextError()

/**
 * Undefined Error.
 */
public final class UndefinedCarrotContextError : CarrotContextError()

/**
 * Invalid Encryption key - not set.
 */
public final class EncryptionKeyNotSetCarrotContextError : CarrotContextError()

/**
 * Invalid encryption initialisation vector, not set.
 */
public final class EncryptionInvalidInitialisationVectorError : CarrotContextError()

/**
 * Entity being saved into the persistent storage has an empty id. Entity ids should be managed by the customer application.
 *
 * Please set the entity id and try again.
 */
public final class EmptyEntityIdCarrotContextError : CarrotContextError()

/**
 *  Carrot Context class. Them main entry point into the Framework.
 *
 * # Note:
 * Warning: `CarrotContext` class is not thread safe.
*/
public final class CarrotContext {
    private var _model: MasterModel? = null
    private var _persistence = PersistenceType.persistent
    private var _isAuthenticated = false
    private var _credentials: CarrotCredentials? = null
    private var _appContext: Context? = null

    private var _categoriserAPI: CategorisationAPI? = null
    private var _transactionAPI: TransactionAPI? = null
    private var _budgetAPI: BudgetAPI? = null
    private var _zenAPI: ZenAPI? = null

    public constructor() {
        _persistence = PersistenceType.persistent
    }

    internal constructor(persistence: PersistenceType) {
        _persistence = persistence
    }

    /**
     * Applies library license. Throws an Error if the license is invalid.
     *
     * @param license license key
     * @param appContext application context
     *
     * @throws [InvalidLicenseCarrotContextError] if license is invalid
     * @throws [LicenseHasExpiredCarrotContextError] if license has expired
     * @throws [CouldNotLoadBundleIdCarrotContextError] if application bundle id did not load
     * @throws [WrongBundleIdCarrotContextError] if the library is licensed for another application
     * @throws [FailedToLoadModelCarrotContextError] if model can't load
     */
    @Throws
    public fun setLicense(license: String, appContext: Context) {
        _appContext = appContext

        // Android specific, Credentials object tries to parse JSON and throws JSONException
        // if it fails
        try {
            _credentials = CarrotCredentials(license)
        } catch (e:JSONException) {
            throw InvalidLicenseCarrotContextError()
        }

        AndroidThreeTen.init(appContext)
        NotificationCenter.init(appContext)

        validateCredentials(_credentials!!, appContext)
    }

    /**
     * Applies library license. Throws an Error if the license is invalid.
     *
     * @param license license key
     * @param appContext application context
     * @param encryptionKey Encryption Key
     * @param encryptionIV Encryption Initialisation Vector, must be of length 16
     *
     * @throws [InvalidLicenseCarrotContextError] if license is invalid
     * @throws [LicenseHasExpiredCarrotContextError] if license has expired
     * @throws [CouldNotLoadBundleIdCarrotContextError] if application bundle id did not load
     * @throws [WrongBundleIdCarrotContextError] if the library is licensed for another application
     * @throws [FailedToLoadModelCarrotContextError] if model can't load
     */
    @Throws
    public fun setLicense(license: String, appContext: Context, encryptionKey: String, encryptionIV: String) {
        setEncryption(encryptionKey, encryptionIV)
        setLicense(license, appContext)
    }

    @Throws
    internal fun validateCredentials(credentials: CarrotCredentials, appContext: Context) {
        _isAuthenticated = false

        try {
            credentials.validateSignature()

            val licenseDecoder = LicenseDecoder(credentials.licenseKey!!)

            if (!licenseDecoder.checkPlatform()) {
                throw InvalidLicenseCarrotContextError()
            }

            val bundleId = appContext.packageName
            if (!licenseDecoder.checkBundleId(bundleId)) {
                throw WrongBundleIdCarrotContextError()
            }

            if (!licenseDecoder.checkExpirationDate()) {
                throw LicenseHasExpiredCarrotContextError()
            }

            if (!licenseDecoder.checkModules(listOf(LicenseDecoder.MODULE_FULL, LicenseDecoder.MODULE_CATEGORISE))) {
                throw InvalidLicenseCarrotContextError()
            }

            // TODO: extract version and check it

            val tflWeightTensor = licenseDecoder.extractTensorFlowLiteParameters()

            // Android specific, required for the files locatiton
            PersistentStorage._filesDir = appContext.filesDir

            _model = MasterModel(
                _persistence,
                tflWeightTensor
            )

            if (_model == null) {
                throw FailedToLoadModelCarrotContextError()
            }

            _isAuthenticated = true
        } catch (e: Exception) {
            _isAuthenticated = false
            _model = null
            throw e
        }
    }

    /**
     * Set Encryption parameters. The encryption parameters are required for storing data in the persistent storage. If the encryption keys were not set, then all persistent related methods will `throw`
     *
     * @param key encryptions key
     * @param iv Inititalisation vector, must be of length 16
     */
    public fun setEncryption(key: String, iv: String) {
        SharedContext.key = key
        SharedContext.iv = iv
    }

    /**
     * Provides the [CategorisationAPI].
     * [CarrotContext] validates the license and instantiates the object if license is valid, otherwise it throws an error.
     *
     * @throws
     * @return [CategorisationAPI]
     */
    @Throws
    public fun getCategorisationAPI() : CategorisationAPI {
        if (_categoriserAPI == null) {
            _categoriserAPI = CategorisationAPI(_model, _isAuthenticated, _persistence, _credentials)
        }

        return _categoriserAPI!!
    }

    /**
     * Provides the [TransactionAPI].
     * [CarrotContext] validates the license and instantiates the object if license is valid, otherwise it throws an error.
     *
     * @throws
     * @return [TransactionAPI]
     */
    @Throws
    public fun getTransactionAPI() : TransactionAPI {
        if (_transactionAPI == null) {
            _transactionAPI = TransactionAPI(getCategorisationAPI(), _credentials, _appContext!!)
        }

        return _transactionAPI!!
    }

    /**
     * Provides the [BudgetAPI]
     * [CarrotContext] validates the license and instantiates the object if license is valid, otherwise it throws an error.
     *
     * @throws
     * @return [BudgetAPI]
     */
    @Throws
    public fun getBudgetAPI() : BudgetAPI {
        if (_budgetAPI == null) {
            _budgetAPI = BudgetAPI(getTransactionAPI(), _credentials, _appContext!!)
        }

        return _budgetAPI!!
    }

    /**
     * Provides the Zen Score Calculation API
     * [CarrotContext] validates the license and instantiates the object if license is valid, otherwise it throws an error.
     *
     * @throws
     * @return [ZenAPI]
     */
    @Throws
    public fun getZenAPI() : ZenAPI {
        if (_zenAPI == null) {
            _zenAPI = ZenAPI(getTransactionAPI(),  getBudgetAPI(), _credentials)
        }

        return _zenAPI!!
    }
}