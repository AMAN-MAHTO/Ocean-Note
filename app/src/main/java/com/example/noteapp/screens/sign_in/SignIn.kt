package com.example.noteapp.screens.sign_in

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.noteapp.R

@Composable
fun SignIn(
    viewModel: SignInViewModel = hiltViewModel(),
    onGoogleSignIn: ()-> Unit,
    onClickSignUpText: (String)->Unit
) {
    val email = viewModel.email.collectAsState()
    val password = viewModel.password.collectAsState()

    Box(
        Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            Modifier.fillMaxWidth(.8f)
        ) {
            Text(text = "Sign In",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.fillMaxWidth(1f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                singleLine = true,
                value = email.value,
                label = { Text(text = "Email") },
                modifier = Modifier.fillMaxWidth(1f),
                onValueChange = {
                    viewModel.updateEmail(it)
                })

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                value = password.value,
                label = { Text(text = "Password") },
                modifier = Modifier.fillMaxWidth(1f),
                onValueChange = {
                    viewModel.updatePassword(it)
                })


            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {viewModel.signIn()},
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.fillMaxWidth(1f)

            ) {
                Text(text = "Sign In")
            }
            Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick = {onGoogleSignIn()},
                shape = MaterialTheme.shapes.small,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ),
                modifier = Modifier.fillMaxWidth(1f)



            ) {
                Box(
                    Modifier.fillMaxWidth(1f)
                ) {
                    Icon(painter = painterResource(id = R.drawable.google_icon),
                        contentDescription = "",
                        tint = Color.Unspecified,
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.TopStart)
                    )


                    Text(text = "Sign in with Google", Modifier.align(Alignment.Center),
                        textAlign = TextAlign.Center
                    )
                }

            }
            Spacer(modifier = Modifier.height(4.dp))

            Text(text = "Don't have a account? Sign Up",
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { viewModel.navigateSignUp(onClickSignUpText) }
            )


        }
    }

}



@Preview(showBackground = true)
@Composable
fun previewSignIn() {
    SignIn(onGoogleSignIn = {}){
    }

}