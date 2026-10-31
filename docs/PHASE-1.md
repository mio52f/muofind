# المرحلة الأولى — Android shell وCI

## المنجز في هذا الهيكل
- مشروع Android أصلي بلغة Kotlin.
- Activity أولية توضّح حالة المشروع.
- إعدادات SDK وJDK ثابتة.
- GitHub Actions لبناء APK Debug ورفع الناتج كـ artifact.
- لا يوجد Gradle Wrapper لتجنّب ملفات wrapper الثنائية غير الموثوقة أو التالفة؛ Gradle يُجهّز على runner فقط.

## ما لم يُنفّذ بعد
- حسابات المستخدمين أو تسجيل الدخول.
- خادم API أو قاعدة بيانات.
- BLE advertising / scanning.
- جمع الموقع أو صلاحيات الموقع.
- خدمة foreground أو إشعار المشاركة.
- إنشاء المعرفات الدورية والتشفير.

## معيار قبول المرحلة
ينجح workflow في `assembleDebug` وينتج:
`app/build/outputs/apk/debug/app-debug.apk`
