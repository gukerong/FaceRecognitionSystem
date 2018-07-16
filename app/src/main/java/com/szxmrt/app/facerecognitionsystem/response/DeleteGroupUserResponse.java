package com.szxmrt.app.facerecognitionsystem.response;

import com.google.gson.JsonObject;
import com.szxmrt.app.facerecognitionsystem.util.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Response;

public class DeleteGroupUserResponse extends BaseOkHttpResponse<String> {
	private boolean result = false;
	@Override
	public void onBeforeRequest() {

	}

	@Override
	public void onFailure(Call call, Exception e) {

	}

	@Override
	public void onSuccess(Response response, String s) {
		LogUtil.e("s",s);
		try {
			JSONObject jsonObject = new JSONObject(s);
			String error_code = jsonObject.optString("error_code",null);
			result = error_code == null;
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onError(Response response, int code, Exception e) {

	}

	@Override
	public void onTokenError(Response response, int code) {

	}
	public boolean getResult(){
		return result;
	}
}
