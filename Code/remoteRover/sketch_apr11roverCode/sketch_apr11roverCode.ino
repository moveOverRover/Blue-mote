
#define STOP 2
#define FORWARD 1
#define BACK 0
#define MB 5
#define EN_B 4
#define EN_A 3
#define MA 2
#define SERVO 6
#define TRIG 9
#define ECHO 8

#include <Ultrasonic.h>
Ultrasonic ultrasonic(TRIG,ECHO); // establishes trig and echo pins
#include <Servo.h>
Servo myServo;

int D, lastD, auxD = 5;

void drive(int dir){
  if (dir == FORWARD){
    digitalWrite(MA, HIGH);
    digitalWrite(EN_A, HIGH);
  } else if (dir == BACK){
    digitalWrite(MA, LOW);
    digitalWrite(EN_A, HIGH);
  } else {
    digitalWrite(EN_A, LOW);
  }
}

void setup(){
  pinMode(A0,INPUT);
  for(int k=2; k<7; k++){
    pinMode(k,OUTPUT);
  }
  pinMode(TRIG,OUTPUT);
  pinMode(ECHO,INPUT);
  myServo.attach(SERVO);
  myServo.write(70);
  Serial.begin(9600);
  digitalWrite(EN_A,LOW);
  digitalWrite(EN_B,HIGH);
  Serial.println("Hello World!");
}

void loop() {
    D = ultrasonic.read();
  Serial.print("distance in cm:  ");
  Serial.println(D);
  if (D > 70){
    drive(FORWARD);
    myServo.write(70);
  } else if (D > 10) {
    myServo.write(0);
    delay(7000);
  } else {
    drive(STOP); // something went wrong here
  }
  delay(100);
}
