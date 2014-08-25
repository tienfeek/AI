package com.tien.ai.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.os.Environment;
import android.util.Log;
import android.xutil.Util;


/**
 * @author 
 * 日志
 * Logcat日志级别: 0:禁用日志, 1:启用(所有日志),  2:verbose, 3: debug, 4:info, 5:warn, 6:error, 7:assert
 * 文件日志输出: 0:禁用日志, 1:启用(所有日志),  2:verbose, 3: debug, 4:info, 5:warn, 6:error, 7:assert
 */
public class XLog {

	private static final String TAG = "contacts";
	private static int LOGCAT_PRIORITY = 1;
	//@SuppressWarnings("unused")
	private static int LOGFILE_PRIORITY = 1;
	static {
		try {
//			#ifdef LOGCAT_LEVEL
//			#expand LOGCAT_PRIORITY=Integer.valueOf(%LOGCAT_LEVEL%);
//			#endif
//			#ifdef LOGFILE_LEVEL
//			#expand LOGFILE_PRIORITY=Integer.valueOf(%LOGFILE_LEVEL%);
//			#endif
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 设置logcat的log级别, 见android.util.Log.VERBOSE等
	 * @param level 值是android.util.Log.VERBOSE/DEBUG/INFO/WARN等, 0表示全部禁用, 1表示全部启用
	 */
	public static void setLogLevel(int level) {

		LOGCAT_PRIORITY = level;
	}

	private static boolean enableLogcat(int level) {

		return LOGCAT_PRIORITY != 0 && level >= LOGCAT_PRIORITY;
	}

	@SuppressWarnings("unused")
	private static boolean enableLogfile(int level) {

		return LOGFILE_PRIORITY != 0 && level >= LOGFILE_PRIORITY;
		//		return false;
	}

	public static void v(String msg) {

		println_native(Log.VERBOSE, TAG, msg);
	}

	public static void v(String msg, Throwable tr) {

		println_native(Log.VERBOSE, TAG, msg + '\n' + getStackTraceString(tr));
	}

	public static void v(String tag, String msg) {

		println_native(Log.VERBOSE, tag, msg);
	}

	public static void d(String msg) {

		println_native(Log.DEBUG, TAG, msg);
	}

	public static void d(String msg, Throwable tr) {

		println_native(Log.DEBUG, TAG, msg + '\n' + getStackTraceString(tr));
	}

	public static void d(Throwable tr) {

		println_native(Log.DEBUG, TAG, getStackTraceString(tr));
	}

	public static void d(String tag, String msg) {

		//d(msg1 + ": " + msg2);
		println_native(Log.DEBUG, tag, msg);
	}

	public static void i(String msg) {

		println_native(Log.INFO, TAG, msg);
	}

	public static void i(String msg, Throwable tr) {

		println_native(Log.INFO, TAG, msg + '\n' + getStackTraceString(tr));
	}

	public static void i(String tag, String msg) {

		//i(msg1 + ": " + msg2);
		println_native(Log.INFO, tag, msg);
	}

	public static void w(String msg) {

		println_native(Log.WARN, TAG, msg);
	}

	public static void w(String msg, Throwable tr) {

		println_native(Log.WARN, TAG, msg + '\n' + getStackTraceString(tr));
	}

	public static void w(String tag, String msg) {

		//w(msg1 + ": " + msg2);
		println_native(Log.WARN, tag, msg);
	}

	public static void w(Throwable tr) {

		println_native(Log.WARN, TAG, getStackTraceString(tr));
	}

	public static void e(String msg) {

		println_native(Log.ERROR, TAG, msg);
	}

	public static void e(String msg, Throwable tr) {

		println_native(Log.ERROR, TAG, msg + '\n' + getStackTraceString(tr));
	}

	public static void e(Throwable tr) {

		e(TAG, tr);
	}

	public static void e(String tag, String msg) {

		//e(msg1 + ": " + msg2);
		println_native(Log.ERROR, tag, msg);
	}

	private static String getStackTraceString(Throwable tr) {

		if (tr == null) {
			return "";
		}
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		tr.printStackTrace(pw);
		return sw.toString();
	}

	private static void println_native(int priority, String tag, String msg) {

		if (enableLogcat(priority)) {
			Log.println(priority, tag, msg);
		}
				if (enableLogfile(priority)) {
					println_file(priority, tag, msg);
				}
	}

	private static void println_file(int priority, String tag, String msg) {

		long threadID = Thread.currentThread().getId();
		StringBuffer sb = new StringBuffer(256);
		//Calendar c = Calendar.getInstance();//Util.getCurrentCalendar();
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sb.append(format.format(date));
		sb.append(" ");
		sb.append(" [");
		sb.append(threadID);
		sb.append("] [");
		sb.append(priority);
		sb.append("] [");
		sb.append(tag);
		sb.append("]  ");
		sb.append(msg);
		sb.append("\n");
		putCache(sb.toString());
	}

	private static void putCache(String s) {

		synchronized (cacheBuffer) {
			cacheBuffer.append(s);
			if (cacheBuffer.length() > 8192) {
				FileService mFileService = new FileService();
				try {
					mFileService.saveToSDCard(Environment.getExternalStorageDirectory() + "/ai" + "/" + "Log.txt", cacheBuffer.toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
				cacheBuffer.setLength(0);
			}
		}
	}

//		public static void flush() {
//			if (LOGFILE_PRIORITY != 0) {
//				if(cacheBuffer.length()==0) {
//					return;
//				}
//				synchronized (cacheBuffer) {
//					FileOperator.write(getLogFileName(), cacheBuffer.toString());
//					cacheBuffer.setLength(0);
//				}
//			}
//		}
	private static StringBuffer cacheBuffer = new StringBuffer(8192 + 1024);
	private static String logFileName = null;

//	public static String getLogFileName() {
//
//				if (logFileName == null) {
//					Calendar c = Calendar.getInstance();
//					logFileName = "eq_" + Util.getDate(c, "-") + "_" + Util.getTime(c, "-") + ".log";
//				}
//		return logFileName;
//	}
}
