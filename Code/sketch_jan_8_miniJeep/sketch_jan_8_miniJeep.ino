#define D1 3
#define D2 4
#define D3 5
#define T1 6
#define T2 7
#define T3 8
#define HORN 10
#define LIGHTS 9

#define X_UPPER 120
#define X_LOWER 60
#define Y_UPPER 700
#define Y_LOWER 300

#define SAFTY_PIN 2

int x = 90;
int y = 500;
int horn = 0;
int lights = 0;
String clean;

void actions () {
  // this is for strearing
  if (x > X_UPPER) {
    digitalWrite(T3,HIGH);
    digitalWrite(T2,HIGH);
    digitalWrite(T1,LOW);
    
  } else if (x < X_LOWER) {
    digitalWrite(T3,HIGH);
    digitalWrite(T2,LOW);
    digitalWrite(T1,HIGH);
    
  } else {
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

  if (horn == 1) {
    tone(HORN, 400, 250);
  } else {
   noTone(HORN);
  }

  if (lights == 1) {
    digitalWrite(LIGHTS, HIGH);
  } else {
    digitalWrite(LIGHTS, LOW);
  }
}

void recive (){
    x = Serial.parseInt(SKIP_ALL);
    y = Serial.parseInt(SKIP_ALL);
    lights = Serial.parseInt(SKIP_ALL);
    horn = Serial.parseInt(SKIP_ALL);
    
    while (Serial.available() > 0){
      clean = Serial.read();
     }
}


void setup(){
  for(int k=3; k<11; k++){
    pinMode(k,OUTPUT);
    //digitalWrite(k, LOW);
  }
  Serial.begin(38400);
  pinMode(SAFTY_PIN, INPUT);
}

void loop() {
 

  if (digitalRead(SAFTY_PIN) == HIGH) {
     recive();

  } else {
      x = 90;
      y = 500;
      horn = 0;
      lights = 0;
  }

 
  
  actions();
  

/*
  // debugging
  recive();
  
  Serial.print(x);
  Serial.print(", ");
  Serial.println(y);
 */
}
