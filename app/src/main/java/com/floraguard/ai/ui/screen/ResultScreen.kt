package com.floraguard.ai.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.Science
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import com.floraguard.ai.data.PlantCarePlan
import com.floraguard.ai.ui.FloraGuardUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    uiState: FloraGuardUiState,
    onBackHome: () -> Unit,
    onDiagnoseAnother: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Diagnosis Result") }
            )
        }
    ) { paddingValues ->
        ResultContent(
            paddingValues = paddingValues,
            uiState = uiState,
            onBackHome = onBackHome,
            onDiagnoseAnother = onDiagnoseAnother
        )
    }
}

@Composable
private fun ResultContent(
    paddingValues: PaddingValues,
    uiState: FloraGuardUiState,
    onBackHome: () -> Unit,
    onDiagnoseAnother: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (uiState.isProcessing) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.size(40.dp))
                Text(
                    text = "Running offline diagnosis...",
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            return
        }

        uiState.selectedImage?.let { bitmap ->
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Captured leaf image",
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 260.dp)
            )
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Detected Disease",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = uiState.diagnosisLabel.ifBlank { "No diagnosis yet" },
                    style = MaterialTheme.typography.headlineSmall
                )
                uiState.confidence?.let { score ->
                    Text(
                        text = "Confidence: ${(score * 100).toInt()}%",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        uiState.carePlan?.let { plan ->
            CarePlanSection(plan = plan)
        }

        uiState.errorMessage?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onDiagnoseAnother
        ) {
            Text("Diagnose Another Plant")
        }

        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = onBackHome
        ) {
            Text("Back to Home")
        }
    }
}

@Composable
private fun CarePlanSection(plan: PlantCarePlan) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Actionable Treatment",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(text = plan.treatmentSteps)

            PlantCareRecommendations(plan = plan)
        }
    }
}

@Composable
private fun PlantCareRecommendations(plan: PlantCarePlan) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Plant Care Recommendations",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                RecommendationCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Outlined.Eco,
                    title = "Soil Composition",
                    description = plan.soilComposition
                )
                RecommendationCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Outlined.WaterDrop,
                    title = "Watering Logic",
                    description = plan.wateringLogic
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                RecommendationCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Outlined.WbSunny,
                    title = "Light Exposure",
                    description = plan.lightExposure
                )
                RecommendationCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Outlined.Science,
                    title = "Nutrient Guide",
                    description = plan.nutrientGuide
                )
            }
        }
    }
}

@Composable
private fun RecommendationCard(
    modifier: Modifier,
    icon: ImageVector,
    title: String,
    description: String
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(28.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
