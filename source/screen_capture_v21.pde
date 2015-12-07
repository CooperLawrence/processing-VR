
//Java libraries used to get some info like mouse position, change the window size and position, remove the frame, etc.
import java.awt.*;

//Processing Serial library to read incomming data from the arduino
import processing.serial.*;

//Classes to run the headset's different modes
ScreenCapture screenCapture;

//Java robot objects
Robot screenshotRobot; //Create the robot object to capture the screen
Robot mouseRobot; //Create the robot object to move the mouse

//Objects to inform the user of the different displays attatched to the computer
GraphicsEnvironment g = GraphicsEnvironment.getLocalGraphicsEnvironment(); //Load all the connected displays
GraphicsDevice[] displays = g.getScreenDevices(); //Put all the displays into an array

Serial myPort; //Serial object

int port = 0; //Port to use for the serial connection

int lb = 10; //Linebreak in ASCII (Look for a "\n")
String inString = null; //Incoming data

float xVal, yVal, zVal; //Angular position for all three axis <- turn into vector
float lastXVal, lastYVal, lastZVal; //Last reading of angular position for all three axis <- turn into vector
int xDiff, yDiff, zDiff; //Difference between the current and last readings from the accelerometer <- turn into vector

int mouseXPos, mouseYPos; //Mouse's x and y positions <- turn into vector

int captureWidth; //Width the screen capture will start with <- turn into vector
int captureHeight;//Height the screen capture will start with <- turn into vector

int outputWidth; //Width of the display <- turn into vector
int outputHeight; //Height of the display <- turn into vector

int customOutputWidth; //Custom width of the display <- turn into vector
int customOutputHeight; //Custom height of the display <- turn into vector
int customDisplay; //Custom display to use

int windowLocationX; //X position to start the fullscreen video <- turn into vector
int windowLocationY; //Y position to start the fullscreen video <- turn into vector

int horizontalDisplacement; //Spacing on the left/right of the images based on screen size <- turn into vector
int verticalDisplacement; //Spacing on the top/bottom of the images based on screen size <- turn into vector

int horizontalShift; //Spacing on the left/right of the images based on user's settings <- turn into vector
int verticalShift; //Spacing on the top/bottom of the images based on user's settings <- turn into vector

int horizontalZoom; //Horizontal size adjustment based on the user's settings <- turn into vector
int verticalZoom; //vertical size adjustment based on the user's settings <- turn into vector

boolean setLocation = true; //Decides whether or not to set the window's location at startup
boolean showVideo = false; //Shows the left/right image

boolean loadProfile; //Sets whether or not the user has pressed 'l' to load a profile
boolean saveProfile; //Sets whether or not the user has pressed 's' to save a profile

int selectedDisplay; //The display the user has chosen to use from the main menu
boolean displaySelected = false; //Has the user chosen a display to use from the main menu?

int mouseSensitivity; //Sensitivity for the accelerometer's mouse control

boolean mouseControl = false; //Whether or not the program has control of the mouse

PImage screenshot = new PImage(1, 1, RGB); //Screenshot image

PImage bufferImg; //Buffer image for the camera's capture

void settings() {
  size(400, 200);
}

void setup() {
  frameRate(60);
  smooth();

  //Load the properties file
  loadProperties();

  //Initiate the screenshot robot
  try {
    screenshotRobot = new Robot();
  }
  catch (Exception e) {
    println("Screenshot robot exception");
    e.printStackTrace();
  }

  //Initiate the mouse movement robot
  try {
    mouseRobot = new Robot();
  }
  catch (Exception e) {
    println("Mouse robot exception");
    e.printStackTrace();
  }

  //List all the available serial ports
  printArray(Serial.list());

  if (port < Serial.list().length) {
    myPort = new Serial(this, Serial.list()[port], 9600); //Open the port to be used at the specified baud rate
    myPort.clear(); //Empty the buffer and start fresh

    //Throw out the first reading, in case it's only a fragment of the data
    myPort.bufferUntil(lb);
    inString = null;
  }
}

void draw() {
  if (showVideo == true) {
    background(200);
  } else {
    //Load the main menu
    mainMenu();
  }
}

void serialEvent(Serial p) {
  inString = (myPort.readString()); //Read the new line of data

  try {
    String[] splitData = split(inString, ','); //Parse the data (expects a format of X,Y,Z)

    //Save the last accelerometer readings
    lastXVal = xVal;
    lastYVal = yVal;
    lastZVal = zVal;

    //Save the current accelerometer readings
    xVal = (float(splitData[0]))*mouseSensitivity;
    yVal = (float(splitData[1]))*mouseSensitivity;
    zVal = (float(splitData[2]))*mouseSensitivity;

    //Calculate the difference in data
    xDiff = round(xVal - lastXVal);
    yDiff = round(yVal - lastYVal);
    zDiff = round(zVal - lastZVal);

    //Display the accelerometer's movement in the console
    //println(-xDiff + ", " + yDiff);
  } 
  catch (Exception e) {
    println("Serial exception");
    e.printStackTrace();
  }
}