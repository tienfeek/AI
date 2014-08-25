package com.tien.ai;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.json.JSONObject;

import android.app.AlarmManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.google.gson.Gson;
import com.tien.ai.demain.Upgrade;
import com.tien.ai.fragment.AiListFragment;
import com.tien.ai.fragment.FriendsFragment;
import com.tien.ai.fragment.MeFragment;
import com.tien.ai.receiver.AlarmReceiver;
import com.tien.ai.utils.NetworkUtil;
import com.tien.ai.utils.UpgradeUtil;
import com.tien.ai.view.UpgradeDialog;
import com.tien.volley.Response;
import com.tien.volley.Response.ErrorListener;
import com.tien.volley.VolleyError;
import com.tien.volley.toolbox.JsonObjectRequest;

public class MainActivity extends BaseActivity implements OnClickListener, Observer{
    
    private int mCurrentTabIndex = 0;
    private TabInfo mCurrentTab = null;
    private final HashMap<Integer,TabInfo> mTabs = new HashMap<Integer,TabInfo>();
    private HashMap<Integer, Integer[]> btnStatus = new HashMap<Integer, Integer[]>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);

		findview();
		addFragment();
		doTabChanged(R.id.ai_btn);
		
		upgradeCheck();
		
		alarmTask();
		
	}
	

    @Override
    protected void onNewIntent(Intent intent) {
        String action = intent.getAction();

    }
    

    private void findview(){
        findViewById(R.id.contact_btn).setOnClickListener(this);
        findViewById(R.id.ai_btn).setOnClickListener(this);
        findViewById(R.id.me_btn).setOnClickListener(this);
        
        btnStatus.put(R.id.contact_btn, new Integer[]{R.drawable.bg_main_bottom_bg,R.drawable.bg_main_tab_pressed});
        btnStatus.put(R.id.ai_btn,  new Integer[]{R.drawable.bg_main_bottom_bg,R.drawable.bg_main_tab_pressed});
        btnStatus.put(R.id.me_btn,  new Integer[]{R.drawable.bg_main_bottom_bg,R.drawable.bg_main_tab_pressed});
    }
    
    private void addFragment(){
        TabInfo friendTabInfo = new TabInfo(FriendsFragment.class.getName(), FriendsFragment.class, null);
//        friendTabInfo.fragment = Fragment.instantiate(this, friendTabInfo.clss.getName(), friendTabInfo.args);
        mTabs.put(R.id.contact_btn, friendTabInfo);
        
        mTabs.put(R.id.ai_btn,new TabInfo(AiListFragment.class.getName(), AiListFragment.class, null));
        mTabs.put(R.id.me_btn,new TabInfo(MeFragment.class.getName(), MeFragment.class, null));
    }
    
    @Override
    public void onClick(View v) {
        
        if(v.getId() == R.id.contact_btn){
            doTabChanged(R.id.contact_btn);
        }else if(v.getId() == R.id.ai_btn){
            doTabChanged(R.id.ai_btn);
        }else if(v.getId() == R.id.me_btn){
            doTabChanged(R.id.me_btn);
        }
        
    }
    
    
    private void doTabChanged(int index) {
        if(index == mCurrentTabIndex){
            return;
        }
        
        findViewById(index).setBackgroundResource(btnStatus.get(index)[1]);
        if(mCurrentTabIndex != 0){
            findViewById(mCurrentTabIndex).setBackgroundResource(btnStatus.get(mCurrentTabIndex)[0]);
        }
        
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if (mCurrentTab != null && mCurrentTab.fragment != null) {
            ft.hide(mCurrentTab.fragment);
        }
        TabInfo newTab = mTabs.get(index);
        
//        if(newTab.clss == FriendsFragment.class){
//            ft.add(R.id.container, newTab.fragment, newTab.tag);
//        }
        if (newTab.fragment == null) {
            newTab.fragment = Fragment.instantiate(this, newTab.clss.getName(), newTab.args);
            ft.add(R.id.container, newTab.fragment, newTab.tag);
        } else {
            ft.show(newTab.fragment);
        }
        mCurrentTab = newTab;
        mCurrentTabIndex = index;
        ft.commit();
    }
    
    
    static final class TabInfo {
        private final String tag;
        private final Class<?> clss;
        private final Bundle args;
        private Fragment fragment = null;

        TabInfo(String tag, Class<?> clss, Bundle args) {
            this.tag = tag;
            this.clss = clss;
            this.args = args;
        }
    }

    private void upgradeCheck(){
        
        Map<String,String> params =  NetworkUtil.initRequestParams();
        int versionCode = UpgradeUtil.getVerCode(getApplicationContext());
        params.put("version",String.valueOf(versionCode));
        
        String url = Constant.URL_UPGRADE;
        JsonObjectRequest request = new JsonObjectRequest(url, params, new Response.Listener<JSONObject>(){

            @Override
            public void onResponse(JSONObject response) {
                
                JSONObject data = response.optJSONObject("data");
                if(data != null){
                    Gson gson = new Gson();
                    Upgrade upgrade = gson.fromJson(data.toString(), Upgrade.class);
                    if(upgrade.isUpdate()){
                        UpgradeDialog dialog = new UpgradeDialog(MainActivity.this, upgrade);
                        dialog.show();
                    }
                    
                    
                }
            }

        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        
        AIApplication.getInstance().getRequestQueue().add(request);
    }
    
    
    
    private void alarmTask(){
        Intent intent = new Intent("com.tien.ai.alarm");
        intent.setClass(this, AlarmReceiver.class);
 
        PendingIntent pi=PendingIntent.getBroadcast(this, 0, intent,0);
        //设置一个PendingIntent对象，发送广播
        AlarmManager am=(AlarmManager)getSystemService(ALARM_SERVICE);
        
        am.cancel(pi);
        //获取AlarmManager对象
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 20*60*1000, pi);
    }
    
    
    @Override
    public void update(Observable observable, Object data) {
        if (data instanceof NotificationItem) {
            NotificationItem item = (NotificationItem) data;
            if (item.getType() == NotificationItem.TYPE_ADD_FRIEND ) {
                doTabChanged(R.id.contact_btn);
            }  
        }
        
    }

}
