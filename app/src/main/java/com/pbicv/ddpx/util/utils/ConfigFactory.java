package com.pbicv.ddpx.util.utils;


import com.pbicv.ddpx.game.pojo.bean.HttpResult;

public class ConfigFactory {
    private HttpResult mHttpResult;
    private int mVestBoot = 2; //1：打开正式页面 2：打开假页面   默认打开假页面

    private ConfigFactory(){}

    public static final ConfigFactory getInstance() {
        return LazyHolder.INSTANCE;
    }

    private static class LazyHolder {
        private static final ConfigFactory INSTANCE = new ConfigFactory();
    }

    public void setVestBoot(int vestBoot){
        mVestBoot = vestBoot;
    }

    public int getVestBoot(){
        return mVestBoot;
    }

    public void setLoginResult(HttpResult httpResult){
        mHttpResult = httpResult;
    }

    public HttpResult getLoginResult(){
        return mHttpResult;
    }
}
