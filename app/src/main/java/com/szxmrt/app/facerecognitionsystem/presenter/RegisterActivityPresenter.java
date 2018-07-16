package com.szxmrt.app.facerecognitionsystem.presenter;

import com.szxmrt.app.facerecognitionsystem.activity.RegisterActivity;
import com.szxmrt.app.facerecognitionsystem.base.BaseActivityPresenter;
import com.szxmrt.app.facerecognitionsystem.face.FaceRegisterResponse;
import com.szxmrt.app.facerecognitionsystem.util.SharedPreferencesUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018-06-09
 */

public class RegisterActivityPresenter extends BaseActivityPresenter<RegisterActivity> {
	private static final String CONFIG = "drinkVMConfig";//存放用户信息文件名
	private SharedPreferencesUtil sharedPreferencesUtil;
	private long log_id;
	public RegisterActivityPresenter(RegisterActivity activity) {
		super(activity);
		sharedPreferencesUtil = new SharedPreferencesUtil(activity,CONFIG);
	}
	public Boolean faceRegister(byte[] bytes,String uid,String user_Info){
		List<byte[]> list = new ArrayList<>();
		list.add(bytes);
		FaceRegisterResponse registerResponse = FaceRegisterResponse.getRegister();
		String body = registerResponse.register(list,uid,user_Info);
		try {
			JSONObject jsonObject = new JSONObject(body);
			String error_code = jsonObject.optString("error_code",null);
			if(error_code!=null){
				log_id = jsonObject.getLong("log_id");
			}
			return error_code != null;
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}
	}
	public void register(){

	}
}
