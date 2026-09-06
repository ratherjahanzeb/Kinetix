package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.MainViewModel
import com.example.R
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SplashScreen(
    viewModel: MainViewModel,
    navController: NavController,
    onboardingCompleted: Boolean?
) {
    val progress = remember { Animatable(0f) }

    LaunchedEffect(onboardingCompleted) {
        if (onboardingCompleted != null) {
            com.example.playShoooSound()
            // Start the squiggly circle animation
            progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 1500, easing = LinearEasing)
            )
            
            // Add a small pause at the end for visual completion
            delay(200)
            
            val startDest = "home"
            
            navController.navigate(startDest) {
                popUpTo("splash") { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(200.dp)
            ) {
                // The squiggly circle
                val primaryColor = MaterialTheme.colorScheme.primary
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val path = Path()
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val baseRadius = size.width / 2f - 24.dp.toPx()
                    val numSquiggles = 16
                    val squiggleAmplitude = 6.dp.toPx()

                    val endAngle = progress.value * 360f

                    if (endAngle > 0) {
                        for (angle in 0..endAngle.toInt()) {
                            val rad = Math.toRadians(angle.toDouble())
                            // Add sine wave to radius for squiggly effect
                            val currentRadius = baseRadius + (sin(rad * numSquiggles).toFloat() * squiggleAmplitude)
                            val x = center.x + currentRadius * cos(rad).toFloat()
                            val y = center.y + currentRadius * sin(rad).toFloat()

                            if (angle == 0) {
                                path.moveTo(x, y)
                            } else {
                                path.lineTo(x, y)
                            }
                        }

                        drawPath(
                            path = path,
                            color = primaryColor,
                            style = Stroke(
                                width = 6.dp.toPx(),
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                    }
                }

                // The real app icon
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(androidx.compose.ui.graphics.Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.app_logo),
                        contentDescription = "App Icon",
                        modifier = Modifier.size(96.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // The heart
            Icon(
                imageVector = androidx.compose.material.icons.Icons.Filled.Favorite,
                contentDescription = "Heart",
                tint = androidx.compose.ui.graphics.Color.Red,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
