package com.tien.ai.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.xutil.XLog;

import com.tien.ai.R;
import com.tien.ai.UserActivity;
import com.tien.ai.db.DBManager;
import com.tien.ai.demain.Friend;
import com.tien.volley.imagecache.ImageCacheManager;
import com.tien.volley.toolbox.NetworkImageView;



/**
 * 
 * <p>Title: ContactsAdapter</p>
 * <p>Description: 联系人列表adapter</p>
 * @author wangtf
 * @date 2014-1-22
 */
public class FriendsAdapter extends BaseAdapter implements SectionIndexer, Filterable {
//public class FriendsAdapter extends BaseAdapter {

		private FriendFilter mFriendFilter;
		
		private LayoutInflater mInflater;
		
		private final Object mLock = new Object();
		private HashMap<Integer, String> charMap = new HashMap<Integer, String>();
		private List<Friend> friends = new ArrayList<Friend>();
		private ArrayList<Friend> filterDatas;
		private Context context;
		private List<String> remindUids = new ArrayList<String>();

		public FriendsAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
			this.context = context;
			remindUids = DBManager.getInstance().queryAllRemind();
		}
		
		public List<String> getRemindUids(){
		    return remindUids;
		}

		public void setDatas(boolean clear, List<Friend> contactMetas){
			if(clear){
				this.friends.clear();
			}
			
			this.friends.addAll(contactMetas);
			for(Friend friend : contactMetas){
			    if(remindUids.contains(friend.getUid())){
			        friend.setRemind(true);
	            }
			}
		}
		
		@Override
		public int getCount() {
			return friends.size();
		}

		@Override
		public Object getItem(int position) {
			return friends.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.friends_list_item, null);
				holder = new ViewHolder();
				holder.avatarIV = (NetworkImageView) convertView.findViewById(R.id.avatar_iv);
				holder.name = (TextView) convertView.findViewById(R.id.star_nickname);
				holder.remindIV = (ImageView) convertView.findViewById(R.id.remind_iv);
				holder.mLinearLayout = (LinearLayout) convertView.findViewById(R.id.section);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			final Friend friend = friends.get(position);
			holder.name.setText(friend.getNickname());
			
			 holder.avatarIV.setErrorImageResId(R.drawable.avatar_default);
			 holder.avatarIV.setImageResource(R.drawable.avatar_default);
			 holder.avatarIV.setDefaultImageResId(R.drawable.avatar_default);
			   
			 //XLog.i("wanges", friend.getNickname()+":"+friend.getAvatar());
			 holder.avatarIV.setImageUrl(friend.getAvatar(), ImageCacheManager.getInstance().getImageLoader());
			
			 XLog.i("wanges", friend.getNickname()+":"+friend.isRemind());
			if(friend.isRemind()){
			    holder.remindIV.setVisibility(View.VISIBLE);
			}else{
			    holder.remindIV.setVisibility(View.GONE);
			}
			
            // 显示组和数据
            char firstChar = friend.getFirstChar();

            if (position == 0) {
                holder.mLinearLayout.setVisibility(View.VISIBLE);
                setSection(holder.mLinearLayout, firstChar, position);
            } else {
                Friend preFriend = friends.get(position - 1);
                char preFirstChar = preFriend.getFirstChar();
                // 根据比较显示组
                if (firstChar != preFirstChar) {
                    holder.mLinearLayout.setVisibility(View.VISIBLE);
                    setSection(holder.mLinearLayout, firstChar, position);
                } else {
                    holder.mLinearLayout.setVisibility(View.GONE);
                }
            }
			
			final ImageView remindIV = holder.remindIV;
			
			holder.name.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
				    remindIV.setVisibility(View.GONE);
				    friend.setRemind(false);
				    DBManager.getInstance().deleteRemindByUid(friend.getUid());
				    
					Intent intent = new Intent(context, UserActivity.class);
					intent.putExtra("uid", friend.getUid());
					context.startActivity(intent);
				}
			});

			return convertView;
		}
		
		  // 添加分组头
        private void setSection(LinearLayout header, char c, int position) {
            header.removeAllViews();
            TextView text = new TextView(context);
            // 分割条颜色
            header.setBackgroundColor(Color.parseColor("#e0e0e0"));
            // header.setBackgroundColor(Color.TRANSPARENT);
            text.setTextColor(Color.WHITE);
            text.setText(String.valueOf(c));
            text.setTextSize(18);
            text.setPadding(10, 0, 0, 0);
            text.setGravity(Gravity.CENTER_VERTICAL);
            header.addView(text);
            header.setEnabled(false);
            header.setFocusable(false);
            header.setFocusableInTouchMode(false);
            // 分组的头字母
            charMap.put(position, String.valueOf(c));

        }

        // 根据分组确定位置
        public int getPositionForSection(int section) {
            if (section == 35) {
                return 0;
            }
            for (int i = 0; i < friends.size(); i++) {
                char firstChar = friends.get(i).getFirstChar();
                if ("".equals(firstChar))
                    break;

                if (firstChar == section) {
                    return i;
                }
            }
            return -1;
        }

        // 根据位置确定分组
        public int getSectionForPosition(int position) {
            // XLog.i("wangtf", position + "  " + charMap);
            return charMap.get(position).toUpperCase().charAt(0);
        }

        public Object[] getSections() {
            return null;
        }

        @Override
        public Filter getFilter() {
            if (mFriendFilter == null) {
                mFriendFilter = new FriendFilter();
            }
            return mFriendFilter;
        }

        // 搜索过滤器
        class FriendFilter extends Filter {

            @Override
            protected FilterResults performFiltering(CharSequence prefix) {
                // 持有过滤操作完成之后的数据。该数据包括过滤操作之后的数据的值以及数量。 count:数量
                // values包含过滤操作之后的数据的值
                FilterResults results = new FilterResults();

                if (filterDatas == null) {
                    synchronized (mLock) {
                        // 将list的用户 集合转换给这个原始数据的ArrayList
                        filterDatas = new ArrayList<Friend>(friends);
                    }
                }
                if (prefix == null || prefix.length() == 0) {
                    synchronized (mLock) {
                        ArrayList<Friend> list = new ArrayList<Friend>(filterDatas);
                        results.values = list;
                        results.count = list.size();
                    }
                } else {

                    // 声明一个临时的集合对象 将原始数据赋给这个临时变量
                    final ArrayList<Friend> values = filterDatas;

                    final int count = values.size();

                    // 新的集合对象
                    final ArrayList<Friend> newValues = new ArrayList<Friend>(count);
                    // 做正式的筛选
                    char c = prefix.charAt(0);
                    String prefixString = "";
                    if (c >= 'A' && c <= 'z') {
                        prefixString = prefix.toString().toUpperCase();
                        for (int i = 0; i < count; i++) {
                            // 如果姓名的前缀相符相符就添加到新的集合
                            final Friend value = (Friend) values.get(i);

                            String nicknamePinyin = value.getNicknamePinyin();
                            if (nicknamePinyin.startsWith(prefixString)) {
                                newValues.add(value);
                            }
                        }
                        // 中文 数字
                    } else {
                        prefixString = prefix.toString();
                        for (int i = 0; i < count; i++) {
                            // 如果姓名的前缀相符相符就添加到新的集合
                            final Friend value = (Friend) values.get(i);
                            String nickname = value.getNickname();
                            if (nickname.startsWith(prefixString)) {
                                newValues.add(value);
                            }
                        }
                    }

                    // 然后将这个新的集合数据赋给FilterResults对象
                    results.values = newValues;
                    results.count = newValues.size();
                }

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint,
                    FilterResults results) {
                // 重新将与适配器相关联的List重赋值一下
                friends = (ArrayList<Friend>) results.values;

                if (results.count > 0) {
//                    next.setVisibility(View.GONE);
                    notifyDataSetChanged();
                } else {
//                    next.setVisibility(View.VISIBLE);
                    notifyDataSetInvalidated();
                }
            }

        }

		
		private static class ViewHolder {
		    
		    NetworkImageView avatarIV;
		    TextView name;
		    ImageView remindIV;
		    LinearLayout mLinearLayout;
		}
	}
