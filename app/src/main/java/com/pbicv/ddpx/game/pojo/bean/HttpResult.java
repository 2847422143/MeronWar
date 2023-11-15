package com.pbicv.ddpx.game.pojo.bean;

public class HttpResult {

    /**
     * boot : 1, // 1: 打开正式页面，2: 打开假页面
     * isTarget : 1, // IP 判断，0: 非目标国家用户，1: 目标国家用户
     * bucket : 3, // 分桶 ID，用来做网站内部分流测试
     * cleanCache : 2, // 1: 清理 Webview 缓存，2: 不清理 Webview
     * url : "https://h5.pocket-trade.net/" // 分桶后的网站正式地址
     */

    private int boot;
    private int isTarget;
    private int bucket;
    private int cleanCache;
    private String url;

    public int getBoot() {
        return boot;
    }

    public void setBoot(int boot) {
        this.boot = boot;
    }

    public int getIsTarget() {
        return isTarget;
    }

    public void setIsTarget(int isTarget) {
        this.isTarget = isTarget;
    }

    public int getBucket() {
        return bucket;
    }

    public void setBucket(int bucket) {
        this.bucket = bucket;
    }

    public int getCleanCache() {
        return cleanCache;
    }

    public void setCleanCache(int cleanCache) {
        this.cleanCache = cleanCache;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "LoginResult{" +
                "boot=" + boot +
                ", isTarget=" + isTarget +
                ", bucket=" + bucket +
                ", cleanCache=" + cleanCache +
                ", url='" + url + '\'' +
                '}';
    }
}
