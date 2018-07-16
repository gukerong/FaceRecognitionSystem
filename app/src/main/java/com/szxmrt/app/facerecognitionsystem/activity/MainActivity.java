package com.szxmrt.app.facerecognitionsystem.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.Face;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.szxmrt.app.facerecognitionsystem.R;
import com.szxmrt.app.facerecognitionsystem.base.BaseActivity;
import com.szxmrt.app.facerecognitionsystem.model.MainActivityModel;
import com.szxmrt.app.facerecognitionsystem.presenter.MainActivityPresenter;
import com.szxmrt.app.facerecognitionsystem.util.CameraUtil;

import java.lang.ref.WeakReference;
import java.util.Arrays;

import butterknife.BindView;

public class MainActivity extends BaseActivity<MainActivityPresenter,MainActivityModel> implements SurfaceHolder.Callback{
	@BindView(R.id.previewView)
	SurfaceView previewView;
	@BindView(R.id.tip)
	ImageView imageView;
	@BindView(R.id.ss)
	ImageView ss;
	@BindView(R.id.faceNum)
	TextView faceNum;
	@BindView(R.id.faceView)
	SurfaceView faceView;
	private HandlerThread handlerThread;
	private Handler handler;
	private CameraUtil cameraUtil;
	private CaptureRequest.Builder builder;
	private SurfaceHolder surfaceHolder;
	private SurfaceHolder faceSurfaceHolder;
	private Handler mainHandler;
	boolean recognitionFlag;
	@Override
	public int getLayoutId() { return R.layout.activity_main; }


	@Override
	public MainActivityPresenter initPresenter() { return new MainActivityPresenter(this); }

	@Override
	public void initVariables() {
		recognitionFlag = true;
		handlerThread = new HandlerThread("camera");
		handlerThread.start();
		handler = new Handler(handlerThread.getLooper());
		mainHandler = new Handler(getMainLooper());
		faceView.setZOrderOnTop(true);
		faceView.setZOrderMediaOverlay(true);
		faceSurfaceHolder = faceView.getHolder();
		faceSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);
		surfaceHolder = previewView.getHolder();
		surfaceHolder.addCallback(this);
		cameraUtil = new CameraUtil(this,surfaceHolder);
	}

	@Override
	public void initListener() {
		cameraUtil.setImageReaderListenerCollBack(new CameraUtil.ImageReaderListener() {
			@Override
			public boolean imageReaderListenerCollBack(byte[] bytes) {
				RecognitionAsyncTask recognitionAsyncTask = new RecognitionAsyncTask(MainActivity.this);
				recognitionAsyncTask.execute(bytes);
				return recognitionFlag;
			}
		});
		imageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				cameraUtil.closeCamera();
				stopBackgroundThread();
				recognitionFlag = true;
				Intent intent1 = new Intent(MainActivity.this, RegisterActivity.class);
				startActivity(intent1);
				finish();
			}
		});
	}

	@Override
	public MainActivityModel initModel() {
		return new MainActivityModel(this);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
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
			Log.e("MainActivity","onOpened");
			cameraUtil.startPreview(camera,stateCallback);
		}
		@Override
		public void onDisconnected(@NonNull CameraDevice camera) {
			Log.e("MainActivity","onOpened");
			cameraUtil.closeCamera();
		}
		@Override
		public void onError(@NonNull CameraDevice camera, int error) {
			Log.e("MainActivity","onOpened");
			cameraUtil.closeCamera();
		}
	};
	private CameraCaptureSession.StateCallback stateCallback = new CameraCaptureSession.StateCallback(){
		@Override
		public void onConfigured(@NonNull CameraCaptureSession session) {
			Log.e("MainActivity","onConfigured");
			cameraUtil.initPreview(session,captureCallback);
		}
		@Override
		public void onConfigureFailed(@NonNull CameraCaptureSession session) {
			Toast.makeText(MainActivity.this, "配置失败！", Toast.LENGTH_SHORT).show();
		}
	};
	private CameraCaptureSession.CaptureCallback captureCallback = new CameraCaptureSession.CaptureCallback() {
		/**
		 * 对摄像头返回的结果进行处理,并获取人脸数据
		 * @param result 摄像头数据
		 */
		private void process(CaptureResult result) {
			//获得Face类
			Face face[]=result.get(CaptureResult.STATISTICS_FACES);
			//如果有人脸的话
			if (face!=null && face.length>0 && recognitionFlag){
				setRecognitionFlag(false);
				cameraUtil.takePicture();
				//获取人脸矩形框
				Rect bounds = face[0].getBounds();
/*				float y = size.getHeight()/2 - bounds.top ;
				Log.e("height" , String.valueOf(size.getWidth()));
				Log.e("top" , String.valueOf(y));
				Log.e("left" ,  String.valueOf(bounds.left));
				Log.e("right" , String.valueOf(bounds.right));*/
/*				bounds.set(bounds.left, (int) y,bounds.right,bounds.bottom);
				if(faceSurfaceHolder != null ){
					//锁定整个SurfaceView
					Canvas mCanvas = faceSurfaceHolder.lockCanvas();
					mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
						//Logger.v("onResult"+ pointF.x + "_" + pointF.y);
						mCanvas.drawRect(bounds,faceUtil.paint);//绘制矩形
					faceSurfaceHolder.unlockCanvasAndPost(mCanvas);;
				}*/
				getNum(face.length,null);
			}else {
				getNum(0,null);
			}
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

		@Override
		public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
			process(result);
			//Log.e("onCaptureCompleted","onCaptureCompleted");
		}

		@Override
		public void onCaptureFailed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureFailure failure) {
			super.onCaptureFailed(session, request, failure);
		}
	};
	public void setRecognitionFlag(boolean b){
		recognitionFlag = b;
	}
	protected void stopBackgroundThread() {
		if(handlerThread!=null){
			handlerThread.quitSafely();
			try {
				handlerThread.join();
				handlerThread = null;
				handler = null;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.e("onResume","onResume");
	}
	public void showToast(final String msg){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(MainActivity.this,msg,Toast.LENGTH_SHORT).show();
			}
		});
	}
	static class RecognitionAsyncTask extends AsyncTask<byte[],Object,Integer> {
		WeakReference<MainActivity> weakReference;
		private RecognitionAsyncTask(MainActivity activity ){
			weakReference = new WeakReference<>(activity);
		}

		@Override
		protected Integer doInBackground(byte[]... bytes) {
			MainActivity mainActivity = weakReference.get();
			return mainActivity.getPresenter().faceIdentify(bytes[0]);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected void onProgressUpdate(Object[] values) {
			super.onProgressUpdate(values);
		}
		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			MainActivity activity = weakReference.get();
			switch (result){
				case 1000:
					activity.showToast("识别成功");
					activity.cameraUtil.closeCamera();
					activity.stopBackgroundThread();
					break;
				case 1001:
					activity.showToast("非会员");
					break;
				case 1002:
					activity.showToast("人数过多");
					break;
				case 1003:
					activity.showToast("识别失败");
					break;
				case 1004:
					activity.showToast("ERROR");
					break;
				case 1005:
					break;
				default:
					break;
			}
		}
	}
	@Override
	protected void onStop() {
		super.onStop();
		cameraUtil.closeCamera();
		stopBackgroundThread();
		Log.e("onPause","onPause");
	}
	public void getNum(final int num, final Bitmap image){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(faceNum!=null){
					faceNum.setText(String.valueOf(num));
				}
				if(ss!=null){
					ss.setImageBitmap(image);
				}

			}
		});
	}
}
