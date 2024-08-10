package com.example.noteapp.auth.presentation.sign_in

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.noteapp.R
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AnimatedJellyfish(
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition()

    // Jellyfish floating animation
    val jellyfishOffsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 25f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    // Bubble rising animation
    val bubbleOffsetY by infiniteTransition.animateFloat(
        initialValue = 40f,
        targetValue = 25f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )



    Surface(modifier = modifier,color = Color.Transparent) {
        // Jellyfish animation
        Image(
            painter = painterResource(id = R.drawable.jellyfishbody),
            contentDescription = null,
            modifier = Modifier
                .offset(y = jellyfishOffsetY.dp)
                .padding(16.dp)
                .graphicsLayer(
                    translationY = jellyfishOffsetY,
                    alpha = 0.8f
                )
        )

        // Bubble animation
        Image(
            painter = painterResource(id = R.drawable.bubbles),
            contentDescription = null,
            modifier = Modifier
                .offset(y = bubbleOffsetY.dp)
                .padding(16.dp)
                .graphicsLayer(
                    translationY = bubbleOffsetY,
                    alpha = 0.6f
                )
        )


    }
}


@Composable
fun VibratingStringAnimation() {
    val infiniteTransition = rememberInfiniteTransition()
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
//        targetValue = 3 * PI.toFloat(),
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                10000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )
    val color = MaterialTheme.colorScheme.onSurface



    Canvas(modifier = Modifier.fillMaxSize()) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val stringLength = canvasWidth

        val e = vse(
            L = stringLength,
            c = listOf(0.5, 1.0, 0.5, 1.0, 1.5, 1.0, 0.15, 2.0, 0.15, 1.0, 1.5, 1.0, 0.5, 1.0, 0.5),

            )
        val e2 = vse(
            L = stringLength,
            c = listOf(1.0, 5.0, 1.0, 0.3, 0.0, 0.3, 2.0, 3.0, 2.0, 1.0, 2.0, 1.0),

            )
        val e3 = vse(
            L = stringLength,
            c = listOf(
                0.3, 0.5, 1.0, 1.5, 1.0, 0.15,
                0.3, 0.5, 1.0, 1.5, 1.0, 0.15,
                2.0, 0.15, 1.0, 2.0, 1.0, 0.0, 0.3,
            ),

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
            val ye = e.U(x.toFloat(), time) / 2
            val curve = 50 * sin(x / canvasWidth * PI).toFloat()
            path.lineTo(x.toFloat(), canvasHeight - curve + ye.toFloat())

            val ye2 = e2.U(x.toFloat(), time) / 2

            path2.lineTo(x.toFloat(), canvasHeight - curve - 10f + ye2.toFloat())

            val ye3 = e3.U(x.toFloat(), time) / 2

            path3.lineTo(x.toFloat(), canvasHeight - curve - 9f + ye3.toFloat())

        }
        path.lineTo(canvasWidth, canvasHeight)
        path2.lineTo(canvasWidth, canvasHeight)
        path3.lineTo(canvasWidth, canvasHeight)

        drawPath(
            color = Color(0xFF646464),
            path = path3,
//            style = Stroke(
//                2f
//            )
        )
        drawPath(
            color = Color(0xFF333333),
            path = path2,
//            style = Stroke(
//                2f
//            )
        )
        drawPath(
            color = Color(0xFF000000),
            path = path,
//            style = Stroke(
//                2f
//            )
        )


    }
}
class vse(
    private val c: List<Double> = listOf(0.5, 1.0, 0.5, 1.0, 1.5, 1.0, 0.15, 2.0, 0.15, 1.0, 1.5, 1.0, 0.5, 1.0, 0.5),
    private val v: Float = 10f,
    private val L:Float = 10f
){

    val N = c.sum()
    val Nm = c.size
    fun P(n: Int,x: Float,t: Float): Double{
        return sin((n* PI*x )/L)* cos((n* PI*v*t)/L)
    }

    fun U(x:Float,t:Float,amplitudeFactor:Float = 0f): Double{
        var total = 0.0

        for(n in 1..Nm){
            total += c[n-1]*P(n,x,t)
        }
        return (N + amplitudeFactor)* (total)
    }
}
