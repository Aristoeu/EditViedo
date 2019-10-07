package com.coolweather.editvedio.videoEdit;

import VideoHandle.EpVideo;

public interface VideoEditContract {

    interface VideoEditView{

        void dismissDialog();

        void showDialog();

        void setDialogProgress(int i);

        void playVideo(String outPath);

        void addFeatures(EpVideo epVideo);

    }

    interface VideoEditPresenter{

        void execVideo(String videoUrl, int id);

    }

}
