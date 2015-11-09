void mainMenu() {
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

