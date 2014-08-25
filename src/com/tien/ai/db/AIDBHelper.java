package com.tien.ai.db;

import com.tien.ai.AIApplication;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.format.DateFormat;
import android.xutil.XLog;

/**
 * 
 * @Description:
 * @author:wangtf
 * @see:   
 * @since:      
 * @copyright © baidu.com
 * @Date:2014-2-27
 */
public class AIDBHelper extends SQLiteOpenHelper {
	
	private static final String CREATE_TABLE_START_SQL = "CREATE TABLE IF NOT EXISTS ";
	private static final String CREATE_TABLE_PRIMIRY_SQL = " integer primary key autoincrement,";

	/**数据库名称*/
	private static final String DB_NAME = "ai.db";
	
	/**数据库版本*/
	private static final int VERSION = 6;
	
	public static final String TABLE_CONVERSATION = "conversation";
	public static final String TABLE_REMIND = "remind";
	public static final String TABLE_GAME = "game";
	

	
	public AIDBHelper(){
		super(AIApplication.getInstance(), DB_NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		createTables(db);
	}
	
	@Override
	synchronized public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (newVersion > oldVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONVERSATION);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_REMIND);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_GAME);
			
			XLog.i("wanges", "onUpgrade");
			onCreate(db);
		    
//		    StringBuffer gameSql = new StringBuffer();
//		    gameSql.append(CREATE_TABLE_START_SQL).append(TABLE_REMIND).append(" ( "); 
//		    gameSql.append(" _id").append(CREATE_TABLE_PRIMIRY_SQL);
//		    gameSql.append(" uid").append(" varchar(32)  ,");
//		    gameSql.append(" game_id").append(" varchar(32)  ,");
//		    gameSql.append(" game_name").append(" varchar(32)  ,");
//		    gameSql.append(" game_sequence").append(" varchar(32)  ,");
//		    gameSql.append(" me_data").append(" varchar(32)  ,");
//		    gameSql.append(" opponent_data").append(" varchar(32)  ,");
//		    gameSql.append(" win_uid").append(" varchar(32)  ,");
//		    gameSql.append(" status").append(" int default 0 ,");
//		    gameSql.append(" time").append(" long )");
//		    
//		    db.execSQL(gameSql.toString());
		}
	}

	
	synchronized public void createTables(SQLiteDatabase db) {

		if(db == null || db.isReadOnly()){
			db  = getWritableDatabase();
		}

		//创建用户信息表的SQL语句
		StringBuffer userSql = new StringBuffer();
		userSql.append(CREATE_TABLE_START_SQL).append(TABLE_CONVERSATION).append(" ( "); 
		userSql.append(" _id").append(CREATE_TABLE_PRIMIRY_SQL);
		userSql.append(" uid").append(" varchar(32)  ,");
		userSql.append(" nickname").append(" varchar(32)  ,");
		userSql.append(" type").append(" int, ");
		userSql.append(" in_count").append(" int default 0 ,");
		userSql.append(" mask").append(" int default 0 , ");
		userSql.append(" time").append(" varchar(32) )");
		
		//
		StringBuffer remindSql = new StringBuffer();
		remindSql.append(CREATE_TABLE_START_SQL).append(TABLE_REMIND).append(" ( "); 
		remindSql.append(" _id").append(CREATE_TABLE_PRIMIRY_SQL);
		remindSql.append(" uid").append(" varchar(32)  ,");
		remindSql.append(" remind").append(" int default 0 ,");
		remindSql.append(" time").append(" varchar(32) )");
		
		 StringBuffer gameSql = new StringBuffer();
		 gameSql.append(CREATE_TABLE_START_SQL).append(TABLE_GAME).append(" ( "); 
		 gameSql.append(" _id").append(CREATE_TABLE_PRIMIRY_SQL);
		 gameSql.append(" uid").append(" varchar(32)  ,");
		 gameSql.append(" game_id").append(" varchar(32)  ,");
		 gameSql.append(" game_name").append(" varchar(32)  ,");
		 gameSql.append(" game_sequence").append(" varchar(32)  ,");
		 gameSql.append(" game_role").append(" varchar(32)  ,");
		 gameSql.append(" game_data").append(" varchar(32)  ,");
		 //gameSql.append(" me_data").append(" varchar(32)  ,");
		 //gameSql.append(" opponent_data").append(" varchar(32)  ,");
		 gameSql.append(" win_uid").append(" varchar(32)  ,");
		 gameSql.append(" game_url").append(" varchar(32)  ,");
		 gameSql.append(" game_status").append(" int default 0  ,");
		 gameSql.append(" status").append(" int default 0 ,");
		 gameSql.append(" time").append(" long )");
         
         db.execSQL(userSql.toString());
         db.execSQL(remindSql.toString());
         db.execSQL(gameSql.toString());
		
	}
	
	

	
}
