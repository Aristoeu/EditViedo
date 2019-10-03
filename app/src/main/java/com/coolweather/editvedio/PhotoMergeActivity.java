package com.coolweather.editvedio;

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
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.coolweather.editvedio.PhotoUtils.*;

//不删了
public class PhotoMergeActivity extends AppCompatActivity {
    private Bitmap bitmap1 = null,bitmap2 = null;
    private ImageView imageView1,imageView2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_merge);
        imageView1 = findViewById(R.id.photo_1);
        imageView2 = findViewById(R.id.photo_2);
        findViewById(R.id.bt_choose_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(PhotoMergeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(PhotoMergeActivity.this, new String[]{ Manifest.permission. WRITE_EXTERNAL_STORAGE }, 1);
                } else {
                    openAlbum(1);
                }
            }
        });
        findViewById(R.id.bt_choose_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(PhotoMergeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(PhotoMergeActivity.this, new String[]{ Manifest.permission. WRITE_EXTERNAL_STORAGE }, 2);
                } else {
                    openAlbum(2);
                }
            }
        });
        findViewById(R.id.bt_merge_x).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bitmap1!=null && bitmap2!=null) {
                    bitmap1 = newXBitmap(bitmap1,bitmap2);
                    saveBitmapFile(bitmap1);
                    imageView1.setImageBitmap(bitmap1);
                }
            }
        });
        findViewById(R.id.bt_merge_y).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bitmap1!=null && bitmap2!=null) {
                    bitmap1 = newYBitmap(bitmap1,bitmap2);
                    saveBitmapFile(bitmap1);
                    imageView1.setImageBitmap(bitmap1);
                }
            }
        });
        findViewById(R.id.bt_merge_more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PhotoMergeActivity.this,PhotoMerge2Activity.class));
            }
        });
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
                    handleImageOnKitKat(data, 1);
                }
                break;
            case 2:
                if (resultCode == RESULT_OK) {
                    handleImageOnKitKat(data, 2);
                }
                break;
        }
    }
    @TargetApi(19)
    private void handleImageOnKitKat(Intent data,int n) {
        String imagePath = null;
        Uri uri = data.getData();
        Log.d("TAG", "handleImageOnKitKat: uri is " + uri);
        if (DocumentsContract.isDocumentUri(this, uri)) {
            // 如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1]; // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        displayImage(imagePath,n); // 根据图片路径显示图片
    }
    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
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
