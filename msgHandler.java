package com.example.bluetoothshenanegans2;

public class msgHandler {
    private int drv;
    private int turn;
    private int h;
    private int p;
    private int t;
    private byte [] cmd;

    msgHandler () {
        cmd = new byte[7];
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

    public byte int2Bytes(int checkSum, byte [] bytes){ // writes a 2 bit binary number to be used as the checksum
        bytes[0] = 0x00;
        bytes[1] = 0x00;

        if (checkSum%2 != 0){ // if checkSum is odd then we know the first bit is a 1
            bytes[1] = (byte) (bytes[1] | 0x01);
        }

        int shift;
        byte mask;
        int tempSum = checkSum;

        while ((shift=int2Bit(tempSum)) != 0){ // I forgot how all of this works too
            tempSum = tempSum - power(2,shift);
            mask = 0x01;
            if (shift > 7){
                shift -=8;
                mask <<= shift;
                bytes[0] = (byte) (bytes[0] | mask);
            } else {
                mask <<= shift;
                bytes[1] = (byte) (bytes[1] | mask);
            }
        }
        return bytes[1];
    }

    public int int2Bit(int tempSum){ // determines biggest bit position of checksum *** helper function
        int k =0;
        while((tempSum =tempSum/2) >= 1){
            k++;
        }
        return k;
    }

    public long getSum(byte [] msg, int leng){ // makes a series of bytes into a binary number which is returned as an integer
        byte mask, temp;
        long sum = 0;

        for (int i=leng; i>0; i--){ // repeats until every byte has been processed start at last byte
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
            sum+=getSum(temp,1);
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
        byte [] bytes = new byte [2];
        cmd[0] = int2Bytes(this.drv, bytes);
        cmd[1] = (byte) 0x82;
        cmd[2] = (byte) 0x82;
        cmd[3] = int2Bytes(this.turn,bytes);
        cmd[4] = int2Bytes(zDoer(), bytes);
        int2Bytes(cSum(cmd,5), bytes);
        cmd[5] = bytes[0];
        cmd[6] = bytes[1];
    }

    //Getters-------------------------
    public int getDrv(){ return drv; }
    public int getTurn(){ return turn; }
    public int getH(){ return h; }
    public int getP(){ return p; }
    public int getT(){ return t; }
    public byte[] getCmd() {
        return cmd;
    }

    @Override
    public String toString(){
        //return bytes2String(cmd, 7);
        return this.drv + ", 130, 130, " + this.turn + ", " + zDoer() + ", " + cSum(cmd,5);
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
