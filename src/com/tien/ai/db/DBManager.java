package com.tien.ai.db;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.tien.ai.demain.Game;
import com.tien.ai.demain.GameData;
import com.tien.ai.utils.PreferenceUtils;

/**
 * @Description:
 * @author:wangtf
 * @see:
 * @since:
 * @copyright © baidu.com
 * @Date:2014-2-27
 * 
 * DBManager.initializeInstance(dbhlper);
 * SQLiteDatabase database = DBManager.getInstance().openDatabase();
 * database.insert(...);
 * DatabaseManager.getInstance().closeDatabase(); // correct way
 * 
 */
public class DBManager {

	private AtomicInteger mOpenCounter = new AtomicInteger();

	private static DBManager instance;
	private static SQLiteOpenHelper mDBHelper;
	private SQLiteDatabase mDatabase;

	public static synchronized void initializeInstance(SQLiteOpenHelper helper) {
		if (instance == null) {
			instance = new DBManager();
			mDBHelper = helper;
		}
		
	}

	public static synchronized DBManager getInstance() {
		if (instance == null) {
			throw new IllegalStateException(DBManager.class.getSimpleName() + " is not initialized, call initializeInstance(..) method first.");
		}

		return instance;
	}

	public synchronized SQLiteDatabase openDatabase() {
		if (mOpenCounter.incrementAndGet() == 1) {
			// Opening new database
			 try {
				 mDatabase = mDBHelper.getWritableDatabase();
			 } catch (Exception e) {
				 mDatabase = mDBHelper.getReadableDatabase();
			 }
		}
		return mDatabase;
	}

	public synchronized void closeDatabase() {
		if (mOpenCounter.decrementAndGet() == 0) {
			// Closing database
			mDatabase.close();
		}
	}
	
	public boolean add(String uid, String nickname, int type){
	    mDatabase = mDBHelper.getWritableDatabase();
	    mDatabase.beginTransaction();
	    
	    SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm:ss");
        String time = format.format(new Date());  
//      CharSequence time = DateFormat.format("yyyy-MM-dd HH:mm:ss", System.currentTimeMillis());
        String where = "uid = ?";
        String[] whereValue = { uid };
        
	    Cursor cursor = mDatabase.query(AIDBHelper.TABLE_CONVERSATION, new String[]{"uid","in_count"},  where, whereValue, null, null, null);
	    if(cursor != null && cursor.getCount() >0){
	       if(cursor.moveToNext()){
	           if(type == 0){
	               updateData(uid, type, 0, time);
	           }else{
	               int inCount = cursor.getInt(cursor.getColumnIndex("in_count"));
	                
	               updateData(uid, type, ++inCount ,time);
	           }
            }
	    }else{
	        ContentValues cv = new ContentValues();
	        cv.put("uid", uid);
	        cv.put("nickname", nickname);
	        cv.put("type", type);
	        cv.put("time", time);
	        if(type == 0){
	            cv.put("in_count", 0);
            }else{
                cv.put("in_count", 1);
            }
	        
	        mDatabase.insert(AIDBHelper.TABLE_CONVERSATION, null, cv);
	    }
	    
        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();

        
        closeDatabase();
        return true;
    }
	
	public long delete(String uid){
	    mDatabase = mDBHelper.getWritableDatabase();
        String where = "uid = ?";
        String[] whereValue = { uid };
        long num = mDatabase.delete(AIDBHelper.TABLE_CONVERSATION, where, whereValue);
        return num;
	}
	
	public long updateInCount(String uid, int count){
        mDatabase = mDBHelper.getWritableDatabase();
        mDatabase.beginTransaction();
        String where = "uid = ?";
        String[] whereValue = { uid };
        
        ContentValues cv = new ContentValues();
        cv.put("in_count", count);
        
        long num = mDatabase.update(AIDBHelper.TABLE_CONVERSATION,cv,  where, whereValue);
        return num;
    }
	
	public long updateData(String uid, int type, int count, String time){
        mDatabase = mDBHelper.getWritableDatabase();
        String where = "uid = ?";
        String[] whereValue = { uid };
        
        ContentValues cv = new ContentValues();
        cv.put("in_count", count);
        cv.put("type", type);
        cv.put("time", time);
        
        long num = mDatabase.update(AIDBHelper.TABLE_CONVERSATION,cv,  where, whereValue);
        return num;
    }
	
	public void insertRemind(String uid, int remind){
	    mDatabase = mDBHelper.getWritableDatabase();
	    mDatabase.beginTransaction();
	    
	    String where = "uid = ?";
        String[] whereValue = { uid };
        long num = mDatabase.delete(AIDBHelper.TABLE_REMIND, where, whereValue);
        
	    ContentValues cv = new ContentValues();
        cv.put("uid", uid);
        cv.put("remind", String.valueOf(remind));
        SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm:ss");
        String time = format.format(new Date());  
        cv.put("time", time);
        
        mDatabase.insert(AIDBHelper.TABLE_REMIND, null, cv);
        
        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();
	}
	
	public long deleteRemindByUid(String uid){
	    mDatabase = mDBHelper.getWritableDatabase();
        String where = "uid = ?";
        String[] whereValue = { uid };
        long num = mDatabase.delete(AIDBHelper.TABLE_REMIND, where, whereValue);
        return num;
	}
	
	public ArrayList<String> queryAllRemind(){
	    ArrayList<String> remindUids = new ArrayList<String>();
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        Cursor cursor = db.query(AIDBHelper.TABLE_REMIND, new String[]{"uid"}, null, null, null, null, null);
        if(cursor != null){
            while(cursor.moveToNext()){
                String uid = cursor.getString(0);
                if(!"".equals(uid)){
                    remindUids.add(uid);
                }
            }
        }
        return remindUids;
    }
	
	public void insertGame(Game game){
        mDatabase = mDBHelper.getWritableDatabase();
        mDatabase.beginTransaction();
        
        String where = "game_sequence = ?";
        String[] whereValue = { game.getGameSequence() };
        long num = mDatabase.delete(AIDBHelper.TABLE_GAME, where, whereValue);
        
        ContentValues cv = new ContentValues();
        cv.put("uid", game.getFromUid());
        cv.put("game_id", game.getGameId());
        cv.put("game_sequence", game.getGameSequence());
        cv.put("game_role", game.getGameRole());
        cv.put("game_data", game.getGameData());
        //cv.put("game_name", game.getName());
        cv.put("win_uid", game.getWinUid());
        cv.put("game_url", game.getGameUrl());
        cv.put("game_status", String.valueOf(game.getGameStatus()));
        cv.put("status", game.getStatus());
        cv.put("time", System.currentTimeMillis());
        
        mDatabase.insert(AIDBHelper.TABLE_GAME, null, cv);
        
        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();
    }
	
	public Game loadGameBySequence(String sequence){
	    SQLiteDatabase db = mDBHelper.getReadableDatabase();
	    String[] query = new String[]{"uid","game_id","game_name", "game_sequence", "game_role", "game_data", "win_uid", "game_url", "game_status", "status",  "time"};
	    String where = "game_sequence = ?";
        String[] whereValue = { sequence };
        Cursor cursor = db.query(AIDBHelper.TABLE_GAME, query, where, whereValue, null, null, "time desc");
        
        if(cursor != null && cursor.moveToNext()){
            String uid = cursor.getString(0);
            String gameId = cursor.getString(1);
            String gameName = cursor.getString(2);
            String gameSequence = cursor.getString(3);
            String gameRole = cursor.getString(4);
            String gameData = cursor.getString(5);
            String winUid = cursor.getString(6);
            String gameUrl = cursor.getString(7);
            int gameStatus = cursor.getInt(8);
            int status = cursor.getInt(9);
            long time = cursor.getLong(10);
            
            Game game = new Game(uid, PreferenceUtils.loadUid(), gameId, gameSequence, gameRole, gameData, winUid, gameUrl, gameStatus);
            game.setStatus(status);
            
            return game;
        }
        return null;
	}
	
	public long updateStatus(String gameSequence, int status){
        mDatabase = mDBHelper.getWritableDatabase();
        String where = "game_sequence = ?";
        String[] whereValue = { gameSequence };
        
        ContentValues cv = new ContentValues();
        cv.put("status", status);
        
        long num = mDatabase.update(AIDBHelper.TABLE_GAME,cv,  where, whereValue);
        return num;
    }
	/**
	 * 查询
	 * 
	 * @param sql
	 * @return
	 */
	public Cursor select(String sql) {
		return mDatabase.rawQuery(sql, null);
	}
	
	
	public Cursor queryAll(){
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
		return db.query(AIDBHelper.TABLE_CONVERSATION, new String[]{"uid","nickname","type", "in_count", "mask",  "time"}, null, null, null, null, "time desc");
	}
	
	public Cursor queryGame(){
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        String where = "status = ?";
        String[] whereValue = { "1" };
        return db.query(AIDBHelper.TABLE_GAME, new String[]{"uid","game_sequence", "time"}, where, whereValue, null, null, "time desc");
    }
}