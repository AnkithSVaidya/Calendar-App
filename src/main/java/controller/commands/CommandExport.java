package controller.commands;

import java.io.IOException;
import java.util.List;

import model.ICalendar;
import model.ICalendarManager;

/**
 * Class for the Export command. Designed to take user input and execute a certain function.
 */
public class CommandExport extends ICommand {

  String fileName;

  /**
   * Constructor method to build an Export Command.
   * @param inputs - List of user inputs.
   */
  CommandExport(List<String> inputs) {

    if (!isInputValid(inputs)) {
      throw new IllegalArgumentException("Illegal input for export command.");
    }

    this.fileName = inputs.get(2);
  }

  /**
   * Function to export the calendar to csv.
   * @param calendarManager - calendar manager.
   */
  @Override
  public void executeCommand(ICalendarManager calendarManager) {

    ICalendar cal = calendarManager.getCurrentCalendar();
    if (cal == null) {
      throw new IllegalStateException("No calendar found.");
    }

    try {
      String fileName = cal.exportToCSV(this.fileName);
      System.out.println("Export Filename: " + fileName);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Method to validate the inputs for an Export command.
   * Sample command: export cal fileName.csv.
   * @param inputs - The user's inputs
   * @return - Boolean if valid or not.
   */
  private boolean isInputValid(List<String> inputs) {

    if (inputs.size() != 3) {
      return false;
    }

    return inputs.get(0).equals("export") && inputs.get(1).equals("cal")
        && inputs.get(2).endsWith(".csv");
  }

}
