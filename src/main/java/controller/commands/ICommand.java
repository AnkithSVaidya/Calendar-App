package controller.commands;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.Temporal;

import model.ICalendarManager;

/**
 * Abstract class for Commands. This is the template for all commands in the project.
 */
public abstract class ICommand {

  /**
   * Method to execute a command. Will execute a command based on the type of class implementing it.
   * @param calManager - Calendar from main method.
   */
  abstract void executeCommand(ICalendarManager calManager);

  /**
   * Method to check if a user input date string is valid or not.
   * @param dateString - Date string in YYYY-MM-DD format.
   * @return - Validity.
   */
  boolean isValidDateInput(String dateString) {
    try {
      Temporal date = LocalDate.parse(dateString);
      return true;
    } catch (DateTimeParseException e) {
      return false;
    }
  }

  boolean isValidDateTimeInput(String dateTime) {
    try {
      Temporal dt = LocalDateTime.parse(dateTime);
      return true;
    } catch (DateTimeParseException e) {
      return false;
    }
  }
}
