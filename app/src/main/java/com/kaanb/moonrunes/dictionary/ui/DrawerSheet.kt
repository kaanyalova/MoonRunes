package com.kaanb.moonrunes.dictionary.ui

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kaanb.moonrunes.AboutActivity
import com.kaanb.moonrunes.R
import com.kaanb.moonrunes.navigation.Route

@Composable
fun DrawerSheet(
    modifier: Modifier = Modifier,
    selectedItem: Route,
    onItemClicked: (Route) -> Unit,
    pendingCards: Int = 5
) {
    ModalDrawerSheet() {
        val context = LocalContext.current
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(stringResource(R.string.app_name), modifier = Modifier.padding(16.dp))
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            NavigationDrawerItem(
                icon = {
                    Icon(
                        painter = painterResource(R.drawable.dictionary_24px),
                        contentDescription = null
                    )
                },
                label = { Text(stringResource(R.string.sidebar_name_dictionary)) },
                selected = selectedItem == Route.DictionarySearch,
                onClick = { onItemClicked(Route.DictionarySearch) })

            NavigationDrawerItem(
                icon = {
                    Icon(
                        painter = painterResource(R.drawable.favorite_24px),
                        contentDescription = null
                    )
                },
                label = { Text(stringResource(R.string.sidebar_name_favorites)) },
                selected = selectedItem == Route.FavoritesList,
                onClick = { onItemClicked(Route.FavoritesList) })

            NavigationDrawerItem(
                icon = {
                    Icon(
                        painter = painterResource(R.drawable.cards_stack_24px),
                        contentDescription = null
                    )
                },
                label = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(stringResource(R.string.sidebar_name_flash_cards), modifier = Modifier.padding(end = 8.dp))
                        if (pendingCards > 0) {
                            AssistChip(onClick = {}, label = {
                                Text("$pendingCards ${stringResource(R.string.cards_due)}")
                            })
                        }
                    }
                },
                selected = selectedItem == Route.FlashCards,
                onClick = { onItemClicked(Route.FlashCards) })



            NavigationDrawerItem(
                icon = {
                    Icon(
                        painter = painterResource(R.drawable.clear_day_24px),
                        contentDescription = null
                    )
                },
                label = { Text(stringResource(R.string.sidebar_name_wotd)) },
                selected = selectedItem == Route.Wotd,
                onClick = {
                    onItemClicked(Route.Wotd)
                }
            )

            HorizontalDivider(modifier = Modifier.padding(8.dp))

            NavigationDrawerItem(icon = {
                Icon(
                    painter = painterResource(R.drawable.info_24px), contentDescription = null
                )
            }, label = {
                Text(stringResource(R.string.sidebar_name_about))
            }, selected = false, onClick = {
                val intent = Intent(context, AboutActivity::class.java)
                context.startActivity(intent)
            })


            NavigationDrawerItem(
                icon = {
                    Icon(
                        painter = painterResource(R.drawable.settings_24px),
                        contentDescription = null
                    )
                },
                label = { Text(stringResource(R.string.sidebar_name_settings)) },
                selected = false,
                onClick = {

                }
            )


        }
    }

}