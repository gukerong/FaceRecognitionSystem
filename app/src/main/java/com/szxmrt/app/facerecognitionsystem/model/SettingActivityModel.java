package com.szxmrt.app.facerecognitionsystem.model;

import com.szxmrt.app.facerecognitionsystem.activity.SettingActivity;
import com.szxmrt.app.facerecognitionsystem.base.BaseActivityModel;
import com.szxmrt.app.facerecognitionsystem.entity.User;
import com.szxmrt.app.facerecognitionsystem.response.FaceGroupGetUserResponse;
import com.szxmrt.app.facerecognitionsystem.util.OkHttpUtils;

import java.io.IOException;
import java.util.List;

/**
 * Created by Administrator on 2018-06-22
 */

public class SettingActivityModel extends BaseActivityModel<SettingActivity>{
	private String getUsersUrl = "https://aip.baidubce.com/rest/2.0/face/v2/faceset/group/getusers?access_token=";
	private String token = "24.7124197137e4903780c4a42a6b0b1571.2592000.1531116134.282335-11088236";
	private OkHttpUtils okHttpUtils;
	private FaceGroupGetUserResponse response;
	public SettingActivityModel(SettingActivity activity) {
		super(activity);
		okHttpUtils = OkHttpUtils.getOkHttpUtils();
	}
	public List<User> getGroupUser() throws IOException{
		response = new FaceGroupGetUserResponse();
		String param = "group_id=" + "test" + "&start=" + 0 + "&end=" + 10;
		okHttpUtils.synPostJson(getUsersUrl+token,param,OkHttpUtils.URLENCODED,response);
		return response.getUsers();
	}
	public int getUserNum(){
		return response.getUserNum();
	}
}
