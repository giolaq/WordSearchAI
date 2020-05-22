package com.laquysoft.wordsearchai.textrecognizer

import android.graphics.Bitmap
import com.huawei.hms.mlsdk.MLAnalyzerFactory
import com.huawei.hms.mlsdk.common.MLFrame

class HMSDocumentTextRecognizer : DocumentTextRecognizer {

    //private val detector = MLAnalyzerFactory.getInstance().remoteDocumentAnalyzer
    private val detector = MLAnalyzerFactory.getInstance().localTextAnalyzer

    override fun processImage(
        bitmap: Bitmap,
        success: (Document) -> Unit,
        error: (String?) -> Unit
    ) {
        val hmsFrame = MLFrame.fromBitmap(bitmap)
        detector.asyncAnalyseFrame(hmsFrame)
            .addOnSuccessListener { mlDocument ->
                if (mlDocument != null) {
                    val words = mlDocument.blocks
                        .flatMap { it.contents }
                        .flatMap { it.contents }

                    val symbols: MutableList<Symbol> = emptyList<Symbol>().toMutableList()

                    words.forEach {
                        val rect = it.border
                        it.stringValue.forEachIndexed { idx, value ->
                            symbols.add(Symbol(
                                value.toString(),
                                rect,
                                idx,
                                it.stringValue.length
                            ))
                        }
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
