package com.example.awei.slamshow;

import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class GlviewActivity extends AppCompatActivity {
    public MyGLview myGLview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modelview);
        LinearLayout linearLayout = findViewById(R.id.ll);
        myGLview = new MyGLview(this, "a");
        linearLayout.addView(myGLview);
        String plyUrl = getIntent().getStringExtra("plyUrl");
    }
    @Override
    protected void onStart() {
        super.onStart();
    }
    @Override protected void onPause() {
        super.onPause();
        myGLview.onPause();
    }
    @Override protected void onResume() {
        super.onResume();
    }

    @Override public void onConfigurationChanged(Configuration conf) {
        super.onConfigurationChanged(conf);
    }
    public void Reset(View v) {
        myGLview.Reset();
    }
}
