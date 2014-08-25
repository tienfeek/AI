/**
 * 
 */
package com.tien.ai;

import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.tien.ai.listener.NetworkErrorResponeListener;
import com.tien.ai.utils.NetworkUtil;
import com.tien.ai.utils.ToastUtil;
import com.tien.volley.Response;
import com.tien.volley.VolleyError;
import com.tien.volley.toolbox.JsonObjectRequest;

/**
 * TODO
 * @author wangtianfei01
 *
 */
public class RegsterActivity extends Activity implements OnClickListener{
    
   
    private EditText phoneET;
    private EditText nicknameET;
    private EditText passwordET;
    private EditText repasswordET;
    private Button regBtn;
    private Button backBtn;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.regster_layout);
        findView();
        addListener();
        setPhone();
    }
    
    private void findView(){
        this.phoneET = (EditText) findViewById(R.id.phone_et);
        this.nicknameET = (EditText) findViewById(R.id.nickname_et);
        this.passwordET = (EditText) findViewById(R.id.password_et);
        this.repasswordET = (EditText) findViewById(R.id.repassword_et);
        this.regBtn = (Button) findViewById(R.id.regster_btn);
        this.backBtn = (Button) findViewById(R.id.back_btn);
    }
    
    private void addListener(){
        this.backBtn.setOnClickListener(this);
        this.regBtn.setOnClickListener(this);
    }
    
    private void setPhone(){
        TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        String tel = tm.getLine1Number();//手机号码
        if(tel != null && tel.trim().length()>0){
            if(tel.startsWith("+86")){
                phoneET.setText(tel.substring(3));
            }
           
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.back_btn){
            finish();
        }else if(v.getId() == R.id.regster_btn){
            checkRegInfo();
            reg();
        }
        
    }
    
    
    private void checkRegInfo(){
        if(phoneET.getEditableText().toString().trim().length() == 0){
            ToastUtil.shortToast("电话号码不能为空!");
            return;
        }
        
        if(nicknameET.getEditableText().toString().trim().length() == 0){
            ToastUtil.shortToast("昵称不能为空!");
            return;
        }
        
        String password = passwordET.getEditableText().toString().trim();
        String repassword = repasswordET.getEditableText().toString().trim();
        if(password.length() == 0){
            ToastUtil.shortToast("密码不能为空!");
            return;
        }
        
        if(repassword.length() == 0){
            ToastUtil.shortToast("确认密码不能为空!");
            return;
        }
        
        if(!repassword.equals(password)){
            ToastUtil.shortToast("两次密码输入不一致!");
            return;
        }
    }
    
    private void reg(){
        String url = Constant.URL_REG;
        final String phone = phoneET.getEditableText().toString().trim();
        final String nickname = nicknameET.getEditableText().toString().trim();
        final String password = passwordET.getEditableText().toString().trim();
        
        Map<String, String> params = NetworkUtil.initRequestParams();
        params.put("phone", phone);
        params.put("nickname", nicknameET.getEditableText().toString().trim());
        params.put("password", passwordET.getEditableText().toString().trim());
        JsonObjectRequest request = new JsonObjectRequest(url, params, new Response.Listener<JSONObject>(){

            @Override
            public void onResponse(JSONObject response) {
                  int ret = response.optInt("ret");
                  if(ret == 0){
                     JSONObject data = response.optJSONObject("data");
                     if(data != null){
                         String uid = data.optString("uid");
                         Intent intent = new Intent();
                         intent.putExtra("phone", phone);
                         intent.putExtra("nickname", nickname);
                         intent.putExtra("password", password);
                         setResult(RESULT_OK, intent);
                         finish();
                     }
                     
                  }else{
                      JSONObject data = response.optJSONObject("data");
                      if(data != null){
                          String msg = data.optString("msg");
                          ToastUtil.shortToast(msg);
                      }else{
                          ToastUtil.shortToast("注册失败!");
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
