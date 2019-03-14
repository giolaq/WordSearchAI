package com.laquysoft.wordsearchai

import android.util.Log
import java.util.ArrayList
import java.util.HashSet

class WordSearch {
    private var result: MutableSet<String> = HashSet()

    fun findWords(board: Array<CharArray>, words: List<String>): List<String> {

        val trie = Trie()
        words.forEach { trie.insert(it) }

        val visited = board.map { chars -> BooleanArray(chars.size) }.toTypedArray()

        for (i in 0..board.size) {
            for (j in 0..board[i].size) {
                dfs(board, visited, "", i, j, trie)
            }
        }
        return ArrayList(result)
    }

    private fun dfs(board: Array<CharArray>, visited: Array<BooleanArray>, str: String, i: Int, j: Int, trie: Trie) {
        if (i < 0 || j < 0) return

        var newStr = str
        val m = board.size

        if (i >= m) return
        val n = board[i].size

        if (j >= n) return

        if (visited[i][j])
            return

        newStr += board[i][j]

        if (!trie.startsWith(newStr))
            return

        if (trie.search(newStr)) {
            result.add(newStr)
            Log.d("WORDSEARCH", "Result $newStr $i $j")
        }

        visited[i][j] = true
        dfs(board, visited, newStr, i - 1, j, trie)
        dfs(board, visited, newStr, i + 1, j, trie)
        dfs(board, visited, newStr, i, j - 1, trie)
        dfs(board, visited, newStr, i, j + 1, trie)
        visited[i][j] = false
    }
}

