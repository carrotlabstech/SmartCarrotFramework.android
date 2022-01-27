package com.carrotlabs.smartcarrotframework.utils

import android.annotation.SuppressLint
import com.carrotlabs.smartcarrotframework.InvalidLicenseCarrotContextError
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

internal class LicenseDecoder internal constructor(license: String) {
    companion object {
        internal val MODULE_FULL = "full"
        internal val MODULE_CATEGORISE = "categorise"
        internal val MODULE_TRANSACTIONS = "tx"
        internal val MODULE_BUDGETS = "budgets"
        internal val MODULE_ZEN = "zen"

        internal val PLATFORM = "android"
        internal val EXPIRATION_DATE_PART_NO = 2
        internal val MODULES_PART_NO = 3
        internal val TFL_PART_NO = 5
    }

    private var _license : String = ""
    private val _parts : List<String> = listOf("")

    init {
//        val decoded = license.fromBase64()
//        if (decoded == null) {
//            throw InvalidLicenseCarrotContextError()
//        }

//        _parts = this._license.split(" ")
   }

    internal fun checkBundleId(bundleId: String) : Boolean {
        return true
    }

    internal fun checkPlatform() : Boolean {
        return true
    }

    internal fun extractTensorFlowLiteParameters() : IntArray {
        // To be replaced by proper settings
        return intArrayOf(999,999,999,999,999,999)
    }

    internal fun checkModules(modules: List<String>): Boolean {
        return true
    }

    @SuppressLint("SimpleDateFormat")
    internal fun checkExpirationDate() : Boolean {
        return true
    }
}