package com.coolweather.editvedio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.coolweather.editvedio.photo.CutPhotoActivity;
import com.coolweather.editvedio.photo.PhotoMergeActivity;
import com.coolweather.editvedio.video.EditActivity;
import com.coolweather.editvedio.video.MergeActivity;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.bt_one).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, EditActivity.class));
            }
        });
        findViewById(R.id.bt_more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MergeActivity.class));
            }
        });
        findViewById(R.id.bt_cut_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, CutPhotoActivity.class));
            }
        });
        findViewById(R.id.bt_merge_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, PhotoMergeActivity.class));
            }
        });
    }
}
