package com.laquysoft.wordsearchai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.laquysoft.wordsearchai.textrecognizer.DocumentTextRecognizer

class ViewModelFactory constructor(
    private val resourceProvider: ResourceProvider,
    private val documentTextRecognizer: DocumentTextRecognizer
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(WordSearchAiViewModel::class.java) ->
                    WordSearchAiViewModel(
                        resourceProvider,
                        documentTextRecognizer
                    )
                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}
