package com.carrotlabs.smartcarrotframework

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.carrotlabs.smartcarrotframework.models.MasterModel
import com.carrotlabs.smartcarrotframework.models.PersistenceType
import com.carrotlabs.smartcarrotframework.models.PersistentStorage
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class MasterModelTest {
    @Test
    fun testRunModel() {
        if (BuildConfig.COUNTRY_TARGET == "CH") {
            val model = MasterModel(PersistenceType.ephemeral, GlobalsTests.WEIGHT_TENSOR)

            Assert.assertEquals(model.categorise("coop tankstelle", -100.0, CategorisationType.nonPersonal), 80) // gas
            Assert.assertEquals(model.categorise("coop Paradeplatz", -100.0, CategorisationType.nonPersonal), 24) // groceries
        }
    }

    @Test
    fun testEmptyTransaction() {
        val model = MasterModel(PersistenceType.ephemeral, GlobalsTests.WEIGHT_TENSOR)

        Assert.assertEquals(model.categorise("", 0.0, CategorisationType.personal), TransactionCategory.UNCATEGORISED.getIntId())
    }

    @Test
    fun testTraining() {
        if (BuildConfig.COUNTRY_TARGET == "US") {
            PersistentStorage.reset()
            val model = MasterModel(PersistenceType.ephemeral, GlobalsTests.WEIGHT_TENSOR)

            val online_shopping = TransactionCategory("online_shopping").getIntId()
            val groceries = TransactionCategory("groceries").getIntId()

            Assert.assertEquals(model.categorise("Amazon", -12.0, CategorisationType.personal), online_shopping)
            Assert.assertEquals(model.categorise("Amazon", -70.0, CategorisationType.personal), online_shopping)

            model.learn("Amazon", -12.0, groceries)

            Assert.assertEquals(model.categorise("Amazon", -12.0, CategorisationType.personal), groceries)
            Assert.assertEquals(model.categorise("Amazon", -70.0, CategorisationType.personal), online_shopping)
        }
    }

    @Test
    fun testLearnAndCategorize() {
        if (BuildConfig.COUNTRY_TARGET == "CH") {
            PersistentStorage.reset()

            val model = MasterModel(PersistenceType.ephemeral, GlobalsTests.WEIGHT_TENSOR)

            val children = TransactionCategory("children").getIntId()
            val traffic_fine = TransactionCategory("traffic_fine").getIntId()
            val alimony = TransactionCategory("alimony").getIntId()
            val groceries = TransactionCategory("groceries").getIntId()
            //let insurance = UInt8(TransactionCategory(id: "insurance", subId: "").getIntId())
            val gas = TransactionCategory("gas").getIntId()
//        val fees_charges = TransactionCategory("fees_tax_charges").getIntId()
            val clothes = TransactionCategory("clothes").getIntId()
            val resto = TransactionCategory("restaurants").getIntId()

            model.learn("Stadt Bülach trx number 401", -30.0, traffic_fine)
            model.learn("Stadt Bülach trx number 400", -1600.0, children)
            model.learn("Binkys stupid money transfer again", -1400.0, alimony)
            model.learn("bp station", -12.0, groceries)

            Assert.assertEquals(model.categorise("Stadt Bülach trx number 400", -1600.0, CategorisationType.personal), children)
            Assert.assertNotEquals(model.categorise("Stadt Bülach trx number 400", -1600.0, CategorisationType.nonPersonal), children)

            Assert.assertEquals(model.categorise("Stadt Bülach trx number 401", -30.0, CategorisationType.personal), traffic_fine)
            Assert.assertNotEquals(model.categorise("Stadt Bülach trx number 401", -30.0, CategorisationType.nonPersonal), traffic_fine)

            Assert.assertEquals(model.categorise("Binkys stupid money transfer again", -1400.0, CategorisationType.personal), alimony)
            Assert.assertNotEquals(model.categorise("Binkys stupid money transfer again", -1400.0, CategorisationType.nonPersonal), alimony)

            Assert.assertEquals(model.categorise("bp station", -12.0, CategorisationType.personal), groceries)
            Assert.assertEquals(model.categorise("bp station", -70.0, CategorisationType.personal), gas)
            Assert.assertEquals(model.categorise("bp station", -70.0, CategorisationType.nonPersonal), gas)
        }
    }
}