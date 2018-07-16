package com.szxmrt.app.facerecognitionsystem.face;

import android.support.annotation.NonNull;
import android.util.Log;

import com.szxmrt.app.facerecognitionsystem.util.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class GetBaiduToken {
	private static String tokenUrl = "https://aip.baidubce.com/oauth/2.0/token?grant_type=client_credentials&"+
			"client_id=GOFqNgodRz7B5rvlS2QypU93&client_secret=7hGBYaYUCq3pXiRvd6hgInWUgjTojESQ";
	private MediaType json = MediaType.parse("application/json; charset=utf-8");
	private OkHttpClient okHttpClient = new OkHttpClient();
	public GetBaiduToken(){

	}
	public String getToken(){
		RequestBody body = RequestBody.create(json,"");
		String token = "";
		final Request request = new Request.Builder().url(tokenUrl).post(body).build();
		try {
			Response response = okHttpClient.newCall(request).execute();
			if (response!=null){
				String res = response.body().string();
				JSONObject jsonObject = new JSONObject(res);
				token = jsonObject.optString("access_token",null);
			}
			return token;
		} catch (IOException | JSONException e) {
			e.printStackTrace();
			return token;
		}
	}
}
