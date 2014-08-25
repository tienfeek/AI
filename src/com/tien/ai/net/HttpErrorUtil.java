package com.tien.ai.net;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * 
 * @ClassName: HttpErrorUtil
 * @Description: 网络问题工具类
 * @author:wangtf
 * @date 2011-11-4 上午10:27:34
 * 
 */
public class HttpErrorUtil {

	public static void callbackError(final HttpResult mHttpResult, final String className) {

		if (mHttpResult == null) {
			return;
		}
		mHttpResult.setHttpError();
		
//		AIApplication.getHandler().post(new Runnable() {
//
//			public void run() {
//
//				ExceptionObservable mExceptionObservable = ExceptionObservable.getExceptionObservable();
//				HttpError mHttpError = mHttpResult.getmHttpError();
//				mExceptionObservable.notifyObservers(mHttpError, className);
//			}
//		});
	}

	/**
	 * 
	 * @Title: parseWrongResult
	 * @Description: 解析错误结果，如密码错误
	 * @author wangtf
	 * @date 2011-11-4
	 * @return void
	 * @throws
	 * @parameters
	 */
	public static void parseWrongResult(String json, HttpResult mHttpResult) {

		try {
			//XLog.i("parseWrongResult:" + json);
			if (mHttpResult == null) {
				mHttpResult = new HttpResult();
				mHttpResult.setStatus(false);
			}
			// 返回的json为null、“” 都表示没有正确展示的数据
			if (json == null || "".equals(json)) {
				mHttpResult.setValidData(false);
				return;
			}
			JSONObject jsonAll = new JSONObject(json);
			// 不在对success进行解析，
			String code = jsonAll.getString("code");
			if (!"0".equals(code)) {
				mHttpResult.setCode(Integer.parseInt(code));
				mHttpResult.setMsg(jsonAll.getString("msg"));
			}
			mHttpResult.setValidData(true);
			mHttpResult.setHttpError();
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
	}
}
