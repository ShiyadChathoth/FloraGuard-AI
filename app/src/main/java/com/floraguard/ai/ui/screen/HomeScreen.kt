package com.floraguard.ai.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    manualDiseaseInput: String,
    onManualDiseaseInputChange: (String) -> Unit,
    onOpenCamera: () -> Unit,
    onManualLookup: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("FloraGuard AI") }
            )
        }
    ) { paddingValues ->
        HomeContent(
            paddingValues = paddingValues,
            manualDiseaseInput = manualDiseaseInput,
            onManualDiseaseInputChange = onManualDiseaseInputChange,
            onOpenCamera = onOpenCamera,
            onManualLookup = onManualLookup
        )
    }
}

@Composable
private fun HomeContent(
    paddingValues: PaddingValues,
    manualDiseaseInput: String,
    onManualDiseaseInputChange: (String) -> Unit,
    onOpenCamera: () -> Unit,
    onManualLookup: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Offline Plant Disease Detection & Care",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Capture or upload a leaf image to run on-device diagnosis with TensorFlow Lite."
        )

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onOpenCamera
        ) {
            Text("Capture / Upload Leaf Image")
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Manual Offline Lookup",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = manualDiseaseInput,
                    onValueChange = onManualDiseaseInputChange,
                    label = { Text("Disease name (e.g., Tomato_Early_Blight)") },
                    singleLine = true
                )

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = manualDiseaseInput.isNotBlank(),
                    onClick = onManualLookup
                ) {
                    Text("Find Care Plan")
                }
            }
        }
    }
}
