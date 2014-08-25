package com.tien.ai.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;
import com.tien.ai.R;



	/**
	 * @Description:EwaveProgressDialog
	 * @author:Tienfook Chang
	 * @see:
	 * @since:
	 * @copyright © 35.com
	 * @Date:2012-10-30
	 */
	public class LoadingDialog extends Dialog {

		private static LoadingDialog loadingDialog = null;
		
		private static final int MIN_SHOW_TIME = 500; // ms
		private static final int MIN_DELAY = 500; // ms

		private long mStartTime = -1;
		private boolean mPostedHide = false;
		private boolean mPostedShow = false;
		private boolean mDismissed = false;
		private Handler mHandler;
		
		public LoadingDialog(Context context) {
			super(context);
			this.mHandler = new Handler();
		}

		public LoadingDialog(Context context, int theme) {
			super(context, theme);
			this.mHandler = new Handler();
		}

		public static LoadingDialog createDialog(Context context) {
			
			loadingDialog = new LoadingDialog(context, R.style.NoTitleDialog);
			loadingDialog.setContentView(R.layout.loading);
			loadingDialog.setCancelable(false);
			loadingDialog.setCanceledOnTouchOutside(false);
			loadingDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
			
			return loadingDialog;
		}

		public void onWindowFocusChanged(boolean hasFocus) {

			if (loadingDialog == null) {
				return;
			}
			ImageView outerIV = (ImageView) loadingDialog.findViewById(R.id.loading_outer_iv);
			ImageView innerIV = (ImageView) loadingDialog.findViewById(R.id.loading_inner_iv);
			this.outerView(outerIV);
			this.innerView(innerIV);
		}

		
		private void innerView(View target){
			ObjectAnimator animator = ObjectAnimator.ofFloat(target, "alpha", 1.0f, 0.0f);
			animator.setDuration(400);
			animator.setRepeatCount(ValueAnimator.INFINITE);
			animator.setRepeatMode(ValueAnimator.REVERSE);
			animator.start();
		}
		
		private void outerView(View target){
			ObjectAnimator animator = ObjectAnimator.ofFloat(target, "rotation", 0, 360);
			animator.setDuration(1200);
			animator.setInterpolator(new LinearInterpolator());
			animator.setRepeatCount(ValueAnimator.INFINITE);
			//animator.setRepeatMode(ValueAnimator.REVERSE);
			animator.start();
		}
		
		@Override
		public void onAttachedToWindow() {
			super.onAttachedToWindow();
			removeCallbacks();
		}

		@Override
		public void onDetachedFromWindow() {
			super.onDetachedFromWindow();
			removeCallbacks();
		}

		private void removeCallbacks() {
			mHandler.removeCallbacks(mDelayedHide);
			mHandler.removeCallbacks(mDelayedShow);
		}

		/**
		 * Hide the progress view if it is visible. The progress view will not be hidden until it has been shown
		 * for at least a minimum show time. If the progress view was not yet visible, cancels showing the
		 * progress view.
		 */
		public void delayHide() {
			mDismissed = true;
			mHandler.removeCallbacks(mDelayedShow);
			long diff = System.currentTimeMillis() - mStartTime;
			if (diff >= MIN_SHOW_TIME || mStartTime == -1) {
				// The progress spinner has been shown long enough
				// OR was not shown yet. If it wasn't shown yet,
				// it will just never be shown.
				dismiss();
				loadingDialog = null;
			} else {
				// The progress spinner is shown, but not long enough,
				// so put a delayed message in to hide it when its been
				// shown long enough.
				if (!mPostedHide) {
					mHandler.postDelayed(mDelayedHide, MIN_SHOW_TIME - diff);
					mPostedHide = true;
				}
			}
		}

		/**
		 * Show the progress view after waiting for a minimum delay. If during that time, hide() is called, the
		 * view is never made visible.
		 */
		public void delayShow() {
			// Reset the start time.
			mStartTime = -1;
			mDismissed = false;
			mHandler.removeCallbacks(mDelayedHide);
			if (!mPostedShow) {
				mHandler.postDelayed(mDelayedShow, MIN_DELAY);
				mPostedShow = true;
			}
			
		}

		/**
		 * 
		 * @Description:setMessage
		 * @param strMessage
		 * @return
		 * @see:
		 * @since:
		 * @author:Tienfook Chang
		 * @date:2012-10-30
		 */
		public LoadingDialog setMessage(CharSequence strMessage) {
			TextView tvMsg = (TextView) loadingDialog.findViewById(R.id.loading_msg_tv);

			if (tvMsg != null) {
				tvMsg.setText(strMessage);
			}

			return loadingDialog;
		}
		
		
		private final Runnable mDelayedHide = new Runnable() {

			@Override
			public void run() {
				mPostedHide = false;
				mStartTime = -1;
				dismiss();
				loadingDialog = null;
			}
		};

		private final Runnable mDelayedShow = new Runnable() {

			@Override
			public void run() {
				mPostedShow = false;
				if (!mDismissed) {
					mStartTime = System.currentTimeMillis();
					show();
				}
			}
		};

	}

	
