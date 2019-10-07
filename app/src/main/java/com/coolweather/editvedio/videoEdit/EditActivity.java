package com.coolweather.editvedio.videoEdit;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.coolweather.editvedio.BaseActivity;
import com.coolweather.editvedio.R;
import com.coolweather.editvedio.utils.UriUtils;
import com.coolweather.editvedio.videoTrimmer.VideoTrimmerActivity;

import VideoHandle.EpVideo;


public class EditActivity extends BaseActivity implements View.OnClickListener, VideoEditContract.VideoEditView {

    private static final int CHOOSE_FILE = 10;
    private CheckBox cb_clip, cb_crop, cb_rotation, cb_mirror;
    private EditText et_clip_start, et_clip_end, et_crop_x, et_crop_y, et_crop_w, et_crop_h, et_rotation;
    private TextView tv_file;
    private String videoUrl;
    private ProgressDialog mProgressDialog;
    private RadioGroup radioGroup;
    private VideoEditPresenter videoEditPresenter;

    @Override
    protected void initUI() {
        setContentView(R.layout.activity_edit);
        videoEditPresenter = new VideoEditPresenter(this,EditActivity.this);
        radioGroup = findViewById(R.id.radio_group);
        cb_clip =  findViewById(R.id.cb_clip);
        cb_crop =  findViewById(R.id.cb_crop);
        cb_rotation =  findViewById(R.id.cb_rotation);
        cb_mirror =  findViewById(R.id.cb_mirror);
        et_clip_start =  findViewById(R.id.et_clip_start);
        et_clip_end =  findViewById(R.id.et_clip_end);
        et_crop_x =  findViewById(R.id.et_crop_x);
        et_crop_y =  findViewById(R.id.et_crop_y);
        et_crop_w =  findViewById(R.id.et_crop_w);
        et_crop_h =  findViewById(R.id.et_crop_h);
        et_rotation =  findViewById(R.id.et_rotation);
        tv_file =  findViewById(R.id.tv_file);
        findViewById(R.id.bt_file).setOnClickListener(this);
        findViewById(R.id.bt_exec).setOnClickListener(this);
        findViewById(R.id.bt_preview).setOnClickListener(this);
        cb_mirror.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                cb_rotation.setChecked(true);
            }
        });
        cb_rotation.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(!isChecked){
                cb_mirror.setChecked(false);
            }
        });
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setMax(100);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setTitle("正在处理");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_file:
                chooseFile();
                break;
            case R.id.bt_exec:
                videoEditPresenter.execVideo(videoUrl,radioGroup.getCheckedRadioButtonId());
                break;
            case R.id.bt_preview:
               if(videoUrl != null && !"".equals(videoUrl)){
                    VideoTrimmerActivity.call(EditActivity.this,videoUrl);
                }
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
                    videoUrl = UriUtils.getPath(EditActivity.this, data.getData());
                    tv_file.setText(videoUrl);
                    break;
                }
        }
    }

    /**
     * 开始编辑
     */

    @Override
    public void addFeatures(EpVideo epVideo) {
        if(cb_clip.isChecked())
            epVideo.clip(Float.parseFloat(et_clip_start.getText().toString().trim()),Float.parseFloat(et_clip_end.getText().toString().trim()));
        if(cb_crop.isChecked())
            epVideo.crop(Integer.parseInt(et_crop_w.getText().toString().trim()),Integer.parseInt(et_crop_h.getText().toString().trim()),Integer.parseInt(et_crop_x.getText().toString().trim()),Integer.parseInt(et_crop_y.getText().toString().trim()));
        if(cb_rotation.isChecked())
            epVideo.rotation(Integer.parseInt(et_rotation.getText().toString().trim()),cb_mirror.isChecked());
    }

    @Override
    public void playVideo(String outPath) {
        Intent v = new Intent(Intent.ACTION_VIEW);
        v.setDataAndType(Uri.parse(outPath), "video/mp4");
        startActivity(v);
    }

    @Override
    public void dismissDialog() {
        mProgressDialog.dismiss();
    }

    @Override
    public void showDialog() {
        mProgressDialog.show();
    }

    @Override
    public void setDialogProgress(int i) {
        mProgressDialog.setProgress(i);
    }

}