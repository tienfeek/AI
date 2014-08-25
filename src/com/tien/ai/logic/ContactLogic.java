package com.tien.ai.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import android.content.ContentProviderOperation;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.RawContacts.Data;
import android.xutil.task.BackTask;
import android.xutil.task.ForeTask;

import com.tien.ai.callback.DataCallback;
import com.tien.ai.demain.Contact;
import com.tien.ai.demain.ContactsMeta;
import com.tien.ai.utils.NicknameComparator;
import com.tien.ai.utils.PinyinUtils;
import com.tien.ai.utils.XLog;

/**
 * <p>
 * Title: ContactLogic.java
 * </p>
 * <p>
 * Description: 联系人逻辑类
 * </p>
 * 
 * @author wangtf
 * @date 2014-1-19
 */
public class ContactLogic extends BetterTable {
    
    private static final Uri TABLE_URI = ContactsContract.Contacts.CONTENT_URI;
    
    /**
     * 
     * <p>
     * Title: loadAllContactMetas
     * </p>
     * <p>
     * Description:
     * </p>
     */
    public static void loadAllContactMetas(final DataCallback<List<ContactsMeta>> callback) {
        new BackTask(true) {
            
            @Override
            public void onBack() {
                
                final List<ContactsMeta> contactMetas = new ArrayList<ContactsMeta>();
                
                String[] progection = new String[] { ContactsContract.Contacts._ID,
                    ContactsContract.Contacts.DISPLAY_NAME };
                query(TABLE_URI, progection, null, new DataCallback<Cursor>() {
                    
                    @Override
                    public void callback(Cursor cursor) {
                        
                        if (cursor != null) {
                            ContactsMeta contactMeta = cursor2ContactMeta(cursor);
                            contactMetas.add(contactMeta);
                        }
                    }
                });
                Collections.sort(contactMetas, new NicknameComparator());
                
                new ForeTask(true) {
                    
                    @Override
                    public void onFore() {
                        callback.callback(contactMetas);
                    }
                };
            }
        };
    }
    
    private static ContactsMeta cursor2ContactMeta(Cursor cursor) {
        // 获取联系人id号
        String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
        // 获取联系人姓名
        String displayName = cursor.getString(cursor
            .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        ContactsMeta contactMeta = new ContactsMeta();// 初始化联系人信息
        contactMeta.setNickname(displayName);
        contactMeta.setContactId(id);
        
        String pinyin = "";
        try {
            pinyin = PinyinUtils.getHanyuPinyin(displayName);
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
        }
        contactMeta.setNicknamePinyin(pinyin);
        char firstChar = pinyin.length() > 0 ? pinyin.charAt(0) : 0;
        contactMeta.setFirstChar(firstChar);
        
        return contactMeta;
    }
    
    /**
     * 获取所有联系人信息
     * 
     * @param context
     * @return
     */
    public static ArrayList<Contact> getContactInfo() {
        ArrayList<Contact> infoList = new ArrayList<Contact>();
        
        Cursor cur = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null,
            null, null);
        
        try {
            while (cur.moveToNext()) {
                // 获取联系人id号
                int id = cur.getInt(cur.getColumnIndex(ContactsContract.Contacts._ID));
                // 获取联系人姓名
                String displayName = cur.getString(cur
                    .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                Contact info = new Contact(displayName);// 初始化联系人信息
                info.setId(id);
                
                // 查看联系人有多少电话号码, 如果没有返回0
                int phoneCount = cur.getInt(cur
                    .getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                lookupPhones(phoneCount, id, info);
                
                // lookupEmails(id, info);
                
                infoList.add(info);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeCursor(cur);
        }
        
        return infoList;
    }
    
    public static String lookupPhone(String id) {
        
        Cursor phonesCursor = getContentResolver().query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + id, null, null);
        if (phonesCursor != null) {
            
            Map<Integer, String> phones = new HashMap<Integer, String>();
            try {
                while (phonesCursor.moveToNext()) {
                    // 遍历所有电话号码
                    String phoneNumber = phonesCursor.getString(phonesCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    // 对应的联系人类型
                    int type = phonesCursor.getInt(phonesCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                    
                    phones.put(type, phoneNumber);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                closeCursor(phonesCursor);
            }
            phonesCursor.close();
            // 设置联系人电话信息
            if(phones.size() >0){
               if(phones.containsKey(2)){
                   return  phones.get(2);
               }else{
                   String[] phonenumber = null;
                try {
                    phonenumber = (String [])phones.values().toArray();
                } catch (Exception e) {
                    e.printStackTrace();
                    phonenumber = new String[]{};
                }
                   if(phonenumber.length >0){
                       return phonenumber[0];
                   }else{
                       return "";
                   }
               }
               
            }else{
                return "";
            }
        }else{
            return "";
        }
    }
    
    /**
     * 获取联系人电话信息
     * 
     * @param phoneCount
     * @param id
     * @param info
     * @return
     */
    private static void lookupPhones(int phoneCount, int id, Contact info) {
        
        if (phoneCount > 0) {
            
            Cursor phonesCursor = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + id, null, null);
            
            ArrayList<Contact.PhoneInfo> phoneNumberList = new ArrayList<Contact.PhoneInfo>();
            try {
                while (phonesCursor.moveToNext()) {
                    // 遍历所有电话号码
                    String phoneNumber = phonesCursor.getString(phonesCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    // 对应的联系人类型
                    int type = phonesCursor.getInt(phonesCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                    
                    // 初始化联系人电话信息
                    Contact.PhoneInfo phoneInfo = new Contact.PhoneInfo();
                    phoneInfo.type = type;
                    phoneInfo.number = phoneNumber;
                    
                    phoneNumberList.add(phoneInfo);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                closeCursor(phonesCursor);
            }
            phonesCursor.close();
            // 设置联系人电话信息
            info.setPhoneList(phoneNumberList);
        }
    }
    
    public static int getContactId(long rawContactId) {
        Cursor cursor = getContentResolver().query(RawContacts.CONTENT_URI, null,
            RawContacts._ID + "=?", new String[] { String.valueOf(rawContactId) }, null);
        int contactId = 0;
        try {
            while (cursor.moveToNext()) {
                contactId = cursor.getInt(cursor.getColumnIndex(RawContacts.CONTACT_ID));
            }
        } catch (Exception e) {
            
        } finally {
            closeCursor(cursor);
        }
        return contactId;
    }
    
    public static int getRawContactId(int contactId) {
        Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null,
            ContactsContract.Contacts._ID + "=?", new String[] { String.valueOf(contactId) }, null);
        int rawContactId = 0;
        try {
            while (cursor.moveToNext()) {
                rawContactId = cursor.getInt(cursor.getColumnIndex("name_raw_contact_id"));
                XLog.i("wanges", "contactId:" + contactId + " rawContactId:" + rawContactId);
            }
        } catch (Exception e) {
            
        } finally {
            closeCursor(cursor);
        }
        return rawContactId;
    }
    
    // public static int addContact(Contact contact) {
    //
    // ContentValues values = new ContentValues();
    // Uri rawContactUri = getContentResolver().insert(RawContacts.CONTENT_URI,
    // values);
    // long rawContactId = ContentUris.parseId(rawContactUri);
    // //contact.setId((int)rawContactId);
    //
    //
    //
    // values.clear();
    // values.put(Data.RAW_CONTACT_ID, rawContactId);
    // values.put(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);
    // values.put(StructuredName.GIVEN_NAME, contact.getDisplayName());
    // getContentResolver().insert(android.provider.ContactsContract.Data.CONTENT_URI,
    // values);
    //
    // List<Contact.PhoneInfo> phoneList = contact.getPhoneList();
    // for (Contact.PhoneInfo phoneInfo : phoneList) {
    // values.clear();
    // values.put(android.provider.ContactsContract.Contacts.Data.RAW_CONTACT_ID,
    // rawContactId);
    // values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
    // values.put(Phone.NUMBER, phoneInfo.number);
    // values.put(Phone.TYPE, phoneInfo.type);
    // getContentResolver().insert(android.provider.ContactsContract.Data.CONTENT_URI,
    // values);
    // }
    //
    //
    //
    // List<Contact.EmailInfo> emailList = contact.getEmail();
    //
    // for (Contact.EmailInfo email : emailList) {
    // values.clear();
    // values.put(android.provider.ContactsContract.Contacts.Data.RAW_CONTACT_ID,
    // rawContactId);
    // values.put(Data.MIMETYPE, Email.CONTENT_ITEM_TYPE);
    // values.put(Email.DATA, email.email);
    // values.put(Email.TYPE, email.type);
    // getContentResolver().insert(android.provider.ContactsContract.Data.CONTENT_URI,
    // values);
    // }
    //
    // return (int)rawContactId;
    // }
    //
    // public static void updateContact(Contact contact) {
    //
    // int rawContactId = getRawContactId(contact.getId());
    // ContentValues values = new ContentValues();
    //
    // ArrayList<ContentProviderOperation> ops = new
    // ArrayList<ContentProviderOperation>();
    //
    // // values.put(Data.RAW_CONTACT_ID, rawContactId);
    // // values.put(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);
    // // values.put(StructuredName.GIVEN_NAME, contact.getDisplayName());
    // // getContentResolver().update(ContactsContract.Data.CONTENT_URI, values,
    // ContactsContract.Contacts._ID+"=?", new
    // String[]{String.valueOf(rawContactId)});
    // ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
    // .withSelection(Data.RAW_CONTACT_ID + "=?" + " AND "+
    // ContactsContract.Data.MIMETYPE +
    // " = ?",new String[] {
    // String.valueOf(rawContactId),StructuredName.CONTENT_ITEM_TYPE })
    // .withValue(StructuredName.DISPLAY_NAME,
    // contact.getDisplayName()).build());
    //
    // List<Contact.PhoneInfo> phoneList = contact.getPhoneList();
    // for (Contact.PhoneInfo phoneInfo : phoneList) {
    // // values.clear();
    // //
    // values.put(android.provider.ContactsContract.Contacts.Data.RAW_CONTACT_ID,
    // contact.getId());
    // // values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
    // // values.put(Phone.NUMBER, phoneInfo.number);
    // // values.put(Phone.TYPE, phoneInfo.type);
    // // getContentResolver().update(ContactsContract.Data.CONTENT_URI, values,
    // ContactsContract.Contacts._ID+"=?", new
    // String[]{String.valueOf(rawContactId)});
    //
    // ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
    // .withSelection(Data.RAW_CONTACT_ID + "=?" + " AND "+
    // ContactsContract.Data.MIMETYPE + " = ?" +
    // " AND " + Phone.TYPE + "=?",new String[] {
    // String.valueOf(rawContactId),Phone.CONTENT_ITEM_TYPE,
    // String.valueOf(phoneInfo.type) }).withValue(Phone.NUMBER,
    // phoneInfo.number).build());
    // }
    //
    // List<Contact.EmailInfo> emailList = contact.getEmail();
    //
    // for (Contact.EmailInfo email : emailList) {
    // // values.clear();
    // // values.put(ContactsContract.Contacts.Data.RAW_CONTACT_ID,
    // rawContactId);
    // // values.put(Data.MIMETYPE, Email.CONTENT_ITEM_TYPE);
    // // values.put(Email.DATA, email.email);
    // // values.put(Email.TYPE, email.type);
    // // getContentResolver().update(ContactsContract.Data.CONTENT_URI, values,
    // ContactsContract.Contacts._ID+"=?", new
    // String[]{String.valueOf(rawContactId)});
    // ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
    // .withSelection(Data.RAW_CONTACT_ID + "=?" + " AND "+
    // ContactsContract.Data.MIMETYPE + " = ?" +
    // " AND " + Email.TYPE + "=?",new String[] {
    // String.valueOf(rawContactId),Email.CONTENT_ITEM_TYPE,
    // String.valueOf(email.type) }).withValue(Email.DATA,
    // email.email).build());
    // }
    //
    // try {
    // getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
    // } catch (RemoteException e) {
    // e.printStackTrace();
    // } catch (OperationApplicationException e) {
    // e.printStackTrace();
    // }
    // }
    //
    // public static void deleteById(int contactId){
    // getContentResolver().delete(ContentUris.withAppendedId(RawContacts.CONTENT_URI,
    // contactId), null, null);
    // }
    
}
