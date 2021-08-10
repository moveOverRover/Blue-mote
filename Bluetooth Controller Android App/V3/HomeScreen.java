package com.example.bluetoothshenenanegans3;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Set;

public class HomeScreen extends AppCompatActivity {
    private Button connect2;
    private Button close;
    private EditText editText;
    private TextView list;
    public static final String EXTRA_TEXT = "EXTRA_TEXT";
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        editText = findViewById(R.id.editText);
        list = findViewById(R.id.list);

        sharedPref = getSharedPreferences("myPref", MODE_PRIVATE);
        editText.setText(sharedPref.getString("Last_Device", "Enter a New Adress!"));

        connect2 = findViewById(R.id.connect_to_bluetooth);
        connect2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPref.edit().putString("Last_Device", editText.getText().toString()).apply();
                openMainActivity();
            }
        });

        close = findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPref.edit().putString("Last_Device", editText.getText().toString()).apply();
                finish();
            }
        });
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        for(BluetoothDevice bt : pairedDevices){
            list.append(bt.getName() + " : \t\t" + bt.getAddress() + "\n");
        }
    }

    public void openMainActivity(){
        String myString = new String(editText.getText().toString());
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(EXTRA_TEXT,myString);
        startActivity(intent);
    }
}