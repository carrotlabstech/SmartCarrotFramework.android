package com.carrotlabs.smartcarrotframework

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.carrotlabs.smartcarrotframework.models.OptimizedPrimeModel
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OptimizedPrimeModelTest {
    private val model =
        OptimizedPrimeModel(
            1,
            GlobalsTests.WEIGHT_TENSOR
        )

    @Test
    fun testRunCHModel()
    {
        if (BuildConfig.COUNTRY_TARGET == "CH") {
            Assert.assertEquals(80, model.categorise("coop tankstelle", -100.0, CategorisationType.nonPersonal)) // gas
            Assert.assertEquals(24, model.categorise("coop Paradeplatz", -100.0, CategorisationType.nonPersonal)) // groceries
            Assert.assertEquals(54, model.categorise("apple", -100.0, CategorisationType.nonPersonal)) // parent category - shopping
            Assert.assertEquals(54, model.categorise("apple digite", -100.0, CategorisationType.nonPersonal)) // electronics
            Assert.assertEquals(49, model.categorise("INNERT 30 TAGEN, Z RICH LEBENSVERSICH.-, GESELLSCHAFT AG, POLICE: VXXL1X15701FSC900200(&*)60FS, CD, LSV Belastung CHF", -100.0, CategorisationType.nonPersonal)) // life_insurance
            Assert.assertEquals(61, model.categorise("galaxu", -100.0, CategorisationType.nonPersonal)) // oneline shoppping
            Assert.assertEquals(54, model.categorise("galaxu digitec", -100.0, CategorisationType.nonPersonal)) // electronics
            Assert.assertEquals(54, model.categorise("galaxus digitec", -100.0, CategorisationType.nonPersonal)) // electronics
        }
    }

    @Test
    fun testIndividualCHValues() {
        if (BuildConfig.COUNTRY_TARGET == "CH") {
            Assert.assertEquals(model.categorise("coop tankstelle", -100.0, CategorisationType.nonPersonal), 80) // gas
        }
    }

    @Test
    fun testRunUSModel() {
        if (BuildConfig.COUNTRY_TARGET == "US") {
            Assert.assertEquals(model.categorise("gas", -100.0, CategorisationType.nonPersonal), 80) // gas
            Assert.assertEquals(model.categorise("plane", -100.0, CategorisationType.nonPersonal), 91) // gas
            Assert.assertEquals(model.categorise("donald", -100.0, CategorisationType.nonPersonal), 55) // gas
            Assert.assertEquals(model.categorise("donalds", -100.0, CategorisationType.nonPersonal), 55) // gas
            Assert.assertEquals(model.categorise("amazon", -100.0, CategorisationType.nonPersonal), 61) // gas
            Assert.assertEquals(model.categorise("mobile", -100.0, CategorisationType.nonPersonal), 27) // gas
            Assert.assertEquals(model.categorise("air", -100.0, CategorisationType.nonPersonal), 104) // gas

            Assert.assertEquals(model.categorise("amazon.com", -100.0, CategorisationType.nonPersonal), 61) // gas
            Assert.assertEquals(model.categorise("mcdonalds", -100.0, CategorisationType.nonPersonal), 55) // gas
            Assert.assertEquals(model.categorise("WHATABURGER", -100.0, CategorisationType.nonPersonal), 62) // gas
            Assert.assertEquals(model.categorise("STARBUCKS", -100.0, CategorisationType.nonPersonal), 55) // gas
            Assert.assertEquals(model.categorise("WAL-MART", -100.0, CategorisationType.nonPersonal), 104) // gas
            Assert.assertEquals(model.categorise("AMAZON MKTPLACE PMTS", -100.0, CategorisationType.nonPersonal), 61) // gas
            Assert.assertEquals(model.categorise("AMAZON Inc.", -100.0, CategorisationType.nonPersonal), 61) // gas
            Assert.assertEquals(model.categorise("Microsoft Inc.", -100.0, CategorisationType.nonPersonal), 61) // gas
            Assert.assertEquals(model.categorise("AMAZON Inc.", -100.0, CategorisationType.nonPersonal), 61) // gas
            Assert.assertEquals(model.categorise("AMAZON", -100.0, CategorisationType.nonPersonal), 61) // gas
            Assert.assertEquals(model.categorise("Google Inc.", -100.0, CategorisationType.nonPersonal), 61) // gas
            Assert.assertEquals(model.categorise("Google", -100.0, CategorisationType.nonPersonal), 61) // gas

            Assert.assertEquals(model.categorise("HOTEL", -100.0, CategorisationType.nonPersonal), 93) // gas

            Assert.assertEquals(model.categorise("account transfer", -100.0, CategorisationType.nonPersonal), 108) // gas
            Assert.assertEquals(model.categorise("transfer", -100.0, CategorisationType.nonPersonal), 108) // gas
            Assert.assertEquals(model.categorise("account transfer", 100.0, CategorisationType.nonPersonal), 108) // gas
        }
    }

    @Test
    fun testRunModelUSIndividual() {
        if (BuildConfig.COUNTRY_TARGET == "US") {
            Assert.assertEquals(model.categorise("Google",-100.0, CategorisationType.nonPersonal), 61) // gas
        }
    }


    @Test
    fun testIncome() {
        Assert.assertEquals(43, model.categorise("Salary payment", 15000.0, CategorisationType.nonPersonal)) // income / wage
    }

    @Test
    fun testIndividualUSValues() {
        if (BuildConfig.COUNTRY_TARGET == "US") {
            Assert.assertEquals(model.categorise("amazon", -100.0, CategorisationType.nonPersonal), 61)
        }
    }
}