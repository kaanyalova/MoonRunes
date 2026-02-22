package com.kaanb.moonrunes

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kaanb.moonrunes.dictionary.dao.connectToDictionaryDb
import com.kaanb.moonrunes.dictionary.util.decompressDictionaries
import com.kaanb.moonrunes.dictionary.repository.DictionaryRepository
import com.kaanb.moonrunes.ui.theme.MoonRunesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MoonRunesTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SomeDictionaryEntries(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }

        decompressDictionaries(this)


    }

}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {


    Text(
        text = "Hello $name!",
        modifier = modifier
    )

}


@Composable
fun SomeDictionaryEntries(modifier: Modifier = Modifier) {
    val dao = connectToDictionaryDb(LocalContext.current).dictionaryDao()
    val repo = DictionaryRepository(dao);
    val entries = repo.searchDictionary("fish");

    Column(modifier = Modifier
        .verticalScroll(rememberScrollState())
        .padding(4.dp)) {
        entries.forEach { entry ->
            ElevatedCard(modifier = Modifier.padding(4.dp)) {
                Text(entry.toString())

            }

        }

    }



    Log.d("dict list", entries.toString())
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MoonRunesTheme {
        Greeting("Android")
    }


}