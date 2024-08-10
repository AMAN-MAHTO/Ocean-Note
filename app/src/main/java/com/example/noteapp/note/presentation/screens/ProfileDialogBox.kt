package com.example.noteapp.note.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.noteapp.auth.domain.model.UserData

@Composable
fun ProfileDialogBox(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    userData: UserData?,
    onClickLogout: () -> Unit
) {

    ProfileDialogBoxContent(
        onDismissRequest,
        userData,
        onClickLogout
    )

}

@Composable
fun ProfileDialogBoxContent(
    onDismissRequest: () -> Unit,
    userData: UserData?,
    onClickLogout: () -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier
                .fillMaxWidth(1f)
//                .padding(bottom = 16.dp)
        ) {

            Column(
                Modifier
                    .padding(
                        16.dp
                    )
                    .padding(top = 8.dp)
                    .fillMaxWidth(),
//                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (userData?.profilePictureUrl != null) {
                    AsyncImage(
                        model = userData.profilePictureUrl,
                        contentDescription = "Profile picture",
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
                if (userData?.username != null) {
                    Text(
                        text = userData.username,
                        style = MaterialTheme.typography.titleLarge
                    )
//                    Spacer(modifier = Modifier.height(16.dp))
                }
                if (userData?.email != null) {
                    Text(
                        text = userData.email,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                TextButton(
                    onClick = onClickLogout, modifier = Modifier.align(Alignment.End),
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Logout")
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = "",
                        modifier = Modifier.size(20.dp)
                    )

                }
            }
        }

    }
}

@Preview
@Composable
private fun prev323() {
    ProfileDialogBoxContent(
        onDismissRequest = { },
        userData = UserData("1", "AMAN MAHTO", "amahto848@gmail.com", "")
    ) {}
}