package com.szxmrt.app.facerecognitionsystem.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.szxmrt.app.facerecognitionsystem.util.PermissionUtil;

import java.util.Timer;
import java.util.TimerTask;


public class SplashActivity extends AppCompatActivity {
	private Timer timer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	    View view = this.getWindow().getDecorView();
	    int visibility = View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
	    view.setSystemUiVisibility(visibility);
        PermissionUtil permissionUtil = new PermissionUtil(this);
        if (!permissionUtil.checkPermission(Manifest.permission.CAMERA)) {
            permissionUtil.requestPermission(new String[]{Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.INTERNET}, 1);
        }else {
            startActivity();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					startActivity();
                } else {
                    finish();
                }
                break;
            default:
                break;
        }
    }
	public void startActivity(){
		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				Intent intent = new Intent(SplashActivity.this,Camera1RecognitionActivity.class);
				startActivity(intent);
				finish();
			}
		},3000);
	}

	@Override
	protected void onResume() {
		super.onResume();
		IntentFilter usbFilter = new IntentFilter();
		usbFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
		usbFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
		registerReceiver(mUsbReceiver, usbFilter);
	}

	@Override
	protected void onPause() {
		super.onPause();
		timer.cancel();
		unregisterReceiver(mUsbReceiver);
	}
	private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
/*			String action = intent.getAction();
			tvInfo.append("BroadcastReceiver in\n");

			if(UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
				tvInfo.append("ACTION_USB_DEVICE_ATTACHED\n");
			} else if(UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
				tvInfo.append("ACTION_USB_DEVICE_DETACHED\n");
			}*/
		}
	};
	@Override
    protected void onStop() {
        super.onStop();
    }
}
