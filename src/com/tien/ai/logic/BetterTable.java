package com.tien.ai.logic;

import java.util.Collection;

import com.tien.ai.AIApplication;
import com.tien.ai.callback.DataCallback;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;


/**
 * 
 * @Description:操作数据库表 基类 (提供公用操作)
 * @author: 
 * @see:   
 * @since:      
 * @Date:2012-6-21
 */
	
public abstract class BetterTable{
	//AND 条件常量
	protected static final String AND =" and ";
	protected static final String LIKE =" like ";
	protected static final String OR =" or ";
	protected static final String PLACEHOLDER =" ? ";
	protected static final String GROUP_BY =" group by ";
	
	//未删除标记
	protected static final int UN_DELETEED_FLAG = 0 ;
	
	//sort_key 字段
	
	protected static final String SORT_KEY = "sort_key";
	//memetype_id 字段
	protected static final String MIME_TYPE_ID = "mimetype_id";
	
	public static final int INT_DISABLE_VALUE = -1;
	
	/***
	 * @Description:get ContentResolver
	 * @return
	 * @see: 
	 * @since: 
	 * @author: 
	 * @date:2012-6-21
	 */
	public static ContentResolver getContentResolver() {

		return AIApplication.getInstance().getContentResolver();

	}
	
//	public static DBHelper getDBHelper() {
//		
//		return Singlton.getInstance(DBHelper.class);
//	}
	
	
	/**
	 * 
	 * @Description: 根据数组参数和字段名称 生成 in 条件  case: in(1,3,4)
	 * @param name 字段名称
	 * @param array 参数
	 * @return
	 * @see: 
	 * @since: 
	 * @author: 
	 * @date:2012-6-21
	 */
	public static String appendInWhere(String name,Object[] array) {

		StringBuilder where = new StringBuilder();

		if (array != null && array.length > 0) {

			where.append(name).append(" in(");

			for (Object i : array) {
				if(i instanceof String){
					where.append("'").append(i).append("'").append(",");
				}else{
					where.append(i).append(",");
				}
				
			}

			where.deleteCharAt(where.length() - 1);
			where.append(")");
			
			return where.toString();
		}

		return "";

	}
	
	public static String appendInWhere(String name,Collection<Long> array) {

		StringBuilder where = new StringBuilder();

		if (array != null && array.size() > 0) {

			where.append(name).append(" in(");

			for (Object i : array) {
				if(i instanceof String){
					where.append("'").append(i).append("'").append(",");
				}else{
					where.append(i).append(",");
				}
				
			}

			where.deleteCharAt(where.length() - 1);
			where.append(")");
			
			return where.toString();
		}

		return "";

	}
	
	/**
	 * 
	 * @Description:将int值转为String
	 * @param value
	 * @return
	 * @see: 
	 * @since: 
	 * @author: 
	 * @date:2012-6-21
	 */
	public static String toString(int value){
		return Integer.toString(value);
	}
	
	/**
	 * 
	 * @Description:close cursor object
	 * @param cursor
	 * @see: 
	 * @since: 
	 * @author: 
	 * @date:2012-6-21
	 */
	public static void closeCursor(Cursor cursor){
		if(cursor!=null){
			cursor.close();
			cursor=null;
		}
	}
	
	/**
	 * 
	 * @Description:查询数量
	 * @param uri
	 * @param selection
	 * @param args
	 * @return
	 * @see: 
	 * @since: 
	 * @author: 
	 * @date:2012-8-7
	 */
	public static int readCount(Uri uri,String selection,String[] args){

		Cursor cursor = getContentResolver().query(uri,new String[]{"count(_id)"}, selection, args,null);
		
		try{
			if(cursor==null){
				return 0;
			}
			
			if(cursor.moveToNext()){
				return cursor.getInt(0);
			}
			
		} finally {
			closeCursor(cursor);
		}
		return 0;
		
	}
	
	/**
	 * 
	 * @Description:查询
	 * @param uri
	 * @param projection
	 * @param selection
	 * @param args
	 * @param orderName
	 * @param cursorCallback
	 * @author: 
	 * @date:2012-9-27
	 */
	public static void query(Uri uri, String[] projection, String selection,String[] args,String orderName, DataCallback<Cursor> cursorCallback) {
		Cursor cursor = getContentResolver().query(uri, projection, selection, args, orderName);
		try {
			while (cursor.moveToNext()) {
				cursorCallback.callback(cursor);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeCursor(cursor);
		}
	}
	
	public static void query(Uri uri, String[] projection, String selection, DataCallback<Cursor> cursorCallback) {
			query(uri, projection, selection,null,null,cursorCallback);
	}
	
	public static int onDelete(final Uri uri, final String selection, final String[] selectionArgs) {
		return getContentResolver().delete(uri, selection, selectionArgs);
	}
	
}
