import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.awt.*; 
import processing.serial.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class screen_capture_v21 extends PApplet {


//Java libraries used to get some info like mouse position, change the window size and position, remove the frame, etc.


//Processing Serial library to read incomming data from the arduino


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

public void settings() {
  size(400, 200);
}

public void setup() {
  frameRate(60);
  

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

public void draw() {
  if (showVideo == true) {
    background(200);
  } else {
    //Load the main menu
    mainMenu();
  }
}

public void serialEvent(Serial p) {
  inString = (myPort.readString()); //Read the new line of data

  try {
    String[] splitData = split(inString, ','); //Parse the data (expects a format of X,Y,Z)

    //Save the last accelerometer readings
    lastXVal = xVal;
    lastYVal = yVal;
    lastZVal = zVal;

    //Save the current accelerometer readings
    xVal = (PApplet.parseFloat(splitData[0]))*mouseSensitivity;
    yVal = (PApplet.parseFloat(splitData[1]))*mouseSensitivity;
    zVal = (PApplet.parseFloat(splitData[2]))*mouseSensitivity;

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

public class SecondApplet extends PApplet {
  public void settings() {
    fullScreen(selectedDisplay); //Start the applet fullscreen on the selected display
  }

  public void setup() {
  }

  public void draw() {
    if (screenCapture.available) {
      background(0); //Refresh the screen
      screenCapture.displayCapture(); //Display the newly captured frame
    }

    screenCapture.moveMouse(); //Move the user's mouse
  }
}
public void keyPressed() {
  if (showVideo == true) {
    //Load profiles 1 through 3
    if (key == 'l' || key == 'L') {
      loadProfile = true;
    }

    if (loadProfile == true) {
      if (key == '1') {
        loadProfile(1);
        loadProfile = false;
        println("Loaded profile one.");
      } else if (key == '2') {
        loadProfile(2);
        loadProfile = false;
        println("Loaded profile two.");
      } else if (key == '3') {
        loadProfile(3);
        loadProfile = false;
        println("Loaded profile three.");
      }
    }

    //Save profiles 1 through 3
    if (key == 's' || key == 'S') {
      saveProfile = true;
    }

    if (saveProfile == true) {
      if (key == '1') {
        saveProfile(1);
        saveProfile = false;
        println("Saved settings to profile one.");
      } else if (key == '2') {
        saveProfile(2);
        saveProfile = false;
        println("Saved settings to profile two.");
      } else if (key == '3') {
        saveProfile(3);
        saveProfile = false;
        println("Saved settings to profile three.");
      }
    }

    //Zoom the images in and out
    if (key == 'w' || key == 'W') {
      horizontalZoom += -1;
    } else if (key == 'e' || key == 'E') {
      horizontalZoom += 1;
    } else if (key == 'a' || key == 'A') {
      verticalZoom += -1;
    } else if (key == 'q' || key == 'Q') {
      verticalZoom += 1;
    }

    //Shift the position of the images
    if (key == CODED) {
      if (keyCode == LEFT) {
        horizontalShift += 1;
      } else if (keyCode == RIGHT) {
        horizontalShift += -1;
      } else if (keyCode == UP) {
        verticalShift += -1;
      } else if (keyCode == DOWN) {
        verticalShift += 1;
      }
    }

    //Set the image size equal to the capture size
    if (key == 'r' || key == 'R') {
      //Not yet in use
    }

    //Allow the program to control the mouse's location
    if (key == 'v' || key == 'V') {
      mouseControl = !mouseControl;
    }

    //Re create the buffer image to copy the video to with updated parameters
    bufferImg = createImage(outputWidth/2 - (horizontalDisplacement*2) + horizontalZoom, outputHeight - (verticalDisplacement*2) + verticalZoom, RGB);
  }
}
public void loadProperties() {
  //Loading data file for initial properties
  String[] lines = loadStrings("data/properties.ini");

  //Selecting the starting capture size
  String[] lineOne = split(lines[0], "=");
  String[] captureDimensions = split(lineOne[1], ",");

  captureWidth = PApplet.parseInt(captureDimensions[0]);
  captureHeight = PApplet.parseInt(captureDimensions[1]);

  //Set the mouse sensitivity
  String[] lineTwo = split(lines[1], "=");
  mouseSensitivity = PApplet.parseInt(lineTwo[1]);
}
public void mainMenu() {
  //Add a nice green background
  background(30, 170, 25);

  //Start button
  if ((mouseX > width/40) && (mouseX < width/40 + width/4) && (mouseY > height/20) && (mouseY < height/20 + height/4)) {
    fill(80); //Darker

    //Start the video if the user clicks the button
    if (mousePressed == true) {
      if (displaySelected == true) {
        //Initialize the screen capture object
        screenCapture = new ScreenCapture();
      }
    }
  } else {
    fill(120); //Lighter
  }

  //Display the button and text
  rect(width/40, height/20, width/4, height/4, 25);
  fill(0);
  textSize((width*height)/2500);
  textAlign(CENTER);
  text("Start", width/40 + (width/4)/2, height/20 + (height/6));

  //Display the size of all displays attatched to the computer
  textAlign(LEFT);
  textSize((width*height)/3000);
  text("Available displays:", width/3, 26);

  //Available displays
  int textYPos = 60;

  textSize((width*height)/4000);
  textAlign(LEFT);
  for (int d = 0; d < displays.length; d++) {
    if ((mouseX > (width/3) - 5) && (mouseX < ((width/3) - 5) + width - (width/3) - 10 && (mouseY > textYPos - 25) && (mouseY < (textYPos - 25) + 30))) {
      fill(80); //Darker

      if (mousePressed == true) {
        if (d < displays.length) {
          //Set the window location
          windowLocationX = displays[d].getDefaultConfiguration().getBounds().x;
          windowLocationY = 0;

          //Set the window size
          outputWidth = displays[d].getDisplayMode().getWidth();
          outputHeight = displays[d].getDisplayMode().getHeight();

          selectedDisplay = d + 1;
          displaySelected = true;
        }
      }
    } else {
      if (displaySelected == true) {
        fill(80); //Darker
      } else {
        fill(120); //Lighter
      }

      if (selectedDisplay != d + 1) {
        fill(120); //Lighter
      }
    }

    //Display the buttons and text
    rect((width/3) - 5, textYPos - 25, width - (width/3) - 10, 30, 25);

    fill(0);
    text("Display " + (d + 1) + ": " + displays[d].getDisplayMode().getWidth() + "x" + displays[d].getDisplayMode().getHeight(), width/3, textYPos - 3); //Show the text on screen

    textYPos += 30;
  }
}
//Load the selected profile
public void loadProfile(int profileNumber) {
  //Load the lines from the profile
  String[] lines = loadStrings("profiles/profile" + profileNumber + ".ini");

  //Get the data from each line
  String[] lineOne = split(lines[0], ",");
  String[] lineTwo = split(lines[1], ",");
  String[] lineThree = split(lines[2], ",");

  //Save each value
  horizontalDisplacement = PApplet.parseInt(lineOne[0]);
  verticalDisplacement = PApplet.parseInt(lineOne[1]);
  horizontalShift = PApplet.parseInt(lineTwo[0]);
  verticalShift = PApplet.parseInt(lineTwo[1]);
  horizontalZoom = PApplet.parseInt(lineThree[0]);
  verticalZoom = PApplet.parseInt(lineThree[1]);
}

//Save the selected profile
public void saveProfile(int profileNumber) {
  String[] changes = new String[3];

  //The three lines to be saved
  changes[0] = str(horizontalDisplacement) + "," + str(verticalDisplacement);
  changes[1] = str(horizontalShift) + "," + str(verticalShift);
  changes[2] = str(horizontalZoom) + "," + str(verticalZoom);

  //Write to the profile
  saveStrings("data/profiles/profile" + profileNumber + ".ini", changes);
}

class ScreenCapture {
  //Variables
  boolean available = false; //True if a new frame is available to display
  //screenshotRobot.createScreenCapture(new Rectangle(appletTwo.getLocation().x + windowBorder.left, appletTwo.getLocation().y, captureWidth - windowBorder.left - windowBorder.right, captureHeight - windowBorder.bottom));

  PVector rectanglePosition = new PVector();
  PVector rectangleSize = new PVector();

  SecondApplet sa;
  Rectangle captureRect;

  //Constructor
  ScreenCapture() {
    //Set the size for the images
    horizontalDisplacement = outputWidth/20;
    verticalDisplacement = outputHeight/20;

    //Change the window's size
    surface.setResizable(true);
    surface.setSize(captureWidth, captureHeight);

    //Name of the second applet
    String[] args = {"Video"};

    //Create and start the new applet
    sa = new SecondApplet();
    PApplet.runSketch(args, sa);

    //Update the capture size based on the capture window
    captureWidth = width;
    captureHeight = height;

    //Start displaying the capture
    showVideo = true;

    //Start the screenshot thread for the first time
    thread("threadTask");
  }

  public void takeScreenshot() {
    //Stupid code required to retrieve frame information in Processing 3
    Frame[] frames = Frame.getFrames(); //Load all the running frames
    Frame frame = frames[1]; //Tell Processing which frame is the ACTUAL main frame
    //println(frame.getLocation());

    //Update the capture size based on the capture window
    Insets windowBorder = frame.getInsets();

    //Update the capture size based on the capture window
    captureWidth = width;
    captureHeight = height + windowBorder.top;

    //Recreate the capture rectangle's location and size if either have changed
    if (frame.getLocation().y >= 0) { //Only perform the following check if the capture window hasn't been minimized (Y position will be < 0)
      if (rectanglePosition.x != frame.getLocation().x || rectanglePosition.y != frame.getLocation().y || rectangleSize.x != captureWidth || rectangleSize.y != captureHeight) {
        captureRect = new Rectangle(frame.getLocation().x + windowBorder.left, frame.getLocation().y, captureWidth, captureHeight);

        rectanglePosition.set(frame.getLocation().x, frame.getLocation().y);
        rectangleSize.set(captureWidth, captureHeight);

        surface.setTitle(captureWidth + "x" + captureHeight + "=" + (captureWidth*captureHeight));
      }
    }

    try {
      screenshot = new PImage(screenshotRobot.createScreenCapture(captureRect));
    }
    catch (Exception e) {
      println("createScreenCapture exception");
      e.printStackTrace();
    }

    //Resize the screenshot
    screenshot.resize(outputWidth/2 - (horizontalDisplacement*2) + horizontalZoom, outputHeight - (verticalDisplacement*2) + verticalZoom);

    //Let the user know there is a new frame ready to view
    available = true;

    //Restart the thread 
    thread("threadTask");
  }

  public void displayCapture() {
    //Display the left and right images and add curves to the edges
    fill(0);
    noStroke();

    //Left image
    sa.image(screenshot, 0 + horizontalDisplacement + horizontalShift - (horizontalZoom/2), 0 + verticalDisplacement + verticalShift - (verticalZoom/2));

    //Right image
    sa.image(screenshot, outputWidth/2 + horizontalDisplacement - horizontalShift - (horizontalZoom/2), 0 + verticalDisplacement + verticalShift - (verticalZoom/2));

    //Let the user know there aren't any new frames ready to view
    available = false;
  }

  public void moveMouse() {
    //Move the mouse
    if (mouseControl == true) {
      //Move based on the difference between the current and last readings (Values must be added to the vhorizontal and vertical movements to account for the mouse being not perfectly centered)
      //mouseRobot.mouseMove((appletTwo.getLocationOnScreen().x + appletTwo.getWidth()/2) + (-zDiff), (appletTwo.getLocationOnScreen().y + appletTwo.getHeight()/2) + (yDiff));

      //Same as above, but gives the user full control of their mouse
      mouseRobot.mouseMove(MouseInfo.getPointerInfo().getLocation().x + (-zDiff), MouseInfo.getPointerInfo().getLocation().y + (-yDiff));
    }
  }
}
public void threadTask() {
  screenCapture.takeScreenshot();
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "screen_capture_v21" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
