package com.szxmrt.app.facerecognitionsystem.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import com.szxmrt.app.facerecognitionsystem.R;
import java.lang.ref.WeakReference;

/**
 * Created by Administrator on 2018-06-21
 */

public class WifiStateView extends AppCompatImageView {

	private WifiManager wifiManager;

	private WifiHandler wifiHandler;

	//没有开启Wifi或开启了Wifi但没有连接
	private static final int LEVEL_NONE = 0;

	//Wifi信号等级（最弱）
	private static final int LEVEL_1 = 1;

	//Wifi信号等级
	private static final int LEVEL_2 = 2;

	//Wifi信号等级
	private static final int LEVEL_3 = 3;

	//Wifi信号等级（最强）
	private static final int LEVEL_4 = 4;

	private final String TAG = "WifiStateView";
	public WifiStateView(Context context) {
		super(context);
	}

	public WifiStateView(Context context, @Nullable AttributeSet attrs) {
		super(context,attrs);
		wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		wifiHandler = new WifiHandler(this);
	}

	public WifiStateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

	}

	private static class WifiHandler extends Handler {

		//虚引用
		private WeakReference<WifiStateView> stateViewWeakReference;

		WifiHandler(WifiStateView wifiStateView) {
			stateViewWeakReference = new WeakReference<>(wifiStateView);
		}

		@Override
		public void handleMessage(Message msg) {
			WifiStateView wifiStateView = stateViewWeakReference.get();
			if (wifiStateView == null) {
				return;
			}
			switch (msg.what) {
				case LEVEL_1:
					wifiStateView.setImageResource(R.mipmap.wifi_1);
					break;
				case LEVEL_2:
					wifiStateView.setImageResource(R.mipmap.wifi_2);
					break;
				case LEVEL_3:
					wifiStateView.setImageResource(R.mipmap.wifi_3);
					break;
				case LEVEL_4:
					wifiStateView.setImageResource(R.mipmap.wifi_4);
					break;
				case LEVEL_NONE:
				default:
					wifiStateView.setImageResource(R.mipmap.wifi_none);
					break;
			}
		}
	}

	private BroadcastReceiver wifiStateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			switch (intent.getAction()) {
				case WifiManager.WIFI_STATE_CHANGED_ACTION:
					if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLING) {
						wifiHandler.sendEmptyMessage(LEVEL_NONE);
					}
					break;
				case WifiManager.RSSI_CHANGED_ACTION:
					if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLED) {
						wifiHandler.sendEmptyMessage(LEVEL_NONE);
						return;
					}
					WifiInfo wifiInfo = wifiManager.getConnectionInfo();
					int level = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), 5);
					wifiHandler.sendEmptyMessage(level);
					break;
			}
		}
	};




	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		IntentFilter intentFilter = new IntentFilter();
		//Wifi连接状态变化
		intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		//Wifi信号强度变化
		intentFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
		getContext().registerReceiver(wifiStateReceiver, intentFilter);
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		wifiHandler.removeCallbacksAndMessages(null);
		getContext().unregisterReceiver(wifiStateReceiver);
	}

}