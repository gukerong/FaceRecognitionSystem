package com.szxmrt.app.facerecognitionsystem.response;

import com.szxmrt.app.facerecognitionsystem.entity.User;
import com.szxmrt.app.facerecognitionsystem.util.LogUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2018-06-23
 */

public class FaceGroupGetUserResponse extends BaseOkHttpResponse<String>{

	private static String TAG = FaceGroupGetUserResponse.class.getSimpleName();
	private List<User> users = new ArrayList<>();
	private int result_num = 0;
	@Override
	public void onBeforeRequest() {

	}

	@Override
	public void onFailure(Call call, Exception e) {

	}

	@Override
	public void onSuccess(Response response, String resultStr) {
		try {
			List<User> users = new ArrayList<>();
			JSONObject jsonObject = new JSONObject(resultStr);
			String error_code = jsonObject.optString("error_code",null);
			if(error_code != null){
				LogUtil.e(TAG,error_code);
				return;
			}
			JSONArray userArray = jsonObject.getJSONArray("result");
			LogUtil.i(TAG,userArray.toString());
			result_num = jsonObject.getInt("result_num");
			long log_id = jsonObject.getLong("log_id");
			LogUtil.i(TAG,"result_num : "+result_num+"  log_id : "+log_id);
			for(int i=0;i<userArray.length();i++){
				JSONObject userObject = userArray.getJSONObject(i);
				User user = new User(userObject.getString("uid"),userObject.getString("user_info"),null);
				users.add(user);
			}
			this.users = users;
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
	public List<User> getUsers(){
		return this.users;
	}
	public int getUserNum(){
		return this.result_num;
	}
}
