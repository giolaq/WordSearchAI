package com.laquysoft.wordsearchai


class CloudDocumentTextRecognitionProcessor {

    fun process(
        text: String,
        dictionary: List<String>
    ): List<String> {
        /**
         * Take firebase text result, convert it to lower case and drop the last line that is
         * always a /n, also remove the spaces
         *
         * Do not consider 2 char length words in our search
         */
        val board = text
            .toLowerCase()
            .lines()
            .dropLast(1)
            .map { it.replace("\\s".toRegex(), "") }

        return WordSearchLinear().findWords(board, dictionary.filter { it.length > 2 })
    }

}
