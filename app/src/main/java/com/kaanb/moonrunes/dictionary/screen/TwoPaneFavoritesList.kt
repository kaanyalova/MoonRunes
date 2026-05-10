package com.kaanb.moonrunes.dictionary.screen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import com.kaanb.moonrunes.R
import com.kaanb.moonrunes.dictionary.ui.DrawerSheet
import com.kaanb.moonrunes.dictionary.ui.dictionary_entry.DictionaryEntry.DetailedDictionaryEntry
import com.kaanb.moonrunes.dictionary.util.DictionaryEntry
import com.kaanb.moonrunes.dictionary.viewmodel.TwoPaneDictionaryListFavoritesViewModel
import com.kaanb.moonrunes.navigation.Route
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun FavoritesListScreen(
    modifier: Modifier = Modifier,
    innerPadding: PaddingValues,
    results: List<DictionaryEntry>,
    selectEntry: (Long) -> Unit,
    selectedEntry: DictionaryEntry?,
    navigateToKanji: (String) -> Unit,
    onFavoriteButtonPressed: () -> Unit,
    onRoute: (Route) -> Unit = {}
) {
    val navigator = rememberListDetailPaneScaffoldNavigator<Long>()
    val scope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    NavigableListDetailPaneScaffold(
        navigator = navigator,
        listPane = {
            AnimatedPane() {
                ModalNavigationDrawer(
                    drawerState = drawerState, drawerContent = {
                        DrawerSheet(selectedItem = Route.FavoritesList, onItemClicked = { route ->
                            scope.launch { drawerState.close() }
                            onRoute(route)
                        })
                    }) {
                    Scaffold(
                        modifier = Modifier
                            .nestedScroll(scrollBehavior.nestedScrollConnection)
                            .fillMaxSize(),
                        topBar = {
                            TopAppBar(
                                windowInsets = WindowInsets(0, 0, 0, 0),
                                navigationIcon = {
                                    IconButton(
                                        modifier = Modifier.windowInsetsPadding(
                                            WindowInsets.statusBars
                                        ), onClick = {
                                            scope.launch { drawerState.apply { if (isClosed) open() else close() } }
                                        }) {
                                        Icon(
                                            painterResource(R.drawable.menu_24px),
                                            contentDescription = null
                                        )
                                    }
                                },
                                title = {
                                    Text(
                                        stringResource(R.string.sidebar_name_favorites),
                                        modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)
                                    )
                                },
                                scrollBehavior = scrollBehavior
                            )
                        },
                        content = { innerPadding ->
                            val isExpanded = windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)
                            ListPane(
                                innerPadding = innerPadding, items = results, onItemClick = { id ->
                                    scope.launch {
                                        selectEntry(id)
                                        navigator.navigateTo(
                                            pane = ListDetailPaneScaffoldRole.Detail,
                                            contentKey = id
                                        )
                                    }
                                },
                                selectedItem = selectedEntry?.entryId,
                                isExpanded = isExpanded
                            )
                        },
                    )
                }
            }
        },
        detailPane = {
            AnimatedPane() {
                if (selectedEntry != null) {
                    DetailedDictionaryEntry(
                        modifier = Modifier.padding(top = 8.dp),
                        entry = selectedEntry,
                        navigateToKanji = navigateToKanji,
                        onFavoriteButtonPressed = onFavoriteButtonPressed
                    )
                }
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesListScreen(
    modifier: Modifier = Modifier,
    innerPadding: PaddingValues,
    viewModel: TwoPaneDictionaryListFavoritesViewModel = hiltViewModel(),
    navigateToKanji: (String) -> Unit,
    onRoute: (Route) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    FavoritesListScreen(
        modifier = modifier,
        innerPadding = innerPadding,
        results = uiState.entries,
        selectEntry = viewModel::selectEntry,
        selectedEntry = uiState.selectedEntry,
        navigateToKanji = navigateToKanji,
        onFavoriteButtonPressed = viewModel::onFavoriteButtonPressed,
        onRoute = onRoute,
    )
}

