package com.szxmrt.app.facerecognitionsystem.util;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FT311SerialPort {
	private Context context;
	private UsbAccessory usbAccessory;
	private PendingIntent pendingIntent;
	private UsbManager usbManager;
	private boolean isPermission = false;
	private static final String ACTION_USB_PERMISSION = "com.szxmrt.app.USB_PERMISSION";
	private byte[] writeUsbData = new byte[256];
	private ParcelFileDescriptor parcelFileDescriptor;
	private FileInputStream fileInputStream;
	private FileOutputStream fileOutputStream;
	private int readCount = 0;
	private HexString hexString = new HexString();
	public FT311SerialPort(Context context){
		this.context = context;
		getUsbManager();
	}
	private void getUsbManager(){
		usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
		if(usbManager!=null){
			UsbAccessory[] usbAccessories = usbManager.getAccessoryList();
			if (usbAccessories!=null && usbAccessories.length>0){
				usbAccessory = usbAccessories[0];
/*				textView.setText("model : "+usbAccessory.getModel()
								+"  version : "+usbAccessory.getVersion()
								+"  manufacturer"+usbAccessory.getManufacturer()
								+"  "+usbAccessory.getSerial()
								+"\r\n");*/
				if(!usbManager.hasPermission(usbAccessory)){
					usbManager.requestPermission(usbAccessory,pendingIntent);
				}else {
					openAccessory(usbAccessory);
				}
			}
		}
		pendingIntent = PendingIntent.getBroadcast(context,0,new Intent(ACTION_USB_PERMISSION),PendingIntent.FLAG_CANCEL_CURRENT);
		IntentFilter intentFilter = new IntentFilter(ACTION_USB_PERMISSION);
		//intentFilter.addAction(UsbManager.EXTRA_PERMISSION_GRANTED);
		context.registerReceiver(broadcastReceiver,intentFilter);
	}
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (ACTION_USB_PERMISSION.equals(action)) {
				synchronized (this) {
					UsbAccessory usbAccessory = intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
					if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
						if (usbAccessory != null) {
							Toast.makeText(context,"获取usb权限",Toast.LENGTH_SHORT).show();
							openAccessory(usbAccessory);
						}
					} else {
						Toast.makeText(context,"没有usb权限",Toast.LENGTH_SHORT).show();
						isPermission = false;
					}
				}
			}
		}
	};

	/**
	 *
	 * @param baud       baud rate
	 * @param dataBits  8:8bit, 7: 7bit
	 * @param stopBits  1:1stop bits, 2:2 stop bits
	 * @param parity    0: none, 1: odd, 2: even, 3: mark, 4: space
	 * @param flowControl   0:none, 1: flow control(CTS,RTS)
	 */
	public void setConfig(int baud, byte dataBits, byte stopBits, byte parity, byte flowControl) {
		/*prepare the baud rate buffer*/
		writeUsbData[0] = (byte)baud;
		writeUsbData[1] = (byte)(baud >> 8);
		writeUsbData[2] = (byte)(baud >> 16);
		writeUsbData[3] = (byte)(baud >> 24);
		/*data bits*/
		writeUsbData[4] = dataBits;
		/*stop bits*/
		writeUsbData[5] = stopBits;
		/*parity*/
		writeUsbData[6] = parity;
		/*flow control*/
		writeUsbData[7] = flowControl;
		/*send the UART configuration packet*/
		sendPacket(8);
	}


	private void openAccessory(UsbAccessory accessory) {
		parcelFileDescriptor = usbManager.openAccessory(accessory);
		if(parcelFileDescriptor != null){
			FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
			fileInputStream = new FileInputStream(fileDescriptor);
			fileOutputStream = new FileOutputStream(fileDescriptor);
			/*check if any of them are null*/
			if(fileInputStream == null) return;
			isPermission = true;
		}
	}

	public void CloseAccessory() {
		try{
			if (parcelFileDescriptor != null)  parcelFileDescriptor.close();
			if (fileInputStream != null) fileInputStream.close();
			if (fileOutputStream != null) fileOutputStream.close();
			parcelFileDescriptor = null;
			fileInputStream = null;
			fileOutputStream = null;
		}catch (IOException e){
			e.printStackTrace();
		}
	}
	/*method to send on USB*/
	private void sendPacket(int numBytes) {
		try {
			if(fileOutputStream != null){
				fileOutputStream.write(writeUsbData, 0,numBytes);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void checkDoor(byte[] bytes,int count){
		try {
			//readCount = count;
			if (fileOutputStream != null){
				fileOutputStream.write(bytes);
			}
			if (fileInputStream != null){
				while (readCount<count){
					readCount += fileInputStream.read(bytes,readCount,count);
				}
			}
		} catch (IOException e){
			e.printStackTrace();
		}
	}
	public void sendPacket(byte[] bytes){
		try {
			if(fileOutputStream != null){
				fileOutputStream.write(bytes);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public String readData(int size){
		byte[] bytes = new byte[1025];
		StringBuilder stringBuilder = new StringBuilder();
		if (fileInputStream!=null){
			int count = 0;
			while (count<size){
				try {
					count += fileInputStream.read(bytes,count,size);
					if (count==-1){
						break;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			for(int i=0;i<size;i++){
				stringBuilder.append(hexString.byteToHexStr(bytes[i]));
			}
		}
		return stringBuilder.toString();
	}
}
