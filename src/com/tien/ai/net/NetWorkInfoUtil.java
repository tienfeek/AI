package com.tien.ai.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.telephony.TelephonyManager;

/**
 * @author Tienfook
 * @version 2011-8-22 下午02:23:25
 */
public class NetWorkInfoUtil {

	public static Context context;

	/**
	 * 获取WIFI连接状态 UNKNOW DISCONNECTED CONNECTED
	 */
	public static String getWifiState(Context context) {

		String wifiState = null;
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		State wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
		if (wifi != null) {
			wifiState = wifi.toString();
		}
		return wifiState;
	}

	/**
	 * 获取WIFI连接状态是否已连接上
	 */
	public static boolean wifiConected(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		State wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
		return wifi == State.CONNECTED ? true : false;
	}

	/**
	 * 获取移动网络连接状态 UNKNOW DISCONNECTED CONNECTED
	 */
	public static String getMobileState(Context context) {

		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		String mobileState = null;
		State mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
		if (mobile != null) {
			mobileState = mobile.toString();
		}
		return mobileState;
	}

	/**
	 * 获取当前网络制式：UMTS WCDMA :联通 GPRS EDGE GSM 如果网络为WIFI 则 返回subtypeName为空
	 */
	public static String getActiveSubtypeName(Context context) {

		String subtypeName = null;
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		if (info != null) {
			subtypeName = info.getSubtypeName();
		}
		return subtypeName;
	}

	/**
	 * 获取当前首要接入网络类型：WIFI MOBILE
	 * 
	 * @param context
	 * @return WIFI MOBILE<只要是手机网络都返回mobile>
	 */
	public static String getActiveTypeName(Context context) {

		String activeTypeName = null;
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		if (info != null) {
			activeTypeName = info.getTypeName();
		}
		return activeTypeName;
	}

	/**
	 * /** 如果3G网络和wifi网络都未连接，且不是处于正在连接状态 则进入Network Setting界面 由用户配置网络连接
	 * 
	 * @param context
	 * @return 当前WIFI OR 3G是否连接
	 */
	// public static boolean checkNetworkInfoAndConfig(final Context context) {
	// ConnectivityManager cm = (ConnectivityManager) context
	// .getSystemService(Context.CONNECTIVITY_SERVICE);
	// // Network Typ
	// // mobile 3G Data Network
	// State mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
	// .getState();
	// // wifi
	// State wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
	// .getState();
	// boolean netSataus = false;
	// // 如果3G网络和wifi网络都未连接，且不是处于正在连接状态 则进入Network Setting界面 由用户配置网络连接
	// if (mobile == State.CONNECTED || mobile == State.CONNECTING)
	// return true;
	// if (wifi == State.CONNECTED || wifi == State.CONNECTING)
	// return true;
	// // netSataus = cm.getActiveNetworkInfo().isAvailable();
	// if (!netSataus) {
	// Builder b = new AlertDialog.Builder(context).setTitle("没有可用的网络")
	// .setMessage("是否对网络进行设置？");
	// b.setPositiveButton("是", new DialogInterface.OnClickListener() {
	// public void onClick(DialogInterface dialog, int whichButton) {
	// context.startActivity(new Intent(
	// Settings.ACTION_WIRELESS_SETTINGS));
	// // 直接进入手机中的wifi网络设置界面 }
	// // context.startActivity(new
	// // Intent(Settings.ACTION_WIFI_SETTINGS));
	// }
	// }).setNeutralButton("否", new DialogInterface.OnClickListener() {
	// public void onClick(DialogInterface dialog, int whichButton) {
	// dialog.cancel();
	// }
	// }).show();
	// } else {
	// return false;
	// }
	// ;
	// return netSataus;
	// }
	/**
	 * 
	 * @param context
	 * @return 当前终端SIM卡信息
	 */
	public static String getSimType(Context context) {

		// 获得SIMType 　　
		String simType = "unknown";
		// 获得系统服务，从而取得sim数据 　　
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE); // 获得手机SIMType
		int type = tm.getNetworkType();
		// 判断类型值，并且命名 　
		if (type == TelephonyManager.NETWORK_TYPE_UMTS) {
			simType = "USIM";
			// 类型为UMTS定义为wcdma的USIM卡 　　
		} else if (type == TelephonyManager.NETWORK_TYPE_GPRS) {
			simType = "SIM";// 类型为GPRS定义为GPRS的SIM卡 　　
		} else if (type == TelephonyManager.NETWORK_TYPE_EDGE) {
			simType = "SIM";
			// 类型为EDGE定义为EDGE的SIM卡 　　
		} else {
			simType = "UIM";
			// 类型为unknown定义为cdma的UIM卡 　　
		}
		return simType;
	}

	/**
	 * 
	 * @param context
	 * @return 获取网络连接状态 true false
	 */
	public static boolean isConencting(Context context) {

		boolean isSuccess = false;
		ConnectivityManager connectionManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo nwInfo = connectionManager.getActiveNetworkInfo();
		if (null != nwInfo) {
			isSuccess = nwInfo.isConnectedOrConnecting();
		} else {
			isSuccess = false;
		}
		return isSuccess;
	}
}
