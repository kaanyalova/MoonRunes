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
import androidx.hilt.work.HiltWorkerFactory
import androidx.lifecycle.lifecycleScope
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.kaanb.moonrunes.dictionary.screen.DictionarySearchScreen
import com.kaanb.moonrunes.dictionary.util.decompressDictionaries
import com.kaanb.moonrunes.flash_cards.repository.FlashCardRepository
import com.kaanb.moonrunes.flash_cards.worker.FlashCardOptimizerWorker
import com.kaanb.moonrunes.navigation.NavigationRoot
import com.kaanb.moonrunes.navigation.Route
import com.kaanb.moonrunes.ui.theme.MoonRunesTheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    // janky way of loading the rust stuff
    @Inject
    lateinit var flashCardRepository: FlashCardRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        val constraints = Constraints.Builder().setRequiresBatteryNotLow(true).build()
        val optimizeCardsWorker =
            OneTimeWorkRequestBuilder<FlashCardOptimizerWorker>().setConstraints(constraints).build()
        WorkManager.getInstance(this).enqueue(optimizeCardsWorker)

        lifecycleScope.launch {
            flashCardRepository.getDueCards()
        }
        setContent {
            MoonRunesTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val context = LocalContext.current

                    runBlocking {
                        // zstd is fast enough, this happens only once
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

