package com.laquysoft.wordsearchai

import android.util.Log
import java.util.ArrayList
import java.util.HashSet

class WordSearchLinear {
    private var result: MutableSet<String> = HashSet()

    fun findWords(board: Array<CharArray>, words: List<String>): List<String> {

        val trie = Trie()
        words.forEach { trie.insert(it) }

        val visited = board.map { chars -> BooleanArray(chars.size) }.toTypedArray()

        board.forEachIndexed { rowIndex, row ->
            row.indices.forEach { charIndex ->
                dfsT(board, visited, "", rowIndex, charIndex, trie)
                dfsF(board, visited, "", rowIndex, charIndex, trie)
                dfsD(board, visited, "", rowIndex, charIndex, trie)
                dfsB(board, visited, "", rowIndex, charIndex, trie)
                dfsFU(board, visited, "", rowIndex, charIndex, trie)
                dfsFD(board, visited, "", rowIndex, charIndex, trie)
                dfsBD(board, visited, "", rowIndex, charIndex, trie)
                dfsBT(board, visited, "", rowIndex, charIndex, trie)
            }
        }
        return ArrayList(result)
    }

    private fun dfsF(board: Array<CharArray>, visited: Array<BooleanArray>, str: String, i: Int, j: Int, trie: Trie) {
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
        dfsF(board, visited, str, i, j + 1, trie)
        visited[i][j] = false
    }

    private fun dfsB(board: Array<CharArray>, visited: Array<BooleanArray>, str: String, i: Int, j: Int, trie: Trie) {
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
        dfsB(board, visited, str, i, j - 1, trie)
        visited[i][j] = false
    }

    private fun dfsT(board: Array<CharArray>, visited: Array<BooleanArray>, str: String, i: Int, j: Int, trie: Trie) {
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
        dfsT(board, visited, str, i+1, j, trie)
        visited[i][j] = false
    }

    private fun dfsD(board: Array<CharArray>, visited: Array<BooleanArray>, str: String, i: Int, j: Int, trie: Trie) {
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
        dfsD(board, visited, str, i-1, j, trie)
        visited[i][j] = false
    }


    private fun dfsFU(board: Array<CharArray>, visited: Array<BooleanArray>, str: String, i: Int, j: Int, trie: Trie) {
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
        dfsFU(board, visited, str, i-1, j+1, trie)
        visited[i][j] = false
    }

    private fun dfsFD(board: Array<CharArray>, visited: Array<BooleanArray>, str: String, i: Int, j: Int, trie: Trie) {
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
        dfsFD(board, visited, str, i+1, j+1, trie)
        visited[i][j] = false
    }


    private fun dfsBD(board: Array<CharArray>, visited: Array<BooleanArray>, str: String, i: Int, j: Int, trie: Trie) {
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
        dfsBD(board, visited, str, i+1, j-1, trie)
        visited[i][j] = false
    }

    private fun dfsBT(board: Array<CharArray>, visited: Array<BooleanArray>, str: String, i: Int, j: Int, trie: Trie) {
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
        dfsBT(board, visited, str, i-1, j-1, trie)
        visited[i][j] = false
    }
}

