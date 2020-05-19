package com.laquysoft.wordsearchai

import android.app.Activity
import com.laquysoft.wordsearchai.textrecognizer.DocumentTextRecognizerService

fun Activity.getViewModelFactory(): ViewModelFactory {
    return ViewModelFactory(ResourceProvider(this), DocumentTextRecognizerService.create(this))
}
