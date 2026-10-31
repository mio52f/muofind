package com.muo.find.ui

import android.Manifest
import android.content.Intent
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
import com.muo.find.service.HelperForegroundService

class MainActivity : AppCompatActivity() {

    companion object {
        private const val PERMISSION_REQUEST_CODE = 200
    }

    private lateinit var txtStatusTitle: TextView
    private lateinit var btnPermissions: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        checkAndRequestPermissions()
        setupListeners()
    }

    private fun initViews() {
        txtStatusTitle = findViewById(R.id.txtStatusTitle)
        btnPermissions = findViewById(R.id.btnPermissions)
    }

    private fun setupListeners() {
        btnPermissions.setOnClickListener {
            checkAndRequestPermissions()
        }

        findViewById<CardView>(R.id.cardMyDevices).setOnClickListener {
            showToast("قسم أجهزتي: يجرى تحميل الأجهزة المرتبطة بحسابك")
        }

        findViewById<CardView>(R.id.cardFindPhone).setOnClickListener {
            showToast("تم تفعيل بث وضع الهاتف المفقود وتوليد المعرف المؤقت EID")
        }

        findViewById<CardView>(R.id.cardHelpOthers).setOnClickListener {
            if (hasAllPermissions()) {
                startHelperService()
                showToast("تم تفعيل خدمة مساعدة الآخرين في الخلفية بنجاح")
            } else {
                showToast("يرجى منح الصلاحيات المطلوبة أولاً")
            }
        }

        findViewById<CardView>(R.id.cardLostDevices).setOnClickListener {
            showToast("عرض الأجهزة المفقودة المكتشفة بالقرب منك")
        }

        findViewById<CardView>(R.id.cardLogs).setOnClickListener {
            showToast("سجل التقارير المشفرة لعمليات العثور")
        }

        findViewById<CardView>(R.id.cardSettings).setOnClickListener {
            showToast("قسم الإعدادات والتحكم في الخصوصية والمفاتيح")
        }
    }

    private fun startHelperService() {
        val intent = Intent(this, HelperForegroundService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }

    private fun checkAndRequestPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!hasPermission(Manifest.permission.BLUETOOTH_SCAN)) permissionsToRequest.add(Manifest.permission.BLUETOOTH_SCAN)
            if (!hasPermission(Manifest.permission.BLUETOOTH_ADVERTISE)) permissionsToRequest.add(Manifest.permission.BLUETOOTH_ADVERTISE)
            if (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) permissionsToRequest.add(Manifest.permission.BLUETOOTH_CONNECT)
        }

        if (!hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!hasPermission(Manifest.permission.POST_NOTIFICATIONS)) permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toTypedArray(), PERMISSION_REQUEST_CODE)
        } else {
            updateStatusUI(true)
        }
    }

    private fun hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }

    private fun hasAllPermissions(): Boolean {
        var valid = hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            valid = valid && hasPermission(Manifest.permission.BLUETOOTH_SCAN) && hasPermission(Manifest.permission.BLUETOOTH_ADVERTISE)
        }
        return valid
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            val allGranted = grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }
            updateStatusUI(allGranted)
        }
    }

    private fun updateStatusUI(isGranted: Boolean) {
        if (isGranted) {
            txtStatusTitle.text = getString(R.string.status_ready)
            txtStatusTitle.setTextColor(ContextCompat.getColor(this, R.color.status_green))
            btnPermissions.isEnabled = false
            btnPermissions.text = "الصلاحيات مفعّلة"
        } else {
            txtStatusTitle.text = getString(R.string.status_permission_missing)
            txtStatusTitle.setTextColor(ContextCompat.getColor(this, R.color.status_red))
            btnPermissions.isEnabled = true
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
