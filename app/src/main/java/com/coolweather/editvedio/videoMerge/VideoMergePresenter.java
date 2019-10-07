package com.coolweather.editvedio.videoMerge;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.coolweather.editvedio.utils.StorageUtil;
import com.coolweather.editvedio.utils.UIThreadUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import VideoHandle.EpEditor;
import VideoHandle.EpVideo;
import VideoHandle.OnEditorListener;
import nl.bravobit.ffmpeg.ExecuteBinaryResponseHandler;
import nl.bravobit.ffmpeg.FFmpeg;

public class VideoMergePresenter implements VideoMergeContract.VideoMergePresenter {
    private VideoMergeContract.VideoMergeView mergeView;
    private Context context;
    private List<EpVideo> videoList;
    private String outPath;
    private String[] outputFiles = new String[4];
    private OnEditorListener onEditorListener;
    private ExecuteBinaryResponseHandler executeBinaryResponseHandler;
    private int step;
    private boolean isGlideByLc = false;


    VideoMergePresenter(VideoMergeContract.VideoMergeView mergeView,Context context){
        this.mergeView = mergeView;
        this.context = context;
        this.videoList = new ArrayList<>();
        this.outPath = Environment.getExternalStorageDirectory() + "/outmerge.mp4";
        for (int i = 0;i<4;i++)outputFiles[i] = "";
        step = 0;
        this.onEditorListener = new OnEditorListener() {
            @Override
            public void onSuccess() {
                UIThreadUtil.runOnUiThread(() -> Toast.makeText(context, "success", Toast.LENGTH_SHORT).show());
                mergeView.dissmissProgressDialog();

                mergeView.playVideo(outPath);
            }

            @Override
            public void onFailure() {
                UIThreadUtil.runOnUiThread(() -> Toast.makeText(context, "failed", Toast.LENGTH_SHORT).show());
                mergeView.dissmissProgressDialog();
            }

            @Override
            public void onProgress(float v) {
                mergeView.UpdateProgressDialog(v);
            }

        };
        this.executeBinaryResponseHandler = new ExecuteBinaryResponseHandler() {
            int cnt = 0;
            @Override public void onSuccess(String s) {
                UIThreadUtil.runOnUiThread(() -> Toast.makeText(context, "success", Toast.LENGTH_SHORT).show());
                step++;
                switch (step){
                    case 1:process(outputFiles[0],nameVideo(1)," -vf fade=in:0:50 ");break;
                    case 2:process(outputFiles[1],nameVideo(2)," -vf reverse ");break;
                    case 3:process(videoList.get(1).getVideoPath(),nameVideo(3)," -vf fade=in:0:50 ");break;
                    case 4:mergeVideo(false,false);
                }
            }

            @Override public void onStart() {
                mergeView.showDialogue(50);
            }

            @Override
            public void onFailure(String message) {
                mergeView.dissmissProgressDialog();
                UIThreadUtil.runOnUiThread(() -> Toast.makeText(context, "failed", Toast.LENGTH_SHORT).show());
                Log.d("<<<>>>","failed");
            }

            @Override
            public void onFinish() {
            }

            @Override
            public void onProgress(String message) {
                int s = step+1;
                cnt++;
                mergeView.setTextProgress(s,cnt);
            }

        };
    }
    @Override
    public void chooseFile() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        mergeView.startVideo(intent);

    }

    void setGlideByLc(boolean isGlideByLc){
        this.isGlideByLc = isGlideByLc;
    }

    @Override
    public void mergeVideo(boolean isByLc, boolean isUsingList) {
        if (videoList.size() > 1) {
            mergeView.showDialogue(0);
            if (isByLc && isUsingList)
                EpEditor.mergeByLc(context,videoList, new EpEditor.OutputOption(outPath), onEditorListener);
            else if (isUsingList)EpEditor.merge(videoList,new EpEditor.OutputOption(outPath),onEditorListener);
            else {
                List<EpVideo> epVideoList = new ArrayList<>();
                epVideoList.add(new EpVideo(outputFiles[2]));
                epVideoList.add(new EpVideo(outputFiles[3]));
                if (isGlideByLc)EpEditor.mergeByLc(context,epVideoList,new EpEditor.OutputOption(outPath),onEditorListener);
                else EpEditor.merge(epVideoList,new EpEditor.OutputOption(outPath),onEditorListener);
            }
        } else {
            UIThreadUtil.runOnUiThread(() -> Toast.makeText(context, "请至少添加两个视频哦", Toast.LENGTH_SHORT).show());
        }
    }

    @Override
    public void mergeWithGlide() {
        if (videoList.size() > 1) {

            for (int i = 0;i<4;i++)outputFiles[i] = "";
            UIThreadUtil.runOnUiThread(() -> Toast.makeText(context, "转场合并默认只处理前两个视频哦", Toast.LENGTH_SHORT).show());
            // 默认用list(0)反转，得到process1,再glide in 0:50，得到process2,再反转，得到process3，将list(1) glide in 0:50,得到process4，
            // 然后拼接process3和process4，得到transition.mp4并打开
            step = 0;
            process(videoList.get(0).getVideoPath(),nameVideo(step)," -vf reverse ");

        }else {
            UIThreadUtil.runOnUiThread(() -> Toast.makeText(context, "请至少添加两个视频哦", Toast.LENGTH_SHORT).show());
        }
    }

    @Override
    public String nameVideo(int i) {
        if (!outputFiles[i].equals(""))return outputFiles[i];
        final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        final String outputName =  timeStamp + ".mp4";

        outputFiles[i] = StorageUtil.getCacheDir() + "/" + outputName;
        return outputFiles[i];
    }

    @Override
    public void process(String beginPath, String afterPath, String execCommand) {
        String cmd = "-i "+beginPath+execCommand+afterPath;
        String[] command = cmd.split(" ");
        try {
            FFmpeg.getInstance(context).execute(command, executeBinaryResponseHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addToVideoList(String videoUrl) {
        videoList.add(new EpVideo(videoUrl));
    }


}
