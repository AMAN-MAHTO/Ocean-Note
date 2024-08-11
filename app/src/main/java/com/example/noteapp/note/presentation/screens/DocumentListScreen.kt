package com.example.noteapp.note.presentation.screens

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.compose.AppTheme
import com.example.noteapp.R
import com.example.noteapp.Screen
import com.example.noteapp.auth.data.GoogleAuthUiClient
import com.example.noteapp.auth.domain.model.UserData
import com.example.noteapp.components.CustomFloatingActionButton
import com.example.noteapp.note.data.FirebaseFirestoreClientImpl
import com.example.noteapp.note.domain.models.Document
import com.example.noteapp.note.presentation.view_models.DocActions
import com.example.noteapp.note.presentation.view_models.DocumentListState
import com.example.noteapp.note.presentation.view_models.DocumentListViewModel
import com.example.noteapp.note.presentation.view_models.FilterDoc
import com.example.ui.theme.bodyFontFamily
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject


@Composable
fun DocumentListScreen(
    viewModel: DocumentListViewModel = hiltViewModel(),
    navHostController: NavHostController,
) {
//    val documents = remember {
//        mutableStateOf(emptyList<Document>())
//    }
//    documents.value =
//        viewModel.ownedDocuments.collectAsState().value + viewModel.sharedDocument.collectAsState().value
//    Log.d("Document", "DocumentListScreen: ${documents.value.sortedBy { it.updatedAt }.toString()}")

    DocumentList(
        state = viewModel.state.collectAsState(),
        onClickDoc = { navHostController.navigate(Screen.Document.setId(it)) },
        onClickFAB = viewModel::onClickFAB,
        navHostController = navHostController,
        onDismissProfileDialogRequest = viewModel::onDismissProfileDialogRequest,
        onProfileDialogRequest = viewModel::onProfileDialogRequest,
        onLogout = viewModel::onLogout,
        onDismissShortBySheet = viewModel::onDismissShortBySheet,
        onClickShortByItem = viewModel::onClickShortByItem,
        onClickShortBy = viewModel::onClickshortBy,
        onLongClickDoc = viewModel::onLongClickDoc,
        onDismissDocActionSheet = viewModel::onDismissDocActionSheet,
        onClickDocActionItem = viewModel::onClickDocActionItem,
        onDismissDeleteActionDialog = viewModel::onDismissDeleteActionDialog,
        onDeleteAlertConformation = viewModel::onDeleteAlertConformation,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun DocumentList(
    modifier: Modifier = Modifier,
    onClickDoc: (docId: String) -> Unit,
    onClickFAB: (navHostController: NavHostController) -> Unit,
    navHostController: NavHostController,
    onDismissProfileDialogRequest: () -> Unit,
    onProfileDialogRequest: () -> Unit,
    onLogout: (navHostController: NavHostController) -> Unit,
    state: State<DocumentListState>,
    onDismissShortBySheet: () -> Unit,
    onDismissDocActionSheet: () -> Unit,
    onClickShortByItem: (FilterDoc) -> Unit,
    onClickShortBy: () -> Unit,
    onLongClickDoc: (docid: String) -> Unit,
    onClickDocActionItem: (DocActions, navHostController: NavHostController) -> Unit,
    onDismissDeleteActionDialog: () -> Unit,
    onDeleteAlertConformation: () -> Unit,
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val sortBySheetState = rememberModalBottomSheetState()
    val docActionSheetState = rememberModalBottomSheetState()
    val list =
        state.value.ownedDocuments + state.value.sharedDocuments
    val query = remember {
        mutableStateOf("")
    }
    val sortedlist = when (state.value.filter) {
        FilterDoc.LAST_UPDATED -> list.sortedByDescending { it.updatedAt }
        FilterDoc.NAME -> list.sortedByDescending { it.title }

    }.filter {
        if (query.value.isNotEmpty())
            query.value.toLowerCase(Locale.ROOT) in it.title.toLowerCase(Locale.ROOT) || query.value.toLowerCase(
                Locale.ROOT
            ) in it.body.toLowerCase(Locale.ROOT)
        else true
    }
    val colors = if (isSystemInDarkTheme()) {
        listOf(
            Color(0xFF0073A3), // Dark blue gradient start
            Color(0xFF005377), // Dark blue gradient middle
            Color(0xFF003F5C), // Dark blue gradient middle
            Color(0xFF002B3F)
        )
    } else {
        listOf(
            Color(0xFF72B8FF),
            Color(0xFF619FFF),
            Color(0xFF4C8CFF),
            Color(0xFF325899),
        )
    }
    Scaffold(
        modifier = Modifier,
        floatingActionButton = {
            CustomFloatingActionButton(
                onClick = { onClickFAB(navHostController) }, icon = Icons.Filled.Add
            )
        }
    ) {
        Column(
            modifier = modifier

                .padding(it)
                .padding(start = 16.dp, end = 16.dp)
        ) {
            if (state.value.showSortBySheet)
                ModalBottomSheet(
                    onDismissRequest = onDismissShortBySheet,
                    dragHandle = {}, sheetState = sortBySheetState
                ) {
                    Column(
                        modifier = Modifier.padding(

                            top = 16.dp,
                            bottom = 48.dp
                        ),
                    ) {
                        Text(
                            "Sort By",
                            modifier = Modifier.padding(start = 48.dp, end = 16.dp),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = bodyFontFamily
                            )
                        )
                        HorizontalDivider(modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))
                        NavigationDrawerItem(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            label = { Text("Name") },
                            icon = {

                                Icon(
                                    imageVector = Icons.Default.ArrowDownward,
                                    contentDescription = "",
                                    tint = if (state.value.filter == FilterDoc.NAME)
                                        MaterialTheme.colorScheme.onSurface
                                    else
                                        MaterialTheme.colorScheme.surfaceContainer
                                )

                            },
                            selected = state.value.filter == FilterDoc.NAME,
                            onClick = { onClickShortByItem(FilterDoc.NAME) })
                        NavigationDrawerItem(
                            modifier = Modifier.padding(horizontal = 16.dp),

                            label = { Text("Last Updated") },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.ArrowDownward,
                                    contentDescription = "",
                                    tint = if (state.value.filter == FilterDoc.LAST_UPDATED)
                                        MaterialTheme.colorScheme.onSurface
                                    else
                                        MaterialTheme.colorScheme.surfaceContainer
                                )


                            },
                            selected = state.value.filter == FilterDoc.LAST_UPDATED,
                            onClick = { onClickShortByItem(FilterDoc.LAST_UPDATED) })
                    }

                }

            if (state.value.showDocActionSheet)
                ModalBottomSheet(
                    onDismissRequest = onDismissDocActionSheet,
                    dragHandle = {},
                    sheetState = docActionSheetState
                ) {
                    Column(
                        modifier = Modifier.padding(

                            top = 16.dp,
                            bottom = 48.dp
                        ),
                    ) {
                        Text(
                            "Doc Action",
                            modifier = Modifier.padding(start = 48.dp, end = 16.dp),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = bodyFontFamily
                            )
                        )
                        HorizontalDivider(modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))
                        NavigationDrawerItem(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            label = { Text("Share") },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "",

                                    )

                            },
                            selected = false,
                            onClick = { onClickDocActionItem(DocActions.SHARE, navHostController) })
                        NavigationDrawerItem(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            label = { Text("Manage Access") },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.People,
                                    contentDescription = "",
                                )
                            },
                            selected = false,
                            onClick = {
                                onClickDocActionItem(
                                    DocActions.MANAGE_ACCESS,
                                    navHostController
                                )
                            })
                        NavigationDrawerItem(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            label = { Text("Delete") },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "",
                                )
                            },
                            selected = false,
                            onClick = {
                                onClickDocActionItem(
                                    DocActions.DELETE,
                                    navHostController
                                )
                            })
                    }

                }




            when {
                state.value.isProfileView -> ProfileDialogBox(
                    onDismissRequest = onDismissProfileDialogRequest,
                    userData = state.value.userData,
                    onClickLogout = { onLogout(navHostController) }
                )

                state.value.showDeleteAlertBox -> DeleteConfirmationDialogBox(
                    onDismissRequest = onDismissDeleteActionDialog,
                    onConfirmation = onDeleteAlertConformation
                )

            }
            Column {
                SearchBar(
                    modifier = Modifier.fillMaxWidth(),
                    query = query.value,
                    onQueryChange = {
                        query.value = it
                    },

                    onSearch = {},
                    active = false,
                    onActiveChange = {},
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "")
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = onProfileDialogRequest,

                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .clip(CircleShape)
                                .background(color = MaterialTheme.colorScheme.secondaryContainer)
                        ) {
                            if (state.value.userData?.profilePictureUrl != null) {
                                AsyncImage(
                                    model = state.value.userData!!.profilePictureUrl,
                                    contentDescription = "Profile picture",
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            } else {

                                Icon(
                                    painter = painterResource(id = R.drawable.user),
                                    modifier = Modifier.size(18.dp),
                                    contentDescription = "",
                                    tint = MaterialTheme.colorScheme.tertiary
                                )
                            }

                        }
                    }
                ) {

                }
                Spacer(modifier = modifier.height(16.dp))
                TextButton(onClick = onClickShortBy) {
                    Text(
                        if (state.value.filter == FilterDoc.NAME) {
                            "Name"
                        } else {
                            "Last Updated"
                        }
                    )
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowDownward,
                        contentDescription = "",
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = modifier.height(16.dp))
            }
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                modifier = modifier.padding(it),
//                contentPadding = PaddingValues(8.dp),
                verticalItemSpacing = 16.dp,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                items(sortedlist) {
                    Card(
                        shape = MaterialTheme.shapes.extraSmall,

                        modifier = Modifier
                            .combinedClickable(
                                onLongClick = {
                                    onLongClickDoc(it.id)
                                },
                                onClick = {
                                    onClickDoc(it.id)
                                }
                            ),

                        ) {
                        Column(
                            modifier = Modifier.padding(
                                start = 8.dp,
                                end = 8.dp,
                                top = 16.dp,
                                bottom = 16.dp
                            )
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
                                    text = it.body,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(8.dp),
                                    maxLines = 6,
                                    minLines = 3,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = it.title,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                maxLines = 2, minLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

            }

        }
    }
}

@Preview
@Composable
private fun DocListPrev(

) {
    val documents = listOf(
        Document(
            id = "",
            body = "llj",
            title = "sdfgsdfggggggggggggggggggfffffffffffffffgsdfg",
            ownerId = "FKy3vlHgiPhR7PGv7JOcWH908so1"
        ),
        Document(
            id = "1BkcqaMhqjbWUJ61FJ6b",
            title = "title3",
            body = "body3 jlasdlfjalsjdfljasldfjlaslkdjfasjdlfjlasdjjflkjaslkdflasjf;lj",
            ownerId = "FKy3vlHgiPhR7PGv7JOcWH908so1",
            currentEditors = listOf("editor3", "editor2"),
            lastEditTime = 1719549696618,
            createdAt = 1719549696618,
            updatedAt = 1719549696618
        ),
        Document(
            id = "ZV9o4ToqYIbDzg9vzjkD",
            title = "title1",
            body = "body1 lkjalsjfljalkdfjlasdjfljaslflasjlfjlaskdjfljaslfjalsjfljaslfjlasjdflkjadfjasldf;asjdfjalfj;asf;lkasjfljaslkf;alsjflkasd;lfjaslddjfkasjdf;lajf;lkjas;lfj;alsdjf;lajsd;lfja;sljf;lkasjf;ljaslkdfj;alsdf;lksjd;lkfjas;ldf;lkasjdfk;asj;lfjaslkdjflakj",
            ownerId = "FKy3vlHgiPhR7PGv7JOcWH908so1",
            currentEditors = listOf("editor2", "editor3"),
            lastEditTime = 1719549696618,
            createdAt = 1719549696618,
            updatedAt = 1719549696618
        ),
        Document(
            id = "dYzhmJTTBUFnhBH3Uuvv",
            title = "title2",
            body = "body2 laldfoajslfal;sdfwei0080-rflasmd;m l",
            ownerId = "FKy3vlHgiPhR7PGv7JOcWH908so1",
            currentEditors = listOf("editor3", "editor4"),
            lastEditTime = 1719549696618,
            createdAt = 1719549696618,
            updatedAt = 1719549696618
        ),
        Document(
            id = "5p1bXtq5yZFm8EezF29m",
            title = "title1",
            body = "body1 ljl;jdljflasdjfljasl;djfjlasjdfljajsldjflasdmcxm,v,jxowpoqeropqweoerf dmv;spotiqpowerpoqwieriqwpfxcvzsmcvvwqirpqpweifasdm.xcvmxcmvwqepor",
            ownerId = "G55ICUXesqS0co8U10E8s3acl3w2",
            currentEditors = listOf("editor3", "editor3"),
            lastEditTime = 1719549824809,
            createdAt = 1719549824809,
            updatedAt = 1719549824809
        ),
        Document(
            id = "PjZMnieZJyPBQkwS3zA1",
            title = "title2",
            body = "body2 ljasljdflajpweirfldmmasl; opieorf",
            ownerId = "G55ICUXesqS0co8U10E8s3acl3w2",
            currentEditors = listOf("editor2", "editor3"),
            lastEditTime = 1719549824809,
            createdAt = 1719549824809,
            updatedAt = 1719549824809
        )
    )

    AppTheme {

        DocumentList(
            onClickDoc = {},
            onClickFAB = {},
            navHostController = rememberNavController(),
            onDismissProfileDialogRequest = { },
            onProfileDialogRequest = { },
            onLogout = {},
            onDismissShortBySheet = {},
            onClickShortByItem = {},
            onClickShortBy = {},
            onDismissDocActionSheet = {},
            onLongClickDoc = {},
            onDeleteAlertConformation = {},
            onClickDocActionItem = { _, _ -> },
            onDismissDeleteActionDialog = {},
            state = remember {
                mutableStateOf(
                    DocumentListState(
                        ownedDocuments = documents,
                        userData = UserData("", "", "", "")
                    )
                )
            }
        )
    }
}






