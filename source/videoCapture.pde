
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

  void run() {
    getVideo();
    displayCamera();
  }

  //Functions
  void getVideo() {
    if (cam.available() == true) {
      cam.read();
    }

    //Copy the video to a buffer image of the correct size
    bufferImg.copy(cam, 0, 0, cam.width, cam.height, 0, 0, outputWidth/2 - (horizontalDisplacement*2) + horizontalZoom, outputHeight - (verticalDisplacement*2) + verticalZoom);
  }

  void displayCamera() {
    //Display the left and right images and add curves to the edges
    fill(0);
    noStroke();

    //Left image
    image(bufferImg, 0 + horizontalDisplacement + horizontalShift - (horizontalZoom/2), 0 + verticalDisplacement + verticalShift - (verticalZoom/2));

    //Right image
    image(bufferImg, outputWidth/2 + horizontalDisplacement - horizontalShift - (horizontalZoom/2), 0 + verticalDisplacement + verticalShift - (verticalZoom/2));
  }
}

