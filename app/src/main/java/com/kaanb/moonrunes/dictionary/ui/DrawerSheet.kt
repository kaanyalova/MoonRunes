package com.kaanb.moonrunes.dictionary.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kaanb.moonrunes.R
import com.kaanb.moonrunes.navigation.Route

@Composable
fun DrawerSheet(
    modifier: Modifier = Modifier, selectedItem: Route, onItemClicked: (Route) -> Unit
) {
    ModalDrawerSheet() {
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
                selected = selectedItem == Route.KanjiEntry,
                onClick = { onItemClicked(Route.DictionarySearch) })

            NavigationDrawerItem(
                icon = {
                    Icon(
                        painter = painterResource(R.drawable.cards_stack_24px),
                        contentDescription = null
                    )
                },
                label = { Text(stringResource(R.string.sidebar_name_flash_cards)) },
                selected = selectedItem == Route.KanjiEntry,
                onClick = { onItemClicked(Route.DictionarySearch) })

            HorizontalDivider(modifier = Modifier.padding(8.dp))

            NavigationDrawerItem(icon = {
                Icon(
                    painter = painterResource(R.drawable.info_24px), contentDescription = null
                )
            }, label = {
                Text(stringResource(R.string.sidebar_name_about))
            }, selected = false, onClick = {})


            NavigationDrawerItem(
                icon = {
                    Icon(
                        painter = painterResource(R.drawable.settings_24px),
                        contentDescription = null
                    )
                },
                label = { Text(stringResource(R.string.sidebar_name_settings)) },
                selected = false,
                onClick = {}
            )


        }
    }

}