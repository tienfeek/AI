package com.tien.ai.view;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;
import com.tien.ai.AIApplication;
import com.tien.ai.BaseActivity;
import com.tien.ai.R;
import com.tien.ai.utils.DimensionUtil;
import com.tien.ai.utils.XLog;

public class BulletinWindow  implements Observer{

	private WindowManager wm = null;
	private WindowManager.LayoutParams wmParams = null;
	private View view;
	private boolean attach = false;
	private Context context;
	private int width;
	private LinearLayout mBulletinLayout;
	private TextView mBulletin;
	private boolean ellipsEnd = true;
//	private BulletinThread bulletinThread;
	private BlockingQueue<String> msg = new ArrayBlockingQueue<String>(200);
	private static BulletinWindow bulletinWindow = new BulletinWindow();

	private Handler handler = new Handler();
	static {
//		NotificationCenter.defaultCenter().addObserver(bulletinWindow);
	}

	private BulletinWindow() {
		this.context = AIApplication.getInstance().getApplicationContext();
		createView();
	}

	public static BulletinWindow getInstance() {
		return bulletinWindow;
	}

	private void createView() {
		view = LayoutInflater.from(context).inflate(R.layout.rkgk_bulletin_window, null);
		// 获取WindowManager
		wm = (WindowManager) context.getApplicationContext().getSystemService("window");
		width = wm.getDefaultDisplay().getWidth();
		// 设置LayoutParams(全局变量）相关参数
		wmParams = new WindowManager.LayoutParams();
		wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;// 该类型提供与用户交互，置于所有应用程序上方，但是在状态栏后面
		wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;// 不接受任何按键事件
		wmParams.gravity = Gravity.TOP;
		// 以屏幕左上角为原点，设置x、y初始值
		wmParams.x = 0;
		wmParams.y = 0;
		// 设置悬浮窗口长宽数据
		wmParams.width = WindowManager.LayoutParams.MATCH_PARENT;
		wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		wmParams.format = PixelFormat.RGBA_8888;

		mBulletinLayout = (LinearLayout) view.findViewById(R.id.bulletin_layout);
		mBulletin = (TextView)view.findViewById(R.id.bulletin);
		
	}

	private void showAnimation(){
		
		if(mBulletinLayout.getVisibility() == View.GONE){
			mBulletinLayout.setVisibility(View.VISIBLE);
			String nickname = msg.poll();
			String content = nickname + " 给你发了一个哇!";
			SpannableString spannableString = new SpannableString(content);
            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#ff5722")), 0, nickname.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			mBulletin.setText(spannableString);
			ObjectAnimator animator = ObjectAnimator.ofFloat(mBulletinLayout, "y", 0 - DimensionUtil.dip2px(50, context), 0).setDuration(300);
			animator.setInterpolator(new AccelerateInterpolator());
			animator.setRepeatCount(0);
			animator.setRepeatMode(ValueAnimator.RESTART);
			animator.addListener(new AnimatorListener() {
				
				@Override
				public void onAnimationStart(Animator arg0) {
	                playMusic();           
				}

				@Override
				public void onAnimationRepeat(Animator arg0) { }
				
				@Override
				public void onAnimationEnd(Animator arg0) {
					handler.postDelayed(new Runnable() {
						
						@Override
						public void run() {
							dismissAnimation();
						}
					}, 2000);
				}
				
				@Override
				public void onAnimationCancel(Animator arg0) { }
			});
			AnimatorSet show = new AnimatorSet();
			show.play(animator);
			show.start();
		}
	}
	
	 private void playMusic() {
         try {
             MediaPlayer mediaPlayer = MediaPlayer.create(AIApplication.getInstance().getApplicationContext(), R.raw.wa2);
             mediaPlayer.setLooping(false);
//             AudioManager am=(AudioManager)AIApplication.getInstance().getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
//             am.adjustStreamVolume (AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);  
//             am.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);//得到听筒模式的最大值  
//             am.getStreamVolume(AudioManager.STREAM_VOICE_CALL);//得到听筒模式的当前值  
             mediaPlayer.setVolume(1f,1f);
             mediaPlayer.seekTo(0);      
             mediaPlayer.start();
         } catch (IllegalStateException e) {                 
             e.printStackTrace();
         } catch (Exception e) {                 
             e.printStackTrace();
         }
     }
	
	private void dismissAnimation(){
		float height = mBulletinLayout.getHeight();
		ObjectAnimator animator = ObjectAnimator.ofFloat(mBulletinLayout, "y", 0, 0 - height).setDuration(300);
		animator.setInterpolator(new AccelerateInterpolator());
		animator.setRepeatCount(0);
		animator.setRepeatMode(ValueAnimator.RESTART);
		animator.addListener(new AnimatorListener() {
			
			@Override
			public void onAnimationStart(Animator arg0) { }
			
			@Override
			public void onAnimationRepeat(Animator arg0) { }
			
			@Override
			public void onAnimationEnd(Animator arg0) { 
				mBulletinLayout.setVisibility(View.GONE);
				if(msg.size() != 0){
					handler.postDelayed(new Runnable() {
						
						@Override
						public void run() {
							showAnimation();
						}
					}, 1500);
				}
			}
			
			@Override
			public void onAnimationCancel(Animator arg0) { }
		});
		AnimatorSet dismiss = new AnimatorSet();
		dismiss.play(animator);
		dismiss.start();
	}
	

	
	public void addBulletin(final String bulletin, final String uid){
		handler.post(new Runnable() {
			
			@Override
			public void run() {
				
				if(appBackgroud()){
				    msg.offer(bulletin);
				    showAnimation();
				}else{
				    sendNotification(bulletin, uid);
				}
				
			}
		});
	}

	private boolean appBackgroud(){
	   ArrayList<BaseActivity> activities = AIApplication.getInstance().getActivities();
	   for (BaseActivity activity : activities) {
	       if(activity.isDisplay()){
	           return true;
	       }
	   }
	   return false;
	}
	
	private void sendNotification(String nickname, String uid){
	    Context context = AIApplication.getInstance().getApplicationContext();
	    // 创建一个通知  
        Notification mNotification = new Notification();  
  
        // 设置属性值  
        mNotification.icon = R.drawable.wa_logo;  
        mNotification.tickerText = "来之好友的哇";  
        mNotification.when = System.currentTimeMillis(); // 立即发生此通知  
  
        // 添加震动,后来得知需要添加震动权限 : Virbate Permission  
        mNotification.defaults |= Notification.DEFAULT_VIBRATE ;
        // 添加声音效果  
        mNotification.sound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.wa2);
  
        //添加状态标志   
  
        //FLAG_AUTO_CANCEL          该通知能被状态栏的清除按钮给清除掉  
        //FLAG_NO_CLEAR             该通知能被状态栏的清除按钮给清除掉  
        //FLAG_ONGOING_EVENT        通知放置在正在运行  
        //FLAG_INSISTENT            通知的音乐效果一直播放  
        mNotification.flags = Notification.FLAG_AUTO_CANCEL ;  
        mNotification.number += 1;  
       
        //将该通知显示为默认View  
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,new Intent(context, com.tien.ai.MainActivity.class), 0);  
        SpannableString spannableString = new SpannableString(nickname + " 给你发了一个哇");
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#ff5722")), 0, nickname.length(),
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mBulletin.setText(spannableString);
        mNotification.setLatestEventInfo(AIApplication.getInstance().getApplicationContext(), "哇~", spannableString, contentIntent);  
          
        // 设置setLatestEventInfo方法,如果不设置会App报错异常  
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);  
        int num = 0;
        try {
            num = Integer.parseInt(uid);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            num = (int)(Math.random()*1000);
        }
        mNotificationManager.notify(num, mNotification);  
	}
	/**
	 * 清除悬浮窗口
	 */
	public void clearPop() {

		XLog.d("////////////clearPop/////////////");

		if (attach) {

			wm.removeView(view);
			attach = false;
		}
	}

	/**
	 * 绑定悬浮窗口
	 */
	public void attachPop() {

		XLog.d("////////////attachPop/////////////：" + attach);

		if (!attach) {
			wm.addView(view, wmParams);
			attach = true;
		}
	}

	@Override
	public void update(Observable observable, Object data) {
//		if (data != null && data instanceof NotificationItem) {
//			NotificationItem item = (NotificationItem) data;
//			if (item.getType() == NotificationItem.TYPE_APP_VISIBILITY_CHANGED) {
//				if ((Boolean) item.getMessage()) {
//					attachPop();
//				} else {
//					clearPop();
//				}
//			} 
//		}
	}

}
