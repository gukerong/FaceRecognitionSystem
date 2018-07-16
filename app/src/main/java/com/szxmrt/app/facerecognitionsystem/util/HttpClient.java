package com.szxmrt.app.facerecognitionsystem.util;

import android.util.Log;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * http请求客户端
 * Created by xjh on 2017/1/11.
 */
public class HttpClient {

    /** 超时时间*/
    private static final long TIME_OUT = 10;

    private static OkHttpClient mClient;

    static {
        OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder();
        /* 连接超时时间*/
        okHttpBuilder.connectTimeout(TIME_OUT, TimeUnit.SECONDS);
        /* 读取超时时间*/
        okHttpBuilder.readTimeout(TIME_OUT, TimeUnit.SECONDS);
        /* 写入超时时间*/
        okHttpBuilder.writeTimeout(TIME_OUT, TimeUnit.SECONDS);
        /* 允许重定向*/
        okHttpBuilder.followRedirects(true);

        mClient = okHttpBuilder.build();
    }

    /**
     * 同步get请求
     * @param url
     */
    public static HttpResponse get(String url) {
        try {
            Request request = new Request.Builder().url(url).addHeader("Accept", "application/json").build();
            Log.d("HttpRequest", url);
            Response response = mClient.newCall(request).execute();
            if(response.isSuccessful()) {
                HttpResponse httpResponse = new HttpResponse(response.body().string());
                return httpResponse;
            } else {
                throw new Exception("网络异常");
            }
        } catch (Exception e) {
            return new HttpResponse(e);
        }
    }

    /**
     * 同步post请求(参数为键值对)
     * @param url
     * @param params
     */
    public static HttpResponse post(String url, Map<String, String> params) {
        try {
            FormBody.Builder builder = new FormBody.Builder();
            if(params != null) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    builder.add(entry.getKey(), entry.getValue());
                }
            }
            FormBody formBody = builder.build();
            Request request = new Request.Builder().url(url).post(formBody).build();
            Response response = mClient.newCall(request).execute();
            if(response.isSuccessful()) {
                HttpResponse httpResponse = new HttpResponse(response.body().string());
                return httpResponse;
            } else {
                throw new Exception("网络异常");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new HttpResponse(e);
        }
    }

    /**
     * post请求（参数为json）
     * @param url
     * @param json
     * @return
     */
    public static HttpResponse post(String url, String json) throws SocketTimeoutException{
	    try {
		    RequestBody body = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded; charset=utf-8"), json);
		    Request request = new Request.Builder().url(url).post(body).build();
		    Response response = mClient.newCall(request).execute();
		    if(response!=null && response.isSuccessful()) {
		    	ResponseBody s = response.body();
		    	if (s!=null){
				    return new HttpResponse(s.string());
			    }else {
		    		return new HttpResponse("error");
			    }
		    }else {
			    return new HttpResponse("error");
		    }
	    } catch (IOException e) {
		    e.printStackTrace();
		    return new HttpResponse("error");
	    }

    }


}
