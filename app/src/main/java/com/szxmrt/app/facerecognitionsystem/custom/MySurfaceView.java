package com.szxmrt.app.facerecognitionsystem.custom;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.szxmrt.app.facerecognitionsystem.util.LogUtil;

/**
 * Created by Administrator on 2018-06-08
 */

public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback{
	private SurfaceViewCreateListener listener;
	public MySurfaceView(Context context) {
		super(context);
		LogUtil.e("MySurfaceView","1");
	}

	public MySurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LogUtil.e("MySurfaceView","2");
		getHolder().addCallback(this);
	}

	public MySurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		LogUtil.e("MySurfaceView","3");

	}

	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	public MySurfaceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		LogUtil.e("MySurfaceView","4");
	}
	public void setSurfaceViewCreateListener(SurfaceViewCreateListener listener){
		this.listener = listener;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		LogUtil.e("MySurfaceView","surfaceCreated");
		if(listener!=null){
			listener.callback(holder);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		LogUtil.e("MySurfaceView","surfaceChanged");
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		LogUtil.e("MySurfaceView","surfaceDestroyed");
	}

	public interface SurfaceViewCreateListener{
		void callback(SurfaceHolder surfaceHolder);
	}
}
