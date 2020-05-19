package com.laquysoft.wordsearchai

import android.content.Context
import android.graphics.Bitmap
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.huawei.hms.api.HuaweiApiAvailability
import com.huawei.hms.mlsdk.MLAnalyzerFactory
import com.huawei.hms.mlsdk.common.MLFrame
import com.laquysoft.wordsearchai.overlay.Symbol

enum class ServiceType {
    HUAWEI,
    GOOGLE
}

data class Document(val stringValue: String, val count: Int, val symbols: List<Symbol>)

interface DocumentTextRecognizer {

    fun processImage(bitmap: Bitmap, success: (Document) -> Unit, error: (String?) -> Unit)
}

class GMSDocumentTextRecognizer : DocumentTextRecognizer {

    private val detector = FirebaseVision.getInstance().cloudDocumentTextRecognizer

    override fun processImage(
        bitmap: Bitmap,
        success: (Document) -> Unit,
        error: (String?) -> Unit
    ) {
        val firebaseImage = FirebaseVisionImage.fromBitmap(bitmap)
        detector.processImage(firebaseImage)
            .addOnSuccessListener {
                val symbols = it.blocks
                    .flatMap { it.paragraphs }
                    .flatMap { it.words }
                    .flatMap { it.symbols }
                    .map { Symbol(it.text, it.boundingBox) }

                val document = Document(it.text, it.blocks.size, symbols)
                success(document)
            }
            .addOnFailureListener { error(it.localizedMessage) }
    }
}

class HMSDocumentTextRecognizer : DocumentTextRecognizer {

    private val detector = MLAnalyzerFactory.getInstance().remoteDocumentAnalyzer

    override fun processImage(
        bitmap: Bitmap,
        success: (Document) -> Unit,
        error: (String?) -> Unit
    ) {
        val hmsFrame = MLFrame.fromBitmap(bitmap)
        detector.asyncAnalyseFrame(hmsFrame)
            .addOnSuccessListener {
                val symbols = it.blocks
                    .flatMap { it.sections }
                    .flatMap { it.wordList }
                    .flatMap { it.characterList }
                    .map { Symbol(it.stringValue, it.border) }
                val document = Document(it.stringValue, it.blocks.size, symbols)
                success(document)
            }
            .addOnFailureListener { error(it.localizedMessage) }
    }
}

object DocumentTextRecognizerService {

    /**
     *  Check HMS or GMS is available on device
     */
    private fun getServiceType(context: Context) = when {
        isGooglePlayServicesAvailable(context) -> ServiceType.GOOGLE
        isHuaweiMobileServicesAvailable(context) -> ServiceType.HUAWEI
        else -> ServiceType.GOOGLE
    }

    private fun isGooglePlayServicesAvailable(context: Context): Boolean {
        return GoogleApiAvailability.getInstance()
            .isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS
    }

    private fun isHuaweiMobileServicesAvailable(context: Context): Boolean {
        return HuaweiApiAvailability.getInstance()
            .isHuaweiMobileServicesAvailable(context) == com.huawei.hms.api.ConnectionResult.SUCCESS

    }

    /**
     *  Create Service
     */
    fun create(context: Context): DocumentTextRecognizer {
        val type = getServiceType(context)
        if (type == ServiceType.HUAWEI)
            return HMSDocumentTextRecognizer()
        return GMSDocumentTextRecognizer()
    }
}
