package com.tien.ai.demain;

public class Ai {
    
    private String uid;
    private String nickname;
    private int type;
    private int inCount;
    private String time = "";
    private String gameSequence = "";
    
    
    public Ai(String uid, String nickname, int type, int inCount, String time) {
        super();
        this.uid = uid;
        this.nickname = nickname;
        this.type = type;
        this.inCount = inCount;
        this.time = time;
    }
    
    public String getUid() {
        return uid;
    }
    
    public void setUid(String uid) {
        this.uid = uid;
    }
    
    public String getNickname() {
        return nickname;
    }
    
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    
    public int getType() {
        return type;
    }
    
    public void setType(int type) {
        this.type = type;
    }
    
    
    
    public int getInCount() {
        return inCount;
    }

    public void setInCount(int inCount) {
        this.inCount = inCount;
    }

    public String getTime() {
        return time;
    }
    
    public void setTime(String time) {
        this.time = time;
    }

    public String getGameSequence() {
        return gameSequence;
    }

    public void setGameSequence(String gameSequence) {
        this.gameSequence = gameSequence;
    }

    
    
    
}
