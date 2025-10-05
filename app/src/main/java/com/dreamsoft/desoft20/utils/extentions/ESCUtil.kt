package com.dreamsoft.desoft20.utils.extentions

import android.graphics.Bitmap
import java.io.ByteArrayOutputStream

/**
 * Common command encapsulation
 * 常用指令封装
 */
object ESCUtil {

    const val ESC: Byte = 0x1B // Escape
    const val FS: Byte = 0x1C  // Text delimiter
    const val GS: Byte = 0x1D  // Group separator
    const val DLE: Byte = 0x10 // data link escape
    const val EOT: Byte = 0x04 // End of transmission
    const val ENQ: Byte = 0x05 // Enquiry character
    const val SP: Byte = 0x20  // Spaces
    const val HT: Byte = 0x09  // Horizontal list
    const val LF: Byte = 0x0A  // Print and wrap (horizontal orientation)
    const val CR: Byte = 0x0D  // Home key
    const val FF: Byte = 0x0C  // Carriage control (print and return to the standard mode (in page mode))
    const val CAN: Byte = 0x18 // Canceled (cancel print data in page mode)

    /**
     * Initialize printer
     * 初始化打印机
     */
    fun initPrinter(): ByteArray {
        return byteArrayOf(ESC, 0x40)
    }

    /**
     * Print darkness command
     * 打印浓度指令
     */
    fun setPrinterDarkness(value: Int): ByteArray {
        return byteArrayOf(
            GS,
            40,
            69,
            4,
            0,
            5,
            5,
            (value shr 8).toByte(),
            value.toByte()
        )
    }

    /**
     * Print single QR code - Sunmi custom command
     * 打印单个二维码 sunmi自定义指令
     * @param code: QR code data
     * @param modulesize: QR code block size (unit: dots, value 1 to 16)
     * @param errorlevel: QR code error correction level (0 to 3)
     *                0 -- Error correction level L (7%)
     *                1 -- Error correction level M (15%)
     *                2 -- Error correction level Q (25%)
     *                3 -- Error correction level H (30%)
     */
    fun getPrintQRCode(code: String, modulesize: Int, errorlevel: Int): ByteArray {
        val buffer = ByteArrayOutputStream()
        try {
            buffer.write(setQRCodeSize(modulesize))
            buffer.write(setQRCodeErrorLevel(errorlevel))
            buffer.write(getQCodeBytes(code))
            buffer.write(getBytesForPrintQRCode())
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return buffer.toByteArray()
    }

    /**
     * Print two QR codes using raster bitmap
     * Convert multiple QR codes to raster bitmap for printing
     * 使用光栅位图打印两个二维码
     * 将多个二维码转换为光栅位图打印
     */
    fun getPrintDoubleQRCode(qr1: String, qr2: String, size: Int): ByteArray {
        val bytes1 = byteArrayOf(GS, 0x76, 0x30, 0x00)
        val bytes2 = BytesUtil.getZXingQRCode(qr1, qr2, size)
        return BytesUtil.byteMerger(bytes1, bytes2 ?: byteArrayOf())
    }

    /**
     * Print one-dimensional barcode
     * 打印一维条形码
     */
    fun getPrintBarCode(
        data: String,
        symbology: Int,
        height: Int,
        width: Int,
        textposition: Int
    ): ByteArray {
        var finalSymbology = symbology
        var finalWidth = width
        var finalTextposition = textposition
        var finalHeight = height

        if (finalSymbology < 0 || finalSymbology > 10) {
            return byteArrayOf(LF)
        }

        if (finalWidth < 2 || finalWidth > 6) {
            finalWidth = 2
        }

        if (finalTextposition < 0 || finalTextposition > 3) {
            finalTextposition = 0
        }

        if (finalHeight < 1 || finalHeight > 255) {
            finalHeight = 162
        }

        val buffer = ByteArrayOutputStream()
        try {
            buffer.write(
                byteArrayOf(
                    0x1D, 0x66, 0x01, 0x1D, 0x48, finalTextposition.toByte(),
                    0x1D, 0x77, finalWidth.toByte(), 0x1D, 0x68, finalHeight.toByte(), 0x0A
                )
            )

            val barcode: ByteArray? = if (finalSymbology == 10) {
                BytesUtil.getBytesFromDecString(data)
            } else {
                data.toByteArray(charset("GB18030"))
            }
            if (barcode == null) {
                return byteArrayOf(LF)
            }

            if (finalSymbology > 7) {
                buffer.write(
                    byteArrayOf(
                        0x1D, 0x6B, 0x49, (barcode.size + 2).toByte(),
                        0x7B, (0x41 + finalSymbology - 8).toByte()
                    )
                )
            } else {
                buffer.write(
                    byteArrayOf(
                        0x1D, 0x6B, (finalSymbology + 0x41).toByte(),
                        barcode.size.toByte()
                    )
                )
            }
            buffer.write(barcode)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return buffer.toByteArray()
    }

    /**
     * Raster bitmap printing
     * 光栅位图打印
     */
    fun printBitmap(bitmap: Bitmap): ByteArray {
        val bytes1 = byteArrayOf(GS, 0x76, 0x30, 0x00)
        val bytes2 = BytesUtil.getBytesFromBitMap(bitmap)
        return BytesUtil.byteMerger(bytes1, bytes2)
    }

    /**
     * Raster bitmap printing with mode setting
     * 光栅位图打印 设置mode
     */
    fun printBitmap(bitmap: Bitmap, mode: Int): ByteArray {
        val bytes1 = byteArrayOf(GS, 0x76, 0x30, mode.toByte())
        val bytes2 = BytesUtil.getBytesFromBitMap(bitmap)
        return BytesUtil.byteMerger(bytes1, bytes2)
    }

    /**
     * Raster bitmap printing
     * 光栅位图打印
     */
    fun printBitmap(bytes: ByteArray): ByteArray {
        val bytes1 = byteArrayOf(GS, 0x76, 0x30, 0x00)
        return BytesUtil.byteMerger(bytes1, bytes)
    }

    /**
     * Select bitmap command with mode setting
     * Need to set 1B 33 00 to set line spacing to 0
     * 选择位图指令 设置mode
     * 需要设置1B 33 00将行间距设为0
     */
    fun selectBitmap(bitmap: Bitmap, mode: Int): ByteArray {
        return BytesUtil.byteMerger(
            BytesUtil.byteMerger(
                byteArrayOf(0x1B, 0x33, 0x00),
                BytesUtil.getBytesFromBitMap(bitmap, mode)
            ),
            byteArrayOf(0x0A, 0x1B, 0x32)
        )
    }

    /**
     * Skip specified number of lines
     * 跳指定行数
     */
    fun nextLine(lineNum: Int): ByteArray {
        return ByteArray(lineNum) { LF }
    }

    // ------------------------style set-----------------------------

    /**
     * Set default line spacing
     * 设置默认行间距
     */
    fun setDefaultLineSpace(): ByteArray {
        return byteArrayOf(0x1B, 0x32)
    }

    /**
     * Set line spacing
     * 设置行间距
     */
    fun setLineSpace(height: Int): ByteArray {
        return byteArrayOf(0x1B, 0x33, height.toByte())
    }

    // ------------------------underline-----------------------------

    /**
     * Set underline with one dot width
     * 设置下划线1点
     */
    fun underlineWithOneDotWidthOn(): ByteArray {
        return byteArrayOf(ESC, 45, 1)
    }

    /**
     * Set underline with two dot width
     * 设置下划线2点
     */
    fun underlineWithTwoDotWidthOn(): ByteArray {
        return byteArrayOf(ESC, 45, 2)
    }

    /**
     * Cancel underline
     * 取消下划线
     */
    fun underlineOff(): ByteArray {
        return byteArrayOf(ESC, 45, 0)
    }

    // ------------------------bold-----------------------------

    /**
     * Bold font
     * 字体加粗
     */
    fun boldOn(): ByteArray {
        return byteArrayOf(ESC, 69, 0xF)
    }

    /**
     * Cancel bold font
     * 取消字体加粗
     */
    fun boldOff(): ByteArray {
        return byteArrayOf(ESC, 69, 0)
    }

    // ------------------------character-----------------------------

    /**
     * Single byte mode on
     * 单字节模式开启
     */
    fun singleByte(): ByteArray {
        return byteArrayOf(FS, 0x2E)
    }

    /**
     * Single byte mode off
     * 单字节模式关闭
     */
    fun singleByteOff(): ByteArray {
        return byteArrayOf(FS, 0x26)
    }

    /**
     * Set single byte character set
     * 设置单字节字符集
     */
    fun setCodeSystemSingle(charset: Byte): ByteArray {
        return byteArrayOf(ESC, 0x74, charset)
    }

    /**
     * Set multi-byte character set
     * 设置多字节字符集
     */
    fun setCodeSystem(charset: Byte): ByteArray {
        return byteArrayOf(FS, 0x43, charset)
    }

    // ------------------------Align-----------------------------

    /**
     * Align left
     * 居左
     */
    fun alignLeft(): ByteArray {
        return byteArrayOf(ESC, 97, 0)
    }

    /**
     * Align center
     * 居中对齐
     */
    fun alignCenter(): ByteArray {
        return byteArrayOf(ESC, 97, 1)
    }

    /**
     * Align right
     * 居右
     */
    fun alignRight(): ByteArray {
        return byteArrayOf(ESC, 97, 2)
    }

    /**
     * Cutter
     * 切刀
     */
    fun cutter(): ByteArray {
        return byteArrayOf(0x1d, 0x56, 0x01)
    }

    /**
     * Label or black mark mode positioning
     * For compatibility reasons, only the handheld can support the SDK,
     * other devices must use the command method
     * 标签or黑标模式定位
     * 由于兼容性原因只有手持机可以支持SDK,其他设备必须使用指令方法
     */
    fun labellocate(): ByteArray {
        return byteArrayOf(0x1C, 0x28, 0x4C, 0x02, 0x00, 0x43, 0x31)
    }

    /**
     * Label or black mark mode output
     * For compatibility reasons, only the handheld can support the SDK,
     * other devices must use the command method
     * 标签or黑标模式送出
     * 由于兼容性原因只有手持机可以支持SDK,其他设备必须使用指令方法
     */
    fun labelout(): ByteArray {
        return byteArrayOf(0x1C, 0x28, 0x4C, 0x02, 0x00, 0x42, 0x31)
    }

    ////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////          private                /////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////

    /**
     * Set QR code size
     * 二维码块大小设置指令
     */
    private fun setQRCodeSize(modulesize: Int): ByteArray {
        return byteArrayOf(
            GS,
            0x28,
            0x6B,
            0x03,
            0x00,
            0x31,
            0x43,
            modulesize.toByte()
        )
    }

    /**
     * Set QR code error level
     * 二维码纠错等级设置指令
     */
    private fun setQRCodeErrorLevel(errorlevel: Int): ByteArray {
        return byteArrayOf(
            GS,
            0x28,
            0x6B,
            0x03,
            0x00,
            0x31,
            0x45,
            (48 + errorlevel).toByte()
        )
    }

    /**
     * Print stored QR code data
     * 打印已存入数据的二维码
     */
    private fun getBytesForPrintQRCode(): ByteArray {
        return byteArrayOf(
            0x1D,
            0x28,
            0x6B,
            0x03,
            0x00,
            0x31,
            0x51,
            0x30,
            0x0A
        )
    }

    /**
     * Get QR code bytes
     * 二维码存入指令
     */
    private fun getQCodeBytes(code: String): ByteArray {
        val buffer = ByteArrayOutputStream()
        try {
            val d = code.toByteArray(charset("GB18030"))
            var len = d.size + 3
            if (len > 7092) len = 7092

            buffer.write(0x1D)
            buffer.write(0x28)
            buffer.write(0x6B)
            buffer.write(len)
            buffer.write(len shr 8)
            buffer.write(0x31)
            buffer.write(0x50)
            buffer.write(0x30)

            for (i in 0 until d.size.coerceAtMost(len)) {
                buffer.write(d[i].toInt())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return buffer.toByteArray()
    }
}