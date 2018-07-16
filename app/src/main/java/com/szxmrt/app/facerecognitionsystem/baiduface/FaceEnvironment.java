package com.szxmrt.app.facerecognitionsystem.baiduface;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018-06-20
 */

public class FaceEnvironment {
	public static final String TAG = "Baidu-IDL-FaceSDK";
	public static final String OS = "android";
	public static final String SDK_VERSION = "3.1.0.0";
	public static final int AG_ID = 3;
	public static final float VALUE_BRIGHTNESS = 40.0F;
	public static final float VALUE_BLURNESS = 0.5F;
	public static final float VALUE_OCCLUSION = 0.5F;
	public static final int VALUE_HEAD_PITCH = 10;
	public static final int VALUE_HEAD_YAW = 10;
	public static final int VALUE_HEAD_ROLL = 10;
	public static final int VALUE_CROP_FACE_SIZE = 400;
	public static final int VALUE_MIN_FACE_SIZE = 200;
	public static final float VALUE_NOT_FACE_THRESHOLD = 0.6F;
	public static final boolean VALUE_IS_CHECK_QUALITY = true;
	public static final int VALUE_DECODE_THREAD_NUM = 2;
	public static final int VALUE_LIVENESS_DEFAULT_RANDOM_COUNT = 3;
	public static final int VALUE_MAX_CROP_IMAGE_NUM = 1;
	public static long TIME_TIPS_REPEAT = 3000L;
	public static long TIME_MODULE = 0L;
	public static long TIME_DETECT_NO_FACE_CONTINUOUS = 1000L;
	public static long TIME_DETECT_MODULE = 15000L;
	public static long TIME_LIVENESS_MODULE = 15000L;
	private static boolean mIsDebug = false;
	private static int[] mSoundIds;
	private static int[] mTipsTextIds;
	public static final List<LivenessTypeEnum> livenessTypeDefaultList = new ArrayList<>();

	static {
		livenessTypeDefaultList.add(LivenessTypeEnum.Eye);
		livenessTypeDefaultList.add(LivenessTypeEnum.Mouth);
		livenessTypeDefaultList.add(LivenessTypeEnum.HeadUp);
		livenessTypeDefaultList.add(LivenessTypeEnum.HeadDown);
		livenessTypeDefaultList.add(LivenessTypeEnum.HeadLeft);
		livenessTypeDefaultList.add(LivenessTypeEnum.HeadRight);
		mSoundIds = new int[FaceStatusEnum.values().length];
		mTipsTextIds = new int[FaceStatusEnum.values().length];
		for(int i = 0; i < mSoundIds.length; ++i) {
			mSoundIds[i] = 0;
			mTipsTextIds[i] = 0;
		}
	}
	public FaceEnvironment() {
	}

	public static boolean isDebugable() {
		return mIsDebug;
	}

	public static int getSoundId(FaceStatusEnum status) {
		int soundId = mSoundIds[status.ordinal()];
		return soundId;
	}

	public static int getTipsId(FaceStatusEnum status) {
		int tipsId = mTipsTextIds[status.ordinal()];
		return tipsId;
	}

	public static void setSoundId(FaceStatusEnum status, int soundId) {
		if(mSoundIds != null) {
			try {
				mSoundIds[status.ordinal()] = soundId;
			} catch (Exception var3) {
				var3.printStackTrace();
			}
		}
	}

	public static void setTipsId(FaceStatusEnum status, int tipsId) {
		if(mTipsTextIds != null) {
			try {
				mTipsTextIds[status.ordinal()] = tipsId;
			} catch (Exception var3) {
				var3.printStackTrace();
			}
		}

	}


}
