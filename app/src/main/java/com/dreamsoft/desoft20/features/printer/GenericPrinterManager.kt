package com.dreamsoft.desoft20.features.printer

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.dreamsoft.desoft20.features.printer.models.PrintType
import com.dreamsoft.desoft20.features.printer.models.PrinterLine
import com.dreamsoft.desoft20.features.printer.models.PrinterTextAlignment
import com.dreamsoft.desoft20.features.printer.models.PrinterTextSize
import com.dreamsoft.desoft20.utils.extentions.ImageHelper
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.OutputStream
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GenericPrinterManager @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private val uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    private var socket: BluetoothSocket? = null
    private var outputStream: OutputStream? = null

    @RequiresPermission(allOf = ["android.permission.BLUETOOTH_CONNECT", "android.permission.BLUETOOTH_SCAN"])
    fun connect(deviceName: String): Boolean {
        if(socket!=null || outputStream!=null){
            disconnect()
            socket = null
            outputStream = null
        }
        val bluetoothManager = context.getSystemService(BluetoothManager::class.java)
        val bluetoothAdapter = bluetoothManager?.adapter ?: return false
        bluetoothAdapter.cancelDiscovery()
        val device: BluetoothDevice = bluetoothAdapter.bondedDevices.firstOrNull {
            it.name == deviceName
        } ?: return false

        socket = device.createRfcommSocketToServiceRecord(uuid)
        socket?.connect()
        outputStream = socket?.outputStream
        return true
    }

    @RequiresPermission("android.permission.BLUETOOTH_ADMIN")
    fun connectLegacy(deviceName: String): Boolean {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
            throw Exception("This function is only available from api leve 29 and previous versions")
        }

        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_ADMIN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            throw Exception("No tiene los premisos para acceder a esta opcion")
        }
        val bluetoothManager =
            context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter
        bluetoothAdapter.cancelDiscovery()
        val device: BluetoothDevice = bluetoothAdapter.bondedDevices.firstOrNull {
            it.name == deviceName
        } ?: return false

        socket = device.createRfcommSocketToServiceRecord(uuid)
        socket?.connect()
        outputStream = socket?.outputStream
        return true
    }

    private fun write(data: ByteArray) {
        outputStream?.write(data)
        outputStream?.flush()
    }

    // ---------------- ESC/POS commands ----------------

    fun reset() {
        write(byteArrayOf(0x1B, 0x40)) // Initialize
    }

    fun setAlign(alignment:PrinterTextAlignment) {
        // 0 = left, 1 = center, 2 = right
        write(byteArrayOf(0x1B, 0x61, alignment.value.toByte()))
    }

    fun setTextSize(textSize: PrinterTextSize) {
        /*
         * width and height range: 1–8
         * ESC/POS packs them into a single byte: (width - 1) << 4 | (height - 1)
         */
        val size: Int = ((textSize.width - 1) shl 4) or (textSize.height - 1)
        write(byteArrayOf(0x1D, 0x21, size.toByte()))
    }

    fun setFontSmall(enabled: Boolean) {
        // ESC M n → 0=Font A (normal), 1=Font B (small)
        val n: Byte = if (enabled) 1 else 0
        write(byteArrayOf(0x1B, 0x4D, n))
    }

    fun setBold(enabled: Boolean) {
        write(byteArrayOf(0x1B, 0x45, if (enabled) 1 else 0))
    }

    fun setUnderline(mode: Int) {
        // 0=off, 1=single, 2=double
        write(byteArrayOf(0x1B, 0x2D, mode.toByte()))
    }

    fun setInverse(enabled: Boolean) {
        write(byteArrayOf(0x1D, 0x42, if (enabled) 1 else 0))
    }

    fun resetTextStyle() {
        setAlign(PrinterTextAlignment.LEFT)
        setBold(false)
        setUnderline(0)
        setInverse(false)
        setTextSize(PrinterTextSize.NORMALTEXT)
        setFontSmall(false)
    }

    fun printText(text: String) {
        write(text.toByteArray(Charsets.UTF_8))
        write(byteArrayOf(0x0A)) // New line
    }

    fun printLine(line: PrinterLine){
        when(line.type){
            PrintType.TEXT -> {
                setAlign(line.alignment)
                if(line.size == PrinterTextSize.SMALLTEXT){
                    setFontSmall(true)
                }else{
                    setFontSmall(false)
                    setTextSize(line.size)
                }
                setBold(line.bold)
                if(line.underline){
                    setUnderline(1)
                }else{
                    setUnderline(0)
                }
                printText(line.text)
            }
            PrintType.FEED -> {
                feedLines(1)
            }
            PrintType.BARCODE -> {
                printBarcode(line.text)
            }
            PrintType.IMAGE -> {
                val image = ImageHelper.base64ToBitmapOptimized(line.text)
                if (image != null) {
                    printImage(image)
                }else{
                    printText("Error al decodificar la imagen")
                }
            }
            PrintType.COLUMN -> {
                printText("Esta funcionalidad no esta implementada en la version actual")
            }
            PrintType.SEPARATOR -> {
                printText("Esta funcionalidad no esta implementada en la version actual")
            }
            PrintType.CUTTER -> {
                write(byteArrayOf(0x1D, 0x56, 1)) // Cut paper
            }
        }
    }

    fun printBarcode(data: String) {
        // Example: CODE39
        write(byteArrayOf(0x1D, 0x68, 100)) // Barcode height
        write(byteArrayOf(0x1D, 0x48, 2))   // Print below barcode
        write(byteArrayOf(0x1D, 0x6B, 4))   // CODE39
        write(data.toByteArray(Charsets.UTF_8))
        write(byteArrayOf(0x00)) // Terminator
    }

    fun printQRCode(data: String) {
        val bytes = data.toByteArray(Charsets.UTF_8)
        val size = bytes.size + 3
        val pL = (size % 256).toByte()
        val pH = (size / 256).toByte()

        // Store QR data
        write(byteArrayOf(0x1D, 0x28, 0x6B, pL, pH, 0x31, 0x50, 0x30))
        write(bytes)

        // Print QR
        write(byteArrayOf(0x1D, 0x28, 0x6B, 3, 0, 0x31, 0x51, 0x30))
    }

    fun printImage(bitmap: Bitmap) {
        val bytes = ImageHelper.bitmapToEscPos(bitmap)
        write(bytes)
    }

    fun feedLines(n: Int) {
        write(byteArrayOf(0x1B, 0x64, n.toByte()))
    }

    fun disconnect() {
        outputStream?.flush()
        outputStream?.close()
        socket?.close()
    }

    fun haveBluetoothPermission(context: Context): Boolean {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.R){
            return ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_SCAN
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED
        }else{
            return ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_ADMIN
            ) == PackageManager.PERMISSION_GRANTED
        }
    }
}