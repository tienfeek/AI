/**
 * 
 */
package com.tien.ai.utils;

import java.text.SimpleDateFormat;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.tien.ai.AIApplication;

/**
 * TODO
 * @author wangtianfei01
 *
 */
public class PreferenceUtils {
    
    
    public static void  saveUidAndToken(String uid, String token, String phone){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(AIApplication.getInstance().getApplicationContext());
        Editor editor = sp.edit();
        editor.putString("uid", uid);
        editor.putString("token", token);
        editor.putString("phone", phone);
        editor.commit();
    }
    
    public static String loadToken(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(AIApplication.getInstance().getApplicationContext());
        return sp.getString("token", "");
    }
    
    public static String loadUid(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(AIApplication.getInstance().getApplicationContext());
        return sp.getString("uid", "");
    }
    
    public static String loadPhone(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(AIApplication.getInstance().getApplicationContext());
        return sp.getString("phone", "");
    }
    
    
    
    public void saveAiListTime(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(AIApplication.getInstance().getApplicationContext());
        Editor editor = sp.edit();
        editor.putLong("ai_list_update_time", System.currentTimeMillis());
        editor.commit();
    }
    
    public static boolean ifNeedUpdateAiList(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(AIApplication.getInstance().getApplicationContext());
        long lastUpdateTime = sp.getLong("ai_list_update_time", 0);
        
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");  
        String lastUpdateTimeStr = sf.format(lastUpdateTime);  
        String currentTime = sf.format(System.currentTimeMillis());  
        if(lastUpdateTimeStr.equals(currentTime)){
            return false;
        }else{
            return true;
        }
    }
    
}
