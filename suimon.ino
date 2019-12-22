#include <ESP8266WiFi.h>
#include <FirebaseArduino.h>    
#include <ArduinoJson.h> 
#include <FirebaseHTTPClient.h>
#define STEPPER_PIN_1 D2
#define STEPPER_PIN_2 D3
#define STEPPER_PIN_3 D4
#define STEPPER_PIN_4 D5
#define FIREBASE_HOST "suimon2.firebaseio.com"
#define FIREBASE_AUTH "x3gMbN3tCSl0xmuWw37buwlm7geKAC8jkX8NWrUW"
#define WIFI_SSID "cobacoba"                                             // input your home or public wifi name 
#define WIFI_PASSWORD "nunutaeteros"                                    //password of wifi ssid

int step_number = 0;
const int trigPin = D0;  //D4
const int echoPin = D1;  //D3

long duration;int distance;
int adwal, gate, lastgate, lastdist, distPercent, gateConv;
void ultrasonik(){
  digitalWrite(trigPin, LOW);
  delayMicroseconds(2);
// Sets the trigPin on HIGH state for 10 micro seconds
  digitalWrite(trigPin, HIGH);
  delayMicroseconds(10);
  digitalWrite(trigPin, LOW);
  
  // Reads the echoPin, returns the sound wave travel time in microseconds
  duration = pulseIn(echoPin, HIGH);
  // Calculating the distance
  distance= duration*0.034/2;
  
  // Prints the distance on the Serial Monitor
  Serial.print("Distance: ");
  Serial.println(distance);
  Firebase.setInt("ultrasonik",distance);
   if (Firebase.failed()) {
      Serial.print("setting /message failed:");
      Serial.println(Firebase.error());  
  }
  
}

void AdwalFunction(){
  adwal = Firebase.getInt("adwal");
  if(adwal == 1){
    Serial.print("Mode :");
    Serial.println(adwal);
    
  }else{
    Serial.print("Mode :");
    Serial.println(adwal);
  }
}

void GateFunction(){
  gate = Firebase.getInt("gate");
  
    Serial.print("Gate Open :");
    Serial.print(gate);
    Serial.println("%");
    
}

void manual(){
  if(gate != lastgate){
    if(lastgate >= gate){
      for(int a = 0; a <= abs(lastgate - gate) *50; a++){
        OneStep(true);
        delay(2);
      }
    }
    else if(lastgate <= gate){
      for(int a = 0; a <= abs(lastgate - gate) *50; a++){
        OneStep(false);
        delay(2);
      }
    }
    lastgate = gate;
  }
}

void automatic(){
   if(abs(distance-30) != lastdist){
    if(lastdist >= abs(distance-30)){
      for(int a = 0; a <= abs(lastdist - abs(distance-30)) *166; a++){
        OneStep(true);
        delay(2);
      }
    }
    else if(lastdist <= abs(distance-30)){
      for(int a = 0; a <= abs(lastdist - abs(distance-30)) *166; a++){
        OneStep(false);
        delay(2);
      }
    }
    lastdist = abs(distance-30);
  }
}
void OneStep(bool dir){
    if(dir){
      switch(step_number){
        case 0:
        digitalWrite(STEPPER_PIN_1, HIGH);
        digitalWrite(STEPPER_PIN_2, LOW);
        digitalWrite(STEPPER_PIN_3, LOW);
        digitalWrite(STEPPER_PIN_4, LOW);
        break;
        case 1:
        digitalWrite(STEPPER_PIN_1, LOW);
        digitalWrite(STEPPER_PIN_2, HIGH);
        digitalWrite(STEPPER_PIN_3, LOW);
        digitalWrite(STEPPER_PIN_4, LOW);
        break;
        case 2:
        digitalWrite(STEPPER_PIN_1, LOW);
        digitalWrite(STEPPER_PIN_2, LOW);
        digitalWrite(STEPPER_PIN_3, HIGH);
        digitalWrite(STEPPER_PIN_4, LOW);
        break;
        case 3:
        digitalWrite(STEPPER_PIN_1, LOW);
        digitalWrite(STEPPER_PIN_2, LOW);
        digitalWrite(STEPPER_PIN_3, LOW);
        digitalWrite(STEPPER_PIN_4, HIGH);
        break;
    } 
  }else{
      switch(step_number){
      case 0:
      digitalWrite(STEPPER_PIN_1, LOW);
      digitalWrite(STEPPER_PIN_2, LOW);
      digitalWrite(STEPPER_PIN_3, LOW);
      digitalWrite(STEPPER_PIN_4, HIGH);
      break;
      case 1:
      digitalWrite(STEPPER_PIN_1, LOW);
      digitalWrite(STEPPER_PIN_2, LOW);
      digitalWrite(STEPPER_PIN_3, HIGH);
      digitalWrite(STEPPER_PIN_4, LOW);
      break;
      case 2:
      digitalWrite(STEPPER_PIN_1, LOW);
      digitalWrite(STEPPER_PIN_2, HIGH);
      digitalWrite(STEPPER_PIN_3, LOW);
      digitalWrite(STEPPER_PIN_4, LOW);
      break;
      case 3:
      digitalWrite(STEPPER_PIN_1, HIGH);
      digitalWrite(STEPPER_PIN_2, LOW);
      digitalWrite(STEPPER_PIN_3, LOW);
      digitalWrite(STEPPER_PIN_4, LOW);
    } 
  }
  step_number++;
    if(step_number > 3){
      step_number = 0;
    }
}

void setup() {
  Serial.begin(115200);
  //mOTOR
  pinMode(STEPPER_PIN_1, OUTPUT);
  pinMode(STEPPER_PIN_2, OUTPUT);
  pinMode(STEPPER_PIN_3, OUTPUT);
  pinMode(STEPPER_PIN_4, OUTPUT);

  //ultrasonic
  
  pinMode(trigPin, OUTPUT); // Sets the trigPin as an Output
  pinMode(echoPin, INPUT); // Sets the echoPin as an Input

  
  // put your setup code here, to run once:
  delay(1000);                
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);                                     //try to connect with wifi
  Serial.print("Connecting to ");
  Serial.print(WIFI_SSID);
  while (WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
    delay(500);
  }
  
  Serial.println();
  Serial.print("Connected to ");
  Serial.println(WIFI_SSID);
  Serial.print("IP Address is : ");
  Serial.println(WiFi.localIP());                                            //print local IP address
  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);                              // connect to firebase
     if (Firebase.failed()) {
      Serial.print("setting /message failed:");
      Serial.println(Firebase.error());  
  }

  
}

void loop() {
  // put your main code here, to run repeatedly:
  ultrasonik();
  AdwalFunction();
  GateFunction();
  if(adwal == 1){
    automatic();
    distPercent = lastdist / 30 * 100;
    lastgate = distPercent;
  }else{
    manual();
    gateConv = lastgate / 100 *30;
    lastdist = gateConv;
  }
  
}
