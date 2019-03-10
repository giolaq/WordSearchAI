package com.laquysoft.wordsearchai


class CloudDocumentTextRecognitionProcessor {

    fun findWords(
        wordsDetected: List<String>,
        dictionary: List<String>
    ): List<String> {
        val charsToElaborate = wordsDetected.map { it.replace("\\s".toRegex(), "").toCharArray() }
        return WordSearchLinear().findWords(charsToElaborate.toTypedArray(), dictionary)
    }

}
