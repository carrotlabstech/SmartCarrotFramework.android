package com.carrotlabs.smartcarrotframework.APIs

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.runner.RunWith
import com.carrotlabs.smartcarrotframework.*
import com.carrotlabs.smartcarrotframework.GlobalsTests
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

@RunWith(AndroidJUnit4::class)
internal class CategorisationAPITest{
    private var carrotContext = CarrotContext()
    private var categorisaionAPI: CategorisationAPI? = null

    fun getContext() : Context {
        return InstrumentationRegistry.getInstrumentation().targetContext
    }

    @Before
    fun setUp() {
        carrotContext.setEncryption(GlobalsTests.PASSWORD, GlobalsTests.IV)
        carrotContext.setLicense(GlobalsTests.LICENSE_VALID_FULL, getContext())
        categorisaionAPI = carrotContext.getCategorisationAPI()

        categorisaionAPI!!.reset()
    }

    @Test
    fun testCategorisationUS() {
        val n = getContext().packageName

        if (shouldRunUSTests()) {
            Assert.assertEquals(61, categorisaionAPI!!.categorise("Google Inc.", BigDecimal(-100), CategorisationType.nonPersonal))
        }
    }

    @Test
    fun testReset() {
        val someText = "Amazon text here"
        val lowValue = BigDecimal(-10)
        val highValue = BigDecimal(-70)
        val randomPersonalCategory = 88

        val nonPersonalCategoryId = categorisaionAPI!!.categorise(someText, lowValue, CategorisationType.nonPersonal)
        categorisaionAPI!!.learn(someText, lowValue, randomPersonalCategory)

        Assert.assertEquals(randomPersonalCategory, categorisaionAPI!!.categorise(someText, lowValue, CategorisationType.personal))
        Assert.assertNotEquals(randomPersonalCategory, categorisaionAPI!!.categorise(someText, highValue, CategorisationType.personal))

        categorisaionAPI!!.reset()
        Assert.assertEquals(nonPersonalCategoryId, categorisaionAPI!!.categorise(someText, lowValue, CategorisationType.personal))
    }

    internal fun shouldRunUSTests() : Boolean {
        return BuildConfig.COUNTRY_TARGET == "US"
    }
}