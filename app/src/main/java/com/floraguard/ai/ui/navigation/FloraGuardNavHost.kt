package com.floraguard.ai.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.floraguard.ai.ui.FloraGuardViewModel
import com.floraguard.ai.ui.screen.CameraScreen
import com.floraguard.ai.ui.screen.HomeScreen
import com.floraguard.ai.ui.screen.ResultScreen

private object Destination {
    const val HOME = "home"
    const val CAMERA = "camera"
    const val RESULTS = "results"
}

@Composable
fun FloraGuardNavHost(viewModel: FloraGuardViewModel = viewModel()) {
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsState()
    var manualInput by rememberSaveable { mutableStateOf("") }

    NavHost(navController = navController, startDestination = Destination.HOME) {
        composable(Destination.HOME) {
            HomeScreen(
                manualDiseaseInput = manualInput,
                onManualDiseaseInputChange = { manualInput = it },
                onOpenCamera = {
                    viewModel.prepareForNewDiagnosis()
                    navController.navigate(Destination.CAMERA)
                },
                onManualLookup = {
                    viewModel.lookupDiseaseManually(manualInput)
                    navController.navigate(Destination.RESULTS)
                }
            )
        }

        composable(Destination.CAMERA) {
            CameraScreen(
                onImageCaptured = { bitmap ->
                    viewModel.processCapturedImage(bitmap)
                    navController.navigate(Destination.RESULTS)
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Destination.RESULTS) {
            ResultScreen(
                uiState = uiState,
                onBackHome = {
                    navController.popBackStack(Destination.HOME, false)
                },
                onDiagnoseAnother = {
                    viewModel.prepareForNewDiagnosis()
                    navController.navigate(Destination.CAMERA) {
                        popUpTo(Destination.HOME)
                    }
                }
            )
        }
    }
}
