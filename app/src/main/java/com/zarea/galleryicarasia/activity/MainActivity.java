package com.zarea.galleryicarasia.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.zarea.galleryicarasia.R;
import com.zarea.galleryicarasia.fragment.GridViewFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new GridViewFragment())
                    .commit();
        }
    }
}
