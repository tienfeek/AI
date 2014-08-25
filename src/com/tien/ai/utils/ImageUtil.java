package com.tien.ai.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.view.Display;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.WindowManager;
import android.widget.ImageView;

/**
 * @author Tienfook
 * @version 2011-8-19 下午04:12:29 图片处理工具
 */
public class ImageUtil {

	public static final int UNCONSTRAINED = -1;
	private static String sd_card = "/sdcard/";
	private static String path = "Fans/imageinfo_cache/";

	public static InputStream getRequest(String path) throws Exception {

		URL url = new URL(path);
		XLog.i("请求图片地址：" + url);
		// Long startTime = System.currentTimeMillis();
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setConnectTimeout(6000);
		InputStream in = null;
		if (conn.getResponseCode() == 200) {
			in = conn.getInputStream();
		}
		return in;
	}

	/**
	 * @Title: readInputStream
	 * @Description: 描述 从流中读取数据
	 * @author qiaosheling
	 * @date 2011-10-8
	 * @return byte[]
	 * @throws
	 * @parameters
	 */
	public static byte[] readInputStream(InputStream inStream) throws Exception {

		ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = inStream.read(buffer)) != -1) {
			outSteam.write(buffer, 0, len);
		}
		outSteam.close();
		inStream.close();
		return outSteam.toByteArray();
	}

	public static Bitmap getBitmapFromUrl(String url) throws Exception {

		byte[] bytes = getBytesFromUrl(url);
		return byteToBitmap(bytes);
	}

	public static byte[] getBytesFromUrl(String url) throws Exception {

		return readInputStream(getRequest(url));
	}

	public static Bitmap byteToBitmap(byte[] byteArray) {

		if (byteArray.length != 0) {
			return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
		} else {
			return null;
		}
	}

	public static Drawable byteToDrawable(byte[] byteArray) {

		ByteArrayInputStream ins = new ByteArrayInputStream(byteArray);
		return Drawable.createFromStream(ins, null);
	}

	public static byte[] Bitmap2Bytes(Bitmap bm) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		return baos.toByteArray();
	}

	/**
	 * 
	 * @Title: Bitmap2Stream
	 * @Description: Bitmap转化为输入流
	 * @author wangtf
	 * @date 2011-9-27
	 * @return InputStream
	 * @throws
	 * @parameters
	 */
	public static InputStream Bitmap2Stream(Bitmap bm) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.JPEG, 80, baos);
		InputStream in = new ByteArrayInputStream(baos.toByteArray());
		// Log.i("wangtf", "处理后的图片大小：" + baos.toByteArray().length);
		return in;
	}

	public static Bitmap drawableToBitmap(Drawable drawable) {

		Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
				drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
		drawable.draw(canvas);
		return bitmap;
	}

	/**
	 * 图片去色,返回灰度图片
	 * 
	 * @param bmpOriginal
	 *            传入的图片
	 * @return 去色后的图片
	 */
	public static Bitmap toGrayscale(Bitmap bmpOriginal) {

		int width, height;
		height = bmpOriginal.getHeight();
		width = bmpOriginal.getWidth();
		Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
		Canvas c = new Canvas(bmpGrayscale);
		Paint paint = new Paint();
		ColorMatrix cm = new ColorMatrix();
		cm.setSaturation(0);
		ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
		paint.setColorFilter(f);
		c.drawBitmap(bmpOriginal, 0, 0, paint);
		return bmpGrayscale;
	}

	/**
	 * 去色同时加圆角
	 * 
	 * @param bmpOriginal
	 *            原图
	 * @param pixels
	 *            圆角弧度
	 * @return 修改后的图片
	 */
	public static Bitmap toGrayscale(Bitmap bmpOriginal, int pixels) {

		return toRoundCorner(toGrayscale(bmpOriginal), pixels);
	}

	/**
	 * 把图片变成圆角
	 * 
	 * @param bitmap
	 *            需要修改的图片
	 * @param pixels
	 *            圆角的弧度
	 * @return 圆角图片
	 */
	public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {

		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final Paint paint = new Paint();
		paint.setAntiAlias(true);
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		canvas.drawRoundRect(rectF, pixels, pixels, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	/**
	 * 使圆角功能支持BitampDrawable
	 * 
	 * @param bitmapDrawable
	 * @param pixels
	 * @return
	 */
	public static BitmapDrawable toRoundCorner(BitmapDrawable bitmapDrawable, int pixels) {

		Bitmap bitmap = bitmapDrawable.getBitmap();
		bitmapDrawable = new BitmapDrawable(toRoundCorner(bitmap, pixels));
		return bitmapDrawable;
	}

	/**
	 * 放大缩小图片
	 * 
	 * @param bitmap
	 * @param w
	 * @param h
	 * @return
	 */
	public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {

		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Matrix matrix = new Matrix();
		float scaleWidht = ((float) w / width);
		float scaleHeight = ((float) h / height);
		matrix.postScale(scaleWidht, scaleHeight);
		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
		return newbmp;
	}

	/**
	 * 获得带倒影的图片方法
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Bitmap createReflectionImageWithOrigin(Bitmap bitmap) {

		final int reflectionGap = 4;
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);
		Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, height / 2, width, height / 2, matrix, false);
		Bitmap bitmapWithReflection = Bitmap.createBitmap(width, (height + height / 2), Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmapWithReflection);
		canvas.drawBitmap(bitmap, 0, 0, null);
		Paint deafalutPaint = new Paint();
		canvas.drawRect(0, height, width, height + reflectionGap, deafalutPaint);
		canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);
		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0, bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff,
				0x00ffffff, TileMode.CLAMP);
		paint.setShader(shader);
		// Set the Transfer mode to be porter duff and destination in
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		// Draw a rectangle using the paint with our linear gradient
		canvas.drawRect(0, height, width, bitmapWithReflection.getHeight() + reflectionGap, paint);
		return bitmapWithReflection;
	}

	
	//质量压缩
	public static  Bitmap compressImage(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length / 2048 > 100) { // 循环判断如果压缩后图片是否大于200kb,大于继续压缩
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;// 每次都减少10
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		return bitmap;
	}

	/**
	 * 
	 * @Title: getBitmapByPath
	 * @Description: 从文件中获取图片，并转化指定分辨率的图片
	 * @author wangtf
	 * @date 2011-9-25
	 * @return Bitmap
	 * @throws
	 * @parameters
	 */
	public static Bitmap getBitmapByPath(String path, Options options, int width, int height) throws FileNotFoundException {

		File file = new File(path);
		if (!file.exists()) {
			throw new FileNotFoundException();
		}
		FileInputStream in = null;
		in = new FileInputStream(file);
		if (options != null) {
			Rect r = new Rect(0, 0, width, height);
			int w = r.width();
			int h = r.height();
			int maxSize = w > h ? w : h;
			int inSimpleSize = computeSampleSize(options, maxSize, w * h);
			options.inSampleSize = inSimpleSize; // 设置缩放比例
			options.inJustDecodeBounds = false;
		}
		Bitmap mBitmap = BitmapFactory.decodeStream(in, null, options);
		try {
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return mBitmap;
	}

	/**
	 * 
	 * @Title: delPicSuffix
	 * @Description: 删除图片后缀图片
	 * @author wangtf
	 * @date 2011-10-12
	 * @return String
	 * @throws
	 * @parameters
	 */
	public static String delPicSuffix(String imageName) {

		int poc = imageName.lastIndexOf(".");
		if (poc > 0) {
			imageName = imageName.substring(0, poc);
		}
		return imageName;
	}

//	public static Bitmap readMemory(String imageName, String url) {
//		// 1.从内存中读取
//		LruBitmapCache mLruBitmapCache = Configuration.getConfiguration().getLruBitmapCache();
//
//		Bitmap bmp = mLruBitmapCache.get(url);
//		// XLog.i("wanges", mLruBitmapCache.toString());
//		return bmp;
//	}
//
//	public static Bitmap readDisc(String imageName, String url, int imageType) {
//		Bitmap mBitmap = null;
//		// 2.从文件中读取
//		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
//			String path = Environment.getExternalStorageDirectory() + Globle.APPLICATION_DIR + Globle.SD_CARD_CACHE_IMAGE_DIR + "/";
//			FileUtil.createDir(path);
//			mBitmap = BitmapFactory.decodeFile(path + imageName);
//			if (mBitmap != null) {
//				// 图片缓存到LRUCache
//				mBitmap = cacheBitmap2Memory(mBitmap, url, imageType);
//				// 修改文件的最后修改时间
//				FileUtil.updateFileTime(path, imageName);
//			}
//		}
//		return mBitmap;
//	}

	/**
	 * 
	 * @Title: pullBitmap
	 * @Description: 读取图片
	 * @author wangtf
	 * @date 2011-10-12
	 * @return Bitmap
	 * @throws
	 * @parameters imageName图片名 如ewave.JPEG
	 */
//	public static Bitmap readBitmap(String imageName, String url) {
//
//		Bitmap mBitmap = null;
//		// // 1.从内存中读取
//		LruBitmapCache mLruBitmapCache = Configuration.getConfiguration().getLruBitmapCache();
//		mBitmap = mLruBitmapCache.get(url);
//		if (mBitmap != null) {
//			return mBitmap;
//		}
//
//		// 2.从文件中读取
//		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
//			String path = Environment.getExternalStorageDirectory() + Globle.APPLICATION_DIR + Globle.SD_CARD_CACHE_IMAGE_DIR + "/";
//			FileUtil.createDir(path);
//			mBitmap = BitmapFactory.decodeFile(path + imageName);
//			// 修改文件的最后修改时间
//			if (mBitmap != null) {
//				FileUtil.updateFileTime(path, imageName);
//				Configuration.getConfiguration().getLruBitmapCache().put(url, mBitmap);
//			}
//		}
//
//		return mBitmap;
//	}

	/**
	 * @Title: cacheBitmap
	 * @Description: 缓存图片
	 * @author wangtf
	 * @date 2011-10-12
	 * @return boolean
	 * @throws
	 * @parameters imageName图片名 如ewave.JPEG
	 */
//	public static void cacheBitmap(Bitmap bmp, String url, String imageName) throws IOException {
//
//		if (bmp == null) {
//			return;
//		}
//		// 1.缓存到内存
//		cacheBitmap2Memory(bmp, url, 0);
//		// 2.缓存到文件
//		cacheBitmap2Disc(bmp, url, imageName);
//	}
//
//	public static void cacheBitmap(Bitmap bmp, String url, String imageName, int imageType) throws IOException {
//
//		if (bmp == null) {
//			return;
//		}
//		// 1.缓存到内存
//		cacheBitmap2Memory(bmp, url, imageType);
//		// 2.缓存到文件
//		cacheBitmap2Disc(bmp, url, imageName);
//	}

//	public static Bitmap cacheBitmap2Memory(Bitmap bmp, String url, int imageType) {
//		if (bmp == null) {
//			return bmp;
//		}
//		// 头像图片需要圆角
//		// if (imageType == Globle.HEAD_PORTRAIT || imageType ==
//		// Globle.SMALL_HEAD_PORTRAIT) {
//		bmp = ImageUtil.toRoundCorner(bmp, 2);
//		// }
//		Configuration.getConfiguration().getLruBitmapCache().put(url, bmp);
//		return bmp;
//	}
//
//	public static void cacheBitmap2Disc(Bitmap bmp, String url, String imageName) {
//		if (bmp == null) {
//			return;
//		}
//		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
//			String path = Environment.getExternalStorageDirectory() + Globle.APPLICATION_DIR + Globle.SD_CARD_CACHE_IMAGE_DIR + "/";
//			// 缓存清除策略
//			new FileUtil().removeCache(path);
//			FileUtil.createDir(path);
//			saveBitmap(bmp, path + imageName);
//		}
//	}

	/**
	 * 
	 * @Title: obtainNetworkImage
	 * @Description: 获取网络图片
	 * @author wangtf
	 * @date 2011-10-12
	 * @return Bitmap
	 * @throws
	 * @parameters
	 */
//	public static Bitmap obtainNetworkImage(String imageName, String url) {
//
//		return obtainNetworkImage(imageName, url, false, 0);
//	}
//
//	public static Bitmap obtainNetworkImage(String imageName, String url, boolean cache) {
//
//		return obtainNetworkImage(imageName, url, cache, 0);
//	}

	/**
	 * 
	 * @Title: obtainNetworkImage
	 * @Description: 获取网络图片
	 * @author wangtf
	 * @date 2011-10-12
	 * @return Bitmap
	 * @throws
	 * @parameters
	 */
//	public static Bitmap obtainNetworkImage(String imageName, String url, boolean cache, int imageType) {
//
//		// .从网络读取
//		Bitmap bitmap = null;
//		try {
//			InputStream in = ImageUtil.getRequest(url);
//			if (in != null) {
//				byte[] byteArray = getBytes(in);
//				bitmap = byteToBitmap(byteArray);
//				// 缓存下载的图片
//				if (bitmap != null && cache) {
//					// 1.缓存到文件
//					cacheBitmap2Disc(bitmap, url, imageName);
//					// 2.缓存到内存LRUCache
//					bitmap = cacheBitmap2Memory(bitmap, url, imageType);
//				}
//			}
//		} catch (MalformedURLException e) {
//			// TODO: handle exception
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//		}
//		return bitmap;
//	}

	/**
	 * 
	 * @Title: saveBitmap
	 * @Description: 将Bitmap保存到本地
	 * @author wangtf
	 * @date 2011-9-25
	 * @return boolean
	 * @throws
	 * @parameters
	 */
	public static boolean saveBitmap(Bitmap bitmap, String path) {

		File file = new File(path);
		FileOutputStream out;
		boolean result = false;
		try {
			out = new FileOutputStream(file);
			result = bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
			if (result) {
				out.flush();
				out.close();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * @Title: getBytes
	 * @Description: 输入流转字节数组
	 * @author wangtf
	 * @date 2011-10-21
	 * @return byte[]
	 * @throws
	 * @parameters
	 */
	public static byte[] getBytes(InputStream in) throws IOException {

		// Log.i("wangtf", "InputStream:"+in.toString());
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = in.read(buffer)) != -1) {
			baos.write(buffer, 0, len);
			// baos.flush();
		}
		byte[] bytes = baos.toByteArray();
		// Log.i("wangtf", "baos:"+baos.toString());
		baos.close();
		in.close();
		return bytes;
	}

	/**
	 * 
	 * @Description:
	 * @param bitmap
	 * @param pixels
	 * @param mImageView
	 * @see:
	 * @since:
	 * @author:Tienfook Chang
	 * @date:2012-12-28
	 */
	public static void setRoundCornerImageView(Bitmap bitmap, ImageView mImageView) {
		mImageView.setImageBitmap(toRoundCorner(zoomBitmap(bitmap, 36, 36), 5));
	}

	public static Bitmap getPicFromBytes(byte[] bytes, BitmapFactory.Options opts) {
		if (bytes != null)
			if (opts != null)
				return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
			else
				return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
		return null;
	}

	/**
	 * 
	 * @Description:isGif
	 * @param fileNameString
	 * @return
	 * @see:
	 * @since:
	 * @author:Tienfook Chang
	 * @date:Jul 11, 2012
	 */
	public static boolean isGif(String fileNameString) {
		try {
			String[] strarray = fileNameString.split("\\.");
			if (strarray[1].equals("gif") || strarray[1].equals("GIF")) {
				return true;
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}

	public static boolean isImage(String fileNameString) {
		try {
			String[] strarray = fileNameString.split("\\.");
			String strarrayStr = strarray[1].toLowerCase();
			if (strarrayStr.equals("gif") || strarrayStr.equals("png") || strarrayStr.equals("jpg") || strarrayStr.equals("png")
					|| strarrayStr.equals("jpeg")) {
				return true;
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}

	/**
	 * 
	 * @Title: getBitmapByPath
	 * @Description: 从文件中获取图片，并转化指定分辨率的图片
	 * @author wangtf
	 * @date 2011-9-25
	 * @return Bitmap
	 * @throws
	 * @parameters
	 */
	public static Bitmap getBitmapByPath(String name) throws FileNotFoundException {
		String picName = mixHashStr(name);

		File file = new File(sd_card + path + picName);
		if (!file.exists()) {
			throw new FileNotFoundException();
		}
		FileInputStream in = null;
		in = new FileInputStream(file);
		Bitmap mBitmap = BitmapFactory.decodeStream(in);
		try {
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return mBitmap;
	}

	public static String mixHashStr(String str) {
		return Long.toHexString(mixHash(str));
	}

	public static long mixHash(String str) {
		long hash = str.hashCode();
		hash <<= 32;
		hash |= FNVHash1(str);
		return hash;
	}

	public static int FNVHash1(String data) {
		final int p = 16777619;
		int hash = (int) 2166136261L;
		for (int i = 0; i < data.length(); i++)
			hash = (hash ^ data.charAt(i)) * p;
		hash += hash << 13;
		hash ^= hash >> 7;
		hash += hash << 3;
		hash ^= hash >> 17;
		hash += hash << 5;
		return hash;
	}

	/**
	 * 
	 * @Title: computeSampleSize
	 * @Description: 获取需要进行缩放的比例，即options.inSampleSize
	 * @author wangtf
	 * @date 2011-9-25
	 * @return int
	 * @throws
	 * @parameters
	 */
	public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {

		int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}
		return roundedSize;
	}

	/**
	 * 
	 * @Title: computeInitialSampleSize
	 * @Description: 计算宽高返回缩放比例
	 * @author wangtf
	 * @date 2011-9-25
	 * @return int
	 * @throws
	 * @parameters
	 */
	private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {

		double w = options.outWidth;
		double h = options.outHeight;
		int lowerBound = (maxNumOfPixels == UNCONSTRAINED) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == UNCONSTRAINED) ? 128 : (int) Math.min(Math.floor(w / minSideLength),
				Math.floor(h / minSideLength));
		if (upperBound < lowerBound) {
			return lowerBound;
		}
		if ((maxNumOfPixels == UNCONSTRAINED) && (minSideLength == UNCONSTRAINED)) {
			return 1;
		} else if (minSideLength == UNCONSTRAINED) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}

	/**
	 * 
	 * @Title: getOptions
	 * @Description: 获得图片设置信息
	 * @author wangtf
	 * @date 2011-9-25
	 * @return Options
	 * @throws
	 * @parameters
	 */
	public static Options getOptions(String path) {

		Options options = new Options();
		options.inJustDecodeBounds = true;// 只描边，不读取数据
		BitmapFactory.decodeFile(path, options);
		return options;
	}

	/**
	 * 转换图片成圆形
	 * 
	 * @param bitmap
	 *            传入Bitmap对象
	 * @return
	 */
	public static Bitmap toRoundBitmap(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float roundPx;
		float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
		if (width <= height) {
			roundPx = width / 2;
			top = 0;
			bottom = width;
			left = 0;
			right = width;
			height = width;
			dst_left = 0;
			dst_top = 0;
			dst_right = width;
			dst_bottom = width;
		} else {
			roundPx = height / 2;
			float clip = (width - height) / 2;
			left = clip;
			right = width - clip;
			top = 0;
			bottom = height;
			width = height;
			dst_left = 0;
			dst_top = 0;
			dst_right = height;
			dst_bottom = height;
		}

		Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect src = new Rect((int) left, (int) top, (int) right, (int) bottom);
		final Rect dst = new Rect((int) dst_left, (int) dst_top, (int) dst_right, (int) dst_bottom);
		final RectF rectF = new RectF(dst);

		paint.setAntiAlias(true);

		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, src, dst, paint);
		return output;
	}

	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = bitmap.getWidth() / 2;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}

	public static Bitmap getCircleBitmap(Bitmap bitmap) {
		int x = bitmap.getWidth();
		Bitmap output = Bitmap.createBitmap(x, x, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		// 根据原来图片大小画一个矩形
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		paint.setAntiAlias(true);
		paint.setColor(color);
		// 画出一个圆
		canvas.drawCircle(x / 2, x / 2, x / 2, paint);
		// canvas.translate(-25, -6);
		// 取两层绘制交集,显示上层
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		// 将图片画上去
		canvas.drawBitmap(bitmap, rect, rect, paint);
		// 返回Bitmap对象
		return output;
	}

	/**
	 * 获取和保存当前屏幕的截图
	 */
	public static boolean saveCurrentScreenShot(String picPath, String picFilename, Activity mActivity) {
		// 1.构建Bitmap
		WindowManager windowManager = mActivity.getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		int w = display.getWidth();
		int h = display.getHeight();
		Bitmap Bmp = Bitmap.createBitmap(w, h, Config.ARGB_8888);
		// 2.获取屏幕
		View decorview = mActivity.getWindow().getDecorView();
		decorview
				.measure(MeasureSpec.makeMeasureSpec(w - 2, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(h - 10, MeasureSpec.EXACTLY));
		decorview.layout(0, 0, decorview.getMeasuredWidth(), decorview.getMeasuredHeight());
		decorview.setDrawingCacheEnabled(true);
		Bmp = decorview.getDrawingCache();
		// 3.保存Bitmap
		try {
			File path = new File(picPath);
			// 文件
			String filepath = picPath + picFilename;
			File file = new File(filepath);
			if (!path.exists()) {
				path.mkdirs();
			}
			if (!file.exists()) {
				file.createNewFile();
			}
			FileOutputStream fos = null;
			fos = new FileOutputStream(file);
			if (null != fos) {
				Bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
				fos.flush();
				fos.close();
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public Bitmap getRoundedShape(Bitmap scaleBitmapImage) {
		  // TODO Auto-generated method stub
		  int targetWidth = 50;
		  int targetHeight = 50;
		  Bitmap targetBitmap = Bitmap.createBitmap(targetWidth, 

		                            targetHeight,Bitmap.Config.ARGB_8888);
		  

		                Canvas canvas = new Canvas(targetBitmap);
		  Path path = new Path();
		  path.addCircle(((float) targetWidth - 1) / 2,
		  ((float) targetHeight - 1) / 2,
		  (Math.min(((float) targetWidth), 
		                ((float) targetHeight)) / 2),
		          Path.Direction.CCW);
		                canvas.clipPath(path);
		  Bitmap sourceBitmap = scaleBitmapImage;
		  canvas.drawBitmap(sourceBitmap, 
		                                new Rect(0, 0, sourceBitmap.getWidth(),
		    sourceBitmap.getHeight()), 
		                                new Rect(0, 0, targetWidth,
		    targetHeight), null);
		  return targetBitmap;
		 }


}
