package com.laquysoft.wordsearchai.algo

import java.util.*

class WordSearchLinear {
    private var result: MutableSet<String> = HashSet()

    private val directionX = intArrayOf(0, 0, 1, -1, 1, -1, 1, -1)
    private val directionY = intArrayOf(1, -1, 0, 0, 1, -1, -1, 1)

    fun findWords(board: List<String>, words: List<String>): List<String> {

        val trie = Trie()
        words.forEach { trie.insert(it) }


        for ( i in 0 until board.size) {
            for (j in 0 until board[i].length) {
                for(d in 0 until directionX.size) {
                    val visited = board.map { chars -> BooleanArray(chars.length) }.toTypedArray()

                    dfs(board, visited, "", i, j, trie, Pair(directionX[d], directionY[d]))
                }
            }
        }

        return ArrayList(result)
    }

    private fun dfs(
        board: List<String>,
        visited: Array<BooleanArray>,
        str: String,
        i: Int,
        j: Int,
        trie: Trie,
        direction: Pair<Int, Int>
    ) {
        if (i < 0 || j < 0 || i >= board.size || j >= board[i].length || visited[i][j]) return

        val newStr = str + board[i][j]

        if (!trie.startsWith(newStr)) return

        if (trie.search(newStr)) {
            result.add(newStr)
        }

        visited[i][j] = true
        dfs(board, visited, newStr, i + direction.first, j + direction.second, trie, direction)
        visited[i][j] = false
    }

}

