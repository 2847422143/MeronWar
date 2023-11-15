package com.pbicv.ddpx.util.utils;

import com.blankj.utilcode.util.AppUtils;

public class DeviceUtils {


    //qx使用的方法
    public static String getUniqueDeviceId() {
        String str;
        if (com.blankj.utilcode.util.DeviceUtils.getUniqueDeviceId() != "" && com.blankj.utilcode.util.DeviceUtils.getUniqueDeviceId() != null) {
            str = com.blankj.utilcode.util.DeviceUtils.getUniqueDeviceId();
        } else {
            str = com.blankj.utilcode.util.DeviceUtils.getMacAddress() + com.blankj.utilcode.util.DeviceUtils.getAndroidID();
        }
        return str;
    }

    public static String getPkgIdForUrl() {
        return AppUtils.getAppPackageName() + "." + AppUtils.getAppVersionCode();
//                return "com.gxvkz.tvyh" + "." + AppUtils.getAppVersionCode();

    }

}
