package com.sermah.vkapp.ui.utils

import android.text.SpannableStringBuilder
import android.text.SpannedString
import androidx.core.text.buildSpannedString
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TextSpanningTest {

    // Don't write #
    private fun SpannableStringBuilder.spanTag(tag: String) {
        val idx = this.indexOf(tag, 0, false) - 1
        setSpan(
            PostSpan.HashTag(tag),
            idx,
            idx + tag.length,
            SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    private fun SpannableStringBuilder.spanTags(vararg tags: String) {
        tags.forEach { spanTag(it) }
    }

    private fun SpannableStringBuilder.spanInLink(link: String) {
        val idx = this.indexOf(link, 0, false) - 1
        setSpan(
            PostSpan.InternalLink(link),
            idx,
            idx + link.length,
            SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    private fun SpannableStringBuilder.spanInLinks(vararg links: String) {
        links.forEach { spanInLink(it) }
    }

    private fun SpannableStringBuilder.spanXInLink(link: String, text: String) {
        val idx = this.indexOf(text, 0, false)
        setSpan(
            PostSpan.InternalLink(link),
            idx,
            idx + text.length - 1,
            SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    private fun SpannableStringBuilder.spanXInLinks(vararg linkTexts: Pair<String, String>) {
        linkTexts.forEach { (lnk, txt) -> spanXInLink(lnk, txt) }
    }

    private fun assertSpannedMatch(check: SpannedString, expected: SpannedString) {
        assert(check.toString() == expected.toString()) {
            "Strings don't match -\n" +
                "  Expected: '$expected',\n" +
                "  Received: '$check'."
        }

        var i = 0
        while (i < expected.length) {
            val chk = check.nextSpanTransition(i, check.length, PostSpan::class.java)
            val exp = expected.nextSpanTransition(i, expected.length, PostSpan::class.java)

            assert(chk == exp) {
                "Spans don't match (at i = $i - chk = $chk, exp = $exp)."
            }

            val chkSpans = check.getSpans(i, chk, PostSpan::class.java)
            val expSpans = expected.getSpans(i, exp, PostSpan::class.java)

            // it's either 0 or 1 span at each point of string, so we can safely zip and check them
            assert(
                chkSpans.size == expSpans.size &&
                    chkSpans.zip(expSpans).all { it.first == it.second }
            ) {
                "Spans don't match ($i .. $exp) -\n" +
                    "  Expected (${expSpans.size}): ${expSpans.toList()},\n" +
                    "  Received (${chkSpans.size}): ${chkSpans.toList()}."
            }

            i = exp
        }
    }

    @Test
    fun `Hashtags spanning`() {
        // Prep
        val str = "This is a text with #hashtag and # #some_more and #moore+-" +
            " and 88#uu and ##lol and #bawl012 #"
        val exp = buildSpannedString {
            append(str)
            spanTags("hashtag", "some_more", "moore", "uu", "lol", "bawl012")
        }

        // Do
        val res = str.spanWith()

        // Check
        assertSpannedMatch(res, exp)
    }

    @Test
    fun `At links spanning`() {
        // Prep
        val str = "This is a text with @links and @ @some_more and @moore+-" +
            " and 88@uu and @@lol and @bawl012 @"
        val exp = buildSpannedString {
            append(str)
            spanInLinks("links", "some_more", "moore", "uu", "lol", "bawl012")
        }

        // Do
        val res = str.spanWith()

        // Check
        assertSpannedMatch(res, exp)
    }

    @Test
    fun `Extended at links spanning`() {
        // Prep
        val str = "This is a text with @links (link1) and @ @some_more (12) and @moore+-" +
            " and 88@uu and @@lol (beb) and @bawl012() @io(close) @yuki (), @"
        val exp = buildSpannedString {
            append(
                "This is a text with link1 and @ 12 and @moore+-" +
                    " and 88@uu and @beb and @bawl012() close @yuki (), @"
            )
            spanXInLinks(
                "links" to "link1",
                "some_more" to "12",
                "lol" to "beb",
                "io" to "close",
            )
            spanInLinks(
                "moore",
                "uu",
                "bawl012",
                "yuki"
            )
        }

        // Do
        val res = str.spanWith()

        // Check
        assertSpannedMatch(res, exp)
    }

    @Test
    fun `Square links spanning`() {
        // Prep
        val str = "This is a text with [links|link1] and @ [some_more|12] and [moore+-|text]" +
            " and 88[uu|] and @[lolik|beb] and [ bawl012|] [iooooo|close] [yuki|short], [|]"
        val exp = buildSpannedString {
            append(
                "This is a text with link1 and @ 12 and [moore+-|text]" +
                    " and 88[uu|] and @beb and [ bawl012|] close [yuki|short], [|]"
            )
            spanXInLinks(
                "links" to "link1",
                "some_more" to "12",
                "lolik" to "beb",
                "iooooo" to "close",
            )
        }

        // Do
        val res = str.spanWith()

        // Check
        assertSpannedMatch(res, exp)
    }
}