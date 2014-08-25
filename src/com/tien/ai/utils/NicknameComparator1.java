package com.tien.ai.utils;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import android.text.TextUtils;

import com.tien.ai.demain.Friend;;


/**
 * 
 * @Description:联系人列表排序
 * @author: 
 * @see:
 * @since:
 * @Date:2012-5-31
 */
public class NicknameComparator1 implements Comparator<Friend> {

	// 中文排序用
	private static final Collator COLLATOR_CN = Collator.getInstance(Locale.CHINA);
	// 前后都是闭区间
	private static boolean inRange11(int val, int from, int to) {
		return val >= from && val <= to;
	}

	// 是否字母,a-z,A-Z
	public static boolean isAlpha(char ch) {
		if (inRange11(ch, 'a', 'z') || inRange11(ch, 'A', 'Z')) {
			return true;
		}
		return false;
	}

	public int compare(Friend star1, Friend star2) {
		String s1 = "";
		String s2 = "";
		try {
			//字母
			if("".equals(star1.getNicknamePinyin())){
				s1 = PinyinUtils.getHanyuPinyin(star1.getNickname()).toUpperCase();
			}else{
				s1 = star1.getNicknamePinyin();
			}
			if("".equals(star2.getNicknamePinyin())){
				s2 = PinyinUtils.getHanyuPinyin(star2.getNickname()).toUpperCase();
			}else{
				s2 = star2.getNicknamePinyin();
			}
			
			
		} catch (BadHanyuPinyinOutputFormatCombination e) {
			e.printStackTrace();
		}
		
		
		//空串比较
		if(TextUtils.isEmpty(s1)) {
			if(TextUtils.isEmpty(s2)) {
				return 0;
			}else {
				return 1;
			}
		}else {
			if(TextUtils.isEmpty(s2)) {
				return -1;
			}else {
			}
		}
		
		 
		char ch1 = s1.charAt(0);
		char ch2 = s2.charAt(0);

		if(ch1 == ch2){
			// 同一字母开关，英文在前，汉字在后
			String d1 = star1.getNickname();
			String d2 = star2.getNickname();
			return COLLATOR_CN.compare(d1, d2);
		}
		
		if (ch1 < 128 && ch2 < 128) {
			if (isAlpha(ch1)) {
				if (isAlpha(ch2)) {

				} else {
					return 1;
				}
			} else {
				if (isAlpha(ch2)) {
					return -1;
				} else {
				}
			}
		}
		return s1.compareTo(s2);

	}
}
