package com.carrotlabs.smartcarrotframework.models

import com.carrotlabs.smartcarrotframework.LoadErrorCarrotContextError
import com.carrotlabs.smartcarrotframework.SaveErrorCarrotContextError
import java.io.*
import java.lang.Exception

internal enum class PersistenceType {
    ephemeral, persistent
}

internal data class PersistentStorageDataClass(val wordDictionary : ArrayList<String>,
                                               val weightsMatix : ArrayList<ArrayList<Float>>,
                                               val densityMatrix : ArrayList<ArrayList<Float>>,
                                               val dictionary : ArrayList<ArrayList<Float>>)

// singlton
internal object PersistentStorage {
    private val URL_CARROT_FRAMEWORK_STORAGE = "v1-data-android.cldat"
    internal var _filesDir : File? = null

    /// The words dictionary, it is connected to the weightsMatrix
    internal var _wordsDictionary = ArrayList<String>()

    /// Weights Matrix, synced with the words dictionary (the same number of rows = words)
    internal var _weightsMatrix = ArrayList<ArrayList<Float>>()

    internal var _densityMatrix = ArrayList<ArrayList<Float>>()

    internal var _dictionary = ArrayList<ArrayList<Float>>()

    internal fun reset() {
        _wordsDictionary = ArrayList<String>()
        _weightsMatrix = ArrayList<ArrayList<Float>>()
        _densityMatrix = ArrayList<ArrayList<Float>>()
        _dictionary = ArrayList<ArrayList<Float>>()
    }

    internal fun save(persistence: PersistenceType) {
        if (persistence == PersistenceType.persistent) {
            var allTogether = listOf<Any>(
                _wordsDictionary,
                _weightsMatrix,
                _densityMatrix,
                _dictionary
            )

            if (_filesDir == null)
                throw SaveErrorCarrotContextError()

            try {
                val storage = File(_filesDir!!, URL_CARROT_FRAMEWORK_STORAGE)
                ObjectOutputStream(FileOutputStream(storage)).use { it -> it.writeObject(allTogether)  }
            } catch (e: Exception) {
                throw SaveErrorCarrotContextError()
            }
        }
    }

    internal fun load(persistence: PersistenceType) : PersistentStorageDataClass {
        if (persistence == PersistenceType.persistent) {
            if (_filesDir == null)
                throw LoadErrorCarrotContextError()

            val storage = File(_filesDir!!, URL_CARROT_FRAMEWORK_STORAGE)
            if (!storage.exists()) {
                reset()
            } else {

                try {
                    var allTogether: List<Any>

                    ObjectInputStream(FileInputStream(storage)).use { it ->
                        val objAllTogether = it.readObject()
                        when (objAllTogether) {
                            is List<*> -> {
                                allTogether = objAllTogether as List<Any>
                                _wordsDictionary = allTogether[0] as ArrayList<String>
                                _weightsMatrix = allTogether[1] as ArrayList<ArrayList<Float>>
                                _densityMatrix = allTogether[2] as ArrayList<ArrayList<Float>>
                                _dictionary = allTogether[3] as ArrayList<ArrayList<Float>>
                            }
                            else -> throw LoadErrorCarrotContextError()
                        }
                    }
                } catch (e: Exception) {
                    throw LoadErrorCarrotContextError()
                }

            }
        }

        return PersistentStorageDataClass(
            _wordsDictionary,
            _weightsMatrix,
            _densityMatrix,
            _dictionary
        )
    }
}