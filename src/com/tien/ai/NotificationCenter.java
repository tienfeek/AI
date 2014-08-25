/**
 * 
 */
package com.tien.ai;

import java.util.Observable;


public class NotificationCenter extends Observable {

	private static volatile NotificationCenter mInstance = null;
	
	private NotificationCenter() {
		super();
	}
	
	public static NotificationCenter defaultCenter() {
		if (mInstance == null) {
			synchronized(NotificationCenter.class) {
				if (mInstance == null) {
					mInstance = new NotificationCenter();
				}
			}
		}
		return mInstance;
	}
	
	public void postNotification(Object data) {
		setChanged();
		notifyObservers(data);
	}
}
