

package com.example.composerapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.OptIn
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi

import androidx.navigation.compose.*

import com.example.composerapp.ui.theme.ComposerAppTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore




class MainActivity : ComponentActivity() {
    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialisation de Firebase
        FirebaseApp.initializeApp(this)

        // Vérification de l'initialisation de Firebase
        val firebaseApp = FirebaseApp.getInstance()
        if (firebaseApp != null) {
            Log.d("Firebase", "Firebase initialisé avec succès.")
        } else {
            Log.e("Firebase", "Erreur lors de l'initialisation de Firebase.")
        }

        enableEdgeToEdge()

        setContent {
            ComposerAppTheme {
                // NavHost for navigation between screens
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "splash") {

                    composable("login") {
                        LoginScreen(navController = navController)
                    }

                    composable("QUIZ") {
                        MathsQuizPage(navController = navController)
                    }
                    composable("Ajout") {
                        AddQuizPage(navController = navController)
                    }
                    composable("signup") {
                        RegisterScreen(navController = navController, onSuccess = {})
                    }
                    composable("home") {
                        HomePage(navController = navController)
                    }
                    composable("splash") {
                        SplashScreen(navController = navController)
                    }

                }
            }
        }
    }
}