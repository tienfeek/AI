package com.tien.ai.utils;

import android.os.Environment;

/**
 * @author Tienfook
 * @version 2011-8-23 上午11:11:22 缓存文件存放路径
 */
public class FileCacheDir {

	public static final String SDCARD_DIR = Environment.getExternalStorageDirectory().toString();
	public static final String NO_SDCARD_DIR = Environment.getDataDirectory() + "/data/";
	public static final String CACHE_EXT_NAME = ".txt";
	public static final String CACHE = "data/data/ewave/cache/";
	public static final String DATABASES = "data/data/ewave/databases/";
	public static final String FILES = "data/data/ewave/files/";
	public static final String LIB = "data/data/ewave/lib/";
	public static final String SHARED_PREFS = "data/data/ewave/shared_prefs/";

	public static final String PORTRAIT = "mnt/sdcard/ewave/portrait/";
	public static final String PRE = "mnt/sdcard/ewave/pre/";

	// 用户信息
	// public static final String USER_INFO_FOUCS_CACHE = "user_info_foucs_cahce";
	// public static final String USER_INFO_FANS_CACHE = "user_info_fans_cahce";
	// public static final String USER_RECENT_AT_EUSER_LIST = "user_recent_at_euser_list.data";

}
