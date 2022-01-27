package com.carrotlabs.smartcarrotframework.utils

internal class StringSanitizer {
    companion object {
        internal fun cleanseString(arg: String): String {
            var result = arg.toLowerCase()

            result = "(\\s+)?&(\\s+)?".toRegex().replace(result, "xqzptx")
            result = "[0-9]+".toRegex().replace(result, "")
            result = "\\.".toRegex().replace(result," ")
            result = "ü".toRegex().replace(result, "ue")
            result = "ä".toRegex().replace(result, "ae")
            result = "ö".toRegex().replace(result, "oe")
            result = "é|è|ê|ë".toRegex().replace(result, "e")
            result = "à|â".toRegex().replace(result, "a")
            result = "ô".toRegex().replace(result, "o")
            result = "û".toRegex().replace(result, "u")
            result = "ï|î".toRegex().replace(result, "i")
            result = "ç".toRegex().replace(result, "c")
            result = "\\.|-|_|\\s+".toRegex().replace(result, " ")
            result = "\\|".toRegex().replace(result, "")

            // this should leave only ASCII chars in the string
            result ="[^a-z| ]".toRegex().replace(result, "")

            while (result != " [a-z]{1} | [a-z]{1}$|^[a-z]{1} ".toRegex().replace(result, " ")) {
                result = " [a-z]{1} | [a-z]{1}$|^[a-z]{1} ".toRegex().replace(result, " ")
            }

            result = "\\s+".toRegex().replace(result, " ")
            result = "^\\s+|\\s+$".toRegex().replace(result, "")

            result = result.trim()

            return result
        }
    }
}