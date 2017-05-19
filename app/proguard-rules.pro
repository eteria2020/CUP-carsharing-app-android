#
# DEFAULT PROGUARD RULES
#
# Add project specific ProGuard rules inside proguard-rules.pro
#

-keepclassmembers class com.unity3d.** { *; }
-keepclassmembers class org.fmod.** { *; }
-keepclassmembers class bitter.jnibridge.** { *; }

-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

#WARNING
-dontwarn com.squareup.picasso.**
-dontwarn retrofit2.**
-dontwarn okhttp3.**
-dontwarn javax.xml.stream.**
-dontwarn org.xmlpull.**
-dontwarn com.squareup.**
-dontwarn net.htmlparser.jericho.**
-dontwarn com.parse.**
-dontwarn com.facebook.**
-dontwarn okio.**
-dontwarn org.joda.**
-dontwarn com.google.**
-dontwarn rx.**
-dontwarn android.webkit.WebView
-dontwarn android.net.http.SslError
-dontwarn android.webkit.WebViewClient
-dontwarn it.jakala.common.**
-dontwarn mbanje.kurt.fabbutton.**
-dontwarn org.iban4j.**
-dontwarn com.amazonaws.**
-dontwarn com.viewpagerindicator.**
-dontwarn com.octo.android.robospice.**
-dontwarn org.apache.**
-dontwarn me.zhanghai.**
-dontwarn com.throrinstudio.android.common.libs.validator.**
-dontwarn com.androidmapsextensions.**
-dontwarn com.millennialmedia.**
-dontwarn com.inmobi.**
-dontwarn com.appnexus.**
-dontwarn com.adsdk.**
-dontwarn com.smartadserver.**
-dontwarn io.fabric.**
-dontwarn com.mngads.**
-dontwarn com.retency.**
-dontwarn com.appsfire.**
-dontwarn com.caverock.**
-dontwarn com.amazon.**
-dontwarn com.flurry.**
-dontwarn liverail.library.**
-dontwarn loopj.**
-dontwarn com.mikepenz.**
-dontwarn com.github.lzyzsd.**
-dontwarn rx.**
-dontwarn com.google.zxing.**
-dontwarn com.journeyapps.barcodescanner.**
-dontwarn com.astuetz.**

-dontwarn bitter.jnibridge.**
-dontwarn com.unity3d.**
-dontwarn org.fmod.**
-dontwarn com.visagetechnologies.visagetrackerunitydemo.**


#LIBRERIE
-keep class com.google.** { *; }
-keep class retrofit2.** { *; }
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-keep class com.j256.** { *; }
-keep class de.greenrobot.**  { *; }
-keep class org.simpleframework.xml.**  { *; }
-keep class org.xmlpull.**  { *; }
-keep class javax.xml.stream.**  { *; }
-keep class com.facebook.**  { *; }
-keep class org.apache.harmony.xnet.provider.jsse.**  { *; }
-keep class com.stanfy.** { *; }
-keep class se.emilsjolander.** { *; }
-keep class com.mobprofs.** { *; }
-keep class com.squareup.** { *; }
-keep interface com.squareup.** { *; }
-keep class com.crashlytics.android.** { *; }
-keep class com.throrinstudio.android.common.libs.validator.** { *; }
-keep class it.synesthesia.customholopicker.** { *; }
-keep class io.vov.vitamio.** { *; }
-keep class org.jsoup.** { *; }
-keep class com.parse.** { *; }
-keep class de.keyboardsurfer.android.** { *; }
-keep class com.sothree.slidinguppanel.** { *; }
-keep class com.gigya.** {*;}
-keep class com.daimajia.** {*;}
-keep class com.doomonefireball.** {*;}
-keep class com.github.** {*;}
-keep class com.hannesdorfmann.** {*;}
-keep class com.viewpagerindicator.** {*;}
-keep class it.xonne.** {*;}
-keep class org.opencv.** {*;}
-keep class io.fabric.** {*;}
-keep class org.jraf.** {*;}
-keep class mbanje.kurt.fabbutton.** {*;}
-keep class org.iban4j.** {*;}
-keep class com.amazonaws.** {*;}
-keep class com.octo.android.robospice.** {*;}
-keep class org.apache.** {*;}
-keep class me.zhanghai.** {*;}
-keep class com.androidmapsextensions.** {*;}
-keep class com.millennialmedia.** {*;}
-keep class com.inmobi.** {*;}
-keep class com.appnexus.** {*;}
-keep class com.adsdk.** {*;}
-keep class com.smartadserver.** {*;}
-keep class com.mngads.** {*;}
-keep class com.retency.** {*;}
-keep class com.appsfire.** {*;}
-keep class com.caverock.** {*;}
-keep class com.amazon.** {*;}
-keep class com.flurry.** {*;}
-keep class liverail.library.** {*;}
-keep class loopj.** {*;}
-keep class com.mikepenz.** {*;}
-keep class com.github.lzyzsd.** {*;}
-keep class rx.** {*;}
-keep class com.google.zxing.** {*;}
-keep class com.journeyapps.barcodescanner.** {*;}
-keep class com.astuetz.** {*;}

-keep public class android.net.http.SslError
-keep public class android.webkit.WebViewClient


-keep class bitter.jnibridge.** {*;}
-keep class com.unity3d.** {*;}
-keep class org.fmod.** {*;}
-keep class com.visagetechnologies.visagetrackerunitydemo.** {*;}


# Allow obfuscation of android.support.v7.internal.view.menu.**
# to avoid problem on Samsung 4.2.2 devices with appcompat v21
# see http://stackoverflow.com/questions/24809580/noclassdeffounderror-android-support-v7-internal-view-menu-menubuilder
-keep class !android.support.v7.view.menu.*MenuBuilder*, android.support.v7.** { *; }
-keep interface android.support.v7.* { *; }

# REFLECT OTTO BUS
-keepclassmembers class ** {
    @com.squareup.otto.Subscribe public *;
    @com.squareup.otto.Produce public *;
}

# ABOUT LIBRARIES
-keep class .R
-keep class **.R$* {
    <fields>;
}

# Butterknife
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes SourceFile, LineNumberTable, Signature, *Annotation*

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
#-keep class com.google.gson.stream.** { *; }

##---------------End: proguard configuration for Gson  ----------


##--- ADVERTISER INSTALL.COM
-keep public class * extends android.content.BroadcastReceiver
-keep class com.google.android.gms.common.GooglePlayServicesUtil {
    *;
}
-keep class com.google.android.gms.ads.identifier.AdvertisingIdClient {
    *;
}
-keep class com.google.android.gms.ads.identifier.AdvertisingIdClient$Info {
    *;
}
##--- ADVERTISER INSTALL.COM



##--- APPTENTIVE
-dontwarn android.support.v7.**
-keep class android.support.v7.** { *; }
-keep interface android.support.v7.** { *; }

-dontwarn android.support.v4.app.**
-keep class android.support.v4.app.** { *; }
-keep interface android.support.v4.app.** { *; }

-dontwarn com.google.android.gms.**
-keep public class com.google.android.gms.**

-dontwarn com.apptentive.**
-keepattributes SourceFile,LineNumberTable
-keep class com.apptentive.** { *; }
##--- APPTENTIVE

##--- DAGGER
-keepclassmembers,allowobfuscation class * {
@javax.inject.* *;
@dagger.* *;
<init>();
}
-keep class **$ModuleAdapter
-keep class **$InjectAdapter
-keep class **$StaticInjection
-keep class **$Adapter
-keepnames class dagger.Lazy
##--- DAGGER

##--- GUAVA
# Configuration for Guava 18.0
#
# disagrees with instructions provided by Guava project: https://code.google.com/p/guava-libraries/wiki/UsingProGuardWithGuava

-keep class com.google.common.io.Resources {
    public static <methods>;
}
-keep class com.google.common.collect.Lists {
    public static ** reverse(**);
}
-keep class com.google.common.base.Charsets {
    public static <fields>;
}

-keep class com.google.common.base.Joiner {
    public static com.google.common.base.Joiner on(java.lang.String);
    public ** join(...);
}

-keep class com.google.common.collect.MapMakerInternalMap$ReferenceEntry
-keep class com.google.common.cache.LocalCache$ReferenceEntry

# http://stackoverflow.com/questions/9120338/proguard-configuration-for-guava-with-obfuscation-and-optimization
-dontwarn javax.annotation.**
-dontwarn javax.inject.**
-dontwarn sun.misc.Unsafe

# Guava 19.0
-dontwarn java.lang.ClassValue
-dontwarn com.google.j2objc.annotations.Weak
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

-dontwarn com.google.common.collect.MinMaxPriorityQueue
-keepclasseswithmembers public class * {
    public static void main(java.lang.String[]);
}

##--- GUAVA




#UNITY
-dontwarn it.synesthesia.my_app.ui.selfiedistelle.**
-dontwarn it.synesthesia.my_app.components.UnityPlayerWrapper
-keep class it.synesthesia.my_app.ui.selfiedistelle.** {*;}
-keep class it.synesthesia.my_app.components.UnityPlayerWrapper {*;}

#MODEL X serializzazione
-keep class it.synesthesia.my_app.data.models.** { *;}
-keep class it.synesthesia.my_app.data.common.DELETE_WITH_BODY { *;}
-keep class it.synesthesia.my_app.data.ExcludeSerialization { *;}

