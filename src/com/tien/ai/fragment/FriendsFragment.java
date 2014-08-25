package com.tien.ai.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tien.ai.AIApplication;
import com.tien.ai.AddFriendActivity;
import com.tien.ai.Constant;
import com.tien.ai.NotificationCenter;
import com.tien.ai.NotificationItem;
import com.tien.ai.R;
import com.tien.ai.adapter.FriendsAdapter;
import com.tien.ai.demain.Friend;
import com.tien.ai.listener.NetworkErrorResponeListener;
import com.tien.ai.utils.NetworkUtil;
import com.tien.ai.utils.NicknameComparator;
import com.tien.ai.utils.NicknameComparator1;
import com.tien.ai.utils.PinyinUtils;
import com.tien.ai.utils.ToastUtil;
import com.tien.ai.utils.XLog;
import com.tien.volley.Response;
import com.tien.volley.VolleyError;
import com.tien.volley.toolbox.JsonObjectRequest;

/**
 * 
 * <p>Title: ContactsFragment</p>
 * <p>Description:联系人列表界面 </p>
 * @author wangtf
 * @date 2014-1-15
 */
public class FriendsFragment extends Fragment implements Observer, OnClickListener{

	private Button addFriendBtn;
	private Button refershBtn;
	private ProgressBar loadingPB;
	private TextView tipTV;
	private ListView listView;
	private AutoCompleteTextView contactsSearchView;
	
	private FriendsAdapter friendsAdapter = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		NotificationCenter.defaultCenter().addObserver(this);
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.friends_view, container, false);
		findView(view);
		addListener();
		
		return view;
	}

	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		friendsAdapter = new FriendsAdapter(getActivity());
		listView.setAdapter(friendsAdapter);
		XLog.i("wanges", "FriendsFragment onActivityCreated");
		loadData();
	}

	
	@Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_friend_btn:
                Intent intent = new Intent(getActivity(), AddFriendActivity.class);
                getActivity().startActivity(intent);
                break;
            case R.id.refersh_btn:
                loadData();
                break;
            default:
                break;
        }
        
    }

	@Override
	public void onDestroy() {
	    super.onDestroy();
//	    NotificationCenter.defaultCenter().deleteObserver(this);
	    XLog.i("wanges", "ContactsFragment onDestroy()");
	}

	private void findView(View view) {
		addFriendBtn = (Button) view.findViewById(R.id.add_friend_btn);
		listView = (ListView) view.findViewById(R.id.contacts_lv);
		RelativeLayout empty = (RelativeLayout) view.findViewById(R.id.empty);
		refershBtn = (Button) view.findViewById(R.id.refersh_btn);
		loadingPB = (ProgressBar) view.findViewById(R.id.loading_pb);
		tipTV = (TextView) view.findViewById(R.id.tip_tv);
		
		listView.setEmptyView(empty);

	}


	public void addListener() {
	    addFriendBtn.setOnClickListener(this);
	    refershBtn.setOnClickListener(this);

//		contactsSearchView.addTextChangedListener(new TextWatcher() {
//
//			@Override
//			public void onTextChanged(CharSequence s, int start, int before,
//					int count) {
//				contactsAdapter.getFilter().filter(contactsSearchView.getText().toString());
//			}
//
//			@Override
//			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//			}
//
//			@Override
//			public void afterTextChanged(Editable s) {
//			}
//		});
	}

	@Override
    public void update(Observable observable, Object data) {
		if (data instanceof NotificationItem) {
			NotificationItem item = (NotificationItem) data;
			XLog.i("wanges", "NotificationItem "+item.getType());
			if (item.getType() == NotificationItem.TYPE_ADD_FRIEND ) {
			    loadData();
			} else if(item.getType() == NotificationItem.TYPE_MODIFY_USERINFO){
			    String uid = (String)item.getMessage();
			    XLog.i("wanges", "update uid:"+uid);
			    friendsAdapter.getRemindUids().add(uid);
			    loadData();
			    
			    
			} 
		}
		
    }

	private void loadData(){
	    
	    loadingPB.setVisibility(View.VISIBLE);
	    tipTV.setVisibility(View.GONE);
	    refershBtn.setVisibility(View.GONE);
	    
	    String url = Constant.URL_FRIEND_LIST; 
	    Map<String, String> params = NetworkUtil.initRequestParams();
	     JsonObjectRequest request = new JsonObjectRequest(url, params, new Response.Listener<JSONObject>(){

	         @Override
	         public void onResponse(JSONObject response) {
	             loadingPB.setVisibility(View.GONE);
	             tipTV.setVisibility(View.VISIBLE);
	             refershBtn.setVisibility(View.VISIBLE);
	             
	               int ret = response.optInt("ret");
	               if(ret == 0){
	                   JSONArray arry = response.optJSONArray("data");
	                  if(arry != null){
	                      Gson gson = new Gson();
	                      ArrayList<Friend> friendList =  ( ArrayList<Friend>)gson.fromJson(arry.toString(), new TypeToken<List<Friend>>(){}.getType());
	                      
	                      productPinyin(friendList);
	                      
	                      Collections.sort(friendList, new NicknameComparator1());
	                      friendsAdapter.setDatas(true, friendList);
	                      friendsAdapter.notifyDataSetChanged();
	                  }
	                  
	               }else{
	                   JSONObject data = response.optJSONObject("data");
	                   if(data != null){
	                       String msg = data.optString("msg");
	                       ToastUtil.shortToast(msg);
	                   }else{
//	                       ToastUtil.shortToast("登陆失败!");
	                   }
	               }
	         }
	         
	     }, new NetworkErrorResponeListener(getActivity()) {

	         @Override
	         public void onErrorResponse(VolleyError error) {
	             super.onErrorResponse(error);
	             
	             loadingPB.setVisibility(View.GONE);
	             tipTV.setVisibility(View.VISIBLE);
	             refershBtn.setVisibility(View.VISIBLE);
	         }
	     });
	     //界面启动时优先使用缓存;
	     request.setTag(this);
	     AIApplication.getInstance().getRequestQueue().add(request);
    }

	private void productPinyin(ArrayList<Friend> friendList){
	    for(Friend friend : friendList){
            try {
                String pinyin = PinyinUtils.getFirstHanyuPinyin(friend.getNickname());
                friend.setNicknamePinyin(pinyin);
                friend.setFirstChar(pinyin.toUpperCase().charAt(0));
            } catch (BadHanyuPinyinOutputFormatCombination e) {
                e.printStackTrace();
            }
	      
	    }
	}


}
