
public class SecondApplet extends PApplet {
  void settings() {
    fullScreen(selectedDisplay); //Start the applet fullscreen on the selected display
  }

  public void setup() {
  }

  public void draw() {
    if (screenCapture.available) {
      background(0); //Refresh the screen
      screenCapture.displayCapture(); //Display the newly captured frame
    }

    screenCapture.moveMouse(); //Move the user's mouse
  }
}