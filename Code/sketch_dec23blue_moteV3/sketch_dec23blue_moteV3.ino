/* author: Alex Semenov
  Third resonable working version of the blue-tooth remote.
  Sends 2 more analog inputs than hte previous version (It now is sending every imput it has)
  Sends data at a lower resolution 8bit instead of 10 but with much more freqent sends and more data per send
  Calculates a checksum and send it with the data for error checking
  Much faster than the previous version (remeber to reduce the delay later can prolly be about 40)
*/
#define X_A A3 //left Joystick x-axis
#define Y_A A1 //left Joystick y-axis
#define X_B A4 //right Joysitck x-axis
#define Y_B A2 //right Joystick y-axis
#define TOGGLE 2
#define BTN_A 3
#define BTN_B 4
#define R 5 
#define B 6
#define G 7
int x_a, y_a, x_b, y_b, toggle, btn_a, btn_b, z;
char cmd [7];
char btn_c[4];

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

byte int2Byte(int x, boolean yesMap){ // helper function
  if (yesMap){
    x = map(x, 0, 1023, 0, 255); //must fit in one byte
  }
  return (byte)x;
}

void byteSum(int checkSum, char* msg){ // writes a 2 bit binary number to be used as the checksum
  msg[5] = 0x00;
  msg[6] = 0x00;
  
  if (checkSum < 256){ // if the check sum is small we only need one byte so we can write the other one to be 0
    msg[5] = 0x00;
    msg[6] = (char)int2Byte(checkSum,false);
    return;
  }
  
  if (checkSum%2 != 0){ // if checkSum is odd then we know the first bit is a 1
    cmd[6] = cmd[6] | 0x01;
  }
  
  int shift;
  byte mask;
  int tempSum = checkSum;
  
  while ((shift=int2Bit(tempSum)) != 0){ // I forgot how all of this works too
    //Serial.print(shift);
    //Serial.print(": ");
    //Serial.println(tempSum);
    tempSum = tempSum - power(2,shift);
    mask = 0x01;
    if (shift > 7){
      shift -=8;
      mask <<= shift;
      msg[5] = msg[5] | mask;
    } else {
      mask <<= shift;
      msg[6] = msg[6] | mask;
    }
  }
}

int int2Bit(int tempSum){ // determines bigest bit position of checksum *** helper function
  int k =0;
  while((tempSum =tempSum/2) >= 1){
   k++; 
  }
  return k;
}

unsigned long int getSum(char *msg, int leng){ // makes a serires of bytes into a binary number which is returned as an integer
 byte mask;
 unsigned long int sum = 0;
  
  for (int i=leng; i>0; i--){ // repetes untill every byte has been prosessed start at last byte
    //if (msg[i-1] == 0x00){
    //  return 0;
    //}
    mask = 0x01;
    for (int j=0; j<8; j++){ // start at first bit
      if(msg[i-1] & mask){ // if bit is 1
       sum += power(2,j+(8*(leng-i))); // add bit
      }
      mask <<= 1; // bit shift mask to the left
    }
  }
  return sum;
}

int cSum(char* msg, int leng){ // calcualtes integer checksum
  int sum =0;
  for (int k=0; k<leng; k++){
    sum+=getSum(&msg[k],1);
  }
  return sum;
}

void assemble(char* msg){ // makes the message
  int checkSum = 0;
  msg[0] = (char)int2Byte(x_a, true);
  msg[1] = (char)int2Byte(x_b, true);
  msg[2] = (char)int2Byte(y_a, true);
  msg[3] = (char)int2Byte(y_b, true);
  msg[4] = (char)int2Byte(z, false);
  byteSum(cSum(cmd, 5), cmd);
}

void zDoer(){ // inner btn has proprity over outer btn
  if (btn_c[0] == 'b' && btn_c[1] == 'd' && btn_c[3] == 'f'){ // nothing
    z=0;
  } else if (btn_c[0] == 'a' && btn_c[1] == 'd' && btn_c[2] == 'f'){ // toggle
    z=1;
  } else if (btn_c[0] == 'a' && btn_c[1] == 'c' && btn_c[2] == 'f'){ // toggle + inner
    z=2;
  } else if (btn_c[0] == 'a' && btn_c[1] == 'd' && btn_c[2] == 'e') { // toggel + outer
    z=3;
  } else if (btn_c[1] == 'c') { // inner
    z=4;
  } else if (btn_c[2] == 'e') { // outer
    z=5;
  }
}

void btns(){
  if (toggle == 1) {
    digitalWrite(R, HIGH);
    digitalWrite(B, LOW);
    digitalWrite(G, LOW);
    btn_c[0] = 'a';
  } else {
    btn_c[0] = 'b';
  }
  if (btn_a == 1) {
    digitalWrite(R, LOW);
    digitalWrite(B, HIGH);
    digitalWrite(G, LOW);
    btn_c[1] = 'c';
  }else {
    btn_c[1] ='d';
  }
  if (btn_b == 1) {
    digitalWrite(R, LOW);
    digitalWrite(B, LOW);
    digitalWrite(G, HIGH);
    btn_c[2] = 'e';
  }else {
    btn_c[2] = 'f';
  }
  if (toggle != 1 && btn_a != 1 && btn_b != 1){
    digitalWrite(R, LOW);
    digitalWrite(B, LOW);
    digitalWrite(G, LOW);
  }
}

void printMsg(){
 for(int i=0; i<7; i++){
  Serial.write(cmd[i]);
 }
}

void setup() {
  Serial.begin(38400);
  pinMode(TOGGLE, INPUT);
  pinMode(BTN_A, INPUT);
  pinMode(BTN_B, INPUT);
  pinMode(R, OUTPUT);
  pinMode(G, OUTPUT);
  pinMode(B, OUTPUT);
}

void loop() {
  delay(40); // the reciver needs this big of a delay becuase of the speed it reads data in
  // read all sensor data
  x_a = analogRead(X_A);
  y_a = analogRead(Y_A);
  x_b = analogRead(X_B);
  y_b = analogRead(Y_B);
  toggle = digitalRead(TOGGLE);
  btn_a = digitalRead(BTN_A);
  btn_b = digitalRead(BTN_B);
  
  btns(); // lights up the led and configs the button data
  zDoer(); // finds the z byte ascii value
  assemble(cmd);
  
  if(toggle == HIGH){ // if the toggle is high then enable transmission otherwise do nothing
    printMsg();
  }
}
