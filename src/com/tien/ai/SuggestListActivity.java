/**
 * 
 */
package com.tien.ai;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tien.ai.adapter.SuggestAdapter;
import com.tien.ai.demain.Suggest;
import com.tien.ai.listener.NetworkErrorResponeListener;
import com.tien.ai.utils.NetworkUtil;
import com.tien.volley.Response;
import com.tien.volley.VolleyError;
import com.tien.volley.toolbox.JsonObjectRequest;

/**
 * TODO
 * @author wangtianfei01
 *
 */
public class SuggestListActivity extends BaseActivity implements OnClickListener{
    
    private ListView suggestLV;
    private SuggestAdapter suggestAdapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        this.setContentView(R.layout.suggest_list_layout);
        this.findView();
        this.addListener();
        this.loadData();
        
        this.suggestAdapter = new SuggestAdapter(this);
        this.suggestLV.setAdapter(suggestAdapter);
    }
    
    private void findView(){
        this.suggestLV = (ListView) findViewById(R.id.suggest_lv);
    }
    
    private void addListener(){
        findViewById(R.id.back_btn).setOnClickListener(this);
    }
    
    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.back_btn){
            finish();
        }
    }
    
    private void loadData(){
        String url = Constant.URL_SUGGEST_LIST;
        Map<String, String> params = NetworkUtil.initRequestParams();
        
        JsonObjectRequest request = new JsonObjectRequest(url, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                int ret = response.optInt("ret");
                if(ret == 0){
                    JSONArray array = response.optJSONArray("data");
                    if(array != null){
                        Gson gson = new Gson();
                        List<Suggest> newSuggests = gson.fromJson(array.toString(), new TypeToken<List<Suggest>>(){}.getType());
                        if(newSuggests != null && newSuggests.size() > 0){
                            suggestAdapter.setData(newSuggests);
                            suggestAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        }, new NetworkErrorResponeListener(this){
            @Override
            public void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
            }
        });
        
        request.setTag(this);
        AIApplication.getInstance().getRequestQueue().add(request);
    }
    
}
