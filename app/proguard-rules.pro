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

#CacheWebview
-dontwarn ren.yale.android.cachewebviewlib.**
-keep class ren.yale.android.cachewebviewlib.**{*;}

#okhttp
-dontwarn okhttp3.**
-keep class okhttp3.**{*;}

#okio
-dontwarn okio.**
-keep class okio.**{*;}

-keep class com.appsflyer.** { *; }

-keep public class com.android.installreferrer.** { *; }


#google
#-keep class com.google.android.gms.ads.**{*;}
#TODO 上线 不混淆会数组异常
-keep class com.blankj.utilcode.util.**{*;}

#facebook
#-keep class com.facebook.applinks.** { *; }
#-keepclassmembers class com.facebook.applinks.** { *; }
#-keep class com.facebook.FacebookSdk { *; }

#huawei
#-keep class com.huawei.hms.ads.** { *; }
#-keep interface com.huawei.hms.ads.** { *; }
#-dontwarn com.huawei.hms.ads.**

#cn.hx.plugin.ui为前面配置的packageBase
#-keep class com.ruep.rwedn.ui.** {*;}
#-keep class com.xauz.cmjwd.activity.** {*;}
# 自定义数据模型的bean目录
-keep class com.pbicv.ddpx.game.pojo**{*;}

#agentweb
-keep class com.just.agentweb.** { *;}
-dontwarn com.alipay.sdk.**
-dontwarn com.download.library.**

# 保持jsb方法不混淆
#-keep class c.C{
# public final void getAndroidInfo();
# public final void getAppId();
# public final void getAppName();
# public final void getBootAPIData();
# public final void getClipboardText();
# public final void getDeviceId();
# public final void getGaId();
# public final void getGoogleInstall();
# public final void getGoogleOpen();
# public final void getLoadParamData();
# public final void getLoginClose();
# public final void getReferrerData();
# public final void getSSAID();
# public final void getSourcePath();
# public final void openBrowser(java.lang.String);
# public void openUrlBySystemBrowser(java.lang.String);
# public void pushCustom(byte[]);
# public void pushPurchase(byte[], byte[]);
# public final void setClipboardText(java.lang.String);
# public final void setNativeNotification(java.lang.String , java.lang.String , java.lang.String, java.lang.String);
# public final void setLoadFinish();
#}