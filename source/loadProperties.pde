void loadProperties() {
  //Loading data file for initial properties
  String[] lines = loadStrings("data/properties.ini");

  //Selecting the starting capture size
  String[] lineOne = split(lines[0], "=");
  String[] captureDimensions = split(lineOne[1], ",");

  captureWidth = int(captureDimensions[0]);
  captureHeight = int(captureDimensions[1]);

  //Set the mouse sensitivity
  String[] lineTwo = split(lines[1], "=");
  mouseSensitivity = int(lineTwo[1]);
}