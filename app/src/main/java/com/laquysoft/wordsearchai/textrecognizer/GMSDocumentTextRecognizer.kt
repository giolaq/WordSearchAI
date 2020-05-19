package com.laquysoft.wordsearchai.textrecognizer

import android.graphics.Bitmap
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage

class GMSDocumentTextRecognizer : DocumentTextRecognizer {

    private val detector = FirebaseVision.getInstance().cloudDocumentTextRecognizer

    override fun processImage(
        bitmap: Bitmap,
        success: (Document) -> Unit,
        error: (String?) -> Unit
    ) {
        val firebaseImage = FirebaseVisionImage.fromBitmap(bitmap)
        detector.processImage(firebaseImage)
            .addOnSuccessListener { firebaseVisionDocumentText ->
                val symbols = firebaseVisionDocumentText.blocks
                    .flatMap { it -> it.paragraphs }
                    .flatMap { it.words }
                    .flatMap { it.symbols }
                    .map {
                        Symbol(
                            it.text,
                            it.boundingBox
                        )
                    }

                val document =
                    Document(
                        firebaseVisionDocumentText.text,
                        firebaseVisionDocumentText.blocks.size,
                        symbols
                    )

                success(document)
            }
            .addOnFailureListener { error(it.localizedMessage) }
    }
}
