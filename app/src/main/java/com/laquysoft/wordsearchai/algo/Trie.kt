package com.laquysoft.wordsearchai.algo

class TrieNode(
    var next: Array<TrieNode?> = arrayOfNulls(26),
    var word: String? = null
)


fun buildTrie(words: Array<String>): TrieNode {
    val root = TrieNode()
    for (w in words) {
        var p = root
        for (c in w.toCharArray()) {
            val i = c - 'a'
            if (p.next[i] == null) p.next[i] = TrieNode()
            p = p.next[i]!!
        }
        p.word = w
    }
    return root
}
