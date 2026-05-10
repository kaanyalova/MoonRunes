package com.kaanb.moonrunes.dictionary.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.kaanb.moonrunes.flash_cards.ui.FlashCard
import com.kaanb.moonrunes.flash_cards.viewmodels.FlashCardViewModel
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kaanb.moonrunes.R
import com.kaanb.moonrunes.dictionary.ui.DrawerSheet
import com.kaanb.moonrunes.navigation.Route
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashCardScreen(
    modifier: Modifier = Modifier,
    innerPadding: PaddingValues,
    onRoute: (Route) -> Unit,
    viewModel: FlashCardViewModel = hiltViewModel(),
    goBack: () -> Unit
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerSheet(
                selectedItem = Route.FlashCards,
                onItemClicked = { it ->
                    scope.launch {
                        drawerState.close()
                    }
                    onRoute(it)
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {},
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                drawerState.open()
                            }
                        }) {
                            Icon(painterResource(R.drawable.menu_24px), null)
                        }
                    }
                )
            }
        ) { paddingValues ->
            if (uiState.value.dueCards.isEmpty()) {
                Box(modifier= Modifier.padding(paddingValues).fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(stringResource(R.string.flashcard_no_cards_due), style = MaterialTheme.typography.titleLarge)
                        Button(onClick = goBack) {
                            Text(stringResource(R.string.flashcard_go_back))
                        }
                    }

                }
            } else {
                FlashCard(
                    modifier = Modifier.padding(paddingValues),
                    onPressReview = viewModel::onClickReview,
                    flashCard = uiState.value.dueCards.first(),
                    showAnswer = uiState.value.showAnswer,
                    onClickReveal = viewModel::showAnswer
                )
            }

        }
    }


}
