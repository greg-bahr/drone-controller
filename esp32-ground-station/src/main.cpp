#include <Arduino.h>
#include <LoRa.h>
#include <mavlink/minimal/mavlink.h>
#include "BluetoothSerial.h"

byte localAddress = 0xAA;
byte droneAddress = 0xBB;

const int csPin = 5;
const int resetPin = 33;

BluetoothSerial SerialBT;

void onReceive(int packetSize) {
  if (packetSize == 0) return;

  // read packet header bytes:
  int recipient = LoRa.read();
  byte sender = LoRa.read();

  if (recipient != localAddress || sender != droneAddress) {
    return;
  }

  SerialBT.write(0xAB);
  SerialBT.write(LoRa.packetRssi());
  SerialBT.write(LoRa.packetSnr());

  while (LoRa.available()) {
    SerialBT.write(LoRa.read());
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

  while(SerialBT.available()) {
    uint8_t c = SerialBT.read();

    if(mavlink_parse_char(MAVLINK_COMM_0, c, &msg, &status)) {
      sendMessage(&msg);
    }
  }
}

void setup() {
  Serial.begin(9600);
  while (!Serial);

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
}
