package com.laquysoft.wordsearchai

import com.laquysoft.wordsearchai.algo.WordSearch
import com.laquysoft.wordsearchai.algo.WordSearchLinear


object CloudDocumentTextRecognitionProcessor : WordSearch by WordSearchLinear {

    fun process(
        text: String,
        dictionary: List<String>
    ): List<String> {
        /**
         * Take firebase text result, convert it to lower case and drop the last line that is
         * always a /n, also remove the spaces
         *
         * Do not consider less than 3 char length words in our search
         */
        val board = text
            .toLowerCase()
            .lines()
            .dropLast(1)
            .map { it.replace("\\s".toRegex(), "").toCharArray() }
            .toTypedArray()

        return findWords(board, dictionary.filter { it.length > 2 }.toTypedArray())
    }

}
