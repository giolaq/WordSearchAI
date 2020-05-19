package com.laquysoft.wordsearchai.textrecognizer

import android.graphics.Bitmap
import com.huawei.hms.mlsdk.MLAnalyzerFactory
import com.huawei.hms.mlsdk.common.MLFrame

class HMSDocumentTextRecognizer : DocumentTextRecognizer {

    private val detector = MLAnalyzerFactory.getInstance().remoteDocumentAnalyzer

    override fun processImage(
        bitmap: Bitmap,
        success: (Document) -> Unit,
        error: (String?) -> Unit
    ) {
        val hmsFrame = MLFrame.fromBitmap(bitmap)
        detector.asyncAnalyseFrame(hmsFrame)
            .addOnSuccessListener { mlDocument ->
                if ( mlDocument != null ) {
                    val symbols = mlDocument.blocks
                        .flatMap { it.sections }
                        .flatMap { it.wordList }
                        .flatMap { it.characterList }
                        .map {
                            Symbol(
                                it.stringValue,
                                it.border
                            )
                        }

                    val document =
                        Document(
                            mlDocument.stringValue,
                            mlDocument.blocks.size,
                            symbols
                        )

                    success(document)
                }
            }
            .addOnFailureListener { error(it.localizedMessage) }
    }
}
