package com.carrotlabs.smartcarrotframework

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.carrotlabs.smartcarrotframework.models.ComplexEnglishInputTensorBuilder
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class ComplexEnglishInputTensorBuilderTest {
    private val builder = ComplexEnglishInputTensorBuilder()

    @Test
    fun testGetTripletIndex()
    {
        Assert.assertEquals(builder.getTripletIndex(" aa"), 15139)
        Assert.assertEquals(builder.getTripletIndex(" a "), 13681)
        Assert.assertEquals(builder.getTripletIndex("sos"), 3987)

        Assert.assertTrue(builder.getTripletIndex("xaa") < 0)
        Assert.assertTrue(builder.getTripletIndex("qia") < 0)
        Assert.assertEquals(builder.getTripletIndex("qoa"), 14362)
    }

    @Test
    fun testCharIndex() {
        val triplet = " aa"

        val x = builder.getCharacterIndex(triplet[0], 0)
        val y = builder.getCharacterIndex(triplet[1], 1)
        val z = builder.getCharacterIndex(triplet[2], 2)

        Assert.assertEquals(x, 22)
        Assert.assertEquals(y, 26)
        Assert.assertEquals(z, 27)
    }

    @Test
    fun testMagicNumber() {
        Assert.assertEquals(builder.getMagicNumber(builder.getTripletsWithDuplicates("aaa")), 79)
        Assert.assertEquals(builder.getMagicNumber(arrayOf("aaa")), 79)
        Assert.assertEquals(builder.getMagicNumber(arrayOf("qia")), 55)

        Assert.assertEquals(builder.getMagicNumber(builder.getTripletsWithDuplicates("qia xaa and friends")), 961)

        Assert.assertEquals(builder.getMagicNumber(builder.getTripletsWithDuplicates("cash withdrawal card rb kaiserstuhl on id amount eur charges min eur eur total debit eur rate eurchf")), 5783)
    }

    @Test
    fun testBuild() {
        var inputTensor = builder.build("qia xaa and friends")

        var trueValues = ArrayList<Int>()
        for (i in 0..inputTensor.count() - 1) {
            if (inputTensor[i] == 1f) {
                trueValues.add(i)
            }
        }
        Assert.assertEquals(trueValues.count(), 118)

        inputTensor = builder.build("cash withdrawal card rb kaiserstuhl on id amount eur charges min eur eur total debit eur rate eurchf")
        trueValues = ArrayList<Int>()
        for (i in 0..inputTensor.count() - 1) {
            if (inputTensor[i] == 1f) {
                trueValues.add(i)
            }
        }
        Assert.assertEquals(trueValues.count(), 184)

        inputTensor = builder.build("coop tankstelle")
        trueValues = ArrayList<Int>()
        for (i in 0..inputTensor.count() - 1) {
            if (inputTensor[i] == 1f) {
                trueValues.add(i)
            }
        }
        Assert.assertEquals(trueValues.count(), 113)
    }

    @Test
    fun testBuildUS() {
        val inputTensor = builder.build( "donalds")

        var trueValues = ArrayList<Int>()
        for (i in 0..inputTensor.count() - 1) {
            if (inputTensor[i] == 1f) {
                trueValues.add(i)
            }
        }
        Assert.assertEquals(trueValues.count(), 108)
    }

    fun testTripletsWithDuplicates() {
        val tripletsWithDuplicates = builder.getTripletsWithDuplicates("qia xaa and friends")

//        for (triplet in tripletsWithDuplicates) {
//            print("\(builder.getCharacterIndex(char: triplet[0], index: 0)) \(builder.getCharacterIndex(char: triplet[1], index: 1)) \(builder.getCharacterIndex(char: triplet[2], index: 2))")
//        }

        Assert.assertEquals(tripletsWithDuplicates.count(), 17)
    }

    @Test
    fun testIndexes() {
        val realTriplets = builder.getTriplets("coop tankstelle")
        var realIndexes = realTriplets.map({ builder.getTripletIndex(it) }).toIntArray()

        val expectedIndexes = arrayOf(229, 4658, 8391, 8442,9925,10445,10632,12115,12957,13184,15353,17637, 18700).toIntArray()

        realIndexes.sort()
        Assert.assertArrayEquals(expectedIndexes, realIndexes)
    }

    @Test
    fun testIndexesUS() {
        var realTriplets = builder.getTriplets("gas")
        var realIndexes = realTriplets.map({ builder.getTripletIndex(it) }).toIntArray()

        var expectedIndexes = arrayOf(952).toIntArray()

        realIndexes.sort()
        Assert.assertArrayEquals(expectedIndexes, realIndexes)

        realTriplets = builder.getTriplets("plane")
        realIndexes = realTriplets.map({ builder.getTripletIndex(it) }).toIntArray()

        expectedIndexes = arrayOf(3155, 6817, 17186).toIntArray()

        realIndexes.sort()
        Assert.assertArrayEquals(expectedIndexes, realIndexes)

        realTriplets = builder.getTriplets("amazon")
        realIndexes = realTriplets.map({ builder.getTripletIndex(it) }).toIntArray()

        expectedIndexes = arrayOf(4296, 9851, 14711, 15756).toIntArray()

        realIndexes.sort()
        Assert.assertArrayEquals(expectedIndexes, realIndexes)
    }
}