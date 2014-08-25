package com.tien.ai.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.xutil.XLog;

import com.tien.ai.AIApplication;
import com.tien.ai.Constant;
import com.tien.ai.NotificationCenter;
import com.tien.ai.NotificationItem;
import com.tien.ai.R;
import com.tien.ai.demain.ContactsMeta;
import com.tien.ai.listener.NetworkErrorResponeListener;
import com.tien.ai.logic.ContactLogic;
import com.tien.ai.utils.NetworkUtil;
import com.tien.ai.utils.PreferenceUtils;
import com.tien.ai.utils.ToastUtil;
import com.tien.ai.view.LoadingDialog;
import com.tien.volley.Response;
import com.tien.volley.VolleyError;
import com.tien.volley.toolbox.JsonObjectRequest;



/**
 * 
 * <p>Title: ContactsAdapter</p>
 * <p>Description: 联系人列表adapter</p>
 * @author wangtf
 * @date 2014-1-22
 */
public class ContactsAdapter extends BaseAdapter {

		
		private LayoutInflater mInflater;
		
		private List<ContactsMeta> contactMetas = new ArrayList<ContactsMeta>();
		private Context context;
		private int addColor;
		private int inviteColor;
		private int addedColor;

		public ContactsAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
			this.context = context;
			addColor = Color.parseColor("#42bd41");
			inviteColor = Color.parseColor("#5677fc");
			addedColor = Color.parseColor("#bdbdbd");
		}
		
		public List<ContactsMeta> getContactsMetas(){
		    return contactMetas;
		}

		public void setDatas(boolean clear, List<ContactsMeta> contactMetas){
			if(clear){
				this.contactMetas.clear();
			}
			
			this.contactMetas.addAll(contactMetas);
			this.notifyDataSetChanged();
		}
		
		@Override
		public int getCount() {
			return contactMetas.size();
		}

		@Override
		public Object getItem(int position) {
			return contactMetas.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.contacts_list_item, null);
				holder = new ViewHolder();
				holder.name = (TextView) convertView.findViewById(R.id.star_nickname);
//				holder.mLinearLayout = (LinearLayout) convertView.findViewById(R.id.section);
				holder.mInviteBtn = (Button) convertView.findViewById(R.id.invite_btn);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			final ContactsMeta contactMeta = contactMetas.get(position);

			holder.name.setText(contactMeta.getNickname());
			
			int status = contactMeta.getStatus();
			XLog.i("wanges", "status:"+status);
			if(status == 0){
			    holder.mInviteBtn.setText("添加"); 
			    holder.mInviteBtn.setTextColor(addColor);
			    holder.mInviteBtn.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        addFriend(contactMeta, v);
                    }
                });
			}else if(status == 1){
			    holder.mInviteBtn.setText("邀请");
			    holder.mInviteBtn.setTextColor(inviteColor);
			    holder.mInviteBtn.setOnClickListener(new View.OnClickListener() {

	                @Override
	                public void onClick(View v) {
	                    String phone = ContactLogic.lookupPhone(contactMeta.getContactId());
	                    Uri uri = Uri.parse("smsto:"+phone);            
	                    Intent it = new Intent(Intent.ACTION_SENDTO, uri);            
	                    it.putExtra("sms_body", "有事没事骚扰下好友，快去哇一下吧！下载地址http://yf-ccl-fw02.yf01.baidu.com:8401/ai/wa.apk 记得安装后加我 "+PreferenceUtils.loadPhone());            
	                    context.startActivity(it);  
	                }
	            });
			}else if(status == 2){
			    holder.mInviteBtn.setText("已添加");	
			    holder.mInviteBtn.setTextColor(addedColor);
			    holder.mInviteBtn.setOnClickListener(null);
			}

			return convertView;
		}

		
		static class ViewHolder {
		    
		    TextView name;
		    Button mInviteBtn;
		    LinearLayout mLinearLayout;
		}
		
		 private void addFriend(final ContactsMeta contactMeta, final View v){
		        String url = Constant.URL_ADD_FRIEND;
		        Map<String, String> params = NetworkUtil.initRequestParams();
		        params.put("friend_uid", contactMeta.getUid());
		        JsonObjectRequest request = new JsonObjectRequest(url, params, new Response.Listener<JSONObject>(){

		            @Override
		            public void onResponse(JSONObject response) {
		                  int ret = response.optInt("ret");
		                  if(ret == 0){
		                     JSONObject data = response.optJSONObject("data");
		                     if(data != null){
		                         ToastUtil.shortToast("添加成功!");
		                         NotificationCenter.defaultCenter().postNotification(new NotificationItem(NotificationItem.TYPE_ADD_FRIEND, ""));
		                         Button inviteBtn = (Button)v;
		                         inviteBtn.setText("已添加");    
		                         inviteBtn.setTextColor(addedColor);
		                         inviteBtn.setOnClickListener(null);
		                         
		                         contactMeta.setStatus(2);
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
		            
		        }, new NetworkErrorResponeListener((Activity)context) {

		            @Override
		            public void onErrorResponse(VolleyError error) {
		                super.onErrorResponse(error);
		            }
		        });
		        AIApplication.getInstance().getRequestQueue().add(request);
		    }
	}
