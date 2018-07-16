package com.szxmrt.app.facerecognitionsystem.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.Face;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.FaceDetector;
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.WindowManager;
import android.widget.Toast;

import com.szxmrt.app.facerecognitionsystem.activity.MainActivity;
import com.szxmrt.app.facerecognitionsystem.presenter.MainActivityPresenter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;

/**
 * android5.0相机工具类
 * Created by Administrator on 2018-06-06
 */

public class CameraUtil{
	private Activity activity;
	private SurfaceHolder surfaceHolder;
	private CaptureRequest.Builder builder;
	private CaptureRequest captureRequest;
	private Handler handler;
	private CameraManager cameraManager;
	private String mCameraId;
	private ImageReader imageReader;
	private CameraDevice cameraDevice;
	private CameraCaptureSession cameraCaptureSession;
	private int mFaceDetectMode;
	private Size size;
	private FaceUtil faceUtil;
	private SurfaceHolder faceSurfaceHolder;
	private Handler mainHandler;
	private CameraDevice.StateCallback mCameraDeviceStateCallback;
	private boolean recognitionFlag;
	private CameraCaptureSession.CaptureCallback captureCallback;
	private CameraCaptureSession.StateCallback stateCallback;
	private ImageReaderListener listener;
	private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
	static {
		ORIENTATIONS.append(Surface.ROTATION_0, 90);
		ORIENTATIONS.append(Surface.ROTATION_90, 0);
		ORIENTATIONS.append(Surface.ROTATION_180, 270);
		ORIENTATIONS.append(Surface.ROTATION_270, 180);
	}
	public CameraUtil(Activity activity,SurfaceHolder surfaceHolder ){
		recognitionFlag = false;
		this.activity =  activity;
		this.surfaceHolder = surfaceHolder;
		faceUtil = FaceUtil.faceUtilBuilder();
		cameraManager = (CameraManager) activity.getSystemService(Activity.CAMERA_SERVICE);
	}

	public void setCamera(Handler handler ,CameraDevice.StateCallback mCameraDeviceStateCallback) {
		this.handler = handler;
		this.mCameraDeviceStateCallback = mCameraDeviceStateCallback;
		if (cameraManager != null) {
			try {
				String[] cameraIdList = cameraManager.getCameraIdList();
				mCameraId = cameraIdList[0];
				mFaceDetectMode = faceUtil.getFDMode(cameraManager,mCameraId);
			} catch (CameraAccessException | SecurityException e) {
				e.printStackTrace();
			}
		}else {
			Toast.makeText(activity,"未检测到摄像头",Toast.LENGTH_SHORT).show();
		}
	}
	public void openCamera(){
		try {
			cameraManager.openCamera(mCameraId, mCameraDeviceStateCallback, handler);
		} catch (CameraAccessException| SecurityException e) {
			e.printStackTrace();
		}
	}
	public void startPreview(CameraDevice cameraDevice,CameraCaptureSession.StateCallback stateCallback){
		try {
			this.stateCallback = stateCallback;
			this.cameraDevice = cameraDevice;
			//surfaceHolder.setFixedSize(size.getWidth(),size.getHeight());
			Surface surface = surfaceHolder.getSurface();
			builder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
			builder.addTarget(surface);
			cameraDevice.createCaptureSession(Arrays.asList(surface,imageReader.getSurface()),stateCallback,handler);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void initPreview(CameraCaptureSession session,CameraCaptureSession.CaptureCallback captureCallback){
		cameraCaptureSession = session;
		this.captureCallback = captureCallback;
		// 设置自动对焦模式
		//builder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
		// 设置自动曝光模式
		//builder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
		//推荐采用这种最简单的设置请求模式
		builder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
		builder.set(CaptureRequest.STATISTICS_FACE_DETECT_MODE,mFaceDetectMode);
		captureRequest = builder.build();
		try {
			session.setRepeatingRequest(captureRequest,captureCallback,handler);
		} catch (CameraAccessException e) {
			e.printStackTrace();
		}
	}

	private Size getMinPreviewSize(double ratio,Size[] sizes){
		List<Size> sizeList = new ArrayList<>();
		for(Size size : sizes){
			Log.e("size","width : "+size.getWidth()+"  height : "+size.getHeight());
			if(ratio == getRatio(size.getWidth(),size.getHeight())){
				sizeList.add(size);
			}
		}
		if(sizeList.size()>0){
			return Collections.min(sizeList,new CompareSizeByArea());
		}return sizes[0];
	}
	private double getRatio(int width,int height){
		// 创建一个数值格式化对象
		NumberFormat numberFormat = NumberFormat.getInstance();
		// 设置精确到小数点后2位
		numberFormat.setMaximumFractionDigits(2);
		String result = numberFormat.format((float) width / (float) height * 100);
		Log.e("ratio", String.valueOf(width)+" "+String.valueOf(height)+" "+result);
		return Double.valueOf(result);
	}

	public void setUpCameraOutputs() {
		try {
			//获取屏幕分辨率
			DisplayMetrics displayMetrics = new DisplayMetrics();
			activity.getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);
			int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
			Log.e("rotation",rotation+"");
			int w = displayMetrics.widthPixels;
			int h = displayMetrics.heightPixels;
			if(rotation==0 || rotation==2){
				h = displayMetrics.widthPixels;
				w = displayMetrics.heightPixels;
			}
			double ratio = getRatio(w,h);
			// 获取指定摄像头的特性
			CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(mCameraId);
			// 获取摄像头支持的配置属性
			StreamConfigurationMap streamConfigurationMap = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
			Size[] sizes = streamConfigurationMap.getOutputSizes(ImageFormat.JPEG);
			size = getMinPreviewSize(ratio, sizes);
			Log.e("previewSize", "width : " + size.getWidth() + "  height : " + size.getHeight());
			imageReader = setImageReader();
		} catch (CameraAccessException e) {
			e.printStackTrace();
		}
	}
	public void restartPreview(){
		startPreview(cameraDevice,stateCallback);
		initPreview(cameraCaptureSession,captureCallback);
	}
	private ImageReader setImageReader(){
		// 创建一个ImageReader对象，用于获取摄像头的图像数据
		ImageReader imageReader = ImageReader.newInstance(size.getWidth(), size.getHeight(), ImageFormat.JPEG, 1);
		imageReader.setOnImageAvailableListener( new ImageReader.OnImageAvailableListener() {
			// 当照片数据可用时激发该方法
			@Override
			public void onImageAvailable(ImageReader reader) {
				Image image = reader.acquireNextImage();
				ByteBuffer buffer = image.getPlanes()[0].getBuffer();
				byte[] data = new byte[buffer.remaining()];
				buffer.get(data);
				if(listener!=null){
					recognitionFlag = listener.imageReaderListenerCollBack(data);
				}
				image.close();
				buffer.clear();
			}
		},handler);
		return imageReader;
	}
	public void takePicture() {
		if (cameraDevice == null || recognitionFlag) return;
		recognitionFlag = false;
		// 创建拍照需要的CaptureRequest.Builder
		final CaptureRequest.Builder captureRequestBuilder;
		try {
			cameraCaptureSession.stopRepeating();
			captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
			// 将imageReader的surface作为CaptureRequest.Builder的目标
			captureRequestBuilder.addTarget(imageReader.getSurface());
			// 获取手机方向
			int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
			// 根据设备方向计算设置照片的方向
			captureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));
			cameraCaptureSession.capture(captureRequestBuilder.build(), new CameraCaptureSession.CaptureCallback() {
				// 拍照完成时激发该方法
				@Override
				public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
					Log.e("onCaptureCompleted","onCaptureCompleted");
				}
			}, handler);
		} catch (CameraAccessException e) {
			e.printStackTrace();
		}
	}
	public void closeCamera() {
		if (null != cameraDevice) {

			cameraDevice.close();
			cameraDevice = null;
		}
		if (null != imageReader) {
			imageReader.close();
			imageReader = null;
		}
	}

	public void setImageReaderListenerCollBack(ImageReaderListener listener){
		this.listener = listener;
	}
	public interface ImageReaderListener{
		boolean imageReaderListenerCollBack(byte[] bytes);
	}





	private Size chooseOptimalSize(Size[] sizes, int width, int height, Size size) {
		// 收集摄像头支持的大过预览Surface的分辨率
		List<Size> sizeList = new ArrayList<>();
		int w = size.getWidth();
		int h = size.getHeight();
		for (Size option : sizes) {
			if (option.getHeight() == option.getWidth() * h / w && option.getWidth() >= width && option.getHeight() >= height) {
				sizeList.add(option);
			}
		}
		// 如果找到多个预览尺寸，获取其中面积最小的
		if (sizeList.size() > 0) {
			return Collections.min(sizeList, new CompareSizeByArea());
		} else {
			System.out.println("找不到合适的预览尺寸！！！");
			return sizes[0];
		}
	}
}
