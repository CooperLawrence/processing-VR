
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

  void takeScreenshot() {
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

  void displayCapture() {
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

  void moveMouse() {
    //Move the mouse
    if (mouseControl == true) {
      //Move based on the difference between the current and last readings (Values must be added to the vhorizontal and vertical movements to account for the mouse being not perfectly centered)
      //mouseRobot.mouseMove((appletTwo.getLocationOnScreen().x + appletTwo.getWidth()/2) + (-zDiff), (appletTwo.getLocationOnScreen().y + appletTwo.getHeight()/2) + (yDiff));

      //Same as above, but gives the user full control of their mouse
      mouseRobot.mouseMove(MouseInfo.getPointerInfo().getLocation().x + (-zDiff), MouseInfo.getPointerInfo().getLocation().y + (-yDiff));
    }
  }
}

