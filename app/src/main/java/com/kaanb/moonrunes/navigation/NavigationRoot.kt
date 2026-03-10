package com.kaanb.moonrunes.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.kaanb.moonrunes.dictionary.screen.DictionaryEntryScreen
import com.kaanb.moonrunes.dictionary.screen.DictionarySearchScreen
import com.kaanb.moonrunes.dictionary.util.DictionaryEntry

@Composable
fun NavigationRoot(innerPadding: PaddingValues) {
    val backStack = rememberNavBackStack(Route.DictionarySearch)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {

            entry<Route.DictionarySearch> {
                DictionarySearchScreen(innerPadding = innerPadding)
            }

            entry<Route.DictionaryEntry> { key ->
                DictionaryEntryScreen(key.id)
            }

        }
    )
}