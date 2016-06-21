package com.example.administrator.phoneplayer.activity.adapter;

import android.content.Context;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.phoneplayer.R;
import com.example.administrator.phoneplayer.activity.domain.MediaItem;
import com.example.administrator.phoneplayer.activity.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/6/16.
 */
public class Local_video_adapter extends BaseAdapter {
    private List<MediaItem> list = new ArrayList<>();
    private Context context;
    private Utils utils;

    public Local_video_adapter(Context context, List list) {
        this.context = context;
        this.list = list;

    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MediaItem item = list.get(position);
        ViewHolder viewHolder;
        utils = new Utils();
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = View.inflate(context, R.layout.local_video_listview_item_layout, null);
            viewHolder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
            viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            viewHolder.tv_duration = (TextView) convertView.findViewById(R.id.tv_duration);
            viewHolder.tv_size = (TextView) convertView.findViewById(R.id.tv_size);


            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.iv_icon.setImageResource(R.drawable.video_default_icon);
        viewHolder.tv_name.setText(item.getName());
        viewHolder.tv_duration.setText(utils.stringForTime((int) item.getDuration()));
        viewHolder.tv_size.setText(Formatter.formatFileSize(context, item.getSize()));


        return convertView;
    }


    static class ViewHolder {
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_duration;
        TextView tv_size;


    }


}
