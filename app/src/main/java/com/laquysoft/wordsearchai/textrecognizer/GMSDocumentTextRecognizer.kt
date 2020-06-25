package com.laquysoft.wordsearchai.textrecognizer

import android.graphics.Bitmap
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import kotlinx.coroutines.tasks.await

class GMSDocumentTextRecognizer : DocumentTextRecognizer {

    private val detector = FirebaseVision.getInstance().onDeviceTextRecognizer

    override suspend fun processImage(
        bitmap: Bitmap,
        success: (Document) -> Unit,
        error: (String?) -> Unit
    ) {
        try {
            val firebaseImage = FirebaseVisionImage.fromBitmap(bitmap)
            val firebaseVisionDocumentText = detector.processImage(firebaseImage).await()
            if (firebaseVisionDocumentText != null) {
                val words = firebaseVisionDocumentText.textBlocks
                    .flatMap { it -> it.lines }
                    .flatMap { it.elements }

                val symbols: MutableList<Symbol> = emptyList<Symbol>().toMutableList()

                words.forEach {
                    val rect = it.boundingBox
                    if (rect != null) {
                        it.text.forEachIndexed { idx, value ->
                            symbols.add(
                                Symbol(
                                    value.toString(),
                                    rect,
                                    idx,
                                    it.text.length
                                )
                            )
                        }
                    }
                }


                val document =
                    Document(
                        firebaseVisionDocumentText.text,
                        firebaseVisionDocumentText.textBlocks.size,
                        symbols
                    )

                success(document)
            }
        } catch (exception: Exception) {
            error(exception.localizedMessage)
        }
    }
}
