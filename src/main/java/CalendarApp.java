import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import controller.IController;
//import controller.Controller;
import controller.HeadlessController;
import controller.InteractiveController;
import controller.MVCCommandController;
import model.CalendarManager;
import model.ICalendarManager;
import view.CalendarView;
import view.IView;

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

    IController controller;
    ICalendarManager model = new CalendarManager();

    // Run interactive mode.
    if (parseInputArr.size() == 2) {
      if (parseInputArr.get(0).equals("--mode") && parseInputArr.get(1).equals("interactive")) {
        controller = new InteractiveController(System.in, System.out, model);
        controller.controllerGo();

      }
    }
    // Run headless mode with file.
    else if (parseInputArr.size() == 3) {
      File f = new File(parseInputArr.get(2));

      // Readable and appendable
      if (parseInputArr.get(0).equals("--mode") && parseInputArr.get(1).equals("headless")
          && f.exists() && !f.isDirectory()) {
        controller = new HeadlessController(System.in, System.out, model, f);
        controller.controllerGo();
      }
    }
    else if (parseInputArr.isEmpty()) {
      IView view = new CalendarView();
      controller = new MVCCommandController(model, view);
      controller.controllerGo();
    }
  }
}
