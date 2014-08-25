package com.tien.ai.demain;


/**
 * 
 * <p>Title: ContactsMeta</p>
 * <p>Description: 联系人简略信息</p>
 * @author wangtf
 * @date 2014-1-22
 */
public class ContactsMeta implements Cloneable {

	private String uid = "";
	private String contactId = "";
	private String nickname = "";
	private String nicknamePinyin = "";
	private String avatar = "";
	private char firstChar = 0;
	private int status = 1;

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}
	

	public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getNicknamePinyin() {
		return nicknamePinyin;
	}

	public void setNicknamePinyin(String nicknamePinyin) {
		this.nicknamePinyin = nicknamePinyin;
	}
	
	

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public char getFirstChar() {
		return firstChar;
	}

	public void setFirstChar(char firstChar) {
		this.firstChar = firstChar;
	}
	
	
	

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

   

	@Override
    public String toString() {
        return "ContactsMeta [uid=" + uid + ", contactId=" + contactId + ", nickname=" + nickname
            + ", status=" + status + "]";
    }

    @Override
	public ContactsMeta clone() throws CloneNotSupportedException {
		ContactsMeta contacts = null;
		try {
			contacts = (ContactsMeta) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return contacts;
	}
	
	 
}
