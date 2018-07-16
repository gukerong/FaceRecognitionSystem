package com.szxmrt.app.facerecognitionsystem.camera;

import android.hardware.Camera;
import android.support.annotation.IntDef;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;

/**
 * 相机控制接口
 * Created by Administrator on 2018-06-15
 */

public interface ICameraControl {
	boolean faceRecognitionFlag = false;
	int CAMERA_FACING_BACK = 0;

	int CAMERA_FACING_FRONT = 1;

	int CAMERA_USB = 2;
	@IntDef({CAMERA_FACING_FRONT, CAMERA_FACING_BACK, CAMERA_USB})
	@interface CameraFacing {}

	/**
	 * 	垂直方向{@link #setDisplayOrientation(int)}
	 */

	int ORIENTATION_PORTRAIT = 0;
	/**
	 * 水平方向{@link #setDisplayOrientation(int)}
	 */
	 //
	int ORIENTATION_HORIZONTAL = 1;
	/**
	 * 水平翻转方向{@link #setDisplayOrientation(int)}
	 */
	 int ORIENTATION_INVERT = 2;
	/**
	 * 垂直翻转方向{@link #setDisplayOrientation(int)}
	 */
	int ORIENTATION_VERTICALLY = 3;

	@IntDef({ORIENTATION_PORTRAIT, ORIENTATION_HORIZONTAL, ORIENTATION_INVERT,ORIENTATION_VERTICALLY})
	@interface Orientation {}
	// android5.0之前使用camera1
	int CAMERA1 = 1;
	// android5.0之后使用camera2
	int CAMERA2 = 2;

	@IntDef({CAMERA1,CAMERA2})
	@interface CameraType{}
	//  打开相机
	void openCamera();
	//  关闭相机
	void stopCamera();
	//  停止预览
	void pauseCamera();
	//  开启预览
	void openPreview();
	//  设置打开的相机（前置、后置、USB）
	void setCameraFacing(@CameraFacing int cameraFacing);
	void setDisplayOrientation(@Orientation int orientation);
	void setPreviewSize(int width,int height);
	void setPreviewView(TextureView textureView);
	void setSurfaceHolder(SurfaceHolder surfaceHolder);
	void setPreviewCallback(PreviewCallbackListener listener);
	public interface PreviewCallbackListener{
		void onPreviewFrame();
	}

}
