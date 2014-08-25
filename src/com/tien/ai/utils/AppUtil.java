/**
 * 
 */
package com.tien.ai.utils;

import java.util.Collections;
import java.util.List;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

/**
 * TODO
 * @author wangtianfei01
 *
 */
public class AppUtil {
    
    
    public static boolean processStatus(Context context) {  
        PackageManager pm = context.getPackageManager();  
        // 查询所有已经安装的应用程序  
        List<ApplicationInfo> listAppcations = pm.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);  
        Collections.sort(listAppcations,new ApplicationInfo.DisplayNameComparator(pm));// 排序  
  
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);  
        // 通过调用ActivityManager的getRunningAppProcesses()方法获得系统里所有正在运行的进程  
        List<ActivityManager.RunningAppProcessInfo> appProcessList = mActivityManager  
                .getRunningAppProcesses();  
  
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcessList) {  
            String processName = appProcess.processName; // 进程名  
            Log.i("wanges", "processName:"+processName);
            if(processName.contains("com.tien.ai:bdservice_v1")){
                return true;
            }
  
        }  
  
        return false;  
  
    }  
    
}
