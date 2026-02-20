package com.floraguard.ai.ui.screen

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(
    onImageCaptured: (Bitmap) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    var cameraLoading by remember { mutableStateOf(true) }
    var cameraError by remember { mutableStateOf<String?>(null) }
    val previewView = remember { PreviewView(context) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
        if (!granted) {
            cameraError = "Camera permission is required for image capture."
            cameraLoading = false
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri == null) {
            return@rememberLauncherForActivityResult
        }
        val bitmap = decodeUriToBitmap(context, uri)
        if (bitmap == null) {
            cameraError = "Unable to decode gallery image."
        } else {
            onImageCaptured(bitmap)
        }
    }

    LaunchedEffect(hasCameraPermission) {
        if (!hasCameraPermission) {
            cameraLoading = false
            return@LaunchedEffect
        }

        cameraLoading = true
        cameraError = null

        runCatching {
            val cameraProvider = ProcessCameraProvider.getInstance(context).get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }
            val capture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                capture
            )

            imageCapture = capture
            cameraLoading = false
        }.onFailure { error ->
            cameraError = "Unable to start camera: ${error.message}"
            cameraLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Leaf Capture") },
                navigationIcon = {
                    OutlinedButton(onClick = onBack) {
                        Text("Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        CameraContent(
            paddingValues = paddingValues,
            hasCameraPermission = hasCameraPermission,
            cameraLoading = cameraLoading,
            cameraError = cameraError,
            previewView = previewView,
            onRequestPermission = {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            },
            onCaptureImage = {
                captureImage(
                    context = context,
                    imageCapture = imageCapture,
                    onSuccess = onImageCaptured,
                    onError = { message -> cameraError = message }
                )
            },
            onUploadFromGallery = {
                galleryLauncher.launch("image/*")
            }
        )
    }
}

@Composable
private fun CameraContent(
    paddingValues: PaddingValues,
    hasCameraPermission: Boolean,
    cameraLoading: Boolean,
    cameraError: String?,
    previewView: PreviewView,
    onRequestPermission: () -> Unit,
    onCaptureImage: () -> Unit,
    onUploadFromGallery: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (!hasCameraPermission) {
            Text(
                text = "Camera permission is required to capture leaf images.",
                style = MaterialTheme.typography.bodyLarge
            )
            Button(onClick = onRequestPermission) {
                Text("Grant Camera Permission")
            }
            OutlinedButton(onClick = onUploadFromGallery) {
                Text("Upload from Gallery")
            }
            return
        }

        if (cameraLoading) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.size(40.dp))
                Text(text = "Starting camera...", modifier = Modifier.padding(top = 8.dp))
            }
        } else {
            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                factory = { previewView }
            )
        }

        cameraError?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                modifier = Modifier.weight(1f),
                onClick = onCaptureImage
            ) {
                Text("Capture")
            }
            OutlinedButton(
                modifier = Modifier.weight(1f),
                onClick = onUploadFromGallery
            ) {
                Text("Upload")
            }
        }
    }
}

private fun captureImage(
    context: Context,
    imageCapture: ImageCapture?,
    onSuccess: (Bitmap) -> Unit,
    onError: (String) -> Unit
) {
    if (imageCapture == null) {
        onError("Camera is not initialized yet.")
        return
    }

    val outputFile = File.createTempFile("floraguard_capture_", ".jpg", context.cacheDir)
    val outputOptions = ImageCapture.OutputFileOptions.Builder(outputFile).build()

    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                val bitmap = BitmapFactory.decodeFile(outputFile.absolutePath)
                if (bitmap == null) {
                    onError("Unable to decode captured image.")
                } else {
                    onSuccess(bitmap)
                }
                outputFile.delete()
            }

            override fun onError(exception: ImageCaptureException) {
                onError("Image capture failed: ${exception.message}")
                outputFile.delete()
            }
        }
    )
}

private fun decodeUriToBitmap(context: Context, uri: Uri): Bitmap? {
    return runCatching {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        } else {
            @Suppress("DEPRECATION")
            context.contentResolver.openInputStream(uri).use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            }
        }
    }.getOrNull()
}
