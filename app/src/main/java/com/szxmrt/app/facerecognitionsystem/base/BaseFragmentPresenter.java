package com.szxmrt.app.facerecognitionsystem.base;

import java.lang.ref.WeakReference;

/**
 * Created by Administrator on 2018-04-21
 */

public abstract class BaseFragmentPresenter<F extends BaseFragment>{
    private WeakReference<F> weakReference;
    public BaseFragmentPresenter(F activty){
        weakReference = new WeakReference<F>(activty);
    }
    public F getActivity(){
        return weakReference.get();
    }
    public void onDestory(){
        if(weakReference!=null){
            weakReference.clear();
            weakReference = null;
        }
    }
}
