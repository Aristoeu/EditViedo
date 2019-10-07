package com.coolweather.editvedio.photo;

import android.content.Intent;
import android.net.Uri;

import java.io.File;

public interface PhotoEditContract {

    interface PhotoCutView{

        void openGallery();

        void checkPermissions();

        void startCrop(Uri uri, Intent intent, File mCutFile);

    }

    interface PhotoEditPresenter{

        void cropPhoto(Uri uri);

    }

    interface PhotoMergeView{

        void setMergedPhoto(boolean isX);

        void checkPermissions(int i);

        void openAlbum(int n);

        void displayImage(String imagePath,int n);


    }
}
