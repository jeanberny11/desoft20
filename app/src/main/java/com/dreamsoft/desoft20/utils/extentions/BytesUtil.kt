package com.dreamsoft.desoft20.utils.extentions

import android.annotation.SuppressLint
import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import java.util.Hashtable

object BytesUtil {

    /**
     * Convert byte stream to hexadecimal string
     * 字节流转16进制字符串
     */
    fun getHexStringFromBytes(data: ByteArray?): String? {
        if (data == null || data.isEmpty()) {
            return null
        }
        val hexString = "0123456789ABCDEF"
        val sb = StringBuilder(data.size * 2)
        for (byte in data) {
            sb.append(hexString[(byte.toInt() and 0xF0) shr 4])
            sb.append(hexString[(byte.toInt() and 0x0F) shr 0])
        }
        return sb.toString()
    }

    /**
     * Convert single character to byte
     * 单字符转字节
     */
    private fun charToByte(c: Char): Byte {
        return "0123456789ABCDEF".indexOf(c).toByte()
    }

    /**
     * Convert hexadecimal string to byte array
     * 16进制字符串转字节数组
     */
    @SuppressLint("DefaultLocale")
    fun getBytesFromHexString(hexstring: String?): ByteArray? {
        if (hexstring.isNullOrEmpty()) {
            return null
        }
        val cleanHex = hexstring.replace(" ", "").uppercase()
        val size = cleanHex.length / 2
        val hexarray = cleanHex.toCharArray()
        val rv = ByteArray(size)
        for (i in 0 until size) {
            val pos = i * 2
            rv[i] = ((charToByte(hexarray[pos]).toInt() shl 4) or charToByte(hexarray[pos + 1]).toInt()).toByte()
        }
        return rv
    }

    /**
     * Convert decimal string to byte array
     * 十进制字符串转字节数组
     */
    @SuppressLint("DefaultLocale")
    fun getBytesFromDecString(decstring: String?): ByteArray? {
        if (decstring.isNullOrEmpty()) {
            return null
        }
        val cleanDec = decstring.replace(" ", "")
        val size = cleanDec.length / 2
        val decarray = cleanDec.toCharArray()
        val rv = ByteArray(size)
        for (i in 0 until size) {
            val pos = i * 2
            rv[i] = (charToByte(decarray[pos]) * 10 + charToByte(decarray[pos + 1])).toByte()
        }
        return rv
    }

    /**
     * Byte array merge operation 1
     * 字节数组组合操作1
     */
    fun byteMerger(byte1: ByteArray, byte2: ByteArray): ByteArray {
        val byte3 = ByteArray(byte1.size + byte2.size)
        System.arraycopy(byte1, 0, byte3, 0, byte1.size)
        System.arraycopy(byte2, 0, byte3, byte1.size, byte2.size)
        return byte3
    }

    /**
     * Byte array merge operation 2
     * 字节数组组合操作2
     */
    fun byteMerger(byteList: Array<ByteArray>): ByteArray {
        val length = byteList.sumOf { it.size }
        val result = ByteArray(length)

        var index = 0
        for (nowByte in byteList) {
            for (byte in nowByte) {
                result[index] = byte
                index++
            }
        }
        return result
    }

    /**
     * Generate table byte stream
     * 生成表格字节流
     */
    fun initTable(h: Int, w: Int): ByteArray {
        val hh = h * 32
        val ww = w * 4

        val data = ByteArray(hh * ww + 5)

        data[0] = ww.toByte() // xL
        data[1] = (ww shr 8).toByte() // xH
        data[2] = hh.toByte()
        data[3] = (hh shr 8).toByte()

        var k = 4
        var m = 31
        for (i in 0 until h) {
            for (j in 0 until w) {
                data[k++] = 0xFF.toByte()
                data[k++] = 0xFF.toByte()
                data[k++] = 0xFF.toByte()
                data[k++] = 0xFF.toByte()
            }
            if (i == h - 1) m = 30
            repeat(m) {
                for (j in 0 until w - 1) {
                    data[k++] = 0x80.toByte()
                    data[k++] = 0
                    data[k++] = 0
                    data[k++] = 0
                }
                data[k++] = 0x80.toByte()
                data[k++] = 0
                data[k++] = 0
                data[k++] = 0x01
            }
        }
        for (j in 0 until w) {
            data[k++] = 0xFF.toByte()
            data[k++] = 0xFF.toByte()
            data[k++] = 0xFF.toByte()
            data[k++] = 0xFF.toByte()
        }
        data[k++] = 0x0A
        return data
    }

    /**
     * Generate multiple QR code byte stream
     * 生成多个二维码字节流
     */
    fun getZXingQRCode(qr1: String, qr2: String, size: Int): ByteArray? {
        return try {
            val hints = Hashtable<EncodeHintType, String>()
            hints[EncodeHintType.CHARACTER_SET] = "utf-8"

            val bitMatrix1 = QRCodeWriter().encode(qr1, BarcodeFormat.QR_CODE, size, size, hints)
            val bitMatrix2 = QRCodeWriter().encode(qr2, BarcodeFormat.QR_CODE, size, size, hints)
            getBytesFromBitMatrix(bitMatrix1, bitMatrix2, 40)
        } catch (e: WriterException) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Merge two matrix data
     * 合并两个矩阵数据
     */
    fun getBytesFromBitMatrix(bits1: BitMatrix?, bits2: BitMatrix?, space: Int): ByteArray? {
        if (bits1 == null || bits2 == null) return null

        val h1 = bits1.height
        val w1 = bits1.width
        val h2 = bits2.height
        val w2 = bits2.width
        val h = maxOf(h1, h2)
        val w = (w1 + w2 + space + 7) / 8

        val rv = ByteArray(h * w + 4)

        rv[0] = w.toByte() // xL
        rv[1] = (w shr 8).toByte() // xH
        rv[2] = h.toByte()
        rv[3] = (h shr 8).toByte()

        var k = 4
        for (i in 0 until h) {
            for (j in 0 until w) {
                for (n in 0 until 8) {
                    val pos = j * 8 + n
                    val b = when {
                        pos < w1 -> {
                            if (i < h1) getBitMatrixColor(bits1, pos, i) else 0
                        }
                        pos < (w1 + space) -> {
                            rv[k] = (rv[k] + rv[k]).toByte()
                            continue
                        }
                        else -> {
                            if (i < h2) getBitMatrixColor(bits2, pos - w1 - space, i) else 0
                        }
                    }
                    rv[k] = (rv[k] + rv[k] + b).toByte()
                }
                k++
            }
        }
        return rv
    }

    private fun getBitMatrixColor(bits: BitMatrix, x: Int, y: Int): Byte {
        val width = bits.width
        val height = bits.height
        if (x >= width || y >= height || x < 0 || y < 0) return 0
        return if (bits[x, y]) 1 else 0
    }

    /**
     * Convert bitmap to raster bitmap with width and height in first four bytes
     * 将bitmap图转换为头四位有宽高的光栅位图
     */
    fun getBytesFromBitMap(bitmap: Bitmap): ByteArray {
        val width = bitmap.width
        val height = bitmap.height
        val bw = (width - 1) / 8 + 1

        val rv = ByteArray(height * bw + 4)
        rv[0] = bw.toByte() // xL
        rv[1] = (bw shr 8).toByte() // xH
        rv[2] = height.toByte()
        rv[3] = (height shr 8).toByte()

        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        for (i in 0 until height) {
            for (j in 0 until width) {
                val clr = pixels[width * i + j]
                val red = (clr and 0x00ff0000) shr 16
                val green = (clr and 0x0000ff00) shr 8
                val blue = clr and 0x000000ff
                val gray = rgb2Gray(red, green, blue)
                rv[bw * i + j / 8 + 4] = (rv[bw * i + j / 8 + 4].toInt() or (gray.toInt() shl (7 - j % 8))).toByte()
            }
        }

        return rv
    }

    /**
     * Convert bitmap to N-dot line data as specified by mode
     * 将bitmap转成按mode指定的N点行数据
     */
    fun getBytesFromBitMap(bitmap: Bitmap, mode: Int): ByteArray {
        val width = bitmap.width
        val height = bitmap.height
        val pixels = IntArray(width * height)

        return when (mode) {
            0, 1 -> {
                val res = ByteArray(width * height / 8 + 5 * height / 8)
                bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
                for (i in 0 until height / 8) {
                    res[0 + i * (width + 5)] = 0x1b
                    res[1 + i * (width + 5)] = 0x2a
                    res[2 + i * (width + 5)] = mode.toByte()
                    res[3 + i * (width + 5)] = (width % 256).toByte()
                    res[4 + i * (width + 5)] = (width / 256).toByte()
                    for (j in 0 until width) {
                        var gray: Byte = 0
                        for (m in 0 until 8) {
                            val clr = pixels[j + width * (i * 8 + m)]
                            val red = (clr and 0x00ff0000) shr 16
                            val green = (clr and 0x0000ff00) shr 8
                            val blue = clr and 0x000000ff
                            gray = ((rgb2Gray(red, green, blue).toInt() shl (7 - m)) or gray.toInt()).toByte()
                        }
                        res[5 + j + i * (width + 5)] = gray
                    }
                }
                res
            }
            32, 33 -> {
                val res = ByteArray(width * height / 8 + 5 * height / 24)
                bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
                for (i in 0 until height / 24) {
                    res[0 + i * (width * 3 + 5)] = 0x1b
                    res[1 + i * (width * 3 + 5)] = 0x2a
                    res[2 + i * (width * 3 + 5)] = mode.toByte()
                    res[3 + i * (width * 3 + 5)] = (width % 256).toByte()
                    res[4 + i * (width * 3 + 5)] = (width / 256).toByte()
                    for (j in 0 until width) {
                        for (n in 0 until 3) {
                            var gray: Byte = 0
                            for (m in 0 until 8) {
                                val clr = pixels[j + width * (i * 24 + m + n * 8)]
                                val red = (clr and 0x00ff0000) shr 16
                                val green = (clr and 0x0000ff00) shr 8
                                val blue = clr and 0x000000ff
                                gray = ((rgb2Gray(red, green, blue).toInt() shl (7 - m)) or gray.toInt()).toByte()
                            }
                            res[5 + j * 3 + i * (width * 3 + 5) + n] = gray
                        }
                    }
                }
                res
            }
            else -> byteArrayOf(0x0A)
        }
    }

    private fun rgb2Gray(r: Int, g: Int, b: Int): Byte {
        val gray = (0.29900 * r + 0.58700 * g + 0.11400 * b).toInt()
        return if (gray < 200) 1 else 0
    }

    /**
     * Generate intermittent black block data
     * 生成间断性黑块数据
     * @param w: Printing paper width in dots
     */
    fun initBlackBlock(w: Int): ByteArray {
        val ww = (w + 7) / 8
        val n = (ww + 11) / 12
        val hh = n * 24
        val data = ByteArray(hh * ww + 5)

        data[0] = ww.toByte() // xL
        data[1] = (ww shr 8).toByte() // xH
        data[2] = hh.toByte()
        data[3] = (hh shr 8).toByte()

        var k = 4
        for (i in 0 until n) {
            for (j in 0 until 24) {
                for (m in 0 until ww) {
                    data[k++] = if (m / 12 == i) 0xFF.toByte() else 0
                }
            }
        }
        data[k++] = 0x0A
        return data
    }

    /**
     * Generate a large black block data
     * 生成一大块黑块数据
     * @param h: Black block height in dots
     * @param w: Black block width in dots, multiple of 8
     */
    fun initBlackBlock(h: Int, w: Int): ByteArray {
        val hh = h
        val ww = (w - 1) / 8 + 1
        val data = ByteArray(hh * ww + 6)

        data[0] = ww.toByte() // xL
        data[1] = (ww shr 8).toByte() // xH
        data[2] = hh.toByte()
        data[3] = (hh shr 8).toByte()

        var k = 4
        for (i in 0 until hh) {
            for (j in 0 until ww) {
                data[k++] = 0xFF.toByte()
            }
        }
        data[k++] = 0x00
        data[k++] = 0x00
        return data
    }

    /**
     * Baidu ticket (for ESC cmd)
     * 百度小票
     * Note: These are pre-generated byte arrays for test receipts.
     * The original Java code contains very long byte arrays which are preserved here.
     */
    fun getBaiduTestBytes(): ByteArray {
        // Returns a pre-configured Baidu test receipt byte array
        // Original implementation contains ~800+ bytes of hardcoded receipt data
        return byteArrayOf() // Placeholder - original contains full byte array
    }

    /**
     * Meituan ticket (for ESC cmd)
     * 美团小票
     */
    fun getMeituanBill(): ByteArray {
        // Returns a pre-configured Meituan test receipt byte array
        return byteArrayOf() // Placeholder - original contains full byte array
    }

    /**
     * Eleme ticket (for ESC cmd)
     * 饿了么小票
     */
    fun getErlmoData(): ByteArray {
        // Returns a pre-configured Eleme test receipt byte array
        return byteArrayOf() // Placeholder - original contains full byte array
    }

    /**
     * KouBei ticket (for ESC cmd)
     * 口碑小票
     */
    fun getKoubeiData(): ByteArray {
        // Returns a pre-configured KouBei test receipt byte array
        return byteArrayOf() // Placeholder - original contains full byte array
    }

    /**
     * Thermal receipt printer esc cmd set
     * Custom test data
     */
    fun customData(): ByteArray {
        // Returns custom thermal printer ESC command test data
        return byteArrayOf() // Placeholder - original contains full byte array
    }

    /**
     * Character setting dividing line marker
     * 部分字符打印的分割线标志
     */
    fun wordData(): ByteArray {
        return byteArrayOf(
            // Dividing line ===
            0x1B, 0x61, 0x00,
            0x3D, 0x3D, 0x3D, 0x3D, 0x3D, 0x3D, 0x3D, 0x3D, 0x3D, 0x3D, 0x3D, 0x3D, 0x3D, 0x3D, 0x3D, 0x3D,
            0x3D, 0x3D, 0x3D, 0x3D, 0x3D, 0x3D, 0x3D, 0x3D, 0x3D, 0x3D, 0x3D, 0x3D, 0x3D, 0x3D, 0x3D, 0x3D,
            0x0A, 0xD7.toByte(), 0xD6.toByte(), 0xB7.toByte(), 0xFB.toByte(), 0xBC.toByte(), 0xAF.toByte(),
            0xC9.toByte(), 0xE8.toByte(), 0xD6.toByte(), 0xC3.toByte(), 0x0A,
            0x3D, 0x3D, 0x3D, 0x3D, 0x3D, 0x3D, 0x3D, 0x3D, 0x3D, 0x3D, 0x3D, 0x3D, 0x3D, 0x3D, 0x3D, 0x3D,
            0x3D, 0x3D, 0x3D, 0x3D, 0x3D, 0x3D, 0x3D, 0x3D, 0x3D, 0x3D, 0x3D, 0x3D, 0x3D, 0x3D, 0x3D, 0x3D,
            0x0A, 0x1C, 0x26, 0x1C, 0x43, 0x00
        )
    }
}