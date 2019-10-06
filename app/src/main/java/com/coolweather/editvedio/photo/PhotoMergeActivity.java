package com.coolweather.editvedio.photo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.coolweather.editvedio.BaseActivity;
import com.coolweather.editvedio.R;
import com.coolweather.editvedio.utils.ToastUtil;
import com.coolweather.editvedio.utils.UriUtils;
import com.itheima.library.PhotoView;
import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.functions.Consumer;

import static com.coolweather.editvedio.utils.PhotoUtils.*;

//不删了
public class PhotoMergeActivity extends BaseActivity {
    private Bitmap bitmap1 = null,bitmap2 = null;
    private PhotoView imageView1,imageView2;

    @Override
    protected void initUI() {
        setContentView(R.layout.activity_photo_merge);
        imageView1 = findViewById(R.id.photo_1);
        imageView2 = findViewById(R.id.photo_2);
        imageView1.enable();
        imageView2.enable();
        findViewById(R.id.bt_choose_1).setOnClickListener(view -> checkPermissions(1));
        findViewById(R.id.bt_choose_2).setOnClickListener(view -> checkPermissions(2));
        findViewById(R.id.bt_merge_x).setOnClickListener(view -> setMergedPhoto(true));
        findViewById(R.id.bt_merge_y).setOnClickListener(view -> setMergedPhoto(false));
        findViewById(R.id.bt_merge_more).setOnClickListener(view -> startActivity(new Intent(PhotoMergeActivity.this, PhotoMerge2Activity.class)));
    }

    private void setMergedPhoto(boolean isX) {
        if (bitmap1 != null && bitmap2 != null) {
            bitmap1 = isX ? newXBitmap(bitmap1, bitmap2) : newYBitmap(bitmap1,bitmap2);
            saveBitmapFile(bitmap1);
            imageView1.setImageBitmap(bitmap1);
        }
    }

    private void checkPermissions(int i) {
        if (ContextCompat.checkSelfPermission(PhotoMergeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(PhotoMergeActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, i);
        } else {
            openAlbum(i);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum(1);
                } else {
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            case 2:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum(2);
                } else {
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }
    private void openAlbum(int n) {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,n); // 打开相册
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    displayImage(UriUtils.getPath(this,data.getData()),1);
                }
                break;
            case 2:
                if (resultCode == RESULT_OK) {
                    displayImage(UriUtils.getPath(this,data.getData()),2);
                }
                break;
        }
    }

    private void displayImage(String imagePath,int n) {
        if (imagePath != null) {
            switch (n){
                case 1:
                    bitmap1 = BitmapFactory.decodeFile(imagePath);
                    imageView1.setImageBitmap(bitmap1);
                    break;
                case 2:
                    bitmap2 = BitmapFactory.decodeFile(imagePath);
                    imageView2.setImageBitmap(bitmap2);
                    break;
            }
        } else {
            Toast.makeText(this, "failed to get image", Toast.LENGTH_SHORT).show();
        }
    }

}
