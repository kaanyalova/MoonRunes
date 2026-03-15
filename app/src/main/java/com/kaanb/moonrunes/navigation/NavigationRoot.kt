package com.kaanb.moonrunes.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.kaanb.moonrunes.dictionary.screen.DictionaryEntryScreen
import com.kaanb.moonrunes.dictionary.screen.DictionarySearchScreen
import com.kaanb.moonrunes.dictionary.util.DictionaryEntry
import com.kaanb.moonrunes.dictionary.viewmodel.DictionaryEntryViewModel
import com.kaanb.moonrunes.dictionary.viewmodel.DictionarySearchViewModel

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
                    // todo: should i put these here, or in the viewmodel?
                    onNavigateToEntry = { id ->
                        backStack.add(Route.DictionaryEntry(id))
                    })
            }

            entry<Route.DictionaryEntry> { key ->
                val viewModel =
                    hiltViewModel<DictionaryEntryViewModel, DictionaryEntryViewModel.Factory>(
                        creationCallback = { factory ->
                            factory.create(key.id)
                        })

                DictionaryEntryScreen(innerPadding = innerPadding, viewModel = viewModel)

            }

        },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        )
    )
}