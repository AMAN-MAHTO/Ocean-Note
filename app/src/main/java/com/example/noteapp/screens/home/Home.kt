package com.example.noteapp.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.StarRate
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Notes
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.StarRate
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material3.Card
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.noteapp.screens.note_list.NoteList
import com.example.noteapp.models.UserData
import com.example.noteapp.services.GoogleAuthUiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
    homeViewModel: HomeViewModel = hiltViewModel(),
    navController: NavController,
    onNoteListTileClick:(id: String)->Unit,
    onClickAddNewNote:()->Unit,
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.fillMaxWidth(.75f)
            ) {
                DrawerHeader(homeViewModel.getUserData())
                DrawerBody(homeViewModel.getNavigationItems(),
                    navController,
                    onNavigationItemClicked = {
                        scope.launch {
                            drawerState.close()
                        }
                    })

            }
        }
    ) {
        Scaffold(

            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    ),
                    title = {
                        Text(
                            text = "Notes",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { onMenuIconClick(drawerState, scope) }) {
                            Icon(imageVector = Icons.Filled.Menu, contentDescription = "")
                        }
                    },
                    actions = {
                        Row {
                            IconButton(
                                onClick = { },

                                ) {

                                Icon(imageVector = Icons.Filled.Search, contentDescription = "")
                            }
                        }


                    },

                    )
            },

            floatingActionButton = {
                FloatingActionButton(onClick = {onClickAddNewNote()}) {

                    Icon(imageVector = Icons.Filled.Add, contentDescription = "Search icon")
                }
            }
        ) { innerPadding ->
            NoteList(innerPadding,onNoteListTileClick={onNoteListTileClick(it)})

        }
    }


}

fun onMenuIconClick(drawerState: DrawerState, scope: CoroutineScope) {
    scope.launch {
        drawerState.apply {
            if (isClosed) open()
            else close()
        }
    }

}


@Composable
fun DrawerBody(
    navigationItems: List<NavigationItem>,
    navController: NavController,
    onNavigationItemClicked: () -> Unit
) {
    var selectedItemIndex by rememberSaveable {
        mutableStateOf(0)
    }

    
        navigationItems.forEachIndexed { index, navigationItem ->
            NavigationDrawerItem(
                //avtive indicater padding
                modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 4.dp, bottom = 4.dp),
                icon = {
                    if (index == selectedItemIndex)
                        if (navigationItem.title == "Logout") {
                            Icon(
                                imageVector = navigationItem.selectedIcon,
                                contentDescription = "navigation icon selected",
                                tint = MaterialTheme.colorScheme.error
                            )
                        } else
                            Icon(
                                imageVector = navigationItem.selectedIcon,
                                contentDescription = "navigation icon selected"
                            )
                    else
                        if (navigationItem.title == "Logout") {
                            Icon(
                                imageVector = navigationItem.unselectedIcon,
                                contentDescription = "navigation icon selected",
                                tint = MaterialTheme.colorScheme.error
                            )
                        } else
                            Icon(
                                imageVector = navigationItem.unselectedIcon,
                                contentDescription = "navigation icon unselected"
                            )
                },
                selected = index == selectedItemIndex,
                label = {
                    if (navigationItem.title == "Logout") {
                        Text(text = navigationItem.title, color = MaterialTheme.colorScheme.error)
                    } else {
                        Text(text = navigationItem.title)
                    }

                },

                onClick = {
                    selectedItemIndex = index
                    navigationItem.onItemClick(navController)
                    onNavigationItemClicked()

                })

            if (index == 2 || index == 4) {
                HorizontalDivider(
                    modifier = Modifier.padding(
                        start = 24.dp,
                        end = 24.dp,
                        top = 16.dp,
                        bottom = 16.dp
                    )
                )

            }

        }




}



@Composable
fun DrawerHeader(userData: UserData?) {
    Card(
        modifier = Modifier
            .fillMaxWidth(1f)
            .padding(bottom = 16.dp)
    ) {

        Column(
            Modifier.padding(
                start = 24.dp,
                top = 50.dp,
                end = 24.dp,
                bottom = 16.dp
            )
        ) {
            if (userData?.profilePictureUrl != null) {
                AsyncImage(
                    model = userData.profilePictureUrl,
                    contentDescription = "Profile picture",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            if (userData?.username != null) {
                Text(
                    text = userData.username,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun HomePreview(

) {

    Column {

        DrawerHeader(userData = UserData(
            "","AMAN MAHTO","https://yt3.ggpht.com/ytc/AIf8zZRbmJuX7cIam3HsvgsbVY_BgyGt55TlujeKUao=s48-c-k-c0x00ffffff-no-rj-mo"
        )
        )

//        DrawerBody(navigationItems = _navigationItems) {

        }


    }



