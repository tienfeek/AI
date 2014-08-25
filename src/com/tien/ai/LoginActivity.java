/**
 * 
 */
package com.tien.ai;

import java.util.Map;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.tien.ai.listener.NetworkErrorResponeListener;
import com.tien.ai.utils.NetworkUtil;
import com.tien.ai.utils.PreferenceUtils;
import com.tien.ai.utils.ToastUtil;
import com.tien.ai.utils.XLog;
import com.tien.volley.Response;
import com.tien.volley.VolleyError;
import com.tien.volley.toolbox.JsonObjectRequest;


public class LoginActivity extends BaseActivity implements OnClickListener{
    
    private EditText mPhoneET;
    private EditText mPassowrdET;
    private Button mLoginBtn;
    private Button mRegBtn;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        if(!"".equals(PreferenceUtils.loadToken())){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
       
        
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.login_layout);
        
        this.findView();
        this.addListener();
        if(!Utils.hasBind(this)){
            this.initWithApiKey();
        }
        
        TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        String tel = tm.getLine1Number();//手机号码
        if(tel != null && tel.trim().length()>0){
            if(tel.startsWith("+86")){
                mPhoneET.setText(tel.substring(3));
            }
        }
    }
    
    
    private void findView(){
        this.mPhoneET = (EditText)findViewById(R.id.phone_et);
        this.mPassowrdET = (EditText)findViewById(R.id.password_et);
        this.mLoginBtn = (Button)findViewById(R.id.login_btn);
        this.mRegBtn = (Button)findViewById(R.id.regster_btn);
    }
    
    private void addListener(){
        this.mLoginBtn.setOnClickListener(this);
        this.mRegBtn.setOnClickListener(this);
    }
    
    // 以apikey的方式绑定
    private void initWithApiKey() {
        // Push: 无账号初始化，用api key绑定
        PushManager.startWork(getApplicationContext(),
                PushConstants.LOGIN_TYPE_API_KEY,
                Utils.getMetaValue(LoginActivity.this, "api_key"));
    }


    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.login_btn:
            	
            	if(!NetworkUtil.isNetworkConnected()){
            		ToastUtil.shortToast("没有网络连接，请检查网络设置！");
            		return;
            	}
            	
            	if(!Utils.hasBind(this)){
            		initWithApiKey();
            		ToastUtil.shortToast("绑定推送失败，正在重新绑定，请再次点击登录!");
            		return;
            	}
               
                String phone = mPhoneET.getEditableText().toString().trim();
                String password = mPassowrdET.getEditableText().toString().trim();
                if(!invidateVerify(phone, password)){
                    return;
                }
                
                login(phone, password);
                break;
            case R.id.regster_btn:
                intent = new Intent(LoginActivity.this, RegsterActivity.class);
                startActivityForResult(intent,2);
                break;
            
            default:
                break;
        }
    }
    
    private boolean invidateVerify(String phone, String password){
        if(phone.length() == 0){
            ToastUtil.shortToast("用户名不能为空!");
            return false;
        }
        if(password.length() == 0){
            ToastUtil.shortToast("密码不能为空!");
            return false;
        }
        return true;
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 2 && resultCode == RESULT_OK){
            String phone = data.getStringExtra("phone");
            String password = data.getStringExtra("password");
            
            login(phone, password);
        }
    }
    
    
    private void login(final String phone, String password){
        String url = Constant.URL_LOGIN;
        
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        boolean bindFlag = sp.getBoolean("bind_flag", false);
        
        if(!bindFlag){
            
        }
        
        String bdUid = sp.getString("bd_uid", "");
        String channelId = sp.getString("channel_id", "");
        
        XLog.i("wanges", "bd_uid:"+bdUid+" channel_id:"+channelId);

        Map<String, String> params = NetworkUtil.initRequestParams();
        params.clear();
        params.put("phone", phone);
        params.put("password", password);
        params.put("bd_uid", bdUid);
        params.put("bd_channel_id", channelId);
        JsonObjectRequest request = new JsonObjectRequest(url, params, new Response.Listener<JSONObject>(){

            @Override
            public void onResponse(JSONObject response) {
                  int ret = response.optInt("ret");
                  if(ret == 0){
                     JSONObject data = response.optJSONObject("data");
                     XLog.i("wanges","login success");
                     if(data != null){
                         String uid = data.optString("uid");
                         String token = data.optString("token");
                         
                         PreferenceUtils.saveUidAndToken(uid, token, phone);
                         Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                         startActivity(intent);
                         finish();
                     }
                     
                  }else{
                      JSONObject data = response.optJSONObject("data");
                      if(data != null){
                          String msg = data.optString("msg");
                          ToastUtil.shortToast(msg);
                      }else{
                          ToastUtil.shortToast("登陆失败!");
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
