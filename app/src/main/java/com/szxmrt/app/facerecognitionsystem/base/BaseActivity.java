package com.szxmrt.app.facerecognitionsystem.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.szxmrt.app.facerecognitionsystem.R;
import com.szxmrt.app.facerecognitionsystem.util.ActivityCollector;
import com.szxmrt.app.facerecognitionsystem.util.LogUtil;

import java.lang.reflect.Method;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2018-04-19
 */

@SuppressLint("Registered")
public abstract class BaseActivity<P extends BaseActivityPresenter,M extends BaseActivityModel> extends AppCompatActivity {
    private P presenter;
    public Unbinder unbinder;
    private M model;
    private static String TAG = BaseActivity.class.getSimpleName();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
	    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        unbinder = ButterKnife.bind(this);
        presenter = initPresenter();
        model = initModel();
        ActivityCollector.addActivity(this);
        initVariables();
        initListener();
        setContentPaddingTop(this);
	    //initBottomUIMenu();
	    getScreenInfo();
    }
    public P getPresenter(){ return presenter; }
    public M getModel(){ return model; }
    public void initBottomUIMenu(){
        if(Build.VERSION.SDK_INT<19){
            View view = this.getWindow().getDecorView();
            view.setSystemUiVisibility(View.GONE);
        }else {
            View view = this.getWindow().getDecorView();
            int visibility = View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            view.setSystemUiVisibility(visibility);
        }
    }
	public void showToast(final String msg, final Activity activity){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast toast = Toast.makeText(activity,msg,Toast.LENGTH_SHORT);
				toast.show();
			}
		});
	}
	public void getScreenInfo(){
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		LogUtil.e("dpi", String.valueOf(displayMetrics.densityDpi));
		LogUtil.e("density",String.valueOf(displayMetrics.density));
		LogUtil.e("screen","width : "+displayMetrics.widthPixels+"  height : "+displayMetrics.heightPixels);
	}
    public abstract int getLayoutId();
    public abstract P initPresenter();
    public abstract void initVariables();
    public abstract void initListener();
	public abstract M initModel();
	@Override
	protected void onDestroy() {
		super.onDestroy();
		presenter.onDestory();
		model.onDestroy();
	}
	/**
	 * 利用反射获取状态栏高度
	 */
	private int getStatusBarHeight() {
		int result = 0;
		//获取状态栏高度的资源id
		int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}
	private void setContentPaddingTop(Activity activity){
		//设置 paddingTop
		ViewGroup rootView = (ViewGroup) getWindow().getDecorView().findViewById(android.R.id.content);
		rootView.setPadding(0, getStatusBarHeight(), 0, 0);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		//banStatusBar();
	}

/*	public static final int DISABLE_EXPAND = 0x00010000;//4.2以上的整形标识
	public static final int DISABLE_EXPAND_LOW = 0x00000001;//4.2以下的整形标识
	public static final int DISABLE_NONE = 0x00000000;//取消StatusBar所有disable属性，即还原到最最原始状态

	private void unBanStatusBar() {//利用反射解除状态栏禁止下拉
		Object service = getSystemService("statusbar");
		try {
			Class<?> statusBarManager = Class.forName("android.app.StatusBarManager");
			Method expand = statusBarManager.getMethod("disable", int.class);
			expand.invoke(service, DISABLE_NONE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setStatusBarDisable(int disable_status) {//调用statusBar的disable方法
		Object service = getSystemService("statusbar");
		try {
			Class<?> statusBarManager = Class.forName("android.app.StatusBarManager");
			Method expand = statusBarManager.getMethod("disable", int.class);
			expand.invoke(service, disable_status);
		} catch (Exception e) {
			unBanStatusBar();
			e.printStackTrace();
		}
	}

	private void banStatusBar() {//禁止statusbar下拉，适配了高低版本
		int currentApiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentApiVersion <= 16) {
			setStatusBarDisable(DISABLE_EXPAND_LOW);
		} else {
			setStatusBarDisable(DISABLE_EXPAND);
		}
	}*/
}
