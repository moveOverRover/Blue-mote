package com.example.bluetoothshenenanegans3;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private Intent intent;
    private String text;
    private UIHandler handy;
    private BlueHandler bHandle;
    private Button connect;
    private Handler mHandler;

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

        bHandle.startConnection();
        Toast.makeText(getApplicationContext(), "Connected to:\n" + bHandle.toString(), Toast.LENGTH_LONG).show();

        brodcast.run();

        connect = findViewById(R.id.connect_to_bluetooth);
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHandler.removeCallbacks(brodcast);
                bHandle.endConnection();
                finish();
            }
        });
    }

    byte [] myBytes = new byte[5];
    private final Runnable brodcast = new Runnable(){
        @Override
        public void run() {

            mHandler.postDelayed(this,40);
            if (handy.getQ() == 1 && !handy.getBtnPressed()){
                bHandle.btWrite(handy.getCmdr().getCmd(), 7);
                /*
                try {
                   if(bHandle.getIn().available() > 0){

                       for( int i=0; i<5; i++){
                           myBytes[i] = (byte) bHandle.getIn().read();
                       }
                       handy.getTextView().setText(bHandle.getBitBlaster().bytes2String(myBytes, 5));
                        bHandle.getBitBlaster().setRet(myBytes);

                       //bHandle.btRead();
                       //handy.getTextView().setText(bHandle.getBitBlaster().cSum(bHandle.getBitBlaster().getRet(),3));
                       handy.getTextView().append("\n" + bHandle.getBitBlaster().mPares());
                       handy.getTextView().append(bHandle.getBitBlaster().toString(0));
                       
                       if (bHandle.btRead()) {
                           handy.getTextView().append("" + bHandle.getBitBlaster().mPares());
                           handy.getTextView().append(bHandle.getBitBlaster().toString(0));
                       } else {
                           handy.getTextView().append("Error cant read 1");
                       }

                   }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                */
            }
        }
    };
}