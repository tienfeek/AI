/**
 * 
 */
package com.tien.ai.receiver;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.tien.ai.Utils;
import com.tien.ai.utils.XLog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.os.Handler;

/**
 * TODO
 * @author wangtianfei01
 *
 */
public class BootReceiver extends BroadcastReceiver {  
    
      
    @Override   
    public void onReceive(final Context context, Intent intent) {   
        
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            
            @Override
            public void run() {
                initPushWork(context); 
            }
            
        }, 700);
  
    }

    private void initPushWork(Context context) {
        boolean success = false;   
      
        //获得网络连接服务   
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);   
        // State state = connManager.getActiveNetworkInfo().getState();   
        // 获取WIFI网络连接状态  
        State state = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();   
        // 判断是否正在使用WIFI网络   
        if (State.CONNECTED == state) {  
            success = true;   
        }   
        // 获取GPRS网络连接状态   
        state = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();  
        // 判断是否正在使用GPRS网络   
        if (State.CONNECTED == state){   
            success = true;   
        }   
      
        if (success) {   
            XLog.i("wanges", " BootReceiver startwork pre:");
            PushManager.startWork(context.getApplicationContext(),
                PushConstants.LOGIN_TYPE_API_KEY, Utils.getMetaValue(context, "api_key"));
            XLog.i("wanges", " BootReceiver startwork post:");
//            if (!Utils.hasBind(context.getApplicationContext())) {
//                PushManager.startWork(context.getApplicationContext(),
//                    PushConstants.LOGIN_TYPE_API_KEY, Utils.getMetaValue(context, "api_key"));
//            } else {
//                if (!PushManager.isPushEnabled(context)) {
//                    PushManager.resumeWork(context);
//                }
//            }
        }
    }
 }  