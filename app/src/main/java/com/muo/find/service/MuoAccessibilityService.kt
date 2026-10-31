package com.muo.find.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.view.accessibility.AccessibilityEvent

class MuoAccessibilityService : AccessibilityService() {

    companion object {
        @Volatile
        var instance: MuoAccessibilityService? = null
            private set
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
    }

    fun tapAt(
        x: Float,
        y: Float,
        onResult: (Boolean) -> Unit
    ) {
        val path = Path().apply {
            moveTo(x, y)
        }

        val gesture = GestureDescription.Builder()
            .addStroke(
                GestureDescription.StrokeDescription(
                    path,
                    0,
                    120
                )
            )
            .build()

        val accepted = dispatchGesture(
            gesture,
            object : GestureResultCallback() {
                override fun onCompleted(
                    gestureDescription: GestureDescription?
                ) {
                    onResult(true)
                }

                override fun onCancelled(
                    gestureDescription: GestureDescription?
                ) {
                    onResult(false)
                }
            },
            null
        )

        if (!accepted) onResult(false)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // لا نقرأ محتوى الشاشات أو بيانات التطبيقات الأخرى.
    }

    override fun onInterrupt() {
    }

    override fun onDestroy() {
        if (instance === this) {
            instance = null
        }
        super.onDestroy()
    }
}
