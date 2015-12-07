//Load the selected profile
void loadProfile(int profileNumber) {
  //Load the lines from the profile
  String[] lines = loadStrings("profiles/profile" + profileNumber + ".ini");

  //Get the data from each line
  String[] lineOne = split(lines[0], ",");
  String[] lineTwo = split(lines[1], ",");
  String[] lineThree = split(lines[2], ",");

  //Save each value
  horizontalDisplacement = int(lineOne[0]);
  verticalDisplacement = int(lineOne[1]);
  horizontalShift = int(lineTwo[0]);
  verticalShift = int(lineTwo[1]);
  horizontalZoom = int(lineThree[0]);
  verticalZoom = int(lineThree[1]);
}

//Save the selected profile
void saveProfile(int profileNumber) {
  String[] changes = new String[3];

  //The three lines to be saved
  changes[0] = str(horizontalDisplacement) + "," + str(verticalDisplacement);
  changes[1] = str(horizontalShift) + "," + str(verticalShift);
  changes[2] = str(horizontalZoom) + "," + str(verticalZoom);

  //Write to the profile
  saveStrings("data/profiles/profile" + profileNumber + ".ini", changes);
}