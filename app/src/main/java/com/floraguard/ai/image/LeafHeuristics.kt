package com.floraguard.ai.image

import android.graphics.Bitmap
import kotlin.math.abs

object LeafHeuristics {
    data class Result(
        val greenRatio: Float,
        val edgeDensity: Float,
        val isLikelyLeaf: Boolean
    )

    fun analyze(bitmap: Bitmap): Result {
        val safeBitmap = if (bitmap.config == Bitmap.Config.HARDWARE) {
            bitmap.copy(Bitmap.Config.ARGB_8888, false)
        } else {
            bitmap
        }

        val targetWidth = 256
        val scale = targetWidth.toFloat() / safeBitmap.width.coerceAtLeast(1)
        val targetHeight = (safeBitmap.height * scale).toInt().coerceAtLeast(1)
        val resized = if (safeBitmap.width > targetWidth) {
            Bitmap.createScaledBitmap(safeBitmap, targetWidth, targetHeight, true)
        } else {
            safeBitmap
        }

        val width = resized.width
        val height = resized.height
        val totalPixels = width * height
        if (totalPixels <= 0) {
            return Result(0f, 0f, false)
        }

        val pixels = IntArray(totalPixels)
        resized.getPixels(pixels, 0, width, 0, 0, width, height)

        var greenCount = 0
        val gray = IntArray(totalPixels)
        for (i in pixels.indices) {
            val color = pixels[i]
            val r = (color shr 16) and 0xFF
            val g = (color shr 8) and 0xFF
            val b = color and 0xFF

            if (g > 60 && g > r + 20 && g > b + 20) {
                greenCount++
            }

            gray[i] = (r * 30 + g * 59 + b * 11) / 100
        }

        val greenRatio = greenCount.toFloat() / totalPixels.toFloat()

        var edgeCount = 0
        if (width > 2 && height > 2) {
            for (y in 1 until height - 1) {
                val row = y * width
                for (x in 1 until width - 1) {
                    val idx = row + x
                    val g00 = gray[idx - width - 1]
                    val g01 = gray[idx - width]
                    val g02 = gray[idx - width + 1]
                    val g10 = gray[idx - 1]
                    val g12 = gray[idx + 1]
                    val g20 = gray[idx + width - 1]
                    val g21 = gray[idx + width]
                    val g22 = gray[idx + width + 1]

                    val gx = -g00 + g02 - 2 * g10 + 2 * g12 - g20 + g22
                    val gy = -g00 - 2 * g01 - g02 + g20 + 2 * g21 + g22
                    val magnitude = abs(gx) + abs(gy)
                    if (magnitude > 150) {
                        edgeCount++
                    }
                }
            }
        }

        val interiorPixels = ((width - 2) * (height - 2)).coerceAtLeast(1)
        val edgeDensity = edgeCount.toFloat() / interiorPixels.toFloat()

        val isLikelyLeaf = greenRatio >= 0.14f || (greenRatio >= 0.10f && edgeDensity >= 0.010f)

        return Result(greenRatio, edgeDensity, isLikelyLeaf)
    }
}
