package com.tien.ai.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;


/**
 * @Description:
 * @author:wangtf
 * @see:   
 * @since:      
 * @copyright © baidu.com
 * @Date:2014-3-12
 */
public class DimensionUtil {
	
	
	public static int dip2px(int dpValue, Context context) {
		Resources r = context.getResources();
		int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, r.getDisplayMetrics());
		return px;
	}
	
	public static int sp2px(int spValue ,Context context){
		Resources r = context.getResources();
		int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, r.getDisplayMetrics());
		return px;
		
	}
	

}
