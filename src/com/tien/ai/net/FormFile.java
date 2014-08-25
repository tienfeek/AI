package com.tien.ai.net;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory.Options;

import com.tien.ai.utils.ImageUtil;


/**
 * 
 * @Description:
 * @author:wangtf
 * @see:
 * @since:
 * @copyright © 35.com
 * @Date:2012-4-18
 */
public class FormFile {

	// 上传文件的数据
	private byte[] data;
	private InputStream inStream;
	private File file;
	// 文件名称
	private String filname;
	// 文件类型：头像 微薄图片 等
	private String fileType;
	// 请求参数名称
	private String parameterName;
	// 内容类型
	private String contentType = "application/octet-stream";

	/**
	 * 上传小文件，把文件数据先读入内存
	 * 
	 * @param filname
	 * @param data
	 * @param parameterName
	 * @param contentType
	 */
	public FormFile(String filname, byte[] data, String parameterName, String contentType) {
		this.data = data;
		this.filname = filname;
		this.parameterName = parameterName;
		if (contentType != null)
			this.contentType = contentType;
	}

	/**
	 * 上传大文件，一边读文件数据一边上传
	 * 
	 * @param filname
	 * @param file
	 * @param parameterName
	 * @param contentType
	 */
	public FormFile(String filname, File file, String parameterName, String contentType) {
		this.filname = filname;
		this.parameterName = parameterName;
		this.file = file;
		try {
			this.inStream = new FileInputStream(file);
			this.data = readStream(inStream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (contentType != null)
			this.contentType = contentType;
	}

	/**
	 * 
	 * @Title:
	 * @Description:
	 * @param @param filname
	 * @param @param in
	 * @param @param parameterName
	 * @param @param contentType
	 */

	public FormFile(String filname, InputStream in, String parameterName, String contentType, String fileType) {
		this.filname = filname;
		this.parameterName = parameterName;
		this.inStream = in;
		this.fileType = fileType;
		try {
			this.data = readStream(inStream);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (contentType != null)
			this.contentType = contentType;
	}

	public File getFile() {
		return file;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public InputStream getInStream() {
		return inStream;
	}

	public byte[] getData() {
		return data;
	}

	public String getFilname() {
		return filname;
	}

	public void setFilname(String filname) {
		this.filname = filname;
	}

	public String getParameterName() {
		return parameterName;
	}

	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	/**
	 * 读取流
	 * 
	 * @param inStream
	 * @return 字节数组
	 * @throws Exception
	 */
	public byte[] readStream(InputStream inStream) throws Exception {
		ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = -1;

		while ((len = inStream.read(buffer)) != -1) {
			outSteam.write(buffer, 0, len);
		}

		outSteam.close();
		inStream.close();
		return outSteam.toByteArray();
	}

	public static FormFile buildFormFile(String picPath, int maxWidth, int maxHeight, String fileType) throws FileNotFoundException {
		FormFile mFormFile = null;
		if (!"".equals(picPath)) {
			InputStream mBitmapIn = null;
			File file = new File(picPath);
			// 缩放图片
			Options options = ImageUtil.getOptions(picPath);
//			if (options.outHeight > maxHeight || options.outWidth > maxWidth) {
				Bitmap tempBitmap = ImageUtil.getBitmapByPath(picPath, options, maxWidth, maxHeight);
				mBitmapIn = ImageUtil.Bitmap2Stream(tempBitmap);
//			} else {
				//mBitmapIn = new FileInputStream(file);
//			}
			mFormFile = new FormFile(file.getName(), mBitmapIn, "image/jpeg", "image/jpeg", fileType);
			//mFormFile = new FormFile("upload_image.jpg", mBitmapIn, "image/jpeg", "image/jpeg", fileType);
		}
		return mFormFile;
	}
}
