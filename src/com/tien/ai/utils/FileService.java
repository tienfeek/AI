package com.tien.ai.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import android.content.Context;
import android.os.Environment;

import com.tien.ai.R;

/**
 * @author Tienfook
 * @version 2011-8-19 下午04:10:12 将String 存储到文件
 */
public class FileService {

	private Context context;

	public FileService() {

	}

	public FileService(Context context) {

		this.context = context;
	}

	/**
	 * 
	 * @Title: createCache
	 * @Description: 创建CACHE文件
	 * @author Tienfook
	 * @date 2011-9-7
	 * @return void
	 * @throws
	 * @parameters
	 */
	public void createCache(String filename, String content) throws Exception {

		File file = new File(context.getCacheDir(), filename + FileCacheDir.CACHE_EXT_NAME);
		// if(!file.exists()){
		// file.mkdirs();
		// }
		FileOutputStream outStream = new FileOutputStream(file);
		outStream.write(content.getBytes());
		outStream.close();
	}

	/**
	 * 
	 * @Title: getCacheContent
	 * @Description: 读取CACHE文件
	 * @author Tienfook
	 * @date 2011-9-7
	 * @return String
	 * @throws
	 * @parameters
	 */
	public String getCacheContent(String filename) throws Exception {

		File file = new File(context.getCacheDir() + "/" + filename + FileCacheDir.CACHE_EXT_NAME);

		FileInputStream inStream = new FileInputStream(file);
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		byte[] data = outStream.toByteArray();
		inStream.close();
		outStream.close();
		return new String(data);

	}

	/**
	 * 
	 * @Title: getCacheContent
	 * @Description: 读取CACHE文件
	 * @author Tienfook
	 * @date 2011-9-7
	 * @return String
	 * @throws
	 * @parameters
	 */
	public String getHomeCacheContent(String filename) throws Exception {

		File file = new File(context.getCacheDir() + "/" + filename + FileCacheDir.CACHE_EXT_NAME);
		if (file.exists()) {
			FileInputStream inStream = new FileInputStream(file);
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = inStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, len);
			}
			byte[] data = outStream.toByteArray();
			inStream.close();
			outStream.close();
			return new String(data);
		} else {
			return new String("all");
		}
	}

	/**
	 * 
	 * @Title: sdCardStatus
	 * @Description: 判断SD卡是否可用
	 * @author Tienfook
	 * @date 2011-9-7
	 * @return boolean true 可用
	 * @throws
	 * @parameters
	 */
	public boolean sdCardStatus() {

		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))// SD
		{
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @Title: saveToSDCard
	 * @Description: 保存文件到SDCARD
	 * @author Tienfook
	 * @date 2011-9-7
	 * @return void
	 * @throws
	 * @parameters
	 */
	public void saveToSDCard(String filename, String content) throws Exception {

		if (sdCardStatus()) {
			File file = new File(Environment.getExternalStorageDirectory(), filename);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileOutputStream outStream = new FileOutputStream(file);
			outStream.write(content.getBytes());
			outStream.close();
		}
	}

	public void save(String filename, String content) throws Exception {

		FileOutputStream outStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
		outStream.write(content.getBytes());
		outStream.close();
	}

	public void saveAppend(String filename, String content) throws Exception {// ctrl+shift+y

		FileOutputStream outStream = context.openFileOutput(filename, Context.MODE_APPEND);
		outStream.write(content.getBytes());
		outStream.close();
	}

	public void saveReadable(String filename, String content) throws Exception {

		FileOutputStream outStream = context.openFileOutput(filename, Context.MODE_WORLD_READABLE);
		outStream.write(content.getBytes());
		outStream.close();
	}

	public void saveWriteable(String filename, String content) throws Exception {

		FileOutputStream outStream = context.openFileOutput(filename, Context.MODE_WORLD_WRITEABLE);
		outStream.write(content.getBytes());
		outStream.close();
	}

	public void saveRW(String filename, String content) throws Exception {

		FileOutputStream outStream = context.openFileOutput(filename, Context.MODE_WORLD_WRITEABLE + Context.MODE_WORLD_READABLE);
		outStream.write(content.getBytes());
		outStream.close();
	}

	/**
	 * 
	 * @Title: getContent
	 * @Description: 读取/data/data/package-name/files 下文件
	 * @author Tienfook
	 * @date 2011-9-7
	 * @return String
	 * @throws
	 * @parameters
	 */
	public String getContent(String filename) throws Exception {

		FileInputStream inStream = context.openFileInput(filename);
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		byte[] data = outStream.toByteArray();
		inStream.close();
		outStream.close();
		return new String(data);
	}
}
