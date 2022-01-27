package com.carrotlabs.smartcarrotframework.db

import android.database.Cursor
import org.threeten.bp.LocalDateTime
import java.math.BigDecimal

internal fun Cursor.unwrapString(arg: Any?) : String {
    val optional = arg as? String
    if (optional == null) {
        return ""
    } else {
        return optional
    }
}

internal fun Cursor.unwrapBigDecimal(arg: Any?) : BigDecimal {
    val optional = arg as? BigDecimal
    if (optional == null) {
        return BigDecimal.ZERO
    } else {
        return optional
    }
}

internal fun Cursor.unwrapInt(arg: Any?) : Int {
    val optional = arg as? Int
    if (optional == null) {
        return 0
    } else {
        return optional
    }
}

internal fun Cursor.unwrapLocalDateTime(arg: Any?) : LocalDateTime {
    val optional = arg as? LocalDateTime
    if (optional == null) {
        return LocalDateTime.now()
    } else {
        return optional
    }
}