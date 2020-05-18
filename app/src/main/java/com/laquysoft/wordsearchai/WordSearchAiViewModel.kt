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


class WordSearchAiViewModel(application: Application) : AndroidViewModel(application) {

    val resultList: MutableLiveData<List<String>> = MutableLiveData()
    val resultBoundingBoxes: MutableLiveData<List<FirebaseVisionDocumentText.Symbol>> = MutableLiveData()
    val resultBoundingBoxesHMS: MutableLiveData<List<MLDocument.Character>> = MutableLiveData()

    private lateinit var dictionary: List<String>

    fun detectDocumentTextIn(bitmap: Bitmap) {

        val detector = FirebaseVision.getInstance()
            .cloudDocumentTextRecognizer

        val firebaseImage = FirebaseVisionImage.fromBitmap(bitmap)

        val analyzer = MLAnalyzerFactory.getInstance().remoteDocumentAnalyzer
        val frame = MLFrame.fromBitmap(bitmap)

        loadDictionary()


        val task: Task<MLDocument> = analyzer.asyncAnalyseFrame(frame)
        task.addOnSuccessListener {
            if (it != null) {
                postWordsFoundHMS(it)
                postBoundingBoxesHMS(it)
            }
        } .addOnFailureListener {
            Toast.makeText(getApplication(), "Error detecting Text $it", Toast.LENGTH_LONG).show()
        }

        detector.processImage(firebaseImage)
            .addOnSuccessListener {
                if (it != null) {
                    postWordsFound(it)
                    postBoundingBoxes(it)
                }
            }
            .addOnFailureListener {
                Toast.makeText(getApplication(), "Error detecting Text $it", Toast.LENGTH_LONG).show()
            }

    }

    private fun postBoundingBoxesHMS(mlDocument: MLDocument) {

        val result = mlDocument.blocks
            .flatMap { it.sections }
            .flatMap { it.wordList }
            .flatMap { it.characterList }

        resultBoundingBoxesHMS.postValue(result)
    }

    private fun postWordsFoundHMS(mlDocument: MLDocument) {
        if (mlDocument.blocks.size == 0) {
            Toast.makeText(getApplication(), "No Text detected", Toast.LENGTH_LONG).show()
        }

        val wordsFound = CloudDocumentTextRecognitionProcessor.process(mlDocument.stringValue, dictionary)
        resultList.postValue(wordsFound)
    }


    private fun postBoundingBoxes(text: FirebaseVisionDocumentText) {

        val result = text.blocks
            .flatMap { it.paragraphs }
            .flatMap { it.words }
            .flatMap { it.symbols }

        resultBoundingBoxes.postValue(result)
    }

    private fun postWordsFound(firebaseVisionDocumentText: FirebaseVisionDocumentText) {
        if (firebaseVisionDocumentText.blocks.size == 0) {
            Toast.makeText(getApplication(), "No Text detected", Toast.LENGTH_LONG).show()
        }

        val wordsFound = CloudDocumentTextRecognitionProcessor.process(firebaseVisionDocumentText.text, dictionary)
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
