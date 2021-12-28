import processing.serial.*;

private final int ADDR_LENGA = 0; // address of the length identifier
private final int ADDR_LENGB = 1; // address of the repeated length identifier
private final int ADDR_ID = 2; // address of the ID
private final int ADDR_TYPE = 3; // address of the message type
private final int ADDR_CONT = 4; // address of the contents of the message
private final int SIZE_CONST = 6; // the size of the everything but the message
private final int SIZE_CHECKSUM = 2; // the size of the checksum
private final byte ID = 0x10; // the id of this device
private byte recivedlast = 0x00; // last message received? 0x01 : 0x00
private HashMap<String,Integer> type_lookup; // all possible instructions that can be added to the message
private HashMap<String,Integer> size_lookup; // the sizes of the messages
private final String [] lookupStrings = {"1A_3L","3T_1V"}; // add new commands to this array as necessary
private final int [] lookupSizes = {13,10}; // add new commands to this array as necessary
private byte [] message  = new byte[255];  // the whole message we want to send
private byte [] received = new byte[255]; // the whole received message
private int [] partner = new int[5]; // contains decoded info about the partner
private int [] values = new int[250]; //stores current values of things
int [] colors = new int[3];
private int lengthMistakes;
private int checksumMistakes;

Serial myPort;  // Create object from Serial class
String val;     // Data received from the serial port

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
  return (int) bytes2Int(received, size+index, index);
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
    String toReturn = new String(""); //<>//

    if (length == (int)bytes2Int(bytes, ADDR_LENGB+1, ADDR_LENGB)){
        toReturn += "Length Passed";
        length = (int) bytes2Int(bytes, 1+ADDR_LENGA, ADDR_LENGA);
    } else {
        toReturn += ", Length Failed: " + bytes2Int(bytes,1+ADDR_LENGA,ADDR_LENGA) + "/" + bytes2Int(bytes, 1+ADDR_LENGB, ADDR_LENGB);
        recivedlast = 0x00;
        lengthMistakes++;
        return toReturn;
    }
   //for (int i=0; i<length; i++){ // reads the bytes buffer into the received message
   //    received[i] = bytes[i];
   // }
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
    partner[ADDR_LENGA] = length;
    partner[ADDR_LENGB] = length;
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
    int2Bytes(size, message,1, ADDR_LENGA); // updates the length identifier
    int2Bytes(size, message,1, ADDR_LENGB); // updates the length identifier
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

public float floatMap(float x, float in_min, float in_max, float out_min, float out_max) {
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

public String readData (Serial myPort) {
    int counter = 0;
    int counter2 = 0;
    do {
    counter = myPort.read(); //<>//
    counter2 = myPort.read();
    } while (counter != counter2 || counter == 0);
    if (counter == 0 || counter2 == 0){
      return "counter = 0";
    }
    
    if (counter == counter2){
        int2Bytes(counter, received, 1, 0);
        int2Bytes(counter, received, 1, 1);
        for(int i=2; i<counter; i++){
            int2Bytes(myPort.read(), received, 1, i);
        }
    } else {
        return readInstruction(received, 0);
    }
    
    
    return readInstruction(received,counter); // 10 = counter?
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

public void setValues () {
    for (int i=0; i<250; i++){
        values[i] = 0;
    }
}

void setup()
{
  // I know that the first port in the serial list on my mac
  // is Serial.list()[0].
  // On Windows machines, this generally opens COM1.
  // Open whatever port is the one you're using.
  String portName = Serial.list()[1]; //change the 0 to a 1 or 2 etc. to match your port
  myPort = new Serial(this, portName, 38400);
  setLookup();
  setValues();
}

void draw()
{
  if ( myPort.available() > 0) 
  {  // If data is available,
    println(readData(myPort));
    println(readStringValue(0, 10));
  //val = myPort.readStringUntil('\n');         // read it and store it in val
  }
  
//println(val); //print it out in the console
}
