package com.szxmrt.app.facerecognitionsystem.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2018-06-22
 */

public class User implements Serializable{
	private String uid;
	private String user_info;

	private String group;
	public User(){}
	public User(String uid,String user_info,String group){
		this.uid = uid;
		this.user_info = user_info;
		this.group = group;
	}
	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getUser_info() {
		return user_info;
	}

	public void setUser_info(String user_info) {
		this.user_info = user_info;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	@Override
	public String toString() {
		return "User{" +
				"uid='" + uid + '\'' +
				", user_info='" + user_info + '\'' +
				", group='" + group + '\'' +
				'}';
	}
}
