/**
 * 
 */
package com.tien.ai.demain;

/**
 * TODO
 * @author wangtianfei01
 *
 */
public class Game {
    
    private String from_uid = "";
    private String to_uid = "";
    private String game_id = "";
    private String game_sequence = "";
    private String game_role = "";
    private String game_data = "";
    private String win_uid = "";
    private String game_url = "";
    private int game_status;
    private int status = 0;
    
    
    
    public Game(String fromUid,  String toUid, 
        String gameId, String gameSequence, String gameRole, String gameData, String winUid,
        String gameUrl, int gameStatus) {
        super();
        this.from_uid = fromUid;
        this.to_uid = toUid;
        this.game_id = gameId;
        this.game_sequence = gameSequence;
        this.game_role = gameRole;
        this.game_data = gameData;
        this.win_uid = winUid;
        this.game_url = gameUrl;
        this.game_status = gameStatus;
    }
    
    public String getFromUid() {
        return from_uid;
    }
    public void setFromUid(String fromUid) {
        this.from_uid = fromUid;
    }
    public String getToUid() {
        return to_uid;
    }
    public void setToUid(String toUid) {
        this.to_uid = toUid;
    }
    public String getGameId() {
        return game_id;
    }
    public void setGameId(String gameId) {
        this.game_id = gameId;
    }
    public String getGameSequence() {
        return game_sequence;
    }
    public void setGameSequence(String gameSequence) {
        this.game_sequence = gameSequence;
    }
    public String getGameRole() {
        return game_role;
    }
    public void setGameRole(String gameRole) {
        this.game_role = gameRole;
    }
    public String getGameData() {
        return game_data;
    }
    public void setGameData(String gameData) {
        this.game_data = gameData;
    }
    public String getWinUid() {
        return win_uid;
    }
    public void setWinUid(String winUid) {
        this.win_uid = winUid;
    }
    public String getGameUrl() {
        return game_url;
    }
    public void setGameUrl(String gameUrl) {
        this.game_url = gameUrl;
    }
    
    

    public int getGameStatus() {
        return game_status;
    }

    public void setGameStatus(int gameStatus) {
        this.game_status = gameStatus;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Game [from_uid=" + from_uid + ", to_uid=" + to_uid + ", game_id=" + game_id
            + ", game_sequence=" + game_sequence + ", game_role=" + game_role + ", game_data="
            + game_data + ", win_uid=" + win_uid + ", game_url=" + game_url + ", game_status="
            + game_status + ", status=" + status + "]";
    }

    
    
    
    
      
    
    
}
