package controller;

import java.io.File;
import java.io.IOException;

import model.ICalendarManager;

/**
 * Main controller interface. Initializes two different go functions based on desired mode.
 */
public interface CalendarController {

  /**
   * Method for headless mode.
   * @param calendarManager - Calendar list from main.
   * @param txtFile - Commands text file.
   * @throws IOException - Throws exception if invalid command.
   */
  void controllerGo(ICalendarManager calendarManager, File txtFile) throws IOException;

  /**
   * Method for interactive move.
   * @param calendarManager - Calendar list from main.
   * @throws IOException - Throws exception if invalid command.
   */
  void controllerGo(ICalendarManager calendarManager) throws IOException;
}
