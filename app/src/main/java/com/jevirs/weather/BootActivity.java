package com.jevirs.weather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;


public class BootActivity extends ActionBarActivity {

    private static final String TIMES = "is the first time?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.boot);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences preferences = getSharedPreferences(TIMES, MODE_PRIVATE);
                Boolean isFirst = preferences.getBoolean(TIMES, true);
                if (isFirst) {
                    SharedPreferences.Editor editor = getSharedPreferences(TIMES, MODE_PRIVATE).edit();
                    editor.putBoolean(TIMES, false);
                    editor.apply();
                    setContentView(R.layout.activity_boot);
                    Button button = (Button) findViewById(R.id.go);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(BootActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                } else {
                    SharedPreferences.Editor editor = getSharedPreferences(TIMES, MODE_PRIVATE).edit();
                    editor.putBoolean(TIMES, false);
                    editor.apply();
                    Intent intent = new Intent(BootActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 2000);
    }
}