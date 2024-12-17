package com.example.composerapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.composerapp.ui.theme.ComposerAppTheme

// === Page d'accueil (HomePage) Composable ===
@Composable
fun    HomePage(navController: NavController = rememberNavController(), modifier: Modifier = Modifier) {
    // Liste des images des cours
    val imagesCours = listOf(
        R.drawable.job,
        R.drawable.pdf,
        R.drawable.meal,
        R.drawable.js,
        R.drawable.about,
        R.drawable.tractor
    )

    // Liste des noms des cours
    val nomsCours = listOf(
        "Cours Informatique",
        "Cours Anglais",
        "Cours Mathématiques",
        "Cours Physique",
        "Cours Allemand",
        "Cours EPS"
    )

    Box(modifier = modifier.fillMaxSize()) {
        // Structure de la page avec la barre de recherche et les cartes
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(bottom = 56.dp), // Ajouter un padding en bas pour éviter que la navigation se chevauche
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Barre de recherche
            BarreDeRecherche()

            // Texte de bienvenue
            Text(
                text = "Bienvenue sur la page d'accueil",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Espacement pour centrer les cartes
            Spacer(modifier = Modifier.weight(1f))

            // Afficher les cartes dans une LazyColumn avec animation
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(nomsCours.size) { index ->
                    // Ajouter l'animation de visibilité à chaque carte
                    var visible by remember { mutableStateOf(true) }

                    // AnimatedVisibility entourant chaque CourseCard
                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
                        exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 })
                    ) {
                        CarteDeCours(
                            nomCours = nomsCours[index], // Passer le nom du cours
                            imageResourceId = imagesCours[index], // Passer l'image correspondante
                            onClick = {
                                // Si un cours spécifique est cliqué, naviguer vers la page du quiz
                                if (nomsCours[index] == "Cours Mathématiques") {
                                    navController.navigate("QUIZ") // Navigation vers la page MathsQuizPage
                                }
                            }
                        )
                    }
                }
            }

            // Espacement entre les cartes et le bas de l'écran
            Spacer(modifier = Modifier.weight(1f))
        }

        // Ajouter la barre de navigation en bas
        NavigationBas(modifier = Modifier.align(Alignment.BottomCenter))
    }
}

// === Carte de Cours (CourseCard) Composable ===
@Composable
fun CarteDeCours(
    nomCours: String, // Affichage du nom du cours
    imageResourceId: Int, // Affichage de l'image du cours
    onClick: () -> Unit = {} // Par défaut, aucune action de clic
) {
    val padding = 16.dp
    Column(
        Modifier
            .clickable(onClick = onClick) // On garde le clic mais il peut être vide
            .padding(padding)
            .fillMaxWidth()
    ) {
        // Image en haut de la carte
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        ) {
            Image(
                painter = painterResource(id = imageResourceId),
                contentDescription = nomCours,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(Modifier.height(padding))

        // Contenu de la carte
        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // Affichage du nom du cours dans le bas de la carte
            Text(
                text = nomCours, // Afficher le nom du cours ici
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

// === Barre de Recherche (SearchBar) Composable ===
@Composable
fun BarreDeRecherche(modifier: Modifier = Modifier) {
    TextField(
        value = "",
        onValueChange = {},
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null
            )
        },
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedContainerColor = MaterialTheme.colorScheme.surface
        ),
        placeholder = {
            Text(stringResource(R.string.placeholder_search)) // Assurez-vous que cette chaîne est définie dans les ressources
        },
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
    )
}

// === Navigation en bas (Bottom Navigation) Composable ===
@Composable
private fun NavigationBas(modifier: Modifier = Modifier) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier
    ) {
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = null
                )
            },
            label = {
                Text(stringResource(R.string.bottom_navigation_home))
            },
            selected = true,
            onClick = {}
        )
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null
                )
            },
            label = {
                Text(stringResource(R.string.bottom_navigation_home))
            },
            selected = true,
            onClick = {}
        )
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null
                )
            },
            label = {
                Text(stringResource(R.string.bottom_navigation_profile))
            },
            selected = false,
            onClick = {}
        )
    }
}

// === Prévisualisation ===
@Preview(showBackground = true)
@Composable
fun PrévisualisationParDéfaut() {
    ComposerAppTheme {
        HomePage() // Cela affichera la Page d'accueil avec les animations activées
    }
}
