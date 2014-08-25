/**
 * 
 */
package com.tien.ai;

import com.tien.ai.utils.PreferenceUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

/**
 * TODO
 * @author wangtianfei01
 *
 */
public class Splash extends Activity {
    
   
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.splash_layout);
        
        new Handler().postDelayed(new Runnable() {
            
            @Override
            public void run() {
                if(!"".equals(PreferenceUtils.loadToken())){
                    Intent intent = new Intent(Splash.this, MainActivity.class);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(Splash.this, LoginActivity.class);
                    startActivity(intent);
                }
                finish();
                
            }
        }, 1500);
    }
    
}
