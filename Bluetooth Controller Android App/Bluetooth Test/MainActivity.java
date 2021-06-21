package com.example.bluetoothshenanignas1;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

//use as reference
//https://www.youtube.com/watch?v=TLXpDY1pItQ

public class MainActivity extends AppCompatActivity {

    static final UUID nUUID = UUID.fromString("00001101-0000-1000-8000-34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        System.out.println(btAdapter.getBondedDevices());

        BluetoothDevice hcO5 = btAdapter.getRemoteDevice(")):21:13:02:B6:SB");
        System.out.println(hcO5.getName());

        BluetoothSocket btSocket = null;
        int counter = 0;
        do {
            try {
                btSocket = hcO5.createRfcommSocketToServiceRecord(nUUID);
                System.out.println(btSocket);
                btSocket.connect();
                System.out.println(btSocket.isConnected());
            } catch (IOException e) {
                e.printStackTrace();
            }
            counter++;
        } while (!btSocket.isConnected() && counter < 3);

        try {
            OutputStream outputStream = btSocket.getOutputStream();
            outputStream.write('0');
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            InputStream inputStream = btSocket.getInputStream();
            inputStream.skip(inputStream.available());
            for (int i=0; i<26; i++){
                byte b = (byte)inputStream.read();
                System.out.println((char)b);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            btSocket.close();
            System.out.println(btSocket.isConnected());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}