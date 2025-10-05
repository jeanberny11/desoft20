package com.dreamsoft.desoft20.features.bluetooth

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import com.dreamsoft.desoft20.features.bluetooth.models.BluetoothDeviceInfo

import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BluetoothManager @Inject constructor(
) {

    @RequiresPermission(allOf = ["android.permission.BLUETOOTH_CONNECT", "android.permission.BLUETOOTH_SCAN"])
    private fun getAdapter(context: Context): BluetoothAdapter? {
        val bluetoothManager = context.getSystemService(BluetoothManager::class.java)
        return bluetoothManager.adapter
    }

    @Deprecated("Use getAdapter instead if you are targeting Android 11+ or api 30+")
    @RequiresPermission("android.permission.BLUETOOTH_ADMIN")
    private fun getAdapterLegacy(context: Context): BluetoothAdapter? {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
            throw Exception("This function is only available from api leve 29 and previous versions")
        }
        val bluetoothManager =
            context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        return bluetoothManager.adapter
    }

    @RequiresPermission(allOf = [Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT])
    fun isBluetoothEnabled(context: Context): Boolean {
        val adapter = getAdapter(context)
        return adapter?.isEnabled ?: false
    }

    @RequiresPermission(allOf = [Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT])
    fun isBluetoothAvailable(context: Context): Boolean {
        val adapter = getAdapter(context)
        return adapter != null
    }

    @RequiresPermission(allOf = [Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT])
    fun getBondedDevices(context: Context): List<BluetoothDeviceInfo> {
        return try {
            val adapter = getAdapter(context)
            if (adapter != null) {
                adapter.bondedDevices.map { device ->
                    BluetoothDeviceInfo(
                        name = device.name ?: "Unknown Device",
                        address = device.address
                    )
                }
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    @RequiresPermission("android.permission.BLUETOOTH_ADMIN")
    fun getBondedDevicesLegacy(context: Context): List<BluetoothDeviceInfo> {
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
        return try {
            val adapter = getAdapterLegacy(context)
            if (adapter != null) {
                adapter.bondedDevices.map { device ->
                    BluetoothDeviceInfo(
                        name = device.name ?: "Unknown Device",
                        address = device.address
                    )
                }
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun hasBluetoothPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_ADMIN
            ) == PackageManager.PERMISSION_GRANTED
        }
    }
}