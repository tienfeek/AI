package com.tien.ai.view;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.SectionIndexer;

/**
 * 
 * <p>Title: SideBar</p>
 * <p>Description:快速导航栏 </p>
 * @author wangtf
 * @date 2014-1-22
 */
public class SideBar extends View {
	private char[] chars;
	private SectionIndexer sectionIndexter = null;
	private ListView mListview;
	//private TextView mDialogText;
	private int mItemHeight = 26;
	private int selected = 0;
	
	public SideBar(Context context) {
		super(context);
		init();
	}
	public SideBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	private void init() {
		chars = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S',
				'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
		//setBackgroundColor(0x44FFFFFF);
	}
	public SideBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	public void setListView(ListView list) {
		this.mListview = list;
		sectionIndexter = (SectionIndexer) list.getAdapter();
	}
//	public void setTextView(TextView mDialogText) {
//		this.mDialogText = mDialogText;
//	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		//空出4px
		mItemHeight = (MeasureSpec.getSize(heightMeasureSpec) - 4)/ (chars.length);
	}
	
//	public void setSelected(int position){
//		selected = position;
//		int c = sectionIndexter.getSectionForPosition(position);
//		for(int i=0; i<chars.length; i++){
//			if(c == chars[i]){
//				selected = i;
//			}
//		}
//		this.invalidate();
//	}
	
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		int i = (int) event.getY();
		int idx = i / mItemHeight;
		if (idx >= chars.length) {
			idx = chars.length - 1;
		} else if (idx < 0) {
			idx = 0;
		}
		if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
			//setBackgroundResource(R.drawable.scrollbar_bg);
			//滑动的时候显示当前选中的值
//			mDialogText.setVisibility(View.VISIBLE);
//			//第一个位置显示“search”， 表示搜索框
//			if (idx == 0) {
//				mDialogText.setText("Search");
//				mDialogText.setTextSize(16);
//			} else {//其余位置，显示当前的索引值
//				mDialogText.setText(String.valueOf(chars[idx]));
//				mDialogText.setTextSize(20);
//			}
			if (sectionIndexter == null) {
				sectionIndexter = (SectionIndexer) mListview.getAdapter();
			}
			int position = sectionIndexter.getPositionForSection(chars[idx]);
			if (position == -1) {
				return true;
			}
			mListview.setSelection(position);
//			setSelected(position);
		}else {
			//索引栏未触发事件时， 隐藏显示的文字
//			mDialogText.setVisibility(View.INVISIBLE);
		}
		
		if (event.getAction() == MotionEvent.ACTION_UP) {
			setBackgroundDrawable(new ColorDrawable(0x00000000));
		}
		
		return true;
	}
	protected void onDraw(Canvas canvas) {
		Paint paint = new Paint();
		paint.setColor(Color.parseColor("#000000"));
		paint.setTextSize(20);
		//paint.setStrokeWidth(3);
		paint.setAntiAlias(true);
		paint.setTextAlign(Paint.Align.CENTER);
		float widthCenter = getMeasuredWidth() / 2;
		for (int i = 0; i < chars.length; i++) {
			canvas.drawText(String.valueOf(chars[i]), widthCenter, mItemHeight + (i * mItemHeight), paint);
			//if(i == selected){
				//canvas.drawCircle(widthCenter/2, mItemHeight + (i * mItemHeight)+mItemHeight/2, mItemHeight/2, paint);
			//}
		}
		super.onDraw(canvas);
	}
}