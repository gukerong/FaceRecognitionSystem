package com.szxmrt.app.facerecognitionsystem.camera;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.TextureView;

import com.szxmrt.app.facerecognitionsystem.util.LogUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * android5.0以下相机控制类
 * Created by Administrator on 2018-06-15
 *
 * Camera步骤：1、设置cameraFace {@link #setCameraFacing(int)} ，调用{@link #openCamera()} 打开相机
 *            2、设置相机属性 {@link #setDisplayOrientation} {@link #setPreviewSize(int, int)} {@link #setSurfaceTexture(SurfaceTexture)}
 *            3、调用{@link #openPreview()} 打开相机预览
 */

public class Camera1TextureControl implements ICameraControl {
	private static final String TAG = Camera1TextureControl.class.getSimpleName();

	private Context context;
	private Camera camera;
	private int cameraId = 0;
	private int orientation = 0;
	private int previewWidth = 0;
	private int previewHeight = 0;
	private int displayOrientation = 0;
	private TextureView textureView = null;
	private SurfaceTexture surfaceTexture = null;
	private Camera.Parameters parameters;
	private GetCameraParameters getCameraParameters = new GetCameraParameters();
	@CameraFacing
	private int cameraFacing = CAMERA_FACING_BACK;
	public Camera1TextureControl(Context context){
		this.context = context;
	}
	@Override
	public void openCamera() {
		startCamera();
	}
	@Override
	public void stopCamera() {
		if(camera!=null){
			camera.addCallbackBuffer(null);
			camera.setPreviewCallbackWithBuffer(null);
			camera.stopPreview();
			camera.release();
			camera = null;
		}
		LogUtil.i(TAG,"stopCamera");
	}

	@Override
	public void pauseCamera() {
		if (camera!=null){
			camera.stopPreview();
		}
		LogUtil.i(TAG,"pauseCamera");
	}

	@Override
	public void openPreview() {
		startPreview();
	}

	@Override
	public void setCameraFacing(@CameraFacing int cameraFacing) {
		this.cameraFacing = cameraFacing;
		Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
		for(int i=0;i<Camera.getNumberOfCameras();i++){
			Camera.getCameraInfo(i,cameraInfo);
			if(cameraInfo.facing == cameraFacing){
				cameraId = i;
				break;
			}
		}
	}

	@Override
	public void setDisplayOrientation(@Orientation int orientation) {
		displayOrientation = getCameraParameters.getDisplayOrientation(orientation,cameraId);
	}

	@Override
	public void setPreviewSize(int width, int height) {
		if (parameters!=null){
			List<Camera.Size> sizeList = parameters.getSupportedPreviewSizes();
			for (Camera.Size size : sizeList){
				LogUtil.i(TAG,"cameraSize : "+"width : "+size.width+"  ,height : "+size.height);
			}
			Camera.Size size = getCameraParameters.getOptimalSize(width,height,sizeList);
			LogUtil.i(TAG,"optimalSize : "+"width : "+size.width+"  ,height : "+size.height);
			previewHeight = size.height;
			previewWidth = size.width;
		}
	}

	@Override
	public void setPreviewView(TextureView textureView) {

	}

	@Override
	public void setSurfaceHolder(SurfaceHolder surfaceHolder) {
	}

	public void setSurfaceTexture(SurfaceTexture surfaceTexture){
		this.surfaceTexture = surfaceTexture;
	}

	@Override
	public void setPreviewCallback(PreviewCallbackListener listener) {
	}

	public Camera getCamera(){
		return camera;
	}

	private void startCamera(){
		if(this.camera == null){
			this.camera = Camera.open(cameraId);
			this.parameters = camera.getParameters();
			LogUtil.i(TAG,"openCamera " + cameraId);
		}
	}
	private void startPreview(){
		try {
			parameters.setPreviewSize(previewWidth,previewHeight);
			camera.setDisplayOrientation(displayOrientation);
			camera.setParameters(parameters);
			if(context instanceof Camera.PreviewCallback){
				for (int i = 0; i < 3; i++) {
					camera.addCallbackBuffer(new byte[((previewWidth * previewHeight) * ImageFormat.getBitsPerPixel(ImageFormat.NV21)) /8]);
				}
				camera.setPreviewCallbackWithBuffer((Camera.PreviewCallback) context);
			}
			camera.setPreviewTexture(surfaceTexture);
			camera.startPreview();
		} catch (IOException e) {
			e.printStackTrace();
		}
		LogUtil.i(TAG,"startPreview");
	}
}
