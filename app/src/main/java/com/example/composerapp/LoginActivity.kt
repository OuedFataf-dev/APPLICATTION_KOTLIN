package com.example.composerapp

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
//import androidx.compose.material3.icons.filled.Lock
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.composerapp.ui.theme.ComposerAppTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable

//import androidx.compose.material3.icons.filled.Email
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.text.input.VisualTransformation
import kotlinx.coroutines.delay

val loginAuth = FirebaseAuth.getInstance() // Renommer auth en loginAuth

@Composable
fun LoginScreen(navController: NavController = rememberNavController(), modifier: Modifier = Modifier) {
    // Utilisation de `remember` et `mutableStateOf` pour gérer l'état des champs
    val emailState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }
    // passwordState by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    // State pour les messages d'erreur et la réussite de la connexion
    var errorMessage by remember { mutableStateOf("") }
    var isLoginSuccessful by remember { mutableStateOf(false) }

    // State pour gérer l'animation du bouton
    var isButtonVisible by remember { mutableStateOf(false) }

    // Fonction pour gérer la soumission du formulaire
    val handleLogin = {
        // Validation des champs
        when {
            emailState.value.isEmpty() || passwordState.value.isEmpty() -> {
                errorMessage = "Tous les champs sont obligatoires."
            }
            passwordState.value.length < 4 -> {
                errorMessage = "Le mot de passe doit contenir au moins 4 caractères."
            }
            else -> {
                // Authentification Firebase
                loginAuth.signInWithEmailAndPassword(emailState.value, passwordState.value)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Connexion réussie
                            val user: FirebaseUser? = loginAuth.currentUser
                            errorMessage = "Connexion réussie."
                            isLoginSuccessful = true // Marque la réussite de la connexion
                            // Rediriger vers la page d'accueil
                            navController.navigate("home") {
                                popUpTo("login") { inclusive = true }
                            }
                        } else {
                            // Erreur de connexion
                            errorMessage = "Identifiants invalides. Veuillez vérifier votre email et votre mot de passe."
                        }
                    }
            }
        }
    }

    // Contrôler la visibilité du bouton après un délai
    LaunchedEffect(Unit) {
        // Attendre un moment avant de rendre le bouton visible
        delay(500)  // 500ms de délai avant l'animation
        isButtonVisible = true
    }

    // Affichage de la Box et du formulaire
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            // Champ Email
            OutlinedTextField(
                value = emailState.value,
                onValueChange = { emailState.value = it },
                label = { Text("Email") },
                leadingIcon = {
                    Icon(Icons.Filled.Email, contentDescription = "Email Icon")
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Champ Mot de passe
            OutlinedTextField(
                value = passwordState.value,
                onValueChange = { passwordState.value = it },
                label = { Text("Mot de passe") },
                //visualTransformation = PasswordVisualTransformation(),
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                leadingIcon = {
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = "Password visibility icon",
                        modifier = Modifier.clickable { isPasswordVisible = !isPasswordVisible }
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Message d'erreur ou de succès
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = if (errorMessage.contains("réussie")) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Animation sur le bouton Connexion
            AnimatedVisibility(
                visible = isButtonVisible,
                enter = fadeIn(animationSpec = tween(durationMillis = 1000)), // Animation d'entrée avec fondu
                exit = fadeOut(animationSpec = tween(durationMillis = 500)) // Animation de sortie (optionnelle)
            ) {
                Button(
                    onClick = {
                        handleLogin() // Appeler la fonction de connexion
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Se connecter")
                }
            }

            // Lien pour l'inscription si l'utilisateur n'a pas encore de compte
            TextButton(onClick = {
                navController.navigate("signup") // Naviguer vers l'écran d'inscription
            }) {
                Text("Pas encore de compte ? Inscrivez-vous")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    ComposerAppTheme {
        LoginScreen()
    }
}
