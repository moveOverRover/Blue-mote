package com.example.bluetoothshenenagans4;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;


public class UIHandler extends AppCompatActivity {
    private int q = 0;
    private msgHandler bitBlaster;
    private TextView txt;
    private ToggleButton transmit;
    private Button function2;
    private Button setOri;
    private ImageView pad;
    private ImageView JoyStick;
    private RelativeLayout myLayout;
    private ProgressBar motorTemp;
    private ProgressBar batteryVoltage;
    private ProgressBar throttleCurrent;
    private TextView throttle;
    private TextView temp;
    private TextView voltage;
    private float xOrigin;
    private float yOrigin;
    private float x = 0;
    private float y = 0;
    private boolean btnPressed = false;
    private boolean runOnce = false;
    private boolean babyMode = false;
    private final SharedPreferences sharedPref;
    private final Activity activity;
    private final float x_dis = 300;
    private final float y_dis = 300;
    private final float joystick_offset = -70;
    private final float pad_offset = -200 + joystick_offset;
    private float outMin = 0;
    private float outMax = 255;

    UIHandler(Activity activity) {
        this.activity = activity;
        sharedPref = activity.getSharedPreferences("myPref", MODE_PRIVATE);
        xOrigin = sharedPref.getFloat("xOrigin", 0);
        yOrigin = sharedPref.getFloat("yOrigin",0);
        setAll(sharedPref);
    }
    // TODO fix the bad messages that happen at 255
    public void map(){
        float temp = bitBlaster.map(JoyStick.getY(), yOrigin-y_dis+joystick_offset, yOrigin+y_dis+joystick_offset, outMin, outMax);
        temp = (temp-255)*-1;
        if ((int)temp >= 255){ // for some reason it dont work when sending a 255 byte
            temp = 255-1;
        }
        bitBlaster.writeIntegerValue((int)temp,0);
        bitBlaster.changeInstruction("1A_3L");
        throttleCurrent.setProgress((int)bitBlaster.map(temp,0,255, 0, 255));
        throttle.setText("" + (int)(bitBlaster.map(temp,0,255, 0, 200)-100));
    }

    //Setters-----------------------
    public void setAll(SharedPreferences sharedPref){
        setMsgHandler(sharedPref);
        setTxt();
        setFunction2();
        setSetOri();
        setJoyStick();
        setPad();
        setMyLayout();
        setTransmit();
        setMotorTemp();
        setTemp();
        setBatteryVoltage();
        setVoltage();
        setThrottleCurrent();
        setThrottle();
    }

    private void setMsgHandler(SharedPreferences sharedPref){
        bitBlaster = new msgHandler(sharedPref);
    }
    private void setTxt() {
        txt = (TextView)activity.findViewById(R.id.textView);
    }
    @SuppressLint("ClickableViewAccessibility")
    private void setMyLayout(){
        this.myLayout =  (RelativeLayout) activity.findViewById(R.id.my_layout);
        this.myLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) { //when finger touches screen
                if (!runOnce){  // the first time we load preferences for where the joystick should be
                    runOnce = true;
                    recallOrigin();
                }
                x = event.getX(); // record x and y coordinates of finger
                y = event.getY();

                if(event.getAction() == MotionEvent.ACTION_MOVE && !btnPressed){ // if the finger is moving and the set origin button is not pressed
                    if (x > xOrigin+x_dis){                             // if  finger has moved passed the upper X bound of the Joystick
                        JoyStick.setX(xOrigin+x_dis+joystick_offset);
                    } else if (x < xOrigin-x_dis) {                     // if finger has moved passed lower X bound of Joystick
                        JoyStick.setX(xOrigin-x_dis+joystick_offset);
                    } else{                                             // else just change the position of the Joystick to finger
                        JoyStick.setX(x+joystick_offset);
                    }

                    if (y > yOrigin+y_dis){                            // if finger has moved passed the upper Y bound of the Joystick
                        JoyStick.setY(yOrigin+y_dis+joystick_offset);
                    } else if (y < yOrigin-y_dis){                     // if Finger has moved passed the lower Y bound of the Joystick
                        JoyStick.setY(yOrigin-y_dis+joystick_offset);
                    } else  {                                          // else just change the position of the Joystick to the finger
                        JoyStick.setY(y+joystick_offset);
                    }

                } else if (event.getAction() == MotionEvent.ACTION_UP && !btnPressed){  // if finger leaves the screen reset the position of the Joystick to the origin
                    JoyStick.setX(xOrigin+joystick_offset);
                    JoyStick.setY(yOrigin+joystick_offset);

                } else if (event.getAction() == MotionEvent.ACTION_MOVE && btnPressed){ // if setting new origin
                    JoyStick.setX(x+joystick_offset);
                    JoyStick.setY(y+joystick_offset);
                }

                map(); // maps Joystick movement to a byte number
                // txt.setText("X = " + JoyStick.getX() + "\nY = " + JoyStick.getY());
                //txt.setText(bitBlaster.readValues(4));
                //txt.append('\n' + bitBlaster.getStringMessage());
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

    public void setSetOri() { // sets a new origin for the Joystick
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

                    sharedPref.edit().putFloat("xOrigin", xOrigin).apply(); // saves new x and y to preferences
                    sharedPref.edit().putFloat("yOrigin", yOrigin).apply();
                }
                btnPressed = !btnPressed;
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    public void setFunction2() {
        function2 = activity.findViewById(R.id.function_2);
        function2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (babyMode){
                    function2.setText("Baby Mode");
                    babyMode = false;
                    outMin = 0;
                    outMax = 255;
                } else {
                    function2.setText("Butterscotch");
                    babyMode = true;
                    outMin = 50;
                    outMax = 204;
                }
            }
        });

    }

    public void setTransmit() {
        transmit = (ToggleButton)activity.findViewById(R.id.toggleButton);
        transmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(q==0){
                    q=1;
                    bitBlaster.writeIntegerValue(1, 6);
                } else {
                    q=0;
                    bitBlaster.writeIntegerValue(0,6);
                }
            }
        });
    }

    public void setMotorTemp() {
        motorTemp = (ProgressBar)activity.findViewById(R.id.progressbar);
    }
    public void setTemp() {
        temp = (TextView) activity.findViewById(R.id.temp1);
    }
    public void setBatteryVoltage (){
        batteryVoltage = (ProgressBar) activity.findViewById((R.id.progressbar));
    }
    public void setVoltage (){
        voltage = (TextView) activity.findViewById((R.id.volts));
    }
    public void setThrottleCurrent(){
        throttleCurrent = (ProgressBar) activity.findViewById(R.id.progressbarThrottle);
    }
    public void setThrottle (){
        throttle = (TextView) activity.findViewById(R.id.throttle);
    }


    //Getters-----------------------
    public msgHandler getbitBlaster() {
        return bitBlaster; }
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
    public TextView getTemp() {
        return temp;
    }
    public TextView getVoltage() {
        return voltage;
    }
    public ProgressBar getThrottleCurrent() {
        return throttleCurrent;
    }
    public TextView getThrottle() {
        return throttle;
    }
    public ProgressBar getMotorTemp() {
        return motorTemp;
    }
    public ProgressBar getBatteryVoltage() {
        return batteryVoltage;
    }

    @Override
    public String toString(){
        return bitBlaster.toString();
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
