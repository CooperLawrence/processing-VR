# processing-VR
__Virtual Reality software programmed in Processing__ <br>
This entire project has been developed as a hobby during my final year of highschool. It has been successfully tested by a few hundred users at the 2015 Ottawa Maker Faire.

__Processing__ <br>
If you would like to view the source files, they are included in the download. The software has been updated to Processing 3, and you can download the latest version from their [website](https://processing.org/download/), or on [GitHub](https://github.com/processing/processing).

__Arduino__ <br>
Currently, the Arduino sketch that is used to send the accelerometer and gyroscope data to Processing isn't included in the download. You can write your own code, and send the serial data at a baud rate of 9600, in the format of "X,Y,Z" without the quotations. The sketch will eventually be included in the download, but for now, that's not my main priority. You can download Arduino from their [website](https://www.arduino.cc/en/Main/Software), or on [GitHub](https://github.com/arduino/Arduino).


__Future Updates__ <br>
I am currently working on a new GUI for the main menu. Whether this will be done inside Processing or using native Java is currently undecided.

At some point, the whole program will be rewritten and released as a contributed library for Processing. One of the main focuses before this happens is updating the code to allow the user to use any form of input (video capture, processing sketch, etc).

Another possibility is a complete port of the program over to native Java. If this were to happen, it would be re-released as a new project, and the current project in Processing will be preserved.


__Controls__ <br>
Save Profile: S and # <br>
Load Profile: L and # <br>

Toggle Head Tracking: V <br>

Increase Image Width: E <br>
Decrease Image Width: W <br>
Increase Image Height: Q <br>
Decrease Image Height: A <br>

Move Images Closer: Left Arrow <br>
Move Images Apart: Right Arrow <br>
Move Images Up: Up Arrow <br>
Move Images Down: Down Arrow <br>
