/**
 * 
 */
package com.tien.ai.fragment;

import java.util.ArrayList;
import java.util.Map;

import org.json.JSONObject;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.tien.ai.AIApplication;
import com.tien.ai.AddFriendActivity;
import com.tien.ai.Constant;
import com.tien.ai.LoginActivity;
import com.tien.ai.MainActivity;
import com.tien.ai.NotificationCenter;
import com.tien.ai.NotificationItem;
import com.tien.ai.R;
import com.tien.ai.demain.Friend;
import com.tien.ai.listener.NetworkErrorResponeListener;
import com.tien.ai.utils.NetworkUtil;
import com.tien.ai.utils.PreferenceUtils;
import com.tien.ai.utils.ToastUtil;
import com.tien.volley.Response;
import com.tien.volley.VolleyError;
import com.tien.volley.toolbox.JsonObjectRequest;


public class SearchFriendFragment extends Fragment implements OnClickListener{
    private Button backBtn;
    private ListView searchResultLV;
    
   
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_friend_view, null);
        findView(view);
        return view;
    }
    
    private void findView(View view){
        this.backBtn = (Button)view.findViewById(R.id.back_btn);
        this.searchResultLV = (ListView)view.findViewById(R.id.search_result_lv);
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        addListener();
        
        ArrayList<Friend> friends = ((AddFriendActivity)getActivity()).getFriends();
        SearchResultAdapter adapter = new SearchResultAdapter(friends);
        this.searchResultLV.setAdapter(adapter);
    }
    
    private void addListener(){
        this.backBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
       if(v.getId() == R.id.back_btn){
           getFragmentManager().popBackStack();
       }
    }
    
    
    class SearchResultAdapter extends BaseAdapter{
        
        ArrayList<Friend> friends = new ArrayList<Friend>();
        
        public SearchResultAdapter(ArrayList<Friend> friends){
            this.friends = friends;
        }
        
        @Override
        public int getCount() {
            return friends.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            
            ViewHolder holder = null;
            if(convertView == null){
                holder = new ViewHolder();
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.search_result_listview_item, null);
                holder.avatarIV = (ImageView) convertView.findViewById(R.id.avatar_iv);
                holder.nicknameTV = (TextView) convertView.findViewById(R.id.nickname_tv);
                holder.addTV = (Button) convertView.findViewById(R.id.add_btn);
                
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }
            
             final Friend friend = friends.get(position);
             holder.nicknameTV.setText(friend.getNickname());
            
             holder.addTV.setOnClickListener(new OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    addFriend(friend.getUid());
                }
            });
            
            return convertView;
        }
    }
    
    static class ViewHolder{
        ImageView avatarIV;
        TextView  nicknameTV;
        Button  addTV;
    } 
    
    
    private void addFriend(String friendUid){
        String url = Constant.URL_ADD_FRIEND;
        Map<String, String> params = NetworkUtil.initRequestParams();
        params.put("friend_uid", friendUid);
        JsonObjectRequest request = new JsonObjectRequest(url, params, new Response.Listener<JSONObject>(){

            @Override
            public void onResponse(JSONObject response) {
                  int ret = response.optInt("ret");
                  if(ret == 0){
                     JSONObject data = response.optJSONObject("data");
                     if(data != null){
                         ToastUtil.shortToast("添加成功!");
                        NotificationCenter.defaultCenter().postNotification(new NotificationItem(NotificationItem.TYPE_ADD_FRIEND, ""));
                        getActivity().finish();
                     }
                     
                  }else{
                      JSONObject data = response.optJSONObject("data");
                      if(data != null){
                          String msg = data.optString("msg");
                          ToastUtil.shortToast(msg);
                      }else{
                          ToastUtil.shortToast("添加失败!");
                      }
                  }
            }
            
        }, new NetworkErrorResponeListener(getActivity()) {

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
