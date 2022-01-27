package com.carrotlabs.smartcarrotframework.models

internal class ComplexEnglishInputTensorBuilder {
    private val ASCII_ACHAR_NUMBER:Int = 97
    private val X = intArrayOf(22,26,14,11,12,27,7,10,15,23,3,9,17,18,20,24,16,1,25,21,19,13,4,8,2,5,6)
    private val Y = intArrayOf(20,26,18,13,16,25,8,14,12,27,3,9,21,15,10,19,17,1,11,24,22,23,4,5,2,7,6)
    private val Z = intArrayOf(25,27,18,21,11,23,12,10,14,15,4,6,24,13,8,16,26,1,22,20,19,17,9,7,2,3,5)

    internal fun build(description: String) : FloatArray {
        val triplets = getTriplets(description)

        var result = FloatArray(27 * 27 * 27 - 2) { 0f }
        for (triplet in triplets) {
            val index = getTripletIndex(triplet = triplet)
            if (index >= 0) {
                result[index] = 1f
            }
        }

        // add decoy indexes
        for (decoyIndex in getDecoyTripletIndexes(description)) {
            result[decoyIndex] = 1f
        }

        return result
    }

    internal fun getTriplets(description: String) : Array<String> {
        return getTripletsWithDuplicates(description).distinct().toTypedArray()
    }

    internal fun getTripletsWithDuplicates(description: String) : Array<String> {
        var result: MutableList<String> = ArrayList()

        if (description.count() < 3) {
            result.add(description.padEnd(3, ' '))
        } else {
            for (index in 0..description.count() - 3) {
                result.add(description.substring(index, index + 3))
            }
        }

        return result.toTypedArray()
    }

    fun getDecoyTripletIndexes(description: String) : ArrayList<Int> {
        var result : MutableList<Int> = ArrayList<Int>()
        val magicNumber = getMagicNumber(getTripletsWithDuplicates(description))

        for (xi in 0..9) {
            for (yi in 0..9) {
                for (zi in 0..9) {
                    if ((X[xi] + Y[yi] + Z[zi] + magicNumber) % 10 == 1) {
                        result.add(applyFormula(xi + 1,yi + 1,zi + 1))
                    }
                }
            }
        }

        return result as ArrayList<Int>
    }

    // Accepts duplicates as well!
    internal fun getMagicNumber(triplets: Array<String>) : Int {
        var result = 0

        for (triplet in triplets) {
            result = result + getCharacterIndex(triplet[0], 0) +
            getCharacterIndex(triplet[1], 1) +
            getCharacterIndex(triplet[2], 2)
        }

        return result
    }

    //X=c(  , a, b, c, d,...)
    //X=c(22,26,14,11,12,27,7,10,15,23,3,9,17,18,20,24,16,1,25,21,19,13,4,8,2,5,6),
    //Y=c(20,26,18,13,16,25,8,14,12,27,3,9,21,15,10,19,17,1,11,24,22,23,4,5,2,7,6),
    //Z=c(25,27,18,21,11,23,12,10,14,15,4,6,24,13,8,16,26,1,22,20,19,17,9,7,2,3,5)
    internal fun getTripletIndex(triplet: String) : Int {
        val x = getCharacterIndex(triplet[0], 0)
        val y = getCharacterIndex(triplet[1], 1)
        val z = getCharacterIndex(triplet[2], 2)
        return applyFormula(x, y, z)
    }

    internal fun applyFormula(x: Int, y: Int, z: Int) : Int {
        return x + (( x + y - 1) % 27)*27 + ((x + y + z - 1) % 27)*27*27 - 2 - 1 // last -1 because of the difference in R and Swift, array enumeration
    }

    internal fun getCharacterIndex(char: Char, index: Int) : Int {
        val pickedArray : IntArray

        when (index) {
            0 -> pickedArray = X
            1 -> pickedArray = Y
            2 -> pickedArray = Z
            // should not happen, if it happens then you made something wrong
            else -> pickedArray = X
        }

        var charIndex= 0
        if (char != ' ')  {
            charIndex = char.toInt() - ASCII_ACHAR_NUMBER + 1
        }

        return pickedArray[charIndex]
    }
}
