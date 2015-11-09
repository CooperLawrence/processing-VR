void displayFps() {
  //Display processing's frameRate
  //println("Processing's framrate: " + frameRate);

  fill(0, 255, 0);
  textSize(12);
  text(round(frameRate), 10, height - 10);
}

