package com.coolweather.editvedio.videoTrimmer;

public interface VideoTrimListener {
    void onStartTrim();
    void onFinishTrim(String url);
    void onCancel();
}
