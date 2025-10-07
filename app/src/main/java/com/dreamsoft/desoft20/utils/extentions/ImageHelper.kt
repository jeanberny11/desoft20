package com.dreamsoft.desoft20.utils.extentions

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import kotlin.experimental.or
import androidx.core.graphics.scale
import androidx.core.graphics.get
import kotlin.io.encoding.Base64

object ImageHelper {
    private const val MAX_PRINTER_WIDTH = 384 // 58mm printer
    private const val MAX_IMAGE_SIZE_MB = 5

    /**
     * Decode Base64 directly to downsampled bitmap - much more memory efficient
     */
    fun base64ToBitmapOptimized(
        base64String: String?,
        maxWidth: Int = MAX_PRINTER_WIDTH
    ): Bitmap? {
        if (base64String.isNullOrEmpty()) return null

        val pureBase64 = base64String.substringAfter(",", base64String)

        return try {
            // Step 1: Decode to byte array
            val decodedBytes = Base64.decode(pureBase64)

            // Step 2: Check size before full decode
            if (decodedBytes.size > MAX_IMAGE_SIZE_MB * 1024 * 1024) {
                throw Exception("Image too large: ${decodedBytes.size / 1024}KB")
            }

            // Step 3: Get dimensions WITHOUT loading full image
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size, options)

            // Step 4: Calculate sample size
            val sampleSize = calculateInSampleSize(options, maxWidth)

            // Step 5: Decode with downsampling
            val decodeOptions = BitmapFactory.Options().apply {
                inSampleSize = sampleSize
                inPreferredConfig = Bitmap.Config.RGB_565 // 2 bytes/pixel vs 4
            }

            val bitmap = BitmapFactory.decodeByteArray(
                decodedBytes,
                0,
                decodedBytes.size,
                decodeOptions
            )

            // Step 6: Final scaling if needed
            if (bitmap != null && bitmap.width > maxWidth) {
                val ratio = maxWidth.toFloat() / bitmap.width
                val scaledHeight = (bitmap.height * ratio).toInt()
                val scaled = bitmap.scale(maxWidth, scaledHeight)

                // Recycle original if different
                if (scaled != bitmap) {
                    bitmap.recycle()
                }
                scaled
            } else {
                bitmap
            }

        } catch (e: OutOfMemoryError) {
            System.gc() // Suggest garbage collection
            throw e
        } catch (e: Exception) {
            throw e
        }
    }

    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int
    ): Int {
        val width = options.outWidth
        var inSampleSize = 1

        if (width > reqWidth) {
            val halfWidth = width / 2
            while ((halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    fun bitmapToEscPos(bitmap: Bitmap): ByteArray {
        val scaledBitmap = scaleToMaxWidth(bitmap, 384) // for 58mm printer, use 576 for 80mm
        val width = scaledBitmap.width
        val height = scaledBitmap.height

        val bytes = mutableListOf<Byte>()

        val widthBytes = (width + 7) / 8
        val densityByte: Byte = 33 // 24-dot density mode

        for (y in 0 until height step 24) {
            bytes.add(0x1B) // ESC
            bytes.add(0x2A) // '*'
            bytes.add(densityByte)
            bytes.add((widthBytes % 256).toByte()) // nL
            bytes.add((widthBytes / 256).toByte()) // nH

            for (x in 0 until width) {
                for (k in 0 until 3) {
                    var slice: Byte = 0
                    for (b in 0 until 8) {
                        val yPos = y + k * 8 + b
                        val pixel = if (yPos < height) scaledBitmap[x, yPos] else 0xFFFFFF
                        val r = (pixel shr 16) and 0xFF
                        val g = (pixel shr 8) and 0xFF
                        val b2 = pixel and 0xFF
                        val gray = (r + g + b2) / 3
                        if (gray < 128) {
                            slice = slice or (1 shl (7 - b)).toByte()
                        }
                    }
                    bytes.add(slice)
                }
            }
            bytes.add(0x0A) // New line after printing row block
        }

        return bytes.toByteArray()
    }

    fun base64ToBitmap(base64String: String?): Bitmap? {
        if (base64String.isNullOrEmpty()) {
            return null
        }
        // The Base64 string might include a prefix like "data:image/png;base64,".
        // We need to remove it before decoding.
        val pureBase64Encoded = if (base64String.contains(",")) {
            base64String.substring(base64String.indexOf(",") + 1)
        } else {
            base64String
        }

        return try {
            val decodedBytes = Base64.decode(pureBase64Encoded)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: IllegalArgumentException) {
            // Log error or handle it as needed
            e.printStackTrace()
            null
        } catch (e: Exception) {
            // Catch any other unexpected errors
            e.printStackTrace()
            null
        }
    }

    /**
     * More memory-efficient conversion
     */
    fun bitmapToEscPosOptimized(bitmap: Bitmap): ByteArray {
        // Don't scale again if already at correct size
        val workingBitmap = if (bitmap.width > MAX_PRINTER_WIDTH) {
            scaleToMaxWidth(bitmap, MAX_PRINTER_WIDTH).also {
                bitmap.recycle() // Clean up original
            }
        } else {
            bitmap
        }

        val width = workingBitmap.width
        val height = workingBitmap.height
        val bytes = mutableListOf<Byte>()
        val widthBytes = (width + 7) / 8

        // Process in chunks to avoid large array allocations
        for (y in 0 until height step 24) {
            bytes.add(0x1B) // ESC
            bytes.add(0x2A) // '*'
            bytes.add(33)   // 24-dot density
            bytes.add((widthBytes % 256).toByte())
            bytes.add((widthBytes / 256).toByte())

            for (x in 0 until width) {
                for (k in 0 until 3) {
                    var slice: Byte = 0
                    for (b in 0 until 8) {
                        val yPos = y + k * 8 + b
                        if (yPos < height) {
                            val pixel = workingBitmap[x, yPos]
                            val gray = (Color.red(pixel) + Color.green(pixel) + Color.blue(pixel)) / 3
                            if (gray < 128) {
                                slice = slice or (1 shl (7 - b)).toByte()
                            }
                        }
                    }
                    bytes.add(slice)
                }
            }
            bytes.add(0x0A)
        }

        workingBitmap.recycle() // Clean up
        return bytes.toByteArray()
    }

    private fun scaleToMaxWidth(bitmap: Bitmap, maxWidth: Int): Bitmap {
        if (bitmap.width <= maxWidth) return bitmap
        val ratio = maxWidth.toFloat() / bitmap.width
        val newHeight = (bitmap.height * ratio).toInt()
        return bitmap.scale(maxWidth, newHeight, false)
    }
}