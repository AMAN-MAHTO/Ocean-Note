package com.example.noteapp.note.presentation.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.noteapp.Permission
import com.example.noteapp.auth.domain.model.copyUser
import com.example.noteapp.note.domain.models.ShareHolder
import com.example.noteapp.note.presentation.view_models.ShareDialogState
import com.example.noteapp.note.presentation.view_models.ShareDialogViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareScreen(
    navController: NavHostController,
    viewModel: ShareDialogViewModel = hiltViewModel()
) {
    ShareScreenContent(
        navController,
        viewModel.state.collectAsState(),
        viewModel::onQueryChange,
        viewModel::onClickResultElement,
        viewModel::onClickSelecteElementClose,
        viewModel::onClickDropDownPermission,
        viewModel::changeStatePermission,
        viewModel::onClickShare
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun ShareScreenContent(
    navHostController: NavHostController,
    state: State<ShareDialogState>,
    onQueryChange: (s: String) -> Unit,
    onClickResultElement: (element: copyUser?) -> Unit,
    onClickSelecteElementClose: (element: copyUser) -> Unit,
    onClickDropDownPermission: (shareHolder: ShareHolder, newPermission: Permission) -> Unit,
    changeStatePermission: (permission: Permission) -> Unit,
    onClickShare: (navHostController: NavHostController) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(

                title = {
                    Text(
                        text = "Share Document",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                actions = {
                    IconButton(
                        onClick = { onClickShare(navHostController) },
                        enabled = state.value.selectedElement.isNotEmpty()
                    ) {
                        Icon(Icons.Outlined.Send, contentDescription = "")
                    }
                }


            )
        },

        ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(8.dp)
        ) {

            Row(
                modifier = Modifier
                    .padding(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.PersonAdd,
                    contentDescription = "",
                    modifier = Modifier.padding(8.dp, top = 16.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                FlowRow(
                    Modifier.scrollable(orientation = Orientation.Vertical, state = rememberScrollState())
                ) {
                    if (state.value.selectedElement.isNotEmpty()) {
                        Spacer(modifier = Modifier
                            .height(8.dp)
                            .fillMaxWidth())
                        for (element in state.value.selectedElement) {
                            Row(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .border(1.dp, MaterialTheme.colorScheme.secondary, CircleShape),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                userElement(
                                    modifier = Modifier,
                                    onClickResultElement = onClickResultElement,
                                    it = element,
                                    textStyle = MaterialTheme.typography.bodyMedium,
                                    profilePicSize = 32.dp,
                                )
                                IconButton(
                                    onClick = { onClickSelecteElementClose(element) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Close,
                                        modifier = Modifier.size(16.dp),
                                        contentDescription = "Close"
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                        }
                    }


                    TextField(
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            focusedIndicatorColor = MaterialTheme.colorScheme.surface,
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.surface,
                            cursorColor = MaterialTheme.colorScheme.secondary
                        ),
                        placeholder = { Text("Search") },
                        value = state.value.query,
                        onValueChange = onQueryChange,
                        modifier = Modifier,
                        singleLine = true,
                    )
                }
            }


            Column {

                LazyColumn {
                    items(state.value.result) {

                        userElement(
                            Modifier.padding(8.dp),
                            onClickResultElement,
                            it,
                            profilePicSize = 32.dp,
                            textStyle = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

            }


            Spacer(modifier = Modifier.height(8.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                val expand = remember { mutableStateOf(false) }
                TextButton(
                    onClick = { expand.value = true }, modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.small
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = state.value.permissionType.toString(),
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Start
                        )
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = ""
                        )
                    }
                }
                DropdownMenu(
                    expanded = expand.value,
                    modifier = Modifier.fillMaxWidth(),
                    onDismissRequest = { expand.value = false }
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = Permission.READ.toString(),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        },
                        onClick = {
                            changeStatePermission(Permission.READ)
                            expand.value = false
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = Permission.WRITE.toString(),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        },
                        onClick = {
                            changeStatePermission(Permission.WRITE)
                            expand.value = false
                        }
                    )
                }
            }


        }
    }

}

@Composable
private fun userElement(
    modifier: Modifier,
    onClickResultElement: (element: copyUser?) -> Unit,
    it: copyUser,
    profilePicSize: Dp,
    textStyle: TextStyle
) {
    Card(
        modifier = modifier
            .clickable { onClickResultElement(it) },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,

            )
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            if (it.profilePic != null) {
                AsyncImage(
                    model = it.profilePic,
                    contentDescription = "Profile picture",
                    modifier = Modifier
                        .size(profilePicSize)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

            }
            if (it.email != null) {
                Text(
                    text = it.email,
                    style = textStyle
                )

            }
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}

@Preview
@Composable
private fun ShareScreenContentPreview() {
    ShareScreenContent(
        rememberNavController(),
        state = remember {
            mutableStateOf(
                ShareDialogState(
                    query = "",
                    result = mutableListOf(

                        copyUser(
                            username = "user$",
                            email = "user$@example.com",
                            profilePic = "profilePic$"
                        )
                    ),
                    selectedElement = mutableListOf(
                        copyUser(
                            username = "user$",
                            email = "user$@example.com",
                            profilePic = "profilePic$"
                        )
                    ),
                    peopleWithAcess = listOf(
                        ShareHolder(
                            sharedId = "",
                            documentId = "",
                            email = "amanmahto848@gmail.com",
                            permissionType = "READ"
                        )
                    )
                )
            )
        },
        onQueryChange = {},
        onClickSelecteElementClose = {},
        onClickDropDownPermission = { shareHolder, newPermission -> },
        changeStatePermission = {},
        onClickResultElement = {}
    ) {}
}
//val interactionSource = remember { MutableInteractionSource() }
//var text by remember { mutableStateOf("500") }
//
//BasicTextField(value = state.value.query,
//onValueChange = onQueryChange,
//
//
//) {
//
//    TextFieldDefaults.TextFieldDecorationBox(
//        value = state.value.query,
//        visualTransformation = VisualTransformation.None,
//        innerTextField = it,
//        singleLine = true,
//        enabled = true,
//        interactionSource = interactionSource,
//        // keep vertical paddings but change the horizontal
//        contentPadding = TextFieldDefaults.textFieldWithoutLabelPadding(
//            start = 8.dp, end = 8.dp,
//        ),
//        placeholder = { Text("Search") },
//        colors = TextFieldDefaults.textFieldColors(
//            containerColor = MaterialTheme.colorScheme.surface,
//            focusedIndicatorColor = MaterialTheme.colorScheme.surface,
//            unfocusedIndicatorColor = MaterialTheme.colorScheme.surface,
//            cursorColor = MaterialTheme.colorScheme.primary,
//            errorLabelColor = MaterialTheme.colorScheme.error
//        ),
//    )
//
//}