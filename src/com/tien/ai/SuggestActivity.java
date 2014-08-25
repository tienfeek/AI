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
import android.widget.EditText;

import com.tien.ai.utils.NetworkUtil;
import com.tien.ai.utils.PreferenceUtils;
import com.tien.ai.utils.ToastUtil;
import com.tien.volley.Response;
import com.tien.volley.VolleyError;
import com.tien.volley.toolbox.JsonObjectRequest;

/**
 * TODO
 * @author wangtianfei01
 *
 */
public class SuggestActivity extends BaseActivity implements OnClickListener{
    
    private Button backBtn;
    private Button suggestListBtn;
    private Button commitBtn;
    private EditText suggestET;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        this.setContentView(R.layout.suggest_layout);
        this.findView();
        this.addListener();
    }
    
    private void findView(){
        this.suggestET = (EditText) findViewById(R.id.suggest_et);
    }
    
    private void addListener(){
        findViewById(R.id.back_btn).setOnClickListener(this);
        findViewById(R.id.suggest_list_btn).setOnClickListener(this);
        findViewById(R.id.commit_btn).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.back_btn){
            finish();
        }else if(v.getId() ==  R.id.suggest_list_btn){
            Intent intent = new Intent(this, SuggestListActivity.class);
            startActivity(intent);
        }else if(v.getId() ==  R.id.commit_btn){
            String suggest = suggestET.getEditableText().toString().trim();
            if(suggest.length()==0){
                ToastUtil.shortToast("建议内容不能为空!");
                return;
            }
            suggestRequest(suggest);
        }
        
    }
    
    private void suggestRequest(String suggest){
        String url = Constant.URL_SUGGEST;
        Map<String,String> params = NetworkUtil.initRequestParams();
        params.put("suggest", suggest);
        params.put("uid", PreferenceUtils.loadUid());
        this.startLoading();
        JsonObjectRequest request = new JsonObjectRequest(url, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                endLoading();
               int ret = response.optInt("ret");
               if(ret == 0){
                   ToastUtil.shortToast("提交成功!");
               }else{
                   ToastUtil.shortToast("提交失败");
               }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                endLoading();
            }
        });
        
        AIApplication.getInstance().getRequestQueue().add(request);
    }

    
}
