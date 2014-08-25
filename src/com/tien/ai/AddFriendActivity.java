/**
 * 
 */
package com.tien.ai;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.xutil.XLog;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tien.ai.demain.Friend;
import com.tien.ai.fragment.ContactsFragment;
import com.tien.ai.fragment.SearchFriendFragment;
import com.tien.ai.listener.NetworkErrorResponeListener;
import com.tien.ai.utils.KeyboardHelper;
import com.tien.ai.utils.NetworkUtil;
import com.tien.volley.Response.Listener;
import com.tien.volley.toolbox.JsonObjectRequest;

/**
 * TODO
 * @author wangtianfei01
 *
 */
public class AddFriendActivity extends BaseActivity implements OnClickListener{
    
    private Button backBtn;
    private EditText searchET;
    private RelativeLayout addFriendRL;
    private ImageView searchIV;
    
    private ArrayList<Friend> friends = new ArrayList<Friend>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_friend_layout);
        findView();
        addListener();
    }
    
    private void findView(){
        this.backBtn = (Button) findViewById(R.id.back_btn);
        this.searchET = (EditText) findViewById(R.id.search_et);
        this.addFriendRL = (RelativeLayout) findViewById(R.id.add_friend_rl);
        this.searchIV = (ImageView) findViewById(R.id.search_iv);
    }
    
    private void addListener(){
        this.backBtn.setOnClickListener(this);
        this.addFriendRL.setOnClickListener(this);
        this.searchIV.setOnClickListener(this);
        this.searchET.setOnEditorActionListener(new OnEditorActionListener() {
            
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH){
                    searchFriend(v.getEditableText().toString().trim());
                    
                    KeyboardHelper.hideSoftKeyboard(AddFriendActivity.this, v);
                }
                return true;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                finish();
                break;
            case R.id.search_iv:
                searchFriend(searchET.getEditableText().toString().trim());
                break;
            case R.id.add_friend_rl:
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.animator.fragment_slide_left_enter,
                     R.animator.fragment_slide_left_exit,
                     R.animator.fragment_slide_right_enter,
                     R.animator.fragment_slide_right_exit);
                
                transaction.add(R.id.container, new ContactsFragment());
                transaction.addToBackStack("add_friend");
                transaction.commit();
                break;
            
            default:
                break;
        }
    }
    
    public ArrayList<Friend> getFriends(){
        return friends;
    }
    
    
    private void handleResult(JSONObject response){
        
        JSONArray arry = response.optJSONArray("data");
        Gson gson = new Gson();
        ArrayList<Friend> friendList =  ( ArrayList<Friend>)gson.fromJson(arry.toString(), new TypeToken<List<Friend>>(){}.getType());
        
        XLog.i("wanges", "friendList:"+friendList);
        this.friends = friendList;
        
        KeyboardHelper.hideSoftKeyboard(this, searchET);
        
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.animator.fragment_slide_left_enter,
             R.animator.fragment_slide_left_exit,
             R.animator.fragment_slide_right_enter,
             R.animator.fragment_slide_right_exit);
        
        transaction.add(R.id.container, new SearchFriendFragment());
        transaction.addToBackStack("search");
        transaction.commit();
    }
    
    private void searchFriend(String keyword){
        if(keyword == null || "".equals(keyword)) return;
        
        String url = Constant.URL_SEARCH_FRIEND;
        Map<String, String> params = NetworkUtil.initRequestParams();
        params.put("keyword", keyword);
        
        JsonObjectRequest request = new JsonObjectRequest(url, params, new Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                // TODO Auto-generated method stub
                
                handleResult(response);
            }
        }, new NetworkErrorResponeListener(this));
        
        request.setTag(this);
        AIApplication.getInstance().getRequestQueue().add(request);
    }
    
}
