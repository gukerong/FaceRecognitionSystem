package com.szxmrt.app.facerecognitionsystem.face;

import android.support.annotation.NonNull;
import android.util.Log;

import com.szxmrt.app.facerecognitionsystem.custom.MyApplication;
import com.szxmrt.app.facerecognitionsystem.util.Base64Util;
import com.szxmrt.app.facerecognitionsystem.util.HttpClient;
import com.szxmrt.app.facerecognitionsystem.util.HttpResponse;
import com.szxmrt.app.facerecognitionsystem.util.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 百度检测请求
 * Created by Administrator on 2018-04-12
 */

public class FaceDetectResponse {
    private OkHttpClient okHttpClient;
    private MediaType json;
    private MediaType detectJson;
    private OkHttpClient.Builder builder;
    private String id = "11088236";
    private String apikey = "GOFqNgodRz7B5rvlS2QypU93";
    private String secretkey = "7hGBYaYUCq3pXiRvd6hgInWUgjTojESQ ";
    private static String tokenUrl = "https://aip.baidubce.com/oauth/2.0/token?grant_type=client_credentials&"+
                                    "client_id=GOFqNgodRz7B5rvlS2QypU93&client_secret=7hGBYaYUCq3pXiRvd6hgInWUgjTojESQ";
    private String detectUrl = "https://aip.baidubce.com/rest/2.0/face/v2/detect?access_token=";
    private JSONObject jsonObject;
    private String token;
    private static FaceDetectResponse baiduFace;
    private FaceCallbackListener listener;
    private FaceDetectResponse(){
        json = MediaType.parse("application/json; charset=utf-8");
        detectJson = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
        builder = new OkHttpClient.Builder();
        okHttpClient = builder.build();
        okHttpClient.newBuilder().connectTimeout(4, TimeUnit.SECONDS);
        okHttpClient.newBuilder().readTimeout(4,TimeUnit.SECONDS);
        okHttpClient.newBuilder().writeTimeout(4,TimeUnit.SECONDS);
        jsonObject = new JSONObject();
        token = MyApplication.getToken();
	    LogUtil.e("token",token);
    }
    public static FaceDetectResponse getBaiduFace(){
        if(baiduFace==null){
            synchronized (FaceDetectResponse.class){
                if(baiduFace==null){
                    baiduFace = new FaceDetectResponse();
                }
            }
        }
        return baiduFace;
    }
    public int faceDetection(final byte[] bytes){
        try {
            String imgStr = Base64Util.encode(bytes);
            String imgParam = URLEncoder.encode(imgStr, "UTF-8");
            String param =  "image=" + imgParam + "&max_face_num=" + 1 + "&face_fields="+"age,beauty,expression,faceshape,gender,glasses,landmark,race,qualities";
            HttpResponse httpResponse = HttpClient.post(detectUrl+token,param);
            String body = httpResponse.getBodyString();
            Log.e("facebody",body);
            if (body.isEmpty() || "error".equals(body)) return 0;
            JSONObject jsonObject = new JSONObject(body);
            if(jsonObject.optString("error_code").isEmpty()){
                return jsonObject.getInt("result_num");
            }
            return 0;
        } catch (UnsupportedEncodingException | JSONException | SocketTimeoutException | NullPointerException e) {
            e.printStackTrace();
            return 0;
        }
    }
    public void setFaceCallback(FaceCallbackListener listener){
        this.listener = listener;
    }
    public interface FaceCallbackListener{
        void faceCallback(String body, byte[] bytes);
    }
}