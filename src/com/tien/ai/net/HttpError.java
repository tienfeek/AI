package com.tien.ai.net;

/**
 * 
* @ClassName: HttpError
* @Description: 网络错误信息
* @author:wangtf
* @date 2011-11-3 下午6:53:19
*
 */
public class HttpError {
	
	public boolean status = true;
	public int code;
	public String msg = "";
	
	public boolean isStatus() {
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
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	
}
