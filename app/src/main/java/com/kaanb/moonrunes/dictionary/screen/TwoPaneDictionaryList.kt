package com.kaanb.moonrunes.dictionary.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kaanb.moonrunes.R
import com.kaanb.moonrunes.dictionary.dao.DictionaryDatabaseEntry
import com.kaanb.moonrunes.dictionary.ui.dictionary_entry.BriefDictionaryEntry
import com.kaanb.moonrunes.dictionary.ui.dictionary_entry.DetailedDictionaryEntry
import com.kaanb.moonrunes.dictionary.usecase.GetDictionaryEntryByIdUseCase
import com.kaanb.moonrunes.dictionary.util.DictionaryEntry
import com.kaanb.moonrunes.dictionary.util.formatDictionaryEntry
import com.kaanb.moonrunes.dictionary.viewmodel.DictionarySearchViewModel
import com.kaanb.moonrunes.navigation.Route
import com.kaanb.moonrunes.ui.theme.MoonRunesTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun DictionarySearchScreen(
    modifier: Modifier = Modifier,
    innerPadding: PaddingValues,
    results: List<DictionaryEntry>,
    getEntryById: (Long) -> DictionaryEntry,
    textFieldState: TextFieldState,
    search: (String) -> Unit
) {
    val navigator = rememberListDetailPaneScaffoldNavigator<Long>()
    val scope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val searchBarState = rememberSearchBarState()

    NavigableListDetailPaneScaffold(
        navigator = navigator,
        listPane = {
            AnimatedPane() {
                Scaffold(
                    modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection).fillMaxSize(),
                    topBar = {
                        val focus = LocalFocusManager.current
                        LaunchedEffect(Unit) { focus.clearFocus() }
                        TopAppBar(
                            windowInsets = WindowInsets(0, 0, 0, 0),
                            title = {
                                SearchBar(
                                    modifier = Modifier
                                        .fillMaxWidth()

                                        .windowInsetsPadding(WindowInsets.statusBars)
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
                            },
                            scrollBehavior = scrollBehavior
                        )
                    },
                    content = { innerPadding ->
                        ListPane(
                            innerPadding = innerPadding,
                            items = results,
                            onItemClick = { id ->
                                scope.launch {
                                    navigator.navigateTo(
                                        pane = ListDetailPaneScaffoldRole.Detail,
                                        contentKey = id
                                    )
                                }
                            },
                            selectedItem = navigator.currentDestination?.contentKey
                        )
                    })
            }


        },

        detailPane = {
            val id = navigator.currentDestination?.contentKey
            AnimatedPane() {

                if (id != null) {
                    val entry = getEntryById(id ?: 0)


                    DetailedDictionaryEntry(
                        modifier = Modifier.padding(top = 8.dp),
                        entry = entry
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
                isSelected = entry.entryId == selectedItem
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DictionarySearchScreen(
    modifier: Modifier = Modifier,
    innerPadding: PaddingValues,
    viewModel: DictionarySearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DictionarySearchScreen(
        modifier = modifier,
        innerPadding = innerPadding,
        results = uiState.entries,
        getEntryById = viewModel::getEntryById,
        textFieldState = viewModel.textFieldState,
        search = viewModel::search
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
