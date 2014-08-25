package com.tien.ai.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.tien.ai.Utils;
import com.tien.ai.receiver.AlarmReceiver;

public class PollMessageManager {
    /**
     * 20分钟一次
     */
    public static final long INTERVAL = 20 * 60 * 1000;
    public static boolean isRunning = false;
    
    public static void checkLastest(final Context context) {
        synchronized (context) {
            if (isRunning)
                return;
            
            isRunning = true;
        }
        
        
        try {
            if (!Utils.hasBind(context.getApplicationContext())) {
                PushManager.startWork(context.getApplicationContext(),
                    PushConstants.LOGIN_TYPE_API_KEY, Utils.getMetaValue(context, "api_key"));
            } else {
                if (!PushManager.isPushEnabled(context)) {
                    PushManager.resumeWork(context);
                }
            }
        } catch (Exception e) {
            
        } catch (Error e) {
        }
        
    }
    
    // 20分钟一次
    public static void scheduleCheck(final Context context) {
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent in = new Intent(context, AlarmReceiver.class);
        PendingIntent pendIntent = PendingIntent.getBroadcast(context, 0, in,
            PendingIntent.FLAG_UPDATE_CURRENT);
        int triggerAtTime = (int) (SystemClock.elapsedRealtime() + 5 * 1000);
        alarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, INTERVAL,
            pendIntent);
    }
    
    public static void unscheduleCheck(final Context context) {
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent in = new Intent(context, AlarmReceiver.class);
        PendingIntent pendIntent = PendingIntent.getBroadcast(context, 0, in,
            PendingIntent.FLAG_UPDATE_CURRENT);
        alarmMgr.cancel(pendIntent);
    }
    
    // 20分钟一次
    public static void scheduleAtTimeCheck(final Context context, long triggerAtTime) {
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent in = new Intent(context, AlarmReceiver.class);
        PendingIntent pendIntent = PendingIntent.getBroadcast(context, 0, in,
            PendingIntent.FLAG_UPDATE_CURRENT);
        alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pendIntent);
    }
}
