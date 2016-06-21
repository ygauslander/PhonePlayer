package com.example.administrator.phoneplayer.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;

import com.example.administrator.phoneplayer.R;


public class SplashActivity extends Activity {
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                startNewActivity();


            }
        }, 2000);


    }


    private void startNewActivity() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    //点击进入主界面

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        startNewActivity();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }


        return super.onTouchEvent(event);
    }
}
