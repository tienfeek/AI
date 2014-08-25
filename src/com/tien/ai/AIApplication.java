package com.tien.ai;

import java.util.ArrayList;

import android.graphics.Bitmap.CompressFormat;
import android.xutil.ThreadUtil;

import com.baidu.frontia.FrontiaApplication;
import com.tien.ai.db.AIDBHelper;
import com.tien.ai.db.DBManager;
import com.tien.ai.utils.XLog;
import com.tien.ai.view.BulletinWindow;
import com.tien.volley.RequestQueue;
import com.tien.volley.imagecache.ImageCacheManager;
import com.tien.volley.imagecache.ImageCacheManager.CacheType;
import com.tien.volley.toolbox.Volley;

/*
 * 如果您的工程中实现了Application的继承类，那么，您需要将父类改为com.baidu.frontia.FrontiaApplication。
 * 如果您没有实现Application的继承类，那么，请在AndroidManifest.xml的Application标签中增加属性： 
 * <application android:name="com.baidu.frontia.FrontiaApplication"
 * 。。。
 */
public class AIApplication extends FrontiaApplication {
    
    private static int DISK_IMAGECACHE_SIZE = 1024*1024*10;
    private static CompressFormat DISK_IMAGECACHE_COMPRESS_FORMAT = CompressFormat.PNG;
    private static int DISK_IMAGECACHE_QUALITY = 100;  //PNG is lossless so quality is ignored but must be provided
    
    private ArrayList<BaseActivity> activities = new ArrayList<BaseActivity>();
    private RequestQueue requestQueue;

    @Override
    public void onCreate() {
        super.onCreate();

        // 以下是您原先的代码实现，保持不变
        XLog.setLogLevel(1);
        
        ThreadUtil.init(3, 8);
        
        DBManager.initializeInstance(new AIDBHelper());
        
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        BulletinWindow.getInstance().attachPop();
        
        createImageCache();
        
    }
    
    private static AIApplication instance;

    public AIApplication() {
        instance = this;
    }
    
    public static AIApplication getInstance() {
        return instance;
    }
    
    public ArrayList<BaseActivity> getActivities(){
        return activities;
    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }

    public void setRequestQueue(RequestQueue requestQueue) {
        this.requestQueue = requestQueue;
    }
    
    private void createImageCache(){
        ImageCacheManager.getInstance().init(this,
                this.getPackageCodePath()
                , DISK_IMAGECACHE_SIZE
                , DISK_IMAGECACHE_COMPRESS_FORMAT
                , DISK_IMAGECACHE_QUALITY
                , CacheType.DISK);
    }
    
    @Override
    public void onTerminate() {
        super.onTerminate();
        XLog.i("wanges", "AIApplication onTerminate");
    }
    
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        XLog.i("wanges", "AIApplication onLowMemory");
    }

    @Override
    public void onTrimMemory(int level) {
        // TODO Auto-generated method stub
        super.onTrimMemory(level);
        XLog.i("wanges", "AIApplication onTrimMemory "+level);
    }
}
