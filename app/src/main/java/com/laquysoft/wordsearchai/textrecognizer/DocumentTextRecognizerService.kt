package com.laquysoft.wordsearchai.textrecognizer

import android.content.Context
import android.graphics.Rect
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.huawei.hms.api.HuaweiApiAvailability

enum class ServiceType {
    HUAWEI,
    GOOGLE
}

data class Symbol(
    val text: String?,
    val rect: Rect,
    val idx: Int = 0,
    val length: Int = 0
)

data class Document(val stringValue: String, val count: Int, val symbols: List<Symbol>)

object DocumentTextRecognizerService {

    private fun getServiceType(context: Context) = when {
        isGooglePlayServicesAvailable(
            context
        ) -> ServiceType.GOOGLE
        isHuaweiMobileServicesAvailable(
            context
        ) -> ServiceType.HUAWEI
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

    fun create(context: Context): DocumentTextRecognizer {
        val type =
            getServiceType(
                context
            )
        if (type == ServiceType.HUAWEI)
            return HMSDocumentTextRecognizer()
        return GMSDocumentTextRecognizer()
    }
}
