package com.laquysoft.wordsearchai


class CloudDocumentTextRecognitionProcessor {

    fun findWords(
        wordsDetected: List<CharArray>,
        dictionary: List<String>
    ) = WordSearch().findWords(wordsDetected.toTypedArray(), dictionary)

    companion object {

        private const val TAG = "CloudDocumentTextRecognitionProcessor"
    }
}
