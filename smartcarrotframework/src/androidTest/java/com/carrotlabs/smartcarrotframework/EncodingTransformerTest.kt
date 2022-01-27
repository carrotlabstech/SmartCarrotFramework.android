package com.carrotlabs.smartcarrotframework

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.carrotlabs.smartcarrotframework.utils.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*
import org.junit.Before
import java.math.BigDecimal

@RunWith(AndroidJUnit4::class)
internal class EncodingTransformerTest {

    @Before
    internal fun resetSharedContext() {
        SharedContext.key = ""
        SharedContext.iv = ""
    }

    @Test
    internal fun testEncode() {
        SharedContext.key = "this is a test key"
        SharedContext.iv = "1234567890123456"

        val str = "test s$%^&*tring"

        val encoded = str.aesEncrypt()
        assertNotEquals(encoded, str)
    }

    @Test
    internal fun testEncodeDecodeString() {
        SharedContext.key = "this is a test key"
        SharedContext.iv = "1234567890123456"

        val str = "test s$%^&*tring // transaction movement"

        val encoded = str.aesEncrypt()
        val decoded = encoded.aesDecrypt()

        assertEquals(decoded, str)
    }

    @Test(expected = InvalidInitialisationVectorCarrotContextError::class)
    internal fun testTooShortIV() {
        SharedContext.key = "this is a test key"
        SharedContext.iv = "tooshort"

        val str = "text to encode"

        val encoded = str.aesEncrypt()
        assertNotEquals(encoded, str)
    }

    @Test(expected = InvalidInitialisationVectorCarrotContextError::class)
    internal fun testTooLongIV() {
        SharedContext.key = "this is a test key"
        SharedContext.iv = "toolonglonglonglonglonglong"

        val str = "text to encode"

        val encoded = str.aesEncrypt()
        assertNotEquals(encoded, str)
    }

    @Test
    internal fun testStrToInt() {
        SharedContext.key = "this is a test key"
        SharedContext.iv = "1234567890123456"

        val intVal = 560
        val resVal = intVal.toString().aesEncrypt().aesDecryptInt()

        assertEquals(resVal, intVal)
    }

    @Test
    internal fun testStrToBigDecimal() {
        SharedContext.key = "this is a test key"
        SharedContext.iv = "1234567890123456"

        val intVal = BigDecimal(560.20)
        val resVal = intVal.toString().aesEncrypt().aesDecryptBigDecimal()

        assertEquals(resVal, intVal)
    }
}