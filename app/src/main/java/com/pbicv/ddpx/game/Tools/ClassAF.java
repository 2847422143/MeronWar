package com.pbicv.ddpx.game.Tools;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Process;
import android.webkit.WebView;

import androidx.annotation.NonNull;

import com.appsflyer.AppsFlyerConversionListener;
import com.appsflyer.AppsFlyerLib;
import com.appsflyer.attribution.AppsFlyerRequestListener;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPStaticUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.Utils;
import com.pbicv.ddpx.game.pojo.ImportantValue;


import java.util.Map;


public class ClassAF {

    public static final ClassAF S_APPLICATION_MAIN = new ClassAF();

    private ClassAF() {
    }

    public void webViewSetPath(Context context) {
        if (context == null) {
            return;
        }
        if (Build.VERSION.SDK_INT < 28) {
            return;
        }
        String processName = getProcessName(context);
        LogUtils.d(processName);
        if (context.getPackageName().equals(processName)) {
            return;
        }
        if (processName.isEmpty()) {
            return;
        }
        WebView.setDataDirectorySuffix(processName);


    }


    private String getProcessName(Context context) {
        if (context == null) {
            return null;
        }
        Object systemService = context.getSystemService(Context.ACTIVITY_SERVICE);
        if (systemService == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : ((ActivityManager) systemService).getRunningAppProcesses()) {
            if (runningAppProcessInfo.pid == Process.myPid()) {
                return runningAppProcessInfo.processName;
            }
        }
        return null;
    }



    public void initAppsFlyerMain(ClassAppB A) {
        //转化数据
        AppsFlyerConversionListener conversionListener = new AppsFlyerConversionListener() {
            public void onAppOpenAttribution(Map<String, String> map) {
                for (String str : map.keySet()) {
                    LogUtils.dTag("AppsFlyer", "attribute: " + str + " = " + map.get(str));
                }
            }
            @Override
            public void onAttributionFailure(String str) {
                LogUtils.dTag("AppsFlyer", "error onAttributionFailure : " + str);
            }
            @Override
            public void onConversionDataFail(String str) {
                LogUtils.dTag("AppsFlyer", "error getting conversion data: " + str);
            }
            @Override
            public void onConversionDataSuccess(Map<String, Object> map) {
                for (String str : map.keySet()) {
                    LogUtils.dTag("AppsFlyer", "attribute: " + str + " = " + map.get(str));
                }
            }
        };
        AppsFlyerLib.getInstance().init(ImportantValue.AF_DEV_KEY, conversionListener, A);
        AppsFlyerLib.getInstance().setCustomerUserId(String.valueOf(SPUtils.getInstance("user_info").getInt("uid", 0)));
        AppsFlyerLib.getInstance().start(A, ImportantValue.AF_DEV_KEY, new AppsFlyerRequestListener() {
            @Override
            public void onSuccess() {
                LogUtils.dTag("AppsFlyer", "Launch sent successfully, got 200 response code from server");
            }
            @Override
            public void onError(int i, @NonNull String s) {
                LogUtils.dTag("AppsFlyer", "Launch failed to be sent:\n" +
                        "Error code: " + i + "\n"
                        + "Error description: " + s);
            }
        });

        //上报事件
        AppsFlyerLib.getInstance().logEvent(A, "gee-open", null);
        initSP(A);
        String appsFlyerUID = AppsFlyerLib.getInstance().getAppsFlyerUID(Utils.getApp());

        LogUtils.dTag("appsFlyerUID : " , appsFlyerUID);
    }


    /* SplashActivity里也有一样的操作  这里先注释掉了 */
    public final void initSP(Application application) {
        if (SPUtils.getInstance(ImportantValue.SP_NAME).getBoolean(ImportantValue.SP_FIRST_VISIT, true)) {
            SPStaticUtils.setDefaultSPUtils(SPUtils.getInstance(ImportantValue.SP_NAME));
            SPStaticUtils.put(ImportantValue.SP_FIRST_VISIT, false);
            AppsFlyerLib.getInstance().logEvent(application, "gee-cpi", null);
        }
    }

}
