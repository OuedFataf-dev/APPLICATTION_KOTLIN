package com.example.composerapp

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.composerapp.ui.theme.ComposerAppTheme
import androidx.navigation.compose.rememberNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
//import android.widget.Toast

@Composable
fun AddQuizPage(navController: NavController = rememberNavController()) {
    // Variables pour le formulaire
    var question by remember { mutableStateOf("") }
    var option1 by remember { mutableStateOf("") }
    var option2 by remember { mutableStateOf("") }
    var option3 by remember { mutableStateOf("") }
    var option4 by remember { mutableStateOf("") }
    var correctAnswer by remember { mutableStateOf("") }

    // Variable pour gérer l'affichage d'un message de succès après l'ajout
    var isSubmitted by remember { mutableStateOf(false) }

    // Obtention du contexte
    val context = LocalContext.current  // Corrected line

    // Initialisation de Firestore
    val db = FirebaseFirestore.getInstance()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Ajouter un nouveau quiz", style = MaterialTheme.typography.titleLarge)

        // Champ pour la question
        OutlinedTextField(
            value = question,
            onValueChange = { question = it },
            label = { Text("Question") },
            modifier = Modifier.fillMaxWidth()
        )

        // Champs pour les options
        OutlinedTextField(
            value = option1,
            onValueChange = { option1 = it },
            label = { Text("Option 1") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = option2,
            onValueChange = { option2 = it },
            label = { Text("Option 2") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = option3,
            onValueChange = { option3 = it },
            label = { Text("Option 3") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = option4,
            onValueChange = { option4 = it },
            label = { Text("Option 4") },
            modifier = Modifier.fillMaxWidth()
        )

        // Champ pour la réponse correcte
        OutlinedTextField(
            value = correctAnswer,
            onValueChange = { correctAnswer = it },
            label = { Text("Réponse correcte") },
            modifier = Modifier.fillMaxWidth()
        )

        // Bouton d'ajout de quiz
        Button(
            onClick = {
                // Logique d'ajout du quiz
                if (question.isNotEmpty() && option1.isNotEmpty() && option2.isNotEmpty() && option3.isNotEmpty() && option4.isNotEmpty() && correctAnswer.isNotEmpty()) {
                    // Créer un objet de quiz
                    val quiz = hashMapOf(
                        "question" to question,
                        "option1" to option1,
                        "option2" to option2,
                        "option3" to option3,
                        "option4" to option4,
                        "correctAnswer" to correctAnswer
                    )

                    // Ajouter le quiz à Firestore
                    db.collection("MathQuiz")
                        .add(quiz)
                        .addOnSuccessListener {
                            // Si l'ajout réussit
                            isSubmitted = true
                        }
                        .addOnFailureListener { e ->
                            // Si l'ajout échoue
                            Toast.makeText(context, "Erreur: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ajouter le Quiz")
        }

        // Affichage du message de succès
        LaunchedEffect(isSubmitted) {
            if (isSubmitted) {
                Toast.makeText(context, "Quiz ajouté avec succès!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}


// === Aperçu (Preview) ===
@Preview(showBackground = true)
@Composable
fun AjouterQuizPreview() {
    ComposerAppTheme {
        AddQuizPage()
    }
}
