package com.laquysoft.wordsearchai

import android.util.Log
import java.util.ArrayList
import java.util.HashSet

class WordSearchLinear {
    private var result: MutableSet<String> = HashSet()

    private val directions = listOf(
        Pair(0, 1),
        Pair(0, -1),
        Pair(1, 0),
        Pair(-1, 0),
        Pair(1, 1),
        Pair(-1, -1),
        Pair(1, -1),
        Pair(-1, 1)
    )

    fun findWords(board: Array<CharArray>, words: List<String>): List<String> {

        val trie = Trie()
        words.forEach { trie.insert(it) }

        val visited = board.map { chars -> BooleanArray(chars.size) }.toTypedArray()

        board.forEachIndexed { rowIndex, row ->
            row.indices.forEach { charIndex ->
                directions.forEach {
                    dfs(board, visited, "", rowIndex, charIndex, trie, it)
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
        dfs(board, visited, str, i + direction.first, j + direction.second, trie, direction)
        visited[i][j] = false
    }

}

