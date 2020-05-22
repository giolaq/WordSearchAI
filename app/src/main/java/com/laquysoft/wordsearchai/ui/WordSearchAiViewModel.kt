package com.laquysoft.wordsearchai.ui

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.laquysoft.wordsearchai.textrecognizer.CloudDocumentTextRecognitionProcessor
import com.laquysoft.wordsearchai.utils.ResourceProvider
import com.laquysoft.wordsearchai.textrecognizer.Document
import com.laquysoft.wordsearchai.textrecognizer.DocumentTextRecognizer
import com.laquysoft.wordsearchai.textrecognizer.Symbol


class WordSearchAiViewModel(
    private val resourceProvider: ResourceProvider,
    private val recognizer: DocumentTextRecognizer
) : ViewModel() {

    val resultList: MutableLiveData<List<String>> = MutableLiveData()
    val resultBoundingBoxes: MutableLiveData<List<Symbol>> = MutableLiveData()

    private lateinit var dictionary: List<String>

    fun detectDocumentTextIn(bitmap: Bitmap) {

        loadDictionary()

        recognizer.processImage(bitmap, {
            postWordsFound(it)
            postBoundingBoxes(it)
        },
            {
                Log.e("WordSearchAIViewModel", it)
            })
    }


    private fun postBoundingBoxes(document: Document) {
        resultBoundingBoxes.postValue(document.symbols)
    }

    private fun postWordsFound(document: Document) {
        val wordsFound =
            CloudDocumentTextRecognitionProcessor.process(
                document.stringValue,
                dictionary
            )
        resultList.postValue(wordsFound)
    }

    private fun loadDictionary() {
        val ins = resourceProvider.getDictionaryStream()
        dictionary = ins.readBytes().toString(Charsets.UTF_8).lines()
    }
}
