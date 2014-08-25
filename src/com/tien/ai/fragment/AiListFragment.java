package com.tien.ai.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.json.JSONObject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.readystatesoftware.viewbadger.BadgeView;
import com.tien.ai.AIApplication;
import com.tien.ai.Constant;
import com.tien.ai.NotificationCenter;
import com.tien.ai.NotificationItem;
import com.tien.ai.R;
import com.tien.ai.UserActivity;
import com.tien.ai.db.DBManager;
import com.tien.ai.demain.Ai;
import com.tien.ai.demain.Game;
import com.tien.ai.listener.NetworkErrorResponeListener;
import com.tien.ai.utils.DimensionUtil;
import com.tien.ai.utils.NetworkUtil;
import com.tien.ai.utils.PreferenceUtils;
import com.tien.ai.utils.ToastUtil;
import com.tien.ai.utils.XLog;
import com.tien.ai.view.GameFinishWindow;
import com.tien.ai.view.GameReceiverWindow;
import com.tien.ai.view.swipelistview.BaseSwipeListViewListener;
import com.tien.ai.view.swipelistview.SwipeListView;
import com.tien.volley.Response;
import com.tien.volley.VolleyError;
import com.tien.volley.toolbox.JsonObjectRequest;

public class AiListFragment extends Fragment implements Observer{
    
    private static final int SEND_START = 1;
    private static final int SEND_SUCCESS = 2;
    private static final int SEND_FAIL = 3;
	
	private SwipeListView swipeListView;
	private Button refershBtn;
    private ProgressBar loadingPB;
    private TextView tipTV;
	private ArrayList<Ai> mAiList = new ArrayList<Ai>();
	private AiAdapter mAiAdapter;
	 /** 发送消息超时任务 */
    private Map<String, Runnable> sendMsgTimeoutTasks = new HashMap<String, Runnable>();
    
    /** handler */
    private Handler handler = new Handler();
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NotificationCenter.defaultCenter().addObserver(this);
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
	    if(PreferenceUtils.ifNeedUpdateAiList()){
	    }
		View view = inflater.inflate(R.layout.ai_list_view, null);
		
		this.findView(view);
		return view;
	}
	
	private void findView(View view){
		this.swipeListView = (SwipeListView) view.findViewById(R.id.ai_listview);
		int width = getResources().getDisplayMetrics().widthPixels;
		int offset = width - DimensionUtil.dip2px(160, getActivity());
		this.swipeListView.setOffsetLeft(offset);
		
		RelativeLayout empty = (RelativeLayout) view.findViewById(R.id.empty);
        refershBtn = (Button) view.findViewById(R.id.refersh_btn);
        loadingPB = (ProgressBar) view.findViewById(R.id.loading_pb);
        tipTV = (TextView) view.findViewById(R.id.tip_tv);
        
        this.swipeListView.setEmptyView(empty);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		
		this.addListener();
		this.mAiAdapter = new AiAdapter();
		loadData(1);
//        SwipeDismissAdapter adapter = new SwipeDismissAdapter(mAiAdapter, new OnDismissCallback() {
//            
//            @Override
//            public void onDismiss(AbsListView listView, int[] reverseSortedPositions) {
//                for (int position : reverseSortedPositions) {
//                    mAiList.remove(position);
//                }
//            }
//        });
//        adapter.setAbsListView(swipeListView);
		 View view = getView().findViewById(R.id.empty);
	     this.swipeListView.setEmptyView(view);
         swipeListView.setAdapter(mAiAdapter);
        
       
	}
	
	private void move(final int position){
	    XLog.i("wanges", "move:"+position);
	    updateItem(position, false);
	    if(position == 0) return;
	    
	    final int firstVisiblePostion = swipeListView.getFirstVisiblePosition();
	    
	    int clickItem =  position - firstVisiblePostion;
	   final View top = swipeListView.getChildAt(clickItem-1);
	   final View bottom = swipeListView.getChildAt(clickItem);
	   
	   int []location1 = new int[2];
	   int []location2 = new int[2];
	   top.getLocationOnScreen(location1);
	   bottom.getLocationOnScreen(location2);
	   
	   ObjectAnimator animator1 = ObjectAnimator.ofFloat(top, "translationY", top.getHeight() + swipeListView.getDividerHeight());
	   ObjectAnimator animator2 = ObjectAnimator.ofFloat(bottom, "translationY", -top.getHeight() - swipeListView.getDividerHeight());
	   
	   AnimatorSet animator = new AnimatorSet();
	   animator.play(animator1).with(animator2);
	   animator.setDuration(500);
	   animator.setInterpolator(new AccelerateInterpolator());
	   animator.start();
	   
	   animator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            float value =  (Float)animation.getAnimatedValue();
            //if(position == firstVisiblePostion){
                Rect rect = new Rect(bottom.getLeft(), bottom.getTop() - (int)value, bottom.getLeft()+bottom.getWidth(), bottom.getTop()+bottom.getHeight());
                swipeListView.handleMobileCellScroll(rect);
           // }
        }
	       
	   });
	   
	   animator.addListener(new AnimatorListenerAdapter() {
	       
    	    @Override
    	    public void onAnimationEnd(Animator animation) {
    	        super.onAnimationEnd(animation);
    	        
    	        Ai temp = mAiList.get(position-1);
    	        mAiList.set(position-1, mAiList.get(position));
    	        mAiList.set(position, temp);
    	        
    	        top.setTranslationY(0);
    	        bottom.setTranslationY(0);
    	        
    	        mAiAdapter.notifyDataSetChanged();
    	        
    	        if(position > 1){
    	            
    	            int newPisiton = position -1 ;
    	            move(newPisiton);
    	        }
    	    }
	   });
	}
	
	
	
	 public void addSendMsgTimeoutTask(final String uid) {
	        
	        Runnable task = new Runnable() {
	            
	            @Override
	            public void run() {
	                //updateItem(position, false, true, uid);
	                removeSendMsgTimeoutTask(uid);
	                ToastUtil.shortToast("Send fail to "+uid);
	            }
	        };
	        handler.postDelayed(task, 80000);
	        sendMsgTimeoutTasks.put(uid, task);
	    }
	    
	    public void removeSendMsgTimeoutTask(String uid) {
	        Runnable task = sendMsgTimeoutTasks.get(uid);
	        handler.removeCallbacks(task);
	        sendMsgTimeoutTasks.remove(uid);
	    }
	    
	
	
	private void addListener(){
	    
	    refershBtn.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                    loadData(500);
            }
        });
	   
	    swipeListView.setSwipeListViewListener(new BaseSwipeListViewListener() {
            @Override
            public void onOpened(int position, boolean toRight) {
            }

            @Override
            public void onClosed(int position, boolean fromRight) {
            }

            @Override
            public void onListChanged() {
            }

            @Override
            public void onMove(int position, float x) {
            }

            @Override
            public void onStartOpen(int position, int action, boolean right) {
                XLog.d("swipe", String.format("onStartOpen %d - action %d", position, action));
            }

            @Override
            public void onStartClose(int position, boolean right) {
                XLog.d("swipe", String.format("onStartClose %d", position));
            }

            @Override
            public void onClickFrontView(int position, View v) {
                XLog.d("swipe", String.format("onClickFrontView %d", position));
                
                Ai ai = mAiList.get(position);
                
                if(!"".equals(ai.getGameSequence())){
                    Game game = DBManager.getInstance().loadGameBySequence(ai.getGameSequence());
                    if(game.getGameStatus() == 1){
                        GameReceiverWindow gameReceiverWindow = new GameReceiverWindow(getActivity(), game, ai.getUid());
                        gameReceiverWindow.show();
                    }else if(game.getGameStatus() == 2){
                        GameFinishWindow gameFinishWindow = new GameFinishWindow(getActivity(), game, ai.getUid());
                        gameFinishWindow.show();
                    }
                    
                }else{
                    updateItem(position, true);
                    sendAI(position, ai.getUid(), ai.getNickname());
                    addSendMsgTimeoutTask(ai.getUid());
                }
            }

            @Override
            public void onClickBackView(int position) {
                XLog.d("swipe", String.format("onClickBackView %d", position));
               
            }

            @Override
            public void onDismiss(int[] reverseSortedPositions) {
                
//                for (int position : reverseSortedPositions) {
//                    swipeListView.dismiss(position);
//                    mAiList.remove(position);
//                }
//                mAiAdapter.notifyDataSetChanged();
            }

        });
	    
	}
	
	@Override
	public void onDestroy() {
	    super.onDestroy();
	    NotificationCenter.defaultCenter().deleteObserver(this);
	}
	
	@Override
	public void update(Observable observable, Object data) {
	    NotificationItem item = (NotificationItem)data;
	    if(item.getType() == NotificationItem.TYPE_SEND_AI){
	        loadData(1);
	    }else if(item.getType() == NotificationItem.TYPE_RECEIVE_AI){
//	        for(Ai ai : mAiList){
//	            ai.setAiCount(ai.getAiCount() + 1);
//	            return ai.getAiCount();
//	        }
	        loadData(1);
	    }else if(item.getType() == NotificationItem.TYPE_GAME){
	        //String[] val = (String[])item.getMessage();
	        
//	        if(val == null || val.length != 2) return;
//	        
//	        for(Ai ai : mAiList){
//              if(ai.getUid().equals(val[0])){
//                  ai.setGameSequence(val[1]);
//              }
//            }
//	        mAiAdapter.notifyDataSetChanged();
	        this.loadData(0);
	    }
	}
	
	
	class AiAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return mAiList.size();
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
			if(convertView==null){
				holder = new ViewHolder();
				convertView = LayoutInflater.from(getActivity()).inflate(R.layout.ai_listview_item, null);
				holder.contentRL = (RelativeLayout) convertView.findViewById(R.id.content_rl);
				holder.progressRL = (LinearLayout) convertView.findViewById(R.id.progress_rl);
				holder.aiTV = (TextView) convertView.findViewById(R.id.name);
				holder.aiTimeTV = (TextView) convertView.findViewById(R.id.ai_time_tv);
				holder.aiIV = (ImageView) convertView.findViewById(R.id.ai_iv);
				holder.gameIV = (ImageView) convertView.findViewById(R.id.game_iv);
				holder.delBtn = (Button) convertView.findViewById(R.id.del_btn);
				holder.detailBtn = (Button) convertView.findViewById(R.id.detail_btn);
				
				holder.badgeView = new BadgeView(getActivity(), holder.aiIV);
				holder.badgeView.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
				holder.badgeView.setBackgroundResource(R.drawable.badge);
				holder.badgeView.setBadgeMargin(0, 0);
				holder.badgeView.setTextSize(10);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder)convertView.getTag();
			}
			
			final Ai ai = mAiList.get(position);
			holder.aiTV.setText(ai.getNickname());
			holder.aiTimeTV.setText("最后更新时间: "+ai.getTime());
			
			int type = ai.getType();
			if(type==0){
			    holder.aiIV.setImageResource(R.drawable.ai_out);
			    holder.badgeView.hide();
			}else if(type == 1){
			    holder.aiIV.setImageResource(R.drawable.ai_in);
			    XLog.i("wanges", ai.getNickname()+":"+ai.getInCount());
			    if(ai.getInCount()>1){
			        holder.badgeView.setText(ai.getInCount()+"");
	                holder.badgeView.show();
			    }else if(ai.getInCount()>99){
			        holder.badgeView.setText("99");
                    holder.badgeView.show();
			    }else{
			        holder.badgeView.hide();
			    }
			}
			
			//game 标示
			if(!"".equals(ai.getGameSequence())){
			    holder.gameIV.setVisibility(View.VISIBLE);
			}else{
			    holder.gameIV.setVisibility(View.GONE);  
			}
			
			if(sendMsgTimeoutTasks.get(ai.getUid()) != null){
			    holder.contentRL.setVisibility(View.GONE);
			    holder.progressRL.setVisibility(View.VISIBLE);
			}
			
			final int index = position;
			holder.delBtn.setOnClickListener(new View.OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    delAI(index, ai.getUid());
                   
                }
            });
			
			holder.detailBtn.setOnClickListener(new View.OnClickListener() {
                
                @Override
                public void onClick(View v) {
                   
                    swipeListView.closeAnimate(index);
                    Intent intent = new Intent(getActivity(), UserActivity.class);
                    intent.putExtra("uid", ai.getUid());
                    startActivity(intent);
                }
            });
			return convertView;
		}
		
	}
	
	static class ViewHolder{
	    RelativeLayout contentRL;
	    LinearLayout progressRL;
		TextView aiTV;
		TextView aiTimeTV;
		ImageView aiIV;
		ImageView gameIV;
		Button delBtn;
		Button detailBtn;
		BadgeView badgeView;
	}
	
	private void loadData(long time){
	    loadingPB.setVisibility(View.VISIBLE);
        tipTV.setVisibility(View.GONE);
        refershBtn.setVisibility(View.GONE);
        
        new Handler().postDelayed(new Runnable() {
            
            @Override
            public void run() {
                Cursor cursor =  DBManager.getInstance().queryAll();
                if(cursor != null  &&  cursor.getCount() > 0){
                    mAiList.clear();
                    while(cursor.moveToNext()){
                        String uid = cursor.getString(cursor.getColumnIndex("uid"));
                        String nickname = cursor.getString(cursor.getColumnIndex("nickname"));
                        int type = cursor.getInt(cursor.getColumnIndex("type"));
                        int inCount = cursor.getInt(cursor.getColumnIndex("in_count"));
                        String time = cursor.getString(cursor.getColumnIndex("time"));
                        mAiList.add(new Ai(uid,nickname, type, inCount, time));
                    }
                }
                
                cursor =  DBManager.getInstance().queryGame();
                if(cursor != null  &&  cursor.getCount() > 0){
                    while(cursor.moveToNext()){
                        String uid = cursor.getString(cursor.getColumnIndex("uid"));
                        String gameSequence = cursor.getString(cursor.getColumnIndex("game_sequence"));
                        
                        boolean flag = false;
                        for(Ai ai : mAiList){
                            if(uid.equals(ai.getUid())){
                                ai.setGameSequence(gameSequence);
                                flag = true;
                                break;
                            }
                        }
                        
                        if(!flag){
                            SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm:ss");
                            String time = format.format(new Date()); 
                            XLog.i("wanges", "uid:"+uid);
                            Ai ai = new Ai(uid,"", 1, 1, time);
                            ai.setGameSequence(gameSequence);
                            mAiList.add(ai);
                        }
                    }
                }
                
                mAiAdapter.notifyDataSetChanged();
                
                cursor.close();
                DBManager.getInstance().closeDatabase();
            }
        }, time);
        
       loadingPB.setVisibility(View.GONE);
       tipTV.setVisibility(View.VISIBLE);
       refershBtn.setVisibility(View.VISIBLE);
       
    }
	
	private void sendAI(final int position, final String friendUid, final String friendNickname){
	    String url = Constant.URL_SEND_AI;
	    Map<String, String> params = NetworkUtil.initRequestParams();
	    params.put("friend_uid", friendUid);
	    JsonObjectRequest request = new JsonObjectRequest(url, params, new Response.Listener<JSONObject>(){

            @Override
            public void onResponse(JSONObject response) {
                removeSendMsgTimeoutTask(friendUid);
                DBManager.getInstance().add(friendUid,friendNickname , 0);
                NotificationCenter.defaultCenter().postNotification(new NotificationItem(NotificationItem.TYPE_SEND_AI, ""));
                //move(position);
                updateItem(position, false);
            }
            
        }, new NetworkErrorResponeListener(getActivity()) {

            @Override
            public void onErrorResponse(VolleyError error) {
                updateItem(position, false);
            }
        });
        //界面启动时优先使用缓存;
        request.setTag(getActivity());
        AIApplication.getInstance().getRequestQueue().add(request);
	}
	
	private void delAI(final int index, final String friendUid){
	    swipeListView.closeAnimate(index);
        DBManager.getInstance().delete(friendUid);
        mAiList.remove(index);
        mAiAdapter.notifyDataSetChanged();
    }
	
	private void updateItem(int index, boolean progress){
	   int firstVisible = swipeListView.getFirstVisiblePosition();
	   
	   View view = swipeListView.getChildAt(index - firstVisible);
	   
	   if(progress){
	       view.findViewById(R.id.content_rl).setVisibility(View.GONE); 
	       view.findViewById(R.id.progress_rl).setVisibility(View.VISIBLE); 
	   }else{
	       view.findViewById(R.id.content_rl).setVisibility(View.VISIBLE); 
           view.findViewById(R.id.progress_rl).setVisibility(View.GONE); 
           ImageView aiIV = (ImageView)view.findViewById(R.id.ai_iv);
           aiIV.setImageResource(R.drawable.ai_out);
          
	   }
	}
	
	private int updateAiList(String uid){
	    for(Ai ai : mAiList){
	        if(ai.getUid().equals(uid)){
//	            ai.setAi_count(ai.getAi_count() + 1);
//	            return ai.getAi_count();
	        }
	    }
	    return 0;
	}

}
