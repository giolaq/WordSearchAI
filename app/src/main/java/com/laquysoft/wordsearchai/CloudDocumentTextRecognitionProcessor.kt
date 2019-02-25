package com.laquysoft.wordsearchai


class CloudDocumentTextRecognitionProcessor {

    fun findWords(
        wordsDetected: List<String>,
        dictionary: List<String>
    ): List<String> {
        val charsToElaborate = wordsDetected.map { it.replace("\\s".toRegex(), "").toCharArray() }
        return WordSearch().findWords(charsToElaborate.toTypedArray(), dictionary)
    }
    companion object {

        private const val TAG = "CloudDocumentTextRecognitionProcessor"
    }
}
