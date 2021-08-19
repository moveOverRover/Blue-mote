package com.example.bluetoothshenenagans4;

// Butter Scotch Protocol:
// each capital letter is a nibble, lowercase is a bit, * is multiplication
// by Bytes:                            AA BxxxC DD 1-250*EE FF FF
//                AA     B             xxxc              DD           1-250* EE        FF FF
//   message length^ | ID^ | Received bit?^ | message type^ | message contents^ | check sum^

import android.content.SharedPreferences;

import java.io.Serializable;
import java.net.BindException;
import java.util.HashMap;

public class msgHandler implements Serializable {
    private final int ADDR_LENG = 0; // address of the length identifier
    private final int ADDR_ID = 1; // address of the ID
    private final int ADDR_TYPE = 2; // address of the message type
    private final int ADDR_CONT = 3; // address of the contents of the message
    private final int SIZE_CONST = 5; // the size of the everything but the message
    private final int SIZE_CHECKSUM = 2; // the size of the checksum
    private final byte ID = 0x10; // the id of this device
    private byte recivedlast = 0x00; // last message received? 0x01 : 0x00
    private HashMap<String,Integer> type_lookup; // all possible instructions that can be added to the message
    private HashMap<String,Integer> size_lookup; // the sizes of the messages
    private final String [] lookupStrings = {"1A_3L","3T_1V"}; // add new commands to this array as necessary
    private final int [] lookupSizes = {12,9}; // add new commands to this array as necessary
    private byte [] message;  // the whole message we want to send
    private byte [] received; // the whole received message
    private int [] partner; // contains decoded info about the partner
    private int [] values; //stores current values of things
    int [] colors = new int[3];
    private int lengthMistakes;
    private int checksumMistakes;
    private boolean toggleState = false;
    private boolean inFragment = false;
    SharedPreferences sharedPref;

    msgHandler (SharedPreferences sharedPref) {
        this.sharedPref = sharedPref;
        setPartner();
        setLookup();
        setMessage();
        setReceived();
        setValues();
        setLengthMistakes(0);
        setChecksumMistakes(0);
    }

    public void saveLed (int r, int b, int g){
        sharedPref.edit().putInt("RED", r).apply();
        sharedPref.edit().putInt("GREEN", g).apply();
        sharedPref.edit().putInt("BLUE", b).apply();
    }

    public int [] getSavedLed (){
        colors[0] = sharedPref.getInt("RED", 0);
        colors[1] = sharedPref.getInt("GREEN", 0);
        colors[2] = sharedPref.getInt("BLUE", 0);
        return colors;
    }

    //Writers -----------------------------------------------------
    public void writeIntegerValue(int value, int index) {
        values[index] = value;
    }

    public void writeFloatValue(float value, int index){
        values[index] = (int) value; // writes everything left of the decimal as an int and truncates the rest
        values[index+1] = (int) ((value-(float)values[index])*10000.0); // writes everything right of the decimal like an int in the thousands
    }

    public void writeCharValue(char value, int index){
        values[index] = value;
    }

    public void writeStringValue(char [] string, int index, int length){
        for(int i=0; i<length; i++){
            writeCharValue(string[i], index+i);
        }
    }
    //Readers ---------------------------------------------------
    public int readIntegerValue(int index, int size){
        return (int) bytes2Int(received, size+index+ADDR_CONT, index+ADDR_CONT);
    }

    public float readFloatValue(int index, int size){ // passed the decimal the size is always 2 bytes
        float right = (float) bytes2Int(received, size-2+index+ADDR_CONT, index+ADDR_CONT);
        float left = (float) bytes2Int(received, size+index+ADDR_CONT, index+ADDR_CONT+size-2);
        left = (float) (left/10000.0);

        return right + left;
    }

    public char readCharValue(int index){
        return (char) received[index+ADDR_CONT];
    }

    public String readStringValue(int index, int size){
        String toReturn = "";
        for (int i=0; i<size; i++){
            toReturn += ", " + readIntegerValue(index+i, 1);
        }
        return toReturn;
    }

    public String readValues(int leng){
        String temp = "";
        for(int i=0; i<leng; i++){
            temp += values[i];
        }
        return temp;
    }

    public String getType(){
        return lookupStrings[partner[ADDR_TYPE]];
    }

    public String readInstruction(byte [] bytes, int length){
        String toReturn = new String("");
        for (int i=0; i<length; i++){ // reads the bytes buffer into the received message
            if (i>255){
                break; //for safety
            }
            received[i] = bytes[i];
        }
        if (length == (int) bytes2Int(received,1+ADDR_LENG,ADDR_LENG)){
            toReturn += "Length Passed";
        } else {
            toReturn += ", Length Failed: " + bytes2Int(received,1+ADDR_LENG,ADDR_LENG) + "/" + length;
            recivedlast = 0x00;
            lengthMistakes++;
            return toReturn;
        }
        if (cSum(received,length-SIZE_CHECKSUM) == bytes2Int(received,2+length-SIZE_CHECKSUM,length-SIZE_CHECKSUM)){ // checks checksum
            toReturn += ", Checksum Passed";
        } else {
            toReturn += "Checksum Failed: " + cSum(received,length-SIZE_CHECKSUM) + "/" + bytes2Int(received,2+length-SIZE_CHECKSUM,length-SIZE_CHECKSUM);
            recivedlast = 0x00;
            checksumMistakes++;
            return toReturn;
        }
        recivedlast = 0x01;

        // gets all the easy stuff
        partner[ADDR_LENG] = (int) bytes2Int(received, 1+ADDR_LENG, ADDR_LENG);
        partner[ADDR_ID] = (int) bytes2Int(received, 1+ADDR_ID, ADDR_ID);
        partner[ADDR_TYPE] = (int) bytes2Int(received, 1+ADDR_TYPE, ADDR_TYPE);
        partner[ADDR_CONT] = cSum(received,length-SIZE_CHECKSUM);

        return toReturn;
    }

    public void changeInstruction (String instruction) { // updates the current message with a new instruction
        long temp;
        int j;
        int size = size_lookup.get(instruction);
        message[ADDR_ID] = (byte) (ID|recivedlast); // updates the ID byte
        int2Bytes(size, message,1, ADDR_LENG); // updates the length identifier
        int2Bytes(type_lookup.get(instruction), message, 1, ADDR_TYPE); // updates the type identifier

        for (int i=0; i<size-SIZE_CONST; i++){
            if (values[i] > 255){  // ------------------------------------cant be 255?
                temp = 255;
                j=1;
                while (temp < (long)values[i]){ //finds the total bytes we need to store a number
                    temp = temp*256;
                    j++;
                }
                int2Bytes(values[i], message, j, i+ADDR_CONT); // updates the contents of the message
                i=j-1+i;
            } else {
                int2Bytes(values[i], message, 1, i+ADDR_CONT); // updates the contents of the message
            }
        }

        int2Bytes(cSum(message,size-SIZE_CHECKSUM), message, SIZE_CHECKSUM, size-SIZE_CHECKSUM); // updates the checksum
    }

    public float map(float x, float in_min, float in_max, float out_min, float out_max) {
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }

    public int power(int a, int b){ // a = base, b = exponent
        if(b==0){
            return 1;
        }
        int og = a;
        for(int q=0; q<b-1; q++){
            a = a*og;
        }
        return a;
    }

    // leng > offset || leng < offset
    public void int2Bytes(int num, byte [] bytes, int leng, int offset){ // writes a binary number from an int
        for(int i=offset;i<leng+offset;i++){
            bytes[i] = 0x00;
        }

        if (num%2 != 0){ // if num is odd then we know the first bit is a 1
            bytes[leng+offset-1] = (byte) (bytes[leng+offset-1] | 0x01); // minus one because arrays start at 0
        }

        int shift, pos;
        byte mask;
        int tempNum = num;

        while ((shift=int2Bit(tempNum)) != 0){ // while there is bits left to add
            tempNum = tempNum - power(2,shift); // removes previous bit
            mask = 0x01; // resets the mask
            mask <<= shift%8; // moves the mask to the current bit
            pos = bytePos(leng, shift); // finds the current byte
            bytes[pos+offset] = (byte) (bytes[pos+offset] | mask); //adds bit to byte
        }
    }

    public int int2Bit(int tempNum){ // determines biggest bit position of checksum *** helper function
        int k =0;
        while((tempNum =tempNum/2) >= 1){
            k++;
        }
        return k;
    }

    int bytePos(int leng, int shift){ // counts the byte we are on based on the shift
        int pos = 0;
        while (shift>7){
            shift -=8;
            pos++;
        }
        return leng-pos-1; // -1 because arrays start at 0
    }

    // must be leng > offset
    public long bytes2Int(byte [] msg, int leng, int offset){ // makes a series of bytes into a binary number which is returned as an integer
        byte mask, temp;
        long sum = 0;

        for (int i=leng; i>offset; i--){ // repeats until every byte has been processed start at last byte
            mask = (byte)0x01;
            for (int j=0; j<8; j++){ // start at first bit
                temp = (byte) (mask & msg[i - 1]);
                if (temp == mask) { // if bit is 1
                    sum += power(2,j+(8*(leng-i))); // add bit
                }
                mask = (byte) (mask << 1); // bit shift mask to the left
            }
        }
        return sum;
    }

    public int cSum(byte [] msg, int leng){ // calculates integer checksum
        int sum =0;
        byte [] temp = new byte [1];
        for (int k=0; k<leng; k++){
            temp[0] = msg[k];
            sum+=bytes2Int(temp,1,0);
        }
        return sum;
    }

    //Setters-------------------------
    public void setLookup() {
        type_lookup = new HashMap<>();
        size_lookup = new HashMap<>();
        for (int i=0; i<lookupStrings.length; i++){
            type_lookup.put(lookupStrings[i], i);
            size_lookup.put(lookupStrings[i], lookupSizes[i]);
        }
    }
    public void setMessage() {
        message = new byte[255];
        message[ADDR_ID] = ID;
    }
    public void setReceived() {
        received = new byte[255];
    }
    public void setPartner () {
        partner = new int[4];
    }
    public void setValues () {
        values = new int[250];
        for (int i=0; i<250; i++){
            values[i] = 0;
        }
    }
    public void setInFragment(boolean inFragment) {
        this.inFragment = inFragment;
    }
    public void setChecksumMistakes(int checksumMistakes) {
        this.checksumMistakes = checksumMistakes;
    }
    public void setToggleState(boolean toggleState) {
        this.toggleState = toggleState;
    }

    public void setLengthMistakes(int lengthMistakes) {
        this.lengthMistakes = lengthMistakes;
    }

    //Getters-------------------------
    public byte[] getMessage() {
        return message;
    }
    public byte[] getReceived() {
        return received;
    }
    public byte getRecivedlast() {
        return recivedlast;
    }
    public int[] getValues() {
        return values;
    }
    public int[] getPartner() {
        return partner;
    }
    public int getSize(String instruction){
        return size_lookup.get(instruction);
    }
    public boolean getInFragment(){
        return inFragment;
    }
    public int getLengthMistakes() {
        return lengthMistakes;
    }
    public int getChecksumMistakes() {
        return checksumMistakes;
    }
    public boolean getToggleState() {
        return toggleState;
    }


    public String getStringMessage(){
        String toReturn = "";
        for (int i=0; i<4; i++){
            toReturn += bytes2Int(message,1+i+ADDR_CONT,i+ADDR_CONT);
        }
        return toReturn;
    }

    public String toString(){
        String toReturn = "ID: " + partner[ADDR_ID] + ", SIZE: " + partner[ADDR_LENG] + ", CHECKSUM: " + partner[ADDR_CONT] + ", Contents: " + readStringValue(0,partner[ADDR_LENG]-SIZE_CONST);
        return toReturn;
    }

    @Override
    public boolean equals(Object o) {
        if (o.equals(this)){
            return true;
        }
        if (o.getClass() != this.getClass()) {
            return false;
        }
        return this.toString().equals(o.toString());
    }
}
