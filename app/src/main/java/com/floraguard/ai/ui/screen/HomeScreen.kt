package com.floraguard.ai.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.foundation.clickable
import androidx.compose.material3.Divider
import androidx.compose.material3.Surface
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.floraguard.ai.ui.components.PlantLogo

private val careFeatures = listOf(
    "Treatment Recommendations",
    "Fertilizer Suggestions",
    "Water Requirements",
    "Sunlight Guidance",
    "Soil Suitability"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    manualDiseaseInput: String,
    onManualDiseaseInputChange: (String) -> Unit,
    manualPlantInput: String,
    onManualPlantInputChange: (String) -> Unit,
    plantSuggestions: List<String>,
    onOpenCamera: () -> Unit,
    onManualLookup: () -> Unit,
    onManualPlantLookup: () -> Unit,
    isDarkTheme: Boolean,
    onToggleDarkTheme: (Boolean) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("FloraGuard AI") },
                actions = {
                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = onToggleDarkTheme
                    )
                }
            )
        }
    ) { paddingValues ->
        HomeContent(
            paddingValues = paddingValues,
            manualDiseaseInput = manualDiseaseInput,
            onManualDiseaseInputChange = onManualDiseaseInputChange,
            manualPlantInput = manualPlantInput,
            onManualPlantInputChange = onManualPlantInputChange,
            plantSuggestions = plantSuggestions,
            onOpenCamera = onOpenCamera,
            onManualLookup = onManualLookup,
            onManualPlantLookup = onManualPlantLookup
        )
    }
}

@Composable
private fun HomeContent(
    paddingValues: PaddingValues,
    manualDiseaseInput: String,
    onManualDiseaseInputChange: (String) -> Unit,
    manualPlantInput: String,
    onManualPlantInputChange: (String) -> Unit,
    plantSuggestions: List<String>,
    onOpenCamera: () -> Unit,
    onManualLookup: () -> Unit,
    onManualPlantLookup: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Offline Plant Disease Detection & Care",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        PlantLogo(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp),
            animate = true
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
                    text = "Manual Plant Care Search",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                var isExpanded by remember { mutableStateOf(false) }
                val filteredSuggestions = remember(manualPlantInput, plantSuggestions) {
                    plantSuggestions
                        .filter { it.contains(manualPlantInput.trim(), ignoreCase = true) }
                        .take(6)
                }

                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = manualPlantInput,
                        onValueChange = {
                            onManualPlantInputChange(it)
                            isExpanded = it.isNotBlank()
                        },
                        label = { Text("Plant name (e.g., Snake Plant)") },
                        singleLine = true
                    )

                    if (isExpanded && filteredSuggestions.isNotEmpty()) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            tonalElevation = 2.dp,
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Column {
                                filteredSuggestions.forEachIndexed { index, suggestion ->
                                    Text(
                                        text = suggestion,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                onManualPlantInputChange(suggestion)
                                                isExpanded = false
                                            }
                                            .padding(horizontal = 12.dp, vertical = 10.dp)
                                    )
                                    if (index < filteredSuggestions.lastIndex) {
                                        Divider()
                                    }
                                }
                            }
                        }
                    }
                }

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = manualPlantInput.isNotBlank(),
                    onClick = onManualPlantLookup
                ) {
                    Text("Get Plant Care")
                }
            }
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

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Complete Care Features",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                careFeatures.forEach { feature ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.size(12.dp))
                        Text(
                            text = feature,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
