package com.szxmrt.app.facerecognitionsystem.util;

import android.graphics.Point;
import android.hardware.Camera;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2018-06-20
 */

public class CameraPreviewUtil {
	private static final String TAG = CameraPreviewUtil.class.getSimpleName();
	private static final int MIN_PREVIEW_PIXELS = 307200;
	private static final int MAX_PREVIEW_PIXELS = 921600;

	public CameraPreviewUtil() {
	}

	public static Point getBestPreview(Camera.Parameters parameters, Point screenResolution) {
		List<Camera.Size> rawSupportedSizes = parameters.getSupportedPreviewSizes();
		if(rawSupportedSizes == null) {
			Camera.Size defaultSize = parameters.getPreviewSize();
			return new Point(defaultSize.width, defaultSize.height);
		} else {
			List<Camera.Size> supportedPictureSizes = new ArrayList<>(rawSupportedSizes);
			Collections.sort(supportedPictureSizes, new Comparator<Camera.Size>() {
				public int compare(Camera.Size a, Camera.Size b) {
					int aPixels = a.height * a.width;
					int bPixels = b.height * b.width;
					return bPixels < aPixels?-1:(bPixels > aPixels?1:0);
				}
			});
			double screenAspectRatio = screenResolution.x > screenResolution.y?(double)screenResolution.x / (double)screenResolution.y:(double)screenResolution.y / (double)screenResolution.x;
			Camera.Size selectedSize = null;
			double selectedMinus = -1.0D;
			double selectedPreviewSize = 0.0D;
			Iterator it = supportedPictureSizes.iterator();

			while(true) {
				Camera.Size supportedPreviewSize;
				while(it.hasNext()) {
					supportedPreviewSize = (Camera.Size)it.next();
					int realWidth = supportedPreviewSize.width;
					int realHeight = supportedPreviewSize.height;
					if(realWidth * realHeight < 307200) {
						it.remove();
					} else if(realWidth * realHeight > 921600) {
						it.remove();
					} else {
						double aRatio = supportedPreviewSize.width > supportedPreviewSize.height?(double)supportedPreviewSize.width / (double)supportedPreviewSize.height:(double)supportedPreviewSize.height / (double)supportedPreviewSize.width;
						double minus = Math.abs(aRatio - screenAspectRatio);
						boolean selectedFlag = false;
						if(selectedMinus == -1.0D && minus <= 0.25D || selectedMinus >= minus && minus <= 0.25D) {
							selectedFlag = true;
						}

						if(selectedFlag) {
							selectedMinus = minus;
							selectedSize = supportedPreviewSize;
							selectedPreviewSize = (double)(realWidth * realHeight);
						}
					}
				}

				if(selectedSize != null) {
					return new Point(selectedSize.width, selectedSize.height);
				}

				supportedPreviewSize = parameters.getPreviewSize();
				return new Point(supportedPreviewSize.width, supportedPreviewSize.height);
			}
		}
	}
}
