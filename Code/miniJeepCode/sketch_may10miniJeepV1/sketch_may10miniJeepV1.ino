/* author: Alex Semenov 
  First resonable working version of the blue-tooth reciver.
  No error checking features like checksums ect implemented here.
  Basic communication slow and unreliable, but it works.
  */


#define D1 3
#define D2 4
#define D3 5
#define T1 6
#define T2 7
#define T3 8
#define HORN 11
#define LIGHTS 10
#define MUSIC 12

#define X_UPPER 700
#define X_LOWER 300
#define Y_UPPER 700
#define Y_LOWER 300

#define SAFTY_PIN 2

int x = 500;
int y = 500;
char horn = 'b';
char lights = 'd';
char music = 'f';
int p = 0;

char cmd[100];
char srt;
char ste[6];
char pwr[6];
char btn[4];
int i,j;

void actions () {
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
  
  // this is for the features
  if (horn == 'a') {
    digitalWrite(HORN, HIGH);
  } else {
    digitalWrite(HORN, LOW);
  }
  if (lights == 'c') {
    digitalWrite(LIGHTS, HIGH);
  } else {
    digitalWrite(LIGHTS, LOW);
  }
  if (music == 'e') {
    digitalWrite(MUSIC, HIGH);
  } else {
    digitalWrite(MUSIC, LOW);
  }
}

void serialEvent(){
  p=0; // good connection
  TCNT1  = 0x00; //should reset timer
  j++;
  if (j>17){ //once whole message is recived
    j=0;
    takeIn();
    sort();
  }
}

void takeIn(){
  i=0;
  while((srt = Serial.read()) != -1){ // read entire message to memory
    delay(2);
    cmd[i++] = srt;
  }
  cmd[i] = '\0';
}

void sort(){ // Leave the maximum munch alone!
  i=0;
  j=0;
  while (cmd[i++] != '-'){} //scans for start
  while (cmd[i] != ' '){ // finds stearing data
    ste[j++] = cmd[i++];
  }
  ste[j] = '\0';
  j=0;
  while (cmd[++i] != ' '){ // finds drive data
    pwr[j++] = cmd[i];
  }
  pwr[j] = '\0';
  j=0;
  while(cmd[++i] != '>'){ // finds the feature data
    btn[j++] = cmd[i];
  }
  btn[j] = '\0';
  x=atoi(ste);
  y=atoi(pwr);
  horn = btn[0];
  lights = btn[1];
  music = btn[2];
  Serial.println(x);
  Serial.println(y);
  Serial.println(horn);
  Serial.println(music);
  Serial.println(lights);
}

ISR(TIMER1_COMPA_vect){//timer1 interrupt 10Hz: if timer is ever aloud to complete we have a bad connection
  p = 1; // bad connection
}

void setup(){ // compare match register = [16,000,000Hz/(prescaler*desired interupt frequency)]-1
  for(int k=3; k<13; k++){ // set all pins but 2 to be output
    pinMode(k,OUTPUT);
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
  OCR1A = 1500;// = (16*10^6) / (10*1024) - 1 (must be <65536) ***VERY IMPORTANT*** -LIKES TO BE A NUMBER THAT ENDS IN AT LEAST ONE '0', I HAVE NO IDEA WHY JUST LEAVE IT BE!
  // turn on CTC mode
  TCCR1B |= (1 << WGM12);
  // Set CS12 and CS10 bits for 1024 prescaler
  TCCR1B |= (1 << CS12) | (1 << CS10);  
  // enable timer compare interrupt
  TIMSK1 |= (1 << OCIE1A);
  sei();//allow interrupts
}

void loop() {
 if (p == 0 && digitalRead(SAFTY_PIN) == HIGH){ // good conection
   digitalWrite(13,HIGH);
   actions();
 } else { // bad conection
   digitalWrite(13,LOW);
   x = 500;
   y = 500;
   horn = 'b';
   lights = 'd';
   music = 'f';
   actions();
 }
}
