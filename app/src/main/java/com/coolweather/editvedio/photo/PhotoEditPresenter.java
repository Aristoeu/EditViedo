package com.coolweather.editvedio.photo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PhotoEditPresenter implements PhotoEditContract.PhotoEditPresenter {
    private PhotoEditContract.PhotoCutView photoCutView;
    PhotoEditPresenter(PhotoEditContract.PhotoCutView photoCutView){
        this.photoCutView = photoCutView;
    }
    @Override
    public void cropPhoto(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP"); //打开系统自带的裁剪图片的intent
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");

        // 注意一定要添加该项权限，否则会提示无法裁剪
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        intent.putExtra("scale", true);

        // 设置裁剪区域的宽高比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // 设置裁剪区域的宽度和高度
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);

        // 取消人脸识别
        intent.putExtra("noFaceDetection", true);
        // 图片输出格式
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

        // 若为false则表示不返回数据
        intent.putExtra("return-data", false);

        // 指定裁剪完成以后的图片所保存的位置,pic info显示有延时
        // 从相册中选择，那么裁剪的图片保存在take_photo中
        String time = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA).format(new Date());
        String fileName = "photo_" + time;
        File mCutFile = new File(Environment.getExternalStorageDirectory() + "/take_photo/", fileName + ".jpeg");
        if (!mCutFile.getParentFile().exists()) {
            mCutFile.getParentFile().mkdirs();
        }
        photoCutView.startCrop(uri, intent, mCutFile);
    }
}
