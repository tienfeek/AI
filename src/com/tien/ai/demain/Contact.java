package com.tien.ai.demain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * <p>Title: Contact</p>
 * <p>Description: ��ϵ����Ϣ��װ��</p>
 * @author wangtf
 * @date 2014-1-22
 */
public class Contact implements Serializable{

	/** serialVersionUID*/
    private static final long serialVersionUID = -1876069262957652350L;
    
	private int id ;
	private String uuid = "";
	
	private String displayName = ""; // ����
	/** ��ϵ�˰汾  */
	private int version;

	/** ��ϵ�˵绰��Ϣ */
	public static class PhoneInfo implements Serializable{
		/** serialVersionUID*/
        private static final long serialVersionUID = -3116751920265976314L;
        
		/** ��ϵ�绰���� */
		public int type;
		/** ��ϵ�绰 */
		public String number = "";
		
		@Override
        public String toString() {
	        return "PhoneInfo [type=" + type + ", number=" + number + "]";
        }
	}

	/** ��ϵ��������Ϣ */
	public static class EmailInfo implements Serializable{
		/** serialVersionUID*/
        private static final long serialVersionUID = 8904554848361959115L;
		/** �������� */
		public int type;
		/** ���� */
		public String email = "";
		
		@Override
        public String toString() {
	        return "EmailInfo [type=" + type + ", email=" + email + "]";
        }
		
		
	}

	private List<PhoneInfo> phoneList = new ArrayList<PhoneInfo>(); // ��ϵ����
	private List<EmailInfo> email = new ArrayList<EmailInfo>(); // Email
	

	public Contact(){
		
	}
	/**
	 * ������ϵ����Ϣ
	 * 
	 * @param name
	 *            ��ϵ������
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

	/** ��ϵ�绰��Ϣ */
	public List<PhoneInfo> getPhoneList() {
		return phoneList;
	}

	/** ��ϵ�绰��Ϣ */
	public Contact setPhoneList(List<PhoneInfo> phoneList) {
		this.phoneList = phoneList;
		return this;
	}

	/** ������Ϣ */
	public List<EmailInfo> getEmail() {
		return email;
	}

	/** ������Ϣ */
	public Contact setEmail(List<EmailInfo> email) {
		this.email = email;
		return this;
	}

	
	
	@Override
    public String toString() {
	    return "ContactInfo [name=" + displayName + ", phoneList=" + phoneList + ", email=" + email + "]";
    }
	
	

	
}