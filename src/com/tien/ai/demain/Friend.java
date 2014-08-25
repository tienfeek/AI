/**
 * 
 */
package com.tien.ai.demain;

/**
 * TODO
 * @author wangtianfei01
 *
 */
public class Friend {
    
    private String uid = "";
    
    private String nickname = "";
    
    private String phone = "";
    
    private String avatar = "";
    
    private boolean remind = false;
    
    private char firstChar;
    
    private String nicknamePinyin = "";
    

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNickname() {
        return nickname;
    }
    
    

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    
    

    public boolean isRemind() {
        return remind;
    }

    public void setRemind(boolean remind) {
        this.remind = remind;
    }
    
    

    public char getFirstChar() {
        return firstChar;
    }

    public void setFirstChar(char firstChar) {
        this.firstChar = firstChar;
    }

    public String getNicknamePinyin() {
        return nicknamePinyin;
    }

    public void setNicknamePinyin(String nicknamePinyin) {
        this.nicknamePinyin = nicknamePinyin;
    }

    @Override
    public String toString() {
        return "Friend [uid=" + uid + ", nickname=" + nickname + ", phone=" + phone + ", avatar="
            + avatar + "]";
    }
    
    
    
    
}
