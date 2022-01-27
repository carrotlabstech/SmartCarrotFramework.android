package com.carrotlabs.smartcarrotframework.models

import com.carrotlabs.smartcarrotframework.BuildConfig

internal class ModelContext {
    companion object {
        internal val BUNDLE_IDENTIFIER = "carrotlabs.smartcarrotframework"
        internal val CERTAINTY_THRESHOLD:Float = getThreshold(BuildConfig.COUNTRY_TARGET)

        fun getThreshold(country: String) : Float {
            if (country == "US") {
                return 0.175f
            } else {
                return 0.175f
            }
        }
    }
}