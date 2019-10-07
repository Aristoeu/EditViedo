package com.coolweather.editvedio.videoMerge;

import android.content.Intent;

public interface VideoMergeContract {

    interface VideoMergeView  {

        void initProgressDialog();

        void showDialogue(int i);

        void startVideo(Intent intent);

        void UpdateProgressDialog(float v);

        void playVideo(String outPath);

        void dissmissProgressDialog();

        void setTextProgress(int s,int cnt);

    }

    interface VideoMergePresenter{

        void chooseFile();

        void mergeVideo(boolean isByLc, boolean isUsingList);

        void mergeWithGlide();

        String nameVideo(int i);

        void process(String beginPath, String afterPath, String execCommand);

        void addToVideoList(String videoUrl);

    }

}
