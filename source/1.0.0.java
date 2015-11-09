import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.awt.*; 
import javax.swing.JFrame; 
import processing.serial.*; 
import processing.video.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class screen_capture_v20 extends PApplet {


//Java libraries used to get some info like mouse position, change the window size and position, remove the frame, etc.


//Java library used to create the second window


 //Processing Serial library to read incomming data from the arduino

//Processing library to use video camera


//Classes to run the headset's different modes
ScreenCapture screenCapture;
VideoCapture videoCapture;
Sketch sketch;

//Capture object for taking video
Capture cam;

Robot mouseRobot; //Robot object for moving the mouse

//Objects to take the screenshots
Robot screenshotRobot; //Create the robot object to capture the screen
secondApplet appletTwo; //Create the second applet create a new window
Insets windowBorder; //Size of the border around the capture window

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
boolean writeToConsole; //Decides whether or not to write the program's speed to the console

boolean loadProfile; //Sets whether or not the user has pressed 'l' to load a profile
boolean saveProfile; //Sets whether or not the user has pressed 's' to save a profile

int selectedDisplay; //The display the user has chosen to use from the main menu
boolean displaySelected = false; //Has the user chosen a display to use from the main menu?

boolean displayMouseLocation; //Whether or not to display the mouse's location

boolean mouseControl = false; //Whether or not to be moving the user's mouse

int mouseSensitivity; //Sensitivity for the accelerometer's mouse control

int mode; //Mode for the headset to use

PImage screenshot = new PImage(1, 1, RGB); //Screenshot image

PImage bufferImg; //Buffer image for the camera's capture

boolean displaying = false;

public void setup() {
  size(400, 200);
  frameRate(60);
  smooth();

  //Load the properties file
  loadProperties();

  if (mode == 0) {
    //Initiate the screenshot robot
    try {
      screenshotRobot = new Robot();
    }
    catch (AWTException e) {
    }

    //Initiate the mouse movement robot
    try {
      mouseRobot = new Robot();
    }
    catch (AWTException e) {
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
  } else if (mode == 1) {

    //Get all the available cameras
    String[] cameras = Capture.list();

    //Initiate the camera if it exists
    if (cameras.length > 0) {
      //Initiate the camera
      cam = new Capture(this, 640, 480, 30);
    }
  }
}

public void draw() {
  if (showVideo == true) {
    //Add a black background
    if (mode == 1 || mode == 2) {
      background(0);
    }

    if (mode == 0) {
      if (screenCapture.available) {
        background(0); //Refresh the screen
        screenCapture.displayCapture(); //Display the newly captured frame
      }
      screenCapture.moveMouse(); //Move the user's mouse
    } else if (mode == 1) {
      videoCapture.run();
    } else if (mode == 2) {
      sketch.run();
    }

    //Show the mouse's location in the capture
    if (displayMouseLocation == true) {
      displayMouse();
    }

    //Show the program's speed
    if (writeToConsole == true) {
      displayFps();
    }
  } else {
    //Load the main menu
    mainMenu();
  }
}

public void serialEvent(Serial p) {
  inString = (myPort.readString()); //Read the new line of data

  try {
    String[] splitData = split(inString, ','); //Parse the data

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
    println(-xDiff + ", " + yDiff);
  } 
  catch (Exception e) {
    println("Caught Exception"); //Catch any errors in retrieving data, so the program doesn't crash
  }
}


class Sketch {
  //Variables
  int sketchWidth, sketchHeight; //<- turn into vector

  //Constructor
  Sketch() {
    //Setup the environment for users to code their sketch
    //***DO NOT REMOVE OR MODIFY THE FOLLOWING***
    //Change the window's size
    frame.setResizable(true);
    frame.setSize(outputWidth, outputHeight);

    //Remove the borders from the window
    frame.removeNotify();
    frame.setUndecorated(true);
    frame.addNotify();

    //Sets the location of the display window
    frame.setLocation(windowLocationX, windowLocationY);

    //Keep the display window always on top
    frame.setAlwaysOnTop (true);

    //Start displaying the sketch
    showVideo = true;

    sketchWidth = outputWidth/2 - (horizontalDisplacement*2) + horizontalZoom;
    sketchHeight = outputHeight - (verticalDisplacement*2) + verticalZoom;

    //Remove the cursor
    noCursor();
  }

  public void run() {
    //Translate the sketch so users can use the point (0, 0) as a reference
    pushMatrix();
    translate(0 + horizontalDisplacement + horizontalShift - (horizontalZoom/2), 0 + verticalDisplacement + verticalShift - (verticalZoom/2));

    sketchDraw(); //Execute the user's programmed sketch

    popMatrix();

    //Right image (A direct copy of the left image) ***DO NOT REMOVE! MUST BE RUN ABSOLUTELY LAST***
    copy(0 + horizontalDisplacement + horizontalShift - (horizontalZoom/2), 0 + verticalDisplacement + verticalShift - (verticalZoom/2), outputWidth/2 - (horizontalDisplacement*2) + horizontalZoom, outputHeight - (verticalDisplacement*2) + verticalZoom, outputWidth/2 + horizontalDisplacement - horizontalShift - (horizontalZoom/2), 0 + verticalDisplacement + verticalShift - (verticalZoom/2), outputWidth/2 - (horizontalDisplacement*2) + horizontalZoom, outputHeight - (verticalDisplacement*2) + verticalZoom);
  }

  //User's sketch setup()
  public void sketchSetup() {
  }

  //User's sketch draw()
  public void sketchDraw() {
    //Set the background
    sketchBackground(255, 0, 0);

    fill(0, 0, 255);
    rect(0, 0, 100, 100);

    //Display the cursor
    displayCursor();
  }

  //Extra functions for the user to call
  //Easier command to let users set the background of their sketch
  public void sketchBackground(int r, int g, int b) {
    fill(r, g, b);
    rect(0 + horizontalDisplacement + horizontalShift - (horizontalZoom/2), 0 + verticalDisplacement + verticalShift - (verticalZoom/2), outputWidth/2 - (horizontalDisplacement*2) + horizontalZoom, outputHeight - (verticalDisplacement*2) + verticalZoom);
    //rect(0, 0, sketchWidth, sketchHeight);
  }

  //Fucntion to correctly display the user's mouse
  public void displayCursor() {
    if (mouseX < width/2) {
      fill(255);
      ellipse(mouseX, mouseY, 5, 5);
      ellipse(mouseX + width/2, mouseY, 5, 5);
    }
  }
}

public void displayFps() {
  //Display processing's frameRate
  //println("Processing's framrate: " + frameRate);

  fill(0, 255, 0);
  textSize(12);
  text(round(frameRate), 10, height - 10);
}

public void displayMouse() {
  //Save the mouse's location
  mouseXPos = MouseInfo.getPointerInfo().getLocation().x;
  mouseYPos = MouseInfo.getPointerInfo().getLocation().y;

  //Make the positions positive if they are negative
  if (mouseXPos < 0) {
    mouseXPos = -mouseXPos;
    mouseYPos = -mouseYPos;
  }
  
  //Print the mouse's location
  println(mouseXPos + ", " + mouseYPos);
  
  //Chech if the mouse is inside the capture area
  if ((mouseXPos > appletTwo.getLocation().x) && (mouseYPos > appletTwo.getLocation().y)) {
    if ((mouseXPos < appletTwo.getLocation().x + appletTwo.getWidth()) && (mouseYPos < appletTwo.getLocation().y + appletTwo.getHeight())) {
      
      //Display the mouse's location
      fill(255, 0, 0);
      ellipse(mouseXPos/(captureWidth/screenshot.width), mouseYPos/(captureHeight/screenshot.height), 4, 4);
    }
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
    if (key == 'q' || key == 'Q') {
      horizontalZoom += -1;
    } else if (key == 'w' || key == 'W') {
      horizontalZoom += 1;
    } else if (key == 'z' || key == 'Z') {
      verticalZoom += -1;
    } else if (key == 'a' || key == 'A') {
      verticalZoom += 1;
    }

    //Shift the position of the images
    if (key == CODED) {
      if (keyCode == LEFT) {
        horizontalShift += -1;
      } else if (keyCode == RIGHT) {
        horizontalShift += 1;
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

  //Selecting the display's size for the custom setting
  String[] lineTwo = split(lines[1], "=");
  String[] dimensions = split(lineTwo[1], ",");

  //Save the screen size for later
  customOutputWidth = PApplet.parseInt(dimensions[0]);
  customOutputHeight = PApplet.parseInt(dimensions[1]);

  //Select the display to use for custom settings
  String[] lineThree = split(lines[2], "=");

  customDisplay = PApplet.parseInt(lineThree[1]);

  //Find whether or not to write the program's speed to the console
  String[] lineFour = split(lines[3], "=");

  //Set the boolean value
  if (lineFour[1].equals("true")) {
    writeToConsole = true;
  } else {
    writeToConsole = false;
  }

  //Find whether or not to display the mouse's location
  String[] lineFive = split(lines[4], "=");

  //Set the boolean value
  if (lineFive[1].equals("true")) {
    displayMouseLocation = true;
  } else {
    displayMouseLocation = false;
  }

  //Find what mode the user wants to use the headset in
  String[] lineSix = split(lines[5], "=");
  mouseSensitivity = PApplet.parseInt(lineSix[1]);

  //Find what mode the user wants to use the headset in
  String[] lineSeven = split(lines[6], "=");
  mode = PApplet.parseInt(lineSeven[4]);
}

public void mainMenu() {
  //Add a green background
  background(30, 170, 25);

  //Start button
  if ((mouseX > width/40) && (mouseX < width/40 + width/4) && (mouseY > height/20) && (mouseY < height/20 + height/4)) {
    fill(80); //Darker

    //Start the video if the user clicks the button
    if (mousePressed == true) {
      if (displaySelected == true) {
        //Initialize the specific video object
        if (mode == 0) {
          screenCapture = new ScreenCapture();
        } else if (mode == 1) {
          videoCapture = new VideoCapture();
        } else if (mode == 2) {
          sketch = new Sketch();
        }
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
  int rectYPos = 10;

  textSize((width*height)/4000);
  textAlign(LEFT);
  for (int d = 0; d < displays.length + 1; d++) {
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

          selectedDisplay = d;
          displaySelected = true;
        } else if (d == displays.length) {
          //Set the window location
          windowLocationX = displays[customDisplay].getDefaultConfiguration().getBounds().x;
          windowLocationY = 0;

          //Set the window size
          outputWidth = customOutputWidth;
          outputHeight = customOutputHeight;

          selectedDisplay = d;
          displaySelected = true;
        }
      }
    } else {
      if (displaySelected == true) {
        fill(80); //Darker
      } else {
        fill(120); //Lighter
      }

      if (selectedDisplay != d) {
        fill(120); //Lighter
      }
    }

    //Display the buttons and text
    rect((width/3) - 5, textYPos - 25, width - (width/3) - 10, 30, 25);

    fill(0);
    if (d < displays.length) {
      text("Display " + (d + 1) + ": " + displays[d].getDisplayMode().getWidth() + "x" + displays[d].getDisplayMode().getHeight(), width/3, textYPos - 3); //Show the text on screen
    } else {
      text("Custom: " + customOutputWidth + "x" + customOutputHeight, width/3, textYPos - 3); //Show the text on screen
    }

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
  int sketchWidth, sketchHeight; //<- turn into vector

  boolean available = false;
  //screenshotRobot.createScreenCapture(new Rectangle(appletTwo.getLocation().x + windowBorder.left, appletTwo.getLocation().y, captureWidth - windowBorder.left - windowBorder.right, captureHeight - windowBorder.bottom));

  PVector rectangle = new PVector(0, 0);
  PVector rectangleSize = new PVector(0, 0);

  Rectangle rect;

  //Constructor
  ScreenCapture() {
    //Set the size for the images
    horizontalDisplacement = outputWidth/20;
    verticalDisplacement = outputHeight/20;

    //Change the window's size
    frame.setResizable(true);
    frame.setSize(outputWidth, outputHeight);

    //Remove the borders from the window
    frame.removeNotify();
    frame.setUndecorated(true);
    frame.addNotify();

    //Sets the location of the display window
    frame.setLocation(windowLocationX, windowLocationY);

    //Keep the display window always on top
    frame.setAlwaysOnTop (true);

    //Create a new window
    //captureArea = new JFrame("Capture Area");

    //Set the capture window's size
    //captureArea.setSize(captureWidth, captureHeight);

    //Keep the capture window always on top
    //captureArea.setAlwaysOnTop (true);

    //Make the window able to stop the program when closed
    //captureArea.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    //Make the capture window visible
    //captureArea.setVisible(true);

    //Create the second PFrame
    PFrame f = new PFrame();

    //Start displaying the capture
    showVideo = true;

    //Start the screenshot thread for the first time
    thread("threadTask");
  }

  //Functions
  //  void run() {
  //    takeScreenshot();
  //    displayCapture();
  //    moveMouse();
  //  }

  public void takeScreenshot() {
    //Update the capture size based on the capture window
    captureWidth = appletTwo.getWidth();
    captureHeight = appletTwo.getHeight();

    //Recreate the capture rectangle's location and size if either have changed
    if (appletTwo.getLocationOnScreen().y > 0) { //Only perform the following check if the capture window hasn't been minimized (Y position will be negative)
      if (rectangle.x != appletTwo.getLocationOnScreen().x || rectangle.y != appletTwo.getLocationOnScreen().y || rectangleSize.x != captureWidth || rectangleSize.y != captureHeight) {
        //Get the border size of the capture window
        windowBorder = appletTwo.getInsets();

        rect = new Rectangle(appletTwo.getLocationOnScreen().x + windowBorder.left, appletTwo.getLocationOnScreen().y, captureWidth - windowBorder.left - windowBorder.right, captureHeight - windowBorder.bottom);

        rectangle.set(appletTwo.getLocation().x, appletTwo.getLocation().y);
        rectangleSize.set(captureWidth, captureHeight);
      }
    }

    //Take a new screenshot
    //screenshot = new PImage(screenshotRobot.createScreenCapture (new Rectangle(appletTwo.getLocation().x + windowBorder.left, appletTwo.getLocation().y, captureWidth - windowBorder.left - windowBorder.right, captureHeight - windowBorder.bottom)));
    screenshot = new PImage(screenshotRobot.createScreenCapture(rect));

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
    image(screenshot, 0 + horizontalDisplacement + horizontalShift - (horizontalZoom/2), 0 + verticalDisplacement + verticalShift - (verticalZoom/2));

    //Right image
    image(screenshot, outputWidth/2 + horizontalDisplacement - horizontalShift - (horizontalZoom/2), 0 + verticalDisplacement + verticalShift - (verticalZoom/2));

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


public class PFrame extends JFrame {
  public PFrame() {
    //this.setBounds(width, height, captureWidth, captureHeight); //Xpos, yPos, width, height
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    //this.setAlwaysOnTop(true);
    this.setSize(500, 500);
    this.setLocation(displayWidth/2 - captureWidth/2, displayHeight/2 - captureHeight/2);

    this.setTitle("Capture Area");

    appletTwo = new secondApplet(); 
    add(appletTwo);
    appletTwo.init();
    show();
  }
}

public class secondApplet extends PApplet {
  public void setup() {
    //Size() not needed, as it's set when we create the class
    //size(500, 500);
  }
  public void draw() {
    //Set the window's title
    //this.setTitle("Capture area: " + appletTwo.getWidth() + "x" + appletTwo.getHeight() + "=" + appletTwo.getWidth() * appletTwo.getHeight() + " pixels, " + round(frameRate) + " fps.");
    //println(appletTwo.getLocationOnScreen().x + ", " + appletTwo.getLocationOnScreen().y);
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
      if (key == 'q' || key == 'Q') {
        horizontalZoom += -1;
      } else if (key == 'w' || key == 'W') {
        horizontalZoom += 1;
      } else if (key == 'z' || key == 'Z') {
        verticalZoom += -1;
      } else if (key == 'a' || key == 'A') {
        verticalZoom += 1;
      }

      //Shift the position of the images
      if (key == CODED) {
        if (keyCode == LEFT) {
          horizontalShift += -1;
        } else if (keyCode == RIGHT) {
          horizontalShift += 1;
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
}

public void threadTask() {
  screenCapture.takeScreenshot();
  //screenCapture.displayCapture();
}


class VideoCapture {
  //Variables
  int sketchWidth, sketchHeight; //<- turn into vector

  //Constructor
  VideoCapture() {
    //Set the size for the images
    horizontalDisplacement = outputWidth/20;
    verticalDisplacement = outputHeight/20;

    //Change the window's size
    frame.setResizable(true);
    frame.setSize(outputWidth, outputHeight);

    //Remove the borders from the window
    frame.removeNotify();
    frame.setUndecorated(true);
    frame.addNotify();

    //Sets the location of the display window
    frame.setLocation(windowLocationX, windowLocationY);

    //Keep the display window always on top
    frame.setAlwaysOnTop (true);

    //Initialize the buffer image to copy the video to
    bufferImg = createImage(outputWidth/2 - (horizontalDisplacement*2) + horizontalZoom, outputHeight - (verticalDisplacement*2) + verticalZoom, RGB);

    //bufferImg = new PImage(1, 1); //Camera's resized image

    //Start displaying the camera's video
    showVideo = true;

    //Start the camera's capture
    cam.start();
  }

  public void run() {
    getVideo();
    displayCamera();
  }

  //Functions
  public void getVideo() {
    if (cam.available() == true) {
      cam.read();
    }

    //Copy the video to a buffer image of the correct size
    bufferImg.copy(cam, 0, 0, cam.width, cam.height, 0, 0, outputWidth/2 - (horizontalDisplacement*2) + horizontalZoom, outputHeight - (verticalDisplacement*2) + verticalZoom);
  }

  public void displayCamera() {
    //Display the left and right images and add curves to the edges
    fill(0);
    noStroke();

    //Left image
    image(bufferImg, 0 + horizontalDisplacement + horizontalShift - (horizontalZoom/2), 0 + verticalDisplacement + verticalShift - (verticalZoom/2));

    //Right image
    image(bufferImg, outputWidth/2 + horizontalDisplacement - horizontalShift - (horizontalZoom/2), 0 + verticalDisplacement + verticalShift - (verticalZoom/2));
  }
}

  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "screen_capture_v20" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
