package com.szxmrt.app.facerecognitionsystem.base;

import android.support.v7.app.AppCompatActivity;

import java.lang.ref.WeakReference;

/**
 * 业务逻辑处理基类
 * Created by Administrator on 2018-04-19
 */

public abstract class BaseActivityPresenter<A extends AppCompatActivity> {
    private WeakReference<A> weakReference;
    public BaseActivityPresenter(A activity){
        weakReference = new WeakReference<A>(activity);
    }
    public A getActivity(){
        return weakReference.get();
    }
    public void onDestory(){
        if(null != weakReference){
            weakReference.clear();
            weakReference = null;
        }
    }
}
