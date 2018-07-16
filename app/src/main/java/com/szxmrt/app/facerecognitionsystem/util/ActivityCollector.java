package com.szxmrt.app.facerecognitionsystem.util;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * 活动管理类，添加活动和销毁所有活动
 */
public class ActivityCollector {
    public static List<Activity> activities = new ArrayList<>();
    public static void addActivity(Activity activity){
        activities.add(activity);
    }
    public static void removeActivity(Activity activity){
        activities.remove(activity);
    }
    public static void finishAllActivity(){
        for (Activity activity : activities){
            if(!activity.isFinishing()){
                activity.finish();
            }
        }
    }
}
