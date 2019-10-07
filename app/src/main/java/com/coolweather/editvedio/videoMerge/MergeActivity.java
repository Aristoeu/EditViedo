package com.coolweather.editvedio.videoMerge;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.coolweather.editvedio.BaseActivity;
import com.coolweather.editvedio.R;
import com.coolweather.editvedio.utils.StorageUtil;
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
import nl.bravobit.ffmpeg.FFmpeg;

public class MergeActivity extends BaseActivity implements View.OnClickListener,VideoMergeContract.VideoMergeView {

    private static final int CHOOSE_FILE = 11;
    private TextView tv_add;
    private ProgressDialog mProgressDialog;
    private VideoMergePresenter presenter;

    @Override
    protected void initUI() {
        setContentView(R.layout.activity_merge);
        tv_add =  findViewById(R.id.tv_add);
        initProgressDialog();
        findViewById(R.id.bt_add).setOnClickListener(this);
        findViewById(R.id.bt_merge).setOnClickListener(this);
        findViewById(R.id.bt_glide).setOnClickListener(this);
        findViewById(R.id.bt_merge_normal).setOnClickListener(this);
        findViewById(R.id.bt_glide_normal).setOnClickListener(this);
        presenter = new VideoMergePresenter(this,MergeActivity.this);
    }

    @Override
    public void initProgressDialog() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setMax(100);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setTitle("正在处理");
        mProgressDialog.setProgress(0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_add:
                presenter.chooseFile();
                break;
            case R.id.bt_merge:
                presenter.mergeVideo(true,true);
                break;
            case R.id.bt_glide:
                presenter.setGlideByLc(true);
                presenter.mergeWithGlide();
                break;
            case R.id.bt_merge_normal:
                presenter.mergeVideo(false,true);
                break;
            case R.id.bt_glide_normal:
                presenter.setGlideByLc(false);
                presenter.mergeWithGlide();
                break;
        }
    }


    @Override
    public void startVideo(Intent intent) {
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
                    presenter.addToVideoList(videoUrl);
                    break;
                }
        }
    }

    @Override
    public void UpdateProgressDialog(float v) {
        mProgressDialog.setProgress((int) (v * 100));
    }

    @Override
    public void playVideo(String outPath) {
        Intent v = new Intent(Intent.ACTION_VIEW);
        v.setDataAndType(Uri.parse(outPath), "video/mp4");
        startActivity(v);
    }



    @Override
    public void showDialogue(int i) {
        mProgressDialog.setProgress(i);
        mProgressDialog.show();
    }


    @Override
    public void setTextProgress(int s, int cnt) {
        mProgressDialog.setTitle("正在进行第"+s+"步，进度:"+cnt);
    }
    @Override
    public void dissmissProgressDialog() {
        mProgressDialog.dismiss();
    }


}

//下面的代码重构完都不要了
// private List<EpVideo> videoList;
// private boolean isGlideByLc = false;
// private String[] outputFiles = new String[4];
// private int step = 0;
// final String outPath = Environment.getExternalStorageDirectory() + "/outmerge.mp4";


/*
 * 选择文件
 */
  /*  private void chooseFile() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startVideo(intent);
    }*/
/*   private void addToVideoList(String videoUrl) {
        videoList.add(new EpVideo(videoUrl));
    }*/

  /*  OnEditorListener onEditorListener = new OnEditorListener() {
        @Override
        public void onSuccess() {
            UIThreadUtil.runOnUiThread(() -> Toast.makeText(MergeActivity.this, "success", Toast.LENGTH_SHORT).show());
            dissmissProgressDialog();

            playVideo(outPath);
        }

        @Override
        public void onFailure() {
            UIThreadUtil.runOnUiThread(() -> Toast.makeText(MergeActivity.this, "failed", Toast.LENGTH_SHORT).show());
            dissmissProgressDialog();
        }

        @Override
        public void onProgress(float v) {
            UpdateProgressDialog(v);
        }

    };
*/
/*
 * 合并视频
 */
   /* private void mergeVideo(boolean isByLc,boolean isUsingList) {
        if (videoList.size() > 1) {
            showDialogue(0);
            if (isByLc && isUsingList)
            EpEditor.mergeByLc(this,videoList, new EpEditor.OutputOption(outPath), onEditorListener);
            else if (isUsingList)EpEditor.merge(videoList,new EpEditor.OutputOption(outPath),onEditorListener);
            else {
                List<EpVideo> epVideoList = new ArrayList<>();
                epVideoList.add(new EpVideo(outputFiles[2]));
                epVideoList.add(new EpVideo(outputFiles[3]));
                if (isGlideByLc)EpEditor.mergeByLc(this,epVideoList,new EpEditor.OutputOption(outPath),onEditorListener);
                else EpEditor.merge(epVideoList,new EpEditor.OutputOption(outPath),onEditorListener);
            }
        } else {
            UIThreadUtil.runOnUiThread(() -> Toast.makeText(MergeActivity.this, "请至少添加两个视频哦", Toast.LENGTH_SHORT).show());
        }
    }*/
   /* private void mergeWithGlide() {
       if (videoList.size() > 1) {

           for (int i = 0;i<4;i++)outputFiles[i] = "";
            UIThreadUtil.runOnUiThread(() -> Toast.makeText(MergeActivity.this, "转场合并默认只处理前两个视频哦", Toast.LENGTH_SHORT).show());
            // 默认用list(0)反转，得到process1,再glide in 0:50，得到process2,再反转，得到process3，将list(1) glide in 0:50,得到process4，
            // 然后拼接process3和process4，得到transition.mp4并打开
           step = 0;
           process(videoList.get(0).getVideoPath(),nameVideo(step)," -vf reverse ");

       }else {
            UIThreadUtil.runOnUiThread(() -> Toast.makeText(MergeActivity.this, "请至少添加两个视频哦", Toast.LENGTH_SHORT).show());
        }
    }*/

    /*ExecuteBinaryResponseHandler executeBinaryResponseHandler = new ExecuteBinaryResponseHandler() {
        int cnt = 0;
        @Override public void onSuccess(String s) {
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
            showDialogue(50);
        }

        @Override
        public void onFailure(String message) {
            dissmissProgressDialog();
            UIThreadUtil.runOnUiThread(() -> Toast.makeText(getApplicationContext(), "failed", Toast.LENGTH_SHORT).show());
            Log.d("<<<>>>","failed");
        }

        @Override
        public void onFinish() {
        }

        @Override
        public void onProgress(String message) {
            int s = step+1;
            cnt++;
            setTextProgress(s,cnt);
        }

    };
    */
  /*  private void process(String beginPath,String afterPath,String execCommand) {
        String cmd = "-i "+beginPath+execCommand+afterPath;
        String[] command = cmd.split(" ");
        try {
            FFmpeg.getInstance(getApplicationContext()).execute(command, executeBinaryResponseHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

  /*  private String nameVideo(int i) {
        if (!outputFiles[i].equals(""))return outputFiles[i];
        final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        final String outputName =  timeStamp + ".mp4";

        outputFiles[i] = StorageUtil.getCacheDir() + "/" + outputName;
        return outputFiles[i];
    }*/
