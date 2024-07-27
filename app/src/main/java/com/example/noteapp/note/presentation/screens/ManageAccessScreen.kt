package com.example.noteapp.note.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
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
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.noteapp.Permission
import com.example.noteapp.auth.domain.model.copyUser
import com.example.noteapp.note.domain.models.ShareHolder
import com.example.noteapp.note.presentation.view_models.ManageAccessState
import com.example.noteapp.note.presentation.view_models.ManageAccessViewModel

@Composable
fun ManageAccessScreen(
    onClickDropDownPermission: (copyUser, Permission) -> Unit,
    viewModel: ManageAccessViewModel = hiltViewModel(),
) {

    ManageAccessScreenContent(
        state = viewModel.state.collectAsState(),
        onClickDropDownPermission = viewModel::onClickDropDownPermission
    )


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageAccessScreenContent(
    modifier: Modifier = Modifier,
    state: State<ManageAccessState>,
    onClickDropDownPermission: (ShareHolder, Permission) -> Unit,
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text(text = "Manage Access") }) },
    ) {
        Column(Modifier.padding(it)) {
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn {
                items(state.value.peopleWithAcess) {
                    Card(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(0.7f)
                            ) {
                                if (it.profilePic != null) {
                                    AsyncImage(
                                        model = it.profilePic,
                                        contentDescription = "Profile picture",
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape),
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
                            Column(modifier = Modifier.weight(0.3f)) {
                                val expand = remember { mutableStateOf(false) }
                                TextButton(onClick = { expand.value = true }) {
                                    Text(text = it.permissionType)
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowDown,
                                        contentDescription = ""
                                    )
                                }
                                DropdownMenu(
                                    expanded = expand.value,
                                    onDismissRequest = { expand.value = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text(text = Permission.READ.toString()) },
                                        onClick = {
                                            onClickDropDownPermission(it, Permission.READ)
                                            expand.value = false
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text(text = Permission.WRITE.toString()) },
                                        onClick = {
                                            onClickDropDownPermission(it, Permission.WRITE)
                                            expand.value = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}


@Preview
@Composable
private fun prevllljj() {
    ManageAccessScreenContent(
        state = remember {
            mutableStateOf(
                ManageAccessState(
                    peopleWithAcess = listOf(
                        ShareHolder(
                            email = "email",
                            documentId = "documentId",
                            sharedId = "sharedId",
                            permissionType = Permission.READ.toString()

                        ),
                        ShareHolder(
                            email = "email",
                            documentId = "documentId",
                            sharedId = "sharedId",
                            permissionType = Permission.READ.toString()

                        ),
                        ShareHolder(
                            email = "email",
                            documentId = "documentId",
                            sharedId = "sharedId",
                            permissionType = Permission.READ.toString()

                        ),
                        ShareHolder(
                            email = "email",
                            documentId = "documentId",
                            sharedId = "sharedId",
                            permissionType = Permission.READ.toString()

                        ),
                        ShareHolder(
                            email = "email",
                            documentId = "documentId",
                            sharedId = "sharedId",
                            permissionType = Permission.READ.toString()

                        )
                    )
                )
            )
        },
        onClickDropDownPermission = { _, _ -> }
    )
}