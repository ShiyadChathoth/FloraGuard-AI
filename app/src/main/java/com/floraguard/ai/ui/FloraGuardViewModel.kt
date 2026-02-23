package com.floraguard.ai.ui

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.floraguard.ai.data.FloraGuardDatabase
import com.floraguard.ai.data.PlantCareRepository
import com.floraguard.ai.image.LeafHeuristics
import com.floraguard.ai.ml.TFLiteClassifier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FloraGuardViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PlantCareRepository(
        FloraGuardDatabase.getInstance(application).plantCarePlanDao()
    )
    private val classifier = TFLiteClassifier(application)

    private val _uiState = MutableStateFlow(FloraGuardUiState())
    val uiState: StateFlow<FloraGuardUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.ensureSeedData()
        }
    }

    fun processCapturedImage(bitmap: Bitmap) {
        _uiState.update {
            it.copy(
                selectedImage = bitmap,
                isProcessing = true,
                errorMessage = null,
                carePlan = null
            )
        }

        viewModelScope.launch(Dispatchers.Default) {
            val heuristic = LeafHeuristics.analyze(bitmap)
            if (!heuristic.isLikelyLeaf) {
                _uiState.update {
                    it.copy(
                        diagnosisLabel = "",
                        confidence = null,
                        carePlan = null,
                        isProcessing = false,
                        errorMessage = "Image doesn't look like a leaf. Try a closer shot on a plain background."
                    )
                }
                return@launch
            }

            val diagnosis = classifier.classify(bitmap)
            val plan = repository.getPlanForDisease(diagnosis.label)
            _uiState.update {
                it.copy(
                    diagnosisLabel = diagnosis.label,
                    confidence = diagnosis.confidence,
                    carePlan = plan,
                    isProcessing = false,
                    errorMessage = when {
                        diagnosis.label == "Model_Not_Available" ->
                            "No TensorFlow Lite model found in assets. Add your trained .tflite model to app/src/main/assets and rebuild."

                        plan == null ->
                            "No offline care plan found for ${diagnosis.label}."

                        else -> null
                    }
                )
            }
        }
    }

    fun lookupDiseaseManually(userInput: String) {
        val query = userInput.trim()
        if (query.isEmpty()) {
            _uiState.update {
                it.copy(errorMessage = "Enter a disease name before lookup.")
            }
            return
        }

        _uiState.update {
            it.copy(
                selectedImage = null,
                diagnosisLabel = query,
                confidence = null,
                carePlan = null,
                isProcessing = true,
                errorMessage = null
            )
        }

        viewModelScope.launch {
            val plan = repository.getPlanForDisease(query)
            _uiState.update {
                it.copy(
                    carePlan = plan,
                    isProcessing = false,
                    errorMessage = if (plan == null) {
                        "No offline care plan found for $query."
                    } else {
                        null
                    }
                )
            }
        }
    }

    fun prepareForNewDiagnosis() {
        _uiState.update {
            FloraGuardUiState()
        }
    }

    override fun onCleared() {
        classifier.close()
        super.onCleared()
    }
}
