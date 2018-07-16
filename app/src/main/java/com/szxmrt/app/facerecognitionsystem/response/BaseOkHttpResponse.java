package com.szxmrt.app.facerecognitionsystem.response;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import okhttp3.Call;
import okhttp3.Response;
import static com.google.gson.internal.$Gson$Types.canonicalize;

/**
 * Created by Administrator on 2018-06-23
 */

public abstract class BaseOkHttpResponse<T> {
	public Type type;
	public abstract void onBeforeRequest();
	public abstract void onFailure(Call call, Exception e);
	public abstract void onSuccess(Response response , T t);
	public abstract void onError(Response response , int code , Exception e);
	public abstract void onTokenError(Response response , int code);

	public BaseOkHttpResponse(){
		type = getSuperclassTypeParameter(getClass());
	}
	private static Type getSuperclassTypeParameter(Class<?> subclass) {
		Type superclass = subclass.getGenericSuperclass();
		if (superclass instanceof Class)
		{
			throw new RuntimeException("Missing type parameter.");
		}
		ParameterizedType parameterized = (ParameterizedType) superclass;
		return canonicalize(parameterized.getActualTypeArguments()[0]);
	}
}
