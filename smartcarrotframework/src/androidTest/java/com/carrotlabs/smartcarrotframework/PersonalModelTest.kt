package com.carrotlabs.smartcarrotframework

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.carrotlabs.smartcarrotframework.models.PersistenceType
import com.carrotlabs.smartcarrotframework.models.PersistentStorage
import com.carrotlabs.smartcarrotframework.models.PersonalModel
import org.junit.Assert

import org.junit.Test
import org.junit.runner.RunWith
import kotlin.math.abs

@RunWith(AndroidJUnit4::class)
internal class PersonalModelTest {
    private val _eps:Float = 0.0000001f

    @Test
    fun testLearn() {
        val model = PersonalModel(
            PersistenceType.ephemeral
        )
        val pets = TransactionCategory("pets").getIntId()

        model.learn("    (The) Quiçk45 & brown föx-juMPed_over  456 a b c99    \t\n the lazy à dogs.?  ", 400.0, pets)

        val words = PersistentStorage._wordsDictionary
        val expectedWords = arrayOf("the", "quickxqzptxbrown", "foex", "jumped", "over", "lazy", "dogs")

        // todo: check for the words array
        //         XCTAssertTrue(words.contains(expectedWords))

        var sum = 0.0f
        var dictionary = PersistentStorage._dictionary

        for (i in 0..dictionary.count() - 1) {
            sum += dictionary[i].sum()
        }

        // XCTAssertGreaterThan failed: ("-0.003068924") is not greater than ("1e-07") - this is the diff with R code
        // assert( abs( sum( n@dictionary) - 6.007900126860295 ) < eps )
        Assert.assertTrue(abs(sum - 2.9103267) < _eps )
    }

    @Test
    fun testLearnOneTx() {
        PersistentStorage.reset()

        val children = TransactionCategory("children").getIntId()
        val traffic_fine = TransactionCategory("traffic_fine").getIntId()
        val model = PersonalModel(
            PersistenceType.ephemeral
        )

        model.learn("Stadt Bülach trx number 400", -1600.0, children)
        model.learn("Stadt Bülach trx number 401", -300.0, traffic_fine)

        val tensor = model.getOutputTensor("Stadt Bülach trx number 400", -1600.0)
    }
}