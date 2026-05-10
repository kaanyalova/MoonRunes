package com.kaanb.moonrunes.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.kaanb.moonrunes.dictionary.screen.DictionarySearchScreen
import com.kaanb.moonrunes.dictionary.screen.FavoritesListScreen
import com.kaanb.moonrunes.dictionary.screen.FlashCardScreen
import com.kaanb.moonrunes.dictionary.screen.KanjiDictionaryEntryScreen
import com.kaanb.moonrunes.dictionary.screen.WordOfTheDayScreen
import com.kaanb.moonrunes.dictionary.viewmodel.KanjiEntryViewModel


enum class SidebarNavigation {
    Dictionary,
    Favorites,
    FlashCards,
    About,
}


fun navigateToSideBarEntry(entry: SidebarNavigation, backStack: NavBackStack<NavKey>) {
    when (entry) {
        SidebarNavigation.Dictionary -> backStack.add(Route.DictionarySearch)
        SidebarNavigation.Favorites -> backStack.add(Route.FavoritesList)
        SidebarNavigation.FlashCards -> backStack.add(Route.FlashCards)
        SidebarNavigation.About -> TODO()
    }
}

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
                    navigateTo = { route -> backStack.add(route) }
                )
            }


            entry<Route.FavoritesList> {
                FavoritesListScreen(
                    innerPadding = innerPadding,
                    navigateToKanji = { literal -> backStack.add(Route.KanjiEntry(literal)) },
                    onRoute = { route -> backStack.add(route) }
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

            entry<Route.FlashCards> {
                FlashCardScreen(
                    innerPadding = innerPadding,
                    onRoute = { route -> backStack.add(route) },
                    goBack = {
                        backStack.removeLastOrNull()
                    })
            }

            entry<Route.Wotd> {
                WordOfTheDayScreen(navigateToKanji = {backStack.add(Route.KanjiEntry(it))})
            }

        },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        )
    )
}