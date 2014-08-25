/**
 * 
 */
package com.tien.ai;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import com.tien.ai.view.LoadingDialog;

/**
 * TODO
 * @author wangtianfei01
 *
 */
public class BaseActivity extends Activity {
    
    private boolean isDisplay = false;
    private LoadingDialog loadingDialog;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        AIApplication.getInstance().getActivities().add(this);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        
        this.isDisplay = true;
        
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        
        this.isDisplay = false;
    }

    public boolean isDisplay() {
        return isDisplay;
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        AIApplication.getInstance().getActivities().add(this);
    }
    
    public void startLoading() {
        this.endLoading();
        
        this.loadingDialog = LoadingDialog.createDialog(this);
        this.loadingDialog.show();
    }
    
    public void endLoading(){
        if(loadingDialog != null) loadingDialog.dismiss();
    }
    
}
