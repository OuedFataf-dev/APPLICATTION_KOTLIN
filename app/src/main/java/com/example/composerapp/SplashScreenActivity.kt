package com.example.composerapp

import android.widget.ImageView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import androidx.compose.animation.core.* // Pour l'animation
import androidx.compose.foundation.background
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewParameter

@Composable
fun SplashScreen(navController: NavController) {

    // Utilisation de LaunchedEffect pour faire un délai
    LaunchedEffect(true) {

        // Attendre 3 secondes avant de naviguer vers l'écran de connexion
        delay(4000)

        navController.navigate("login") {
            popUpTo("login") { inclusive = true }
        }
    }

    // Animation du logo
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    // Affichage de l'écran de splash avec un fond dégradé
    Box(modifier = Modifier
        .fillMaxSize()
        .background(brush = Brush.verticalGradient(listOf(Color(0xFF4CAF50), Color(0xFF81C784))))) {

        // Affichage de l'image du logo avec animation de mise à l'échelle
        Image(
            painter = painterResource(id = R.drawable.scholarship),
            contentDescription = "Splash Logo",
            modifier = Modifier
                .align(Alignment.Center)
                .scale(scale) // Appliquer l'animation de mise à l'échelle
        )

        // Indicateur de chargement stylisé
        CircularProgressIndicator(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = 16.dp),
            color = Color.White, // Définir la couleur du chargement si nécessaire
            strokeWidth = 6.dp // Augmenter l'épaisseur de l'indicateur
        )

        // Ajouter un texte d'accueil
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 50.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Bienvenue dans ScholarApp!",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Chargement...",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Normal
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    val navController = rememberNavController() // Créer un NavController pour l'aperçu
    SplashScreen(navController = navController)
}
