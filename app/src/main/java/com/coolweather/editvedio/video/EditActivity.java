package com.coolweather.editvedio.video;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.coolweather.editvedio.R;
import com.coolweather.editvedio.VideoTrimmerActivity;
import com.coolweather.editvedio.utils.UriUtils;

import VideoHandle.EpEditor;
import VideoHandle.EpVideo;
import VideoHandle.OnEditorListener;


public class EditActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int CHOOSE_FILE = 10;
    private CheckBox cb_clip, cb_crop, cb_rotation, cb_mirror;
    private EditText et_clip_start, et_clip_end, et_crop_x, et_crop_y, et_crop_w, et_crop_h, et_rotation;
    private TextView tv_file;
    private Button bt_file, bt_exec, bt_preview;
    private String videoUrl;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        initView();
    }

    private void initView() {
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
        bt_file =  findViewById(R.id.bt_file);
        bt_exec =  findViewById(R.id.bt_exec);
        bt_preview = findViewById(R.id.bt_preview);
        bt_file.setOnClickListener(this);
        bt_exec.setOnClickListener(this);
        bt_preview.setOnClickListener(this);
        cb_mirror.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    cb_rotation.setChecked(true);
                }
            }
        });
        cb_rotation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked){
                    cb_mirror.setChecked(false);
                }
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
                execVideo();
//				test();
                break;
            case R.id.bt_preview:
              /*   Bundle bundle = new Bundle();
                bundle.putString("video-file-path","/storage/emulated/0/download/1.mp4");
                Intent intent = new Intent(this,VideoTrimmerActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);*/
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
    private void execVideo(){
        if(videoUrl != null && !"".equals(videoUrl)){
            EpVideo epVideo = new EpVideo(videoUrl);
            if(cb_clip.isChecked())
                epVideo.clip(Float.parseFloat(et_clip_start.getText().toString().trim()),Float.parseFloat(et_clip_end.getText().toString().trim()));
            if(cb_crop.isChecked())
                epVideo.crop(Integer.parseInt(et_crop_w.getText().toString().trim()),Integer.parseInt(et_crop_h.getText().toString().trim()),Integer.parseInt(et_crop_x.getText().toString().trim()),Integer.parseInt(et_crop_y.getText().toString().trim()));
            if(cb_rotation.isChecked())
                epVideo.rotation(Integer.parseInt(et_rotation.getText().toString().trim()),cb_mirror.isChecked());
            mProgressDialog.setProgress(0);
            mProgressDialog.show();
            final String outPath = Environment.getExternalStorageDirectory() + "/out.mp4";
            EpEditor.exec(epVideo, new EpEditor.OutputOption(outPath), new OnEditorListener() {
                @Override
                public void onSuccess() {
                    //Toast.makeText(EditActivity.this, "编辑完成:"+outPath, Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();

                    Intent v = new Intent(Intent.ACTION_VIEW);
                    v.setDataAndType(Uri.parse(outPath), "video/mp4");
                    startActivity(v);
                }

                @Override
                public void onFailure() {
                    //Toast.makeText(EditActivity.this, "编辑失败", Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                }

                @Override
                public void onProgress(float v) {
                    mProgressDialog.setProgress((int) (v * 100));
                }
            });
        }else{
            //Toast.makeText(this, "选择一个视频", Toast.LENGTH_SHORT).show();
        }
    }

}