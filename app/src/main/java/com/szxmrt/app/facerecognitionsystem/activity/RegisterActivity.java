package com.szxmrt.app.facerecognitionsystem.activity;


import android.content.Intent;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.Face;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.szxmrt.app.facerecognitionsystem.R;
import com.szxmrt.app.facerecognitionsystem.base.BaseActivity;
import com.szxmrt.app.facerecognitionsystem.model.RegisterActivityModel;
import com.szxmrt.app.facerecognitionsystem.presenter.RegisterActivityPresenter;
import com.szxmrt.app.facerecognitionsystem.util.CameraUtil;

import java.lang.ref.WeakReference;

import butterknife.BindView;

public class RegisterActivity extends BaseActivity<RegisterActivityPresenter,RegisterActivityModel>implements SurfaceHolder.Callback {
	@BindView(R.id.registerView)
	SurfaceView surfaceView;
	@BindView(R.id.register_Name)
	EditText editName;
	@BindView(R.id.register_Phone)
	EditText editPhone;
	@BindView(R.id.register_Register)
	Button register;
	@BindView(R.id.register_Photo)
	Button photo;
	private HandlerThread handlerThread;
	private Handler handler;
	private CameraUtil cameraUtil;
	private CaptureRequest.Builder builder;
	private SurfaceHolder surfaceHolder;
	boolean recognitionFlag;
	boolean registerFlag;
	@Override
	public int getLayoutId() {return R.layout.activity_register;}

	@Override
	public RegisterActivityPresenter initPresenter() {
		return new RegisterActivityPresenter(this);
	}

	@Override
	public void initVariables() {
		Log.e("registerActivity","aaa");
		recognitionFlag = false;
		registerFlag = false;
		handlerThread = new HandlerThread("camera");
		handlerThread.start();
		handler = new Handler(handlerThread.getLooper());
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(this);
		cameraUtil = new CameraUtil(this,surfaceHolder);
	}

	@Override
	public void initListener() {
		photo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				recognitionFlag = true;
			}
		});
		register.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(registerFlag){
					if(editName.getText()!=null && editPhone.getText()!=null){
						getPresenter().register();
						//Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
						//startActivity(intent);
					}else {
						Toast.makeText(RegisterActivity.this,"信息不完整",Toast.LENGTH_SHORT).show();
					}
				}else {
					Toast.makeText(RegisterActivity.this,"请上传图片",Toast.LENGTH_SHORT).show();
				}
			}
		});
		cameraUtil.setImageReaderListenerCollBack(new CameraUtil.ImageReaderListener() {
			@Override
			public boolean imageReaderListenerCollBack(byte[] bytes) {
				RegisterAsyncTask registerAsyncTask = new RegisterAsyncTask(RegisterActivity.this);
				registerAsyncTask.execute(bytes);
				return false;
			}
		});
	}

	@Override
	public RegisterActivityModel initModel() {
		return new RegisterActivityModel(this);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.e("registerActivity","surfaceCreated");
		cameraUtil.setCamera(handler,mCameraDeviceStateCallback);
		cameraUtil.setUpCameraOutputs();
		cameraUtil.openCamera();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {

	}
	private CameraDevice.StateCallback mCameraDeviceStateCallback = new CameraDevice.StateCallback() {
		@Override
		public void onOpened(@NonNull CameraDevice camera) {
			cameraUtil.startPreview(camera,stateCallback);
		}
		@Override
		public void onDisconnected(@NonNull CameraDevice camera) {
			cameraUtil.closeCamera();
		}
		@Override
		public void onError(@NonNull CameraDevice camera, int error) {
			cameraUtil.closeCamera();
		}
	};
	private CameraCaptureSession.StateCallback stateCallback = new CameraCaptureSession.StateCallback(){
		@Override
		public void onConfigured(@NonNull CameraCaptureSession session) {
			cameraUtil.initPreview(session,captureCallback);
		}
		@Override
		public void onConfigureFailed(@NonNull CameraCaptureSession session) {
			Toast.makeText(RegisterActivity.this, "配置失败！", Toast.LENGTH_SHORT).show();
		}
	};
	private CameraCaptureSession.CaptureCallback captureCallback = new CameraCaptureSession.CaptureCallback() {
		/**
		 * 对摄像头返回的结果进行处理,并获取人脸数据
		 * @param result 摄像头数据
		 */
		private void process(CaptureResult result) {
			//获得Face类
			/*Face face[]=result.get(CaptureResult.STATISTICS_FACES);
			//如果有人脸的话
			if (face!=null && face.length>0){
				if(recognitionFlag){
					recognitionFlag = false;
					cameraUtil.takePicture();
				}
				getNum(face.length);
			}else {
				getNum(0);
			}*/
		}
		@Override
		public void onCaptureStarted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, long timestamp, long frameNumber) {
			super.onCaptureStarted(session, request, timestamp, frameNumber);
			//Log.e("onCaptureStarted","onCaptureStarted");
		}
		@Override
		public void onCaptureProgressed(CameraCaptureSession session, CaptureRequest request, CaptureResult partialResult) {
			//Log.e("onCaptureProgressed","onCaptureProgressed");
			process(partialResult);
		}

		@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
		@Override
		public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
			process(result);
			//Log.e("onCaptureCompleted","onCaptureCompleted");
		}

		@Override
		public void onCaptureFailed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureFailure failure) {
			super.onCaptureFailed(session, request, failure);
			//Log.e("onCaptureProgressed","onCaptureFailed");
		}
	};
	static class RegisterAsyncTask extends AsyncTask<byte[],Boolean,Boolean> {
		WeakReference<RegisterActivity> weakReference;
		private RegisterAsyncTask(RegisterActivity activity ){
			weakReference = new WeakReference<>(activity);
		}

		@Override
		protected Boolean doInBackground(byte[]... bytes) {
			RegisterActivity activity = weakReference.get();
			return activity.getPresenter().faceRegister(bytes[0], String.valueOf(activity.editPhone.getText()),String.valueOf(activity.editName.getText()));
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected void onProgressUpdate(Boolean[] values) {
			super.onProgressUpdate(values);
		}
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			RegisterActivity activity = weakReference.get();
			activity.registerFlag = true;
			Toast.makeText(activity,"照片上传成功",Toast.LENGTH_LONG).show();
		}
	}
	public void getNum(final int num){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				//faceNum.setText(String.valueOf(num));
			}
		});
	}
}
