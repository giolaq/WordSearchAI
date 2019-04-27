package com.laquysoft.wordsearchai.algo

class TrieNode(
    var next: Array<TrieNode?> = arrayOfNulls(26),
    var word: String? = null
)


fun buildTrie(words: Array<String>): TrieNode {
    val root = TrieNode()
    words.forEach { word ->
        var p = root
        word.toCharArray().forEach {
            val i = it - 'a'
            p.next[i] = p.next[i] ?: TrieNode()
            p = p.next[i]!!
        }
        p.word = word
    }
    return root
}
