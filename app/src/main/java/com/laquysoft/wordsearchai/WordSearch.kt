package com.laquysoft.wordsearchai

import android.util.Log
import java.util.ArrayList
import java.util.HashSet

class WordSearch {
    internal var result: MutableSet<String> = HashSet()

    fun findWords(board: Array<CharArray>, words: List<String>): List<String> {
        //HashSet<String> result = new HashSet<String>();

        val trie = Trie()
        for (word in words) {
            trie.insert(word)
        }

        val m = board.size
        val n = board[0].size

        val visited = Array(m) { BooleanArray(n) }

        for (i in 0 until m) {
            for (j in 0 until n) {
                dfs(board, visited, "", i, j, trie)
            }
        }

        return ArrayList(result)
    }

    private fun dfs(board: Array<CharArray>, visited: Array<BooleanArray>, str: String, i: Int, j: Int, trie: Trie) {
        var str = str
        val m = board.size
        val n = board[0].size

        if (i < 0 || j < 0 || i >= m || j >= n) {
            return
        }

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

