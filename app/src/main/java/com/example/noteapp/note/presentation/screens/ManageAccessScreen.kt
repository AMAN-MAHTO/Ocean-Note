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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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
        onClickDropDownPermission = viewModel::onClickDropDownPermission,
        onClickPeople = viewModel::onClickPeople,
        onDismissRequestBottomSheet = viewModel::onDismissRequestBottomSheet
    )


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageAccessScreenContent(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    state: State<ManageAccessState>,
    onClickDropDownPermission: (ShareHolder, Permission) -> Unit,
    onClickPeople: (ShareHolder) -> Unit,
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

            if (state.value.showBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = onDismissRequestBottomSheet,
                    sheetState = sheetState,

                    ) {
                    // Sheet content
                    Column {
                        val it = state.value.selectedPeople
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
                                            .size(24.dp)
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


                                }


                            }
                        }
                        Divider()
                        
                    }
                    Button(onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                onDismissRequestBottomSheet()
//                                state.value.showBottomSheet = false
                            }
                        }
                    }) {
                        Text("Hide bottom sheet")
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
        onClickDropDownPermission = { _, _ -> },
        navHostController = rememberNavController(),
        onClickPeople = {},
        onDismissRequestBottomSheet = {}
    )
}