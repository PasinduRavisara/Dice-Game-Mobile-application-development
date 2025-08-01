package com.example.dicegame

// Android framework imports
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper

// Compose and Material imports
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Splash screen activity that shows before main content
class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the splash screen composable as content
        setContent {
            SplashScreen()
        }

        // Use handler to delay transition to main activity
        Handler(Looper.getMainLooper()).postDelayed({
            // Start main activity after delay
            startActivity(Intent(this, MainActivity::class.java))
            // Finish splash activity so user can't go back
            finish()
        }, 2000) // 2 second delay
    }
}

// Splash screen composable with animated elements
@Composable
fun SplashScreen() {
    // Create infinite animation for rotation effect
    val infiniteTransition = rememberInfiniteTransition(label = "rotation")

    // Animate rotation from 0 to 360 degrees continuously
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,       // Start at 0 degrees
        targetValue = 360f,      // Rotate to 360 degrees
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing), // 1 second rotation
            repeatMode = RepeatMode.Restart // Smooth restart
        ),
        label = "rotation" // Label for animation
    )

    // Full-screen container with gradient background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A1A1A),  // Very dark gray (top)
                        Color(0xFF000000),  // Pure black (middle)
                        Color(0xFF1A1A1A)   // Very dark gray (bottom)
                    )
                )
            ),
        contentAlignment = Alignment.Center // Center all content
    ) {
        // Vertical column layout for all elements
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Horizontal row for dice images
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp) // Space between dice
            ) {
                // First dice image with rotation animation
                Image(
                    painter = painterResource(id = R.drawable.dice_1),
                    contentDescription = "Dice 1",
                    modifier = Modifier
                        .size(100.dp) // Fixed size
                        .rotate(rotation) // Apply rotation animation
                )
                // Second dice image with rotation animation
                Image(
                    painter = painterResource(id = R.drawable.dice_1),
                    contentDescription = "Dice 2",
                    modifier = Modifier
                        .size(100.dp)
                        .rotate(rotation)
                )
            }

            // Vertical spacing between elements
            Spacer(modifier = Modifier.height(32.dp))

            // Loading text
            Text(
                text = "Loading...",
                color = Color(0xFFE0E0E0),  // Light gray for better visibility
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            // More vertical spacing
            Spacer(modifier = Modifier.height(16.dp))

            // Circular progress indicator
            CircularProgressIndicator(
                color = Color(0xFFE0E0E0),  // Light gray to match text
                modifier = Modifier.size(48.dp) // Larger than default
            )
        }
    }
}