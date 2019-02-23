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
            if (node.children[c - 'a'] == null) {
                node.children[c - 'a'] = TrieNode()
            }
            node = node.children[c - 'a']!!
        }
        node.item = word
    }

    fun search(word: String): Boolean {
        var node = root
        for (c in word.toCharArray()) {
            if (node.children[c - 'a'] == null)
                return false
            node = node.children[c - 'a']!!
        }
        return node.item == word
    }

    fun startsWith(prefix: String): Boolean {
        var node = root
        for (c in prefix.toCharArray()) {
            if (node.children[c - 'a'] == null)
                return false
            node = node.children[c - 'a']!!
        }
        return true
    }
}
