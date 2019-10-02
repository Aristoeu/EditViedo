package com.coolweather.editvedio;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import VideoHandle.EpEditor;
import VideoHandle.EpVideo;
import VideoHandle.OnEditorListener;

public class VideoListActivity extends AppCompatActivity {
    private GridView gridview;
    VideoListAdapter mAdapter;
    List<VideoInfo> listVideos;
    EditText begin,end;
    TextView path;
    Button choose,make;
    int videoSize;
    String videoPath = "";
    private static final int CHOOSE_FILE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);
        Toast.makeText(getApplicationContext(), Environment.getExternalStorageDirectory()+"/outVideo.mp4",Toast.LENGTH_SHORT).show();

        GetVideoInfoUtils provider = new GetVideoInfoUtils(this);
        listVideos = provider.getList();
        videoSize = listVideos.size();
        gridview = findViewById(R.id.gridview);
        begin = findViewById(R.id.et_begin);
        end = findViewById(R.id.et_end);
        path = findViewById(R.id.path);
        choose = findViewById(R.id.bt_choose);
        make = findViewById(R.id.bt_make);
        mAdapter = new VideoListAdapter(this, listVideos);
        gridview.setAdapter(mAdapter);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String url = listVideos.get(position).getPath();
                EpVideo epVideo = new EpVideo(url);
                epVideo.clip(1,5);
                final String oupath = Environment.getExternalStorageDirectory()+"/outVideo.mp4";
                //Toast.makeText(getApplicationContext(),oupath,Toast.LENGTH_SHORT).show();
                EpEditor.exec(epVideo, new EpEditor.OutputOption(oupath), new OnEditorListener() {
                    @Override
                    public void onSuccess() {
                        //  Toast.makeText(getApplicationContext(),"编辑完成",Toast.LENGTH_SHORT).show();
                        Intent v = new Intent(Intent.ACTION_VIEW);
                        v.setDataAndType(Uri.parse(oupath), "video/mp4");
                        startActivity(v);
                    }

                    @Override
                    public void onFailure() {
                        // Toast.makeText(getApplicationContext(),"编辑失败",Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onProgress(float progress) {

                    }
                });
            }
        });
        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, CHOOSE_FILE);
            }
        });
        make.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(begin.getText()) ||TextUtils.isEmpty(end.getText())) {
                    Toast.makeText(VideoListActivity.this, "请输入起止时间！！！", Toast.LENGTH_SHORT).show();
                }else if (videoPath.equals(""))
                    Toast.makeText(VideoListActivity.this, "请选择一个视频！！！", Toast.LENGTH_SHORT).show();
                else {
                    EpVideo epVideo = new EpVideo(videoPath);
                    float fbegin, fend;
                    fbegin = Float.parseFloat(begin.getText().toString());
                    fend = Float.parseFloat(end.getText().toString());

                    epVideo.clip(fbegin, fend - fbegin);
                    final String oupath = Environment.getExternalStorageDirectory() + "/outVideo.mp4";
                    //Toast.makeText(getApplicationContext(),oupath,Toast.LENGTH_SHORT).show();
                    EpEditor.exec(epVideo, new EpEditor.OutputOption(oupath), new OnEditorListener() {
                        @Override
                        public void onSuccess() {
                            //  Toast.makeText(getApplicationContext(),"编辑完成",Toast.LENGTH_SHORT).show();
                            Intent v = new Intent(Intent.ACTION_VIEW);
                            v.setDataAndType(Uri.parse(oupath), "video/mp4");
                            startActivity(v);
                        }

                        @Override
                        public void onFailure() {
                            // Toast.makeText(getApplicationContext(),"编辑失败",Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onProgress(float progress) {

                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case CHOOSE_FILE:
                if (resultCode==RESULT_OK){
                    videoPath = UriUtils.getPath(VideoListActivity.this,data.getData());
                    path.setText(videoPath);
                    break;
                }
        }
    }
}
