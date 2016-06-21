package com.example.administrator.phoneplayer.activity.Fragments;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.administrator.phoneplayer.R;
import com.example.administrator.phoneplayer.activity.SystemVideoPlayerActivity;
import com.example.administrator.phoneplayer.activity.adapter.Local_video_adapter;
import com.example.administrator.phoneplayer.activity.base.BaseFragment;
import com.example.administrator.phoneplayer.activity.domain.MediaItem;
import com.example.administrator.phoneplayer.activity.utils.Utils;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/6/16.
 */
public class Local_video_fragment extends BaseFragment {
    private ListView listView;
    private ArrayList<MediaItem> datas = new ArrayList<>();
    private TextView noVideo_textview;
    private Local_video_adapter adapter;
    private Utils utils;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //不知道怎么回事扫描不到视频，但音乐能扫描出来
//            MediaItem item = new MediaItem("thth", 100, 200, "cxc", "dsfsf");
//            datas.add(item);
            if (datas.size() == 0) {
                //没有数据显示TextView提示没有视频

                noVideo_textview.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);

            } else {
                //设置适配器
                adapter = new Local_video_adapter(context, datas);
                listView.setAdapter(adapter);

                noVideo_textview.setVisibility(View.GONE);


            }

        }
    };

    public Local_video_fragment() {

    }

    public Local_video_fragment(Context context) {
        super(context);
    }

    @Override
    public void initData() {
        getDataFromLocal();


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initData();
        View view = inflater.inflate(R.layout.local_video_fragment_layout, container, false);
        noVideo_textview = (TextView) view.findViewById(R.id.tv_novideo);
        listView = (ListView) view.findViewById(R.id.loacal_video_listview);
        listView.setOnItemClickListener(this);


        return view;
    }

    private void getDataFromLocal() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                ContentResolver resolver = context.getContentResolver();

                String[] objs = {
                        MediaStore.Video.Media.DISPLAY_NAME,//视频在sdcard显示的名称
                        MediaStore.Video.Media.DURATION,//视频的长度
                        MediaStore.Video.Media.SIZE,//视频文件的大小
                        MediaStore.Video.Media.DATA,//视频播放时间
                        MediaStore.Video.Media.ARTIST//演唱者
                };
                Cursor cursor = resolver.query(uri, objs, null, null, null);
                if (cursor != null) {

                    while (cursor.moveToNext()) {

                        MediaItem mediaItem = new MediaItem();

                        String name = cursor.getString(0);//视频的名称
                        mediaItem.setName(name);

                        long duration = cursor.getLong(1);//视频的时长

                        mediaItem.setDuration(duration);

                        long size = cursor.getLong(2);//视频文件的大小
                        mediaItem.setSize(size);

                        String data = cursor.getString(3);//播放地址
                        mediaItem.setData(data);

                        String artist = cursor.getString(4);//艺术家
                        mediaItem.setArtist(artist);

                        datas.add(mediaItem);//把数据添加到集合中

                    }

                }

                //发现消息
                handler.sendEmptyMessage(0);


            }
        }.start();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MediaItem item = datas.get(position);
        //调用系统播放器
//        Intent intent = new Intent();
//        intent.setDataAndType(Uri.parse(item.getData()),"video/*");
//        context.startActivity(intent);

        //调用自己实现的视频播放器
        Intent intent = new Intent(context, SystemVideoPlayerActivity.class);
        // intent.setDataAndType(Uri.parse(item.getData()), "video/*");
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", datas);
        intent.putExtras(bundle);
        intent.putExtra("position", position);


        context.startActivity(intent);
        //不再只发送一个uri数据，而是发送整个视频列表给播放器


    }
}
