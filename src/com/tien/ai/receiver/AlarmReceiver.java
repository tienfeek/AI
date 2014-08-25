/**
 * 
 */
package com.tien.ai.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.tien.ai.Utils;
import com.tien.ai.net.NetWorkInfoUtil;
import com.tien.ai.utils.XLog;


public class AlarmReceiver extends BroadcastReceiver {
    
    @Override
    public void onReceive(Context context, Intent intent) {
        XLog.i("wanges", " AlarmReceiver NetWorkInfoUtil.isConencting(context):"+NetWorkInfoUtil.isConencting(context));
        if (NetWorkInfoUtil.isConencting(context)) {
            XLog.i("wanges", " AlarmReceiver startwork pre:");
            PushManager.startWork(context.getApplicationContext(),
                PushConstants.LOGIN_TYPE_API_KEY, Utils.getMetaValue(context, "api_key"));
            XLog.i("wanges", " AlarmReceiver startwork post:");
//            try{
//                if (!Utils.hasBind(context.getApplicationContext())) {
//                    PushManager.startWork(context.getApplicationContext(),
//                        PushConstants.LOGIN_TYPE_API_KEY, Utils.getMetaValue(context, "api_key"));
//                } else {
//                    if (!PushManager.isPushEnabled(context)) {
//                        PushManager.resumeWork(context);
//                    }
//                }
//            }
//            catch(Exception e){
//                
//            }
//            catch (Error e) {
//            }
            
        }
    }
       
   
}