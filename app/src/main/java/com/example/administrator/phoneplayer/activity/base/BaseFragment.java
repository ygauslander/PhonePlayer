package com.example.administrator.phoneplayer.activity.base;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.widget.AdapterView;

/**
 * Created by Administrator on 2016/6/16.
 * 作用：本地视频，本地音乐，网络视频，网络音乐页面的基类或者公共类或者父类
 */

public abstract class BaseFragment  extends Fragment implements AdapterView.OnItemClickListener{
    protected Context context;

    public boolean hasInit = false;

    public abstract void initData();
    public BaseFragment(){

    }

    public BaseFragment(Context context){
        this.context = context;
    }





}
