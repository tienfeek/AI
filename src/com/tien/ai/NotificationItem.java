package com.tien.ai;

/**
 * 
 * @Description:
 * @author:wangtf
 * @see:   
 * @since:      
 * @copyright © baidu.com
 * @Date:2014-4-24
 */
public class NotificationItem {
	
	public static final int TYPE_ADD_FRIEND = 100;										    //添加好友
	public static final int TYPE_SEND_AI = 1001;										    //发送AI
	public static final int TYPE_RECEIVE_AI = 1002;										    //收到AI
	public static final int TYPE_MODIFY_USERINFO = 1003;							        //修改
	public static final int TYPE_GAME = 1004;							                    //游戏


	private int type;
	private Object message;
	
	public NotificationItem() {}
	
	public NotificationItem(int type, Object message) {
		this.type = type;
		this.message = message;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public Object getMessage() {
		return message;
	}
	public void setMessage(Object message) {
		this.message = message;
	}
	
	
}
