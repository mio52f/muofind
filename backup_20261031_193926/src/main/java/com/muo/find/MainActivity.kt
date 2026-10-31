package com.muo.find

import android.app.Activity
import android.os.Bundle
import android.graphics.Typeface
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView

/**
 * Phase 1 shell only.
 * No BLE advertising/scanning, location collection, account, or network calls are active.
 */
class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val page = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(28, 48, 28, 32)
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        val title = TextView(this).apply {
            text = "MUO Find"
            textSize = 30f
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
        }

        val subtitle = TextView(this).apply {
            text = "العثور على الهاتف بمساعدة شبكة اختيارية"
            textSize = 17f
            gravity = Gravity.CENTER
            setPadding(0, 18, 0, 28)
        }

        val statusTitle = TextView(this).apply {
            text = "حالة المشروع"
            textSize = 19f
            typeface = Typeface.DEFAULT_BOLD
        }

        val status = TextView(this).apply {
            text = "المرحلة الأولى: تم تجهيز هيكل التطبيق فقط.\n\n" +
                "المشاركة غير مفعّلة، ولا يتم استخدام Bluetooth أو الموقع أو الإنترنت حالياً."
            textSize = 16f
            setPadding(0, 12, 0, 0)
        }

        val privacy = TextView(this).apply {
            text = "مبدأ الخصوصية: لا مشاركة دون موافقة صريحة، " +
                "مع إشعار واضح وإمكانية الإيقاف."
            textSize = 14f
            setPadding(0, 28, 0, 0)
        }

        page.addView(title)
        page.addView(subtitle)
        page.addView(statusTitle)
        page.addView(status)
        page.addView(privacy)
        setContentView(page)
    }
}
