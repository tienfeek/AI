package com.tien.ai.view;

import java.util.Map;
import java.util.Random;

import org.json.JSONObject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.xutil.XLog;

import com.google.gson.Gson;
import com.tien.ai.AIApplication;
import com.tien.ai.Constant;
import com.tien.ai.NotificationCenter;
import com.tien.ai.NotificationItem;
import com.tien.ai.R;
import com.tien.ai.db.DBManager;
import com.tien.ai.demain.Game;
import com.tien.ai.demain.GameData;
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
public class GameReceiverWindow extends Dialog implements OnClickListener {
    
    public static final int TYPE_CYCLES = 1;
    public static final int TYPE_DICE = 2;
    
    private Context context;
    private ImageView meIV;
    private ImageView opponentIV;
    private ImageView vsIV;
    private LinearLayout fristLL;
    private LinearLayout secondLL;
    private Button playBtn;
    private Button cancelBtn;
    private Button playAgainBtn;
    private Button closeBtn;
    private int type;
    private String sponsorData = "";
    
    private int meRet;
    private int opponentRet;
    private Game game;
    private GameData gameData;
    private String friendUid = "";
    
    public GameReceiverWindow(Context context, Game game, String friendUid) {
        super(context, R.style.NoTitleDialog);
        setContentView(R.layout.game_recevier_popupwindow);
        
        this.friendUid = friendUid;

        this.context = context;
        this.findView();
        this.addListener();
        
        this.setCancelable(false);
        
        this.game = game;
        
        this.type = Integer.parseInt(game.getGameId());
        
        String gameDataStr = game.getGameData();
        
        Gson gson = new Gson();
        gameData = gson.fromJson(gameDataStr, GameData.class);
        try {
            String meRetStr = gameData.getOpponentData();
            if(meRetStr != null && !"".equals(meRetStr)){
                meRet = Integer.parseInt(meRetStr);
            }
            opponentRet = Integer.parseInt(gameData.getMeData());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        
        if (type == TYPE_CYCLES) {
            if(game.getGameStatus() == 2){
                meIV.setBackgroundResource(R.drawable.jsb + meRet);
            }else{
                meIV.setBackgroundResource(R.drawable.jsb);
            }
            opponentIV.setBackgroundResource((R.drawable.jsb_1 + opponentRet));
        } else if (type == TYPE_DICE) {
            if(game.getGameStatus() == 2){
                meIV.setBackgroundResource(R.drawable.dice_1 + meRet);
            }else{
                meIV.setBackgroundResource(R.drawable.dice_1);
            }
            opponentIV.setBackgroundResource(R.drawable.dice_1 + opponentRet);
        }
        
        if(game.getGameStatus() == 2){
            playBtn.setVisibility(View.GONE);
            displayRetAnimation(judgeWinLos());
        }
    }
    
    private void findView(){
        meIV = (ImageView) findViewById(R.id.me_iv);
        opponentIV = (ImageView) findViewById(R.id.opponent_iv);
        vsIV = (ImageView) findViewById(R.id.vs_iv);
        fristLL = (LinearLayout) findViewById(R.id.frist_ll);
        secondLL = (LinearLayout) findViewById(R.id.second_ll);
        playBtn = (Button) findViewById(R.id.play_btn);
        cancelBtn = (Button) findViewById(R.id.cancel_btn);
        playAgainBtn = (Button) findViewById(R.id.play_again_btn);
        closeBtn = (Button) findViewById(R.id.close_btn);
    }
    
    private void addListener(){
        playBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
        playAgainBtn.setOnClickListener(this);
        closeBtn.setOnClickListener(this);
    }
    
    
    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.play_btn){
            
            Random   rand   =   new   Random();   
            int delay = 3000 + rand.nextInt(1000);
            if (type == TYPE_CYCLES) {
                meRet = rand.nextInt(3);
                int randomCycles = R.drawable.jsb_1 + meRet;
                runFrame("jsb_", 3, randomCycles, 300, delay);
                
            } else if (type == TYPE_DICE) {
                int num = rand.nextInt(6);
                int randomCycles = R.drawable.dice_1 + num;
                runFrame("dice_action_", 4,  randomCycles, 150, delay);
            }
            
        }else if(v.getId() == R.id.cancel_btn){
            this.dismiss();
        }else if(v.getId() == R.id.play_again_btn){
            
        }else if(v.getId() == R.id.close_btn){
            this.dismiss();
        }
    }
    
    
    
    public void stopFrame(View view) {  
        AnimationDrawable anim = (AnimationDrawable) meIV.getBackground();
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
        meIV.setBackgroundDrawable(anim);  //将动画设置为ImageView背景  
        anim.start();   //开始动画  
        
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            
            @Override
            public void run() {
                stopFrame(meIV);
                playBtn.setEnabled(true);
                
                meIV.setBackgroundResource(randomCycles);
                
                displayRetAnimation(judgeWinLos());
                
                sendResult();
            }
        }, delayMillis);
    } 
    
    
    public int judgeWinLos(){
        if(type == TYPE_CYCLES){
            return judgeCycles();
        }else if(type == TYPE_DICE){
            return judgeDice();
        }
        return 0;
    }
    
    public int judgeCycles(){
        if(meRet == opponentRet){
            //平
            gameData.setWinUid("");
            return 1;
        }else if(meRet < opponentRet){
            if(meRet == 0 && opponentRet ==2){
                gameData.setWinUid(game.getToUid());
                return 0;
            }else{
                gameData.setWinUid(friendUid);
                return 2;
            }
        }else if(meRet > opponentRet){
            if(opponentRet == 0 && meRet ==2){
                gameData.setWinUid(friendUid);
                return 2;
            }else{
                gameData.setWinUid(game.getToUid());
                return 0;
            }
        }
        return 0;
    }
    
    public int judgeDice(){
        if(meRet == opponentRet){
            //平
            gameData.setWinUid("");
            return 1;
        }else if(meRet < opponentRet){
            gameData.setWinUid(friendUid);
            return 2;
        }else if(meRet > opponentRet){
            gameData.setWinUid(game.getToUid()); 
            return 0;
        }
        return 0;
    }
    
    
    private void displayRetAnimation(int ret){
        if(ret == 0){
           anmiante(R.drawable.rkgk_compliance_win_2);
        }else if(ret == 1){
            anmiante(R.drawable.rkgk_compliance_win_2);
        }else if(ret == 2){
            anmiante(R.drawable.rkgk_compliance_lose_2);
        }
    }
    
    private void anmiante(final int resId){
        
        PropertyValuesHolder pvhScaleX = PropertyValuesHolder.ofFloat("scaleX", 0.1f);
        PropertyValuesHolder pvhScaleY = PropertyValuesHolder.ofFloat("scaleY", 0.1f);
        PropertyValuesHolder pvhRotation = PropertyValuesHolder.ofFloat("rotation", 720);
        ObjectAnimator startAnimator = ObjectAnimator.ofPropertyValuesHolder(vsIV, pvhScaleX, pvhScaleY, pvhRotation);
        startAnimator.setDuration(1000);
        startAnimator.start();
        
        startAnimator.addListener(new AnimatorListenerAdapter() {
            
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                vsIV.setImageResource(resId);
                
                PropertyValuesHolder pvhScaleX = PropertyValuesHolder.ofFloat("scaleX", 1);
                PropertyValuesHolder pvhScaleY = PropertyValuesHolder.ofFloat("scaleY", 1);
                PropertyValuesHolder pvhRotation = PropertyValuesHolder.ofFloat("rotation", 0);
                ObjectAnimator endAnimator = ObjectAnimator.ofPropertyValuesHolder(vsIV, pvhScaleX, pvhScaleY, pvhRotation);
                endAnimator.setDuration(1000);
                endAnimator.start();
                
                endAnimator.addListener(new AnimatorListenerAdapter() {
                    
                    
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        fristLL.setVisibility(View.GONE);
                        secondLL.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
       
    }
    
    private void sendResult(){
        gameData.setOpponentData(String.valueOf(opponentRet));
        gameData.setMeData(String.valueOf(meRet));
        
        String url = Constant.URL_SEND_PK_RESULT;
        Map<String, String> params = NetworkUtil.initRequestParams();
        XLog.i("wanges", "friendUid:"+friendUid +"  meuid:"+PreferenceUtils.loadUid());
        
        params.put("friend_uid", friendUid);
        //params.put("to_uid", game.getFromUid());
        params.put("game_id", String.valueOf(type));
        params.put("game_role", "1");
        params.put("game_sequence", gameData.getGameSequence());
        params.put("game_status", "2");
        params.put("win_uid", gameData.getWinUid());
        Gson gson = new Gson();
        params.put("game_data", gson.toJson(gameData));
        //params.put("game_url", game.getGameUrl());
        JsonObjectRequest request = new JsonObjectRequest(url, params, new Response.Listener<JSONObject>(){

            @Override
            public void onResponse(JSONObject response) {
                  int ret = response.optInt("ret");
                  if(ret == 0){
                     JSONObject data = response.optJSONObject("data");
                     DBManager.getInstance().updateStatus(gameData.getGameSequence(), 2);
                     NotificationCenter.defaultCenter().postNotification(new NotificationItem(NotificationItem.TYPE_GAME, game.getGameSequence()));
                  }else{
                      JSONObject data = response.optJSONObject("data");
                      if(data != null){
                          String msg = data.optString("msg");
                          ToastUtil.shortToast(msg);
                      }else{
                      }
                  }
            }
            
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                
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
