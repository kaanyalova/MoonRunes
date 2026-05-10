package com.kaanb.moonrunes.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Route : NavKey {
    @Serializable
    data class DictionaryEntry(val id: Long) : Route, NavKey


    @Serializable
    data class KanjiEntry(val literal: String): Route, NavKey

    @Serializable
    data object DictionarySearch: Route, NavKey


    @Serializable
    data object FlashCards: Route, NavKey

    @Serializable
    data object FavoritesList: Route, NavKey

    @Serializable
    data object Wotd: Route, NavKey
}