package com.szxmrt.app.facerecognitionsystem.face;

import android.support.annotation.NonNull;

import com.szxmrt.app.facerecognitionsystem.custom.MyApplication;
import com.szxmrt.app.facerecognitionsystem.util.Base64Util;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 百度人脸活体检测
 * Created by Administrator on 2018-04-16
 */

public class FaceverifyResponse {
    private OkHttpClient okHttpClient;
    private MediaType json;
    private MediaType detectJson;
    private OkHttpClient.Builder builder;
    private String faceverifydUrl = "https://aip.baidubce.com/rest/2.0/face/v2/faceverify?access_token=";
    private String token = "";
    private JSONObject jsonObject;
    private static FaceverifyResponse faceverifyResponse;
    private FaceverifyCallbackListener listener;
    private FaceverifyResponse(){
        okHttpClient = new OkHttpClient();
        json = MediaType.parse("application/json; charset=utf-8");
        detectJson = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
        builder = new OkHttpClient.Builder();
        jsonObject = new JSONObject();
        token = MyApplication.getToken();
    }
    public static FaceverifyResponse getFaceverify(){
        if(faceverifyResponse==null){
            synchronized (FaceverifyResponse.class){
                if(faceverifyResponse==null){
                    faceverifyResponse = new FaceverifyResponse();
                }
            }
        }
        return faceverifyResponse;
    }
    public void faceverify(final byte[] bytes){
        //setToken();
        String imgStr = Base64Util.encode(bytes);
        String imgParam = null;
        try {
            imgParam = URLEncoder.encode(imgStr, "UTF-8");
            String param = "image=" + imgParam + "&face_fields="+"faceliveness";
            RequestBody body = RequestBody.create(detectJson,param);
            Request request = new Request.Builder().url(faceverifydUrl+token).post(body).build();
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                }
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    ResponseBody responseBody = response.body();
                    if(responseBody!=null){
                        String body = responseBody.string();
                        if(listener != null){
                            listener.faceverifyCallback(body,bytes);
                        }
/*                        try {
                            JSONObject jsonObject = new JSONObject(body);
                            String num = jsonObject.getString("result_num");
                            JSONArray jsonArray = jsonObject.getJSONArray("result");
                            JSONObject jsonObject1 = (JSONObject) jsonArray.get(0);
                            Double faceliveness = jsonObject1.getDouble("faceliveness");
                            Logger.e(String.valueOf(faceliveness));
                            if("1".equals(num)){

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }*/

                    }
                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    public void setFaceverifyCallback(FaceverifyCallbackListener listener){
        this.listener = listener;
    }
    public interface FaceverifyCallbackListener{
        void faceverifyCallback(String body, byte[] bytes);
    }
}
