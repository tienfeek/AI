package com.tien.ai.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;

public class UpgradeUtil {
	
	 //获取版本号
	 public static int getVerCode(Context context) {  
	        int verCode = -1;  
	        try {  
	            verCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;  
	        } catch (NameNotFoundException e) {  
	        }  
	        return verCode;  
	    } 
	 //获取版本名称
	    public static String getVerName(Context context) {  
	        String verName = "";  
	        try {  
	            verName = context.getPackageManager().getPackageInfo(  
	                    "com.baidu.regil.seckilling", 0).versionName;  
	        } catch (NameNotFoundException e) {  
	        }  
	        return verName;     
	     }  
	 
	    public static void openApkUrl(String url, Context context){
	    	Intent intent = new Intent(Intent.ACTION_VIEW);
	    	intent .setData(Uri.parse(url));
	    	context.startActivity(intent);
	    }
	 
	//下载文件
	    public static void downFile(final String url, final Context context) {  
	        new Thread() {  
	            public void run() {  
	                HttpClient client = new DefaultHttpClient();  
	                HttpGet get = new HttpGet(url);  
	                HttpResponse response;  
	                String[] strs = url.split("/");
	                String filename = strs[strs.length -1];
	                try {  
	                    response = client.execute(get);  
	                    HttpEntity entity = response.getEntity();  
	                    long length = entity.getContentLength();  
	                    InputStream is = entity.getContent();  
	                    FileOutputStream fileOutputStream = null;  
	                    if (is != null) {  
	                        File file = new File(Environment.getExternalStorageDirectory(),  filename);  
	                        fileOutputStream = new FileOutputStream(file);  
	                        byte[] buf = new byte[1024];  
	                        int ch = -1;  
	                        int count = 0;  
	                        while ((ch = is.read(buf)) != -1) {  
	                            fileOutputStream.write(buf, 0, ch);  
	                            count += ch;  
	                            if (length > 0) {  
	                            }  
	                        }  
	                    }  
	                    fileOutputStream.flush();  
	                    if (fileOutputStream != null) {  
	                        fileOutputStream.close();  
	                    }  
	                    Intent intent = new Intent(Intent.ACTION_VIEW);  
	                    XLog.i("wanges", "apk name:"+filename);
	                    intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory(), filename)),  
	                            "application/vnd.android.package-archive");  
	                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	                    
	                    context.startActivity(intent);  
	                } catch (ClientProtocolException e) {  
	                    e.printStackTrace();  
	                } catch (IOException e) {  
	                    e.printStackTrace();  
	                } catch (Exception e) {  
	                    e.printStackTrace();  
	                }
	            }  
	        }.start();  
	    } 

}
