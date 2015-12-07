
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

  void takeScreenshot() {
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

  void displayCapture() {
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