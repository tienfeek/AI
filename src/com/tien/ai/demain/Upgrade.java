package com.tien.ai.demain;


/**
 * @Description:
 * @author:wangtf
 * @see:   
 * @since:      
 * @copyright © baidu.com
 * @Date:2014-3-3
 */
public class Upgrade {
	
	public static final String UPGRDATE_TYPE = "0";
	public static final String UPGRDATE_FORECE_TYPE = "1";
	
	private boolean update = false;
	/**apk url*/
	private String url = "";
	/**正常更新 0, 强制更新*/
	private String forceUpdate = "";
	
    public boolean isUpdate() {
        return update;
    }
    public void setUpdate(boolean update) {
        this.update = update;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getForceUpdate() {
        return forceUpdate;
    }
    public void setForceUpdate(String forceUpdate) {
        this.forceUpdate = forceUpdate;
    }
	

	
}
