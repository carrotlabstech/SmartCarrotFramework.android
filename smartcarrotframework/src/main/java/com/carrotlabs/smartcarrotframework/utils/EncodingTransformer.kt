package com.carrotlabs.smartcarrotframework.utils

import com.carrotlabs.smartcarrotframework.InvalidInitialisationVectorCarrotContextError
import com.carrotlabs.smartcarrotframework.TooShortKeyCarrotContextError
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset
import java.math.BigDecimal
import java.security.NoSuchAlgorithmException
import java.security.spec.InvalidKeySpecException
import java.security.spec.KeySpec
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

internal fun String.aesEncrypt() : String {
    return aesEncyption.encrypt(SharedContext.key, SharedContext.iv, this)
}

internal fun String.aesDecrypt() : String? {
    var result: String?
    try {
        result = aesEncyption.decrypt(SharedContext.key, SharedContext.iv, this)
    } catch (e: Exception) {
        result = null
    }

    return result
}

internal fun String.aesDecryptInt() : Int? {
    return this.aesDecrypt()?.toInt()
}

internal fun String.aesDecryptBigDecimal() : BigDecimal? {
    return this.aesDecrypt()?.toBigDecimal()
}

internal fun String.aesDecryptLocalDate() : LocalDateTime? {
    val str = this.aesDecrypt() ?: return LocalDateTime.now()
    return LocalDateTime.ofEpochSecond(str.toLong(), 0, ZoneOffset.UTC)
}


internal object aesEncyption {
    private const val pswdIterations = 10
    private const val keySize = 128
    private const val cypherInstance = "AES/CBC/PKCS7Padding"
    private const val secretKeyInstance = "PBKDF2WithHmacSHA1"
    private const val AESSalt = "simpleSalt"

    @Throws(Exception::class)
    internal fun encrypt(key: String, iv: String, textToEncrypt: String): String {
        if (iv.count() != 16) {
            throw InvalidInitialisationVectorCarrotContextError()
        }

        if (key.count() < 6) {
            throw TooShortKeyCarrotContextError()
        }

        val skeySpec =
            SecretKeySpec(getRaw(key, AESSalt), "AES")
        val cipher: Cipher = Cipher.getInstance(cypherInstance)
        cipher.init(
            Cipher.ENCRYPT_MODE,
            skeySpec,
            IvParameterSpec(iv.toByteArray())
        )
        val encrypted: ByteArray = cipher.doFinal(textToEncrypt.toByteArray())

        // using android.util to support older Android 7, java.util has Base64 only since Android 8
        return android.util.Base64.encodeToString(encrypted, android.util.Base64.DEFAULT)
    }

    @Throws(Exception::class)
    internal fun decrypt(key: String, iv: String, textToDecrypt: String?): String {
        if (iv.count() != 16) {
            throw InvalidInitialisationVectorCarrotContextError()
        }

        val encryted_bytes: ByteArray = android.util.Base64.decode(textToDecrypt, android.util.Base64.DEFAULT)
        val skeySpec = SecretKeySpec(getRaw(key, AESSalt), "AES")
        val cipher: Cipher = Cipher.getInstance(cypherInstance)

        cipher.init(
            Cipher.DECRYPT_MODE,
            skeySpec,
            IvParameterSpec(iv.toByteArray())
        )

        val decrypted: ByteArray = cipher.doFinal(encryted_bytes)
        return String(decrypted, charset("UTF-8"))
    }

    private fun getRaw(plainText: String, salt: String): ByteArray {
        try {
            val factory: SecretKeyFactory = SecretKeyFactory.getInstance(secretKeyInstance)
            val spec: KeySpec = PBEKeySpec(
                plainText.toCharArray(),
                salt.toByteArray(),
                pswdIterations,
                keySize
            )
            return factory.generateSecret(spec).getEncoded()
        } catch (e: InvalidKeySpecException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return ByteArray(0)
    }
}