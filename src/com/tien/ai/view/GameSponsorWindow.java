package com.tien.ai.view;

import java.util.Map;
import java.util.Random;

import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.tien.ai.AIApplication;
import com.tien.ai.Constant;
import com.tien.ai.R;
import com.tien.ai.db.DBManager;
import com.tien.ai.demain.Game;
import com.tien.ai.demain.GameData;
import com.tien.ai.listener.NetworkErrorResponeListener;
import com.tien.ai.utils.NetworkUtil;
import com.tien.ai.utils.PreferenceUtils;
import com.tien.ai.utils.ToastUtil;
import com.tien.volley.Response;
import com.tien.volley.VolleyError;
import com.tien.volley.toolbox.JsonObjectRequest;

/**
 * TODO
 * 
 * @author wangtianfei01
 * 
 */
public class GameSponsorWindow extends Dialog implements OnClickListener {
    
    public static final int TYPE_CYCLES = 1;
    public static final int TYPE_DICE = 2;
    
    private Context context;
    private ImageView gameIV;
    private Button playBtn;
    private Button sendBtn;
    private Button cancelBtn;
    private int type;
    private String sponsorData = "";
    private String gameName = "";
    private String friendUid = "";
    
    public GameSponsorWindow(Context context, int type , String friendUid) {
        super(context, R.style.NoTitleDialog);
        setContentView(R.layout.game_popupwindow);

        this.friendUid = friendUid;
        this.context = context;
        this.type = type;
        this.findView();
        this.addListener();
        
        this.setCancelable(false);
        
        if (type == TYPE_CYCLES) {
            gameIV.setBackgroundResource(R.drawable.jsb);
            gameName = "石头剪刀布";
        } else if (type == TYPE_DICE) {
            gameIV.setBackgroundResource(R.drawable.dice_1);
            gameName = "比大小";
        }
    }
    
    private void findView(){
        gameIV = (ImageView) findViewById(R.id.game_iv);
        playBtn = (Button) findViewById(R.id.play_btn);
        sendBtn = (Button) findViewById(R.id.send_btn);
        cancelBtn = (Button) findViewById(R.id.cancel_btn);
    }
    
    private void addListener(){
        playBtn.setOnClickListener(this);
        sendBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
    }
    
    
    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.play_btn){
            
            Random   rand   =   new   Random();   
            int delay = 3000 + rand.nextInt(1000);
            if (type == TYPE_CYCLES) {
                int num = rand.nextInt(3);
                int randomCycles = R.drawable.jsb_1 + num;
                runFrame("jsb_", 3, randomCycles, 300, delay);
                
                sponsorData = String.valueOf(num);
            } else if (type == TYPE_DICE) {
                int num = rand.nextInt(6);
                int randomCycles = R.drawable.dice_1 + num;
                runFrame("dice_action_", 4,  randomCycles, 150, delay);
                
                sponsorData = String.valueOf(num);
            }
            
        }else if(v.getId() == R.id.send_btn){
            String gameSequnce = gameName + System.currentTimeMillis() + new Random().nextInt();
            GameData game = new GameData(String.valueOf(type),gameName, sponsorData, "", gameSequnce);
            sendResult(game);
            
        }else if(v.getId() == R.id.cancel_btn){
            this.dismiss();
        }
    }
    
    
    
    public void stopFrame(View view) {  
        AnimationDrawable anim = (AnimationDrawable) gameIV.getBackground();
        if (anim.isRunning()) { //如果正在运行,就停止  
            anim.stop();  
        }  
    }  
      
    public void runFrame(String animRes, int n, final int randomCycles, int frameTime, long delayMillis) {  
        playBtn.setEnabled(false);
        //完全编码实现的动画效果  
        AnimationDrawable anim = new AnimationDrawable();  
        for (int i = 1; i <= n; i++) {  
            //根据资源名称和目录获取R.java中对应的资源ID  
            int id = getContext().getResources().getIdentifier(animRes + i, "drawable", getContext().getPackageName());  
            //根据资源ID获取到Drawable对象  
            Drawable drawable = getContext().getResources().getDrawable(id);  
            //将此帧添加到AnimationDrawable中  
            anim.addFrame(drawable, frameTime);  
        }  
        anim.setOneShot(false); //设置为loop  
        gameIV.setBackgroundDrawable(anim);  //将动画设置为ImageView背景  
        anim.start();   //开始动画  
        
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            
            @Override
            public void run() {
                stopFrame(gameIV);
                playBtn.setEnabled(true);
                playBtn.setVisibility(View.GONE);
                sendBtn.setVisibility(View.VISIBLE);
                gameIV.setBackgroundResource(randomCycles);
            }
        }, delayMillis);
    }  
    
    private void sendResult(final GameData gameData){
        
        String url = Constant.URL_SEND_PK_RESULT;
        Map<String, String> params = NetworkUtil.initRequestParams();
        params.put("friend_uid", friendUid);
        params.put("game_id", String.valueOf(type));
        params.put("game_sequence", gameData.getGameSequence());
        params.put("game_role", "0");
        params.put("game_status", "1");
        Gson gson = new Gson();
        final String gameDataStr = gson.toJson(gameData);
        params.put("game_data", gameDataStr);
        JsonObjectRequest request = new JsonObjectRequest(url, params, new Response.Listener<JSONObject>(){

            @Override
            public void onResponse(JSONObject response) {
                  int ret = response.optInt("ret");
                  if(ret == 0){
                     JSONObject data = response.optJSONObject("data");
                     DBManager.getInstance().insertGame(new Game(PreferenceUtils.loadUid(), friendUid, String.valueOf(type), gameData.getGameSequence(), "0", gameDataStr, "","", 1));
                     
                     dismiss();
                  }else{
                      JSONObject data = response.optJSONObject("data");
                      if(data != null){
                          String msg = data.optString("msg");
                          ToastUtil.shortToast(msg);
                      }else{
                      }
                  }
            }
            
        }, new NetworkErrorResponeListener((Activity)context) {

            @Override
            public void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
            }
        });
        //界面启动时优先使用缓存;
        request.setTag(this);
        AIApplication.getInstance().getRequestQueue().add(request);
    }
    
    public interface DataCallback {
        
        public void callback(Object obj);
    }
    
}
