package com.example.bluetoothshenanegans2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    Intent intent;
    String text;
    UIHandler handy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        intent = getIntent();
        text = intent.getStringExtra(HomeScreen.EXTRA_TEXT);
        handy = new UIHandler((Activity)this);
        handy.getTextView().setText(text);
    }


}