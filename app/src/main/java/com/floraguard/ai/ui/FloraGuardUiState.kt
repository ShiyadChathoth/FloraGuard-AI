package com.floraguard.ai.ui

import android.graphics.Bitmap
import com.floraguard.ai.data.PlantCarePlan
import com.floraguard.ai.data.PlantCareProfile

data class FloraGuardUiState(
    val selectedImage: Bitmap? = null,
    val diagnosisLabel: String = "",
    val confidence: Float? = null,
    val carePlan: PlantCarePlan? = null,
    val plantCareProfile: PlantCareProfile? = null,
    val plantSuggestions: List<String> = emptyList(),
    val resultType: ResultType = ResultType.DIAGNOSIS,
    val isProcessing: Boolean = false,
    val errorMessage: String? = null
)

enum class ResultType {
    DIAGNOSIS,
    MANUAL_DISEASE,
    MANUAL_PLANT_CARE
}
