/* author: Alex Semenov
  Third resonable working version
  Reads more data at a lower resolution and along with the checksum this greatly improves the speed (by about 4x)
  Dosent use all data recived so that the remote can send every input it has and only some need to be used by the reciver
  Uses a checksum to determine the integrity of hte message
  Kill swith is integrated
  Made the intterupt contain siginicantly less code and switch varriables moddifed in the intterupt to volatle
*/
#define INC_MSG 7 // size of the incomming message
#define OUT_MSG 5 // size of the outgoing message

#define D1 3 // drive pin one
#define D2 4 // drive pin two
#define D3 5 // drive pin three
#define T1 6 // turning pin one
#define T2 7 // turning pin two
#define T3 8 // turning pin three
#define HORN 11 
#define LIGHTS 10
#define MUSIC 12
#define X_UPPER 175 // upper limit of dead zone x joystick 
#define X_LOWER 80 // lower limit of dead zone x joystick
#define Y_UPPER 175 // upper limit of dead zone y joystick 
#define Y_LOWER 75 // lower limit of dead zone y joystick
#define SAFTY_PIN 2 // pint that reads the state of the bluetooth modual

int x = 130; // current x position
int y = 130; // current y position
int z = 0; // state of digital functions
char horn = 'b'; 
char lights = 'd';
char music = 'f';
volatile int p = 0; // if information has been resently recived p=0
char cmd[INC_MSG]; // incoming message
char srt; // buffer var
char ret[OUT_MSG];
volatile int j; // amount of serial events

int power(int a, int b){ // a = base, b = exponent
  if(b==0){
    return 1;
  }
  int og = a;
  for(int q=0; q<b-1; q++){
    a = a*og;
  }
  return a;
}

unsigned long int bytes2Int(char *bytes, int leng, int offset){ // makes a series of bytes into a binary number which is returned as an integer
 byte mask;
 unsigned long int sum = 0;
  
  for (int i=leng; i>offset; i--){ // repetes untill every byte has been prosessed start at last byte
    mask = 0x01;
    for (int k=0; k<8; k++){ // start at first bit
      if(bytes[i-1] & mask){ // if bit is 1
       sum += power(2,k+(8*(leng-i))); // add bit
      }
      mask <<= 1; // bit shift mask to the left
    }
  }
  return sum;
}

void int2Bytes(int num, char* bytes, int leng, int offset){ // writes a binary number from an int
  for (int i=offset; i<leng+offset; i++){ //sets all bits in message to 0
    bytes[i] = 0x00;
  }
  
  if (num%2 != 0){ // if num is odd then we know the first bit is a 1
    bytes[leng+offset-1] = bytes[leng+offset-1] | 0x01;
  }
  
  int shift, pos;
  byte mask;
  int tempNum = num;
  
  while ((shift=int2Bit(tempNum)) != 0){ // while there is bits left to add
    tempNum = tempNum - power(2,shift); // removes previous bit
    mask = 0x01; // resets the mask
    mask <<= shift%8; // moves the mask to the current bit
    pos = bytePos(leng, shift); //finds the current byte
    bytes[pos+offset] = bytes[pos+offset] | mask; // adds the bit to the byte
  }
}

int int2Bit(int tempNum){ // determines bigest bit position of checksum
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

int cSum(char* bytes, int leng){ // calcualtes integer checksum
  int sum =0;
  for (int k=0; k<leng; k++){
    sum+=bytes2Int(&bytes[k],1,0);
  }
  return sum;
}

void actions () {
  // this is for steering
  if (x > X_UPPER){
    digitalWrite(T1,HIGH);
    digitalWrite(T2,LOW);
    digitalWrite(T3,HIGH); 
  } else if (x < X_LOWER){
    digitalWrite(T1,HIGH);
    digitalWrite(T2,HIGH);
    digitalWrite(T3,LOW);
  } else {
    digitalWrite(T1,LOW);
    digitalWrite(T2,LOW);
    digitalWrite(T3,LOW);
  }
  // this is for dirving
  if (y > Y_UPPER){
    digitalWrite(D3,HIGH);
    digitalWrite(D2,LOW);
    digitalWrite(D1,HIGH);
  } else if (y < Y_LOWER) {
    digitalWrite(D3,HIGH);
    digitalWrite(D2,HIGH);
    digitalWrite(D1,LOW);
  } else {
    digitalWrite(D3,LOW);
    digitalWrite(D2,LOW);
    digitalWrite(D1,LOW);
  }
  
  // this is for the features
  if (horn == 'a') {
    digitalWrite(HORN, HIGH);
  } else if (horn == 'b') {
    digitalWrite(HORN, LOW);
  }
  if (lights == 'c') {
    digitalWrite(LIGHTS, HIGH);
  } else if (lights == 'd'){
    digitalWrite(LIGHTS, LOW);
  }
  if (music == 'e') {
    digitalWrite(MUSIC, HIGH);
  } else if (music == 'f') {
    digitalWrite(MUSIC, LOW);
  }
}

void serialEvent(){
  p=0; // good connection
  j++; // counts a serial event
  TCNT1  = 0x00; // should reset timer
}

void zDoer(){
  if(z == 0){ // OFF
    horn = 'b';
    lights = 'd';
    music = 'f';
  } else if (z == 1){ // LIGHTS
    horn = 'b';
    lights = 'c';
    music = 'f';
  } else if (z == 2){ // LIGHTS and HORN
    horn = 'a';
    lights = 'c';
    music = 'f';
  } else if (z == 3){ //  LIGHTS and MUSIC
    horn = 'b';
    lights = 'c';
    music = 'e';
  } else if (z == 4){ // HORN
    horn = 'a';
    lights = 'd';
    music = 'f';
  } else if (z == 5){ // MUSIC
    horn = 'b';
    lights = 'd';
    music = 'e';
  }
}

char* takeIn(char *bytes){
  int i=0;
  int flag = 0;
  while((srt = Serial.read()) != -1){ // read entire message to memory
    delay(2); // could by shorter?
    if(i<INC_MSG){
      bytes[i++] = srt;
    }
  }
  if (i==INC_MSG){ // if too few or too many bytes are recived throw the message away
    return bytes;
  } else {
    return NULL;
  }
}

ISR(TIMER1_COMPA_vect){//timer1 interrupt 5Hz: if timer is ever aloud to complete we have a bad connection
  p = 1; // bad connection
}

void debuggin(){
  /*
  Serial.print("cmd: ");
    for(int i=0;i<INC_MSG;i++){
      Serial.write(cmd[i]);
    }
    Serial.write('\n');
  
    Serial.print("a_x: ");
    Serial.println(bytes2Int(&cmd[0],1));
    Serial.print("a_y: ");
    Serial.println(bytes2Int(&cmd[1],1));
    Serial.print("b_x: ");
    Serial.println(bytes2Int(&cmd[2],1));
    Serial.print("b_y: ");
    Serial.println(bytes2Int(&cmd[3],1));
    Serial.print("z: ");
    Serial.println(z);
    
    Serial.print(cSum(cmd,5));
    Serial.print(" = ");
    Serial.println(bytes2Int(cmd,7,5));
    */

    Serial.print("0: ");
    Serial.println(x);
    Serial.print("1: ");
    Serial.println(bytes2Int(&cmd[1],1,0));
    Serial.print("2: ");
    Serial.println(bytes2Int(&cmd[2],1,0));
    Serial.print("3: ");
    Serial.println(y);
    
}

void setup(){ // compare match register = [16,000,000Hz/(prescaler*desired interupt frequency)]-1
  for(int k=3; k<13; k++){ // set all pins but 2 to be output
    if (k!=6){ // pin 6 has an internal short on current board
       pinMode(k,OUTPUT);
    }
    //digitalWrite(k, LOW);
  }
  Serial.begin(38400);
  pinMode(SAFTY_PIN, INPUT);
  pinMode(13,OUTPUT);
  j=0; // Very important to make zero

  cli(); //stop interrupts
  //set timer1 to 10Hz
  TCCR1A = 0;// set entire TCCR1A register to 0
  TCCR1B = 0;// same for TCCR1B
  TCNT1  = 0;//initialize counter value to 0
  // set compare match register for 10hz increments
  //OCR1A = 1500;// = (16*10^6) / (10*1024) - 1 (must be <65536) ***VERY IMPORTANT*** -LIKES TO BE A NUMBER THAT ENDS IN AT LEAST ONE '0', I HAVE NO IDEA WHY JUST LEAVE IT BE!
  OCR1A = 3000; // double the pervious time becuase it runs better this way
  // turn on CTC mode
  TCCR1B |= (1 << WGM12);
  // Set CS12 and CS10 bits for 1024 prescaler
  TCCR1B |= (1 << CS12) | (1 << CS10);  
  // enable timer compare interrupt
  TIMSK1 |= (1 << OCIE1A);
  sei();//allow interrupts
}

void loop() {
  if(j>INC_MSG){ //once enough bytes have been recived
    j=0;  // reset the war clock
    if (takeIn(cmd) != NULL){ // take in the bytes
      if (cSum(cmd,5) == bytes2Int(cmd,7,5)){ // if the check sum checks out then we can change stuff
        x = bytes2Int(&cmd[0],1,0);
        y = bytes2Int(&cmd[3],1,0);
        z = bytes2Int(&cmd[4],1,0);

        int2Bytes(1,ret,1,0); // if data was good then send good data conformation
      } else {
        int2Bytes(0,ret,1,0); // if data was bad then send bad data conformation
      }
      debuggin();
    } else {
      int2Bytes(0,ret,1,0); // if data was bad then send bad data conformation
    }
    int2Bytes(2,ret,1,1); // message contents
    int2Bytes(3,ret,1,2); // message contents
    int2Bytes(cSum(ret,3), ret, 2, 3); // checksum
    //for(int i=0; i<OUT_MSG; i++){
      //Serial.write(ret[i]);
    //}
  }
  
 if (p == 0 && digitalRead(SAFTY_PIN) == LOW){ // good conection *** saftey pin set low for testing
   digitalWrite(13,HIGH);
   zDoer();
   actions();
 } else { // bad conection
   digitalWrite(13,LOW);
   x = 130;
   y = 130;
   horn = 'b';
   lights = 'd';
   music = 'f';
   actions();
 }
}
