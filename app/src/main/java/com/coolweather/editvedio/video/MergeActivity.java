package com.coolweather.editvedio.video;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.coolweather.editvedio.R;
import com.coolweather.editvedio.utils.StorageUtil;
import com.coolweather.editvedio.utils.ToastUtil;
import com.coolweather.editvedio.utils.UIThreadUtil;
import com.coolweather.editvedio.utils.UriUtils;

import java.util.ArrayList;
import java.util.List;

import VideoHandle.EpEditor;
import VideoHandle.EpVideo;
import VideoHandle.OnEditorListener;
import nl.bravobit.ffmpeg.ExecuteBinaryResponseHandler;
import nl.bravobit.ffmpeg.FFcommandExecuteResponseHandler;
import nl.bravobit.ffmpeg.FFmpeg;

public class MergeActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int CHOOSE_FILE = 11;
    private TextView tv_add;
    private Button bt_add, bt_merge, bt_glide;
    private List<EpVideo> videoList;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merge);
        initView();
    }

    private void initView() {
        tv_add =  findViewById(R.id.tv_add);
        bt_add = findViewById(R.id.bt_add);
        bt_merge = findViewById(R.id.bt_merge);
        bt_glide = findViewById(R.id.bt_glide);
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_add:
                chooseFile();
                break;
            case R.id.bt_merge:
                mergeVideo();
                break;
            case R.id.bt_glide:
                merge2();
                break;

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

    /**
     * 合并视频
     */
    private void mergeVideo() {
        if (videoList.size() > 1) {
            mProgressDialog.setProgress(0);
            mProgressDialog.show();
            final String outPath = Environment.getExternalStorageDirectory() + "/outmerge.mp4";
            EpEditor.merge(videoList, new EpEditor.OutputOption(outPath), new OnEditorListener() {
                @Override
                public void onSuccess() {
                    //Toast.makeText(MergeActivity.this, "编辑完成:"+outPath, Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();

                    Intent v = new Intent(Intent.ACTION_VIEW);
                    v.setDataAndType(Uri.parse(outPath), "video/mp4");
                    startActivity(v);
                }

                @Override
                public void onFailure() {
                    //Toast.makeText(MergeActivity.this, "编辑失败", Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                }

                @Override
                public void onProgress(float v) {
                    mProgressDialog.setProgress((int) (v * 100));
                }

            });
        } else {
            //Toast.makeText(this, "至少添加两个视频", Toast.LENGTH_SHORT).show();
        }
    }
    private void glideMergeVideo(){
        //Toast.makeText(MergeActivity.this,videoList.size(),Toast.LENGTH_SHORT).show();
        //Log.d("<<<>>>","viudfhti");
        if (videoList.size() > 1) {
            mProgressDialog.setProgress(0);
            mProgressDialog.show();
            final String outPath = Environment.getExternalStorageDirectory() + "/outmerge.mp4";
            //String command = "-i "+videoList.get(0).getVideoPath()+" -i "+videoList.get(1).getVideoPath()+" -filter_complex \"[0:v][1:v] overlay=y='if(gte(t,2), -h+(t-2)*800, NAN):x=20\"  "+outPath;
            String command =/* "ffmpeg -i /storage/emulated/0/1.mp4 -i /storage/emulated/0/2.mp4 -vcodec libx264 -filter_complex \"[1:v]scale=1920x1080[v1]; [0:v][v1]overlay=x='if(gte(t,1), min(-w+(t-2)*2000, 0), NAN)':y=0[o]\" -map '[o]' -vcodec h264_videotoolbox -s 320x240 -map 1:1 -allow_sw 1 -y /storage/emulated/0/result.mp4";*/
                    //"ffmpeg -i /storage/emulated/0/1.mp4 -i /storage/emulated/0/2.mp4 -filter_complex \"blend=all_expr='A*(if(gte(T,3),1,T/3))+B*(1-(if(gte(T,3),1,T/3)))'\" /storage/emulated/0/result.mp4";// String command = "ffmpeg -y -i /storage/emulated/0/1.mp4 -vf boxblur=25:5 -preset superfast /storage/emulated/0/result.mp4";
                    "ffmpeg -i /storage/emulated/0/1.mp4 \"fade=in:5:8\" /storage/emulated/0/output.mp4";
            Toast.makeText(MergeActivity.this,command,Toast.LENGTH_SHORT).show();
            EpEditor.execCmd(command, 0, new OnEditorListener() {
                @Override
                public void onSuccess() {
                    mProgressDialog.dismiss();
                }

                @Override
                public void onFailure() {
                    //Toast.makeText(MergeActivity.this,"failed",Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onProgress(float progress) {
                    mProgressDialog.setProgress((int) (progress * 100));
                }
            });
        }
    }
    private void merge2(){
        String outputFile = StorageUtil.getCacheDir() + "/trimmed.mp4" ;
        //String command ="-i /storage/emulated/0/outmerge.mp4 \"fade=in:5:8\" "+outputFile;
        //String command = "-ss " + "00:00:01" + " -t " + "00:00:11" + " -accurate_seek" + " -i " + "/storage/emulated/0/outmerge.mp4" + " -codec copy -avoid_negative_ts 1 " + outputFile;
        String command = "-i /storage/emulated/0/download/3.mp4 -i /storage/emulated/0/download/5.mp4 -filter_complex \"blend=all_expr='A*(if(gte(T,3),1,T/3))+B*(1-(if(gte(T,3),1,T/3)))'\" "+outputFile;
        String[] cmd = command.split(" ");
        FFmpeg.getInstance(this).execute(cmd, new FFcommandExecuteResponseHandler() {
            @Override
            public void onSuccess(String message) {
                UIThreadUtil.runOnUiThread(() -> Toast.makeText(MergeActivity.this, "success", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onProgress(String message) {

            }

            @Override
            public void onFailure(String message) {
                UIThreadUtil.runOnUiThread(() -> Toast.makeText(MergeActivity.this, "failed", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onStart() {
                UIThreadUtil.runOnUiThread(() -> Toast.makeText(MergeActivity.this, "start", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onFinish() {
                UIThreadUtil.runOnUiThread(() -> Toast.makeText(MergeActivity.this, "finish", Toast.LENGTH_SHORT).show());
            }
        });
    }
}