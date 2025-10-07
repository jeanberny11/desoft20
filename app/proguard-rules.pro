# app/proguard-rules.pro

# Keep application class
-keep class com.dreamsoft.desoft20.Application { *; }

# Keep all JavaScript interface methods
-keepclassmembers class com.dreamsoft.desoft20.webview.WebViewJavaScriptInterface {
    @android.webkit.JavascriptInterface <methods>;
}

# Keep all WebView classes
-keep class android.webkit.** { *; }
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# Kotlinx Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.dreamsoft.desoft20.**$$serializer { *; }
-keepclassmembers class com.dreamsoft.desoft20.** {
    *** Companion;
}
-keepclasseswithmembers class com.dreamsoft.desoft20.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep all data classes for serialization
-keep @kotlinx.serialization.Serializable class com.dreamsoft.desoft20.** { *; }

# Hilt/Dagger
-keep class dagger.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }
-keep class dagger.hilt.** { *; }

# Jetpack Compose
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# Google Play Services
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**

# ZXing (barcode)
-keep class com.google.zxing.** { *; }
-dontwarn com.google.zxing.**

# Sunmi Printer SDK
-keep class com.sunmi.peripheral.** { *; }
-dontwarn com.sunmi.peripheral.**

# Keep line numbers for crash reports
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Remove logging in release
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

# Keep crash reporting info
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes Exception