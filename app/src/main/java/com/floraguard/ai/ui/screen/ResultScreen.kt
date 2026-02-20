package com.floraguard.ai.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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

            Text(
                text = "Soil Composition",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(text = plan.soilComposition)

            Text(
                text = "Watering Logic",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(text = plan.wateringLogic)

            Text(
                text = "Light Exposure",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(text = plan.lightExposure)

            Text(
                text = "Nutrient Guide",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(text = plan.nutrientGuide)
        }
    }
}
