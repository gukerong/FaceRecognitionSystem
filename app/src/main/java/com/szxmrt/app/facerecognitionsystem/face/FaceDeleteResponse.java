package com.szxmrt.app.facerecognitionsystem.face;

import com.szxmrt.app.facerecognitionsystem.activity.SettingActivity;
import com.szxmrt.app.facerecognitionsystem.custom.MyApplication;
import com.szxmrt.app.facerecognitionsystem.entity.User;
import com.szxmrt.app.facerecognitionsystem.response.DeleteGroupUserResponse;
import com.szxmrt.app.facerecognitionsystem.response.FaceGroupGetUserResponse;
import com.szxmrt.app.facerecognitionsystem.util.OkHttpUtils;

import java.io.IOException;
import java.util.List;

public class FaceDeleteResponse {
	private String deleteUsersUrl = "https://aip.baidubce.com/rest/2.0/face/v2/faceset/group/deleteuser?access_token=";
	private String token = "";
	private OkHttpUtils okHttpUtils;
	private DeleteGroupUserResponse response;
	public FaceDeleteResponse( ) {
		okHttpUtils = OkHttpUtils.getOkHttpUtils();
		token = MyApplication.getToken();
	}
	public boolean deleteUser(String group_id,String uid) throws IOException {
		response = new DeleteGroupUserResponse();
		String param = "group_id=" + group_id + "&uid=" + uid;
		okHttpUtils.synPostJson(deleteUsersUrl+token,param,OkHttpUtils.URLENCODED,response);
		return response.getResult();
	}
}
