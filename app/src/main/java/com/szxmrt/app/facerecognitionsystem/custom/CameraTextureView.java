package com.szxmrt.app.facerecognitionsystem.custom;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;

import com.szxmrt.app.facerecognitionsystem.util.LogUtil;

/**
 * 自定义surfaceView
 * Created by Administrator on 2018-06-06
 */

public class CameraTextureView extends TextureView implements TextureView.SurfaceTextureListener{
	private static String TAG = CameraTextureView.class.getSimpleName();
	private SurfaceTextureListener listener;
	private SurfaceTexture surfaceTexture;
	public CameraTextureView(Context context) {
		super(context);
	}

	public CameraTextureView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LogUtil.e(TAG,"CameraTextureView");
		setSurfaceTextureListener(this);
	}

	public CameraTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	public CameraTextureView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	@Override
	public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
		LogUtil.e(TAG,"onSurfaceTextureAvailable");
		surfaceTexture = surface;
		if(listener!=null){
			listener.callback(surface);
		}
	}

	@Override
	public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
		LogUtil.e(TAG,"onSurfaceTextureSizeChanged");
	}

	@Override
	public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
		LogUtil.e(TAG,"onSurfaceTextureDestroyed");
		return true;
	}

	@Override
	public void onSurfaceTextureUpdated(SurfaceTexture surface) {
		//LogUtil.e(TAG,"onSurfaceTextureUpdated");

	}
	public void setSurfaceTextureListener(SurfaceTextureListener listener){
		this.listener = listener;
	}
	public interface SurfaceTextureListener{
		void callback(SurfaceTexture surfaceTexture);
	}

}
