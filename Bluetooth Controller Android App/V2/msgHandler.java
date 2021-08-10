package com.example.bluetoothshenanegans2;

public class msgHandler {
    private int drv;
    private int turn;
    private int h;
    private int p;
    private int t;
    private byte [] cmd;
    private byte [] ret;
    private int rec;
    private float volt;

    msgHandler () {
        cmd = new byte[7];
        ret = new byte[5];
        setAll(130,130,0,0,0);
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

        if (num%2 != 0){ // if checkSum is odd then we know the first bit is a 1
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

    public String bytes2String(byte [] bytes, int leng){
        char [] arr = new char [leng];
        for (int i=0; i<leng; i++) {
            arr[i] = (char) (bytes[i]);
        }
        return String.valueOf(arr);
    }

    public int zDoer(){ // inner btn has priority over outer btn
        if (this.p == 0 && this.h == 0 && this.t == 0){ // nothing
            return 0;
        } else if (this.p == 1 && this.h == 0 && this.t == 0){ // toggle
            return 1;
        } else if (this.p == 1 && this.h == 1 && this.t == 0){ // toggle + inner
            return 2;
        } else if (this.p == 1 && this.h == 0 && this.t == 1) { // toggle + outer
            return 3;
        } else if (this.h == 1) { // inner
            return 4;
        } else if (this.t == 1){ // outer
            return 5;
        }
        return 0;
    }

    public boolean mPares(){
        if (cSum(ret, 3) == bytes2Int(ret,5, 3)){
            rec = (int) bytes2Int(ret, 1,0);
            int vo = (int) bytes2Int(ret, 2, 1);
            int lt = (int) bytes2Int(ret, 3, 2);
            volt = Float.parseFloat(new String("" + vo + lt));
            return true;
        }
        return false;
    }

    //Setters-------------------------
    public void setAll(int drv, int turn, int h, int p, int t){
        setDrv(drv);
        setTurn(turn);
        setH(h);
        setP(p);
        setT(t);
        setCmd();
    }
    public void setDrv(int drv){ this.drv = drv; }
    public void setTurn(int turn){ this.turn = turn; }
    public void setH(int h){ this.h = h; }
    public void setP(int p){ this.p = p; }
    public void setT(int t){ this.t = t; }
    public void setCmd(){
        int2Bytes(drv, cmd, 1,0); // first byte
        cmd[1] = (byte) 0x82;                  // second byte
        cmd[2] = (byte) 0x82;                   // third byte
        int2Bytes(turn,cmd,1,3);    //forth byte
        int2Bytes(zDoer(), cmd, 1,4); // fifth byte
        int2Bytes(cSum(cmd,5), cmd, 2, 5); // sixth and seventh bytes
    }
    public void setRet(byte[] ret) {this.ret = ret;}

    //Getters-------------------------
    public int getDrv(){ return drv; }
    public int getTurn(){ return turn; }
    public int getH(){ return h; }
    public int getP(){ return p; }
    public int getT(){ return t; }
    public byte[] getCmd() {
        return cmd;
    }
    public byte[] getRet() {return ret;}

    public String toString(int a){
        //return bytes2String(cmd, 7);
        if (a==1){
            return "Sent: " + this.drv + ", 130, 130, " + this.turn + ", " + zDoer() + ", " + cSum(cmd,5) ;
        } else {
            return "Received: " + this.rec + ", " + this.volt;
        }
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
