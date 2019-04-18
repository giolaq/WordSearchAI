package com.laquysoft.wordsearchai.algo

internal class TrieNode {
    var children = arrayOfNulls<TrieNode>(26)
    var item = ""
}

//Trie
class Trie {
    private var root = TrieNode()

    fun insert(word: String) {
        var node = root
        word.forEach {
            val index = it - 'a'
            if (index in 0..25) {
                if (node.children[index] == null) {
                    node.children[index] = TrieNode()
                }
                node = node.children[it - 'a']!!
            }
        }
        node.item = word
    }

    fun search(word: String): Boolean {
        var node = root
        word.forEach {
            val index = it - 'a'
            if (index in 0..25) {
                node.children[index]?.let { childrenNode ->
                    node = childrenNode
                } ?: return false
            }

        }
        return node.item == word
    }

    fun startsWith(prefix: String): Boolean {
        var node = root
        prefix.forEach {
            val index = it - 'a'
            if (index in 0..25) {
                node.children[index]?.let { childrenNode ->
                    node = childrenNode
                } ?: return false
            }
        }
        return true
    }
}
