package com.szxmrt.app.facerecognitionsystem.camera;

import android.hardware.Camera;
import android.util.SparseIntArray;
import android.view.Surface;

import com.szxmrt.app.facerecognitionsystem.util.LogUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class GetCameraParameters {

	private int detectRotation = 0;
	private int displayOrientation = 0;
	private static String TAG = GetCameraParameters.class.getSimpleName();
	private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
	static {
		ORIENTATIONS.append(Surface.ROTATION_0, 0);
		ORIENTATIONS.append(Surface.ROTATION_90, 90);
		ORIENTATIONS.append(Surface.ROTATION_180, 180);
		ORIENTATIONS.append(Surface.ROTATION_270, 270);}
	public int getDisplayOrientation( int displayOrientation,int cameraId){
		int rotation = ORIENTATIONS.get(displayOrientation);
		this.detectRotation = getCameraDisplayOrientation(rotation,cameraId);
		this.displayOrientation = displayOrientation;
		return detectRotation;

	}
	public int getDetectOrientation(@ICameraControl.CameraFacing int cameraFacing){
		if (cameraFacing == ICameraControl.CAMERA_FACING_FRONT) {
			if (displayOrientation == ICameraControl.ORIENTATION_PORTRAIT) {
				if (detectRotation == 90 || detectRotation == 270) {
					detectRotation = (detectRotation + 180) % 360;
				}
			}
		}
		return detectRotation;
	}
	/**
	 * 根据提供的textureView尺寸来获取适配的相机预览尺寸
	 * @param width 宽
	 * @param height 高
	 * @param sizes 相机可用的尺寸
	 * @return 相机预览尺寸
	 */
	public Camera.Size getOptimalSize(int width, int height, List<Camera.Size> sizes) {
		Camera.Size pictureSize = sizes.get(0);
		List<Camera.Size> candidates = new ArrayList<>();
		for (Camera.Size size : sizes) {
			if (size.width >= width && size.height >= height && size.width * height == size.height * width) {
				// 比例相同
				candidates.add(size);
			} else if (size.height >= width && size.width >= height && size.width * width == size.height * height) {
				// 反比例
				candidates.add(size);
			}
		}
		if (!candidates.isEmpty()) {
			return Collections.min(candidates, sizeComparator);
		}
		for (Camera.Size size : sizes) {
			if (size.width >= width && size.height >= height) {
				return size;
			}
		}
		return pictureSize;
	}
	private Comparator<Camera.Size> sizeComparator = new Comparator<Camera.Size>() {
		@Override
		public int compare(Camera.Size lhs, Camera.Size rhs) {
			return Long.signum((long) lhs.width * lhs.height - (long) rhs.width * rhs.height);
		}
	};
	private int getCameraDisplayOrientation(int degrees, int cameraId ) {
		Camera.CameraInfo info = new Camera.CameraInfo();
		Camera.getCameraInfo(cameraId, info);
		int rotation = 0;
		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			rotation = (info.orientation + degrees) % 360;
			rotation = (360 - rotation) % 360; // compensate the mirror
		} else {
			rotation = (info.orientation - degrees + 360) % 360;
		}
		LogUtil.e(TAG,"displayOrientation : "+rotation);
		return rotation;
	}
}
