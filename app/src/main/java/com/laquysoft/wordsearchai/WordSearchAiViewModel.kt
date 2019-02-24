package com.laquysoft.wordsearchai

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage

class WordSearchAiViewModel(application: Application) : AndroidViewModel(application) {

    private val cloudDocumentTextRecognitionProcessor = CloudDocumentTextRecognitionProcessor()

    var processedBitmap: MutableLiveData<Bitmap> = MutableLiveData()

    val resultList: MutableLiveData<List<String>> = MutableLiveData()

    private lateinit var dictionary: List<String>

    fun detectDocumentTextIn(bitmap: Bitmap) {

        val detector = FirebaseVision.getInstance()
            .cloudDocumentTextRecognizer

        val firebaseImage = FirebaseVisionImage.fromBitmap(bitmap)

        loadDictionary()

        detector.processImage(firebaseImage)
            .addOnSuccessListener {
                if (it != null) {
                    val result = mutableListOf<CharArray>()
                    if (it.blocks.size == 0) {
                        Toast.makeText(getApplication(), "No Text detected", Toast.LENGTH_LONG).show()
                    }
                    it.blocks.forEach { block ->
                        result.add(block.text.toCharArray())
                        Log.d("Found ", block.text)
                    }
                    val wordsFound = cloudDocumentTextRecognitionProcessor.findWords(result, dictionary)
                    resultList.postValue(wordsFound)
                }
            }
            .addOnFailureListener {
                Toast.makeText(getApplication(), "Error detecting Text $it", Toast.LENGTH_LONG).show()
            }

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
