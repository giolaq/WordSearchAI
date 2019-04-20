package com.laquysoft.wordsearchai.algo

import java.util.*

class WordSearchLinear {

    private val directionX = intArrayOf(0, 0, 1, -1, 1, -1, 1, -1)
    private val directionY = intArrayOf(1, -1, 0, 0, 1, -1, -1, 1)

    fun findWords(board: Array<CharArray>, words: Array<String>): List<String> {
        val res = ArrayList<String>()
        val root = buildTrie(words)
        for (i in board.indices) {
            for (j in 0 until board[0].size) {
                for (d in 0 until directionX.size) {
                    val visited = board.map { chars -> BooleanArray(chars.size) }.toTypedArray()
                    dfs(board, visited, i, j, root, res, Pair(directionX[d], directionY[d]))
                }
            }
        }
        return res
    }

    private fun dfs(
        board: Array<CharArray>,
        visited: Array<BooleanArray>,
        i: Int,
        j: Int,
        p: TrieNode,
        res: MutableList<String>,
        direction: Pair<Int, Int>
    ) {

        if (i < 0 || j < 0 || i >= board.size || j >= board[i].size || visited[i][j]) return

        var p = p
        val c = board[i][j]
        if (c - 'a' !in 0..25) return
        if (p.next[c - 'a'] == null) return
        p = p.next[c - 'a']!!
        if (p.word != null) {   // found one
            res.add(p.word!!)
            p.word = null     // de-duplicate
        }

        visited[i][j] = true
        dfs(board, visited, i + direction.first, j + direction.second, p, res, direction)
        visited[i][j] = false
    }
}

