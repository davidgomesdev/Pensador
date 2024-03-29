package me.l3n.bot.discord.pensador.util

import org.jsoup.internal.StringUtil
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode
import org.jsoup.select.NodeTraversor
import org.jsoup.select.NodeVisitor

// Adapted from https://github.com/jhy/jsoup/blob/master/src/main/java/org/jsoup/examples/HtmlToPlainText.java

fun Element.toPlainText(): String {
    val formatter = FormattingVisitor()
    NodeTraversor.traverse(formatter, this) // walk the DOM, and call .head() and .tail() for each node
    return formatter.toString()
}

// the formatting rules, implemented in a breadth-first DOM traverse
private class FormattingVisitor : NodeVisitor {
    private var width = 0
    private val accum = StringBuilder() // holds the accumulated text

    // hit when the node is first seen
    override fun head(node: Node, depth: Int) {
        val name: String = node.nodeName()

        when {
            node is TextNode -> append(node.text()) // TextNodes carry all user-readable text in the DOM.
            name == "li" -> append("\n * ")
            name == "dt" -> append("  ")
            StringUtil.`in`(name,
                "p",
                "h1",
                "h2",
                "h3",
                "h4",
                "h5",
                "tr") -> append("\n")
        }
    }

    // hit when all of the node's children (if any) have been visited
    override fun tail(node: Node, depth: Int) {
        val name: String = node.nodeName()
        if (StringUtil.`in`(name,
                "br",
                "dd",
                "dt",
                "p",
                "h1",
                "h2",
                "h3",
                "h4",
                "h5")
        )
            append("\n")
        else if (name == "a")
            append(java.lang.String.format(" <%s>", node.absUrl("href")))
    }

    // appends text to the string builder with a simple word wrap method
    private fun append(text: String) {
        if (text.startsWith("\n"))
            width = 0 // reset counter if starts with a newline. only from formats above, not in natural text

        if (text == " " &&
            (accum.isEmpty() || StringUtil.`in`(accum.substring(accum.length - 1), " ", "\n"))
        ) return  // don't accumulate long runs of empty spaces

        if (text.length + width > MAX_WIDTH) { // won't fit, needs to wrap
            val words = text.split("\\s+").toTypedArray()

            for (i in words.indices) {
                var word = words[i]
                val last = i == words.size - 1

                if (!last) // insert a space if not the last word
                    word = "$word "

                if (word.length + width > MAX_WIDTH) { // wrap and reset counter
                    accum.append("\n").append(word)
                    width = word.length
                } else {
                    accum.append(word)
                    width += word.length
                }
            }
        } else { // fits as is, without need to wrap text
            accum.append(text)
            width += text.length
        }
    }

    override fun toString(): String {
        return accum.toString()
    }

    companion object {
        private const val MAX_WIDTH = 80
    }
}
