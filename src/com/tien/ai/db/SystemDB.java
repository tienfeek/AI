/**
 * 
 */
package com.tien.ai.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.ClipData.Item;
import android.database.Cursor;
import android.provider.ContactsContract;

import com.tien.ai.AIApplication;

/**
 * TODO
 * @author wangtianfei01
 *
 */
public class SystemDB {
    
    
    public static String queryAllPhone(){
        StringBuffer sb = new StringBuffer();
        
        Cursor cursor = AIApplication.getInstance()
                                     .getApplicationContext()
                                     .getContentResolver()
                                     .query(ContactsContract.Data.CONTENT_URI
                                         ,new String[]{"contact_id","mimetype","data1","data2","sort_key","display_name" }
                                         ,"mimetype=?"
                                         , new String[]{"vnd.android.cursor.item/phone_v2"}
                                         , null);
        
        while (cursor.moveToNext()) {
            
            int contactId = cursor.getInt(0);
            String mimeType = cursor.getString(1);
            String data1 = cursor.getString(2);
            

            if(mimeType.equals("vnd.android.cursor.item/phone_v2")){
                if(data1 != null && data1.trim().length()>0){
                    if(data1.startsWith("+86")){
                       data1 = data1.substring(3);
                    }
                    data1 = data1.replaceAll("-", "");
                    data1 = data1.replaceAll(" ", "");
                }
                sb.append(data1).append(":");
            }
        }
        cursor.close();
        
        return sb.substring(0, sb.length()-1);
    }
    
    public static HashMap<String, String> queryAllContactMap(){
        HashMap<String, String> contactMap = new  HashMap<String, String>();
        
        Cursor cursor = AIApplication.getInstance()
                                     .getApplicationContext()
                                     .getContentResolver()
                                     .query(ContactsContract.Data.CONTENT_URI
                                         ,new String[]{"contact_id","mimetype","data1","data2","sort_key","display_name" }
                                         ,"mimetype=?"
                                         , new String[]{"vnd.android.cursor.item/phone_v2"}
                                         , null);
        
        while (cursor.moveToNext()) {
            
            int contactId = cursor.getInt(0);
            String mimeType = cursor.getString(1);
            String data1 = cursor.getString(2);
            

            if(mimeType.equals("vnd.android.cursor.item/phone_v2")){
                contactMap.put(data1, String.valueOf(contactId));
            }
        }
        cursor.close();
        
        return contactMap;
    }
    
    
    
}
