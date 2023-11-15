package com.pbicv.ddpx.game.abstractClass;

import android.accounts.Account;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.appcompat.app.ActionBar;
import androidx.core.app.NotificationCompat;

import com.android.installreferrer.api.InstallReferrerClient;
import com.android.installreferrer.api.InstallReferrerStateListener;
import com.android.installreferrer.api.ReferrerDetails;
import com.appsflyer.AFInAppEventParameterName;
import com.appsflyer.AppsFlyerLib;
import com.appsflyer.attribution.AppsFlyerRequestListener;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.Utils;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.people.v1.PeopleService;
import com.google.api.services.people.v1.model.Person;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.just.agentweb.AgentWebConfig;
import com.pbicv.ddpx.R;
import com.pbicv.ddpx.game.pojo.ImportantValue;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.HashMap;


public abstract class GLogin extends RxActivity {

    public static final int REQUEST_CODE_GOOGLE_SIGN_LOGIN = 9001;
    private InstallReferrerClient mInstallReferrerClient;
    private Utils.OnAppStatusChangedListener mOnAppStatusChangedListener;
    public String mReferrerDetails;
    HashMap<String, String> mHashMap = new HashMap<>();

    private FirebaseAuth mFirebaseAuth;

    public GoogleSignInClient mGoogleSignInClient;

    private GoogleSignInOptions mGoogleSignInOptions;

    private JSONObject mJSONObject;
    private Account mAccount;
    private String mGAID;
    private boolean mIsGoogleInstall = true;
    private boolean mImmersiveEnabled = false;

    public void initAll(WebView webView) {
        initscreen();//全屏
        obtainInstallReferrer(); //连接到google play，获取安装引荐来源
        signGoogleFirebaseAuth();//Google And Firebase身份验证。
        initAppStatus(webView);//检查app状态
        getAndSaveAdvertisingID();//获取并保存广告ID

    }
    //设置全屏
    private void initscreen() {
        if (Build.VERSION.SDK_INT >= 19) {
            this.mImmersiveEnabled = true;
            setWebFullScreen();
        }
    }
    //将当前界面设置为全屏模式，并隐藏状态栏和导航栏。
    private void setWebFullScreen() {
        WindowInsetsController windowInsetsController;
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            //设置ActionBar的标题显示为false，即隐藏标题。
            supportActionBar.setDisplayShowTitleEnabled(false);
            //设置ActionBar的返回按钮显示为true
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            //隐藏ActionBar
            supportActionBar.hide();
        }
        View decorView = getWindow().getDecorView();
        LogUtils.dTag(getLocalClassName(), " --- hideSystemUI ---");
        if (Build.VERSION.SDK_INT >= 30) {
            windowInsetsController = decorView.getWindowInsetsController();
            windowInsetsController.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
            return;
        }
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
    }
    //获取应用的安装来源信息
    public void obtainInstallReferrer() {
        InstallReferrerClient build = InstallReferrerClient.newBuilder(this).build();
        mInstallReferrerClient = build;
        build.startConnection(new MyInstallReferrerStateListener());
    }
    //监听安装来源信息的获取状态。
    private class MyInstallReferrerStateListener implements InstallReferrerStateListener {
        @Override
        public void onInstallReferrerSetupFinished(int i) {
            LogUtils.dTag(getLocalClassName(), "-- onInstallReferrerSetupFinished --");

            if (i != -1) {
                if (i != 0) {
                    if (i != 1) {
                        if (i != 2) {
                            if (i != 3) {
                                if (i == 4) {
                                    LogUtils.dTag(getLocalClassName(), "-- InstallReferrerResponse: PERMISSION_ERROR --");
                                    return;
                                }
                                return;
                            }
                            LogUtils.dTag(getLocalClassName(), "-- InstallReferrerResponse: DEVELOPER_ERROR --");
                            return;
                        }
                        LogUtils.dTag(getLocalClassName(), "-- InstallReferrerResponse: FEATURE_NOT_SUPPORTED --");
                        return;
                    }
                    LogUtils.dTag(getLocalClassName(), "-- InstallReferrerResponse: SERVICE_UNAVAILABLE --");
                    return;
                }
                LogUtils.dTag(getLocalClassName(), "-- InstallReferrerResponse: OK --");
                GLogin.this.getReferrerDetailsAndDecode();
                return;
            }
            LogUtils.dTag(getLocalClassName(), "-- InstallReferrerResponse: SERVICE_DISCONNECTED  --");
        }
        @Override
        public void onInstallReferrerServiceDisconnected() {
        }
    }
    //用于获取安装来源的详细信息并进行解码
    public void getReferrerDetailsAndDecode() {
        ReferrerDetails referrerDetails;
        try {
            referrerDetails = this.mInstallReferrerClient.getInstallReferrer();
        } catch (RemoteException e) {
            e.printStackTrace();
            referrerDetails = null;
        }
        if (referrerDetails == null) {
            return;
        }
        this.mReferrerDetails = referrerDetails.getInstallReferrer();
        String installReferrer = referrerDetails.getInstallReferrer();
//        LogUtils.dTag("getReferrerDetailsAndDecode", referrerDetails,
//                "referrerClient url:", referrerDetails.getInstallReferrer(),
//                "referrerClient click:", referrerDetails.getReferrerClickTimestampSeconds(),
//                "referrerClient click-server", referrerDetails.getInstallBeginTimestampServerSeconds(),
//                "referrerClient install: ", referrerDetails.getInstallBeginTimestampSeconds(),
//                "referrerClient install-server: ", referrerDetails.getInstallBeginTimestampServerSeconds(),
//                "referrerClient version:", referrerDetails.getInstallVersion()
//        );
        try {
            installReferrer = URLDecoder.decode(installReferrer, "UTF-8");
        } catch (UnsupportedEncodingException e2) {
            e2.printStackTrace();
        }
        String[] split = installReferrer.split("&");
        for (String str : split) {
            if (!TextUtils.isEmpty(str)) {
                String str2 = "=";
                if (!str.contains("=") && str.contains("-")) {
                    str2 = "-";
                }
                String[] split2 = str.split(str2);
                if (split2.length > 1) {
                    try {
                        this.mHashMap.put(URLDecoder.decode(split2[0], "UTF-8"), URLDecoder.decode(split2[1], "UTF-8"));
                    } catch (UnsupportedEncodingException e3) {
                        e3.printStackTrace();
                    }
                }
            }
        }

        LogUtils.dTag(getLocalClassName(), "referrerClient referrerMap: " + mHashMap);
        InstallReferrerClient installReferrerClient = this.mInstallReferrerClient;
        if (installReferrerClient != null) {
            installReferrerClient.endConnection();
        }
    }

    //用于Google Firebase身份验证的方法。
    private void signGoogleFirebaseAuth() {
        FirebaseApp.initializeApp(getApplicationContext());
        mGoogleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(ImportantValue.WEB_GOOGLE_CLIENT_ID).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, mGoogleSignInOptions);
        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    //获取应用程序的缓存路径。
    public static String getCachePath(Context context) {
        try {
            return context.getApplicationContext().getDir("cache", 0).getAbsolutePath() + "/webcache";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    //初始化应用程序的状态。
    public void initAppStatus(WebView webView) {
        mOnAppStatusChangedListener = new PlayOnAppStatusChangedListener(webView);
        AppUtils.registerAppStatusChangedListener(mOnAppStatusChangedListener);
    }
    class PlayOnAppStatusChangedListener implements Utils.OnAppStatusChangedListener {
        final  WebView webView;
        PlayOnAppStatusChangedListener(WebView webView) {
            this.webView = webView;
        }
        @Override
        public void onBackground(Activity activity) {
            LogUtils.dTag(getLocalClassName(), "onBackground: ");
            evaluateJavascriptState(this.webView, "setAppActive", "isActive", Boolean.FALSE);
        }
        @Override
        public void onForeground(Activity activity) {
            LogUtils.dTag(getLocalClassName(), "onForeground: ");
            evaluateJavascriptState(this.webView, "setAppActive", "isActive", Boolean.TRUE);
        }
    }

    //将传入的字符串str作为JavaScript代码在WebView中执行
    public void evaluateJavascriptState(WebView webView, String str, String str2, Boolean bool) {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put(str2, bool);
            evaluateJavascript(webView, str, jSONObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void evaluateJavascript(final WebView webView, final String str, final Object obj) {
        if (webView != null) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public final void run() {
                    sendMessageToJs(webView, str, obj);
                }
            });
        }
    }
    //发送信息到Js
    public  void sendMessageToJs(WebView webView, final String str, Object obj) {
        webView.evaluateJavascript(changeToScript(str, setDataToJson(obj)), new ValueCallback() {// from class: r5.h
            @Override
            public final void onReceiveValue(Object obj2) {
                onJsReceiveValue(str, (String) obj2);
            }
        });
    }
    public void onJsReceiveValue(String str, String str2) {
        LogUtils.dTag(getLocalClassName(), "evaluateJavascript onReceiveValue " + str + str2);
        LogUtils.dTag(getLocalClassName(), "evaluateJavascript end " + str + str2);
    }

    private void getAndSaveAdvertisingID() {
        new Thread(new Runnable() {
            @Override
            public final void run() {
                getAndSaveAdID2SP();
            }
        }).start();
    }

    //获取并保存广告ID（AdID）
    public void getAndSaveAdID2SP() {
        this.mGAID = getGIdThread();
        LogUtils.dTag(getLocalClassName(), "before clear SP");
        //这里会报数组越界异常  需要在混淆规则里排除utils
        SPUtils.getInstance().clear(); //清除SP
        LogUtils.dTag(getLocalClassName(), "clear SP");
        String sp_jump_referrer_path = getPrefsFileStr(ImportantValue.PACKAGE_NAME + "JUMP_REFERRER_PATH" + ImportantValue.APP_ID, "");
        LogUtils.dTag(getLocalClassName(), "clear SP : " + sp_jump_referrer_path);
        SPUtils.getInstance().put(ImportantValue.PACKAGE_NAME + "gaid", this.mGAID);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (this.mImmersiveEnabled && hasFocus) {
            setWebFullScreen();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (this.mImmersiveEnabled) {
            setWebFullScreen();
        }
    }

    public static void checkOpenWebCache(String str, WebSettings webSettings) {
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        if (TextUtils.isEmpty(str)) {
            webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        } else {
            webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        }
    }

    public void eval(WebView webView, String str, String str2, String str3) {
        LogUtils.dTag(getLocalClassName(), "eval", str, str2, str3);
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put(str2, str3);
            evaluateJavascript(webView, str, jSONObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String changeToScript(String functionName, Object obj) {
        if (obj == null) {
            return "javascript:" + functionName + "()";
        }
        String s = "javascript:" + functionName + "(" + obj + ")";
        LogUtils.dTag("javascriptsssssss", s);
        return s;
    }
    public JSONObject setDataToJson(Object obj) {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("success", true);
            jSONObject.put("data", obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jSONObject;
    }

    /* -----------------------                 jsb   start               ----------------------------*/

    public void getAndroidInfo(WebView webView) {
        evaluateJavascript(webView, "setAndroidInfo", getAndroidInfoJsonObj());
    }
    public void getAppId(WebView webView) {
        eval(webView, "setAppId", "id", ImportantValue.APP_ID);
    }

    public void getAppName(WebView webView) {
        eval(webView, "setAppName", "name", getString(R.string.app_name));
    }


    public void getDeviceId(WebView webView) {
        eval(webView, "setDeviceId", "id", DeviceUtils.getUniqueDeviceId());
    }

    public void getGaId(final WebView webView) {
        new Thread(new Runnable() {
            @Override
            public final void run() {
                GLogin.this.getGaIdInNewThread();
            }
        }).start();
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public final void run() {
                GLogin.this.setGaId(webView);
            }
        });
    }

    public  void setGaId(WebView webView) {
        eval(webView, "setGaId", "id", SPUtils.getInstance().getString(ImportantValue.PACKAGE_NAME + "gaid"));
    }


    public void getGoogleInstall(WebView webView) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        if (googleApiAvailability.isGooglePlayServicesAvailable(this) != ConnectionResult.SUCCESS) {
            this.mIsGoogleInstall = false;
        }
        if (webView != null) {
            evaluateJavascript(webView, "setGoogleInstall", Boolean.valueOf(this.mIsGoogleInstall));
        }
    }


    public void getLoginClose(WebView webView) {
        AgentWebConfig.removeAllCookies();
        signOutGoogle();
    }


    public void getSSAID(WebView webView) {
        String ssaid = getSSAID(this);
        eval(webView, "setSSAID", "id", ssaid);
    }


    public void openUrlBySystemBrowser(String str) {
        try {
            startActivity(new Intent("android.intent.action.VIEW", Uri.parse(str)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pushCustom(byte[] bArr) {
        //换成af上报了
        AppsFlyerLib.getInstance().logEvent(this, new String(bArr), null);
    }

    public void pushPurchase(byte[] bArr, byte[] bArr2) {
        String str = new String(bArr);
        String str2 = new String(bArr2);
        final HashMap hashMap = new HashMap();
        hashMap.put(NotificationCompat.CATEGORY_EVENT, str);
        hashMap.put(AFInAppEventParameterName.CONTENT_ID, str);
        hashMap.put(AFInAppEventParameterName.CONTENT_TYPE, str);
        hashMap.put(AFInAppEventParameterName.REVENUE, str2);
        hashMap.put(AFInAppEventParameterName.CURRENCY, "PHP");
        AppsFlyerLib.getInstance().logEvent(mContext, str, hashMap, new AppsFlyerRequestListener() { // from class: com.wildseven.wlsn105.common.webview.WebPage.MessageInterface.2
            @Override
            public void onSuccess() {
            }
            @Override
            public void onError(int i, String str4) {
            }
        });
    }

    /* -----------------------                 jsb  end                ----------------------------*/

    @SuppressLint({"HardwareIds"})
    /* renamed from: O */
    private String getSSAID(Context context) {
        String imei;
        int i = Build.VERSION.SDK_INT;
        if (i >= 29) {
            return Settings.Secure.getString(context.getContentResolver(), "android_id");
        }
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
        if (context.checkSelfPermission("android.permission.READ_PHONE_STATE") != PackageManager.PERMISSION_GRANTED) {
            return "";
        }
        if (telephonyManager.getDeviceId() != null) {
            if (i >= 26) {
                imei = telephonyManager.getImei();
                return imei;
            }
            return telephonyManager.getDeviceId();
        }
        return Settings.Secure.getString(context.getContentResolver(), "android_id");
    }

    private void signOutGoogle() {
        GoogleSignInClient googleSignInClient = this.mGoogleSignInClient;
        if (googleSignInClient != null) {
            googleSignInClient.signOut();
        }
    }
    public  void getGaIdInNewThread() {
        this.mGAID = getGIdThread();
    }

    public String getGIdThread() {
        AdvertisingIdClient.Info info;
        String str = null;
        try {
            info = AdvertisingIdClient.getAdvertisingIdInfo(this);
        } catch (GooglePlayServicesNotAvailableException | GooglePlayServicesRepairableException |
                 IOException e) {
            info = null;
            e.printStackTrace();
        }
        try {
            str = info.getId();
        } catch (NullPointerException e5) {
            e5.printStackTrace();
        }
        return str;
    }

    private JSONObject getAndroidInfoJsonObj() {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("PkgId", AppUtils.getAppPackageName());
            jSONObject.put("AFUID", AppsFlyerLib.getInstance().getAppsFlyerUID(this));
            jSONObject.put("os", "Android");
            jSONObject.put("os_version", DeviceUtils.getSDKVersionCode() + "");
            jSONObject.put("brand", DeviceUtils.getManufacturer());
            jSONObject.put("model", DeviceUtils.getModel());
            jSONObject.put("aaid", SPUtils.getInstance().getString(ImportantValue.PACKAGE_NAME + "gaid"));
            jSONObject.put("android_id", DeviceUtils.getAndroidID());
            String[] split = NetworkUtils.getNetworkType().toString().split("_");
            jSONObject.put("network", split[split.length - 1]);
            jSONObject.put("version_env", "release");
            jSONObject.put("BUILD_USER", "root");
            jSONObject.put("BUILD_JDK", "11");
            jSONObject.put("BUILD_OS", "Linux");
            jSONObject.put("BUILD_UNIXTIME", 1671021090914L);
            jSONObject.put("DEBUG", false);
            jSONObject.put("device_info", getDeviceInfo(this));
            LogUtils.dTag(getLocalClassName(), "getInfo: " + jSONObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jSONObject;
    }

    private static JSONObject getDeviceInfo(Context context) {
        PackageManager packageManager = context.getPackageManager();
        JSONObject jSONObject = new JSONObject();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            try {
                jSONObject.put("versionCode", packageInfo.versionCode);
                jSONObject.put("versionName", packageInfo.versionName);
                jSONObject.put("firstInstallTime", packageInfo.firstInstallTime);
                jSONObject.put("baseRevisionCode", packageInfo.baseRevisionCode);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (PackageManager.NameNotFoundException e2) {
            e2.printStackTrace();
        }
        return jSONObject;
    }


    public void setSpFileStr(String str, String str2) {
        getSpFile().edit().putString(str, str2).apply();
    }


    public String getPrefsFileStr(String str, String str2) {
        return getSpFile().getString(str, str2);
    }


    public SharedPreferences getSpFile() {
        return getSharedPreferences(ImportantValue.PACKAGE_NAME + "PrefsFile", 0);
    }


    public JSONObject putString2Json(String str, String str2, String str3, String str4) {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("name", str);
            jSONObject.put("pic", str2);
            jSONObject.put("email", str3);
            jSONObject.put("phone", str4);
            LogUtils.dTag(getLocalClassName(), "putString2Json: " + jSONObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jSONObject;
    }

    public void setGoogleSignFirebaseAuthAndSendToJs(WebView webView, Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
//            LogUtils.dTag(getLocalClassName(), "Id:" + account.getId()
//                    + "，PhotoUrl:" + account.getPhotoUrl().toString()
//                    + "，DisplayName:" + account.getDisplayName()
//                    + "，FamilyName:" + account.getFamilyName()
//                    + "，GivenName:" + account.getGivenName()
//                    + "，Email:" + account.getEmail()
//                    + "，IdToken:" + account.getIdToken());

            //firebase 验证 google 登录
            firebaseAuthWithGoogle(account.getIdToken());
            this.mAccount = account.getAccount();

            GoogleSignInAccount result = task.getResult(ApiException.class);
            mJSONObject = putString2Json(result.getDisplayName(), String.valueOf(result.getPhotoUrl()), result.getEmail(), result.getIdToken());
            //发送给js
            evaluateJavascript(webView, "setGoogleLogin", this.mJSONObject);
        } catch (ApiException e) {
            LogUtils.dTag(getLocalClassName(), "handleSignInResult:error", e);
            this.mAccount = null;
        }
    }

    private void firebaseAuthWithGoogle(String str) {
        this.mFirebaseAuth.signInWithCredential(GoogleAuthProvider.getCredential(str, null)).addOnCompleteListener(this, new OnCompleteListener() { // from class: r5.a
            @Override
            public final void onComplete(Task task) {
                GLogin.this.logIsTaskSuccessful(task);
            }
        });
    }

    public  void logIsTaskSuccessful(Task task) {
        if (task.isSuccessful()) {
            LogUtils.dTag(getLocalClassName(), "signInWithCredential:success", this.mFirebaseAuth.getCurrentUser());
        } else {
            LogUtils.dTag(getLocalClassName(), "signInWithCredential:failure", task.getException());
        }
    }


    //google登录
    public void signInWithGoogle() {
        //启动登入
        startActivityForResult(getGoogleIntent(), REQUEST_CODE_GOOGLE_SIGN_LOGIN);
    }

    public Intent getGoogleIntent() {
        Intent signInInten;
        if (mGoogleSignInClient == null) {
            signInClient();
        }
        signInInten = mGoogleSignInClient.getSignInIntent();
        return signInInten;
    }

    public void signInClient() {
        if (mGoogleSignInClient == null) {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions
                    .DEFAULT_SIGN_IN)
                    .requestEmail()
                    .requestIdToken(ImportantValue.WEB_GOOGLE_CLIENT_ID)
                    .build();
            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        }

    }

    public void getPhoneNum() {
        if (mAccount == null) {
            LogUtils.dTag(getLocalClassName(), "getContacts: null account");
        } else {
            new PhoneNumAsyncTask(this, this).execute(mAccount);
        }
    }

    @SuppressLint({"StaticFieldLeak"})
    private class PhoneNumAsyncTask extends AsyncTask<Account, Void, Person> {
        //授权获取手机号,需要先在console.cloud.google配置
        private WeakReference<GLogin> weakReference;
        PhoneNumAsyncTask(GLogin activityMainHolder, GLogin activityMainHolder2/*, MyFacebookCallBack myFacebookCallBack*/) {
            this(activityMainHolder2);
        }
        @Override
        public Person doInBackground(Account... accountArr) {
            if (this.weakReference.get() == null) {
                return null;
            }
            try {
                GoogleAccountCredential googleAccountCredential = GoogleAccountCredential.usingOAuth2(this.weakReference.get().getApplicationContext(), Collections.singleton("https://www.googleapis.com/auth/user.phonenumbers.read"));
                googleAccountCredential.setSelectedAccount(accountArr[0]);
                return new PeopleService.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance(), googleAccountCredential)
                        .setApplicationName(AppUtils.getAppName())
                        .build()
                        .people()
                        .get("people/me")
                        .setFields("person.phoneNumbers")
                        .setPersonFields("phoneNumbers").execute();
            } catch (UserRecoverableAuthIOException e) {
                if (this.weakReference.get() != null) {
                    this.weakReference.get().requestGooglePhoneNum(e);
                }
                return null;
            } catch (IOException e2) {
                LogUtils.dTag(getLocalClassName(), "getContacts:exception", e2);
                return null;
            }
        }
        @Override
        public void onPostExecute(Person person) {
            super.onPostExecute(person);
            if (this.weakReference.get() != null) {
                this.weakReference.get().m6120r0(person);
            }
        }
        private PhoneNumAsyncTask(GLogin activityMainHolder) {
            this.weakReference = new WeakReference<>(activityMainHolder);
        }
    }
    protected void m6120r0(Person person) {
        if (person.getPhoneNumbers() == null) {
            LogUtils.dTag(getLocalClassName(), "getContacts:connections: null");
            return;
        }
        LogUtils.dTag(getLocalClassName(), "getContacts:connections: size=" + person.getPhoneNumbers().size());
        for (int i = 0; i < person.getPhoneNumbers().size(); i++) {
            String phoneNumber = person.getPhoneNumbers().get(i).getValue();
            LogUtils.dTag(getLocalClassName(), "phone num : " + phoneNumber);
        }

    }
    //重新请求
    protected void requestGooglePhoneNum(UserRecoverableAuthIOException userRecoverableAuthIOException) {
        startActivityForResult(userRecoverableAuthIOException.getIntent(), 9002);
    }

    public void onActivityResultForFileChooser(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 201) {
            chooseValueCallback(resultCode, intent);
        } else if (requestCode == 202) {
            chooseValueCallback(resultCode, intent);
        }
    }

    public ValueCallback<Uri> mValueCallbackopenFileChooser;

    public ValueCallback<Uri[]> mValueCallBackonShowFileChooser;


    private void chooseValueCallback(int i, Intent intent) {
        ValueCallback<Uri> valueCallback = this.mValueCallbackopenFileChooser;
        if (valueCallback != null) {
            onReceiveValueUri(valueCallback, i, intent);
            this.mValueCallbackopenFileChooser = null;
        }
        ValueCallback<Uri[]> valueCallback2 = this.mValueCallBackonShowFileChooser;
        if (valueCallback2 != null) {
            onReceiveValueUris(valueCallback2, i, intent);
            this.mValueCallBackonShowFileChooser = null;
        }
    }

    private void onReceiveValueUri(ValueCallback<Uri> valueCallback, int i, Intent intent) {
        if (valueCallback == null) {
            return;
        }
        if (intent != null && i == -1) {
            valueCallback.onReceiveValue(intent.getData());
        } else {
            valueCallback.onReceiveValue(null);
        }
    }

    private void onReceiveValueUris(ValueCallback<Uri[]> valueCallback, int i, Intent intent) {
        if (valueCallback == null) {
            return;
        }
        if (intent != null && i == -1) {
            valueCallback.onReceiveValue(new Uri[]{intent.getData()});
        } else {
            valueCallback.onReceiveValue(new Uri[0]);
        }
    }
}
