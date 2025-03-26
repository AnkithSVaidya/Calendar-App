import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import controller.Controller;
import model.CalendarManager;

/**
 * Main class for the Calendar App. Run this class to start up the application.
 */
public class CalendarApp {

  /**
   * Main method for starting the application. Initializes the controller.
   * @param args - User input from when main method is called.
   */
  public static void main(String[] args) throws IOException {

    List<String> parseInputArr = new ArrayList<>(Arrays.asList(args));
    parseInputArr.replaceAll(String::toLowerCase);

    // Run interactive mode.
    if (parseInputArr.size() == 2) {
      if (parseInputArr.get(0).equals("--mode") && parseInputArr.get(1).equals("interactive")) {
        new Controller(System.in, System.out).controllerGo(new CalendarManager());
      }
    }
    // Run headless mode with file.
    else if (parseInputArr.size() == 3) {

      File f = new File(parseInputArr.get(2));

      if (parseInputArr.get(0).equals("--mode") && parseInputArr.get(1).equals("headless")
          && f.exists() && !f.isDirectory()) {
        new Controller(System.in, System.out).controllerGo(new CalendarManager(), f);
      }
    }
  }
}
