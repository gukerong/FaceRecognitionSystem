package com.szxmrt.app.facerecognitionsystem.util;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * 动态权限请求工具类
 */

public class PermissionUtil {
    private Activity context;
    public PermissionUtil(Activity context){
        this.context = context;
    }

    /**
     * 权限检查
     * @param permission 权限
     * @return  true：权限已获得
     */
    public Boolean checkPermission(String permission){
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermission(String[] permissions ,int requestCode){
        ActivityCompat.requestPermissions(context,permissions,requestCode);
    }

}
