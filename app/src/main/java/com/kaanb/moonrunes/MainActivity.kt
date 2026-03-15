package com.kaanb.moonrunes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.kaanb.moonrunes.dictionary.screen.DictionarySearchScreen
import com.kaanb.moonrunes.dictionary.util.decompressDictionaries
import com.kaanb.moonrunes.navigation.NavigationRoot
import com.kaanb.moonrunes.navigation.Route
import com.kaanb.moonrunes.ui.theme.MoonRunesTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MoonRunesTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val context = LocalContext.current

                    LaunchedEffect(true) {
                        decompressDictionaries(context)
                    }

                    NavigationRoot(innerPadding)

                    //SomeDictionaryEntries(
                    //    modifier = Modifier.padding(innerPadding)
                    //)
                }
            }
        }


    }

}

