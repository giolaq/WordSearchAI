package com.laquysoft.wordsearchai

import android.util.Log
import java.util.ArrayList
import java.util.HashSet

class WordSearchLinear {
    private var result: MutableSet<String> = HashSet()

    private val directionX = intArrayOf(0, 0, 1, -1, 1, -1, 1, -1)
    private val directionY = intArrayOf(1, -1, 0, 0, 1, -1, -1, 1)

    fun findWords(board: Array<CharArray>, words: List<String>): List<String> {

        val trie = Trie()
        words.forEach { trie.insert(it) }

        val visited = board.map { chars -> BooleanArray(chars.size) }.toTypedArray()

        for ( i in 0 until board.size) {
            for (j in 0 until board[i].size) {
                for(d in 0 until directionX.size) {
                    dfs(board, visited, "", i, j, trie, Pair(directionX[d], directionY[d]))
                }
            }
        }

        return ArrayList(result)
    }

    private fun dfs(
        board: Array<CharArray>,
        visited: Array<BooleanArray>,
        str: String,
        i: Int,
        j: Int,
        trie: Trie,
        direction: Pair<Int, Int>
    ) {
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
        dfs(board, visited, newStr, i + direction.first, j + direction.second, trie, direction)
        visited[i][j] = false
    }

}

