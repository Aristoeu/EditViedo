package com.coolweather.editvedio;

import android.content.Intent;
import android.view.View;
import android.widget.Button;

import androidx.annotation.IdRes;

import com.coolweather.editvedio.photo.CutPhotoActivity;
import com.coolweather.editvedio.photo.PhotoMergeActivity;
import com.coolweather.editvedio.videoEdit.EditActivity;
import com.coolweather.editvedio.videoMerge.MergeActivity;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

public class MainActivity extends BaseActivity {

    @Override
    protected void initUI() {
        setContentView(R.layout.activity_main);
        Button photo1 = findViewById(R.id.bt_cut_photo);
        Button photo2 = findViewById(R.id.bt_merge_photo);
        Button video1 = findViewById(R.id.bt_one);
        Button video2 = findViewById(R.id.bt_more);
        findViewById(R.id.bt_one).setOnClickListener(v -> startActivity(new Intent(MainActivity.this, EditActivity.class)));
        findViewById(R.id.bt_more).setOnClickListener(v -> startActivity(new Intent(MainActivity.this, MergeActivity.class)));
        findViewById(R.id.bt_cut_photo).setOnClickListener(view -> startActivity(new Intent(MainActivity.this, CutPhotoActivity.class)));
        findViewById(R.id.bt_merge_photo).setOnClickListener(view -> startActivity(new Intent(MainActivity.this, PhotoMergeActivity.class)));
        BottomBar bottomBar = findViewById(R.id.bottomBar);
        bottomBar.setOnTabSelectListener(tabId -> {
            if (tabId == R.id.tab_photo) {
                // The tab with id R.id.tab_favorites was selected,
                // change your content accordingly.
                photo1.setVisibility(View.VISIBLE);
                photo2.setVisibility(View.VISIBLE);
                video1.setVisibility(View.INVISIBLE);
                video2.setVisibility(View.INVISIBLE);
            }
            if (tabId == R.id.tab_video){
                photo1.setVisibility(View.INVISIBLE);
                photo2.setVisibility(View.INVISIBLE);
                video1.setVisibility(View.VISIBLE);
                video2.setVisibility(View.VISIBLE);
            }
        });
    }
}
