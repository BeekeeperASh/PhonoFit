package com.example.phonofit.navigation

import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState


@Composable
fun TopNavigation(
    navController: NavController
) {
    val listItem = listOf(
        NavigateItem.Screen5,
        NavigateItem.Auth
        //NavigateItem.Account,
    )
    androidx.compose.material.TopAppBar(
        backgroundColor = MaterialTheme.colorScheme.secondaryContainer
    ) {

        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = backStackEntry?.destination?.route
        listItem.forEach { item ->
            BottomNavigationItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route)
                },
                icon = {
                    Icon(
                        painter = painterResource(id = item.iconId),
                        contentDescription = "im_icon"
                    )
                },
                label = {
                    Text(text = item.title, fontSize = 8.sp)
                },
                selectedContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                unselectedContentColor = MaterialTheme.colorScheme.secondary
            )
        }
    }
}