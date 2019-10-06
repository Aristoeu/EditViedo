package com.coolweather.editvedio.video;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.coolweather.editvedio.BaseActivity;
import com.coolweather.editvedio.R;
import com.coolweather.editvedio.utils.ToastUtil;
import com.coolweather.editvedio.utils.UriUtils;

import VideoHandle.EpEditor;
import VideoHandle.EpVideo;
import VideoHandle.OnEditorListener;


public class EditActivity extends BaseActivity implements View.OnClickListener {

    private static final int CHOOSE_FILE = 10;
    private CheckBox cb_clip, cb_crop, cb_rotation, cb_mirror;
    private EditText et_clip_start, et_clip_end, et_crop_x, et_crop_y, et_crop_w, et_crop_h, et_rotation;
    private TextView tv_file;
    private Button bt_file, bt_exec, bt_preview;
    private String videoUrl;
    private ProgressDialog mProgressDialog;
    private RadioGroup radioGroup;

    @Override
    protected void initUI() {
        setContentView(R.layout.activity_edit);
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
        bt_file =  findViewById(R.id.bt_file);
        bt_exec =  findViewById(R.id.bt_exec);
        bt_preview = findViewById(R.id.bt_preview);
        bt_file.setOnClickListener(this);
        bt_exec.setOnClickListener(this);
        bt_preview.setOnClickListener(this);
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
                execVideo();
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
    private void execVideo(){
        if(videoUrl != null && !"".equals(videoUrl)){
            EpVideo epVideo = new EpVideo(videoUrl);
            if(cb_clip.isChecked())
                epVideo.clip(Float.parseFloat(et_clip_start.getText().toString().trim()),Float.parseFloat(et_clip_end.getText().toString().trim()));
            if(cb_crop.isChecked())
                epVideo.crop(Integer.parseInt(et_crop_w.getText().toString().trim()),Integer.parseInt(et_crop_h.getText().toString().trim()),Integer.parseInt(et_crop_x.getText().toString().trim()),Integer.parseInt(et_crop_y.getText().toString().trim()));
            if(cb_rotation.isChecked())
                epVideo.rotation(Integer.parseInt(et_rotation.getText().toString().trim()),cb_mirror.isChecked());
            switch (radioGroup.getCheckedRadioButtonId()){
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
            mProgressDialog.setProgress(0);
            mProgressDialog.show();
            final String outPath = Environment.getExternalStorageDirectory() + "/out.mp4";
            EpEditor.exec(epVideo, new EpEditor.OutputOption(outPath), new OnEditorListener() {
                @Override
                public void onSuccess() {
                    ToastUtil.show(EditActivity.this,"编辑完成:"+outPath);
                    mProgressDialog.dismiss();

                    Intent v = new Intent(Intent.ACTION_VIEW);
                    v.setDataAndType(Uri.parse(outPath), "video/mp4");
                    startActivity(v);
                }

                @Override
                public void onFailure() {
                    ToastUtil.show(EditActivity.this, "编辑失败");
                    mProgressDialog.dismiss();
                }

                @Override
                public void onProgress(float v) {
                    mProgressDialog.setProgress((int) (v * 100));
                }
            });
        }else{
            ToastUtil.show(this,"选择一个视频");
        }
    }

}