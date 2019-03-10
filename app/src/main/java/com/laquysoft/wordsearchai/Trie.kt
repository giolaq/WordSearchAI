package com.laquysoft.wordsearchai

internal class TrieNode {
    var children = arrayOfNulls<TrieNode>(26)
    var item = ""
}

//Trie
class Trie {
    private var root = TrieNode()

    fun insert(word: String) {
        var node = root
        for (c in word.toCharArray()) {
            val index = c - 'a'
            if (index in 0..25) {
                if (node.children[index] == null) {
                    node.children[index] = TrieNode()
                }
                node = node.children[c - 'a']!!
            }
        }
        node.item = word
    }

    fun search(word: String): Boolean {
        var node = root
        for (c in word.toCharArray()) {
            val index = c - 'a'
            if (index in 0..25) {
                if (node.children[index] == null)
                    return false
                node = node.children[index]!!
            }

        }
        return node.item == word
    }

    fun startsWith(prefix: String): Boolean {
        var node = root
        for (c in prefix.toCharArray()) {
            val index = c - 'a'
            if (index in 0..25) {
                if (node.children[index] == null)
                    return false
                node = node.children[index]!!
            }
        }
        return true
    }
}
