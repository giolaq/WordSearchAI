package com.laquysoft.wordsearchai

import android.app.Application
import android.graphics.Bitmap
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentText

class WordSearchAiViewModel(application: Application) : AndroidViewModel(application) {

    private val cloudDocumentTextRecognitionProcessor = CloudDocumentTextRecognitionProcessor()

    val resultList: MutableLiveData<List<String>> = MutableLiveData()
    val resultBoundingBoxes: MutableLiveData<List<FirebaseVisionDocumentText.Symbol>> = MutableLiveData()

    private lateinit var dictionary: List<String>

    fun detectDocumentTextIn(bitmap: Bitmap) {

        val detector = FirebaseVision.getInstance()
            .cloudDocumentTextRecognizer

        val firebaseImage = FirebaseVisionImage.fromBitmap(bitmap)

        loadDictionary()

        detector.processImage(firebaseImage)
            .addOnSuccessListener {
                if (it != null) {
                    postWordsFounded(it)
                    postBoundingBoxes(it)
                }
            }
            .addOnFailureListener {
                Toast.makeText(getApplication(), "Error detecting Text $it", Toast.LENGTH_LONG).show()
            }

    }

    private fun postBoundingBoxes(text: FirebaseVisionDocumentText) {
        val result = mutableListOf<FirebaseVisionDocumentText.Symbol>()
        val blocks = text.blocks
        for (i in blocks.indices) {
            val paragraphs = blocks[i].paragraphs
            for (j in paragraphs.indices) {
                val words = paragraphs[j].words
                for (l in words.indices) {
                    val symbols = words[l].symbols
                    for (m in symbols.indices) {
                        result.add(symbols[m])
                    }
                }
            }
        }
        resultBoundingBoxes.postValue(result)
    }

    private fun postWordsFounded(firebaseVisionDocumentText: FirebaseVisionDocumentText) {
        if (firebaseVisionDocumentText.blocks.size == 0) {
            Toast.makeText(getApplication(), "No Text detected", Toast.LENGTH_LONG).show()
        }
        //
        val board = firebaseVisionDocumentText.text.toLowerCase().lines().dropLast(1)
        val wordsFound = cloudDocumentTextRecognitionProcessor.findWords(board, dictionary)
        resultList.postValue(wordsFound)
    }

    private fun loadDictionary() {
        val resources = getApplication<Application>().resources
        val dictionaryFileId = resources.getIdentifier(
            "dictionary",
            "raw", getApplication<Application>().packageName
        )

        val ins = resources.openRawResource(dictionaryFileId)

        dictionary = ins.readBytes().toString(Charsets.UTF_8).lines()

    }
}
