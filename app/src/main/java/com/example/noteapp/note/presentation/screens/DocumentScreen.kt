package com.example.noteapp.note.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.noteapp.Permission
import com.example.noteapp.Screen
import com.example.noteapp.note.domain.models.Document
import com.example.noteapp.note.presentation.view_models.DocumentState
import com.example.noteapp.note.presentation.view_models.DocumentViewModel

@Composable
fun DocumentScreen(
    modifier: Modifier = Modifier,
    viewModel: DocumentViewModel = hiltViewModel(),
    onClickBack: () -> Unit,
    navHostController: NavHostController,

    ) {


    DocumentContent(
        onClickBack = {
            viewModel.onCloseOrSave(onClickBack)
        },
        state = viewModel.state.collectAsState(),
        onTitleChange = viewModel::onTitleChange,
        onBodyChange = viewModel::onBodyChange,
        onClickDelete = viewModel::onClickDelete,
        onDeleteAlertDialogConfirmation = viewModel::onDeleteAlertDialogConfirmation,
        onDeleteAlertDialogDismissRequest = viewModel::onDeleteAlertDialogDismissRequest,
        onClickShare = { navHostController.navigate(Screen.Share.setId(viewModel.docId.value.toString())) },
        onClickManageAccess = { navHostController.navigate(Screen.ManageAccess.setId(viewModel.docId.value.toString())) },
        onDismissShareDialogRequest = viewModel::onDismissShareDialogRequest,
        navHostController = navHostController,

        )


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentContent(
    modifier: Modifier = Modifier,
    onClickBack: () -> Unit,
    state: State<DocumentState>,
    onTitleChange: (title: String) -> Unit,
    onBodyChange: (title: String) -> Unit,
    onClickDelete: () -> Unit,
    onDeleteAlertDialogDismissRequest: () -> Unit,
    onDeleteAlertDialogConfirmation: (navHostController: NavHostController) -> Unit,
    onClickShare: () -> Unit,
    onClickManageAccess: () -> Unit,
    onDismissShareDialogRequest: () -> Unit,
    navHostController: NavHostController,

    ) {
    var expanded by remember { mutableStateOf(false) }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = " ") },
                navigationIcon = {
                    IconButton(onClick = onClickBack) {
                        if (state.value.permission == Permission.READ) {
                            Icon(
                                Icons.Default.ArrowBackIosNew,
                                contentDescription = ""
                            )
                        } else {
                            Icon(
                                Icons.Default.Save,
                                contentDescription = ""
                            )
                        }
                    }
                },
                actions = {
                    if (!state.value.isLoading) {
                        if (state.value.permission == Permission.ALL) {
                            IconButton(onClick = { expanded = true }) {
                                Icon(
                                    imageVector = Icons.Outlined.MoreVert,
                                    contentDescription = "More actions"
                                )
                            }

                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {

                                DropdownMenuItem(onClick = {
                                    expanded = false
                                    onClickDelete()

                                },
                                    text = {
                                        Text("Delete")
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Outlined.Delete,
                                            contentDescription = "Delete",
                                            modifier = Modifier.padding(end = 8.dp)
                                        )

                                    })

                                DropdownMenuItem(onClick = {
                                    expanded = false
                                    onClickShare()
                                }, text = {
                                    Text("Share")
                                },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Outlined.Share,
                                            contentDescription = "Share",
                                            modifier = Modifier.padding(end = 8.dp)
                                        )

                                    })
                                DropdownMenuItem(onClick = {
                                    expanded = false
                                    onClickManageAccess()
                                }, text = {
                                    Text("Manage Access")
                                },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Outlined.People,
                                            contentDescription = "Manage Access",
                                            modifier = Modifier.padding(end = 8.dp)
                                        )

                                    })
                            }
                        }
                    }
                }
            )

        }
    ) {
        Column(Modifier.padding(it)) {
            if (state.value.isLoading) {
                DocumentContentLoading()
            } else {
                if (state.value.userData != null) {

                    if (state.value.permission == Permission.ALL || state.value.permission == Permission.WRITE) {
                        DocumentContentWrite(
                            state = state,
                            onTitleChange = onTitleChange,
                            onBodyChange = onBodyChange
                        )
                    } else if (state.value.permission == Permission.READ) {

                        DocumentContentRead(doc = state.value.document)
                    }


                }
            }

        }
        when {
            state.value.openDeleteAlertDialog -> {
                DeleteConfirmationDialogBox(
                    onDismissRequest = onDeleteAlertDialogDismissRequest,
                    onConfirmation = { onDeleteAlertDialogConfirmation(navHostController) }
                )


            }

//            state.value.openShareDialog -> {
//                ShareDialogBox(onDismissRequest = onDismissShareDialogRequest)
//            }
        }

    }
}

@Composable
fun DocumentContentRead(modifier: Modifier = Modifier, doc: Document) {
    Column(
        modifier = modifier
            .padding(8.dp)
    ) {
        Card(
            shape = MaterialTheme.shapes.extraSmall,
            modifier = Modifier.fillMaxWidth(.8f)
        ) {
            Text(
                text = doc.title,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(8.dp),

                )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxSize(),
            shape = MaterialTheme.shapes.extraSmall
        ) {
            Text(
                text = doc.body,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
fun DocumentContentWrite(
    modifier: Modifier = Modifier,
    state: State<DocumentState>,
    onTitleChange: (title: String) -> Unit,
    onBodyChange: (title: String) -> Unit
) {
    Column(
        modifier = modifier
            .padding(8.dp)
    ) {
        Card(
            shape = MaterialTheme.shapes.extraSmall,
            modifier = Modifier.fillMaxWidth(.8f)
        ) {

            TextField(
                value = state.value.document.title,
                textStyle = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(8.dp),
                onValueChange = onTitleChange
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxSize(),
            shape = MaterialTheme.shapes.extraSmall
        ) {
            TextField(
                value = state.value.document.body,
                textStyle = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(8.dp),
                onValueChange = onBodyChange
            )
        }
    }
}


@Composable
fun DocumentContentLoading(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(8.dp)
    ) {
        Card(
            shape = MaterialTheme.shapes.extraSmall,
            modifier = Modifier.fillMaxWidth(.8f)
        ) {
            Text(
                text = "                 ",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(8.dp),

                )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxSize(),
            shape = MaterialTheme.shapes.extraSmall
        ) {
            Text(
                text = "           ",
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

//@Preview
//@Composable
//private fun DocumentContentPrev() {
//    DocumentContentWrite(doc = Document(
//            id = "",
//    title = "Big title of document is here",
//    body = "```\n" +
//            "Google Map\n" +
//            "Review\n" +
//            "Jwt and Google sign in\n" +
//            "\n" +
//            "\n" +
//            "Filter Heritage sites list, by user current location, showing the nearest locations on top\n" +
//            "\n" +
//            "\n" +
//            " Searching of sites\n" +
//            " \n" +
//            " \n" +
//            " \n" +
//            "  \n" +
//            " \n" +
//            " - [ ] Google Map\n" +
//            "- [ ] Review\n" +
//            "- [ ] Jwt and Google sign in\n" +
//            "- [ ] Filter Heritage sites list, by user current location, showing the nearest locations on top\n" +
//            "- [ ]  Searching of sites\n" +
//            "\n" +
//            "\n" +
//            "\n" +
//            "\n" +
//            "\n" +
//            "\n" +
//            "```\n",
//    ownerId = "G55ICUXesqS0co8U10E8s3acl3w2",
//    currentEditors = listOf("editor${Random.nextInt(1, 5)}", "editor${Random.nextInt(1, 5)}"),
//    lastEditTime = System.currentTimeMillis(),
//    createdAt = System.currentTimeMillis(),
//    updatedAt = System.currentTimeMillis()
//    ))
//}