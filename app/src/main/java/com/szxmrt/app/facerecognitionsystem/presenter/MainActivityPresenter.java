package com.szxmrt.app.facerecognitionsystem.presenter;

import android.util.Log;

import com.szxmrt.app.facerecognitionsystem.activity.MainActivity;
import com.szxmrt.app.facerecognitionsystem.base.BaseActivityPresenter;
import com.szxmrt.app.facerecognitionsystem.face.FaceDetectResponse;
import com.szxmrt.app.facerecognitionsystem.face.FaceIdentifyResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2018-06-06
 */

public class MainActivityPresenter extends BaseActivityPresenter<MainActivity> {
	private MainActivity activity;
	public MainActivityPresenter(MainActivity activity) {
		super(activity);
		this.activity = activity;
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
