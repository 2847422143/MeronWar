package com.pbicv.ddpx.game.Tools;

import android.app.Application;

import com.blankj.utilcode.util.LogUtils;

import java.io.File;

import ren.yale.android.cachewebviewlib.CacheType;
import ren.yale.android.cachewebviewlib.WebViewCacheInterceptor;
import ren.yale.android.cachewebviewlib.WebViewCacheInterceptorInst;
import ren.yale.android.cachewebviewlib.config.CacheExtensionConfig;


public class ClassAppB extends Application {
    private static ClassAppB sInstance;

    public static ClassAppB getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        initAll();
    }

    private void initAll() {
        initLog();
        initRequestServiceMap();
        initGoogle();
        initFireBase();
        initAppsFlyer();
    }

    // init it in ur application
    public void initLog() {
        final LogUtils.Config config = LogUtils.getConfig()
                //TODO 上线:需要关闭log输出
//                .setLogSwitch(BuildConfig.DEBUG)// 设置 log 总开关，包括输出到控制台和文件，默认开
//                .setConsoleSwitch(BuildConfig.DEBUG)// 设置是否输出到控制台开关，默认开
                .setGlobalTag(null)// 设置 log 全局标签，默认为空
                // 当全局标签不为空时，我们输出的 log 全部为该 tag，
                // 为空时，如果传入的 tag 为空那就显示类名，否则显示 tag
                .setLogHeadSwitch(true)// 设置 log 头信息开关，默认为开
                .setLog2FileSwitch(false)// 打印 log 时是否存到文件的开关，默认关
                .setDir("")// 当自定义路径为空时，写入应用的/cache/log/目录中
                .setFilePrefix("")// 当文件前缀为空时，默认为"util"，即写入文件为"util-MM-dd.txt"
                .setBorderSwitch(true)// 输出日志是否带边框开关，默认开
                .setSingleTagSwitch(true)// 一条日志仅输出一条，默认开，为美化 AS 3.1 的 Logcat
                .setConsoleFilter(LogUtils.V)// log 的控制台过滤器，和 logcat 过滤器同理，默认 Verbose
                .setFileFilter(LogUtils.V)// log 文件过滤器，和 logcat 过滤器同理，默认 Verbose
                .setStackDeep(1)// log 栈深度，默认为 1
                .setStackOffset(0);// 设置栈偏移，比如二次封装的话就需要设置，默认为 0
        LogUtils.d(config.toString());
    }

    private void initAppsFlyer() {
        ClassAF.S_APPLICATION_MAIN.initAppsFlyerMain(this);
    }

    private void initFireBase() {

    }
    private void initGoogle() {

    }


    private void initRequestServiceMap() {
        ClassAF applicationMain = ClassAF.S_APPLICATION_MAIN;
        //android 9.0+ , WebView crash caused by more process
        applicationMain.webViewSetPath(getApplicationContext());
        initCacheWebView();

    }
//    private void initCacheWebDemo() {
//        WebViewCacheInterceptor.Builder builder =  new WebViewCacheInterceptor.Builder(this);
//
//        //设置okhttp缓存路径，默认getCacheDir，名称CacheWebViewCache
//        builder.setCachePath(new File(this.getCacheDir(),"cache_path_name"))
//                .setDynamicCachePath(new File(this.getCacheDir(),"dynamic_webview_cache"))
//                .setCacheSize(1024*1024*100)//设置缓存大小，默认100M
//                .setConnectTimeoutSecond(20)//设置http请求链接超时，默认20秒
//                .setReadTimeoutSecond(20);//设置http请求链接读取超时，默认20秒
//        CacheExtensionConfig cacheExtensionConfig = new CacheExtensionConfig();
//        cacheExtensionConfig.removeExtension("html").removeExtension("json");
//        builder.setCacheExtensionConfig(cacheExtensionConfig);
//        builder.setDebug(true);
//        builder.setResourceInterceptor(new ResourceInterceptor() {
//            @Override
//            public boolean interceptor(String url) {
//                return true;
//            }
//        });
//        WebViewCacheInterceptorInst.getInstance().
//                init(builder);
//    }

    private void initCacheWebView() {
        WebViewCacheInterceptor.Builder builder = new WebViewCacheInterceptor.Builder(this);
        builder.setCachePath(new File(getCacheDir(), "cache_path_name")) //设置缓存路径，默认getCacheDir，名称CacheWebViewCache
                .setDynamicCachePath(new File(getCacheDir(), "dynamic_webview_cache"))
                .setCacheSize(1024 * 1024 * 100)//设置缓存路径，默认getCacheDir，名称CacheWebViewCache
                .setConnectTimeoutSecond(100)//设置http请求链接超时，默认20秒
                .setReadTimeoutSecond(100)//设置http请求链接读取超时，默认20秒
                .setCacheType(CacheType.NORMAL);//设置缓存为正常模式，默认模式为强制缓存静态资源
        //通过后缀来判断缓存静态文件，可以添加删除
        CacheExtensionConfig cacheExtensionConfig = new CacheExtensionConfig();
        cacheExtensionConfig.removeExtension("html").removeExtension("json");
        builder.setDebug(true);
        builder.setCacheExtensionConfig(cacheExtensionConfig);
        WebViewCacheInterceptorInst.getInstance().init(builder);
        //TODO 开了一个新线程启动service new了一个webview  看不懂是什么操作
    }

}
