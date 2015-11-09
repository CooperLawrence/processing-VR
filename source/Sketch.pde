
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

  void run() {
    //Translate the sketch so users can use the point (0, 0) as a reference
    pushMatrix();
    translate(0 + horizontalDisplacement + horizontalShift - (horizontalZoom/2), 0 + verticalDisplacement + verticalShift - (verticalZoom/2));

    sketchDraw(); //Execute the user's programmed sketch

    popMatrix();

    //Right image (A direct copy of the left image) ***DO NOT REMOVE! MUST BE RUN ABSOLUTELY LAST***
    copy(0 + horizontalDisplacement + horizontalShift - (horizontalZoom/2), 0 + verticalDisplacement + verticalShift - (verticalZoom/2), outputWidth/2 - (horizontalDisplacement*2) + horizontalZoom, outputHeight - (verticalDisplacement*2) + verticalZoom, outputWidth/2 + horizontalDisplacement - horizontalShift - (horizontalZoom/2), 0 + verticalDisplacement + verticalShift - (verticalZoom/2), outputWidth/2 - (horizontalDisplacement*2) + horizontalZoom, outputHeight - (verticalDisplacement*2) + verticalZoom);
  }

  //User's sketch setup()
  void sketchSetup() {
  }

  //User's sketch draw()
  void sketchDraw() {
    //Set the background
    sketchBackground(255, 0, 0);

    fill(0, 0, 255);
    rect(0, 0, 100, 100);

    //Display the cursor
    displayCursor();
  }

  //Extra functions for the user to call
  //Easier command to let users set the background of their sketch
  void sketchBackground(color r, color g, color b) {
    fill(r, g, b);
    rect(0 + horizontalDisplacement + horizontalShift - (horizontalZoom/2), 0 + verticalDisplacement + verticalShift - (verticalZoom/2), outputWidth/2 - (horizontalDisplacement*2) + horizontalZoom, outputHeight - (verticalDisplacement*2) + verticalZoom);
    //rect(0, 0, sketchWidth, sketchHeight);
  }

  //Fucntion to correctly display the user's mouse
  void displayCursor() {
    if (mouseX < width/2) {
      fill(255);
      ellipse(mouseX, mouseY, 5, 5);
      ellipse(mouseX + width/2, mouseY, 5, 5);
    }
  }
}

