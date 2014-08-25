package com.tien.ai.listener;

import android.app.Activity;
import android.content.Intent;

import com.tien.ai.LoginActivity;
import com.tien.ai.R;
import com.tien.ai.utils.ToastUtil;
import com.tien.volley.NetworkError;
import com.tien.volley.NoConnectionError;
import com.tien.volley.Response.ErrorListener;
import com.tien.volley.ServerError;
import com.tien.volley.TimeoutError;
import com.tien.volley.TokenExpire;
import com.tien.volley.VolleyError;


/**
 * @Description:
 * @author:wangtf
 * @see:   
 * @since:      
 * @copyright © baidu.com
 * @Date:2014-4-11
 */
public class NetworkErrorResponeListener implements ErrorListener {

	private Activity activity;
	
	public NetworkErrorResponeListener(Activity activity){
		this.activity = activity;
	}
	@Override
	public void onErrorResponse(VolleyError error) {
		
		if(error instanceof TimeoutError){
			ToastUtil.shortToast(R.string.timeout_error);
		}else if(error instanceof  ServerError){
			ToastUtil.shortToast(R.string.timeout_error);
		}else if(error instanceof  NoConnectionError){
		    ToastUtil.shortToast(R.string.timeout_error);
		}else if(error instanceof  NetworkError){
			ToastUtil.shortToast(R.string.timeout_error);
		}else if(error instanceof  TokenExpire){
			if(activity != null ){
				Intent intent = new Intent(activity, LoginActivity.class);
				activity.startActivity(intent);
				activity.finish();
			}
			
		}
	}

}
