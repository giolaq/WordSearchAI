package com.laquysoft.wordsearchai.textrecognizer

import android.graphics.Bitmap

interface DocumentTextRecognizer {

    suspend fun processImage(bitmap: Bitmap, success: (Document) -> Unit, error: (String?) -> Unit)
}
