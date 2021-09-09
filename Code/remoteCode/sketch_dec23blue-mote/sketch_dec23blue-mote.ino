#define X_A A7
#define Y_A A6
#define X_B A5
#define Y_B A4
#define TOGGLE 2
#define BTN_A 3
#define BTN_B 4
#define R 5 
#define B 6
#define G 7
int x_a, y_a, x_b, y_b, toggle, btn_a, btn_b;

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
  
  delay(50);
  x_a = analogRead(X_A);
  y_a = analogRead(Y_A);
  x_b = analogRead(X_B);
  y_b = analogRead(Y_B);
  toggle = digitalRead(TOGGLE);
  btn_a = digitalRead(BTN_A);
  btn_b = digitalRead(BTN_B);
  
  x_a = map(x_a, 0, 1023, 0, 179);
  
  if (toggle == 1) {
    digitalWrite(R, HIGH);
    digitalWrite(B, LOW);
    digitalWrite(G, LOW);
  } else if (btn_a == 1) {
    digitalWrite(R, LOW);
    digitalWrite(B, HIGH);
    digitalWrite(G, LOW);
  } else if (btn_b == 1) {
    digitalWrite(R, LOW);
    digitalWrite(B, LOW);
    digitalWrite(G, HIGH);
  } else {
    digitalWrite(R, LOW);
    digitalWrite(B, LOW);
    digitalWrite(G, LOW);
  }
 
  Serial.print(x_a);
  Serial.print(' ');
  //Serial.print(y_a);
  //Serial.print(' ');
  //Serial.print(x_b);
  //Serial.print(" ");
  Serial.print(y_b);
  Serial.print(' ');
  Serial.print(toggle);
  Serial.print(' ');
  //Serial.println(btn_a);
  //Serial.print(" ");
  Serial.print(btn_b);
  Serial.println(' '); 
  
}
