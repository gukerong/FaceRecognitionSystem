package com.szxmrt.app.facerecognitionsystem.util;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.szxmrt.app.facerecognitionsystem.R;
import com.szxmrt.app.facerecognitionsystem.entity.User;
import com.szxmrt.app.facerecognitionsystem.response.BaseOkHttpResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 对okHttp进行封装
 * Created by Administrator on 2018-06-22
 */

public class OkHttpUtils implements IOkHttpUtils{
	private BaseOkHttpResponse okHttpResponse;
	private static String TAG = OkHttpUtils.class.getSimpleName();
	private static OkHttpClient okHttpClient;
	private static OkHttpUtils okHttpUtils;
	private Gson gson;
	static {
		okHttpClient = new OkHttpClient();
		okHttpClient.newBuilder().connectTimeout(10, TimeUnit.SECONDS);
		okHttpClient.newBuilder().readTimeout(10,TimeUnit.SECONDS);
		okHttpClient.newBuilder().writeTimeout(10,TimeUnit.SECONDS);
	}
	public enum HttpMethodType{
		GET,
		POST,
	}
	private OkHttpUtils(){
		gson = new Gson();
	}
	public static OkHttpUtils getOkHttpUtils(){
		if(okHttpUtils == null){
			okHttpUtils = new OkHttpUtils();
		}
		return okHttpUtils;
	}
	//同步get请求
	public void doSynGetRequest(String url) throws IOException {
		Request request = new Request.Builder().url(url).build();
		Response response = okHttpClient.newCall(request).execute();
		LogUtil.e(TAG,response.body().string());
	}
	public void setOkHttpResponse(BaseOkHttpResponse baseOkHttpResponse){
		this.okHttpResponse = okHttpResponse;
	}
/*	//异步get请求
	public void doAsynGetRequest(String url){
		Request request = new Request.Builder().url(url).build();
		okHttpClient.newCall(request).enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				e.printStackTrace();
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {

			}
		});
	}
	//同步post提交json数据
	public void doSynPostJsonRequest(String url,String json) throws IOException {
		RequestBody requestBody = RequestBody.create(MediaType.parse(JSON),json);
		Request request = new Request.Builder().url(url).post(requestBody).build();
		okHttpClient.newCall(request).execute();
	}
	//异步post提交json数据
	public void doAsynPostJsonRequest(String url,String json){
		RequestBody requestBody = RequestBody.create(MediaType.parse(JSON),json);
		Request request = new Request.Builder().url(url).post(requestBody).build();
		okHttpClient.newCall(request).enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {

			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {

			}
		});
	}
	//异步post提交键值对
	public void doAsynPostFormRequest(String url,String json,Map<String,Object> map) throws IOException{
		FormBody.Builder builder = new FormBody.Builder();
		RequestBody formBody = builder.build();
		Request request = new Request.Builder().url(url).build();
		okHttpClient.newCall(request).enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {

			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {

			}
		});
	}
	//异步post上传文件
	public void doAsynPostFileRequest(String url,File file){
		RequestBody requestBody = RequestBody.create(MediaType.parse(MARKDOWN),file);
		Request request = new Request.Builder().url(url).post(requestBody).build();
		okHttpClient.newCall(request).enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {

			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {

			}
		});
	}

	*//**
	 * 异步post提交string
	 * 使用HTTP POST提交请求到服务。这个例子提交了一个markdown文档到web服务，以HTML方式渲染markdown。
	 * 因为整个请求体都在内存中，因此避免使用此api提交大文档（大于1MB）
	 * String postBody = ""	+ "Releases\n"
	 *                      + "--------\n"
	 *                      +"\n"
	 *                      + " * zhangfei\n"
	 *                      + " * guanyu\n"
	 *                      + " * liubei\n";
	 *//*
	public void doAsynPostStringRequest(String url,String postBody){
		RequestBody requestBody = RequestBody.create(MediaType.parse(MARKDOWN),postBody);
		Request request = new Request.Builder().url(url).post(requestBody).build();
		okHttpClient.newCall(request).enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {

			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {

			}
		});
	}*/
	public void synPostJson(String url,String json,String mediaType,BaseOkHttpResponse okHttpResponse) throws IOException{
		RequestBody requestBody = RequestBody.create(MediaType.parse(mediaType),json);
		Request request = new Request.Builder().url(url).post(requestBody).build();
		Response response = okHttpClient.newCall(request).execute();
		if(okHttpResponse!=null && response!=null){
			okHttpResponse.onBeforeRequest();
			if(response.isSuccessful()){
				if(okHttpResponse.type == String.class){
					okHttpResponse.onSuccess(response,response.body().string());
				}
			}else {
				LogUtil.e(TAG,"onFailure");
			}
		}
	}
	//异步get请求
	public void aSynGet(String url,BaseOkHttpResponse okHttpResponse) {

		Request request = buildRequest(url,HttpMethodType.GET,null);
		doASynRequest(request, okHttpResponse);
	}
	//异步post提交json
	public void aSynPostJson(String url,String json,String mediaType,BaseOkHttpResponse okHttpResponse){
		Request request = buildRequest(url,json,mediaType);
		doASynRequest(request, okHttpResponse);
	}
	//异步post提交键值对
	public void aSynPostFormBody(String url,Map<String ,String> map,BaseOkHttpResponse okHttpResponse){
		Request request = buildRequest(url,HttpMethodType.POST,map);
		doASynRequest(request, okHttpResponse);
	}
	private Request buildRequest(String url,HttpMethodType httpMethodType,Map<String,String> map){
		Request.Builder builder = new Request.Builder().url(url);
		if(httpMethodType == HttpMethodType.GET){
			builder.get();
		}else if (httpMethodType == HttpMethodType.POST){
			builder.post(buildRequestBody(map));
		}
		return builder.build();
	}
	private Request buildRequest(String url,String parameter,String mediaType){
		return new Request.Builder().url(url).post(buildRequestBody(parameter,mediaType)).build();
	}
	private void doASynRequest(final Request request,final BaseOkHttpResponse okHttpResponse){
		if(okHttpResponse!=null){
			okHttpResponse.onBeforeRequest();
		}
		okHttpClient.newCall(request).enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				e.printStackTrace();
				LogUtil.e(TAG,"onFailure");
				if(okHttpResponse!=null){
					okHttpResponse.onFailure(call,e);
				}
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				LogUtil.e(TAG,response.code()+"");
				if(okHttpResponse ==null) return;
				String resultStr = response.body().string();
				LogUtil.i(TAG,resultStr);
				if(response.isSuccessful()){
					if(okHttpResponse.type == String.class){
						okHttpResponse.onSuccess(response,resultStr);
					}else {
						try {
							Object object = gson.fromJson(resultStr,okHttpResponse.type);
							okHttpResponse.onSuccess(response,object);
						} catch (JsonParseException e){
							okHttpResponse.onError(response,response.code(),e);
						}
					}
				}else {
					okHttpResponse.onError(response,response.code(),null);
				}
			}

		});
	}
	//创建键值对requestBody
	private RequestBody buildRequestBody(Map<String,String> map){
		FormBody.Builder builder = new FormBody.Builder();
		if(map!=null){
			for(Map.Entry<String,String> entry:map.entrySet()){
				builder.add(entry.getKey(),entry.getValue());
			}
		}
		return builder.build();
	}
	//创建json字符串requestBody
	private RequestBody buildRequestBody(String json,String mediaType){
		return  RequestBody.create(MediaType.parse(mediaType),json);
	}

}
