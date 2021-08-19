package com.example.bluetoothshenenagans4;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ledSettings#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ledSettings extends Fragment {
    private static final String ARG_PARAM1 = "obj";
    private msgHandler bitBlaster;
    private SeekBar redSlider;
    private SeekBar greenSlider;
    private SeekBar blueSlider;
    private TextView lable1;
    private TextView lable2;
    private TextView lable3;
    private Button back;
    private Button mix;
    private Button actionLights;
    private Button mixer;
    private ToggleButton lights;
    SharedPreferences sharedPref;

    public ledSettings() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ledSettings newInstance( msgHandler param1) {
        ledSettings fragment = new ledSettings();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            bitBlaster = (msgHandler) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_led_settings, container, false);
        redSlider = view.findViewById(R.id.seekBar);
        greenSlider = view.findViewById(R.id.seekBar3);
        blueSlider = view.findViewById(R.id.seekBar2);
        lable1 = view.findViewById(R.id.lable1);
        lable2 = view.findViewById(R.id.lable2);
        lable3 = view.findViewById(R.id.lable3);
        back = view.findViewById(R.id.back);
        mix = view.findViewById(R.id.Mix);
        actionLights = view.findViewById(R.id.actionLights);
        lights = view.findViewById(R.id.lights);
        mixer = view.findViewById(R.id.mixer);

        setBack();
        setRedSlider();
        setGreenSlider();
        setBlueSlider();
        setMix();
        setLights();
        setActionLights();
        setMixer();

        int [] savedLed = bitBlaster.getSavedLed();
        lights.setChecked(bitBlaster.getToggleState());
        lable1.setText("RED: " + savedLed[0]);
        lable2.setText("GREEN: " +savedLed[2]);
        lable3.setText("BLUE: " + savedLed[1]);
        redSlider.setProgress(savedLed[0]);
        greenSlider.setProgress(savedLed[1]);
        blueSlider.setProgress(savedLed[2]);
        bitBlaster.writeIntegerValue(redSlider.getProgress(),1);
        bitBlaster.writeIntegerValue(greenSlider.getProgress(),2);
        bitBlaster.writeIntegerValue(blueSlider.getProgress(),3);
        bitBlaster.changeInstruction("1A_3L");


        return view;
    }

    public void setBack() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bitBlaster.setInFragment(false);
                bitBlaster.saveLed(redSlider.getProgress(), greenSlider.getProgress(), blueSlider.getProgress());
                getActivity().onBackPressed();
            }
        });
    }

    public void setMixer() {
        mixer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bitBlaster.writeIntegerValue(1, 5);
                bitBlaster.changeInstruction("1A_3L");
            }
        });
    }

    public void setMix() {
        mix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bitBlaster.writeIntegerValue(2, 5);
                bitBlaster.changeInstruction("1A_3L");
            }
        });
    }

    public void setActionLights() {
        actionLights.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bitBlaster.writeIntegerValue(0, 5);
                bitBlaster.changeInstruction("1A_3L");
            }
        });
    }

    public void setLights() {

        lights.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bitBlaster.getToggleState()){
                    bitBlaster.writeIntegerValue(0, 4);
                    bitBlaster.setToggleState(false);
                } else {
                    bitBlaster.writeIntegerValue(1, 4);
                    bitBlaster.setToggleState(true);
                }
                bitBlaster.changeInstruction("1A_3L");
            }
        });
    }

    public void setRedSlider() {
        redSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    lable1.setText("RED: " + progress);
                    bitBlaster.writeIntegerValue(progress,1);
                    bitBlaster.changeInstruction("1A_3L");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void setGreenSlider() {
        greenSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    lable3.setText("GREEN: " + progress);
                    bitBlaster.writeIntegerValue(progress,2);
                    bitBlaster.changeInstruction("1A_3L");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void setBlueSlider() {
        blueSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    lable2.setText("BLUE: " + progress);
                    bitBlaster.writeIntegerValue(progress,3);
                    bitBlaster.changeInstruction("1A_3L");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}