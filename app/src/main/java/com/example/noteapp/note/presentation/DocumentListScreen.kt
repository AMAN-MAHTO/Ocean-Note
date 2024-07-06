package com.example.noteapp.note.presentation

import android.util.Log
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.noteapp.R
import com.example.noteapp.Screen
import com.example.noteapp.auth.domain.model.UserData
import com.example.noteapp.note.domain.models.Document
import com.example.noteapp.screens.home.NavigationItem
import com.example.noteapp.screens.home.onMenuIconClick
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date


@Composable
fun DocumentListScreen (
    viewModel: DocumentListViewModel = hiltViewModel(),
    navHostController: NavHostController,
){
    val documents = remember {
        mutableStateOf(emptyList<Document>())
    }
    documents.value = viewModel.ownedDocuments.collectAsState().value + viewModel.sharedDocument.collectAsState().value
    Log.d("Document", "DocumentListScreen: ${documents.value.sortedBy { it.updatedAt }.toString()}")

    DocumentList(
        list = viewModel.ownedDocuments.collectAsState().value + viewModel.sharedDocument.collectAsState().value,
        onClickDoc = { navHostController.navigate(Screen.Document.setId(it)) },
        onClickFAB = viewModel::onClickFAB,
        getUserData = viewModel::getUserData,
        getNavigationItems = viewModel::getNavigationItems,
        navHostController= navHostController,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentList(
    modifier: Modifier = Modifier,
    list: List<Document>,
    onClickDoc: (docId:String)->Unit,
    onClickFAB: (navHostController: NavHostController)->Unit,
    getUserData: ()->UserData?,
    getNavigationItems: ()->List<NavigationItem>,
    navHostController: NavHostController,
    ) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.fillMaxWidth(.75f)
            ) {
                DrawerHeader(getUserData())
                DrawerBody(getNavigationItems(),
                    navHostController,
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
                TopAppBar(title = {

                },
                    navigationIcon = {
                        IconButton(onClick = { onMenuIconClick(drawerState, scope) }) {
                            Icon(imageVector = Icons.Filled.Menu, contentDescription = "")
                        }
                    },
    actions = {
        IconButton(onClick = { /*TODO*/ },
            modifier = Modifier
                .clip(CircleShape)
                .background(color = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.user),
                modifier = Modifier.size(18.dp),
                contentDescription = "",
                tint = MaterialTheme.colorScheme.tertiary
            )

        }
    }
                    )



             },
        floatingActionButton = {FloatingActionButton(onClick = { onClickFAB(navHostController) }) {
            Icon(imageVector = Icons.Filled.Add, contentDescription = "")
        }}
    ) {



        LazyVerticalStaggeredGrid(columns = StaggeredGridCells.Fixed(2), modifier = modifier.padding(it)) {


            items(list) {
                Card(
                    shape = MaterialTheme.shapes.extraSmall,
                    modifier = Modifier
                        .padding(8.dp)
                        .clip(MaterialTheme.shapes.extraSmall)
                        .border(
                            width = 2.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.secondary,
                                    MaterialTheme.colorScheme.tertiary
                                )
                            ),
                            shape = MaterialTheme.shapes.extraSmall
                        )

                        .clickable {
                            onClickDoc(it.id)
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface

                    )
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()

                                .border(
                                    width = .5.dp,
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.primary,
                                            MaterialTheme.colorScheme.tertiary
                                        )
                                    ),
                                    shape = MaterialTheme.shapes.extraSmall
                                ),
                            shape = MaterialTheme.shapes.extraSmall
                        ) {

                            Text(
                                text = it.title,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            modifier = Modifier.border(
                                width = .5.dp,
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.tertiary
                                    )
                                ),
                                shape = MaterialTheme.shapes.extraSmall
                            ),
                            shape = MaterialTheme.shapes.extraSmall
                        ) {

                            Text(
                                text = SimpleDateFormat("dd.MM.yyyy").format(Date(it.updatedAt)),
                                modifier = Modifier.padding(6.dp),
                                style = MaterialTheme.typography.bodySmall
                            )

                        }
                    }
                }
            }
        }
    }}
}

@Preview
@Composable
private fun DocListPrev() {
    val documents = listOf(
    Document(id="", title="sdfgsdfggggggggggggggggggfffffffffffffffgsdfg", ownerId="FKy3vlHgiPhR7PGv7JOcWH908so1"),
    Document(id="1BkcqaMhqjbWUJ61FJ6b", title="title3", body="body3", ownerId="FKy3vlHgiPhR7PGv7JOcWH908so1", currentEditors=listOf("editor3", "editor2"), lastEditTime=1719549696618, createdAt=1719549696618, updatedAt=1719549696618),
    Document(id="ZV9o4ToqYIbDzg9vzjkD", title="title1", body="body1", ownerId="FKy3vlHgiPhR7PGv7JOcWH908so1", currentEditors=listOf("editor2", "editor3"), lastEditTime=1719549696618, createdAt=1719549696618, updatedAt=1719549696618),
    Document(id="dYzhmJTTBUFnhBH3Uuvv", title="title2", body="body2", ownerId="FKy3vlHgiPhR7PGv7JOcWH908so1", currentEditors=listOf("editor3", "editor4"), lastEditTime=1719549696618, createdAt=1719549696618, updatedAt=1719549696618),
    Document(id="5p1bXtq5yZFm8EezF29m", title="title1", body="body1", ownerId="G55ICUXesqS0co8U10E8s3acl3w2", currentEditors=listOf("editor3", "editor3"), lastEditTime=1719549824809, createdAt=1719549824809, updatedAt=1719549824809),
    Document(id="PjZMnieZJyPBQkwS3zA1", title="title2", body="body2", ownerId="G55ICUXesqS0co8U10E8s3acl3w2", currentEditors=listOf("editor2", "editor3"), lastEditTime=1719549824809, createdAt=1719549824809, updatedAt=1719549824809)
)
//    DocumentList(list = documents, onClickDoc = {}, onClickFAB = {},
//        getNavigationItems = {
//
//        },
//        navHostController = rememberNavController()
//    )

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
