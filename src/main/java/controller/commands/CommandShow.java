package controller.commands;

import java.time.LocalDateTime;
import java.time.temporal.Temporal;
import java.util.List;

import model.ICalendar;
import model.ICalendarManager;

/**
 * Show command class. When called, this class will be used to show events.
 */
public class CommandShow extends ICommand {

  Temporal dt;

  /**
   * Constructor method for CommandShow.
   * @param inputs - User inputs.
   */
  CommandShow(List<String> inputs) {
    if (!isValidInput(inputs)) {
      throw new IllegalArgumentException("Illegal input for show command.");
    }

    this.dt = LocalDateTime.parse(inputs.get(3));
  }

  /**
   * Method to determine if a user is busy at the given time.
   * @param calendarManager - The calendar from main.
   */
  @Override
  void executeCommand(ICalendarManager calendarManager) {

    ICalendar cal = calendarManager.getCurrentCalendar();
    if (cal == null) {
      throw new IllegalStateException("No calendar found.");
    }

    if (cal.isBusyAt((LocalDateTime) this.dt)) {
      System.out.println("Busy.");
    }
    else {
      System.out.println("Available.");
    }
  }

  /**
   * Checks if show command has valid input.
   * Example: show status on dateStringTtimeString.
   * @return - Boolean for valid input.
   */
  private boolean isValidInput(List<String> inputs) {

    if (inputs.size() != 4) {
      return false;
    }

    return inputs.get(0).equals("show") && inputs.get(1).equals("status")
        && inputs.get(2).equals("on")
        && isValidDateTimeInput(inputs.get(3));
  }
}
