package com.example.noteapp.note.presentation.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.noteapp.auth.domain.model.UserData
import javax.inject.Inject

@Composable
fun ProfileDialogBox (
    modifier: Modifier = Modifier,
    onDismissRequest: ()->Unit,
    userData: UserData?,
    onClickLogout: ()->Unit
    ) {

    ProfileDialogBoxContent(
        onDismissRequest,
        userData,
        onClickLogout
    )

}

@Composable
fun ProfileDialogBoxContent(onDismissRequest: ()->Unit,userData: UserData?, onClickLogout: ()->Unit) {
    Dialog(onDismissRequest = onDismissRequest) {


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

                    Button(onClick = onClickLogout) {
                        Text("Logout")

                    }
                }
            }

    }
}

@Preview
@Composable
private fun prev323() {
    ProfileDialogBoxContent(onDismissRequest = { },
        userData = UserData("","AMAN MAHTO","amahto848@gmail.com","")
    ){}
}