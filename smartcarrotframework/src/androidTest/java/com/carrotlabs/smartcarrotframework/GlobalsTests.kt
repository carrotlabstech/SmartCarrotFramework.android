package com.carrotlabs.smartcarrotframework

internal class GlobalsTests {
    companion object {
        internal val WEIGHT_TENSOR = getWeightTensor()
        internal val LICENSE_VALID_FULL = getFullLicense()

        internal val LICENSE_INVALID_SIGNATURE = ""

        internal val LICENSE_CATEGORISE_ONLY = ""

        internal val LICENSE_WRONG_BUNDLE = ""

        internal val PASSWORD = "paAAword"
        internal val IV = "1234567890123456"

        internal fun getFullLicense() : String {
            val US_FULL_LICENSE = ""

            val CH_FULL_LICENSE = ""

            if (BuildConfig.COUNTRY_TARGET == "US") {
                return US_FULL_LICENSE
            } else {
                return CH_FULL_LICENSE
            }
        }

        internal fun getWeightTensor() : IntArray {
            if (BuildConfig.COUNTRY_TARGET == "US") {
                return intArrayOf(999,999,999,999,999,999)
            } else {
                return intArrayOf(999,999,999,999,999,999)
            }
        }
    }
}