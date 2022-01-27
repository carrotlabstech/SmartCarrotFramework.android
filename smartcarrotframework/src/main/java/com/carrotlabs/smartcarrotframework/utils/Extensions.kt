package com.carrotlabs.smartcarrotframework.utils

import android.util.Base64
import java.math.BigDecimal

internal fun String.fromBase64() : String? {
    val bytes = Base64.decode(this, Base64.DEFAULT)
    return bytes?.toString(Charsets.UTF_8)
}

internal fun BigDecimal.doubleValue() : Double {
    return this.toDouble()
}

internal fun <T> Iterable<T>.sumByBigDecimal(selector: (T) -> BigDecimal): BigDecimal {
    var sum: BigDecimal = BigDecimal.ZERO
    for (element in this) {
        sum += selector(element)
    }
    return sum
}

internal fun <T> Array<T>.sumByBigDecimal(selector: (T) -> BigDecimal): BigDecimal {
    var sum: BigDecimal = BigDecimal.ZERO
    for (element in this) {
        sum += selector(element)
    }
    return sum
}

    // MARK: - a-z -
internal val String.a: String get() = this + "a"
internal val String.b: String get() = this + "b"
internal val String.c: String get() = this + "c"
internal val String.d: String get() = this + "d"
internal val String.e: String get() = this + "e"
internal val String.f: String get() = this + "f"
internal val String.g: String get() = this + "g"
internal val String.h: String get() = this + "h"
internal val String.i: String get() = this + "i"
internal val String.j: String get() = this + "j"
internal val String.k: String get() = this + "k"
internal val String.l: String get() = this + "l"
internal val String.m: String get() = this + "m"
internal val String.n: String get() = this + "n"
internal val String.o: String get() = this + "o"
internal val String.p: String get() = this + "p"
internal val String.q: String get() = this + "q"
internal val String.r: String get() = this + "r"
internal val String.s: String get() = this + "s"
internal val String.t: String get() = this + "t"
internal val String.u: String get() = this + "u"
internal val String.v: String get() = this + "v"
internal val String.w: String get() = this + "w"
internal val String.x: String get() = this + "x"
internal val String.y: String get() = this + "y"
internal val String.z: String get() = this + "z"

internal val String.AA: String get() = this + "A"
internal val String.BB: String get() = this + "B"
internal val String.CC: String get() = this + "C"
internal val String.DD: String get() = this + "D"
internal val String.EE: String get() = this + "E"
internal val String.FF: String get() = this + "F"
internal val String.GG: String get() = this + "G"
internal val String.HH: String get() = this + "H"
internal val String.II: String get() = this + "I"
internal val String.JJ: String get() = this + "J"
internal val String.KK: String get() = this + "K"
internal val String.LL: String get() = this + "L"
internal val String.MM: String get() = this + "M"
internal val String.NN: String get() = this + "N"
internal val String.OO: String get() = this + "O"
internal val String.PP: String get() = this + "P"
internal val String.QQ: String get() = this + "Q"
internal val String.RR: String get() = this + "R"
internal val String.SS: String get() = this + "S"
internal val String.TT: String get() = this + "T"
internal val String.UU: String get() = this + "U"
internal val String.VV: String get() = this + "V"
internal val String.WW: String get() = this + "W"
internal val String.XX: String get() = this + "X"
internal val String.YY: String get() = this + "Y"
internal val String.ZZ: String get() = this + "Z"


//// MARK: - Numbers -
internal val String._1: String get() = this + "1"
internal val String._2: String get() = this + "2"
internal val String._3: String get() = this + "3"
internal val String._4: String get() = this + "4"
internal val String._5: String get() = this + "5"
internal val String._6: String get() = this + "6"
internal val String._7: String get() = this + "7"
internal val String._8: String get() = this + "8"
internal val String._9: String get() = this + "9"
internal val String._0: String get() = this + "0"

// MARK: - Punctuation -
internal val String.space: String get() = "$this "
internal val String.point: String get() = "$this."
internal val String.dash: String get() = "$this-"
internal val String.comma: String get() = "$this,"
internal val String.semicolon: String get() = "$this;"
internal val String.colon: String get() = "$this:"
internal val String.apostrophe: String get() = "$this'"
internal val String.quotation: String get() = this + "\""
internal val String.plus: String get() = "$this+"
internal val String.equals: String get() = "$this="
internal val String.paren_left: String get() = "$this("
internal val String.paren_right: String get() = "$this)"
internal val String.asterisk: String get() = "$this*"
internal val String.ampersand: String get() = "$this&"
internal val String.caret: String get() = "$this^"
internal val String.percent: String get() = "$this%"
internal val String.`$`: String get() = "$this$"
internal val String.pound: String get() = "$this#"
internal val String.at: String get() = "$this@"
internal val String.exclamation: String get() = "$this!"
internal val String.question_mark: String get() = "$this?"
internal val String.back_slash: String get() = this + "\\"
internal val String.forward_slash: String get() = "$this/"
internal val String.curly_left: String get() = "$this{"
internal val String.curly_right: String get() = "$this}"
internal val String.bracket_left: String get() = "$this["
internal val String.bracket_right: String get() = "$this]"
internal val String.bar: String get() = "$this|"
internal val String.less_than: String get() = "$this<"
internal val String.greater_than: String get() = "$this>"
internal val String.underscore: String get() = this +  "_"

// MARK: - Aliases -
internal val String.dot: String get() = this.point
