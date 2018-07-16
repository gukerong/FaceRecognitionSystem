package com.szxmrt.app.facerecognitionsystem.presenter;

import android.content.Context;
import android.widget.Toast;

import com.szxmrt.app.facerecognitionsystem.activity.SettingActivity;
import com.szxmrt.app.facerecognitionsystem.base.BaseActivityPresenter;
import com.szxmrt.app.facerecognitionsystem.face.FaceDeleteResponse;
import com.szxmrt.app.facerecognitionsystem.face.GetBaiduToken;
import com.szxmrt.app.facerecognitionsystem.util.CRC16;
import com.szxmrt.app.facerecognitionsystem.util.FT311SerialPort;
import com.szxmrt.app.facerecognitionsystem.util.HexString;
import com.szxmrt.app.facerecognitionsystem.util.LogUtil;
import com.szxmrt.app.facerecognitionsystem.util.SharedPreferencesUtil;

import java.io.IOException;

/**
 * Created by Administrator on 2018-06-22
 */

public class SettingActivityPresenter extends BaseActivityPresenter<SettingActivity> {
	private FaceDeleteResponse faceDeleteResponse = new FaceDeleteResponse();
	private static final String CONFIG = "drinkVMConfig";//存放用户信息文件名
	private GetBaiduToken getBaiduToken = new GetBaiduToken();
	private HexString hexString = new HexString();
	private CRC16 crc16 = new CRC16();
	private FT311SerialPort ft311SerialPort;
	public SettingActivityPresenter(SettingActivity activity) {
		super(activity);
	}
	public boolean deleteGroupUser(String group_id,String uid){
		try {
			return faceDeleteResponse.deleteUser(group_id,uid);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	public void updateToken(){
		String token = getBaiduToken.getToken();
		if(token!=null){
			SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(getActivity(),CONFIG);
			boolean b = sharedPreferencesUtil.putString("token",token);
			if (b){
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(getActivity(),"token更新成功",Toast.LENGTH_SHORT).show();
					}
				});
			}else {
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(getActivity(),"token存储失败",Toast.LENGTH_SHORT).show();
					}
				});
			}
		}else {
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(getActivity(),"token更新失败",Toast.LENGTH_SHORT).show();
				}
			});

		}
	}
	public void openUsbSerialPort(Context context){
		ft311SerialPort = new FT311SerialPort(context);
		ft311SerialPort.setConfig(19200,(byte)8,(byte)1,(byte) 2,(byte) 0);
	}
	public void closeSerialPort(){
		ft311SerialPort.CloseAccessory();
	}
	public synchronized void controlDoor(String s){
		String crc = crc16.crcVal(hexString.getHexBytes(s));
		ft311SerialPort.sendPacket(hexString.getHexBytes(s+crc));
		ft311SerialPort.readData(6);
	}

}
