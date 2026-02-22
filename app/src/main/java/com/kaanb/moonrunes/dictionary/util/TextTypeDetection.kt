package com.kaanb.moonrunes.dictionary.util

import dev.esnault.wanakana.core.Wanakana


enum class TextType {
    Romaji, // Either a definition lookup or japanese word written in romaji
    ContainsKanji,
    HiraganaOrKatakana,
}

fun detectTextType(input: String): TextType {
    if (containsKanji(input)) {
        return TextType.ContainsKanji
    }

    if (Wanakana.isKana(input)) {
        return TextType.HiraganaOrKatakana
    }

    return TextType.Romaji
}

fun containsKanji(input: String): Boolean {
    val tokens = Wanakana.tokenize(input)
    val hasKanji = tokens.any { it -> Wanakana.isKanji(it) }
    return hasKanji
}