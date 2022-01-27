package com.carrotlabs.smartcarrotframework


import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert

import org.junit.Test
import org.junit.runner.RunWith
import java.lang.Exception

// com.carrotlabs.smartcarrotframework.test
@RunWith(AndroidJUnit4::class)
internal class CarrotContextTest {
    @Test(expected = InvalidLicenseCarrotContextError::class)
    fun testSetAppId_EmptyCredentials_Fail() {
        val context = CarrotContext()
        val license = ""

        context.setLicense(license, getContext())
    }

    @Test(expected = InvalidLicenseCarrotContextError::class)
    fun testSetAppId_EmptyCredentials2_Fail() {
        val context = CarrotContext()
        val license = "{ licenseKey = '', signature = 'lgkjldfnalsdkjflaksd' }"

        context.setLicense(license, getContext())
    }

    @Test(expected = InvalidLicenseCarrotContextError::class)
    fun testSetAppId_CorruptCredentials2_Fail() {
        val context = CarrotContext()
        val license = "{ licenseKey = ' signature = 'lgkjldfnalsdkjflaksd' }"

        context.setLicense(license, getContext())
    }

    @Test(expected = WrongBundleIdCarrotContextError::class)
    fun testSetAppId_WrongBundleId_Fail() {
        val context = CarrotContext()
        val license = GlobalsTests.LICENSE_WRONG_BUNDLE

        context.setLicense(license, getContext())
    }

    @Test
    fun testSetAppId_ValidCredentials_Pass() {
        val context = CarrotContext()
        val license = GlobalsTests.LICENSE_VALID_FULL

        // it should not throw
        context.setLicense(license, getContext())
    }

    @Test(expected = InvalidLicenseCarrotContextError::class)
    fun testSetAppId_InvalidSignature_Fail() {
        val context = CarrotContext()
        val license = GlobalsTests.LICENSE_INVALID_SIGNATURE

        // it should throw
        context.setLicense(license, getContext())
    }

    @Test
    fun testLicenseDoesNotHaveModule() {
        val context = CarrotContext()
        val license = GlobalsTests.LICENSE_CATEGORISE_ONLY

        context.setLicense(license, getContext())

        var api : Any? = null
        val categoriseAPI = context.getCategorisationAPI()
        Assert.assertNotNull(categoriseAPI)

        try {
            api = context.getTransactionAPI()
        } catch (e: Exception) {

        }

        try {
            api = context.getBudgetAPI()
        } catch (e: Exception) {

        }

        try {
            api = context.getZenAPI()
        } catch (e: Exception) {

        }

        Assert.assertNull(api)
    }

    fun getContext() : Context {
        return InstrumentationRegistry.getInstrumentation().targetContext
    }
}
