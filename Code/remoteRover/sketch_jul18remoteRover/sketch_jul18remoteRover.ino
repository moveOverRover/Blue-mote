 #define LEFT 700
#define RIGHT 100
#define CENTER 400
#define R_POWER 127
#define L_POWER 64
#define STOP 2
#define FORWARD 1
#define BACK 0
#define MB 5
#define EN_B 4
#define EN_A 3
#define MA 2
#define SER 6

char state = 'c';
char clean, peak;
int pos = 90;
int waiting = 0;

#include<Servo.h>
Servo myServo;

void recive (){
  waiting = Serial.available();
  delay(10);
  if ( waiting > 4){
    peak = Serial.peek();
    
    if (peak != 'a' && peak != 'b' && peak != 'c'){
     // cleans the new line and other stuff
     while (Serial.available() > 0){
      clean = Serial.read();
     }
    } else {
      state = Serial.read();
    }
    pos = Serial.parseInt(SKIP_ALL);
    
  } else if (waiting > 0){
    while (Serial.available() > 0){
      clean = Serial.read();
     }
  }
}

void drive(int dir){
  if (dir == FORWARD){
    digitalWrite(EN_B, LOW);
    digitalWrite(MA, HIGH);
    digitalWrite(EN_A, HIGH);
    //analogWrite(EN_A,40);
  } else if (dir == BACK){
    digitalWrite(EN_A, LOW);
    digitalWrite(MB, HIGH);
    digitalWrite(EN_B, HIGH);
    //analogWrite(EN_B,40);
  } else {
    digitalWrite(EN_A, LOW);
    digitalWrite(EN_B, LOW);
  }
}

void setup(){
  pinMode(A0,INPUT);
  for(int k=2; k<8; k++){
    pinMode(k,OUTPUT);
  }
  myServo.attach(SER);
  Serial.begin(38400);
  digitalWrite(EN_A,LOW);
  digitalWrite(EN_B,LOW);
  digitalWrite(MA,LOW);
  digitalWrite(MB,LOW);
  
}

void loop() {
  /*
  recive();
  delay(10);
  
  if (state == 'a'){
    drive(FORWARD);
  } else if (state == 'b'){
    drive(BACK);
  } else {
    drive(STOP);
  }
 
  myServo.write(pos);
  */

  drive(FORWARD);
  delay(1000);
  Serial.println("forward");
}
