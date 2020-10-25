#include <SoftwareSerial.h>
int btLow = 8;
int btHigh = 10;

int btRxd = 12;
int btTxd = 13;

int heartSensor = A0;

SoftwareSerial mySerial (btRxd, btTxd);

void setup() {
  pinMode(btLow, OUTPUT);
  pinMode(btHigh, OUTPUT);

  digitalWrite(btLow, LOW);
  digitalWrite(btHigh, HIGH);

  mySerial.begin(9600);
  
  pinMode(heartSensor, INPUT);
}

void loop() {
  mySerial.println(analogRead(heartSensor));
}
