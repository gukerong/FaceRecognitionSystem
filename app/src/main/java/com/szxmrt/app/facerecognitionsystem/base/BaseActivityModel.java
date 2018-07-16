package com.szxmrt.app.facerecognitionsystem.base;

import java.lang.ref.WeakReference;

/**
 * Created by Administrator on 2018-06-22
 */

public abstract class BaseActivityModel<A extends BaseActivity>{
	private WeakReference<A> weakReference;
	public BaseActivityModel(A activity){
		weakReference = new WeakReference<A>(activity);
	}
	public A getActivity(){
		return weakReference.get();
	}
	public void onDestroy(){
		if(null != weakReference){
			weakReference.clear();
			weakReference = null;
		}
	}
}
