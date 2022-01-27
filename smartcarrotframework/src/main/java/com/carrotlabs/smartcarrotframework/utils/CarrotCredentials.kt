package com.carrotlabs.smartcarrotframework.utils

import com.carrotlabs.smartcarrotframework.InvalidLicenseCarrotContextError
import org.json.JSONObject

internal class CarrotCredentials (json: String) : JSONObject(json) {
    internal val licenseKey: String? = this.optString("licenseKey")
    internal val signature: String? = this.optString("signature")

    internal fun isEmpty(): Boolean {
        return signature == null || signature.isEmpty() || licenseKey == null || licenseKey.isEmpty()
    }

    @Throws
    internal fun validateSignature()
    {
        if (isEmpty()) {
            throw InvalidLicenseCarrotContextError()
        }

        if (!DigitalSignature().validate(licenseKey!!, signature!!)) {
            throw InvalidLicenseCarrotContextError()
        }
    }
}