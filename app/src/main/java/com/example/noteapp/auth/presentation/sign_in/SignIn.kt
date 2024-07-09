package com.example.noteapp.auth.presentation.sign_in

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.noteapp.R
import kotlin.reflect.KFunction1

@Composable
fun SignIn(
    viewModel: SignInViewModel = hiltViewModel(),
    onGoogleSignIn: ()-> Unit,
    onClickSignUpText: (String)->Unit
) {
    val email = viewModel.email.collectAsState()
    val password = viewModel.password.collectAsState()
    val updateEmail = viewModel::updateEmail
    val updatePassword = viewModel::updatePassword
    val signIn = viewModel::signIn
    SignInContent( email.value, password.value , updatePassword, updateEmail, signIn, onGoogleSignIn, onClickSignUpText)

}

@Composable
fun SignInContent(

    email: String,
    password: String,
    updatePassword: (it: String) -> Unit,
    updateEmail: (it: String) -> Unit,
    signIn: () -> Unit,
    onGoogleSignIn: () -> Unit,
    onClickSignUpText: (String) -> Unit


    ){

        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(text = "WELCOME",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.fillMaxWidth(1f),
                textAlign = TextAlign.Center
            )


            Spacer(modifier = Modifier.height(4.dp))
            Image(painter = painterResource(id = R.drawable.note_png),
                contentDescription = "",
                )

            Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick = onGoogleSignIn,
                shape = MaterialTheme.shapes.small,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ),
                modifier = Modifier.fillMaxWidth(1f)



            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .align(Alignment.Bottom)
                ) {
                    Icon(painter = painterResource(id = R.drawable.google_icon),
                        contentDescription = "",
                        tint = Color.Unspecified,
                        modifier = Modifier
                            .size(24.dp)

                    )
                    Spacer(modifier = Modifier.width(8.dp))


                    Text(text = "Continue with Google",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(8.dp),
                        textAlign = TextAlign.Center
                    )
                }

            }
            Spacer(modifier = Modifier.height(4.dp))




    }
}



@Preview(showBackground = true)
@Composable
fun previewSignIn() {
    SignInContent(
        email = "",
        password = "",
        updatePassword = {},
        updateEmail = {},
        signIn = { },
        onGoogleSignIn = {  }) {
        
    }

}