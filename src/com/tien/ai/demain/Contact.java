package com.tien.ai.demain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * <p>Title: Contact</p>
 * <p>Description: 联系人信息包装类</p>
 * @author wangtf
 * @date 2014-1-22
 */
public class Contact implements Serializable{

	/** serialVersionUID*/
    private static final long serialVersionUID = -1876069262957652350L;
    
	private int id ;
	private String uuid = "";
	
	private String displayName = ""; // 姓名
	/** 联系人版本  */
	private int version;

	/** 联系人电话信息 */
	public static class PhoneInfo implements Serializable{
		/** serialVersionUID*/
        private static final long serialVersionUID = -3116751920265976314L;
        
		/** 联系电话类型 */
		public int type;
		/** 联系电话 */
		public String number = "";
		
		@Override
        public String toString() {
	        return "PhoneInfo [type=" + type + ", number=" + number + "]";
        }
	}

	/** 联系人邮箱信息 */
	public static class EmailInfo implements Serializable{
		/** serialVersionUID*/
        private static final long serialVersionUID = 8904554848361959115L;
		/** 邮箱类型 */
		public int type;
		/** 邮箱 */
		public String email = "";
		
		@Override
        public String toString() {
	        return "EmailInfo [type=" + type + ", email=" + email + "]";
        }
		
		
	}

	private List<PhoneInfo> phoneList = new ArrayList<PhoneInfo>(); // 联系号码
	private List<EmailInfo> email = new ArrayList<EmailInfo>(); // Email
	

	public Contact(){
		
	}
	/**
	 * 构造联系人信息
	 * 
	 * @param name
	 *            联系人姓名
	 */
	public Contact(String name) {
		this.displayName = name;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	
	public String getDisplayName() {
		return displayName;
	}

	public Contact setDisplayName(String name) {
		this.displayName = name;
		return this;
	}

	/** 联系电话信息 */
	public List<PhoneInfo> getPhoneList() {
		return phoneList;
	}

	/** 联系电话信息 */
	public Contact setPhoneList(List<PhoneInfo> phoneList) {
		this.phoneList = phoneList;
		return this;
	}

	/** 邮箱信息 */
	public List<EmailInfo> getEmail() {
		return email;
	}

	/** 邮箱信息 */
	public Contact setEmail(List<EmailInfo> email) {
		this.email = email;
		return this;
	}

	
	
	@Override
    public String toString() {
	    return "ContactInfo [name=" + displayName + ", phoneList=" + phoneList + ", email=" + email + "]";
    }
	
	

	
}