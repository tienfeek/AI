/**
 * 
 */
package com.tien.ai;

import java.util.Map;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.tien.ai.db.DBManager;
import com.tien.ai.demain.UserInfo;
import com.tien.ai.listener.NetworkErrorResponeListener;
import com.tien.ai.utils.NetworkUtil;
import com.tien.ai.utils.ToastUtil;
import com.tien.ai.view.GameSponsorWindow;
import com.tien.volley.Response;
import com.tien.volley.VolleyError;
import com.tien.volley.imagecache.ImageCacheManager;
import com.tien.volley.toolbox.JsonObjectRequest;
import com.tien.volley.toolbox.NetworkImageView;

public class UserActivity extends BaseActivity implements OnClickListener{
    
    private ScrollView container;
    private Button backBtn;
    private NetworkImageView avatarIV;
    private TextView nicknameTV;
    private TextView moodTV;
    private Button aiBtn;
    private ImageView cyclesIV;
    private ImageView diceIV;
    
    
    
    private UserInfo user;
    private String uid = "";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.user_layout);
        parseIntent();
        findView();
        addListener();
        loadData(uid);
        
    }
    
    private void parseIntent(){
        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");
    }
    
    
    private void findView(){
        this.container = (ScrollView) findViewById(R.id.container);
        this.backBtn = (Button) findViewById(R.id.back_btn);
        this.avatarIV = (NetworkImageView) findViewById(R.id.avator_iv);
        this.nicknameTV = (TextView) findViewById(R.id.nickname_tv);
        this.moodTV = (TextView) findViewById(R.id.mood_tv);
        this.aiBtn = (Button) findViewById(R.id.ai_btn);
        this.cyclesIV = (ImageView) findViewById(R.id.cycles_iv);
        this.diceIV = (ImageView) findViewById(R.id.dice_iv);
    }
    
    private void addListener(){
        this.backBtn.setOnClickListener(this);
        this.aiBtn.setOnClickListener(this);
        this.cyclesIV.setOnClickListener(this);
        this.diceIV.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
       if(v.getId() == R.id.back_btn){
           finish();
       }else if(v.getId() == R.id.ai_btn){
           if(user==null)  return;
           sendAI(user.getUid(), user.getNickname());
        }else if(v.getId() == R.id.cycles_iv){
            GameSponsorWindow popupWindow = new GameSponsorWindow(this, GameSponsorWindow.TYPE_CYCLES, uid);
            popupWindow.show();
        }else if(v.getId() == R.id.dice_iv){
            GameSponsorWindow popupWindow = new GameSponsorWindow(this, GameSponsorWindow.TYPE_DICE, uid);
            popupWindow.show();
        }
        
    }
    
    private void updateUI(){
        this.nicknameTV.setText(user.getNickname());
        if(!"".equals(user.getMood())){
            this.moodTV.setText(user.getMood());
        }
        
        String avatar = user.getAvatar();
        if(avatar == null || "".equals(avatar)) return;
        
        avatarIV.setErrorImageResId(R.drawable.avatar_default);
        avatarIV.setImageResource(R.drawable.avatar_default);
        avatarIV.setDefaultImageResId(R.drawable.avatar_default);
        avatarIV.setImageUrl(avatar, ImageCacheManager.getInstance().getImageLoader());
        
    }
    
    private void loadData(String uid){
        
        String url = Constant.URL_USER_INFO;
        Map<String, String> params = NetworkUtil.initRequestParams();
        params.put("uid", uid);
        JsonObjectRequest request = new JsonObjectRequest(url, params, new Response.Listener<JSONObject>(){

            @Override
            public void onResponse(JSONObject response) {
                  int ret = response.optInt("ret");
                  if(ret == 0){
                     JSONObject data = response.optJSONObject("data");
                     if(data != null){
                         Gson gson = new Gson();
                         user = gson.fromJson(data.toString(), UserInfo.class);
                         updateUI();
                     }
                     
                  }else{
                      JSONObject data = response.optJSONObject("data");
                      if(data != null){
                          String msg = data.optString("msg");
                          ToastUtil.shortToast(msg);
                      }else{
                      }
                  }
            }
            
        }, new NetworkErrorResponeListener(this) {

            @Override
            public void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
            }
        });
        //界面启动时优先使用缓存;
        request.setTag(this);
        AIApplication.getInstance().getRequestQueue().add(request);
    }
    
    
    private void sendAI(final String friendUid, final String friendNickname){
        
        String url = Constant.URL_SEND_AI;
        Map<String, String> params = NetworkUtil.initRequestParams();
        params.put("friend_uid", friendUid);
        JsonObjectRequest request = new JsonObjectRequest(url, params, new Response.Listener<JSONObject>(){

            @Override
            public void onResponse(JSONObject response) {
                  int ret = response.optInt("ret");
                  if(ret == 0){
                     JSONObject data = response.optJSONObject("data");
                     if(data != null){
                         ToastUtil.shortToast("发送成功！");
                         DBManager.getInstance().add(friendUid,friendNickname , 0);
                         NotificationCenter.defaultCenter().postNotification(new NotificationItem(NotificationItem.TYPE_SEND_AI, ""));
                     }
                     
                  }else{
                      JSONObject data = response.optJSONObject("data");
                      if(data != null){
                          String msg = data.optString("msg");
                          ToastUtil.shortToast(msg);
                      }else{
                      }
                  }
            }
            
        }, new NetworkErrorResponeListener(this) {

            @Override
            public void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
            }
        });
        //界面启动时优先使用缓存;
        request.setTag(this);
        AIApplication.getInstance().getRequestQueue().add(request);
    }
    
}
