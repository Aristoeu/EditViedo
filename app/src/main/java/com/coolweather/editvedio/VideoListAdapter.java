package com.coolweather.editvedio;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class VideoListAdapter extends BaseAdapter {
    private Context context;
    private List<VideoInfo> videoInfos;
    public VideoListAdapter(Context context,List<VideoInfo> videoInfos){
        this.context = context;
        this.videoInfos = videoInfos;
    }
    @Override
    public int getCount() {
        return videoInfos.size();
    }

    @Override
    public Object getItem(int i) {
        return videoInfos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (view==null){
            holder = new ViewHolder();
            view = View.inflate(context,R.layout.item_video_list,null);
            holder.duration = view.findViewById(R.id.tv_time);
            holder.title = view.findViewById(R.id.tv_title);
            view.setTag(holder);
        }else {
            holder =(ViewHolder) view.getTag();
        }
        holder.title.setText(videoInfos.get(i).getTitle());
        holder.duration.setText(videoInfos.get(i).getPath());
        return view;
    }

    class ViewHolder{
        TextView title;
        TextView duration;
    }
}
