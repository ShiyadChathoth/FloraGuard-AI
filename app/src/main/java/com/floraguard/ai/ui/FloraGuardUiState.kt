package com.floraguard.ai.ui

import android.graphics.Bitmap
import com.floraguard.ai.data.PlantCarePlan

data class FloraGuardUiState(
    val selectedImage: Bitmap? = null,
    val diagnosisLabel: String = "",
    val confidence: Float? = null,
    val carePlan: PlantCarePlan? = null,
    val isProcessing: Boolean = false,
    val errorMessage: String? = null
)
