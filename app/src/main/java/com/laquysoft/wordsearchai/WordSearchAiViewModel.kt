package com.laquysoft.wordsearchai

import android.app.Application
import android.graphics.Bitmap
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentText
import com.huawei.hmf.tasks.Task
import com.huawei.hms.mlsdk.MLAnalyzerFactory
import com.huawei.hms.mlsdk.common.MLFrame
import com.huawei.hms.mlsdk.document.MLDocument
import com.laquysoft.wordsearchai.overlay.Symbol


class WordSearchAiViewModel(application: Application, val recognizer: DocumentTextRecognizer) :
    AndroidViewModel(application) {

    val resultList: MutableLiveData<List<String>> = MutableLiveData()
    val resultBoundingBoxes: MutableLiveData<List<Symbol>> = MutableLiveData()

    private lateinit var dictionary: List<String>

    fun detectDocumentTextIn(bitmap: Bitmap) {

        val service =
            DocumentTextRecognizerService.create(getApplication<Application>().applicationContext)

        service.processImage(bitmap, {
            postWordsFound(it)
            postBoundingBoxes(it)
        },
            {
                Toast.makeText(getApplication(), "Error detecting Text $it", Toast.LENGTH_LONG)
                    .show()
            })
    }


    private fun postBoundingBoxes(document: Document) {
        resultBoundingBoxes.postValue(document.symbols)
    }

    private fun postWordsFound(document: Document) {
        if (document.count == 0) {
            Toast.makeText(getApplication(), "No Text detected", Toast.LENGTH_LONG).show()
        }

        val wordsFound = CloudDocumentTextRecognitionProcessor.process(
            document.stringValue,
            dictionary
        )
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
