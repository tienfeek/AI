package com.tien.ai.utils;

import android.content.Context;
import android.widget.Toast;

import com.tien.ai.AIApplication;


/**
 * @Description:
 * @author:wangtf
 * @see:   
 * @since:      
 * @copyright © baidu.com
 * @Date:2014-4-15
 */
public class ToastUtil {

	public static void shortToast(int resId){
		Context context = AIApplication.getInstance().getApplicationContext();
		Toast.makeText(context, context.getText(resId), Toast.LENGTH_SHORT).show();
	}
	
	public static void shortToast(String toast){
		Context context = AIApplication.getInstance().getApplicationContext();
		Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
	}
	
	public static void longToast(int resId){
		Context context = AIApplication.getInstance().getApplicationContext();
		Toast.makeText(context, context.getText(resId), Toast.LENGTH_LONG).show();
	}
	
	public static void longToast(String toast){
		Context context = AIApplication.getInstance().getApplicationContext();
		Toast.makeText(context, toast, Toast.LENGTH_LONG).show();
	}
	
	public static void specifyTimeToast(int resId, int time){
		Context context = AIApplication.getInstance().getApplicationContext();
		Toast.makeText(context, context.getText(resId), time).show();
	}
	
	public static void specifyTimeToast(String toast, int time){
		Context context = AIApplication.getInstance().getApplicationContext();
		Toast.makeText(context, toast, time).show();
	}
}
