package com.tien.ai.utils;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.tien.ai.AIApplication;

/**
 * @Description:
 * @author:wangtf
 * @see:
 * @since:
 * @copyright © baidu.com
 * @Date:2014-4-24
 */
public class NetworkUtil {

	public static Map<String, String> initRequestParams() {

		Map<String, String> params = new HashMap<String, String>();
		params.put("token", PreferenceUtils.loadToken());

		return params;
	}

	public static boolean isNetworkConnected() {
		Context context = AIApplication.getInstance().getApplicationContext();
		ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
		if (mNetworkInfo != null) {
			return mNetworkInfo.isAvailable();
		}
		return false;
	}

}
