package com.pbicv.ddpx.game.network.api;



import com.pbicv.ddpx.game.pojo.bean.HttpResponse;
import com.pbicv.ddpx.game.pojo.bean.HttpResult;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.Header;


public interface HostService {
    /**
     * 请求马甲服务器
     * @param
     * @return
     */
    @GET("v1/home/app")
    Observable<HttpResponse<HttpResult>> getHostUrl(@Header("X-APP-CODE") String pkgId , @Header("X-DEVICE-INFO") String deviceId/* ,@Header("X-Forwarded-For") String ip*/ );

}
