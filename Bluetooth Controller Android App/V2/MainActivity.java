package com.example.bluetoothshenanegans2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    Intent intent;
    String text;
    UIHandler handy;
    BlueHandler bHandle;
    Button connect;
    Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHandler = new Handler();
        intent = getIntent();

        text = intent.getStringExtra(HomeScreen.EXTRA_TEXT); // retrieves the text from the last activity
        handy = new UIHandler((Activity)this);
        handy.getTextView().setText(text);

        //bluetooth stuff happens here
        try{
           bHandle = new BlueHandler(text);
        } catch (IOException e) {
            e.printStackTrace();
            handy.getTextView().setText("Error 2");
        }
        handy.getTextView().append(" " + bHandle.startConnection());
        handy.getTextView().append(bHandle.toString());

        brodcast.run();

        connect = findViewById(R.id.connect);
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHandler.removeCallbacks(brodcast);
                bHandle.endConnection();
                finish();
            }
        });
    }

    private final Runnable brodcast = new Runnable(){
        @Override
        public void run() {

            mHandler.postDelayed(this,40);
            if (handy.getQ() == 1){
                bHandle.btWrite(handy.getCmdr().getCmd(), 7);
                //if (bHandle.btRead()) {
                //    handy.getTextView().append("" + bHandle.getBitBlaster().mPares());
                //    handy.getTextView().append(bHandle.getBitBlaster().toString(0));
                //} else {
                //    handy.getTextView().append("Error cant read 1");
                //}
//
            }
        }
    };
}