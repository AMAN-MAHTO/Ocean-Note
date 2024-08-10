package com.example.noteapp.auth.presentation.sign_in

import android.content.res.Configuration
import android.os.Build.VERSION.SDK_INT
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

import coil.request.ImageRequest
import com.example.compose.AppTheme
import com.example.noteapp.R
import kotlin.math.PI
import kotlin.math.sin
import kotlin.reflect.KFunction1

@Composable
fun SignIn(
    viewModel: SignInViewModel = hiltViewModel(),
    onGoogleSignIn: () -> Unit,
    onClickSignUpText: (String) -> Unit
) {
    val email = viewModel.email.collectAsState()
    val password = viewModel.password.collectAsState()
    val updateEmail = viewModel::updateEmail
    val updatePassword = viewModel::updatePassword
    val signIn = viewModel::signIn
    SignInContent(
        email.value,
        password.value,
        updatePassword,
        updateEmail,
        signIn,
        onGoogleSignIn,
        onClickSignUpText
    )

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


) {
    val infiniteTransition = rememberInfiniteTransition()
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
//        targetValue = 3 * PI.toFloat(),
        targetValue = 120f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                10000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ), label = ""
    )
    val color = MaterialTheme.colorScheme.onSurface

    Scaffold(
        modifier = Modifier
    ) {
        val colors = if (isSystemInDarkTheme()) {
            listOf(
                Color(0xFF0073A3), // Dark blue gradient start
                Color(0xFF005377), // Dark blue gradient middle
                Color(0xFF003F5C), // Dark blue gradient middle
                Color(0xFF002B3F)
            )
        } else {
            listOf(
                Color(0xFF72B8FF),
                Color(0xFF619FFF),
                Color(0xFF4C8CFF),
                Color(0xFF325899),
            )
        }

        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    val canvasWidth = size.width
                    val canvasHeight = size.height
                    val offsetY = canvasHeight / 1.4
                    val stringLength = canvasWidth

                    val e = vse(
                        L = stringLength,
                        c = listOf(
                            0.5,
                            1.0,
                            0.5,
                            1.0,
                            1.5,
                            1.0,
                            0.15,
                            2.0,
                            0.15,
                            1.0,
                            1.5,
                            1.0,
                            0.5,
                            1.0,
                            0.5
                        ),
                        v = 5f,
                    )
                    val e2 = vse(
                        L = stringLength,
                        c = listOf(1.0, 5.0, 1.0, 0.3, 0.0, 0.3, 2.0, 3.0, 2.0, 1.0, 2.0, 1.0),
                        v = 5f,

                        )
                    val e3 = vse(
                        L = stringLength,
                        c = listOf(
                            0.3, 0.5, 1.0, 1.5, 1.0, 0.15,
                            0.3, 0.5, 1.0, 1.5, 1.0, 0.15,
                            2.0, 0.15, 1.0, 2.0, 1.0, 0.0, 0.3,
                        ),
                        v = 5f,

                        )


                    val path = Path().apply {

                        moveTo(0f, canvasHeight)
                    }
                    val path2 = Path().apply {

                        moveTo(0f, canvasHeight)
                    }
                    val path3 = Path().apply {

                        moveTo(0f, canvasHeight)
                    }

                    for (x in 0..stringLength.toInt()) {
                        val ye = e.U(x.toFloat(), time) / 2 - offsetY
                        val curve = 50 * sin(x / canvasWidth * PI).toFloat()
                        path.lineTo(x.toFloat(), canvasHeight - curve + ye.toFloat())

                        val ye2 = e2.U(x.toFloat(), time) / 2 - offsetY

                        path2.lineTo(x.toFloat(), canvasHeight - curve - 10f + ye2.toFloat())

                        val ye3 = e3.U(x.toFloat(), time) / 2 - offsetY

                        path3.lineTo(x.toFloat(), canvasHeight - curve - 9f + ye3.toFloat())

                    }
                    path.lineTo(canvasWidth, canvasHeight)
                    path2.lineTo(canvasWidth, canvasHeight)
                    path3.lineTo(canvasWidth, canvasHeight)

                    drawPath(
                        color = colors[1],
                        path = path3,
                    )
                    drawPath(
                        color = colors[0],
                        path = path2,
                    )
                    drawPath(
                        brush = Brush.verticalGradient(
                            colors
                        ),
                        path = path,
                    )


                }
                .padding(it)
                .padding(24.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Ocean\nNotes",

                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
                modifier = Modifier.fillMaxWidth(1f),
                textAlign = TextAlign.Start,

                )


            Spacer(modifier = Modifier.height(4.dp))
            AnimatedJellyfish(Modifier.fillMaxWidth())

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
                    Icon(
                        painter = painterResource(id = R.drawable.google_icon),
                        contentDescription = "",
                        tint = Color.Unspecified,
                        modifier = Modifier
                            .size(24.dp)

                    )
                    Spacer(modifier = Modifier.width(8.dp))


                    Text(
                        text = "Continue with Google",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(8.dp),
                        textAlign = TextAlign.Center
                    )
                }

            }
            Spacer(modifier = Modifier.height(4.dp))


        }
    }

}


@Preview(showBackground = true)
@Composable
fun previewSignIn() {
    AppTheme {

        SignInContent(
            email = "",
            password = "",
            updatePassword = {},
            updateEmail = {},
            signIn = { },
            onGoogleSignIn = { }) {

        }
    }

}