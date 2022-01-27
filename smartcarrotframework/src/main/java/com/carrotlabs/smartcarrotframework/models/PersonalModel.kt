package com.carrotlabs.smartcarrotframework.models

import com.carrotlabs.smartcarrotframework.*
import com.carrotlabs.smartcarrotframework.utils.StringSanitizer
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.pow
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sqrt

private data class FuzzyWordType (val fuzzyWord: String,
                                  val score: Float,
                                  val originalWord: String)

internal class PersonalModel(val persistence: PersistenceType) :
    ModelProtocol {
    private val LEVENSHTEIN_DISTANCE = 2
    private val FORGETTING_LAMBDA: Float = 0.1f
    private val MIXING_ALPHA: Float = 3f
    private val BOOSTING_BETA: Float = 1.1f

    private val _persistence: PersistenceType

    // todo: replace w private
    internal var _densityMatrixObj: DensityMatrixBuilder
    internal var _densityMatrix: DensityMatrixType
    internal var _wordsDictionary: ArrayList<String>
    internal var _weightsMatrix: ArrayList<ArrayList<Float>>
    internal var _dictionary: ArrayList<ArrayList<Float>>

    init {
        _persistence = persistence

        // copy-past of the load method, swift compiler issue
        val loadObj = PersistentStorage.load(
            persistence
        )

        _densityMatrix = loadObj.densityMatrix
        _densityMatrixObj =
            DensityMatrixBuilder(
                _densityMatrix
            )

        _wordsDictionary = loadObj.wordDictionary
        _weightsMatrix = loadObj.weightsMatix
        _dictionary = loadObj.dictionary
    }

    // needs to hide as they are not used directly
    override fun categorise(
        transactionDescription: String,
        transactionAmount: Double,
        personal: CategorisationType
    ): Int {
        return TransactionCategory.UNCATEGORISED_INT_ID
    }

    override fun getOutputTensor(
        transactionDescription: String,
        transactionAmount: Double
    ): CategorizationOutputTensor {
        var result = FloatArray(TransactionCategory.NUMBER_OF_CATEGORIES)

        val description =
            StringSanitizer.cleanseString(
                transactionDescription
            )
        val words = getWords(description)

        val fuzzyWords = getFuzzyWords(words)

        for (categoryId in 0..TransactionCategory.NUMBER_OF_CATEGORIES - 1) {
            for (fuzzyWord in fuzzyWords) {
                val fuzzyWordIndex = _wordsDictionary.indexOf(fuzzyWord.fuzzyWord)

                result[categoryId] += (_dictionary[fuzzyWordIndex][categoryId] * fuzzyWord.score)
            }

            result[categoryId] = sqrt(result[categoryId])
        }

        val likelihood = _densityMatrixObj.getLikelihood(transactionAmount.toFloat())
        for (i in 0..result.count() - 1) {
            result[i] = result[i] * likelihood[i]
        }

        return result
    }

    override fun learn(
        transactionDescription: String,
        transactionAmount: Double,
        personalCategoryId: Int
    ) {
        val description =
            StringSanitizer.cleanseString(
                transactionDescription
            )
        val words = getWords(description)
        val categoryId = personalCategoryId - 1

        // recalculate category weights
        for (word in words) {
            val wRow = this._wordsDictionary.indexOf(word)
            if (wRow >= 0) {
                val row = wRow
                _weightsMatrix[row] = (_weightsMatrix[row]).map({ it * (1f - FORGETTING_LAMBDA) }) as ArrayList<Float>
                _weightsMatrix[row][categoryId] = _weightsMatrix[row][categoryId] + 1
            } else {
                _wordsDictionary.add(word)

                // Kotlin can't create an empty ArrayList, this is the trick to fix it
                var newWeightsRow : ArrayList<Float> = (MutableList<Float>(TransactionCategory.NUMBER_OF_CATEGORIES) { 0f }) as ArrayList<Float>

                newWeightsRow[categoryId] = 1f
                _weightsMatrix.add(newWeightsRow)
            }
        }

        // category frequence per word
        var categoryFrequencyMatrix = ArrayList<ArrayList<Float>>()
        for (i in 0.._weightsMatrix.count() - 1) {
            // Kotlin can't create an empty ArrayList, this is the trick to fix it
            val categoryFrequencyMatrixRow : ArrayList<Float> = (MutableList<Float>(
                TransactionCategory.NUMBER_OF_CATEGORIES
            ) { 0f }) as ArrayList<Float>
            categoryFrequencyMatrix.add(categoryFrequencyMatrixRow)
        }

        for (iRow in 0.._weightsMatrix.count() - 1) {
            val sumPerWord = _weightsMatrix[iRow].sum()

            if (sumPerWord != 0f) {
                for (iCol in 0.._weightsMatrix[iRow].count() - 1) {
                    categoryFrequencyMatrix[iRow][iCol] = _weightsMatrix[iRow][iCol] / sumPerWord
                }
            } else {
                print("Data Scientist messed up")
            }
        }

        // term frequency per category
        var termFrequencyMatrix = ArrayList<ArrayList<Float>>()
        for (i in 0.._weightsMatrix.count() - 1) {
            // Kotlin can't create an empty ArrayList, this is the trick to fix it
            val termFrequencyMatrixRow : ArrayList<Float> = (MutableList<Float>(TransactionCategory.NUMBER_OF_CATEGORIES) {0f} ) as ArrayList<Float>
            termFrequencyMatrix.add(termFrequencyMatrixRow)
        }

        for (iCol in 0..TransactionCategory.NUMBER_OF_CATEGORIES - 1) {
            var sumPerCategory: Float = 0f
            for (iRow in 0.._weightsMatrix.count() - 1) {
                sumPerCategory += _weightsMatrix[iRow][iCol]
            }

            if (sumPerCategory != 0f) {
                for (iRow in 0.._weightsMatrix.count() - 1) {
                    termFrequencyMatrix[iRow][iCol] = _weightsMatrix[iRow][iCol] / sumPerCategory
                }
            }
        }

        _dictionary = ArrayList<ArrayList<Float>>()
        for (iRow in 0.._weightsMatrix.count() - 1) {
            var dictionaryRow = ArrayList<Float>()

            for (iCol in 0..TransactionCategory.NUMBER_OF_CATEGORIES - 1) {
                val element1 = termFrequencyMatrix[iRow][iCol]
                val element2 = categoryFrequencyMatrix[iRow][iCol]

                val element = (sqrt(element1 * element2.pow(MIXING_ALPHA))) * BOOSTING_BETA

                dictionaryRow.add(element.toFloat())
            }

            _dictionary.add(dictionaryRow)
        }

        _densityMatrixObj.learn(
            transactionAmount,
            TransactionCategory.getCategory(
                personalCategoryId
            )
        )

        save()
    }

    private fun save()
    {
        PersistentStorage._densityMatrix = _densityMatrixObj.DensityMatrix
        PersistentStorage._wordsDictionary = _wordsDictionary
        PersistentStorage._weightsMatrix = _weightsMatrix
        PersistentStorage._dictionary = _dictionary

        try {
            PersistentStorage.save(
                _persistence
            )
        } catch (e: Exception) {
            throw SaveErrorCarrotContextError()
        }
}

    override fun load() {
        val loadObj = PersistentStorage.load(
            _persistence
        )

        _densityMatrix = loadObj.densityMatrix
        _densityMatrixObj =
            DensityMatrixBuilder(
                _densityMatrix
            )

        _wordsDictionary = loadObj.wordDictionary
        _weightsMatrix = loadObj.weightsMatix
        _dictionary = loadObj.dictionary
    }

    private fun getWords(description: String) : ArrayList<String> {
        var words = description.split(" ").distinct().toCollection(ArrayList<String>())

        words.sort()

        return words
    }

    private fun getFuzzyWords(words: ArrayList<String>) : ArrayList<FuzzyWordType> {
        // per each word we take the closest match (all of them), <= 2 distance

        var fuzzyWords = ArrayList<FuzzyWordType>()
        for (word in words) {
            var currentFuzzyWords = ArrayList<FuzzyWordType>()

            for (indexWord in this._wordsDictionary) {
                // no need to calculate and compare the levenshtein distance between words is larger
                if (abs(indexWord.count() - word.count()) <= LEVENSHTEIN_DISTANCE) {
                    val distance = word.levenshtein(indexWord)

                    if (distance <= LEVENSHTEIN_DISTANCE) {
                        val score = getScore(word, indexWord, distance)

                        if (score > 0) {
                            currentFuzzyWords.add(FuzzyWordType(indexWord, score, word))
                        }
                    }
                }
            }

            // filter out the noise via the score value
            // if the score is lower then probably that wasn't the the word we are looking for, let's take it away
            if (currentFuzzyWords.count() > 0) {
                var maxScore = currentFuzzyWords[0]
                for (fuzzyWord in currentFuzzyWords) {
                    if (fuzzyWord.score > maxScore.score) {
                        maxScore = fuzzyWord
                    }
                }

                currentFuzzyWords = currentFuzzyWords.filter { it.score >= maxScore.score } as ArrayList<FuzzyWordType>
                fuzzyWords.addAll(currentFuzzyWords)
            }
        }

        return fuzzyWords
    }

    // score: [0:1], 0 - no match, 1 - exact match
    private fun getScore(word: String, indexWord: String, distance: Int) : Float {
        val length = max(word.length, indexWord.length)

        return (1f-distance.toFloat()/length.toFloat()).pow(2f) * 10f
    }
}