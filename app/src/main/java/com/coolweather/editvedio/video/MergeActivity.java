package com.coolweather.editvedio.video;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.coolweather.editvedio.R;
import com.coolweather.editvedio.utils.StorageUtil;
import com.coolweather.editvedio.utils.ToastUtil;
import com.coolweather.editvedio.utils.UIThreadUtil;
import com.coolweather.editvedio.utils.UriUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import VideoHandle.EpEditor;
import VideoHandle.EpVideo;
import VideoHandle.OnEditorListener;
import nl.bravobit.ffmpeg.ExecuteBinaryResponseHandler;
import nl.bravobit.ffmpeg.FFcommandExecuteResponseHandler;
import nl.bravobit.ffmpeg.FFmpeg;

public class MergeActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int CHOOSE_FILE = 11;
    private TextView tv_add;
    private Button bt_add, bt_merge, bt_glide, bt_merge_normal;
    private List<EpVideo> videoList;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merge);
        initView();
        //videoList.add("/storage/emulated/0/DCIM/Camera/5.mp4");
        //videoList.add("/storage/emulated/0/DCIM/Camera/6.mp4");
    }

    private void initView() {
        tv_add =  findViewById(R.id.tv_add);
        bt_add = findViewById(R.id.bt_add);
        bt_merge = findViewById(R.id.bt_merge);
        bt_glide = findViewById(R.id.bt_glide);
        bt_merge_normal = findViewById(R.id.bt_merge_normal);
        videoList = new ArrayList<>();
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setMax(100);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setTitle("正在处理");
        bt_add.setOnClickListener(this);
        bt_merge.setOnClickListener(this);
        bt_glide.setOnClickListener(this);
        bt_merge_normal.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_add:
                chooseFile();
                break;
            case R.id.bt_merge:
                mergeVideo(true,true);
                break;
            case R.id.bt_glide:
                merge2();
                break;
            case R.id.bt_merge_normal:
                mergeVideo(false,true);
        }
    }

    /**
     * 选择文件
     */
    private void chooseFile() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, CHOOSE_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CHOOSE_FILE:
                if (resultCode == RESULT_OK) {
                    String videoUrl = UriUtils.getPath(MergeActivity.this, data.getData());
                    tv_add.setText(tv_add.getText() + videoUrl + "\n");
                    videoList.add(new EpVideo(videoUrl));
                    break;
                }
        }
    }
    final String outPath = Environment.getExternalStorageDirectory() + "/outmerge.mp4";
    OnEditorListener onEditorListener = new OnEditorListener() {
        @Override
        public void onSuccess() {
            UIThreadUtil.runOnUiThread(() -> Toast.makeText(MergeActivity.this, "success", Toast.LENGTH_SHORT).show());
            mProgressDialog.dismiss();

            Intent v = new Intent(Intent.ACTION_VIEW);
            v.setDataAndType(Uri.parse(outPath), "video/mp4");
            startActivity(v);
        }

        @Override
        public void onFailure() {
            UIThreadUtil.runOnUiThread(() -> Toast.makeText(MergeActivity.this, "failed", Toast.LENGTH_SHORT).show());
            mProgressDialog.dismiss();
        }

        @Override
        public void onProgress(float v) {
            mProgressDialog.setProgress((int) (v * 100));
        }

    };
    /**
     * 合并视频
     */
    private void mergeVideo(boolean isByLc,boolean isUsingList) {
        if (videoList.size() > 1) {
            mProgressDialog.setProgress(0);
            mProgressDialog.show();
            if (isByLc && isUsingList)
            EpEditor.mergeByLc(this,videoList, new EpEditor.OutputOption(outPath), onEditorListener);
            else if (isUsingList)EpEditor.merge(videoList,new EpEditor.OutputOption(outPath),onEditorListener);
            else {
                List<EpVideo> epVideoList = new ArrayList<>();
                epVideoList.add(new EpVideo(outputFiles[2]));
                epVideoList.add(new EpVideo(outputFiles[3]));
                EpEditor.mergeByLc(this,epVideoList,new EpEditor.OutputOption(outPath),onEditorListener);
            }
        } else {
            UIThreadUtil.runOnUiThread(() -> Toast.makeText(MergeActivity.this, "请至少添加两个视频哦", Toast.LENGTH_SHORT).show());
        }
    }
    private String[] outputFiles = new String[4];
    private int step = 0;
    private void merge2() {
       if (videoList.size() > 1) {

           for (int i = 0;i<4;i++)outputFiles[i] = "";
            UIThreadUtil.runOnUiThread(() -> Toast.makeText(MergeActivity.this, "转场合并默认只处理前两个视频哦", Toast.LENGTH_SHORT).show());
            //默认用list(0)反转，得到process1,再glide in 0:50，得到process2,再反转，得到process3，将list(1) glide in 0:50,得到process4，
            // 然后拼接process3和process4，得到transition.mp4并打开
           step = 0;
           process(videoList.get(0).getVideoPath(),nameVideo(step)," -vf reverse ");

       }else {
            UIThreadUtil.runOnUiThread(() -> Toast.makeText(MergeActivity.this, "请至少添加两个视频哦", Toast.LENGTH_SHORT).show());
        }
    }
    ExecuteBinaryResponseHandler executeBinaryResponseHandler = new ExecuteBinaryResponseHandler() {
        int cnt = 0;
        @Override public void onSuccess(String s) {
            //if (step==3)mProgressDialog.dismiss();
            UIThreadUtil.runOnUiThread(() -> Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_SHORT).show());
            step++;
            switch (step){
                case 1:process(outputFiles[0],nameVideo(1)," -vf fade=in:0:50 ");break;
                case 2:process(outputFiles[1],nameVideo(2)," -vf reverse ");break;
                case 3:process(videoList.get(1).getVideoPath(),nameVideo(3)," -vf fade=in:0:50 ");break;
                case 4:mergeVideo(false,false);
            }
        }

        @Override public void onStart() {
            mProgressDialog.setProgress(50);
            mProgressDialog.show();
        }

        @Override
        public void onFailure(String message) {
            mProgressDialog.dismiss();
            UIThreadUtil.runOnUiThread(() -> Toast.makeText(getApplicationContext(), "failed", Toast.LENGTH_SHORT).show());
            Log.d("<<<>>>","failed");
        }

        @Override
        public void onFinish() {
            //if (step==3)mProgressDialog.dismiss();
            //Log.d("<<<>>>","finish");
        }

        @Override
        public void onProgress(String message) {
            int s = step+1;
            mProgressDialog.setTitle("正在进行第"+s+"步，进度:"+cnt++);
            //Log.d("<<<>>>","progress");
            //UIThreadUtil.runOnUiThread(() -> Toast.makeText(getApplicationContext(), "process", Toast.LENGTH_SHORT).show());
        }
    };
    private void process(String beginPath,String afterPath,String execCommand) {
        String cmd = "-i "+beginPath+execCommand+afterPath;
        //Log.d("<<<>>>","inputfile: "+inputFile+"  outputfile: "+outputFile);
        //String cmd = "-ss " + start + " -t " + duration + " -accurate_seek" + " -i " + inputFile + " -codec copy -avoid_negative_ts 1 " + outputFile;
        //String cmd = "-ss " + start + " -i " + inputFile + " -ss " + start + " -t " + duration + " -vcodec copy " + outputFile;
        //{"ffmpeg", "-ss", "" + startTime, "-y", "-i", inputFile, "-t", "" + induration, "-vcodec", "mpeg4", "-b:v", "2097152", "-b:a", "48000", "-ac", "2", "-ar", "22050", outputFile}
        //String cmd = "-ss " + start + " -y " + "-i " + inputFile + " -t " + duration + " -vcodec " + "mpeg4 " + "-b:v " + "2097152 " + "-b:a " + "48000 " + "-ac " + "2 " + "-ar " + "22050 "+ outputFile;
        String[] command = cmd.split(" ");
        try {
            FFmpeg.getInstance(getApplicationContext()).execute(command, executeBinaryResponseHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String nameVideo(int i) {
        if (!outputFiles[i].equals(""))return outputFiles[i];
        final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        final String outputName =  timeStamp + ".mp4";

        outputFiles[i] = StorageUtil.getCacheDir() + "/" + outputName;
        return outputFiles[i];
    }
}



//String command ="-i /storage/emulated/0/outmerge.mp4 \"fade=in:5:8\" "+outputFile;
//String command = "-ss " + "00:00:01" + " -t " + "00:00:11" + " -accurate_seek" + " -i " + "/storage/emulated/0/outmerge.mp4" + " -codec copy -avoid_negative_ts 1 " + outputFile;
    /*        String command = "-i "+"/storage/emulated/0/outmerge.mp4"+" -vf reverse "+outputFile;

            //UIThreadUtil.runOnUiThread(() -> Toast.makeText(MergeActivity.this, videoList.get(0).getVideoPath(), Toast.LENGTH_SHORT).show());

            mProgressDialog.setProgress(0);
            mProgressDialog.show();

            String[] cmd = command.split(" ");
            //cnt = 0;

            FFmpeg.getInstance(this).execute(cmd, new ExecuteBinaryResponseHandler() {
                @Override
                public void onSuccess(String message) {
                    //Log.d("<<<>>>","success");
                    mProgressDialog.dismiss();
                    UIThreadUtil.runOnUiThread(() -> Toast.makeText(MergeActivity.this, "success", Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onProgress(String message) {
                    //Log.d("<<<>>>","progress");
                    //cnt++;
                    //if (cnt%10==0)
                    UIThreadUtil.runOnUiThread(() -> Toast.makeText(MergeActivity.this, "progress" Toast.LENGTH_SHORT).show());
                    //mProgressDialog.setTitle(cnt+"");
                }

                @Override
                public void onFailure(String message) {
                    // Log.d("<<<>>>","failed");
                    mProgressDialog.dismiss();
                    UIThreadUtil.runOnUiThread(() -> Toast.makeText(MergeActivity.this, "failed", Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onStart() {
                    // Log.d("<<<>>>","start");
                    UIThreadUtil.runOnUiThread(() -> Toast.makeText(MergeActivity.this, "start", Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onFinish() {
                    // Log.d("<<<>>>","finish");
                    UIThreadUtil.runOnUiThread(() -> Toast.makeText(MergeActivity.this, "finish", Toast.LENGTH_SHORT).show());
                }
            });*/