package com.szxmrt.app.facerecognitionsystem.custom;

import android.app.Application;;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.szxmrt.app.facerecognitionsystem.util.LogUtil;
import com.szxmrt.app.facerecognitionsystem.util.SharedPreferencesUtil;

/**
 * Created by Administrator on 2018-06-15
 */

public class MyApplication extends Application {
	private Handler handler = new Handler(Looper.getMainLooper());
	private SharedPreferencesUtil sharedPreferencesUtil;
	private static String token = "";
	public MyApplication(){

	}
	@Override
	public void onCreate() {
		super.onCreate();
		sharedPreferencesUtil = new SharedPreferencesUtil(this,"drinkVMConfig");
		token = sharedPreferencesUtil.getString("token",null);
	}
	public static String getToken(){
		return token;
	}
}
