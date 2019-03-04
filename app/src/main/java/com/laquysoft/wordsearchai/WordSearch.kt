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

        board.forEachIndexed { rowIndex, row ->
            row.indices.forEach { charIndex ->
                dfs(board, visited, "", rowIndex, charIndex, trie)
            }
        }
        return ArrayList(result)
    }

    private fun dfs(board: Array<CharArray>, visited: Array<BooleanArray>, str: String, i: Int, j: Int, trie: Trie) {
        if (i < 0 || j < 0) return

        var str = str
        val m = board.size

        if (i >= m) return
        val n = board[i].size

        if (j >= n) return

        if (visited[i][j])
            return

        str += board[i][j]

        if (!trie.startsWith(str))
            return

        if (trie.search(str)) {
            result.add(str)
            Log.d("WORDSEARCH", "Result $str $i $j")
        }

        visited[i][j] = true
        dfs(board, visited, str, i - 1, j, trie)
        dfs(board, visited, str, i + 1, j, trie)
        dfs(board, visited, str, i, j - 1, trie)
        dfs(board, visited, str, i, j + 1, trie)
        visited[i][j] = false
    }
}

