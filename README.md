# drone-controller
Telemetry radios and an android bluetooth groundstation to control a drone over MAVLink.

- The Teensy acts as a telemetry radio connected to the Pixhawk flight controller on the drone itself.
- The ESP32 relays the MAVLink packets from the Teensy over Bluetooth to an Android app
- The Android app can receive telemetry from the ESP32 and send commands to fly the drone
