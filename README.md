# Digital-Design-Lab
Android camera security system.


This project tracks movement from a motion senor that activates the Android mobile phone camera and takes an image of 
the intruder. The motion tracking is done using an MSP430 microcontroller (code for that is not included in this file). 
For the MSP430 to be able to jump between tasks we used Mailboxes, which jumps between UART, HC-05 bluetooth module and
the mostion sensor. 

The android app (code provided in this project) turns on the phones camera, when motion is detected an image is taken. 

Bugs in this project: When an image is taken it is not saved on the camera roll in real time, it is saved after some time and
also if the phone is restated.
