package com.muo.find.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.muo.find.R
import com.muo.find.ble.BleManager

class MainActivity : AppCompatActivity(), BleManager.Listener {

    companion object {
        private const val REQUEST_CODE = 200
    }

    private lateinit var status: TextView
    private lateinit var permissionsButton: Button
    private lateinit var ble: BleManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ble = BleManager(this)
        ble.setListener(this)

        status = findViewById(R.id.txtStatusTitle)
        permissionsButton = findViewById(R.id.btnPermissions)

        permissionsButton.setOnClickListener {
            requestPermissions()
        }

        findViewById<CardView>(R.id.cardMyDevices).setOnClickListener {
            toast("إدارة الأجهزة ستضاف لاحقاً")
        }

        findViewById<CardView>(R.id.cardFindPhone).setOnClickListener {
            if (hasBlePermissions()) ble.startAdvertising()
            else requestPermissions()
        }

        findViewById<CardView>(R.id.cardHelpOthers).setOnClickListener {
            if (hasBlePermissions()) ble.startScanning()
            else requestPermissions()
        }

        findViewById<CardView>(R.id.cardLostDevices).setOnClickListener {
            toast("نتائج الاكتشاف تظهر في حالة التطبيق")
        }

        findViewById<CardView>(R.id.cardLogs).setOnClickListener {
            toast("سجل النتائج سيضاف لاحقاً")
        }

        findViewById<CardView>(R.id.cardSettings).setOnClickListener {
            ble.stopAll()
        }

        requestPermissions()
    }

    private fun hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this, permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun hasBlePermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= 31) {
            hasPermission(Manifest.permission.BLUETOOTH_SCAN) &&
            hasPermission(Manifest.permission.BLUETOOTH_ADVERTISE) &&
            hasPermission(Manifest.permission.BLUETOOTH_CONNECT)
        } else {
            hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun requestPermissions() {
        val list = mutableListOf<String>()

        if (Build.VERSION.SDK_INT >= 31) {
            list.addAll(
                listOf(
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_ADVERTISE,
                    Manifest.permission.BLUETOOTH_CONNECT
                ).filter { !hasPermission(it) }
            )
        } else if (!hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            list.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if (Build.VERSION.SDK_INT >= 33 &&
            !hasPermission(Manifest.permission.POST_NOTIFICATIONS)
        ) {
            list.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        if (list.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this, list.toTypedArray(), REQUEST_CODE
            )
        } else {
            updatePermissionStatus()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        results: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, results)
        if (requestCode == REQUEST_CODE) updatePermissionStatus()
    }

    private fun updatePermissionStatus() {
        val ready = hasBlePermissions()
        status.text = if (ready)
            "صلاحيات Bluetooth جاهزة للاختبار"
        else
            "يلزم منح صلاحيات Bluetooth"

        status.setTextColor(
            ContextCompat.getColor(
                this,
                if (ready) R.color.status_green else R.color.status_red
            )
        )

        permissionsButton.isEnabled = !ready
    }

    override fun onStatus(message: String) {
        runOnUiThread {
            status.text = message
        }
    }

    override fun onDeviceFound(id: String, rssi: Int) {
        runOnUiThread {
            status.text =
                "تم اكتشاف جهاز MUO Find\n" +
                "المعرف: $id\n" +
                "قوة الإشارة: $rssi dBm\n" +
                "الإشارة لا تعطي مسافة دقيقة."
        }
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        ble.stopAll()
        ble.setListener(null)
        super.onDestroy()
    }
}
