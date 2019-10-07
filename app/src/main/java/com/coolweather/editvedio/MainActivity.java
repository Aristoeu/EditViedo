package com.coolweather.editvedio;

import android.content.Intent;

import com.coolweather.editvedio.photo.CutPhotoActivity;
import com.coolweather.editvedio.photo.PhotoMergeActivity;
import com.coolweather.editvedio.videoEdit.EditActivity;
import com.coolweather.editvedio.videoMerge.MergeActivity;

public class MainActivity extends BaseActivity {

    @Override
    protected void initUI() {
        setContentView(R.layout.activity_main);

        findViewById(R.id.bt_one).setOnClickListener(v -> startActivity(new Intent(MainActivity.this, EditActivity.class)));
        findViewById(R.id.bt_more).setOnClickListener(v -> startActivity(new Intent(MainActivity.this, MergeActivity.class)));
        findViewById(R.id.bt_cut_photo).setOnClickListener(view -> startActivity(new Intent(MainActivity.this, CutPhotoActivity.class)));
        findViewById(R.id.bt_merge_photo).setOnClickListener(view -> startActivity(new Intent(MainActivity.this, PhotoMergeActivity.class)));
    }
}
