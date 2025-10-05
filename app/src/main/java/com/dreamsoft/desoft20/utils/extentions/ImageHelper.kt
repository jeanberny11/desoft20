package com.dreamsoft.desoft20.utils.extentions

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlin.experimental.or
import androidx.core.graphics.scale
import androidx.core.graphics.get
import kotlin.io.encoding.Base64

object ImageHelper {
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

    private fun scaleToMaxWidth(bitmap: Bitmap, maxWidth: Int): Bitmap {
        if (bitmap.width <= maxWidth) return bitmap
        val ratio = maxWidth.toFloat() / bitmap.width
        val newHeight = (bitmap.height * ratio).toInt()
        return bitmap.scale(maxWidth, newHeight, false)
    }
}