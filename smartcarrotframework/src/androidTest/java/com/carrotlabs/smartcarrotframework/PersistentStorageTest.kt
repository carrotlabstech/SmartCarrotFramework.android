package com.carrotlabs.smartcarrotframework

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.carrotlabs.smartcarrotframework.models.MasterModel
import com.carrotlabs.smartcarrotframework.models.PersistenceType
import com.carrotlabs.smartcarrotframework.models.PersistentStorage
import org.junit.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class PersistentStorageTest {
    val children = TransactionCategory("children").getIntId()
    val traffic_fine = TransactionCategory("traffic_fine").getIntId()
    val alimony = TransactionCategory("alimony").getIntId()
    val groceries = TransactionCategory("groceries").getIntId()

    @Before
    fun setContext() {
        /////////////////////////////////////////////
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        PersistentStorage._filesDir = appContext.filesDir
        /////////////////////////////////////////////
    }

    @After
    fun tearDown() {
        PersistentStorage.reset()

        PersistentStorage.save(PersistenceType.persistent)
    }

    @Test
    fun testSave() {
        learn()

        val storage = PersistentStorage.load(PersistenceType.persistent)

        Assert.assertTrue(storage.densityMatrix.count() > 0)
        Assert.assertTrue(storage.dictionary.count() > 0)
        Assert.assertTrue(storage.weightsMatix.count() > 0)
        Assert.assertTrue(storage.wordDictionary.count() > 0)
    }

    @Test
    fun testReset() {
        // train model and make sure it takes categorization into account
        learn()

        val model = MasterModel(
            PersistenceType.persistent
        )
        var categoryId = model.categorise("Stadt Bülach trx number 400", -1600.0, CategorisationType.personal)

        Assert.assertEquals(categoryId, children)

        // reset model and make sure personalization has gone
        tearDown()
        model.load()

        categoryId = model.categorise("Stadt Bülach trx number 400", -1600.0, CategorisationType.personal)
        val nonPersonalCategoryId = model.categorise("Stadt Bülach trx number 400", -1600.0, CategorisationType.nonPersonal)

        Assert.assertEquals(nonPersonalCategoryId, categoryId)
        Assert.assertNotEquals(categoryId, children)
    }

    private fun learn() {
        val model = MasterModel(
            PersistenceType.persistent
        )

        model.learn("Stadt Bülach trx number 400", -1600.0, children)
        model.learn("Stadt Bülach trx number 401", -30.0, traffic_fine)
        model.learn("Binkys stupid money transfer again", -1400.0, alimony)
        model.learn("bp station", -12.0, groceries)
    }
}