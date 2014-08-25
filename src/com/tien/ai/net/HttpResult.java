package com.tien.ai.net;

import java.io.InputStream;

/**
 * 
 * @ClassName: HttpResult
 * @Description: http连接结果
 * @author:wangtf
 * @date 2011-10-19 下午4:23:57
 * 
 */
public class HttpResult {

	public static final int NETWORK_REQUEST_FAILURE = -1;
	public static final int NETWORK_UNAVAILABLE = -2;
	public static final int NETWORK_FAILURE = -3;
	public static final int FILE_NOT_FOUND_FAILURE = -4; //file
	public static final int UBKNOWN_HOST = 998;
	public static final int TIME_OUT = 999;
	// 1: 用户名为空 2: 密码为空 3. 错误的用户名和密码 4. 文件不能为空，5. 文件上传失败，请稍候再试
	public static final int USERNAME_NULL = 1;
	public static final int PASSWORD_NULL = 2;
	public static final int WRONG_USERNAM_PASSWORD = 3;
	public static final int FILE_NULL = 4;
	public static final int UPLOAD_FILE_FAIL = 5;
	public static final int USER_NOT_EXIST = 7;  //用户不存在
	public static final int CORRECT_RETURN = 200;
	public static final int TEMPORARILY_MOVED = 302;
	public static final int ACCESS_DENIED = 403;
	public static final int SERVER_ERROR = 1000;
	private boolean status = true;// 只有返回200为true，其它情况均为false；
	private int code;
	private String json = "";
	private InputStream inputstream;
	private boolean validData = true; // 服务器放回的数据是否正确，正确数据是指界面正确展示的数据 “success:false”,errr:1 属于不正确数据
	private HttpError mHttpError = new HttpError();
	// 返回给界面使用的数据，通常是已经解析封装好的对象供界面展示使用
	private Object data;
	//错误消息
	private String msg = "";
	private Exception exceptionResult;

	public Exception getExceptionResult() {

		return exceptionResult;
	}

	public void setExceptionResult(Exception exceptionResult) {

		this.exceptionResult = exceptionResult;
	}

	public HttpError getmHttpError() {

		return mHttpError;
	}

	public void setmHttpError(HttpError mHttpError) {

		this.mHttpError = mHttpError;
	}

	public boolean getStatus() {

		return status;
	}

	public void setStatus(boolean status) {

		this.status = status;
	}

	public int getCode() {

		return code;
	}

	public void setCode(int code) {

		this.code = code;
	}

	public String getJson() {

		return json;
	}

	public void setJson(String json) {

		this.json = json;
	}

	public Object getData() {

		return data;
	}

	public void setData(Object data) {

		this.data = data;
	}
	
	

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public InputStream getInputstream() {

		return inputstream;
	}

	public void setInputstream(InputStream inputstream) {

		this.inputstream = inputstream;
	}

	public boolean isValidData() {
		return validData;
	}

	public void setValidData(boolean validData) {
		this.validData = validData;
	}

	public void setHttpError() {
		if(this.code == 0){
			return ;
		}
		mHttpError.setCode(this.code);
		mHttpError.setStatus(this.status);
		
	    mHttpError.setMsg(msg);
		
	}

	@Override
	public String toString() {

		return "HttpResult [status=" + status + ", code=" + code + ", data=" + data + ", inputstream=" + inputstream + "]";
	}
}
