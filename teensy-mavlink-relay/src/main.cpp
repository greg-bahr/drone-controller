#include <Arduino.h>
#include <LoRa.h>
#include <mavlink/standard/mavlink.h>

const int csPin = 10;
const int resetPin = 4;
const int irqPin = 2;

byte localAddress = 0xBB;
byte destination = 0xAA;

void onReceive(int packetSize) {
  if (packetSize == 0) return;

  int recipient = LoRa.read();
  byte sender = LoRa.read();

  if (recipient != localAddress || sender != destination) {
    return;
  }

  while (LoRa.available()) {
    Serial3.write(LoRa.read());
  }
}

void setup() {
  Serial.begin(9600);
  Serial3.begin(115200, SERIAL_8N1);
  while(!Serial3);
  LoRa.setPins(csPin, resetPin, irqPin);

  if (!LoRa.begin(915E6)) {       
    Serial.println("LoRa init failed. Check your connections.");
    while (true);
  }

  LoRa.setTxPower(20);
  LoRa.onReceive(onReceive);
  LoRa.receive();
  Serial.println("LoRa init succeeded.");
}

void sendMessage(mavlink_message_t* msg) {
  uint8_t buf[MAVLINK_MAX_PACKET_LEN];
  uint16_t len = mavlink_msg_to_send_buffer(buf, msg);

  LoRa.beginPacket();
  LoRa.write(destination);
  LoRa.write(localAddress);
  LoRa.write(buf, len);
  LoRa.endPacket();
  
  LoRa.receive();
}

void listenToPixhawk() {
  mavlink_message_t msg;
  mavlink_status_t status;

  while (Serial3.available() > 0) {
    uint8_t c = Serial3.read();

    if (mavlink_parse_char(MAVLINK_COMM_0, c, &msg, &status)) {
      sendMessage(&msg);
    }
  }
}

void loop() {
  listenToPixhawk();
}
