package com.example.noteapp.note.presentation.screens

import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.noteapp.Permission
import com.example.noteapp.note.domain.models.ShareHolder
import com.example.noteapp.note.presentation.view_models.ManageAccessState
import com.example.noteapp.note.presentation.view_models.ManageAccessViewModel
import kotlinx.coroutines.launch

@Composable
fun ManageAccessScreen(
    navHostController: NavHostController,
    viewModel: ManageAccessViewModel = hiltViewModel(),
) {

    ManageAccessScreenContent(
        navHostController = navHostController,
        state = viewModel.state.collectAsState(),
        onClickPermission = viewModel::onClickPermission,
        onClickPeople = viewModel::onClickPeople,
        onDismissRequestBottomSheet = viewModel::onDismissRequestBottomSheet,
        onClickRemovePeople = viewModel::onClickRemovePeople
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageAccessScreenContent(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    state: State<ManageAccessState>,
    onClickPermission: (ShareHolder, Permission) -> Unit,
    onClickPeople: (ShareHolder) -> Unit,
    onClickRemovePeople: (ShareHolder) -> Unit,
    onDismissRequestBottomSheet: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "Manage Access") },
                navigationIcon = {
                    IconButton(onClick = { navHostController.popBackStack() }) {

                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "")
                    }
                })
        },
    ) {
        val sheetState = rememberModalBottomSheetState()
        val scope = rememberCoroutineScope()

        Column(Modifier.padding(it)) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "People with access",
                modifier = Modifier.padding(start = 32.dp),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            LazyColumn {
                items(state.value.peopleWithAcess) {
                    Card(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                            .clickable {
                                onClickPeople(it)
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
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
                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 16.dp)
                            ) {

                                if (it.email != null) {
                                    Text(
                                        text = it.email,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }

                                Text(
                                    text = it.permissionType,
                                    style = MaterialTheme.typography.bodyMedium
                                )

                            }


                        }
                    }
                }
            }


        }
        if (state.value.showBottomSheet) {
            ModalBottomSheet(
                dragHandle = {},
                onDismissRequest = onDismissRequestBottomSheet,
                sheetState = sheetState,

                ) {
                // Sheet content
                Column(
                    modifier = Modifier.padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = 16.dp,
                        bottom = 32.dp
                    ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {


                    val it = state.value.selectedPeople
                    if (it != null) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {

                            if (it.profilePic != null) {
                                AsyncImage(
                                    model = it.profilePic,
                                    contentDescription = "Profile picture",
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Fit
                                )
                            }


                            if (it.email != null) {
                                Text(
                                    text = it.email,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }


                        }




                        HorizontalDivider()

                        NavigationDrawerItem(
                            label = { Text("Read") },
                            onClick = { onClickPermission(it, Permission.READ) },
                            selected = it.permissionType == Permission.READ.toString(),
                            icon = {
                                if (it.permissionType == Permission.READ.toString()) {
                                    Icon(Icons.Outlined.Check, contentDescription = null)
                                } else {
                                    Icon(
                                        Icons.Default.CheckBoxOutlineBlank,
                                        contentDescription = null
                                    )
                                }
                            },
                        )
                        NavigationDrawerItem(
                            label = { Text("Write") },
                            onClick = { onClickPermission(it, Permission.WRITE) },
                            selected = it.permissionType == Permission.WRITE.toString(),
                            icon = {
                                if (it.permissionType == Permission.WRITE.toString()) {
                                    Icon(Icons.Outlined.Check, contentDescription = null)
                                } else {
                                    Icon(
                                        Icons.Default.CheckBoxOutlineBlank,
                                        contentDescription = null
                                    )
                                }
                            },
                        )
                        HorizontalDivider()
                        NavigationDrawerItem(
                            label = { Text("Remove") },
                            onClick = { onClickRemovePeople(it) },
                            selected = false,
                            icon = {
                                Icon(Icons.Default.Close, contentDescription = null)
                            }

                        )

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
        onClickPermission = { _, _ -> },
        navHostController = rememberNavController(),
        onClickPeople = {},
        onClickRemovePeople = {},
        onDismissRequestBottomSheet = {}
    )
}