package com.szxmrt.app.facerecognitionsystem.baiduface;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2018-06-20
 */

public class FaceConfig implements Serializable{
	private static final String TAG = FaceConfig.class.getSimpleName();
	private float brightnessValue = 40.0F;      //亮度
	private float blurnessValue = 0.5F;         //模糊
	private float occlusionValue = 0.5F;        //遮挡
	private int headPitchValue = 10;            //头部倾斜
	private int headYawValue = 10;              //头部偏斜
	private int headRollValue = 10;             //头部转动
	private int cropFaceValue = 400;            //修剪脸部
	private int minFaceSize = 200;              //最小脸部尺寸
	private float notFaceValue = 0.6F;          //脸部阈值
	private int maxCropImageNum = 1;            //最大图片数量
	private boolean isCheckFaceQuality = true;  //是否检测脸部质量
	private boolean isSound = true;              //是否语音提示
	private boolean isVerifyLive = true;        //是否检测活体
	private int faceDecodeNumberOfThreads = 0;  //
	private boolean isLivenessRandom = false;   //
	private int livenessRandomCount = 3;        //
	private List<LivenessTypeEnum> livenessTypeList;    //

	public FaceConfig() {
		this.livenessTypeList = FaceEnvironment.livenessTypeDefaultList;
	}

	public float getBrightnessValue() {
		return this.brightnessValue;
	}

	public void setBrightnessValue(float brightnessValue) {
		this.brightnessValue = brightnessValue;
	}

	public float getBlurnessValue() {
		return this.blurnessValue;
	}

	public void setBlurnessValue(float blurnessValue) {
		this.blurnessValue = blurnessValue;
	}

	public float getOcclusionValue() {
		return this.occlusionValue;
	}

	public void setOcclusionValue(float occlusionValue) {
		this.occlusionValue = occlusionValue;
	}

	public int getHeadPitchValue() {
		return this.headPitchValue;
	}

	public void setHeadPitchValue(int headPitchValue) {
		this.headPitchValue = headPitchValue;
	}

	public int getHeadYawValue() {
		return this.headYawValue;
	}

	public void setHeadYawValue(int headYawValue) {
		this.headYawValue = headYawValue;
	}

	public int getHeadRollValue() {
		return this.headRollValue;
	}

	public void setHeadRollValue(int headRollValue) {
		this.headRollValue = headRollValue;
	}

	public int getCropFaceValue() {
		return this.cropFaceValue;
	}

	public void setCropFaceValue(int cropFaceValue) {
		this.cropFaceValue = cropFaceValue;
	}

	public int getMinFaceSize() {
		return this.minFaceSize;
	}

	public void setMinFaceSize(int minFaceSize) {
		this.minFaceSize = minFaceSize;
	}

	public float getNotFaceValue() {
		return this.notFaceValue;
	}

	public void setNotFaceValue(float notFaceValue) {
		this.notFaceValue = notFaceValue;
	}

	public int getMaxCropImageNum() {
		return this.maxCropImageNum;
	}

	public void setMaxCropImageNum(int maxCropImageNum) {
		this.maxCropImageNum = maxCropImageNum;
	}

	public boolean isCheckFaceQuality() {
		return this.isCheckFaceQuality;
	}

	public void setCheckFaceQuality(boolean checkFaceQuality) {
		this.isCheckFaceQuality = checkFaceQuality;
	}

	public boolean isSound() {
		return this.isSound;
	}

	public void setSound(boolean sound) {
		this.isSound = sound;
	}

	public boolean isLivenessRandom() {
		return this.isLivenessRandom;
	}

	public void setLivenessRandom(boolean livenessRandom) {
		this.isLivenessRandom = livenessRandom;
	}

	public int getLivenessRandomCount() {
		return this.livenessRandomCount;
	}

	public void setLivenessRandomCount(int livenessRandomCount) {
		int count = FaceEnvironment.livenessTypeDefaultList.size();
		this.livenessRandomCount = livenessRandomCount <= count?livenessRandomCount:count;
		this.setLivenessRandom(true);
	}

	public boolean isVerifyLive() {
		return this.isVerifyLive;
	}

	public void setVerifyLive(boolean verifyLive) {
		this.isVerifyLive = verifyLive;
	}

	public int getFaceDecodeNumberOfThreads() {
		return this.faceDecodeNumberOfThreads;
	}

	public void setFaceDecodeNumberOfThreads(int faceDecodeNumberOfThreads) {
		this.faceDecodeNumberOfThreads = faceDecodeNumberOfThreads;
	}

	public List<LivenessTypeEnum> getLivenessTypeList() {
		List newLivenessTypeList = new ArrayList();
		if(this.livenessTypeList != null && this.livenessTypeList.size() != 0) {
			if(this.isLivenessRandom) {
				Collections.shuffle(this.livenessTypeList);
			}

			((List)newLivenessTypeList).addAll(this.livenessTypeList.subList(0, this.livenessRandomCount));
		} else {
			this.livenessTypeList.addAll(FaceEnvironment.livenessTypeDefaultList);
			if(this.isLivenessRandom) {
				Collections.shuffle(this.livenessTypeList);
			}

			newLivenessTypeList = this.livenessTypeList.subList(0, this.livenessRandomCount);
		}

		return (List)newLivenessTypeList;
	}

	public void setLivenessTypeList(List<LivenessTypeEnum> list) {
		this.livenessTypeList = list;
	}
}
