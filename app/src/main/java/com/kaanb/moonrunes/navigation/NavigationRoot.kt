package com.kaanb.moonrunes.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.kaanb.moonrunes.dictionary.screen.DictionarySearchScreen
import com.kaanb.moonrunes.dictionary.screen.KanjiDictionaryEntryScreen
import com.kaanb.moonrunes.dictionary.viewmodel.KanjiEntryViewModel

@Composable
fun NavigationRoot(innerPadding: PaddingValues) {
    val backStack = rememberNavBackStack(Route.DictionarySearch)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<Route.DictionarySearch> {
                DictionarySearchScreen(
                    innerPadding = innerPadding,
                    navigateToKanji = { kanji -> backStack.add(Route.KanjiEntry(kanji)) },
                    // todo: should i put these here, or in the viewmodel?
                )
            }


            entry<Route.KanjiEntry> { key ->
                val viewModel =
                    hiltViewModel<KanjiEntryViewModel, KanjiEntryViewModel.Factory>(
                        creationCallback = { factory ->
                            factory.create(key.literal)
                        })

                KanjiDictionaryEntryScreen(innerPadding = innerPadding, viewModel = viewModel)
            }

        },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        )
    )
}