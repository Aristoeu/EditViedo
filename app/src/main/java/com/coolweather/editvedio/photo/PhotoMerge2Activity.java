package com.coolweather.editvedio.photo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.coolweather.editvedio.BaseActivity;
import com.coolweather.editvedio.R;
import com.itheima.library.PhotoView;
import com.scrat.app.selectorlibrary.ImageSelector;
import static com.coolweather.editvedio.utils.PhotoUtils.*;

import java.util.ArrayList;
import java.util.List;


public class PhotoMerge2Activity extends BaseActivity {
    private static final int REQUEST_CODE_SELECT_IMG = 1;
    private static final int MAX_SELECT_COUNT = 9;
    List<String> paths = new ArrayList<>();
    List<Bitmap> bitmaps = new ArrayList<>();
    TextView mContentTv;
    PhotoView imageView;
    private ProgressDialog mProgressDialog;

    @Override
    protected void initUI() {
        setContentView(R.layout.activity_photo_merge2);
        findViewById(R.id.choose2photo).setOnClickListener(view -> ImageSelector.show(PhotoMerge2Activity.this,REQUEST_CODE_SELECT_IMG,MAX_SELECT_COUNT));
        mContentTv = findViewById(R.id.tv);
        imageView = findViewById(R.id.image2view);
        imageView.enable();
        findViewById(R.id.btn_mergeX).setOnClickListener(view -> {
            if (!bitmaps.isEmpty()) {
                mProgressDialog.setProgress(30);
                mProgressDialog.show();
                imageView.setImageBitmap(drawMulti(bitmaps, true));
                mProgressDialog.dismiss();
            }
        });
        findViewById(R.id.btn_mergeY).setOnClickListener(view -> {
            if (!bitmaps.isEmpty()){
                mProgressDialog.setProgress(30);
                mProgressDialog.show();
                imageView.setImageBitmap(drawMulti(bitmaps,false));
                mProgressDialog.dismiss();
            }
        });
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setMax(100);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setTitle("正在处理，请稍候");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_SELECT_IMG) {
            paths = ImageSelector.getImagePaths(data);
            if (paths.isEmpty()) {
                mContentTv.setText("你还没有选择图片");
                return;
            }
            bitmaps.clear();
            for (String path : paths)
                bitmaps.add(BitmapFactory.decodeFile(path));
            mContentTv.setText("你已经选择了"+paths.size()+"张图片");

            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
