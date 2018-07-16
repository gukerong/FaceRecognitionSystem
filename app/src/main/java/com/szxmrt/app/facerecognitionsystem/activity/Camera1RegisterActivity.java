package com.szxmrt.app.facerecognitionsystem.activity;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.szxmrt.app.facerecognitionsystem.MakeXml;
import com.szxmrt.app.facerecognitionsystem.R;
import com.szxmrt.app.facerecognitionsystem.adapter.KeyBoardAdapter;
import com.szxmrt.app.facerecognitionsystem.base.BaseActivity;
import com.szxmrt.app.facerecognitionsystem.camera.Camera1TextureControl;
import com.szxmrt.app.facerecognitionsystem.camera.ICameraControl;
import com.szxmrt.app.facerecognitionsystem.custom.CameraTextureView;
import com.szxmrt.app.facerecognitionsystem.face.FaceDetector;
import com.szxmrt.app.facerecognitionsystem.model.Camera1RegisterActivityModel;
import com.szxmrt.app.facerecognitionsystem.presenter.Camera1RegisterActivityPresenter;
import com.szxmrt.app.facerecognitionsystem.util.LogUtil;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import butterknife.BindView;

public class Camera1RegisterActivity extends BaseActivity<Camera1RegisterActivityPresenter,Camera1RegisterActivityModel>implements Camera.PreviewCallback,Camera.ErrorCallback {
	@BindView(R.id.registerView)
	CameraTextureView surfaceView;
	@BindView(R.id.register_Name)
	EditText editName;
	@BindView(R.id.register_Phone)
	EditText editPhone;
	@BindView(R.id.register_Register)
	Button register;
	@BindView(R.id.register_Photo)
	Button photo;
	@BindView(R.id.register_faceTip)
	TextView faceNum;
	@BindView(R.id.register_setting)
	ImageView setting;
	@BindView(R.id.register_back)
	ImageView back;
	boolean registerFlag;
	private Camera1TextureControl camera1Control;
	private FaceDetector faceDetector;
	private int detectRotation = 0;
	private boolean recognitionFlag = false;
	private boolean imageFlag = false;
	private boolean settingFlag = false;
	private long recognitionTimer = 0L;
	private byte[] faceByte = null;
	private ScheduledExecutorService scheduledThreadPoolExecutor  = Executors.newScheduledThreadPool(4);;
	private Camera camera;
	private static String TAG = Camera1RegisterActivity.class.getSimpleName();
	@Override
	public int getLayoutId() {return R.layout.activity_register;}

	@Override
	public Camera1RegisterActivityPresenter initPresenter() {
		return new Camera1RegisterActivityPresenter(this);
	}

	@Override
	public void initVariables() {
		recognitionFlag = false;
		registerFlag = false;
		faceDetector = new FaceDetector();
		faceDetector.setMaxFaceNumber(FaceDetector.FIVE);
		camera1Control = new Camera1TextureControl(this);
		camera1Control.setCameraFacing(ICameraControl.CAMERA_FACING_FRONT);

	}

	@Override
	public void initListener() {
		photo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				imageFlag = true;
			}
		});
		register.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!recognitionFlag){
					showToast("请上传图片");
					return;
				}
				if(editName.getText().length()==0 || editPhone.getText().length()==0){
					showToast("请填写姓名和电话号码");
					return;
				}
				if(editName.getText()!=null && editPhone.getText()!=null && faceByte!=null && recognitionFlag){
					RegisterAsyncTask registerAsyncTask = new RegisterAsyncTask(Camera1RegisterActivity.this);
					registerAsyncTask.execute(faceByte);
				}
			}
		});
		surfaceView.setSurfaceTextureListener(new CameraTextureView.SurfaceTextureListener() {
			@Override
			public void callback(SurfaceTexture surfaceTexture) {
				camera1Control.setDisplayOrientation(ICameraControl.ORIENTATION_HORIZONTAL);
				camera1Control.setPreviewSize(1920,1080);
				camera1Control.setSurfaceTexture(surfaceView.getSurfaceTexture());
				camera1Control.openPreview();
			}
		});
		setting.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				/*camera1Control.stopCamera();
				Intent intent = new Intent(Camera1RegisterActivity.this,SettingActivity.class);
				startActivity(intent);*/
				showPopupWindow();
			}
		});
		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				camera1Control.stopCamera();
				Intent intent = new Intent(Camera1RegisterActivity.this,Camera1RecognitionActivity.class);
				startActivity(intent);
				finish();
			}
		});
	}

	@Override
	public Camera1RegisterActivityModel initModel() {
		return new Camera1RegisterActivityModel(this);
	}


	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		camera.addCallbackBuffer(data);
		if(imageFlag && (System.currentTimeMillis()-recognitionTimer)>2000){
			LogUtil.e(TAG,"start take photos");
			recognitionTimer = System.currentTimeMillis();
			TakePhotosAsyncTask takePhotosAsyncTask = new TakePhotosAsyncTask(this);
			takePhotosAsyncTask.execute(data);
		}
		if (settingFlag){
			settingFlag = false;
			TakePhotosAsyncTask takePhotosAsyncTask = new TakePhotosAsyncTask(this);
			takePhotosAsyncTask.execute(data);
		}
	}

	@Override
	public void onError(int error, Camera camera) {

	}

	static class TakePhotosAsyncTask extends AsyncTask<byte[],Integer,Boolean>{
		WeakReference<Camera1RegisterActivity> weakReference;
		private TakePhotosAsyncTask(Camera1RegisterActivity activity ){
			weakReference = new WeakReference<>(activity);
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(byte[]... bytes) {
			Camera1RegisterActivity activity = weakReference.get();
			if(activity.camera!=null){
				Bitmap bitmap = activity.faceDetector.getBitmap(activity.camera,bytes[0],activity.detectRotation);
				int num = activity.faceDetector.findFaces(bitmap);
				if(num<=0){
					publishProgress(0);    //未检测到人脸
					activity.imageFlag = false;
					return false;
				}else if(num>1){
					publishProgress(2);     //检测到多个人脸
					activity.imageFlag = false;
					return false;
				}else {
					publishProgress(1);     //检测到一个人脸，拍照成功
					activity.imageFlag = false;
					return true;
				}
			}else {
				publishProgress(3);         //相机不存在，拍照失败
				activity.imageFlag = false;
				return false;
			}
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			Camera1RegisterActivity activity = weakReference.get();
			switch (values[0]){
				case 0:
					activity.faceNum.setText(R.string.faceNone);
					break;
				case 1:
					activity.faceNum.setText(R.string.photoOK);
					Toast.makeText(activity,R.string.photoOK,Toast.LENGTH_SHORT).show();
					break;
				case 2:
					activity.faceNum.setText(R.string.photoMore);
					Toast.makeText(activity,R.string.photoMore,Toast.LENGTH_SHORT).show();
					break;
				case 3:
					activity.faceNum.setText(R.string.cameraError);
					Toast.makeText(activity,R.string.cameraError,Toast.LENGTH_SHORT).show();
					break;
			}
		}
		@Override
		protected void onPostExecute(Boolean aBoolean) {
			super.onPostExecute(aBoolean);
			Camera1RegisterActivity activity = weakReference.get();
			activity.recognitionFlag = aBoolean;
			activity.faceByte = activity.faceDetector.getFaceByte();
		}
	}
	static class RegisterAsyncTask extends AsyncTask<byte[],Boolean,Boolean> {
		WeakReference<Camera1RegisterActivity> weakReference;
		private RegisterAsyncTask(Camera1RegisterActivity activity ){
			weakReference = new WeakReference<>(activity);
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(byte[]... bytes) {
			Camera1RegisterActivity activity = weakReference.get();
			int num = activity.getPresenter().faceIdentify(bytes[0]);
			LogUtil.e("num",num+"");
			if(num == 1000){
				publishProgress();
				return false;
			}else {
				return activity.getPresenter().faceRegister(bytes[0], String.valueOf(activity.editPhone.getText()),String.valueOf(activity.editName.getText()));
			}

		}
		@Override
		protected void onProgressUpdate(Boolean[] values) {
			super.onProgressUpdate(values);
			Camera1RegisterActivity activity = weakReference.get();
			activity.showToast("用户已注册，请勿多次注册");
		}
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			Camera1RegisterActivity activity = weakReference.get();
			if(result){
				try {
					activity.showToast("注册成功，正在跳转登录界面");
					Thread.sleep(2000);
					activity.camera1Control.stopCamera();
					Intent intent = new Intent(activity,Camera1RecognitionActivity.class);
					activity.startActivity(intent);
					activity.finish();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}else {
				activity.showToast("注册失败，请重新注册");
			}
		}
	}
	public void getNum(final String num){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				faceNum.setText(num);
			}
		});
	}
	public void showToast(final String msg){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast toast = Toast.makeText(Camera1RegisterActivity.this,msg,Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER,0,0);
				toast.show();
			}
		});
	}
	private class FaceDetectThread extends Thread{
		private Camera camera;
		private byte[] bytes;
		private FaceDetectThread(Camera camera,byte[] bytes){
			this.bytes = bytes;
			this.camera = camera;
		}
		@Override
		public void run() {
			super.run();
			final Bitmap bitmap = faceDetector.getBitmap(camera,bytes,detectRotation);
			final int num = faceDetector.findFaces(bitmap);
			if(num>0){
				byte[] bytes = faceDetector.getFaceByte();
				RecognitionAsyncTask recognitionAsyncTask = new RecognitionAsyncTask(Camera1RegisterActivity.this);
				recognitionAsyncTask.execute(bytes);
			}else {
				showToast("识别失败，请重试");
			}
		}
	}
	static class RecognitionAsyncTask extends AsyncTask<byte[],String,Integer> {
		private WeakReference<Camera1RegisterActivity> weakReference;
		private boolean doorFlag = true;
		private RecognitionAsyncTask(Camera1RegisterActivity activity ){
			weakReference = new WeakReference<>(activity);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		@Override
		protected Integer doInBackground(byte[]... bytes) {
			final Camera1RegisterActivity activity = weakReference.get();
			Integer result = activity.getPresenter().faceIdentify(bytes[0]);
			publishProgress(String.valueOf(result));
			return result;
		}
		@Override
		protected void onProgressUpdate(String[] values) {
			super.onProgressUpdate(values);
			Camera1RegisterActivity activity = weakReference.get();

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
					break;
				case "1007":
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
			Camera1RegisterActivity activity = weakReference.get();
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
		}
	}
	private PopupWindow keyboardPopup;
	private KeyBoardAdapter keyBoardAdapter;
	private List<String> keyBoardNum = new ArrayList<>();
	private void showPopupWindow(){
		for(int i=0;i<10;i++){
			keyBoardNum.add(String.valueOf(i));
		}
		keyBoardNum.add(".");
		keyBoardNum.add("-");
		keyBoardNum.add("删除");
		keyBoardNum.add("确定");
		keyBoardAdapter = new KeyBoardAdapter(keyBoardNum);
		GridLayoutManager gridLayoutManager = new GridLayoutManager(this,4);
		View view = LayoutInflater.from(this).inflate(R.layout.popup_keyboard,null);
		keyboardPopup = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT,false);
		RecyclerView recyclerView = view.findViewById(R.id.recycler_keyboard);
		recyclerView.setAdapter(keyBoardAdapter);
		recyclerView.setLayoutManager(gridLayoutManager);
		View rootView = LayoutInflater.from(this).inflate(R.layout.activity_register,null);
		keyboardPopup.showAsDropDown(rootView,Gravity.CENTER,0,0);
	}
	@Override
	protected void onRestart() {
		super.onRestart();
		LogUtil.i(TAG,"onRestart");
	}

	@Override
	protected void onResume() {
		super.onResume();
		LogUtil.i(TAG,"onResume");
		camera1Control.openCamera();
		camera = camera1Control.getCamera();
	}

	@Override
	protected void onPause() {
		super.onPause();
		LogUtil.i(TAG,"onPause");
	}

	@Override
	protected void onStop() {
		super.onStop();
		LogUtil.i(TAG,"onStop");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		LogUtil.i(TAG,"onDestroy");
	}
}
