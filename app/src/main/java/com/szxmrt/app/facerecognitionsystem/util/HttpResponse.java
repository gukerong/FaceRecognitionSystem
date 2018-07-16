package com.szxmrt.app.facerecognitionsystem.util;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

/**
 * http请求的响应
 * Created by xjh on 2017/1/11.
 */
public class HttpResponse {

    private String bodyString;

    private Exception exception;

    public HttpResponse() {
    }

    public HttpResponse(String bodyString) {
        this.bodyString = bodyString;
    }

    public HttpResponse(Exception exception) {
        this.exception = exception;
    }

    public String getBodyString() {
        return bodyString;
    }
    public void setBodyString(String bodyString) {
        this.bodyString = bodyString;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public int getRes(){
        try {
            JSONObject jsonObject = new JSONObject(bodyString);
            int res = jsonObject.optInt("res",404);
            Log.e("Res  :  ",res+"");
            return res;
        } catch (JSONException e) {
            e.printStackTrace();
            return 404;
        }
    }
    public Map<String,String> getRanks(){
        try {
            JSONObject jsonObject = new JSONObject(bodyString);
            JSONObject resultJson = jsonObject.getJSONObject("detector_results");
            //Logger.e("Ranks  :  " + resultJson.toString());
            JSONArray ranksJson = resultJson.getJSONArray("ranks");
            //Logger.e("Ranks  :  " + ranksJson.toString());
            JSONArray ranksArray = ranksJson.getJSONArray(0);
            //Logger.e("Ranks  :  " + ranksArray.toString());
            Map<String,String> map = new HashMap<>();
            map.put("product_code",ranksArray.getString(0));
            map.put("rate",ranksArray.getString(1));
            Log.e("product_code  :  ", ranksArray.getString(0));
            Log.e("rate  :  ",ranksArray.getString(1));
            return map;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
