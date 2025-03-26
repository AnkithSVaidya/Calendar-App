package controller.commands;

import java.time.LocalDateTime;
import java.util.List;

import model.AbstractEvent;
import model.ICalendar;
import model.ICalendarManager;

/**
 * This class represents a Print command. When called, it will print the desired outputs.
 */
public class CommandPrint extends ICommand {

  IDateTimeArguments dateTimeArguments;

  /**
   * Print method for printing out events. Will either print out event in date range or on a date.
   * Sample:
   *   print events on dateString
   *   print events from dateStringTtimeString to dateStringTtimeString
   * @param inputs - User inputs from terminal.
   */
  CommandPrint(List<String> inputs) {

    if (!isInputValid(inputs)) {
      throw new IllegalArgumentException("Illegal input for print command.");
    }

    if (inputs.size() == 4) {
      dateTimeArguments = new OnDT(inputs.get(3));
    }
    else if (inputs.size() == 6) {
      dateTimeArguments = new FromDTToDT(inputs.get(3), inputs.get(5));
    }
  }

  /**
   * Prints a bulleted list of all event(s).
   * @param calendarManager - Calendar manager from main function.
   */
  @Override
  public void executeCommand(ICalendarManager calendarManager) {

    ICalendar cal = calendarManager.getCurrentCalendar();
    if (cal == null) {
      throw new IllegalStateException("No calendar found.");
    }

    System.out.println("Printing event(s).");

    List<AbstractEvent> events = null;

    // Get either events in range or events on date.
    if (this.dateTimeArguments instanceof OnDT) {
      events = cal.getEventsOnDate(this.dateTimeArguments.getD());
    }
    else if (this.dateTimeArguments instanceof FromDTToDT) {
      List<LocalDateTime> range = ((FromDTToDT) this.dateTimeArguments).getDateTimeRange();
      events = cal.getEventsBetween(range.get(0), range.get(1));
    }

    // Print events.
    events.forEach(event -> {
      System.out.println("-" + event.toString());
    });
  }

  /**
   * Function to check for valid input for print.
   * Example: print events on dateString or
   * print events from dateStringTtimeString to dateStringTtimeString
   * @param inputs - Array of inputs.
   * @return - True if valid inputs, false if not.
   */
  private boolean isInputValid(List<String> inputs) {
    boolean valid = false;

    if (inputs.size() == 4) {
      if (inputs.get(0).equals("print") && inputs.get(1).equals("events")
          && inputs.get(2).equals("on")
          && isValidDateInput(inputs.get(3))) {
        valid = true;
      }
    }
    else if (inputs.size() == 6) {
      if (inputs.get(0).equals("print") && inputs.get(1).equals("events")
          && inputs.get(2).equals("from")
          && isValidDateTimeInput(inputs.get(3)) && inputs.get(4).equals("to")
          && isValidDateTimeInput(inputs.get(5))) {
        valid = true;
      }
    }

    return valid;
  }
}
