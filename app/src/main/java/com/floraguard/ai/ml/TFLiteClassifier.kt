package com.floraguard.ai.ml

import android.content.Context
import android.graphics.Bitmap
import java.io.Closeable
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel
import org.tensorflow.lite.Interpreter

class TFLiteClassifier(
    private val context: Context,
    private val modelAssetPath: String = "model.tflite",
    labelsAssetPath: String = "labels.txt"
) : Closeable {

    private val labels: List<String> = loadLabels(labelsAssetPath)
    private val resolvedModelAssetPath: String? = resolveModelAssetPath(modelAssetPath)
    private val interpreter: Interpreter? = createInterpreter()

    fun classify(bitmap: Bitmap): DiagnosisResult {
        val activeInterpreter = interpreter ?: return DiagnosisResult(
            label = "Model_Not_Available",
            confidence = 0f
        )

        val outputTensorShape = activeInterpreter.getOutputTensor(0).shape()
        val outputSize = outputTensorShape.lastOrNull() ?: labels.size.coerceAtLeast(1)
        val output = Array(1) { FloatArray(outputSize) }

        return try {
            activeInterpreter.run(preprocess(bitmap), output)
            val probabilities = output[0]
            val maxEntry = probabilities.withIndex().maxByOrNull { it.value }
            val bestIndex = maxEntry?.index ?: 0
            val label = labels.getOrElse(bestIndex) { "Unknown_Disease" }
            DiagnosisResult(label = label, confidence = maxEntry?.value ?: 0f)
        } catch (_: Exception) {
            DiagnosisResult(label = "Inference_Error", confidence = 0f)
        }
    }

    private fun preprocess(bitmap: Bitmap): ByteBuffer {
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, IMAGE_SIZE, IMAGE_SIZE, true)
        val inputBuffer = ByteBuffer
            .allocateDirect(BATCH_SIZE * IMAGE_SIZE * IMAGE_SIZE * PIXEL_SIZE * FLOAT_SIZE)
            .order(ByteOrder.nativeOrder())

        val pixelValues = IntArray(IMAGE_SIZE * IMAGE_SIZE)
        resizedBitmap.getPixels(pixelValues, 0, IMAGE_SIZE, 0, 0, IMAGE_SIZE, IMAGE_SIZE)

        pixelValues.forEach { pixel ->
            val red = (pixel shr 16 and 0xFF) / 255.0f
            val green = (pixel shr 8 and 0xFF) / 255.0f
            val blue = (pixel and 0xFF) / 255.0f

            inputBuffer.putFloat(red)
            inputBuffer.putFloat(green)
            inputBuffer.putFloat(blue)
        }

        inputBuffer.rewind()
        return inputBuffer
    }

    private fun createInterpreter(): Interpreter? {
        val activeModelPath = resolvedModelAssetPath ?: return null
        return try {
            val modelBuffer = loadModelBuffer(activeModelPath)
            val options = Interpreter.Options().apply {
                setNumThreads(4)
            }
            Interpreter(modelBuffer, options)
        } catch (_: Exception) {
            null
        }
    }

    private fun loadModelBuffer(assetPath: String): ByteBuffer {
        return try {
            val fileDescriptor = context.assets.openFd(assetPath)
            FileInputStream(fileDescriptor.fileDescriptor).channel.use { fileChannel ->
                fileChannel.map(
                    FileChannel.MapMode.READ_ONLY,
                    fileDescriptor.startOffset,
                    fileDescriptor.declaredLength
                )
            }
        } catch (_: Exception) {
            val modelBytes = context.assets.open(assetPath).use { input ->
                input.readBytes()
            }
            ByteBuffer.allocateDirect(modelBytes.size)
                .order(ByteOrder.nativeOrder())
                .apply {
                    put(modelBytes)
                    rewind()
                }
        }
    }

    private fun resolveModelAssetPath(preferredPath: String): String? {
        if (assetExists(preferredPath)) {
            return preferredPath
        }

        return try {
            context.assets.list("")?.firstOrNull { assetName ->
                assetName.endsWith(".tflite", ignoreCase = true)
            }
        } catch (_: Exception) {
            null
        }
    }

    private fun assetExists(assetPath: String): Boolean {
        return try {
            context.assets.open(assetPath).close()
            true
        } catch (_: Exception) {
            false
        }
    }

    private fun loadLabels(labelsAssetPath: String): List<String> {
        return try {
            context.assets.open(labelsAssetPath).bufferedReader().useLines { lines ->
                lines.map { it.trim() }
                    .filter { it.isNotEmpty() }
                    .toList()
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    override fun close() {
        interpreter?.close()
    }

    companion object {
        private const val IMAGE_SIZE = 224
        private const val BATCH_SIZE = 1
        private const val PIXEL_SIZE = 3
        private const val FLOAT_SIZE = 4
    }
}
