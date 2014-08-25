package com.tien.ai.net;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import android.content.Context;
import android.content.res.Configuration;

import com.tien.ai.Constant;
import com.tien.ai.utils.XLog;

/**
 * @Description:ProtocolClient
 * @author:Tienfook Chang
 * @see:
 * @since:
 * @copyright © 35.com
 * @Date:2012-9-19
 */
public class ProtocolClient {

	/**
	 * 
	 * @Description:post with multi params include encode.
	 * @param path
	 * @param params
	 * @param encode
	 * @return HttpResult
	 * @see:
	 * @since:
	 * @author:Tienfook Chang
	 * @date:2012-9-18
	 */
	public static HttpResult post(TreeMap<String, String> params, String encode) throws Exception {

		HttpResult httpResult = new HttpResult();
		StringBuilder parambuilder = new StringBuilder("");

		if (params != null) {
			for (Map.Entry<String, String> entry : params.entrySet()) {
				parambuilder.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), encode)).append("&");
			}
		}

		// signature
//		String signature = MD5.signature(params, encode);
//		parambuilder.append("sig=").append(signature);
//		XLog.i("wangtf", Globle.ACCESS_URL + parambuilder.toString());
//		// byte[] data = parambuilder.toString().getBytes();
		URL url = new URL( parambuilder.toString());
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);// Allow Do Output Request Params.
			conn.setUseCaches(false);// Use Caches.
			conn.setConnectTimeout(15 * 1000);
			conn.setReadTimeout(15 * 1000);
			conn.setRequestMethod("POST");
			// Http Request Head.
			conn.setRequestProperty(
					"Accept",
					"image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
			conn.setRequestProperty("Accept-Language", "zh-CN");
			conn.setRequestProperty(
					"User-Agent",
					"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			// conn.setRequestProperty("Content-Length",
			// String.valueOf(data.length));
			conn.setRequestProperty("Connection", "Keep-Alive");
//			Configuration mConfiguration = Configuration.getConfiguration();
//			conn.setRequestProperty("Cookie", "cu=" + mConfiguration.getUser().getCu() + ";cs=" + mConfiguration.getUser().getCs() + ";");
			// Sent Params.
			// DataOutputStream outStream = new
			// DataOutputStream(conn.getOutputStream());
			// outStream.write(data);
			// outStream.flush();
			// outStream.close();
			XLog.d("code:" + conn.getResponseCode());
			if (conn.getResponseCode() == 200) {
				httpResult.setStatus(true);
				httpResult.setCode(200);
				httpResult.setInputstream(conn.getInputStream());
			} else {
				InputStream mInputStream = conn.getInputStream();
				byte bytes[] = readStream(mInputStream);
				String json = new String(bytes);
				XLog.d(json);
				// 屏蔽json返回带有xml
				if (json.length() > 0) {
					json = json.substring(json.indexOf("{"));
				}
				httpResult.setStatus(false);
				httpResult.setCode(conn.getResponseCode());
			}
		} catch (Exception e) {
			XLog.i(e == null ? "null" : e.toString());
			httpResult.setStatus(false);
			if (e instanceof ConnectException || e instanceof SocketTimeoutException) {
				httpResult.setCode(HttpResult.TIME_OUT);
				XLog.i("SocketTimeoutException");
			} else if (e instanceof UnknownHostException) {
				httpResult.setCode(HttpResult.UBKNOWN_HOST);
			} else if (e instanceof SocketException) {
				httpResult.setCode(HttpResult.NETWORK_UNAVAILABLE);
			} else if (e instanceof FileNotFoundException) {
				httpResult.setCode(HttpResult.FILE_NOT_FOUND_FAILURE);
			} else {
				httpResult.setCode(HttpResult.NETWORK_REQUEST_FAILURE);
			}
		}
		return httpResult;
	}

	

	

	/**
	 * 
	 * @Description:post with multi params include context.
	 * @param mContext
	 * @param path
	 * @param params
	 * @return
	 * @see:
	 * @since:
	 * @author:Tienfook Chang
	 * @date:2012-9-19
	 */
	public static HttpResult postFormData(Context mContext, TreeMap<String, String> params) {

		HttpResult mHttpResult = null;
		if (mContext == null || NetWorkInfoUtil.isConencting(mContext)) {
			try {
				mHttpResult = post(params, "UTF-8");
				if (mHttpResult.getStatus()) {
					InputStream mInputStream = mHttpResult.getInputstream();
					byte bytes[] = readStream(mInputStream);
					String json = new String(bytes);
					// 屏蔽json返回带有xml
					if (json.length() > 0) {
						json = json.substring(json.indexOf("{"));
					}
					mHttpResult.setJson(json);
					XLog.i("wangtf", "json:" + json);
					// 先检查返回的数据是否正确，能够供界面正确展示
					HttpErrorUtil.parseWrongResult(json, mHttpResult);
				}
			} catch (Exception e) {
				e.printStackTrace();
				mHttpResult = new HttpResult();
				mHttpResult.setStatus(false);
			}
		} else if (!NetWorkInfoUtil.isConencting(mContext)) {
			mHttpResult = new HttpResult();
			mHttpResult.setStatus(false);
			mHttpResult.setCode(HttpResult.NETWORK_UNAVAILABLE);
		}
		return mHttpResult;
	}

	/**
	 * 
	 * @Description:post with multi params.
	 * @param path
	 * @param params
	 * @return HttpResult
	 * @see:
	 * @since:
	 * @author:Tienfook Chang
	 * @date:2012-9-18
	 */
	// public static HttpResult postFormData(String path, Map<String, String>
	// params) {
	//
	// HttpResult mHttpResult = null;
	// InputStream mInputStream = null;
	// try {
	// mHttpResult = post(path, params, "UTF-8");
	// if (mHttpResult.getStatus()) {
	// mInputStream = mHttpResult.getInputstream();
	// byte bytes[] = readStream(mInputStream);
	// String json = new String(bytes);
	// mHttpResult.setJson(json);
	//
	// // 先检查返回的数据是否正确，能够供界面正确展示
	// HttpErrorUtil.parseWrongResult(json, mHttpResult);
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// return mHttpResult;
	// }
	/**
	 * 
	 * @Description:post with file and multi params.
	 * @param actionUrl
	 * @param mContext
	 * @param params
	 * @param files
	 * @return
	 * @see: in use
	 * @since:
	 * @author:Tienfook Chang
	 * @date:2012-9-18
	 */
	public static HttpResult postWithFile(Context mContext, Map<String, String> params, FormFile[] files) throws Exception {
		HttpResult httpResult = new HttpResult();
		StringBuilder parambuilder = new StringBuilder("");
		if (params != null) {
			for (Map.Entry<String, String> entry : params.entrySet()) {
				parambuilder.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), "UTF-8")).append("&");
			}
		}
		// signature
//		String signature = MD5.signature(params, "UTF-8");
//		parambuilder.append("sig=").append(signature);
//		// byte[] data = parambuilder.toString().getBytes();
		URL url = new URL(Constant.URL_UPLOAD_AVATAR +"?"+ parambuilder.toString());
		boolean flag = NetWorkInfoUtil.isConencting(mContext);
		if (!flag) {
			httpResult = new HttpResult();
			httpResult.setStatus(false);
			httpResult.setCode(HttpResult.NETWORK_UNAVAILABLE);
			return httpResult;
		}
		try {
			String enterNewLine = "\r\n";
			String fix = "--";
			String boundary = "-----------------------------7da1ec344076c";
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Charset", "UTF-8");
			conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
//			Configuration mConfiguration = Configuration.getConfiguration();
//			if ("".equals(mConfiguration.getUser().getCu())) {
//				mConfiguration.init(mContext);
//			}
//			conn.setRequestProperty("Cookie", "cu=" + mConfiguration.getUser().getCu() + ";cs=" + mConfiguration.getUser().getCs() + ";");

			DataOutputStream ds = new DataOutputStream(conn.getOutputStream());
			Set<String> keySet = params.keySet();
			Iterator<String> it = keySet.iterator();
			while (it.hasNext()) {
				String key = it.next();
				String value = params.get(key);
				ds.writeBytes(fix + boundary + enterNewLine);
				info(fix + boundary + enterNewLine);
				// ds.writeBytes("Content-Disposition: form-data; " + "name=\""
				// + key + "\"" + enterNewLine);
				ds.write(("Content-Disposition: form-data; " + "name=\"" + key + "\"" + enterNewLine).getBytes("UTF-8"));
				info("Content-Disposition: form-data; " + "name=\"" + key + "\"" + enterNewLine);
				ds.writeBytes(enterNewLine);
				info(enterNewLine);
				// ds.writeBytes(value);
				ds.write(value.getBytes("UTF-8"));
				info(value);
				ds.writeBytes(enterNewLine);
				info(enterNewLine);
			}

			if (files != null && files.length > 0) {
				for (int i = 0; i < files.length; i++) {
					if (files[i] != null) {
						ds.writeBytes(fix + boundary + enterNewLine);
						info(fix + boundary + enterNewLine);
						ds.writeBytes("Content-Disposition: form-data; name=\"" + files[i].getFileType() + "\"" + "; filename=\""+ files[i].getFilname() + "\"\r\n");
						info("Content-Disposition: form-data; name=\"" + files[i].getFileType() + "\"" + "; filename=\""+ files[i].getFilname() + "\"\r\n");
						ds.writeBytes("Content-Type: " + files[i].getContentType() + enterNewLine);
						info("Content-Type: " + files[i].getContentType() + enterNewLine);
						ds.writeBytes(enterNewLine);
						info(enterNewLine);
						byte[] buffer = files[i].getData();
						ds.write(buffer);
						info(new String(buffer));
						ds.writeBytes(enterNewLine);
						info(enterNewLine);
					}
				}
			}
			ds.writeBytes(fix + boundary + "--");
			info(fix + boundary + "--");
			ds.flush();
			int code = conn.getResponseCode();
			XLog.i("the response code is:" + code);
			if (code == 200) {
				InputStream is = conn.getInputStream();
				byte bytes[] = readStream(is);
				String json = new String(bytes);
				XLog.i("the response json is:" + json);
				httpResult.setStatus(true);
				httpResult.setCode(200);
				httpResult.setJson(json);
				// 先检查返回的数据是否正确，能够供界面正确展示
				HttpErrorUtil.parseWrongResult(json, httpResult);
				info(json);
			} else {
				httpResult.setStatus(false);
				httpResult.setCode(code);
			}
		} catch (Exception e) {
			if (e instanceof ConnectException || e instanceof SocketTimeoutException) {
				httpResult.setStatus(false);
				httpResult.setCode(HttpResult.TIME_OUT);
			} else if (e instanceof UnknownHostException) {
				httpResult.setStatus(false);
				httpResult.setCode(HttpResult.UBKNOWN_HOST);
			} else {
				httpResult.setStatus(false);
				httpResult.setCode(HttpResult.NETWORK_REQUEST_FAILURE);
			}
		}
		return httpResult;
	}

	/**
	 * 
	 * @Description:readStream
	 * @param inStream
	 * @return
	 * @throws Exception
	 * @see:
	 * @since:
	 * @author:Tienfook Chang
	 * @date:2012-9-19
	 */
	public static byte[] readStream(InputStream inStream) throws Exception {

		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = -1;
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		inStream.close();
		outStream.flush();
		byte[] bytes = outStream.toByteArray();
		outStream.close();
		return bytes;
	}

	private static void info(String info) {

		XLog.i(info);
	}
}