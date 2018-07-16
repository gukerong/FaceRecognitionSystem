package com.szxmrt.app.facerecognitionsystem.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.media.FaceDetector;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2018-06-09
 */

public class FaceUtil {
	public Paint paint;
	private int rectFlagOffset;
	private int mZoomValue;
	private static FaceUtil faceUtil;
	private FaceUtil(){
		if (paint == null) {
			paint = new Paint();
			paint.setAntiAlias(true);
			paint.setColor(Color.RED);
			paint.setStrokeWidth(5);
			paint.setStyle(Paint.Style.STROKE);
		}
		rectFlagOffset = 1;
		mZoomValue = 1;
	}
	public static FaceUtil faceUtilBuilder(){
		if(faceUtil == null){
			synchronized (FaceUtil.class){
				if(faceUtil == null){
					faceUtil = new FaceUtil();
				}
			}

		}
		return faceUtil;
	}
	public Integer getFDMode(CameraManager cameraManager,String mCameraId){
		CameraCharacteristics characteristics = null;
		try {
			characteristics = cameraManager.getCameraCharacteristics(mCameraId);
			//获取人脸检测参数
			int[] faceDetect =characteristics.get(CameraCharacteristics.STATISTICS_INFO_AVAILABLE_FACE_DETECT_MODES);
			Integer maxFD=characteristics.get(CameraCharacteristics.STATISTICS_INFO_MAX_FACE_COUNT);
			Log.e("maxFD",maxFD+"");
			if (faceDetect!=null&& faceDetect.length>0) {
				Log.e("faceDetect",faceDetect.length+"");
				List<Integer> fdList = new ArrayList<>();
				for (int FaceD : faceDetect) { fdList.add(FaceD); }
				if (maxFD!=null && maxFD > 0) { return Collections.max(fdList); }
			}
		} catch (CameraAccessException e) {
			e.printStackTrace();
			return null;
		}
		Log.e("null","null");
		return null;
	}
}
