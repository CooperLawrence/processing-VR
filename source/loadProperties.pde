void loadProperties() {
  //Loading data file for initial properties
  String[] lines = loadStrings("data/properties.ini");

  //Selecting the starting capture size
  String[] lineOne = split(lines[0], "=");
  String[] captureDimensions = split(lineOne[1], ",");

  captureWidth = int(captureDimensions[0]);
  captureHeight = int(captureDimensions[1]);

  //Selecting the display's size for the custom setting
  String[] lineTwo = split(lines[1], "=");
  String[] dimensions = split(lineTwo[1], ",");

  //Save the screen size for later
  customOutputWidth = int(dimensions[0]);
  customOutputHeight = int(dimensions[1]);

  //Select the display to use for custom settings
  String[] lineThree = split(lines[2], "=");

  customDisplay = int(lineThree[1]);

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
  mouseSensitivity = int(lineSix[1]);

  //Find what mode the user wants to use the headset in
  String[] lineSeven = split(lines[6], "=");
  mode = int(lineSeven[4]);
}

