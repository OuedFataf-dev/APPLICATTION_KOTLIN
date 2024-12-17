package com.example.composerapp

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Patterns
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.text.input.VisualTransformation
import com.example.composerapp.ui.theme.ComposerAppTheme
//import androidx.compose.material3.icons.filled.Visibility
//import androidx.compose.material3.icons.filled.VisibilityOff
val auth = FirebaseAuth.getInstance()
val firestore = FirebaseFirestore.getInstance()

@Composable
fun RegisterScreen(
    navController: NavController = rememberNavController(),
    modifier: Modifier = Modifier,
    onSuccess: () -> Unit // Callback pour signaler une inscription réussie
) {
    val useremailState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }
   // var passwordState by remember { mutableStateOf("") }
    //var isPasswordVisible by remember { mutableStateOf(false) }
    val confirmPasswordState = remember { mutableStateOf("") }
   // var passwordState by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val handleSubmit = {
        when {
            useremailState.value.isEmpty() || passwordState.value.isEmpty() || confirmPasswordState.value.isEmpty() -> {
                errorMessage = "Tous les champs sont obligatoires."
            }
            !Patterns.EMAIL_ADDRESS.matcher(useremailState.value).matches() -> {
                errorMessage = "L'adresse e-mail est mal formée."
            }
            passwordState.value.length < 4 -> {
                errorMessage = "Le mot de passe doit contenir au moins 4 caractères."
            }
            passwordState.value != confirmPasswordState.value -> {
                errorMessage = "Les mots de passe ne correspondent pas."
            }
            else -> {
                // Créer l'utilisateur avec Firebase Auth
                auth.createUserWithEmailAndPassword(useremailState.value, passwordState.value)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Si l'inscription Firebase est réussie
                            val user = auth.currentUser
                            val userId = user?.uid ?: return@addOnCompleteListener

                            // Enregistrer l'utilisateur dans Firestore
                            val userMap = hashMapOf(
                                "username" to useremailState.value,
                                "email" to useremailState.value,
                                "created_at" to System.currentTimeMillis()
                            )

                            firestore.collection("users")
                                .document(userId)
                                .set(userMap)
                                .addOnSuccessListener {
                                    // Compte créé avec succès, appel du callback de succès
                                    errorMessage = "Compte créé avec succès."
                                    onSuccess() // Appel du callback pour naviguer vers la page de connexion
                                    // Après l'inscription réussie, on navigue vers la page de connexion
                                    navController.navigate("login") // Cette ligne navigue vers l'écran "login"
                                }
                                .addOnFailureListener { exception ->
                                    errorMessage = "Erreur lors de l'ajout dans Firestore: ${exception.message}"
                                }
                        } else {
                            // Si la création de l'utilisateur échoue
                            errorMessage = "Erreur lors de la création du compte : ${task.exception?.message}"
                        }
                    }
            }
        }
    }

    val context = LocalContext.current

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
            OutlinedTextField(
                value = useremailState.value,
                onValueChange = { useremailState.value = it },
                label = { Text("Nom d'utilisateur") },
                leadingIcon = {
                    Icon(Icons.Filled.Email, contentDescription = "Email Icon")
                },
                modifier = Modifier.fillMaxWidth()
            )

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


            OutlinedTextField(
                value = confirmPasswordState.value,
                onValueChange = { confirmPasswordState.value = it },
                label = { Text("Confirmer le mot de passe") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = if (errorMessage.contains("succès")) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Button(
                onClick = {
                    handleSubmit()
                    if (errorMessage == "Compte créé avec succès.") {
                        // Affichage du Toast et navigation
                        Toast.makeText(
                            context,
                            "Inscription réussie pour ${useremailState.value}",
                            Toast.LENGTH_SHORT
                        ).show()
                        navController.navigate("login")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("S'inscrire")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    ComposerAppTheme {
        RegisterScreen(onSuccess = {} )
    }
}
