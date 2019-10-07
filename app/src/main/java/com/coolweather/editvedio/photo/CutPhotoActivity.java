package com.coolweather.editvedio.photo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.coolweather.editvedio.BaseActivity;
import com.coolweather.editvedio.R;
import com.itheima.library.PhotoView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CutPhotoActivity extends BaseActivity implements View.OnClickListener, PhotoEditContract.PhotoCutView {
    private static final String TAG = "MainActivity";
    private static final int REQUEST_CROP = 1;// 裁剪
    private static final int SCAN_OPEN_PHONE = 2;// 相册
    private static final int REQUEST_PERMISSION = 100;
    private PhotoView img;
    private Uri mCutUri;// 图片裁剪时返回的uri
    private boolean hasPermission = false;
    private PhotoEditPresenter presenter;

    @Override
    protected void initUI() {
        setContentView(R.layout.activity_cut_photo);
        presenter = new PhotoEditPresenter(this);
        findViewById(R.id.btn_open_photo_album).setOnClickListener(this);
        findViewById(R.id.btn_result).setOnClickListener(this);
        img = findViewById(R.id.iv);
        img.enable();
        checkPermissions();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_open_photo_album:
                checkPermissions();
                if (hasPermission) {
                    openGallery();
                }
                break;
            case R.id.btn_result:
                Toast.makeText(this,"裁剪后的图片已经成功保存到手机根目录下的take_photo文件夹中",Toast.LENGTH_SHORT).show();
            default:
                break;
        }
    }

    @Override
    public void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, SCAN_OPEN_PHONE);
    }


    @Override
    public void checkPermissions() {
        // 检查是否有存储和拍照权限
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        ) {
            hasPermission = true;
        } else {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, REQUEST_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                hasPermission = true;
            } else {
                Toast.makeText(this, "权限授予失败！", Toast.LENGTH_SHORT).show();
                hasPermission = false;
            }
        }
    }

    // 图片裁剪
    @Override
    public void startCrop(Uri uri, Intent intent, File mCutFile) {
        mCutUri = Uri.fromFile(mCutFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mCutUri);
        Toast.makeText(this, "剪裁图片", Toast.LENGTH_SHORT).show();
        // 以广播方式刷新系统相册，以便能够在相册中找到刚刚所拍摄和裁剪的照片
        Intent intentBc = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intentBc.setData(uri);
        this.sendBroadcast(intentBc);

        startActivityForResult(intent, REQUEST_CROP); //设置裁剪参数显示图片至ImageVie
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                // 裁剪后设置图片
                case REQUEST_CROP:
                    img.setImageURI(mCutUri);
                    Log.e(TAG, "onActivityResult: imgUri:REQUEST_CROP:" + mCutUri.toString());
                    break;
                // 打开图库获取图片并进行裁剪
                case SCAN_OPEN_PHONE:
                    Log.e(TAG, "onActivityResult: SCAN_OPEN_PHONE:" + data.getData().toString());
                    presenter.cropPhoto(data.getData());
                    break;

                default:
                    break;
            }
        }
    }

}
