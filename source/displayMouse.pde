void displayMouse() {
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
