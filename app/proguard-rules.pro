# Rushd ProGuard rules

# Keep application classes
-keep class com.rushd.app.** { *; }

# AndroidX WebKit
-keep class androidx.webkit.** { *; }

# Keep JavaScript interface if you add one
# -keepclassmembers class com.rushd.app.webview.JavaScriptBridge {
#     @android.webkit.JavascriptInterface <methods>;
# }

# Firebase Messaging (uncomment if Firebase is enabled)
# -keep class com.google.firebase.** { *; }
# -dontwarn com.google.firebase.**

# Generic Android rules
-dontwarn androidx.**
-keep class androidx.** { *; }
-keep interface androidx.** { *; }
