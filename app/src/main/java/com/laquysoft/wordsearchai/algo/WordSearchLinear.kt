package com.laquysoft.wordsearchai.algo

import java.util.*

object WordSearchLinear : WordSearch {

    private val directionX = intArrayOf(0, 0, 1, -1, 1, -1, 1, -1)
    private val directionY = intArrayOf(1, -1, 0, 0, 1, -1, -1, 1)

    override fun findWords(board: Array<CharArray>, words: Array<String>): List<String> {
        val res = ArrayList<String>()
        val root = buildTrie(words)
        for (i in board.indices) {
            for (j in board[i].indices) {
                for (d in directionX.indices) {
                    dfs(board, Pair(i, j), root, res, Pair(directionX[d], directionY[d]))
                }
            }
        }
        return res
    }

    private tailrec fun dfs(
        board: Array<CharArray>,
        coordinates: Pair<Int, Int>,
        p: TrieNode,
        res: MutableList<String>,
        directions: Pair<Int, Int>
    ) {

        val (i, j) = coordinates
        if ( i !in board.indices || j !in board[i].indices) return

        var currentNode = p
        val c = board[i][j]

        if (c - 'a' !in 0..25) return

        currentNode.next[c - 'a']?.let { currentNode = it } ?: return

        if (currentNode.word != null) {
            res.add(currentNode.word!!)
            currentNode.word = null
        }

        val nextI = i + directions.first
        val nextJ = j + directions.second
        dfs(board, Pair(nextI, nextJ), currentNode, res, directions)
    }
}

