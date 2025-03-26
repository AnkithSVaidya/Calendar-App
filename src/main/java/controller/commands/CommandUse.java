package controller.commands;

import java.util.List;

import model.ICalendarManager;

/**
 * Class for the use command. The use command is for selecting which calendar to use.
 */
public class CommandUse extends ICommand {
  String calName;

  private enum UseType {
    USE_CAL;
  }

  private UseType type;

  CommandUse(List<String> inputs) {

    parseInput(inputs);

    this.calName = inputs.get(3);
  }

  @Override
  void executeCommand(ICalendarManager calManager) {
    if (this.type == UseType.USE_CAL) {
      boolean exists = calManager.useCalendar(this.calName);

      if (!exists) {
        throw new IllegalArgumentException("The calendar does not exist.");
      }
    }
  }

  /**
   * Method to parse the user input.
   * @param inputs - The user inputs.
   */
  private void parseInput(List<String> inputs) {
    if (inputs.size() == 4) {
      if (inputs.get(1).equals("calendar") && inputs.get(2).equals("--name")) {
        this.type = UseType.USE_CAL;
      }
    }
    else {
      throw new IllegalArgumentException("Illegal argument in use command.");
    }
  }
}
