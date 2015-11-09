void keyPressed() {
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

