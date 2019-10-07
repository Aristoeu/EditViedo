package com.coolweather.editvedio.videoEdit;

import android.content.Context;
import android.os.Environment;

import com.coolweather.editvedio.R;
import com.coolweather.editvedio.utils.ToastUtil;

import VideoHandle.EpEditor;
import VideoHandle.EpVideo;
import VideoHandle.OnEditorListener;

public class VideoEditPresenter implements VideoEditContract.VideoEditPresenter {
    private VideoEditContract.VideoEditView videoEditView;
    private Context context;
    VideoEditPresenter(VideoEditContract.VideoEditView view, Context context){
        this.videoEditView = view;
        this.context = context;
    }
    @Override
    public void execVideo(String videoUrl, int id) {
        if(videoUrl != null && !"".equals(videoUrl)){
            EpVideo epVideo = new EpVideo(videoUrl);
            videoEditView.addFeatures(epVideo);
            switch (id){
                case R.id.rb_negate:
                    epVideo.addFilter("lutyuv=y=maxval+minval-val:u=maxval+minval-val:v=maxval+minval-val");break;
                case R.id.rb_pad://边框
                    epVideo.addFilter("pad=6/5*iw:6/5*ih:(ow-iw)/2:(oh-ih)/2:color=red");break;
                case R.id.rb_blur:
                    epVideo.addFilter("boxblur=10:1:cr=0:ar=0");break;
                case R.id.rb_draw_lines://加线
                    epVideo.addFilter("drawbox=x=-t:y=0.5*(ih-iw/2.4)-t:w=iw+t*2:h=iw/2.4+t*2:t=2:c=red");break;
                case R.id.rb_draw_grid://网格
                    epVideo.addFilter("drawgrid=width=100:height=100:thickness=2:color=black@0.9");break;
                case -1:break;
            }
            //epVideo.addFilter("[0:v][1:v]overlay=x='if(gte(t,0), -w+(mod(n, W+w))+5, NAN)':y=0[out]"); //这条指令没用
            videoEditView.setDialogProgress(0);
            videoEditView.showDialog();
            final String outPath = Environment.getExternalStorageDirectory() + "/out.mp4";
            EpEditor.exec(epVideo, new EpEditor.OutputOption(outPath), new OnEditorListener() {
                @Override
                public void onSuccess() {
                    ToastUtil.show(context,"编辑完成:"+outPath);
                    videoEditView.dismissDialog();

                    videoEditView.playVideo(outPath);
                }

                @Override
                public void onFailure() {
                    ToastUtil.show(context, "编辑失败");
                    videoEditView.dismissDialog();
                }

                @Override
                public void onProgress(float v) {
                    videoEditView.setDialogProgress((int) (v * 100));
                }
            });
        }else{
            ToastUtil.show(context,"选择一个视频");
        }
    }
}
