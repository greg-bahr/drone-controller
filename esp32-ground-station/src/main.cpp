#include <Arduino.h>
#include <LoRa.h>
#include <mavlink/standard/mavlink.h>
#include "BluetoothSerial.h"

#define BUFFER_SIZE 10000
#define FLUSH_INTERVAL 100

byte localAddress = 0xAA;
byte droneAddress = 0xBB;

const int csPin = 5;
const int resetPin = 33;

BluetoothSerial SerialBT;

unsigned long previousMillis = 0;
byte buffer[BUFFER_SIZE];
int bufferLocation = 0;

void flushBuffer() {
  for (int i = 0; i < bufferLocation; i++) {
    SerialBT.write(buffer[i]);
    buffer[i] = 0;
  }
  bufferLocation = 0;
}

void onReceive(int packetSize) {
  if (packetSize == 0) return;

  // read packet header bytes:
  int recipient = LoRa.read();
  byte sender = LoRa.read();

  if (recipient != localAddress || sender != droneAddress) {
    return;
  }

  while (LoRa.available()) {
    if (bufferLocation == BUFFER_SIZE) {
      flushBuffer();
    }

    buffer[bufferLocation] = LoRa.read();
    bufferLocation++;
  }
}

void sendMessage(mavlink_message_t* msg) {
  uint8_t buf[MAVLINK_MAX_PACKET_LEN];
  uint16_t len = mavlink_msg_to_send_buffer(buf, msg);

  LoRa.beginPacket();
  LoRa.write(droneAddress);
  LoRa.write(localAddress);
  LoRa.write(buf, len);
  LoRa.endPacket();
}

void listenToBluetooth() {
  mavlink_message_t msg;
  mavlink_status_t status;

  if (SerialBT.available()) {
    while(SerialBT.available()) {
      uint8_t c = SerialBT.read();

      if(mavlink_parse_char(MAVLINK_COMM_0, c, &msg, &status)) {
        sendMessage(&msg);
      }
    }
  }
}

void setup() {
  Serial.begin(9600);
  while (!Serial);

  previousMillis = millis();
  SerialBT.begin("Ground Relay");
  LoRa.setPins(csPin, resetPin, -1);

  if (!LoRa.begin(915E6)) {       
    Serial.println("LoRa init failed. Check your connections.");
    while (true);
  }

  LoRa.setTxPower(20);
  Serial.println("LoRa init succeeded.");
}

void loop() {
  onReceive(LoRa.parsePacket());
  listenToBluetooth();

  unsigned long currentMillis = millis();
  if (currentMillis - previousMillis >= FLUSH_INTERVAL) {
    flushBuffer();
    previousMillis = currentMillis;
  }
}
