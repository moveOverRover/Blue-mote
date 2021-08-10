package com.example.bluetoothshenanegans2;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class BlueHandler {
    private final UUID nUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //this is an android thing just leave it
    private String addr = null;
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private BluetoothDevice hc05 = null;
    private OutputStream out = null;
    private InputStream in = null;
    private msgHandler bitBlaster = null;

    BlueHandler (String addr) throws IOException{
        setBtAdapter();
        setAddr(addr);
        setDevice();
        setBtSocket();
        setOut();
        setIn();
        setBitBlaster();
    }

    public String startConnection() {
        int attempt = 0;
        do {
            try {
                this.btSocket.connect();
            } catch (IOException e) {
                e.printStackTrace();
                return "Error 1";
            }
        } while (!btSocket.isConnected() && attempt < 3); //atempts to connect 3 times to the bluetooth

        if (this.btSocket.isConnected()){
            return "Connected";
        } else {
            return "Not Connected";
        }
    }

    public boolean btWrite (byte bytes[], int length){
        try {
            this.out.write(bytes, 0, length);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean btRead() {
        int temp;
        int counter = 0;
        try {
            while((temp = in.read()) != -1){
                //if (counter < 4) {
                //    bitBlaster.int2Bytes(temp, bitBlaster.getRet(), 1, counter);
                //}
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        if (counter != 5){
                return false;
            }
        return true;
    }


    public boolean endConnection(){
        try {
            this.btSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    //-------------------------------------Setters
    public void setBtAdapter() {
        this.btAdapter = BluetoothAdapter.getDefaultAdapter();
    }
    public void setBtSocket() throws IOException {
        this.btSocket = this.hc05.createInsecureRfcommSocketToServiceRecord(nUUID);
    }
    public void setDevice(){
        this.hc05 = btAdapter.getRemoteDevice(this.addr);
    }
    public void setOut() throws IOException {
        out = this.btSocket.getOutputStream();
    }
    public void setAddr(String addr) {
        this.addr = new String(addr);
    }
    public void setIn() throws IOException{
        in = this.btSocket.getInputStream();
    }
    public void setBitBlaster() {
        bitBlaster = new msgHandler();
    }


    //---------------------------------------Getters
    public BluetoothAdapter getBtAdapter() {
        return btAdapter;
    }
    public BluetoothSocket getBtSocket() {
        return btSocket;
    }
    public BluetoothDevice getHc05() {
        return hc05;
    }
    public UUID getnUUID() {
        return nUUID;
    }
    public String getAddr() {
        return new String(this.addr);
    }
    public InputStream getIn() {return in;}
    public OutputStream getOut() {return out;}
    public msgHandler getBitBlaster() {return bitBlaster;}

    @Override
    public String toString() {
    String temp = new String( ", " + this.hc05.getName() + ", isConnected() == " + btSocket.isConnected());
    return temp;
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
