# processing-VR
__Virtual Reality software programmed in Processing__ <br>
This entire project has been developed as a hobby during my final year of highschool. It has been successfully tested by a few hundred users at the 2015 Ottawa Maker Faire.

__Processing__ <br>
If you would like to view the source files, they are included in the download. The software has yet to be updated to Processing 3, so only run the sketch's source files in Processing 2. You can download Processing 2.2.1 from their [website](https://processing.org/download/), or on [GitHub](https://github.com/processing/processing).

__Arduino__ <br>
Currently, the Arduino sketch that is used to send the accelerometer and gyroscope data to Processing isn't included in the download. You can write your own code, and send the serial data at a baud rate of 9600, in the format of "X,Y,Z" without the quotations. The sketch will eventually be included in the download, but for now, that's not my main priority. You can download Arduino from their [website](https://www.arduino.cc/en/Main/Software), or on [GitHub](https://github.com/arduino/Arduino).

__Controls__ <br>
Save Profile: S and # <br>
Load Profile: L and # <br>

Increase Image Width: W <br>
Decrease Image Width: Q <br>
Increase Image Height: A <br>
Decrease Image Height: Z <br>

Move Images Closer: Right Arrow <br>
Move Images Apart: Left Arrow <br>
Move Images Up: Up Arrow <br>
Move Images Down: Down Arrow <br>

__Future Updates__ <br>
Processing 3! The only reason why a new version built in Processing 3 hasn't been released yet is the amount of broken functions and native Java code that doesn't work properly. As of now, the only thing that's holding it up is that trying to get the sketch window's location constantly returns (0, 0).

At some point, the whole program will be rewritten and released as a contributed library for Processing. One of the main focuses before this happens is updating the code to allow the user to use any form of input (video capture, processing sketch, etc).

Stereoscopic vision through hardware image splitting! Rather than splitting the headset's video input via software (which is slow and limits your max resolution), it can be done through the hardware, sending the video to two separate displays mounted in the headset.
