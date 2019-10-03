package com.coolweather.editvedio;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.scrat.app.selectorlibrary.ImageSelector;
import static com.coolweather.editvedio.PhotoUtils.*;

import java.util.ArrayList;
import java.util.List;


public class PhotoMerge2Activity extends AppCompatActivity {
    private static final int REQUEST_CODE_SELECT_IMG = 1;
    private static final int MAX_SELECT_COUNT = 9;
    List<String> paths = new ArrayList<>();
    List<Bitmap> bitmaps = new ArrayList<>();
    TextView mContentTv;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_merge2);
        findViewById(R.id.choose2photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageSelector.show(PhotoMerge2Activity.this,REQUEST_CODE_SELECT_IMG,MAX_SELECT_COUNT);
            }
        });
        mContentTv = findViewById(R.id.tv);
        imageView = findViewById(R.id.image2view);
        findViewById(R.id.btn_mergeX).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!bitmaps.isEmpty())
                    imageView.setImageBitmap(drawMulti(bitmaps,true));
            }
        });
        findViewById(R.id.btn_mergeY).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!bitmaps.isEmpty())
                    imageView.setImageBitmap(drawMulti(bitmaps,false));
            }
        });
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
    private Bitmap drawMulti(List<Bitmap> bitmaps,boolean isX) {/*
        int width = 0;
        int height = 0;

        //获取最大宽度
        for (Bitmap bitmap : bitmaps) {
            height = height + bitmap.getHeight();
            if (width < bitmap.getWidth()) {
                width = bitmap.getWidth();
            }
        }

        Bitmap newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        int tempHeight = 0;
        //画图
        for (Bitmap bitmap : bitmaps) {
            canvas.drawBitmap(bitmap, 0, tempHeight, null);
            tempHeight = bitmap.getHeight() + tempHeight;
            bitmap.recycle();
        }
        return newBitmap;
*/
    if (bitmaps.size()==1)return bitmaps.get(0);
    Bitmap res = null;
    for (Bitmap bitmap : bitmaps){
        if (isX)
        res = newXBitmap(res,bitmap);
        else res = newYBitmap(res,bitmap);
    }
    if (res!=null)
    saveBitmapFile(res);
    return res;
    }



}
