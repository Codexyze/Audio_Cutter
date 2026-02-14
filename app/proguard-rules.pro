# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# ===== Media3 ProGuard Rules =====
# Keep Media3 classes
-keep class androidx.media3.** { *; }
-dontwarn androidx.media3.**

# Keep LogSessionId related classes (API 31+)
-keep class android.media.metrics.** { *; }
-dontwarn android.media.metrics.**

# Keep ExoPlayer classes
-keepclassmembers class androidx.media3.exoplayer.** { *; }

# Keep Transformer classes
-keep class androidx.media3.transformer.** { *; }
-keepclassmembers class androidx.media3.transformer.** { *; }
