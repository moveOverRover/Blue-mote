package com.example.bluetoothshenenanegans3;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;


public class UIHandler extends AppCompatActivity {
    private int q = 0;
    private msgHandler cmdr;
    private TextView txt;
    private ToggleButton transmit;
    private Button function1;
    private Button function2;
    private Button setOri;
    private ImageView pad;
    private ImageView JoyStick;
    private RelativeLayout myLayout;
    private float xOrigin;
    private float yOrigin;
    private float x = 0;
    private float y = 0;
    private boolean btnPressed = false;
    private boolean runOnce = false;
    private final SharedPreferences sharedPref;
    private final Activity activity;
    private final float x_dis = 300;
    private final float y_dis = 300;
    private final float joystick_offset = -70;
    private final float pad_offset = -200 + joystick_offset;

    UIHandler(Activity activity) {
        this.activity = activity;
        sharedPref = activity.getSharedPreferences("myPref", MODE_PRIVATE);
        xOrigin = sharedPref.getFloat("xOrigin", 0);
        yOrigin = sharedPref.getFloat("yOrigin",0);
        setAll();
    }

    public void map(){
        float temp = cmdr.map(JoyStick.getY(), yOrigin-y_dis, yOrigin+y_dis+joystick_offset, 0, 255);
        temp = (temp-255)*-1;
        if ((int)temp > 255){
            temp = 255-1;
        }
        cmdr.setDrv((int)temp);
        cmdr.setCmd();
    }


    //Setters-----------------------
    public void setAll(){
        setMsgHandler();
        setTxt();
        setFunction1();
        setFunction2();
        setSetOri();
        setJoyStick();
        setPad();
        setMyLayout();
        setTransmit();
    }

    private void setMsgHandler(){
        cmdr = new msgHandler();
    }
    private void setTxt() {
        txt = (TextView)activity.findViewById(R.id.textView);
    }
    @SuppressLint("ClickableViewAccessibility")
    private void setMyLayout(){
        this.myLayout =  (RelativeLayout) activity.findViewById(R.id.my_layout);
        this.myLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!runOnce){
                    runOnce = true;
                    recallOrigin();
                }
                x = event.getX();
                y = event.getY();

                if(event.getAction() == MotionEvent.ACTION_MOVE && !btnPressed){
                    if (x > xOrigin+x_dis){
                        JoyStick.setX(xOrigin+x_dis+joystick_offset);
                    } else if (x < xOrigin-x_dis) {
                        JoyStick.setX(xOrigin-x_dis+joystick_offset);
                    } else{
                        JoyStick.setX(x+joystick_offset);
                    }

                    if (y > yOrigin+y_dis){
                        JoyStick.setY(yOrigin+y_dis+joystick_offset);
                    } else if (y < yOrigin-y_dis){
                        JoyStick.setY(yOrigin-y_dis+joystick_offset);
                    } else  {
                        JoyStick.setY(y+joystick_offset);
                    }

                } else if (event.getAction() == MotionEvent.ACTION_UP && !btnPressed){
                    JoyStick.setX(xOrigin+joystick_offset);
                    JoyStick.setY(yOrigin+joystick_offset);

                } else if (event.getAction() == MotionEvent.ACTION_MOVE && btnPressed){
                    JoyStick.setX(x+joystick_offset);
                    JoyStick.setY(y+joystick_offset);
                }

                map();
                txt.setText("X = " + JoyStick.getX() + "\nY = " + JoyStick.getY());
                return true;
            }
        });
    }

    public void setJoyStick() {
        this.JoyStick = (ImageView) activity.findViewById(R.id.joyStick);
    }
    public void setJoyStick(float x, float y){
        JoyStick.setX(x+joystick_offset);
        JoyStick.setY(y+joystick_offset);
    }
    public void setPad(){
        this.pad = (ImageView) activity.findViewById(R.id.pad);
    }
    public void setPad(float x, float y) {
        pad.setX(x+pad_offset);
        pad.setY(y+pad_offset);
    }
    public void setxOrigin(float x){
        this.xOrigin = x;
    }
    public void setyOrigin(float y){
        this.yOrigin = y;
    }
    public void recallOrigin(){
        setPad(xOrigin, yOrigin) ;
        setJoyStick(xOrigin , yOrigin);
    }
    public void setX(float x) {
        this.x = x;
    }
    public void setY(float y) {
        this.y = y;
    }

    public void setSetOri() {
        setOri = (Button) activity.findViewById(R.id.set_origin);
        setOri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!btnPressed){
                    Toast.makeText(activity.getApplicationContext(), "Move JoyStick to new origin", Toast.LENGTH_LONG).show();
                    setOri.setText("Confirm");
                } else {
                    xOrigin = JoyStick.getX();
                    yOrigin = JoyStick.getY();
                    recallOrigin();
                    Toast.makeText(activity.getApplicationContext(), "New origin set at X: " + xOrigin + ", Y: " + yOrigin, Toast.LENGTH_LONG).show();
                    setOri.setText("Set New Origin");

                    sharedPref.edit().putFloat("xOrigin", xOrigin).apply();
                    sharedPref.edit().putFloat("yOrigin", yOrigin).apply();
                }
                btnPressed = !btnPressed;
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    public void setFunction1() {
        function1 = (Button)activity.findViewById(R.id.function_1);
        function1.setOnTouchListener((v, event) -> {
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
    public void setFunction2() {
        function2 = (Button)activity.findViewById(R.id.function_2);
        function2.setOnTouchListener((v, event) -> {
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

    public void setTransmit() {
        transmit = (ToggleButton)activity.findViewById(R.id.toggleButton);
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


    //Getters-----------------------
    public msgHandler getCmdr() {
        return cmdr; }
    public int getQ() {
        return q;
    }
    public boolean getBtnPressed() {
        return btnPressed;
    }
    public float getX() {
        return x;
    }
    public float getY() {
        return y;
    }
    public float getxOrigin() {
        return xOrigin;
    }
    public float getyOrigin() {
        return yOrigin;
    }
    public TextView getTextView() {
        return txt;
    }
    public ImageView getJoyStick() {
        return JoyStick;
    }

    public float getY_dis() {
        return y_dis;
    }

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
