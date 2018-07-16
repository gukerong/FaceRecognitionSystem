package com.szxmrt.app.facerecognitionsystem.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android_serialport_api.SerialPort;


public class SerialPortUtil {
    private CRC16 crc16;
    private HexString hexString;
    private OutputStream outputStream;
    private InputStream inputStream;
    private String sData;
    private SerialPort serialPort;
    private int[] verifys;
    private static SerialPortUtil serialPortUtil;
    private SerialPortUtil()throws IOException{
    	if(serialPort == null){
    		LogUtil.e("SerialPortUtil","打开新串口");
		    serialPort = new SerialPort(new File("/dev/ttyS3"), 9600, 8, 1, 'E');
	    }
	    outputStream = serialPort.getOutputStream();
	    inputStream = serialPort.getInputStream();
	    crc16 = new CRC16();
	    hexString = new HexString();
	    verifys = new int[1];
	    verifys[0] = 1;
	}
    public static SerialPortUtil getseialPortUtil()throws IOException{
	    if(serialPortUtil == null){
		    synchronized (SerialPortUtil.class){
			    if (serialPortUtil == null){
				    serialPortUtil = new SerialPortUtil();
				    LogUtil.e("SerialPortUtil","打开新串口1");
			    }
		    }
	    }
	    return serialPortUtil;
    }
    synchronized public void send(String test){
        byte[] data = hexString.getHexBytes(test);
        String checkout = crc16.crcVal(data);
        checkout = checkout.toUpperCase();
        sData = test + checkout;
        //Log.e("sendrecevice",sData);
        data = hexString.getHexBytes(sData);
        try {
        	if(outputStream == null){
        		LogUtil.e("null","null");
	        }
            outputStream.write(data);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    synchronized public String readRecevice(byte[] buffer,int size){
            StringBuilder stringBuilder = new StringBuilder();
            String result = "";
            int count = 0;
            try {
                if (inputStream == null) return null;
                while (count<size){
                    count += inputStream.read(buffer,count,size-count);
                    if(count==-1){
                        break;
                    }
                }
                for(int i=0;i<size;i++){
                    stringBuilder.append(hexString.byteToHexStr(buffer[i]));
                }
                result = stringBuilder.toString();
	            stringBuilder = null;
                return result;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
    }
    public String PLCControl(String send,byte[] buffer,int size){
        send(send);
        return readRecevice(buffer,size);
    }
    public void closeStream(){
        try {
            if(outputStream != null){
                outputStream.close();
                outputStream = null;
            }
            if (inputStream != null){
                inputStream.close();
                inputStream = null;
            }
            if(serialPort != null){
	            serialPort.close();
	            serialPort = null;
            }
            if (serialPortUtil != null){
            	serialPortUtil = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
