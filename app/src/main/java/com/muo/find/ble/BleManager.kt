package com.muo.find.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.bluetooth.le.*
import android.content.Context
import android.os.ParcelUuid
import java.security.SecureRandom

class BleManager(context: Context) {

    companion object {
        val SERVICE_UUID =
            ParcelUuid.fromString("0000fd6f-0000-1000-8000-00805f9b34fb")
    }

    interface Listener {
        fun onStatus(message: String)
        fun onDeviceFound(id: String, rssi: Int)
    }

    private val app = context.applicationContext
    private val adapter =
        (app.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter

    private var advertiser: BluetoothLeAdvertiser? = null
    private var scanner: BluetoothLeScanner? = null
    private var advCallback: AdvertiseCallback? = null
    private var scanCallback: ScanCallback? = null
    private var listener: Listener? = null

    fun setListener(value: Listener?) {
        listener = value
    }

    @SuppressLint("MissingPermission")
    fun startAdvertising() {
        stopScanning()

        if (!adapter.isEnabled) {
            listener?.onStatus("فعّل Bluetooth أولاً")
            return
        }

        val tx = adapter.bluetoothLeAdvertiser
        if (tx == null) {
            listener?.onStatus("الهاتف لا يدعم بث BLE")
            return
        }

        stopAdvertising()

        val id = ByteArray(8).also {
            SecureRandom().nextBytes(it)
        }

        val data = AdvertiseData.Builder()
            .setIncludeDeviceName(false)
            .addServiceData(SERVICE_UUID, id)
            .build()

        val settings = AdvertiseSettings.Builder()
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
            .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
            .setConnectable(false)
            .build()

        val callback = object : AdvertiseCallback() {
            override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
                listener?.onStatus("بث BLE يعمل بنجاح")
            }

            override fun onStartFailure(errorCode: Int) {
                listener?.onStatus("فشل البث: $errorCode")
            }
        }

        advertiser = tx
        advCallback = callback
        tx.startAdvertising(settings, data, callback)
    }

    @SuppressLint("MissingPermission")
    fun startScanning() {
        stopAdvertising()

        if (!adapter.isEnabled) {
            listener?.onStatus("فعّل Bluetooth أولاً")
            return
        }

        val rx = adapter.bluetoothLeScanner
        if (rx == null) {
            listener?.onStatus("البحث عبر BLE غير مدعوم")
            return
        }

        stopScanning()

        val filter = ScanFilter.Builder()
            .setServiceUuid(SERVICE_UUID)
            .build()

        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()

        val callback = object : ScanCallback() {
            override fun onScanResult(
                callbackType: Int,
                result: ScanResult
            ) {
                val bytes = result.scanRecord
                    ?.getServiceData(SERVICE_UUID) ?: return

                val id = bytes.joinToString("") {
                    "%02X".format(it)
                }

                listener?.onDeviceFound(id, result.rssi)
            }

            override fun onScanFailed(errorCode: Int) {
                listener?.onStatus("فشل البحث: $errorCode")
            }
        }

        scanner = rx
        scanCallback = callback

        rx.startScan(listOf(filter), settings, callback)

        listener?.onStatus("البحث عن أجهزة MUO Find جارٍ...")
    }

    @SuppressLint("MissingPermission")
    fun stopAdvertising() {
        val callback = advCallback ?: return
        try {
            advertiser?.stopAdvertising(callback)
        } catch (_: Exception) {}
        advCallback = null
    }

    @SuppressLint("MissingPermission")
    fun stopScanning() {
        val callback = scanCallback ?: return
        try {
            scanner?.stopScan(callback)
        } catch (_: Exception) {}
        scanCallback = null
    }

    fun stopAll() {
        stopAdvertising()
        stopScanning()
        listener?.onStatus("تم إيقاف Bluetooth")
    }
}
