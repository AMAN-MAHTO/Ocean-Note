package com.example.noteapp.components

import android.graphics.Paint.Align
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Preview
@Composable
fun tr868678y(modifier: Modifier = Modifier) {


    Scaffold(

        floatingActionButton = {

            CustomFloatingActionButton(onClick = {

            }, icon = Icons.Default.Add)
        }
    ) {
        Column(
            modifier = Modifier.padding(it)
        ) {

        }
    }
}

@Composable
fun CustomFloatingActionButton(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    onClick: () -> Unit,

    ) {
    val infiniteTransition = rememberInfiniteTransition(label = "")
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
    val color2 = MaterialTheme.colorScheme.primary
    val color1 = MaterialTheme.colorScheme.primaryContainer
    Box(
        modifier = Modifier
            .size(56.dp)
            .drawBehind {
                val canvasWidth = size.width
                val canvasHeight = size.height
                val stringLength = canvasWidth
                val l = mutableListOf(
                    0.5, 1.0, 0.5, 1.0, 1.5, 1.0, 0.15,
                    2.0, 0.15, 1.0, 1.5, 1.0
                )
                l.replaceAll {
                    it/2
                }
                val e = vse(
                    L = stringLength,
                    c = l,
                    v = 1f
                )


                val centerX = canvasWidth / 2
                val centerY = canvasHeight / 2
                val radius = minOf(canvasWidth, canvasHeight) / 2
                val numberOfSides: Int = 100
                val initialAngle: Float = 0f
                val path = Path()
                val angleStep = 2 * Math.PI / numberOfSides

                for (i in 0 until numberOfSides) {
                    val ye = e.U(i.toFloat(), time) / 2
                    val angle = initialAngle + i * angleStep
                    val x = centerX + (radius * cos(angle)).toFloat()
                    val y = centerY + (radius * sin(angle)).toFloat() + ye

                    if (i == 0) {
                        path.moveTo(x.toFloat(), y.toFloat())
                    } else {
                        path.lineTo(x.toFloat(), y.toFloat())
                    }
                }
                rotate(
                    degrees = -time / 2
                ) {

                    drawPath(
                        color = color1,
                        path = path,
                    )
                }
                rotate(
                    degrees = time / 2
                ) {

                    drawPath(
                        color = color2,
                        path = path,

                        )
                }


            }
            .clip(CircleShape)
            .clickable {
                onClick()
            },

        ) {
        Icon(
            imageVector = icon,
            contentDescription = "",
            modifier = Modifier.align(Alignment.Center),
            tint = MaterialTheme.colorScheme.surface
        )
    }
}

class vse(
    private val c: List<Double> = listOf(
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
    private val v: Float = 10f,
    private val L: Float = 10f
) {

    val N = c.sum()
    val Nm = c.size
    fun P(n: Int, x: Float, t: Float): Double {
        return sin((n * PI * x) / L) * cos((n * PI * v * t) / L)
    }

    fun U(x: Float, t: Float): Double {
        var total = 0.0

        for (n in 1..Nm) {
            total += c[n - 1] * P(n, x, t)
        }
        return (N) * (total)
    }
}
