package com.example.administrator.phoneplayer.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.administrator.phoneplayer.R;
import com.example.administrator.phoneplayer.activity.Fragments.Local_audio_fragment;
import com.example.administrator.phoneplayer.activity.Fragments.Local_video_fragment;
import com.example.administrator.phoneplayer.activity.Fragments.Net_audio_fragment;
import com.example.administrator.phoneplayer.activity.Fragments.Net_video_fragment;
import com.example.administrator.phoneplayer.activity.Fragments.Weixin_voicetalk_fragment;

public class MainActivity extends FragmentActivity implements  /*View.OnClickListener ,*/ RadioGroup.OnCheckedChangeListener {
    private RadioButton local_video;
    private RadioButton local_audio;
    private RadioButton net_video;
    private RadioButton net_audio;
    private RadioGroup rg_main;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rg_main = (RadioGroup) findViewById(R.id.rg_main);
        rg_main.setOnCheckedChangeListener(this);
        rg_main.check(R.id.rb_main_1);

//        local_video = (RadioButton) findViewById(R.id.rb_main_1);
//        local_audio = (RadioButton) findViewById(R.id.rb_main_2);
//        net_video = (RadioButton) findViewById(R.id.rb_main_3);
//        net_audio = (RadioButton) findViewById(R.id.rb_main_4);
//        local_video.setOnClickListener(this);
//        local_audio.setOnClickListener(this);
//        net_video.setOnClickListener(this);
//        net_audio.setOnClickListener(this);


    }

//    @Override
//    protected void onStart() {
//        //这个地方不太明白,怎么样一上来就默认在第一个fragment的界面？
    //使用RadioGroup的check方法
//        super.onStart();
//        local_video.callOnClick();
//
//    }
//
//    @Override
//    public void onClick(View v) {
//
//        FragmentManager manager = getSupportFragmentManager();
//        FragmentTransaction transaction = manager.beginTransaction();
//        switch (v.getId()) {
//            case R.id.rb_main_1:
//                // Toast.makeText(MainActivity.this, "rb_main_1", Toast.LENGTH_SHORT).show();
//                transaction.replace(R.id.fl_main, new Local_video_fragment(this));
//
//                break;
//            case R.id.rb_main_2:
//                //  Toast.makeText(MainActivity.this, "rb_main_2", Toast.LENGTH_SHORT).show();
//                transaction.replace(R.id.fl_main, new Local_audio_fragment(this));
//                break;
//            case R.id.rb_main_3:
//                //  Toast.makeText(MainActivity.this, "rb_main_3", Toast.LENGTH_SHORT).show();
//                transaction.replace(R.id.fl_main, new Net_video_fragment(this));
//                break;
//            case R.id.rb_main_4:
//                // Toast.makeText(MainActivity.this, "rb_main_4", Toast.LENGTH_SHORT).show();
//                transaction.replace(R.id.fl_main, new Net_audio_fragment(this));
//                break;
//
//        }
//        transaction.commit();
//
//    }

//使用这种方式写起来比较简单
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            default:
                position = 1;
                break;
            case R.id.rb_main_2:
                position = 2;
                break;
            case R.id.rb_main_3:
                position = 3;
                break;
            case R.id.rb_main_4:
                position = 4;
                break;
            case R.id.rb_main_5:
                position = 5;
                break;

        }
        setFragment();

    }

    private void setFragment() {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        switch (position) {
            case 1:

                transaction.replace(R.id.fl_main, new Local_video_fragment(this));

                break;
            case 2:

                transaction.replace(R.id.fl_main, new Local_audio_fragment(this));
                break;
            case 3:

                transaction.replace(R.id.fl_main, new Net_video_fragment(this));
                break;
            case 4:

                transaction.replace(R.id.fl_main, new Net_audio_fragment(this));
                break;

            case 5:

                transaction.replace(R.id.fl_main, new Weixin_voicetalk_fragment(this));
                break;

        }
        transaction.commit();
    }
}
