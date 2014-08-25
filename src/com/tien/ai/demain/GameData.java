/**
 * 
 */
package com.tien.ai.demain;

/**
 * TODO
 * @author wangtianfei01
 *
 */
public class GameData {
    
    private String id = "";
    private String name = "";
    private String opponentData = "";
    private String meData = "";
    private String gameSequence = "";
    private String winUid = "";
    
    public GameData(String id, String name, String meData, String opponentData, String gameSequence) {
        super();
        this.id = id;
        this.name = name;
        this.meData = meData;
        this.opponentData = opponentData;
        this.gameSequence = gameSequence;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
   
    public String getOpponentData() {
        return opponentData;
    }
    public void setOpponentData(String opponentData) {
        this.opponentData = opponentData;
    }
    public String getMeData() {
        return meData;
    }
    public void setMeData(String meData) {
        this.meData = meData;
    }
    public String getGameSequence() {
        return gameSequence;
    }
    public void setGameSequence(String gameSequence) {
        this.gameSequence = gameSequence;
    }
    public String getWinUid() {
        return winUid;
    }
    public void setWinUid(String winUid) {
        this.winUid = winUid;
    }
    
    
    
    
}
