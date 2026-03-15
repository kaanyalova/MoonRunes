package com.kaanb.moonrunes.dictionary.screen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kaanb.moonrunes.R
import com.kaanb.moonrunes.dictionary.dao.DictionaryDatabaseEntry
import com.kaanb.moonrunes.dictionary.ui.dictionary_entry.BriefDictionaryEntry
import com.kaanb.moonrunes.dictionary.util.DictionaryEntry
import com.kaanb.moonrunes.dictionary.util.formatDictionaryEntry
import com.kaanb.moonrunes.dictionary.viewmodel.DictionarySearchViewModel
import com.kaanb.moonrunes.ui.theme.MoonRunesTheme
import kotlinx.serialization.json.Json


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DictionarySearchScreen(
    modifier: Modifier = Modifier,
    innerPadding: PaddingValues,
    textFieldState: TextFieldState,
    onSearch: (String) -> Unit,
    results: List<DictionaryEntry>,
    onNavigateToEntry: (Long) -> Unit,
) {
    var expanded by rememberSaveable { mutableStateOf(true) }

    SearchBar(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp), inputField = {
        SearchBarDefaults.InputField(query = textFieldState.text.toString(), onQueryChange = {
            textFieldState.edit { replace(0, length, it) }
            onSearch(textFieldState.text.toString())
        }, onSearch = { query: String ->
            onSearch(textFieldState.text.toString())
            expanded = false
        }, expanded = expanded, onExpandedChange = { it -> expanded = it }, placeholder = {
            Text(stringResource(R.string.dictionary_search_bar_placeholder))
        })
    }, expanded = expanded, onExpandedChange = { it: Boolean -> expanded = it }

    ) {
        LazyColumn(
            contentPadding = innerPadding
        ) {
            results.forEach { entry ->
                item {
                    BriefDictionaryEntry(
                        modifier = Modifier.padding(4.dp),
                        entry = entry,
                        onClick = { id -> onNavigateToEntry(id) })
                }
            }
        }
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DictionarySearchScreen(
    modifier: Modifier = Modifier,
    innerPadding: PaddingValues,
    onNavigateToEntry: (Long) -> Unit,
    viewModel: DictionarySearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DictionarySearchScreen(
        innerPadding = innerPadding,
        textFieldState = viewModel.searchInputState,
        onSearch = { it: String -> viewModel.search(it) },
        results = uiState.entries,
        onNavigateToEntry = onNavigateToEntry
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
        DictionarySearchScreen(
            innerPadding = PaddingValues(16.dp),
            textFieldState = rememberTextFieldState("fish"),
            onSearch = {},
            results = listOf(fishForDisplay),
            onNavigateToEntry = {})
    }
}
