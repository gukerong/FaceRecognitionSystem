package com.szxmrt.app.facerecognitionsystem.face;

import android.util.Log;

import com.szxmrt.app.facerecognitionsystem.custom.MyApplication;
import com.szxmrt.app.facerecognitionsystem.util.Base64Util;
import com.szxmrt.app.facerecognitionsystem.util.HttpClient;
import com.szxmrt.app.facerecognitionsystem.util.HttpResponse;
import com.szxmrt.app.facerecognitionsystem.util.LogUtil;

import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;

/**
 * 百度人脸查找
 * Created by Administrator on 2018-04-13
 */

public class FaceIdentifyResponse {
    private OkHttpClient okHttpClient;
    private MediaType json;
    private MediaType detectJson;
    private OkHttpClient.Builder builder;
    private static String tokenUrl = "https://aip.baidubce.com/oauth/2.0/token?grant_type=client_credentials&"+
            "client_id=GOFqNgodRz7B5rvlS2QypU93&client_secret=7hGBYaYUCq3pXiRvd6hgInWUgjTojESQ";
    private static String findUrl = "https://aip.baidubce.com/rest/2.0/face/v2/identify?access_token=";
    private static String token = "";
    //private String findUrl = "https://aip.baidubce.com/rest/2.0/face/v2/identify?access_token=24.7124197137e4903780c4a42a6b0b1571.2592000.1531116134.282335-11088236";
    private JSONObject jsonObject;
    private static FaceIdentifyResponse baiduFace;
    private IdentifyCallbackListener listener;
    private FaceIdentifyResponse(){
    	LogUtil.e("aaa","aaaa");
        okHttpClient = new OkHttpClient();
        json = MediaType.parse("application/json; charset=utf-8");
        detectJson = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
        builder = new OkHttpClient.Builder();
        jsonObject = new JSONObject();
        token = MyApplication.getToken();
	    LogUtil.e("token",token+"aaaaaa");
    }
    public static FaceIdentifyResponse getRegister(){
        if(baiduFace==null){
            synchronized (FaceRegisterResponse.class){
                if(baiduFace==null){
                    baiduFace = new FaceIdentifyResponse();
                }
            }
        }
        return baiduFace;
    }
    public void setToken(String token){
        findUrl = findUrl+token;
    }
    public String identify(final byte[] bytes){
        try {
            String imgStr = Base64Util.encode(bytes);
            String imgParam = URLEncoder.encode(imgStr, "UTF-8");
            String param = "group_id=" + "test" + "&user_top_num=" + "1" + "&face_top_num=" + "1" + "&ext_fields="+"uid,user_info,scores"+"&images=" + imgParam;
            HttpResponse httpResponse = HttpClient.post(findUrl+token,param);
	        String body = httpResponse.getBodyString();
	        LogUtil.e("facebody",httpResponse.getBodyString());
	        return body;
        } catch (UnsupportedEncodingException | SocketTimeoutException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setIdentifyCallback(IdentifyCallbackListener listener){
        this.listener = listener;
    }
    public interface IdentifyCallbackListener{
        void identifyCallback(String body, byte[] bytes);
    }
}
