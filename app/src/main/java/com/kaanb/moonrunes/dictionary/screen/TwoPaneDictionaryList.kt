package com.kaanb.moonrunes.dictionary.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import com.kaanb.moonrunes.R
import com.kaanb.moonrunes.dictionary.dao.DictionaryDatabaseEntry
import com.kaanb.moonrunes.dictionary.ui.DrawerSheet
import com.kaanb.moonrunes.dictionary.ui.dictionary_entry.DictionaryEntry.BriefDictionaryEntry
import com.kaanb.moonrunes.dictionary.ui.dictionary_entry.DictionaryEntry.DetailedDictionaryEntry
import com.kaanb.moonrunes.dictionary.util.DictionaryEntry
import com.kaanb.moonrunes.dictionary.util.formatDictionaryEntry
import com.kaanb.moonrunes.dictionary.viewmodel.DictionarySearchViewModel
import com.kaanb.moonrunes.navigation.Route
import com.kaanb.moonrunes.ui.theme.MoonRunesTheme
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun DictionarySearchScreen(
    modifier: Modifier = Modifier,
    innerPadding: PaddingValues, // ??
    results: List<DictionaryEntry>,
    textFieldState: TextFieldState,
    search: (String) -> Unit,
    navigateToKanji: (String) -> Unit,
    selectEntry: (Long) -> Unit,
    selectedEntry: DictionaryEntry?,
    onFavoriteButtonPressed: () -> Unit
) {
    val navigator = rememberListDetailPaneScaffoldNavigator<Long>()
    val scope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val searchBarState = rememberSearchBarState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    NavigableListDetailPaneScaffold(
        navigator = navigator,
        listPane = {
            AnimatedPane() {
                ModalNavigationDrawer(
                    drawerState = drawerState, drawerContent = {
                        DrawerSheet(selectedItem = Route.DictionarySearch, onItemClicked = {})
                    }) {
                    Scaffold(
                        modifier = Modifier
                            .nestedScroll(scrollBehavior.nestedScrollConnection)
                            .fillMaxSize(),
                        topBar = {
                            val focus = LocalFocusManager.current
                            LaunchedEffect(Unit) { focus.clearFocus() }
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
                                    Row(
                                        modifier = modifier.windowInsetsPadding(WindowInsets.statusBars),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        SearchBar(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(end = 12.dp, bottom = 8.dp, top = 8.dp),
                                            state = searchBarState,
                                            inputField = {
                                                SearchBarDefaults.InputField(
                                                    textFieldState = textFieldState,
                                                    searchBarState = searchBarState,
                                                    placeholder = { Text(stringResource(R.string.dictionary_search_bar_placeholder)) },
                                                    onSearch = search,
                                                    prefix = {
                                                        Icon(
                                                            painterResource(R.drawable.search_24px),
                                                            contentDescription = null
                                                        )
                                                    },
                                                )

                                            },

                                            )
                                    }
                                },
                                scrollBehavior = scrollBehavior
                            )
                        },
                        content = { innerPadding ->
                            val isExpanded = windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND);
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

/*
scope.launch {
                        navigator.navigateTo(
                            pane = ListDetailPaneScaffoldRole.Detail,
                            contentKey = id
                        )
                    }
 */
@Composable
fun ListPane(
    modifier: Modifier = Modifier,
    innerPadding: PaddingValues,
    items: List<DictionaryEntry>,
    onItemClick: (id: Long) -> Unit,
    selectedItem: Long?,
    isExpanded : Boolean,
) {
    AnimatedVisibility(
        visible = false
    ) {
        TextField(rememberTextFieldState(), modifier = Modifier.fillMaxWidth())

    }


    LazyColumn(
        contentPadding = innerPadding
    ) {
        items(items) { entry ->
            BriefDictionaryEntry(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                entry = entry,
                onClick = onItemClick,
                // only highlight when on a wider screen
                isSelected = if (isExpanded) entry.entryId == selectedItem else false //
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DictionarySearchScreen(
    modifier: Modifier = Modifier,
    innerPadding: PaddingValues,
    viewModel: DictionarySearchViewModel = hiltViewModel(),
    navigateToKanji: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DictionarySearchScreen(
        modifier = modifier,
        innerPadding = innerPadding,
        results = uiState.entries,
        textFieldState = viewModel.textFieldState,
        search = viewModel::search,
        navigateToKanji = navigateToKanji,
        selectEntry = viewModel::selectEntry,
        selectedEntry = uiState.selectedEntry,
        onFavoriteButtonPressed = viewModel::onFavoriteButtonPressed,
    )

}

@Preview(showBackground = true)
@Composable
private fun DictionarySearchScreenPreview() {
    val fishJson = """
{
  "entry": {
    "id": 1578010
  },
  "kanjiElements": [
    {
      "kanjiElement": {
        "id": 56293,
        "body": "魚",
        "entryFk": 1578010
      },
      "priority": [
        {
          "id": 91084,
          "elementFk": 56293,
          "body": "ichi1"
        },
        {
          "id": 91085,
          "elementFk": 56293,
          "body": "ichi2"
        },
        {
          "id": 91086,
          "elementFk": 56293,
          "body": "news1"
        },
        {
          "id": 91087,
          "elementFk": 56293,
          "body": "nf03"
        }
      ]
    }
  ],
  "readingElements": [
    {
      "readingElement": {
        "id": 66286,
        "body": "さかな",
        "entryFk": 1578010
      },
      "priority": [
        {
          "id": 91088,
          "elementFk": 66286,
          "body": "ichi1"
        },
        {
          "id": 91089,
          "elementFk": 66286,
          "body": "news1"
        },
        {
          "id": 91090,
          "elementFk": 66286,
          "body": "nf03"
        },
        {
          "id": 103150,
          "elementFk": 66286,
          "body": "spec1"
        }
      ]
    },
    {
      "readingElement": {
        "id": 66287,
        "body": "うお",
        "entryFk": 1578010
      },
      "priority": [
        {
          "id": 91091,
          "elementFk": 66287,
          "body": "ichi2"
        }
      ]
    }
  ],
  "senses": [
    {
      "sense": {
        "id": 70985,
        "entryFk": 1578010
      },
      "partsOfSpeech": [
        {
          "id": 101827,
          "senseFk": 70985,
          "body": "&n;"
        }
      ],
      "definitions": [
        {
          "id": 141893,
          "senseFk": 70985,
          "body": "fish"
        }
      ]
    }
  ]
}      
      """.trimIndent()

    val fish = Json.decodeFromString<DictionaryDatabaseEntry>(fishJson)
    val fishForDisplay = formatDictionaryEntry(fish, LocalContext.current)

    MoonRunesTheme {
        //DictionarySearchScreen(
        //    innerPadding = PaddingValues(16.dp),
        //    textFieldState = rememberTextFieldState("fish"),
        //    onSearch = {},
        //    results = listOf(fishForDisplay),
        //    onNavigateToEntry = {})
    }
}
