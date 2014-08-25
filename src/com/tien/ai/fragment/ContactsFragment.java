package com.tien.ai.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tien.ai.AIApplication;
import com.tien.ai.Constant;
import com.tien.ai.R;
import com.tien.ai.adapter.ContactsAdapter;
import com.tien.ai.callback.DataCallback;
import com.tien.ai.db.SystemDB;
import com.tien.ai.demain.ContactsMeta;
import com.tien.ai.demain.ContactMapping;
import com.tien.ai.logic.ContactLogic;
import com.tien.ai.net.ContactComparator;
import com.tien.ai.utils.NetworkUtil;
import com.tien.ai.utils.XLog;
import com.tien.volley.Response;
import com.tien.volley.VolleyError;
import com.tien.volley.toolbox.JsonObjectRequest;

/**
 * 
 * <p>
 * Title: ContactsFragment
 * </p>
 * <p>
 * Description:联系人列表界面
 * </p>
 * 
 * @author wangtf
 * @date 2014-1-15
 */
public class ContactsFragment extends Fragment implements Observer, OnClickListener {
    
    private Button backBtn;
    private ListView listView;
    private AutoCompleteTextView contactsSearchView;
    
    private ContactsAdapter contactsAdapter = null;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // NotificationCenter.defaultCenter().addObserver(this);
        
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        
        View view = inflater.inflate(R.layout.contacts_view, container, false);
        findView(view);
        addListener();
        
        return view;
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        contactsAdapter = new ContactsAdapter(getActivity());
        listView.setAdapter(contactsAdapter);
        
        ContactLogic.loadAllContactMetas(contactMetasCallback);
        
    }
    
    private DataCallback<List<ContactsMeta>> contactMetasCallback = new DataCallback<List<ContactsMeta>>() {
        
        @Override
        public void callback(List<ContactsMeta> data) {
            if (!data.isEmpty()) {
                contactsAdapter.setDatas(true, data);
                comparePhone();
                // contactsAdapter.notifyDataSetChanged();
                // ContactsApplication.contactMetas = data;
                //
                // NotificationItem item = new NotificationItem();
                // NotificationCenter.defaultCenter().postNotification(item);
            }
        }
    };
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                getFragmentManager().popBackStack();
                break;
            
            default:
                break;
        }
        
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        // NotificationCenter.defaultCenter().deleteObserver(this);
        XLog.i("wanges", "ContactsFragment onDestroy()");
    }
    
    private void findView(View view) {
        backBtn = (Button) view.findViewById(R.id.back_btn);
        listView = (ListView) view.findViewById(R.id.contacts_lv);
        // contactsSearchView = (AutoCompleteTextView)
        // view.findViewById(R.id.contacts_search_av);
        
    }
    
    public void addListener() {
        backBtn.setOnClickListener(this);
        
        // contactsSearchView.addTextChangedListener(new TextWatcher() {
        //
        // @Override
        // public void onTextChanged(CharSequence s, int start, int before,
        // int count) {
        // contactsAdapter.getFilter().filter(contactsSearchView.getText().toString());
        // }
        //
        // @Override
        // public void beforeTextChanged(CharSequence s, int start, int count,
        // int after) {
        // }
        //
        // @Override
        // public void afterTextChanged(Editable s) {
        // }
        // });
    }
    
    private void comparePhone() {
        String url = Constant.URL_COMPARE_PHONE;
        String phones = SystemDB.queryAllPhone();
        Map<String, String> params = NetworkUtil.initRequestParams();
        params.put("phones", phones);
        
        JsonObjectRequest request = new JsonObjectRequest(url, params,
            new Response.Listener<JSONObject>() {
                
                @Override
                public void onResponse(JSONObject response) {
                    handleData(response);
                }
                
            }, new Response.ErrorListener() {
                
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });
        
        AIApplication.getInstance().getRequestQueue().add(request);
    }
    
    private void handleData(JSONObject response) {
        JSONObject data = response.optJSONObject("data");
        JSONArray friend = data.optJSONArray("friend");
        JSONArray nofriend = data.optJSONArray("nofriend");
        
        Gson gson = new Gson();
        List<ContactMapping> friendList = new ArrayList<ContactMapping>();
        if (friend != null) {
            friendList = gson.fromJson(friend.toString(), new TypeToken<List<ContactMapping>>() {
            }.getType());
        }
        
        List<ContactMapping> nofriendList = new ArrayList<ContactMapping>();
        if (nofriend != null) {
            nofriendList = gson.fromJson(nofriend.toString(),
                new TypeToken<List<ContactMapping>>() {
                }.getType());
        }
        
        HashMap<String, String> contactMap = SystemDB.queryAllContactMap();
        
        HashMap<String, ContactMapping> friendContactMappings = new HashMap<String, ContactMapping>();
        ;
        for (ContactMapping contactMapping : friendList) {
            String conatctId = contactMap.get(contactMapping.getPhone());
            if (conatctId != null) {
                friendContactMappings.put(conatctId, contactMapping);
            }
        }
        
        HashMap<String, ContactMapping> nofriendContactMappings = new HashMap<String, ContactMapping>();
        ;
        for (ContactMapping contactMapping : nofriendList) {
            String conatctId = contactMap.get(contactMapping.getPhone());
            if (conatctId != null) {
                nofriendContactMappings.put(conatctId, contactMapping);
            }
        }
        
        List<ContactsMeta> contactMetas = contactsAdapter.getContactsMetas();
        for (ContactsMeta contactMeta : contactMetas) {
            String contactId = contactMeta.getContactId();
            // 已安装，不是好友
            ContactMapping nocontactMapping = nofriendContactMappings.get(contactId);
            if (nocontactMapping != null) {
                contactMeta.setStatus(0);
                contactMeta.setUid(nocontactMapping.getUid());
            }
            
            // 已安装，已是好友
            ContactMapping contactMapping = friendContactMappings.get(contactId);
            if (contactMapping != null) {
                contactMeta.setStatus(2);
                contactMeta.setUid(contactMapping.getUid());
            }
        }
        Collections.sort(contactMetas, new ContactComparator());
        
        contactsAdapter.notifyDataSetChanged();
    }
    
    @Override
    public void update(Observable observable, Object data) {
        // if (data instanceof NotificationItem) {
        // NotificationItem item = (NotificationItem) data;
        // XLog.i("wanges", "NotificationItem "+item.getType());
        // if (item.getType() == NotificationItem.TYPE_CONTACT_MODIFTY ||
        // item.getType() == NotificationItem.TYPE_CONTACT_ADD
        // || item.getType() == NotificationItem.TYPE_CONTACT_DELETE) {
        // if(contactsAdapter != null){
        // ContactLogic.loadAllContactMetas(contactMetasCallback);
        // XLog.i("wanges", "loadAllContactMetas ------");
        // }
        // }
        // }
    }
    
}
