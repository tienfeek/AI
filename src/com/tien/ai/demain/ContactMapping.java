/**
 * 
 */
package com.tien.ai.demain;

/**
 * TODO
 * @author wangtianfei01
 *
 */
public class ContactMapping {
    
    
    private String contactId = "";
    private String phone = "";
    private String uid = "";
    
    
    public String getContactId() {
        return contactId;
    }
    public void setContactId(String contactId) {
        this.contactId = contactId;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getUid() {
        return uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }
    @Override
    public String toString() {
        return "ContactMapping [contactId=" + contactId + ", phone=" + phone + ", uid=" + uid + "]";
    }
    
    
    
}
