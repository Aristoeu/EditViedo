package com.coolweather.editvedio.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PhotoUtils {
    public static Bitmap resizeBitmap(Bitmap bitmap, int newWidth, int newHeight) {
        float scaleWidth = ((float) newWidth) / bitmap.getWidth();
        float scaleHeight = ((float) newHeight) / bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bmpScale = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return bmpScale;
    }
    public static Bitmap newXBitmap(Bitmap bmp1, Bitmap bmp2) {
        if (bmp1==null)return bmp2;
        Bitmap retBmp;
        int height = bmp1.getHeight();
        if (bmp2.getHeight() != height) {
            //以第一张图片的宽度为标准，对第二张图片进行缩放。

            int w2 = bmp2.getWidth() * height / bmp2.getHeight();
            retBmp = Bitmap.createBitmap(bmp1.getWidth()+w2, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(retBmp);
            Bitmap newSizeBmp2 = resizeBitmap(bmp2, w2, height);
            canvas.drawBitmap(bmp1, 0, 0, null);
            canvas.drawBitmap(newSizeBmp2, bmp1.getWidth(), 0, null);
        } else {
            //两张图片宽度相等，则直接拼接。

            retBmp = Bitmap.createBitmap(bmp1.getWidth()+bmp2.getWidth(), height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(retBmp);
            canvas.drawBitmap(bmp1, 0, 0, null);
            canvas.drawBitmap(bmp2, bmp1.getWidth(), 0, null);
        }

        return retBmp;
    }
    public static Bitmap newYBitmap(Bitmap bmp1, Bitmap bmp2) {
        if (bmp1==null)return bmp2;
        Bitmap retBmp;
        int width = bmp1.getWidth();
        if (bmp2.getWidth() != width) {
            //以第一张图片的宽度为标准，对第二张图片进行缩放。

            int h2 = bmp2.getHeight() * width / bmp2.getWidth();
            retBmp = Bitmap.createBitmap(width, bmp1.getHeight() + h2, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(retBmp);
            Bitmap newSizeBmp2 = resizeBitmap(bmp2, width, h2);
            canvas.drawBitmap(bmp1, 0, 0, null);
            canvas.drawBitmap(newSizeBmp2, 0, bmp1.getHeight(), null);
        } else {
            //两张图片宽度相等，则直接拼接。

            retBmp = Bitmap.createBitmap(width, bmp1.getHeight() + bmp2.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(retBmp);
            canvas.drawBitmap(bmp1, 0, 0, null);
            canvas.drawBitmap(bmp2, 0, bmp1.getHeight(), null);
        }

        return retBmp;
    }
    public static void saveBitmapFile(Bitmap bitmap) {
        String realfile = Environment.getExternalStorageDirectory().toString() + "/merge_result.jpg";
        Log.d("result", realfile);
        //Toast.makeText(this,realfile,Toast.LENGTH_SHORT).show();
        File file=new File(realfile);//将要保存图片的路径
        try{
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
