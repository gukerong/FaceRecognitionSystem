package com.szxmrt.app.facerecognitionsystem.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.szxmrt.app.facerecognitionsystem.R;
import com.szxmrt.app.facerecognitionsystem.base.BaseActivity;
import com.szxmrt.app.facerecognitionsystem.camera.Camera1TextureControl;
import com.szxmrt.app.facerecognitionsystem.camera.ICameraControl;
import com.szxmrt.app.facerecognitionsystem.custom.CameraTextureView;
import com.szxmrt.app.facerecognitionsystem.face.FaceDetector;
import com.szxmrt.app.facerecognitionsystem.model.Camera1RecognitionActivityModel;
import com.szxmrt.app.facerecognitionsystem.presenter.Camera1RecognitionActivityPresenter;
import com.szxmrt.app.facerecognitionsystem.util.LogUtil;
import com.szxmrt.app.facerecognitionsystem.widget.MyTextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import butterknife.BindView;

public class Camera1RecognitionActivity extends BaseActivity<Camera1RecognitionActivityPresenter,Camera1RecognitionActivityModel> implements Camera.PreviewCallback,Camera.ErrorCallback{
	@BindView(R.id.PreviewTexture)
	CameraTextureView textureView;
	@BindView(R.id.faceView)
	ImageView previewView;
	@BindView(R.id.camera1_faceNum)
	TextView faceNum;
	@BindView(R.id.register)
	MyTextView register;
	private Camera1TextureControl camera1Control;
	private FaceDetector faceDetector;
	private ScheduledExecutorService scheduledThreadPoolExecutor;
	private int detectRotation = 0;
	private volatile boolean detectionFlag = false;   //是否进行检测识别
	private volatile boolean recognitionFlag = false;   //是否进行人脸识别
	private long recognitionTimer = 0L;
	private FaceDetectThread faceDetectThread;
	private static String TAG = Camera1RecognitionActivity.class.getSimpleName();
	@Override
	public int getLayoutId() {
		return R.layout.activity_camera1_recognition;
	}

	@Override
	public Camera1RecognitionActivityPresenter initPresenter() {
		return new Camera1RecognitionActivityPresenter(this);
	}
	@Override
	public void initVariables() {
		faceDetector = new FaceDetector();
		faceDetector.setMaxFaceNumber(FaceDetector.FIVE);
		camera1Control = new Camera1TextureControl(this);
		camera1Control.setCameraFacing(ICameraControl.CAMERA_FACING_FRONT);
		detectionFlag = true;
		progressDialog = new ProgressDialog(this);
	}

	@Override
	public void initListener() {
		textureView.setSurfaceTextureListener(new CameraTextureView.SurfaceTextureListener() {
			@Override
			public void callback(SurfaceTexture surfaceTexture) {
				camera1Control.setDisplayOrientation(ICameraControl.ORIENTATION_HORIZONTAL);
				camera1Control.setPreviewSize(1920,1080);
				camera1Control.setSurfaceTexture(textureView.getSurfaceTexture());
				camera1Control.openPreview();
				detectionFlag = true;
			}
		});
		register.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				detectionFlag = false;
				scheduledThreadPoolExecutor.shutdown();
				camera1Control.stopCamera();
				if(scheduledThreadPoolExecutor.isShutdown()){
					scheduledThreadPoolExecutor = null;
					Intent intent = new Intent(Camera1RecognitionActivity.this,Camera1RegisterActivity.class);
					startActivity(intent);
				}
			}
		});
	}

	@Override
	public Camera1RecognitionActivityModel initModel() {
		return new Camera1RecognitionActivityModel(this);
	}
	@Override
	protected void onStart() {
		super.onStart();
		LogUtil.e(TAG,"onStart");
	}

	@Override
	protected void onResume() {
		super.onResume();
		LogUtil.e(TAG,"onResume");
		scheduledThreadPoolExecutor = Executors.newScheduledThreadPool(4);
		camera1Control.openCamera();
		getPresenter().openUsbSerialPort(this);
	}


	@Override
	protected void onRestart() {
		super.onRestart();
		LogUtil.e(TAG,"onRestart");
	}

	@Override
	protected void onPause() {
		super.onPause();
		LogUtil.e(TAG,"onPause");
		if(faceDetectThread!=null){
			faceDetectThread.interrupt();
			faceDetectThread = null;
		}
		getPresenter().closeUsbSerialPort();
	}

	@Override
	protected void onStop() {
		super.onStop();
		LogUtil.e(TAG,"onStop");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		LogUtil.e(TAG,"onDestroy");
	}

	private long time = 0L;
	//上次记录的索引
	int darkIndex = 0;
	//一个历史记录的数组，255是代表亮度最大值
	long[] darkList = new long[]{255, 255, 255, 255};
	//扫描间隔
	int waitScanTime = 300;

	//亮度低的阀值
	int darkValue = 60;
	@BindView(R.id.light)
	TextView light;
	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		camera.addCallbackBuffer(data);
		if(detectionFlag && (System.currentTimeMillis()-recognitionTimer)>1500){
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			recognitionTimer = System.currentTimeMillis();
			faceDetectThread = null;
			faceDetectThread = new FaceDetectThread(camera,data);
			scheduledThreadPoolExecutor.execute(faceDetectThread);
		}
/*		if(System.currentTimeMillis()-time>300){
			time = System.currentTimeMillis();
			int width = camera.getParameters().getPreviewSize().width;
			int height = camera.getParameters().getPreviewSize().height;
			//像素点的总亮度
			long pixelLightCount = 0L;
			//像素点的总数
			long pixeCount = width * height;
			//采集步长，因为没有必要每个像素点都采集，可以跨一段采集一个，减少计算负担，必须大于等于1。
			int step = 10;
			//data.length - allCount * 1.5f的目的是判断图像格式是不是YUV420格式，只有是这种格式才相等
			//因为int整形与float浮点直接比较会出问题，所以这么比
			if (Math.abs(data.length - pixeCount * 1.5f) < 0.00001f) {
				for (int i = 0; i < pixeCount; i += step) {
					//如果直接加是不行的，因为data[i]记录的是色值并不是数值，byte的范围是+127到—128，
					// 而亮度FFFFFF是11111111是-127，所以这里需要先转为无符号unsigned long参考Byte.toUnsignedLong()
					pixelLightCount += ((long) data[i]) & 0xffL;
				}
				//平均亮度
				final long cameraLight = pixelLightCount / (pixeCount / step);
				//更新历史记录
				int lightSize = darkList.length;
				darkList[darkIndex = darkIndex % lightSize] = cameraLight;
				darkIndex++;
				boolean isDarkEnv = true;
				//判断在时间范围waitScanTime * lightSize内是不是亮度过暗
				for (int i = 0; i < lightSize; i++) {
					if (darkList[i] > darkValue) {
						isDarkEnv = false;
					}
				}
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						light.setText("摄像头环境亮度为 ："+cameraLight);
					}
				});
			}
		}*/
	}

	private class FaceDetectThread extends Thread{
		private Camera camera;
		private byte[] bytes;
		private FaceDetectThread(Camera camera,byte[] bytes){
			this.bytes = bytes;
			this.camera = camera;
			detectionFlag = false;
		}
		@Override
		public void run() {
			super.run();
			final Bitmap bitmap = faceDetector.getBitmap(camera,bytes,detectRotation);
			final int num = faceDetector.findFaces(bitmap);
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if(num>0){
						String s = getString(R.string.face)+num;
						faceNum.setText(s);
					}else {
						faceNum.setText(R.string.faceNone);
					}
				}
			});
			if(num>0){
				byte[] bytes = faceDetector.getFaceByte();
				RecognitionAsyncTask recognitionAsyncTask = new RecognitionAsyncTask(Camera1RecognitionActivity.this);
				recognitionAsyncTask.execute(bytes);
			}else {
				detectionFlag = true;
			}
		}
	}
	private volatile ProgressDialog progressDialog;
	static class RecognitionAsyncTask extends AsyncTask<byte[],String,Integer> {
		private WeakReference<Camera1RecognitionActivity> weakReference;
		private boolean doorFlag = true;
		private RecognitionAsyncTask(Camera1RecognitionActivity activity ){
			weakReference = new WeakReference<>(activity);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		@Override
		protected Integer doInBackground(byte[]... bytes) {
			final Camera1RecognitionActivity activity = weakReference.get();
			Integer result = activity.getPresenter().faceIdentify(bytes[0]);
			publishProgress(String.valueOf(result));
			if (result == 1000 ){
				try {
					publishProgress("1006");
					boolean isCLose = false;
					activity.getPresenter().openDoor();
					Thread.sleep(3000);
					while (true){
						final String readDoor = activity.getPresenter().readDoor(6);
						if (readDoor.length()<=0){
							break;
						}
						if ("010101005188".equals(readDoor)){
							isCLose = true;
						}
						if ("010101019048".equals(readDoor)){
							break;
						}
						Thread.sleep(500);
					}
					Thread.sleep(100);
					activity.getPresenter().closeDoor();
					if (isCLose){
						Thread.sleep(100);
						activity.getPresenter().openDoor2();
						Thread.sleep(10000);
					}
					publishProgress("1007");
				} catch (InterruptedException e){
					e.printStackTrace();
				}
			}
			return result;
		}
		@Override
		protected void onProgressUpdate(String[] values) {
			super.onProgressUpdate(values);
			Camera1RecognitionActivity activity = weakReference.get();

			switch (values[0]){
				case "1000":
					activity.showToast("识别成功",activity);
					break;
				case "1001":
					activity.showToast("非会员",activity);
					break;
				case "1002":
					activity.showToast("人数过多",activity);
					break;
				case "1003":
					activity.showToast("识别失败",activity);
					break;
				case "1004":
					//activity.showToast("ERROR",activity);
					break;
				case "1005":
					activity.showToast("用户组为空，请注册",activity);
					break;
				case "1006":
					activity.progressDialog.setTitle("开门中");
					activity.progressDialog.setMessage("请稍等。。。。。。");
					activity.progressDialog.setCancelable(false);
					activity.progressDialog.show();
					break;
				case "1007":
					activity.progressDialog.dismiss();
					break;
				default:
					activity.showToast(values[0],activity);
					break;
			}
			//activity.showToast(values[0],activity);
		}
		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			Camera1RecognitionActivity activity = weakReference.get();
			switch (result){
				case 1000:
					//activity.detectionFlag = true;
					//activity.closeFlag = true;
					//activity.showToast("识别成功",activity);
					break;
				case 1001:
					//activity.showToast("非会员",activity);
					break;
				case 1002:
					//activity.showToast("人数过多",activity);
					break;
				case 1003:
					//activity.showToast("识别失败",activity);
					break;
				case 1004:
					//activity.showToast("ERROR",activity);
					break;
				case 1005:
					//activity.showToast("用户组为空，请注册",activity);
					//activity.detectionFlag = false;
					break;
				default:
					break;
			}
			activity.detectionFlag = true;
		}
	}
	@Override
	public void onError(int error, Camera camera) {
		LogUtil.e(TAG,"error : "+error);
		File file = new File(getExternalFilesDir("error").getPath()+"/error.text");
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			String e = "error"+error;
			fileOutputStream.write(e.getBytes());
			fileOutputStream.close();
		} catch (IOException e){
			e.printStackTrace();
		}

	}
}
