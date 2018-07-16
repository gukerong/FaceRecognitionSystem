package com.szxmrt.app.facerecognitionsystem.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Administrator on 2018-06-21
 */

public class ClockTextView extends android.support.v7.widget.AppCompatTextView {
	private String DEFAULT_TIME_FORMAT = "yy-MM-dd HH:mm";
	private boolean startClock = true;
	private MyHandler myHandler;
	private SimpleDateFormat dateFormatter = new SimpleDateFormat(DEFAULT_TIME_FORMAT, Locale.CHINESE);
	public ClockTextView(Context context) {
		super(context);
	}

	public ClockTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		myHandler = new MyHandler(this);
		//init();
	}

	public ClockTextView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	static class MyHandler extends Handler{
		private WeakReference<ClockTextView> weakReference;
		private MyHandler(ClockTextView clockTextView){
			weakReference = new WeakReference<>(clockTextView);
		}
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what){
				case 0:
					weakReference.get().setText((String)msg.obj);
					break;
				default:
					break;
			}
		}
	}
	private void init() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while(startClock){
					myHandler.obtainMessage(0,dateFormatter.format(Calendar.getInstance().getTime())).sendToTarget();
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
	//根据毫秒时间获取格式化的提示
	private String convertTimeToFormat(long timeMills) {
		long curTime = Calendar.getInstance().getTimeInMillis();
		long time = (curTime - timeMills) / (long)1000;//已经将单位转换成秒

		if (time > 0 && time < 60) {
			return "刚刚";
		} else if (time >= 60 && time < 3600) {
			return time / 60 + "分钟前";
		} else if (time >= 3600 && time < 3600 * 24) {
			return time / 3600 + "小时前";
		} else if (time >= 3600 * 24 && time < 3600 * 24 * 30) {
			return time / 3600 / 24 + "天前";
		} else if (time >= 3600 * 24 * 30 && time < 3600 * 24 * 30 * 12) {
			return time / 3600 / 24 / 30 + "个月前";
		} else if (time >= 3600 * 24 * 30 * 12) {
			return time / 3600 / 24 / 30 / 12 + "年前";
		} else {
			return "刚刚";
		}
	}
}
