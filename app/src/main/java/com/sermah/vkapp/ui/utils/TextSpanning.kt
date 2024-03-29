package com.sermah.vkapp.ui.utils

import android.text.SpannableStringBuilder
import androidx.core.text.buildSpannedString
import androidx.core.text.isDigitsOnly
import java.net.URL
import java.util.StringTokenizer


private fun Char.isAllowedInShortname() =
    this in 'a'..'z'
        || this in 'A'..'Z'
        || this.isDigit()
        || this == '_'

private val LINK_STARTS = listOf(
    "id", "public", "event", "club",
    // TODO https://vk.com/wall-2158488_881014
)

private fun String.isLegitShortname(): Boolean {
    LINK_STARTS.forEach { start ->
        if (startsWith(start))
            return start.length < this.length &&
                slice(start.length until this.length).let {
                    it.isNotEmpty() && it.isDigitsOnly()
                }
    }

    // https://vk.com/faq18038
    return length >= 5
        && all { it.isAllowedInShortname() || it == '.' }
        && last() != '_' && first() != '_'
        && last() != '.' && first() != '.'
        && !slice(0..2).isDigitsOnly()
        && fold(initial = 0) { acc, ch -> // should be >= 4 symbols after a period
        if (ch == '.') (
            if (acc >= 4) 0
            else Int.MIN_VALUE
            ) else (
            // must be a letter after a period
            if (acc == 0 && !ch.isLetter()) Int.MIN_VALUE
            else acc + 1
            )
    } >= 4

}

// TODO: Replace with regex
fun String.spanWith(
//    urls: Boolean = true, // https://...
//    atLink: Boolean = true, // @user
//    atLinkExtended: Boolean = true, // @user (text)
//    squareBrackets: Boolean = true, // [link|text]
//    hashTags: Boolean = true, //
) = buildSpannedString {
    val str = this@spanWith
    val lines = str.split('\n')

    lines.forEachIndexed { il, line ->
        val tokens = StringTokenizer(line, " [|]()@#", true)
            .toList().map { it as String }
        var idx = 0
        while (idx < tokens.size) {
            val tok = tokens[idx]
            if ((tok == "#" || tok == "@") && idx + 1 < tokens.size) {
                val rawLink = tokens[idx + 1]
                var linkUntil = rawLink.indexOfFirst { !it.isAllowedInShortname() }
                if (linkUntil == 0) append(tok)
                else if (tok == "#") {
                    val spanFrom = length

                    append(tok)
                    append(rawLink)
                    idx++

                    if (linkUntil < 0) linkUntil = rawLink.length

                    setSpan(
                        PostSpan.HashTag(rawLink.substring(0 until linkUntil)),
                        spanFrom,
                        spanFrom + linkUntil + 1,
                        SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                } else if (tok == "@") {
                    var hasText = false
                    if (linkUntil < 0 && idx + 4 < tokens.size) {
                        var iidx = idx + 2
                        // skip whitespace
                        while (iidx < tokens.size && tokens[iidx].isBlank()) iidx++

                        if (iidx < tokens.size && tokens[iidx] == "(") {
                            // inside brackets ()
                            iidx++
                            val textStart = iidx
                            var insideText = false
                            // skip text to )
                            while (iidx < tokens.size && tokens[iidx] != ")") {
                                if (tokens[iidx].isNotBlank()) insideText = true
                                iidx++
                            }
                            val textEnd = iidx - 1
                            // if it's ), not eol
                            // and there must be text inside
                            if (iidx < tokens.size && insideText) {
                                val spanFrom = length
                                tokens.slice(textStart..textEnd)
                                    .forEach { append(it) }
                                idx = iidx // on the ), then idx++ in the end
                                hasText = true

                                setSpan(
                                    PostSpan.InternalLink(rawLink),
                                    spanFrom,
                                    length,
                                    SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE
                                )
                            }
                        }
                    }
                    if (!hasText) {
                        val spanFrom = length

                        append(tok)
                        append(rawLink)
                        idx++

                        if (linkUntil < 0) linkUntil = rawLink.length

                        setSpan(
                            PostSpan.InternalLink(rawLink.substring(0 until linkUntil)),
                            spanFrom,
                            spanFrom + linkUntil + 1,
                            SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    }
                }
            } else if (tok == "[" && idx + 4 < tokens.size) {
                val link = tokens[idx + 1]
                val sep = tokens[idx + 2]
                var success = false
                if (sep == "|" && link.isLegitShortname()) {
                    var iidx = idx + 3
                    val textStart = iidx
                    var insideText = false
                    // skip text to ]
                    while (iidx < tokens.size && tokens[iidx] != "]") {
                        if (tokens[iidx].isNotBlank()) insideText = true
                        iidx++
                    }
                    val textEnd = iidx - 1
                    // if it's ], not eol
                    // and there must be text inside
                    if (iidx < tokens.size && insideText) {
                        val spanFrom = length
                        tokens.slice(textStart..textEnd)
                            .forEach { append(it) }
                        idx = iidx // on the ), then idx++ in the end

                        setSpan(
                            PostSpan.InternalLink(link),
                            spanFrom,
                            length,
                            SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE
                        )

                        success = true
                    }
                }
                if (!success) append(tok)
            } else {
                append(tok)
            }
            idx++
        }
        if (il < lines.size - 1) append("\n")
    }
}

sealed interface PostSpan {
    data class HashTag(
        val value: String,
    ) : PostSpan

    data class InternalLink(
        val to: String,
    ) : PostSpan

    data class ExternalLink(
        val url: URL,
    ) : PostSpan
}
