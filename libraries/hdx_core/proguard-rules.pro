# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in E:/Program Files (x86)/Android/android-studio/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the ProGuard
# include property in project.properties.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}


#WARNING
-dontwarn com.squareup.picasso.**
-dontwarn com.squareup.okhttp.**

#LIBRERIE
-keep class com.google.** { *; }
-keep class com.squareup.** { *; }
-keep class com.crashlytics.android.** { *; }

#MODEL X serializzazione

# REFLECT OTTO BUS
-keepclassmembers class ** {
    @com.squareup.otto.Subscribe public *;
    @com.squareup.otto.Produce public *;
}