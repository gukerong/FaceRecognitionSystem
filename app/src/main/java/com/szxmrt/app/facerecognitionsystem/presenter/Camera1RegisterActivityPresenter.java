package com.szxmrt.app.facerecognitionsystem.presenter;

import android.util.Log;

import com.szxmrt.app.facerecognitionsystem.activity.Camera1RegisterActivity;
import com.szxmrt.app.facerecognitionsystem.activity.RegisterActivity;
import com.szxmrt.app.facerecognitionsystem.base.BaseActivityPresenter;
import com.szxmrt.app.facerecognitionsystem.face.FaceDetectResponse;
import com.szxmrt.app.facerecognitionsystem.face.FaceIdentifyResponse;
import com.szxmrt.app.facerecognitionsystem.face.FaceRegisterResponse;
import com.szxmrt.app.facerecognitionsystem.util.LogUtil;
import com.szxmrt.app.facerecognitionsystem.util.SharedPreferencesUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018-06-09
 */

public class Camera1RegisterActivityPresenter extends BaseActivityPresenter<Camera1RegisterActivity> {
	private static final String CONFIG = "drinkVMConfig";//存放用户信息文件名
	private SharedPreferencesUtil sharedPreferencesUtil;
	private long log_id;
	public Camera1RegisterActivityPresenter(Camera1RegisterActivity activity) {
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
			if(error_code==null){
				log_id = jsonObject.getLong("log_id");
			}
			return error_code == null;
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}
	}
	public int faceIdentify(final byte[] bytes){
		try {
			FaceDetectResponse faceDetectResponse = FaceDetectResponse.getBaiduFace();
			faceDetectResponse.faceDetection(bytes);
			FaceIdentifyResponse faceIdentifyResponse = FaceIdentifyResponse.getRegister();
			String body = faceIdentifyResponse.identify(bytes);
			String uid = "";
			String group_id = "";
			String user_info = "";
			int result_num = 0;
			double score = 0;
			JSONObject jsonObject = new JSONObject(body);
			int error_code = jsonObject.optInt("error_code",404);
			if(error_code==404){
				result_num = jsonObject.getInt("result_num");
				if(result_num==1){
					JSONArray resultArray = jsonObject.getJSONArray("result");
					JSONObject jsonObject1 = resultArray.getJSONObject(0);
					uid = jsonObject1.getString("uid");
					JSONArray scores = jsonObject1.getJSONArray("scores");
					score = scores.getDouble(0);
					group_id = jsonObject1.getString("group_id");
					user_info  = jsonObject1.getString("user_info");
					Log.e("face","score  "+score+"  uid  "+uid+"  group_id  "+group_id+"  user_info  "+user_info);
					if(score>=80.0){
						return 1000;

					}else {
						return 1001;
					}
				}else {
					return 1003;
				}
			}else {
				return 1004;
			}
		}catch (JSONException e){
			e.printStackTrace();
			return 1005;
		}
	}
}
