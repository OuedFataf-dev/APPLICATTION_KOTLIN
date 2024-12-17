package com.example.composerapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.composerapp.ui.theme.ComposerAppTheme
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import androidx.compose.foundation.lazy.LazyColumn  // Import pour LazyColumn
import androidx.compose.foundation.lazy.items  // Import pour items

// === Composable de la page du quiz Mathématiques ===
@Composable
fun MathsQuizPage(navController: NavController = rememberNavController(), modifier: Modifier = Modifier) {

    // État pour gérer les données de quiz, erreurs, et progression
    var quizData by remember { mutableStateOf<List<QuizItem>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var currentProgress by remember { mutableStateOf(0f) }
    var loading by remember { mutableStateOf(true) }

    val coroutineScope = rememberCoroutineScope()

    // Fonction pour récupérer les quiz depuis Firestore
    fun fetchQuizData() {
        val db = FirebaseFirestore.getInstance()
        db.collection("MathQuiz")  // Remplacez "MathQuiz" par le nom de votre collection Firestore
            .get()
            .addOnSuccessListener { result ->
                val quizzes = result.map { document ->
                    // Récupérer les options séparées
                    val options = listOf(
                        document.getString("option1") ?: "",
                        document.getString("option2") ?: "",
                        document.getString("option3") ?: "",
                        document.getString("option4") ?: ""
                    )

                    // Créer un objet QuizItem
                    QuizItem(
                        question = document.getString("question") ?: "",
                        options = options,
                        correctAnswer = document.getString("correctAnswer") ?: ""
                    )
                }
                quizData = quizzes
                loading = false // Lorsque les données sont récupérées, arrêtez le chargement
            }
            .addOnFailureListener { exception ->
                Log.e("MathsQuizPage", "Erreur Firestore: ${exception.message}")
                errorMessage = "Erreur lors de la récupération des quiz: ${exception.message}"
                loading = false
            }
    }

    // Récupérer les quiz au démarrage et gérer la barre de progression
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            fetchQuizData() // Appel pour récupérer les données de Firestore
            // Simulation de la progression de chargement
            for (i in 1..100) {
                if (loading) {
                    currentProgress = i.toFloat() / 100
                    delay(50) // Délai pour simuler un chargement
                }
            }
        }
    }

    // Affichage des quiz ou d'un message d'erreur
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(bottom = 56.dp), // Ajouter un padding en bas pour éviter le chevauchement avec la navigation
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Affichage de la barre de progression en haut de la page
            LinearDeterminateIndicator(currentProgress = currentProgress)

            // Affichage du message d'erreur si nécessaire
            errorMessage?.let {
                Text(text = it, color = Color.Red, style = MaterialTheme.typography.bodyMedium)
            }

            // Texte de bienvenue
            Text(
                text = "Bienvenue sur la page de quiz",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            IconButton(
                onClick = {
                    // Naviguer vers la page d'ajout de quiz
                    navController.navigate("Ajout")
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add, // Icône par défaut d'ajout
                    contentDescription = "Ajouter un Quiz"
                )
            }

            // Espacement pour centrer les cartes
            Spacer(modifier = Modifier.weight(1f))

            // Affichage des cartes de quiz dans une LazyColumn
            if (quizData.isNotEmpty()) {
                LazyColumn(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(quizData.size) { index ->
                        QuizCard(quizItem = quizData[index])
                    }
                }
            }

            // Espacement entre les cartes et le bas de l'écran
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

// === Composable de la barre de progression déterminée ===
@Composable
fun LinearDeterminateIndicator(currentProgress: Float) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth().padding(16.dp)
    ) {
        LinearProgressIndicator(
            progress = currentProgress,
            modifier = Modifier.fillMaxWidth().height(8.dp),
        )
        // Optionnel : Afficher le pourcentage à côté de la barre de progression
        Text(
            text = "${(currentProgress * 100).toInt()}%",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

// === Composable pour afficher les cartes de quiz ===
@Composable
fun QuizCard(quizItem: QuizItem) {
    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    var isAnswerCorrect by remember { mutableStateOf<Boolean?>(null) }

    OutlinedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        border = BorderStroke(1.dp, Color.Black),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 200.dp) // Hauteur ajustée pour les questions
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Affichage de la question
            Text(
                text = quizItem.question,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Affichage des options sous forme de boutons
            quizItem.options.forEach { option ->
                val isSelected = option == selectedAnswer
                val backgroundColor = when {
                    isAnswerCorrect == true && option == quizItem.correctAnswer -> Color.Green
                    isAnswerCorrect == false && isSelected -> Color.Red
                    else -> MaterialTheme.colorScheme.primary
                }

                Button(
                    onClick = {
                        selectedAnswer = option
                        isAnswerCorrect = option == quizItem.correctAnswer
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                ) {
                    Text(text = option)
                }
            }

            // Affichage du message de feedback après sélection
            if (isAnswerCorrect != null) {
                Text(
                    text = if (isAnswerCorrect == true) "Bonne réponse !" else "Mauvaise réponse",
                    style = MaterialTheme.typography.bodyLarge.copy(color = if (isAnswerCorrect == true) Color.Green else Color.Red),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

// === Modèle de données pour QuizItem ===
data class QuizItem(
    val question: String,
    val options: List<String>,
    val correctAnswer: String
)

// === Aperçu (Preview) ===
@Preview(showBackground = true)
@Composable
fun MathsQuizPreview() {
    ComposerAppTheme {
        MathsQuizPage()
    }
}
