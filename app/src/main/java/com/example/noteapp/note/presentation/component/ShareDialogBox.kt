package com.example.noteapp.note.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.noteapp.Permission
import com.example.noteapp.auth.domain.model.copyUser
import com.example.noteapp.note.domain.models.ShareHolder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareDialogBox(
    onDismissRequest: ()->Unit,
    viewModel: ShareDialogViewModel = hiltViewModel()
) {


    ShareDialogBoxContent(
        onDismissRequest,
        viewModel.state.collectAsState(),
        viewModel::onQueryChange,
        viewModel::onClickResultElement,
        viewModel::onClickSelecteElementClose,
        viewModel::onClickDropDownPermission,
        viewModel::changeStatePermission,
        viewModel::onClickShare,

    )
}



@Composable
private fun ShareDialogBoxContent(
    onDismissRequest: () -> Unit,
    state: State<ShareDialogState>,
    onQueryChange: (s: String)->Unit,
    onClickResultElement: (element: copyUser?)->Unit,
    onClickSelecteElementClose: ()->Unit,
    onClickDropDownPermission:(shareHolder : ShareHolder, newPermission:Permission)->Unit,
    changeStatePermission: (permission: Permission)->Unit,
    onClickShare: (onDismissRequest: ()->Unit)->Unit) {
    Dialog(
        onDismissRequest = onDismissRequest,
    ) {

        Card(
            Modifier.fillMaxWidth(1f)
        ) {
Box(
    modifier = Modifier.padding(8.dp)
) {


    if (state.value.selectedElement == copyUser()) {

        Card(
            modifier = Modifier
                .padding(8.dp)
        ) {

            TextField(
                value = state.value.query,
                onValueChange = onQueryChange
            )
            LazyColumn() {
                items(state.value.result) {
                    var modifier = Modifier.padding(8.dp)

                    userElement(modifier, onClickResultElement, it)
                }
            }
        }

    } else {
        val modifier = Modifier
            .padding(8.dp)
        Column {

            Row(
                modifier = Modifier
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = MaterialTheme.shapes.small
                    )
                    .padding(end = 8.dp, start = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                userElement(modifier.weight(1f), onClickResultElement, state.value.selectedElement)
                IconButton(onClick = onClickSelecteElementClose, modifier = Modifier.weight(.1f)) {
                    Icon(imageVector = Icons.Filled.Close, contentDescription = "")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
            Column() {
                val expand = remember {
                    mutableStateOf(false)
                }
                TextButton(onClick = { expand.value = true }) {
                    Text(text = state.value.permissionType.toString())
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = ""
                    )
                }
                DropdownMenu(expanded = expand.value, onDismissRequest = { expand.value = false }) {
                    DropdownMenuItem(
                        text = { Text(text = Permission.READ.toString()) },
                        onClick = {
                            changeStatePermission(Permission.READ)
                            expand.value = false
                        })
                    DropdownMenuItem(
                        text = { Text(text = Permission.WRITE.toString()) },
                        onClick = {
                            changeStatePermission( Permission.WRITE)
                            expand.value = false
                        })
                }
            }

                TextButton(
                    onClick = { onClickShare(onDismissRequest) },
                    enabled = if(state.value.selectedElement == copyUser()){false}else{true},

                ) {
                    Text("SHARE")
                }


            }

        }

    }

        Column {
            Spacer(modifier = Modifier.height(84.dp))
            if (state.value.peopleWithAcess.isNotEmpty() && state.value.query.isEmpty() && state.value.selectedElement == copyUser()) {

                Text(text = "Peopel with access")
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn {
                    items(state.value.peopleWithAcess) {
                        Card(
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth(1f)


                        ) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth(1f)
                            ) {
                                Row (
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(.7f)

                                ){

                                if (it.profilePic != null) {
                                    AsyncImage(
                                        model = it.profilePic,
                                        contentDescription = "Profile picture",
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            ,
                                        contentScale = ContentScale.Fit
                                    )
                                }

                                if (it.email != null) {
                                    Text(
                                        text = it.email,
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                }
                                }
                                Column(
                                    modifier = Modifier.weight(.3f)
                                ) {
                                    val expand = remember {
                                        mutableStateOf(false)
                                    }
                                    TextButton(onClick = { expand.value = true }) {
                                        Text(text = it.permissionType)
                                        Icon(
                                            imageVector = Icons.Default.KeyboardArrowDown,
                                            contentDescription = ""
                                        )
                                    }
                                    DropdownMenu(expanded = expand.value, onDismissRequest = { expand.value = false }) {
                                        DropdownMenuItem(
                                            text = { Text(text = Permission.READ.toString()) },
                                            onClick = {
                                                onClickDropDownPermission(it, Permission.READ)
                                                expand.value = false
                                            })
                                        DropdownMenuItem(
                                            text = { Text(text = Permission.WRITE.toString()) },
                                            onClick = {
                                                onClickDropDownPermission(it, Permission.WRITE)
                                                expand.value = false
                                            })
                                    }
                                }
                            }
                        }
                    }
                }
            }


        }


}


        }
    }
}

@Composable
private fun userElement(
    modifier: Modifier,
    onClickResultElement: (element: copyUser?) -> Unit,
    it: copyUser
) {
    Card(
        modifier = modifier
            .fillMaxWidth(1f)
            .clickable { onClickResultElement(it) }

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
                        .size(40.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            if (it.email != null) {
                Text(
                    text = it.email,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}


@Preview
@Composable
private fun ShareDialogBoxContentPrev() {
    ShareDialogBoxContent(
        onDismissRequest = { /*TODO*/ },
        state = remember {
            mutableStateOf(ShareDialogState(
                query = "",
                selectedElement = copyUser(),
                peopleWithAcess = listOf(ShareHolder(sharedId = "", documentId = "", email = "amanmahto848@gmail.com", permissionType = "READ"))
            ))
        },
        onQueryChange = {},
        onClickSelecteElementClose = {},
        onClickDropDownPermission = {
                                    shareHolder, newPermission ->
        },
        changeStatePermission = {},
        onClickResultElement = {}
    ) {

    }
}

