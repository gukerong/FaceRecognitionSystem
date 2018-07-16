package com.szxmrt.app.facerecognitionsystem.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * Created by gkr on 2018/01/22
 * 信息存储类
 */

public class SharedPreferencesUtil {
    private Editor mEditor;
    private SharedPreferences mPreferences;
    private static final String TAG = SharedPreferencesUtil.class.getSimpleName();

    @SuppressLint("CommitPrefEdits")
    public SharedPreferencesUtil(Context context, String fileName) {
        this.mPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        this.mEditor = this.mPreferences.edit();
        //LogUtil.D(TAG," create SharedPreferencesUtil; name : " + mFileName + "; mode : " + mMode);
    }
    @SuppressLint("CommitPrefEdits")
    public void setEditor(Context context, String fileName){
        this.mPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        this.mEditor = this.mPreferences.edit();
    }

    public SharedPreferences getmPreferences() {
        return mPreferences;
    }

    // 读写配置文件
    public boolean putString(String name, String value) {
        mEditor.putString(name, value);
        //LogUtil.D(TAG, " put key : "+name+", value : "+value+" to file : "+mFileName+" result: "+result);
        return mEditor.commit();
    }

    public boolean putLong(String name, Long value) {
        mEditor.putLong(name, value);
        //LogUtil.D(TAG, " put key : "+name+", value : "+value+" to file : "+mFileName+" result: "+result);
        return mEditor.commit();
    }

    public boolean putInt(String name, int value) {
        mEditor.putInt(name, value);
        //LogUtil.D(TAG, " put key : "+name+", value : "+value+" to file : "+mFileName+" result: "+result);
        return mEditor.commit();
    }

    public boolean putBoolean(String name, Boolean value) {
        mEditor.putBoolean(name, value);
        //LogUtil.D(TAG, " put key : "+name+", value : "+value+" to file : "+mFileName+" result: "+result);
        return mEditor.commit();
    }

    public boolean remove(String name) {
        mEditor.remove(name);
        //LogUtil.D(TAG, " remove key : "+name+" from file : "+mFileName+" result: "+result);
        return mEditor.commit();
    }

    public boolean clear() {
        mEditor.clear();
        //LogUtil.D(TAG, " clear file : "+mFileName+" result: "+result);
        return mEditor.commit();
    }

    public long getLong(String key) {
        return mPreferences.getLong(key, 0);
    }

    public int getInt(String key) {
        return mPreferences.getInt(key, 0);
    }

    public Boolean getBoolean(String key) {
        return mPreferences.getBoolean(key, false);
    }

    public String getString(String key) {
        return mPreferences.getString(key, "");
    }

    public long getLong(String key, long defValue) {
        return mPreferences.getLong(key, defValue);
    }

    public int getInt(String key, int defValue) {
        return mPreferences.getInt(key, defValue);
    }

    public Boolean getBoolean(String key, boolean defValue) {
        return mPreferences.getBoolean(key, defValue);
    }

    public String getString(String key, String defValue) {
        return mPreferences.getString(key, defValue);
    }
    public Editor getEditor() {
        return mEditor;
    }
}