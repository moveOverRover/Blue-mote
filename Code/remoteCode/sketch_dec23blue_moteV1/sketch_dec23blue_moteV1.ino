/* author: Alex Semenov 
  First resonable working version of the blue-tooth remote.
  No error checking features like checksums ect implemented here.
  Basic communication slow and unreliable, but it works.
  */


#define X_A A5
#define Y_A A4
#define X_B A6
#define Y_B A7
#define TOGGLE 2
#define BTN_A 3
#define BTN_B 4
#define R 5 
#define B 6
#define G 7
int x_a, y_a, x_b, y_b, toggle, btn_a, btn_b;
char btn_c [4];
char j_a [6];
char j_b [5];
char cmd [17];


int placevalue (int toCheck){
  if(toCheck == 0){
    return 0;
    
  } else if(toCheck > 0){ // positive 
  
    if(toCheck <10){
      return 1;
    } else if (toCheck >999){
      return 4;
    } else if(toCheck >99){
      return 3;
    } else if(toCheck >9){
      return 2;
    }
  } else { // negative
  
    if(toCheck >-10){
      return 1;
    } else if (toCheck <-999){
      return 4;
    } else if(toCheck <-99){
      return 3;
    } else if(toCheck <-9){
      return 2;
    }
  }
  return 0;
}

char* toChar(int myInt, char* myChar, boolean mod){
  int i=0;
  boolean isNegative = false;
  
  if (myInt == 0) { // =0
    if(mod == true){
      myChar[0] = '-';
      for(int j=1; j<5; j++){
        myChar[j] = '0';
      }
      myChar[5] = '\0';
    } else{
      for(int j=0; j<4; j++){
        myChar[j] = '0';
      }
      myChar[4] = '\0';
    }
    return myChar;
    
  } else if(myInt<999 && myInt >-999){ // my int needs a leading 0
    if(myInt > 0){ // no negative
      
        for (i=0; i<4-placevalue(myInt); i++){ //creates leading 0's
          myChar[i] = '0';
        }
        myChar[4] = '\0';
        isNegative = false;

    } else if(myInt < 0) { // negative
    
      myChar[0] = '-';
      for (i=1; i<5-placevalue(myInt); i++){ //creates leading 0's
        myChar[i] = '0'; 
      }
      myChar[5] = '\0';
      isNegative = true;
      
    }// we now have leading 0's, i is set to the right number and we know the size of the char 
    char temp [6] = "hello";
    
    itoa(myInt, temp, 10);
    
    if(isNegative == true){
        for( int j = 1; j<placevalue(myInt)+1; j++){ //dump the rest of the number in
        myChar[i++] = temp[j];
      }
    } else {
      for( int j = 0; j<placevalue(myInt); j++){ //dump the rest of the number in
        myChar[i++] = temp[j];
      }
    }
    return myChar;
    
  }  else { // no leading zero needed
    itoa(myInt, myChar, 10);
    return myChar;
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
  cmd[0] = '<';
  for(int i=0; i<5; i++){
    cmd[i+1] = j_a[i];
  }
  cmd[6] = ' ';
  for(int i=0; i<4; i++){
    cmd[7+i] = j_b[i];
  }
  cmd[11] = ' ';
  for(int i=0; i<3; i++){
    cmd[12+i] = btn_c[i];
  }  
  cmd[15] = '>';
  cmd[16] = '\n';
  for(int i=0; i<17; i++){
    Serial.print(cmd[i]);
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
  delay(50);
  x_a = -1*analogRead(X_A);
  y_a = analogRead(Y_A);
  x_b = analogRead(X_B);
  y_b = analogRead(Y_B);
  toggle = digitalRead(TOGGLE);
  btn_a = digitalRead(BTN_A);
  btn_b = digitalRead(BTN_B);
  
  btns();
  toChar(x_a, j_a, true);
  toChar(y_b, j_b, false);
  printMsg();
}
