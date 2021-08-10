package com.example.bluetoothshenanegans2;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;


public class UIHandler extends AppCompatActivity {
    private int q;
    private final Activity activity;
    private TextView txt;
    private ImageButton forward;
    private ImageButton reverse;
    private ImageButton left;
    private ImageButton right;
    private Button tunes;
    private Button beep;
    private ToggleButton transmit;
    private msgHandler cmdr;

    UIHandler(Activity activity) {
        this.activity = activity;
        setAll();
    }

    //Setters-----------------------
    public void setAll(){
        setTxt();
        setForward();
        setReverse();
        setRight();
        setLeft();
        setTunes();
        setBeep();
        setTransmit();
        setMsgHandler();
        q=0;
    }

    private void setTxt() {
        txt = (TextView)activity.findViewById(R.id.textView);
    }
    @SuppressLint("ClickableViewAccessibility")
    public void setForward() {
        forward = (ImageButton)activity.findViewById(R.id.forward);
        forward.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN){
                cmdr.setDrv(200);
            } else if (event.getAction() == MotionEvent.ACTION_UP){
                cmdr.setDrv(130);
            }
            cmdr.setCmd();
            //txt.setText(cmdr.toString(1));
            return true;
        });
    }
    @SuppressLint("ClickableViewAccessibility")
    public void setReverse() {
        reverse = (ImageButton)activity.findViewById(R.id.reverse);
        reverse.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN){
                cmdr.setDrv(75);
            } else if (event.getAction() == MotionEvent.ACTION_UP){
                cmdr.setDrv(130);
            }
            cmdr.setCmd();
            //txt.setText(cmdr.toString(1));
            return true;
        });
    }
    @SuppressLint("ClickableViewAccessibility")
    public void setLeft() {
        left = (ImageButton)activity.findViewById(R.id.l);
        left.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN){
                cmdr.setTurn(200);
            } else if (event.getAction() == MotionEvent.ACTION_UP){
                cmdr.setTurn(130);
            }
            cmdr.setCmd();
            //txt.setText(cmdr.toString(1));
            return true;
        });
    }
    @SuppressLint("ClickableViewAccessibility")
    public void setRight() {
        right = (ImageButton)activity.findViewById(R.id.r);
        right.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN){
                cmdr.setTurn(75);
            } else if (event.getAction() == MotionEvent.ACTION_UP){
                cmdr.setTurn(130);
            }
            cmdr.setCmd();
            //txt.setText(cmdr.toString(1));
            return true;
        });
    }
    @SuppressLint("ClickableViewAccessibility")
    public void setTunes() {
        tunes = (Button)activity.findViewById(R.id.tunes);
        tunes.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN){
                cmdr.setT(1);
            } else if (event.getAction() == MotionEvent.ACTION_UP){
                cmdr.setT(0);
            }
            cmdr.setCmd();
            //txt.setText(cmdr.toString(1));
            return true;
        });
    }
    @SuppressLint("ClickableViewAccessibility")
    public void setBeep() {
        beep = (Button)activity.findViewById(R.id.beep);
        beep.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN){
                cmdr.setH(1);
            } else if (event.getAction() == MotionEvent.ACTION_UP){
                cmdr.setH(0);
            }
            cmdr.setCmd();
            //txt.setText(cmdr.toString(1));
            return true;
        });
    }
    @SuppressLint("ClickableViewAccessibility")
    public void setTransmit() {
        transmit = (ToggleButton)activity.findViewById(R.id.transmit);
        transmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(q==0){
                    q=1;
                    cmdr.setP(1);
                } else {
                    q=0;
                    cmdr.setP(0);
                }
                cmdr.setCmd();
                //txt.setText(cmdr.toString(1));
            }
        });
    }
    private void setMsgHandler(){
        cmdr = new msgHandler();
    }


    //Getters-----------------------
    public TextView getTextView(){
        return txt;
    }
    public ImageButton getForward(){
        return forward;
    }
    public ImageButton getReverse(){
        return reverse;
    }
    public ImageButton getLeft(){
        return left;
    }
    public ImageButton getRight(){
        return right;
    }
    public Button getTunes() { return tunes; }
    public Button getBeep() { return beep; }
    public Button getToggle() { return transmit; }
    public msgHandler getCmdr() { return cmdr; }
    public int getQ() { return q; }

    @Override
    public String toString(){
        return cmdr.toString();
    }

    @Override
    public boolean equals(Object o){
        if (o.equals(this)){
            return true;
        }
        if (o.getClass() != this.getClass()) {
            return false;
        }
        return this.toString().equals(o.toString());
    }
}
