package com.szxmrt.app.facerecognitionsystem.face;

import android.support.annotation.NonNull;
import android.util.Log;

import com.szxmrt.app.facerecognitionsystem.custom.MyApplication;
import com.szxmrt.app.facerecognitionsystem.util.Base64Util;
import com.szxmrt.app.facerecognitionsystem.util.HttpClient;
import com.szxmrt.app.facerecognitionsystem.util.HttpResponse;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 百度人脸库管理
 * Created by Administrator on 2018-04-13
 */

public class FaceRegisterResponse {
        private OkHttpClient okHttpClient;
        private MediaType json;
        private MediaType detectJson;
        private OkHttpClient.Builder builder;
        private String registerUrl = "https://aip.baidubce.com/rest/2.0/face/v2/faceset/user/add?access_token=";
        private String token = "";
        private JSONObject jsonObject;
        private static FaceRegisterResponse baiduFace;
        private FaceRegisterResponse(){
            okHttpClient = new OkHttpClient();
            json = MediaType.parse("application/json; charset=utf-8");
            detectJson = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
            builder = new OkHttpClient.Builder();
            jsonObject = new JSONObject();
            token = MyApplication.getToken();
        }
        public static FaceRegisterResponse getRegister(){
            if(baiduFace==null){
                synchronized (FaceRegisterResponse.class){
                    if(baiduFace==null){
                        baiduFace = new FaceRegisterResponse();
                    }
                }
            }
            return baiduFace;
        }
        public String register(List<byte[]> bitmaps,String uid,String user_info) {
            List<String> list = new ArrayList<>();
            try {
                for (byte[] bytes : bitmaps){
                    String imgStr = Base64Util.encode(bytes);
                    String imgParam = URLEncoder.encode(imgStr, "UTF-8");
                    list.add(imgParam);
                }
                String param = "uid=" + uid + "&group_id=" + "test" + "&user_info=" + user_info +
                                "&image=" + list.get(0);
	            HttpResponse httpResponse = HttpClient.post(registerUrl+token,param);
	            Log.e("registerBody",httpResponse.getBodyString());
	            return httpResponse.getBodyString();
            }catch (UnsupportedEncodingException | SocketTimeoutException e){
                e.printStackTrace();
                return null;
            }
        }
}
