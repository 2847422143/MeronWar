package com.pbicv.ddpx.game.pojo;

import com.blankj.utilcode.util.AppUtils;

public class ImportantValue {


    //TODO 新包 未修改
    public static final String BASIC_URL = "https://api.pbicvddpx.net";
    public static final String WEB_GOOGLE_CLIENT_ID = "989928353414-j7ai2ng7tfi1ud6o0flud5sp0vd4r14p.apps.googleusercontent.com";
    public static final String AF_DEV_KEY = "DrdEjavQwdWb6HM8AyCPp9";
    public static final String PACKAGE_NAME = AppUtils.getAppPackageName();
    //该网址不需要缓存
    public static final String USER_UPLOAD_URL = "https://storage.crisp.chat/users/upload/visitor";

    /*
*   h01_br_a02_002，如此类推，该包 ID 在每一个包里面写死即可。
    第一段：h 标识合作方类型，02 表示合作方 ID
    z 代表是自研包，02 代表是当前此包的开发者 ID
    第二段：mx、br、ph 标识投放的国家缩写
    第三段：a01 表示某个业务线的包，不同业务的 ID 不同，a 表示某条业务线，01 表示某款产品
    a01 代表自用的推广包
    b01 代表自用的主包
    c01 代表 ASO 流量包
    d01 代表与别人联运的包
    e01 代表代理专用的包
    f01 代表体育等其他品类的包，待扩展
    第四段：001 表示这个包的更新版本号，每次上架审核都需要  +1。
    在线上使用时，统一用偶数版本好，比如 002 004，自己本地测试的统一用 001， 003
*
* */

    //TODO 新包
    public static final String APP_ID = "z01_ph_a44_001";

    // -----------------   sp相关  ----------------------------
    public static final String SP_NAME = AppUtils.getAppPackageName() + "event_track";


    public static final String SP_FIRST_VISIT = AppUtils.getAppPackageName() + "first_visit";


    // -----------------   sp相关  ----------------------------
}
