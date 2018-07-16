package com.szxmrt.app.facerecognitionsystem.presenter;

import android.app.Activity;
import android.app.PendingIntent;
import android.util.Log;
import android.widget.TextView;

import com.szxmrt.app.facerecognitionsystem.activity.Camera1RecognitionActivity;
import com.szxmrt.app.facerecognitionsystem.base.BaseActivity;
import com.szxmrt.app.facerecognitionsystem.base.BaseActivityPresenter;
import com.szxmrt.app.facerecognitionsystem.entity.PLCOrder;
import com.szxmrt.app.facerecognitionsystem.face.FaceDetectResponse;
import com.szxmrt.app.facerecognitionsystem.face.FaceIdentifyResponse;
import com.szxmrt.app.facerecognitionsystem.util.CRC16;
import com.szxmrt.app.facerecognitionsystem.util.FT311SerialPort;
import com.szxmrt.app.facerecognitionsystem.util.HexString;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2018-06-21
 */

public class Camera1RecognitionActivityPresenter extends BaseActivityPresenter<Camera1RecognitionActivity> {
	private FT311SerialPort ft311SerialPort;
	private byte[] bytes = new byte[1024];
	private CRC16 crc16 = new CRC16();
	private HexString hexString = new HexString();
	public Camera1RecognitionActivityPresenter(Camera1RecognitionActivity activity) {
		super(activity);
	}
	public int faceIdentify(final byte[] bytes){
		try {
			FaceDetectResponse faceDetectResponse = FaceDetectResponse.getBaiduFace();
			faceDetectResponse.faceDetection(bytes);
			FaceIdentifyResponse faceIdentifyResponse = FaceIdentifyResponse.getRegister();
			String body = faceIdentifyResponse.identify(bytes);
			String uid = "";
			String group_id = "";
			String user_info = "";
			int result_num = 0;
			double score = 0;
			JSONObject jsonObject = new JSONObject(body);
			int error_code = jsonObject.optInt("error_code",404);
			if(error_code==404){
				result_num = jsonObject.getInt("result_num");
				if(result_num==1){
					JSONArray resultArray = jsonObject.getJSONArray("result");
					JSONObject jsonObject1 = resultArray.getJSONObject(0);
					uid = jsonObject1.getString("uid");
					JSONArray scores = jsonObject1.getJSONArray("scores");
					score = scores.getDouble(0);
					group_id = jsonObject1.getString("group_id");
					user_info  = jsonObject1.getString("user_info");
					Log.e("face","score  "+score+"  uid  "+uid+"  group_id  "+group_id+"  user_info  "+user_info);
					if(score>=80.0){
						return 1000;

					}else {
						return 1001;
					}
				}else {
					return 1003;
				}
			}else if (error_code == 216618){
				return 1005;    //用户组内无用户
			}else {
				return 1004;
			}
		}catch (JSONException e){
			e.printStackTrace();
			return 1005;
		}
	}
	public void openDoor(){
		String crc = crc16.crcVal(hexString.getHexBytes("010548050000"));
		byte[] bytes = hexString.getHexBytes("010548050000"+crc.toUpperCase());
		ft311SerialPort.sendPacket(bytes);
		ft311SerialPort.readData(6);
	}
	public void closeDoor(){
		String crc = crc16.crcVal(hexString.getHexBytes("01054805FF00"));
		byte[] bytes = hexString.getHexBytes("01054805FF00"+crc.toUpperCase());
		ft311SerialPort.sendPacket(bytes);
		ft311SerialPort.readData(6);
	}
	public String readDoor(int size){
		String crc = crc16.crcVal(hexString.getHexBytes("010140070001"));
		byte[] data = hexString.getHexBytes("010140070001"+crc.toUpperCase());
		ft311SerialPort.sendPacket(data);
		return ft311SerialPort.readData(size);
		/*try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return ft311SerialPort.readData(bytes,size);*/
	}
	public void openDoor2(){
		String crc = crc16.crcVal(hexString.getHexBytes(PLCOrder.WM12));
		byte[] bytes = hexString.getHexBytes(PLCOrder.WM12+crc.toUpperCase());
		ft311SerialPort.sendPacket(bytes);
		ft311SerialPort.readData(6);
	}
	public void closeDoor2(){
		String crc = crc16.crcVal(hexString.getHexBytes(PLCOrder.WM13));
		byte[] bytes = hexString.getHexBytes(PLCOrder.WM13+crc.toUpperCase());
		ft311SerialPort.sendPacket(bytes);
		ft311SerialPort.readData(6);
	}
	public void openUsbSerialPort(Activity activity){
		ft311SerialPort = new FT311SerialPort(activity);
		ft311SerialPort.setConfig(19200,(byte)8,(byte)1,(byte)2,(byte)0);
	}
	public void closeUsbSerialPort(){
		ft311SerialPort.CloseAccessory();
	}
}
