package com.kaanb.moonrunes.dictionary.util

fun getRidOfTheWeirdFormattingOfKunReadings(input: String): String {
    return input.replace("." , "").replace("-", "")
}