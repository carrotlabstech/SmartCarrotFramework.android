package com.carrotlabs.smartcarrotframework

import com.carrotlabs.smartcarrotframework.models.ModelProtocol
import com.carrotlabs.smartcarrotframework.models.PersistenceType
import com.carrotlabs.smartcarrotframework.models.PersistentStorage
import com.carrotlabs.smartcarrotframework.utils.CarrotCredentials
import com.carrotlabs.smartcarrotframework.utils.LicenseDecoder
import com.carrotlabs.smartcarrotframework.utils.doubleValue
import java.lang.Exception
import java.math.BigDecimal

/**
 * Allows you to categorise and learn a user’s personalisation setting.
 *
 * The object of the [CategorisationAPI] class cannot be explicitly instantiated, please call [CarrotContext.getCategorisationAPI] for that.
 */
public final class CategorisationAPI : ModuleAPI {
    private var _model: ModelProtocol? = null
    private var _persistence = PersistenceType.persistent
    private var _isAuthenticated: Boolean = false

    internal constructor(
        model: ModelProtocol?,
        isAuthenticated: Boolean,
        persistence: PersistenceType,
        credentials: CarrotCredentials?
    ) : super(listOf(LicenseDecoder.MODULE_CATEGORISE), credentials) {

        _model = model
        _persistence = persistence
        _isAuthenticated = isAuthenticated

        validateSettings()
    }

    /**
     * Categorises a transaction
     *
     * @param description transaction description
     * @param amount transaction amount
     * @param isPersonal if personalisation should be applied. Set it to [CategorisationType.personal] if you want to apply user personalisation settings, and to [CategorisationType.nonPersonal] if you want to use generalised model. Default value is [CategorisationType.personal]`.
     *
     * @throws [NotAuthorisedCarrotContextError] if CategorizationContext has not been authorized
     * @throws [FailedToLoadModelCarrotContextError] if the model didn't load
     *
     * @return Transaction category Id
     */
    @Throws
    public fun categorise(description: String, amount: BigDecimal, isPersonal: CategorisationType = CategorisationType.personal) : Int {
        val model: ModelProtocol = getVerifiedModel()
        return model.categorise(description, amount.doubleValue(), isPersonal)
    }

    /**
     * Learns user's categorisation on a transaction
     *
     * @param description transaction description
     * @param amount transaction amount
     * @param personalCategoryId transaction category id, defined by the user

     * @throws [NotAuthorisedCarrotContextError] if CategorizationContext has not been authorized
     * @throws [FailedToLoadModelCarrotContextError] if the model didn't load
     * @throws [LoadErrorCarrotContextError] if it failed to load user personalised settings.
     * @throws [SaveErrorCarrotContextError] if it failed to save user personalised settings.
     */
    @Throws
    public fun learn(description: String, amount: BigDecimal, personalCategoryId: Int) {
        val model = getVerifiedModel()
        model.learn(description, amount.doubleValue(), personalCategoryId)
    }

    /**
     * Deletes (resets) all user's learnings

     * @throws [NotAuthorisedCarrotContextError] if CategorizationContext has not been authorized
     * @throws [FailedToLoadModelCarrotContextError] if the model didn't load
     * @throws [LoadErrorCarrotContextError] if it failed to load user personalised settings.
     * @throws [SaveErrorCarrotContextError] if it failed to save user personalised settings.
     */
    @Throws
    public fun reset() {
        PersistentStorage.reset()

        try {
            PersistentStorage.save(_persistence)
        } catch (e: Exception) {
            throw SaveErrorCarrotContextError()
        }

        val model = getVerifiedModel()
        model.load()
    }

    @Throws
    private fun getVerifiedModel() : ModelProtocol {
        if (!_isAuthenticated) {
            throw NotAuthorisedCarrotContextError()
        }

        if (_model == null) {
            throw FailedToLoadModelCarrotContextError()
        }
        return _model!!
    }

    @Throws
    internal final override fun validateSettings() {
        super.validateSettings()

        if (!_isAuthenticated) {
            throw NotAuthorisedCarrotContextError()
        }

        if (_model == null) {
            throw FailedToLoadModelCarrotContextError()
        }
    }
}