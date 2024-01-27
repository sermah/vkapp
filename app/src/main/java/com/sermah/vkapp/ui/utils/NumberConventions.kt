package com.sermah.vkapp.ui.utils

/** Makes 1021093 go 1M */
fun displayCount(count: Int): String {
    fun divideByTen(n: Int) = "${n / 10}" +
        if (n % 10 != 0) ",${n % 10}" else ""

//    if (count >= 1_000_000_000_000) return "${divideByTen(count / 100)}T"
    if (count >= 1_000_000_000) return "${divideByTen(count / 100_000_000)}B"
    if (count >= 1_000_000) return "${divideByTen(count / 100_000)}M"
    if (count >= 1_000) return "${divideByTen(count / 100)}K"
    return count.toString()
}