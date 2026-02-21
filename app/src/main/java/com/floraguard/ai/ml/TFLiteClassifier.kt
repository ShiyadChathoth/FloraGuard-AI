package com.floraguard.ai.ml

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import java.io.Closeable
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel
import kotlin.math.roundToInt
import kotlin.math.max
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Tensor

class TFLiteClassifier(
    private val context: Context,
    private val modelAssetPath: String = "model.tflite",
    labelsAssetPath: String = "labels.txt"
) : Closeable {
    private val tag = "TFLiteClassifier"

    private val labels: List<String> = loadLabels(labelsAssetPath)
    private val resolvedModelAssetPath: String? = resolveModelAssetPath(modelAssetPath)
    private val interpreter: Interpreter? = createInterpreter()

    fun classify(bitmap: Bitmap): DiagnosisResult {
        val activeInterpreter = interpreter ?: return DiagnosisResult(
            label = "Model_Not_Available",
            confidence = 0f
        )

        return try {
            val inputTensor = activeInterpreter.getInputTensor(0)
            val outputTensor = activeInterpreter.getOutputTensor(0)
            val output = createOutputBuffer(outputTensor)
            activeInterpreter.run(preprocess(bitmap, inputTensor), output)
            val probabilities = decodeOutputProbabilities(outputTensor, output)
            val maxEntry = probabilities.withIndex().maxByOrNull { it.value }
            val bestIndex = maxEntry?.index ?: 0
            val label = labels.getOrElse(bestIndex) { "Unknown_Disease" }
            DiagnosisResult(label = label, confidence = maxEntry?.value ?: 0f)
        } catch (e: Exception) {
            Log.e(tag, "Inference failed", e)
            DiagnosisResult(label = "Inference_Error", confidence = 0f)
        }
    }

    private fun preprocess(bitmap: Bitmap, inputTensor: Tensor): ByteBuffer {
        val inputShape = inputTensor.shape()
        val imageSize = inputShape.getOrNull(1)?.takeIf { it > 0 } ?: IMAGE_SIZE
        val channels = inputShape.getOrNull(3)?.takeIf { it > 0 } ?: PIXEL_SIZE
        val sourceBitmap = if (bitmap.config == Bitmap.Config.HARDWARE) {
            bitmap.copy(Bitmap.Config.ARGB_8888, false)
        } else {
            bitmap
        }
        val resizedBitmap = Bitmap.createScaledBitmap(sourceBitmap, imageSize, imageSize, true)
        val inputBuffer = ByteBuffer
            .allocateDirect(inputTensor.numBytes())
            .order(ByteOrder.nativeOrder())

        val pixelValues = IntArray(imageSize * imageSize)
        resizedBitmap.getPixels(pixelValues, 0, imageSize, 0, 0, imageSize, imageSize)
        val isFloatInput = inputTensor.dataType() == DataType.FLOAT32

        pixelValues.forEach { pixel ->
            val red = pixel shr 16 and 0xFF
            val green = pixel shr 8 and 0xFF
            val blue = pixel and 0xFF

            if (isFloatInput) {
                when (channels) {
                    1 -> inputBuffer.putFloat((red + green + blue) / 3.0f)
                    3 -> {
                        inputBuffer.putFloat(red.toFloat())
                        inputBuffer.putFloat(green.toFloat())
                        inputBuffer.putFloat(blue.toFloat())
                    }
                    4 -> {
                        inputBuffer.putFloat(red.toFloat())
                        inputBuffer.putFloat(green.toFloat())
                        inputBuffer.putFloat(blue.toFloat())
                        inputBuffer.putFloat(255.0f)
                    }
                    else -> {
                        repeat(channels) { channel ->
                            val value = when (channel % 3) {
                                0 -> red
                                1 -> green
                                else -> blue
                            }
                            inputBuffer.putFloat(value.toFloat())
                        }
                    }
                }
            } else {
                val params = inputTensor.quantizationParams()
                val scale = if (params.scale == 0f) 1f else params.scale
                val zeroPoint = params.zeroPoint
                val inputType = inputTensor.dataType()
                val values = when (channels) {
                    1 -> intArrayOf((red + green + blue) / 3)
                    3 -> intArrayOf(red, green, blue)
                    4 -> intArrayOf(red, green, blue, 255)
                    else -> IntArray(channels) { channel ->
                        when (channel % 3) {
                            0 -> red
                            1 -> green
                            else -> blue
                        }
                    }
                }
                values.forEach { channelValue ->
                    val quantized = (channelValue / scale + zeroPoint).roundToInt()
                    val clamped = when (inputType) {
                        DataType.INT8 -> quantized.coerceIn(-128, 127)
                        else -> quantized.coerceIn(0, 255)
                    }
                    inputBuffer.put(clamped.toByte())
                }
            }
        }

        inputBuffer.rewind()
        return inputBuffer
    }

    private fun createOutputBuffer(outputTensor: Tensor): ByteBuffer {
        return ByteBuffer.allocateDirect(outputTensor.numBytes()).order(ByteOrder.nativeOrder())
    }

    private fun decodeOutputProbabilities(outputTensor: Tensor, output: ByteBuffer): FloatArray {
        output.rewind()
        val outputSize = outputTensor.shape().fold(1) { acc, dim -> max(acc * dim, 1) }
        return when (outputTensor.dataType()) {
            DataType.FLOAT32 -> {
                FloatArray(outputSize) { output.float }
            }
            DataType.UINT8, DataType.INT8 -> {
                val params = outputTensor.quantizationParams()
                val scale = if (params.scale == 0f) 1f else params.scale
                val zeroPoint = params.zeroPoint
                val isSigned = outputTensor.dataType() == DataType.INT8
                FloatArray(outputSize) { index ->
                    val raw = output.get(index).toInt()
                    val quantized = if (isSigned) raw else raw and 0xFF
                    (quantized - zeroPoint) * scale
                }
            }
            else -> {
                FloatArray(outputSize) { 0f }
            }
        }
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
        private const val PIXEL_SIZE = 3
    }
}
