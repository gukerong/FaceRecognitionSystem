package com.szxmrt.app.facerecognitionsystem.face;

import com.szxmrt.app.facerecognitionsystem.custom.MyApplication;
import com.szxmrt.app.facerecognitionsystem.entity.User;
import com.szxmrt.app.facerecognitionsystem.response.FaceGroupGetUserResponse;
import com.szxmrt.app.facerecognitionsystem.util.OkHttpUtils;

import java.io.IOException;
import java.util.List;

public class FaceListResponse {
	private String deleteUsersUrl = "https://aip.baidubce.com/rest/2.0/face/v3/faceset/face/getlist?access_token=";
	private String token = "";
	private OkHttpUtils okHttpUtils;
	private FaceGroupGetUserResponse response;
	public FaceListResponse( ) {
		okHttpUtils = OkHttpUtils.getOkHttpUtils();
		token = MyApplication.getToken();
	}
	public List<User> faceList(String user_id,String group_id) throws IOException {
		response = new FaceGroupGetUserResponse();
		String param = "user_id=" + user_id + "group_id=" + "test";
		okHttpUtils.synPostJson(deleteUsersUrl+token,param,OkHttpUtils.URLENCODED,response);
		return response.getUsers();
	}
}
