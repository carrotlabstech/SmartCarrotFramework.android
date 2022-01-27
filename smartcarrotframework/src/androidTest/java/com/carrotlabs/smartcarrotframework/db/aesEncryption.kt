package com.carrotlabs.smartcarrotframework.db

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.carrotlabs.smartcarrotframework.utils.SharedContext
import com.carrotlabs.smartcarrotframework.utils.aesDecrypt
import com.carrotlabs.smartcarrotframework.utils.aesEncrypt
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class aesEncryption {
    @Test
    fun testEncryptDecrypt() {
        val message = "this is a message to be encrypted and decrypted $%^ 😍"

        SharedContext.key = "paAAword"
        SharedContext.iv = "1234567890123456"

        val encoded = message.aesEncrypt()
        val unencode = encoded.aesDecrypt()

        Assert.assertEquals(message, unencode)
    }

    @Test
    fun testWronPassForDecrypt() {
        val message = "this is a message to be encrypted and decrypted $%^ 😍"

        SharedContext.key = "paAAword"
        SharedContext.iv = "1234567890123456"

        val encoded = message.aesEncrypt()

        SharedContext.key = "pa\$\$word"
        var unencode = encoded.aesDecrypt()

        Assert.assertNull(unencode)

        SharedContext.key = "paAAword"
        SharedContext.iv = "12345678901234561234"
        unencode = encoded.aesDecrypt()

        Assert.assertNull(unencode)
    }
}