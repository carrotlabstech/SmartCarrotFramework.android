package com.carrotlabs.smartcarrotframework

import com.carrotlabs.smartcarrotframework.utils.CarrotCredentials
import com.carrotlabs.smartcarrotframework.utils.LicenseDecoder

/**
 * @suppress
 */
public open class ModuleAPI internal constructor(modules: List<String>, credentials: CarrotCredentials?) {
    internal var _credentials: CarrotCredentials?  = null
    internal var _modules : ArrayList<String>

    init {
        _credentials = credentials
        _modules = ArrayList<String>()

        _modules.add(LicenseDecoder.MODULE_FULL)
        _modules.addAll(modules)
    }

    @Throws
    internal open fun validateSettings() {
        _credentials!!.validateSignature()
        val licenseDecoder = LicenseDecoder(_credentials!!.licenseKey!!)

        if (!licenseDecoder.checkModules(_modules.toList())) {
            throw InvalidLicenseCarrotContextError()
        }
    }
}