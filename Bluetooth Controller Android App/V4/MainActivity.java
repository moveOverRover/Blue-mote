package com.example.bluetoothshenenagans4;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.io.IOException;

public class MainActivity extends AppCompatActivity{
    private Intent intent;
    private String text;
    private UIHandler UI;
    private BlueHandler bHandle;
    private Button connect;
    private Handler mHandler;
    private Button toLedFragment;
    private FrameLayout fragmentContainer;
    private int received = 0;
    private int sent = 0;
    private int badReturns = 0;
    private int averageTemp = 0;
    private float averageVoltage = 0;
    private final int rate = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHandler = new Handler();
        intent = getIntent();
        text = intent.getStringExtra(HomeScreen.EXTRA_TEXT); // retrieves the text from the last activity
        UI = new UIHandler((Activity)this);
        UI.getTextView().setText(text);

        //bluetooth stuff happens here
        try{
           bHandle = new BlueHandler(text, UI.getbitBlaster());
        } catch (IOException e) {
            e.printStackTrace();
            UI.getTextView().setText("Error 2");
        }

        bHandle.startConnection();
        Toast.makeText(getApplicationContext(), "Connected to:\n" + bHandle.toString(), Toast.LENGTH_LONG).show();
        brodcast.run();

        fragmentContainer = (FrameLayout) findViewById(R.id.fragment_contanier);
        toLedFragment = findViewById(R.id.toLedFragment);
        toLedFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!UI.getbitBlaster().getInFragment()){
                    UI.getbitBlaster().setInFragment(true);
                    openFragment(UI.getbitBlaster());
                }
            }
        });

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

    private final Runnable brodcast = new Runnable(){
        @Override
        public void run() {
            if (UI.getQ() == 1 && !UI.getBtnPressed()){
                bHandle.btWrite(UI.getbitBlaster().getMessage(), "1A_3L");
                sent++;
            }
            mHandler.postDelayed(this,40);
            if (bHandle.isAvailable() > 0){
                received++;
                //UI.getTextView().setText("" + bHandle.btRead());
                if(bHandle.btRead().equals("Length Passed, Checksum Passed")){
                    if (bHandle.getBitBlaster().getPartner()[1] == 48) {
                        badReturns++;
                    }
                    averageTemp += bHandle.getBitBlaster().readIntegerValue(0,1);
                    averageVoltage += bHandle.getBitBlaster().readFloatValue(1,3);
                    if(sent%rate == 0){
                        //UI.getTextView().setText("" + bHandle.getBitBlaster().bytes2Int(bHandle.getBitBlaster().getMessage(),2+3,1+3));
                        UI.getTextView().setText("Length Errors Caught: " + bHandle.getBitBlaster().getLengthMistakes() + ", Corruption Errors Caught: " + bHandle.getBitBlaster().getChecksumMistakes());
                        UI.getMotorTemp().setProgress(averageTemp/rate);
                        UI.getTemp().setText("Motor: " + averageTemp/rate);
                        UI.getBatteryVoltage().setProgress((int)bHandle.getBitBlaster().map(averageVoltage/rate, 20, (float) 25.3, 0, 5));
                        UI.getVoltage().setText(averageVoltage/rate  + " V");
                        //UI.getTextView().append("\n" + lastData);
                        UI.getTextView().append("\n sent: " + sent + ", received: " + received + ", badReturns: "+ badReturns);
                        averageTemp = 0;
                        averageVoltage = 0;
                    }
                }
            }
        }
    };

    public void openFragment(msgHandler bitBlaster){
        ledSettings fragment = ledSettings.newInstance(bitBlaster);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.addToBackStack(null);
        transaction.add(R.id.fragment_contanier, fragment, "LED_SETTINGS").commit();
    }
}