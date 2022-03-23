package com.mint.minttracker

import com.mint.minttracker.models.MintLocation
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

fun Double.toUiString(): String {
    return ((this * 100.0).roundToInt() / 100.0).toString()
}

fun Long.msToUiString(): String {
    val sec = ((this / 1000).toInt() % 60).addZero()
    val min = ((this / (1000 * 60) % 60)).toInt().addZero()
    val hr = ((this / (1000 * 60 * 60) % 24)).toInt().addZero()
    return "$hr:$min:$sec"
}

fun Long.msToRecordUiString(): String {
    return SimpleDateFormat("HH'h' mm'\"' ss''", Locale.getDefault())
        .apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        .format(this)
}

fun Long.secToUiString(): String {
    val sec = (this.toInt() % 60).addZero()
    val min = ((this / 60) % 60).toInt().addZero()
    val hr = ((this / (60 * 60) % 24)).toInt().addZero()
    return "$hr:$min:$sec"
}

private fun Int.addZero(): String {
    return if (this < 10) {
        "0$this"
    } else {
        "$this"
    }
}

fun List<MintLocation>.getTotalTimeInMillis(): Long {
    var totalTimeInMillis = 0L
    for (i in this.map { it.segment }.distinct()) {
        val listFilterBySegment = this.filter { it.segment == i }
        if (listFilterBySegment.isNotEmpty()) {
            totalTimeInMillis += listFilterBySegment.last().time - listFilterBySegment.first().time
        }
    }
    return totalTimeInMillis
}
