package com.kaanb.moonrunes.dictionary.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.kaanb.moonrunes.dictionary.dao.DictionaryDatabaseEntry
import com.kaanb.moonrunes.dictionary.dao.connectToDictionaryDb
import com.kaanb.moonrunes.dictionary.repository.DictionaryRepository
import com.kaanb.moonrunes.dictionary.util.decompressDictionaries

@Composable
fun Dictionary(modifier: Modifier = Modifier, dictionaryRepository: DictionaryRepository) {
    val searchState = rememberTextFieldState(initialText = "A")
    var entriesData by remember { mutableStateOf<List<DictionaryDatabaseEntry>>(emptyList()) }

    LaunchedEffect(searchState.text) {
        entriesData = dictionaryRepository.searchDictionary(searchState.text.toString())
    }

    Column() {
        // this doesnt like the im
        TextField(
            state = searchState,
            label = { Text("Search for a word") }
        )

        LazyColumn() {
            //items(entriesData) { it ->
            //    DetailedDictionaryEntry(it)
            //}

        }
    }


}

@Preview
@Composable
private fun DictionaryPreview() {
    decompressDictionaries(LocalContext.current)
    val dao = connectToDictionaryDb(LocalContext.current).dictionaryDao()
    val repository = DictionaryRepository(dao)


    Dictionary(dictionaryRepository = repository)
}