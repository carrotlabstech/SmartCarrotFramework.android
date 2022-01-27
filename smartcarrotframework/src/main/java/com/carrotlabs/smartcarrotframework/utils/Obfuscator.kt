package com.carrotlabs.smartcarrotframework.utils

import kotlin.experimental.xor

internal class Obfuscator (
    private var salt: String
){
    /**
    This method obfuscates the string passed in using the salt
    that was used when the Obfuscator was initialized.

    - parameter string: the string to obfuscate

    - returns: the obfuscated string in a byte array
     */
    internal fun bytesByObfuscatingString(string: String) : ByteArray {
        val text = string.toByteArray(Charsets.UTF_8)
        val cipher = this.salt.toByteArray(Charsets.UTF_8)
        val length : Int = cipher.size

        val encrypted = ByteArray(text.size)

        for (t in 0..text.size - 1) {
            encrypted[t] = text[t].xor(cipher[t % length])
        }

        return encrypted
    }

    /**
    This method reveals the original string from the obfuscated
    byte array passed in. The salt must be the same as the one
    used to encrypt it in the first place.

    - parameter key: the byte array to reveal

    - returns: the original string
     */
    internal fun reveal(key: ByteArray) : String {
        val cipher = this.salt.toByteArray(Charsets.UTF_8)
        val length: Int = cipher.size

        val decrypted = ByteArray(key.size)

        for (i in 0..key.size - 1) {
            decrypted[i] = key[i].xor(cipher[i % length])
        }

        return String(decrypted)
    }
}