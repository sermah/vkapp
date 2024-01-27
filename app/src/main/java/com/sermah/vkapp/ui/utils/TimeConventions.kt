package com.sermah.vkapp.ui.utils

import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

private val dateFormatter: DateTimeFormatter =
    DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)

fun displayTime(unixtime: Int): String = ZonedDateTime.ofInstant(
    Instant.ofEpochSecond(unixtime.toLong()), ZoneId.systemDefault()
).format(dateFormatter)
