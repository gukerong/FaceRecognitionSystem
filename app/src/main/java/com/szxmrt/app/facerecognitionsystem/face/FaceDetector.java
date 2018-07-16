package com.szxmrt.app.facerecognitionsystem.face;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.support.annotation.IntDef;
import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by Administrator on 2018-06-16
 */

public class FaceDetector {

	public static int ONE = 1;
	public static int TOW = 1;
	public static int THREE = 1;
	public static int FOUR = 1;
	public static int FIVE = 1;
	@IntDef @interface maxFaceNumber{}

	private int maxFaceNumber = 0;
	private byte[] faceByte = null;

	public void setMaxFaceNumber(@maxFaceNumber int maxFaceNumber){
		this.maxFaceNumber = maxFaceNumber;
	}

	public byte[] getFaceByte(){
		return faceByte;
	}
	public Bitmap getBitmap(Camera Camera, byte[] bytes, int orientation){
		try {
			Camera.Size size = Camera.getParameters().getPreviewSize();
			YuvImage yuvImg = new YuvImage(bytes, ImageFormat.NV21, size.width, size.height, null);
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inPreferredConfig = Bitmap.Config.RGB_565;
			ByteArrayOutputStream outPutStream = new ByteArrayOutputStream();
			yuvImg.compressToJpeg(new Rect(300, 0, size.width-300, size.height), 70, outPutStream);
			Bitmap bitmap = BitmapFactory.decodeByteArray(outPutStream.toByteArray(), 0, outPutStream.size(),options);
			faceByte = outPutStream.toByteArray();
			outPutStream.close();
			return bitmap;
		}catch (IOException e){
			e.printStackTrace();
			return null;
		}
	}

	public int findFaces(Bitmap faceBitmap) {
		//  Logger.i("图片尺寸："+source.getWidth()+","+source.getHeight());
/*        if (isZoom) {
            Matrix matrix = new Matrix();
            matrix.postScale(mZoomValue, mZoomValue);
            //matrix.postRotate(180);
            source = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
            mBitmapWidth = source.getWidth();
            mBitmapHeight = source.getHeight();
        }*/
		//Bitmap faceBitmap = getBitmap(camera,bytes,orientation);
		android.media.FaceDetector.Face[] faces = new android.media.FaceDetector.Face[maxFaceNumber];
		int faceDetector = new android.media.FaceDetector(faceBitmap.getWidth(), faceBitmap.getHeight(), maxFaceNumber).findFaces(faceBitmap,faces);
		Log.e("faceSize",faceDetector+"");
		return faceDetector;
	}
}
