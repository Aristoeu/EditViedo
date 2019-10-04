package com.coolweather.editvedio.interfaces;

public interface VideoTrimListener {
    void onStartTrim();
    void onFinishTrim(String url);
    void onCancel();
}
