package com.kaanb.moonrunes.flash_cards.util


fun daysToShortReadableTime(days: Float): String {
    val seconds = (days * 24 * 60 * 60).toLong()
    return when {
        seconds >= 86400 -> "${seconds/86400}d"
        seconds >= 3600 -> "${seconds/3600}h"
        seconds >= 60 -> "${seconds/60}m"
        else -> "${seconds}s"
    }
}
