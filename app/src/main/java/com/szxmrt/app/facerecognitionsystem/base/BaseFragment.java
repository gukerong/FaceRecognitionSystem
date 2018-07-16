package com.szxmrt.app.facerecognitionsystem.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2018-04-21
 */

public abstract class BaseFragment<P extends BaseFragmentPresenter> extends Fragment {
    private Unbinder unbinder;
    private P presenter;
    public Activity activity;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(initLayout(),container);
        unbinder = ButterKnife.bind(this,view);
        presenter = initPresenter();
        activity = getActivity();
        initVariables();
        initListener();
        return view;
    }
    public P getPresenter(){ return presenter; }
    public abstract int initLayout();
    public abstract P initPresenter();
    public abstract void initVariables();
    public abstract void initListener();

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(unbinder!=null){
            unbinder.unbind();
            unbinder = null;
        }
    }
}
