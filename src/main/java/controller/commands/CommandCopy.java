package controller.commands;

import java.util.List;

import model.ICalendarManager;

/**
 * CommandCopy class represents a copy command. Can use this class to copy events to other
 * calendars.
 */
public class CommandCopy extends ICommand {

  String eventName;
  IDateTimeArguments onDateTime;
  IDateTimeArguments toDateTime;
  IDateTimeArguments betweenStart;
  IDateTimeArguments betweenEnd;
  String targetCal;

  private enum CopyType {
    COPY_EVENT,
    COPY_ALL_EVENT,
    COPY_ALL_EVENT_INTERVAL,
    ILLEGAL
  }

  CopyType type;

  /**
   * Constructor method for copy command.
   * @param inputs - The user inputs.
   */
  CommandCopy(List<String> inputs) {
    this.type = parseInput(inputs);

    switch (this.type) {
      case COPY_EVENT:
        this.eventName = inputs.get(2);
        this.onDateTime = new OnDT(inputs.get(4));
        this.targetCal = inputs.get(6);
        this.toDateTime = new OnDT(inputs.get(8));
        break;

      case COPY_ALL_EVENT:
        this.onDateTime = new OnDT(inputs.get(3));
        this.targetCal = inputs.get(5);
        this.toDateTime = new OnDT(inputs.get(7));
        break;

      case COPY_ALL_EVENT_INTERVAL:
        this.betweenStart = new OnDT(inputs.get(3));
        this.betweenEnd = new OnDT(inputs.get(5));
        this.targetCal = inputs.get(7);
        this.toDateTime = new OnDT(inputs.get(9));
        break;

      default:
        throw new IllegalArgumentException("Illegal copy command.");
    }
  }

  @Override
  void executeCommand(ICalendarManager calManager) {
    switch (this.type) {
      case COPY_EVENT:
        calManager.copyEvent(this.eventName, this.onDateTime.getDt(),
            this.targetCal, this.toDateTime.getDt());
        break;

      case COPY_ALL_EVENT:
        calManager.copyEventsOn(this.onDateTime.getD(), this.targetCal, this.toDateTime.getD());
        break;

      case COPY_ALL_EVENT_INTERVAL:
        calManager.copyEventsBetween(this.betweenStart.getD(),
            this.betweenEnd.getD(), this.targetCal, this.toDateTime.getD());
        break;

      default:
        break;
    }
  }

  /**
   * Method to parse the user input.
   * @param inputs - The user inputs.
   * @return - Returns the type of command.
   */
  private CopyType parseInput(List<String> inputs) {
    if (inputs.size() == 9) {
      if (inputs.get(1).equals("event") && inputs.get(3).equals("on") &&
          isValidDateTimeInput(inputs.get(4)) && inputs.get(5).equals("--target") &&
          inputs.get(7).equals("to") && isValidDateTimeInput(inputs.get(8))) {
        return CopyType.COPY_EVENT;
      }
    }
    else if (inputs.size() == 8) {
      if (inputs.get(1).equals("events") && inputs.get(2).equals("on")
          && isValidDateInput(inputs.get(3)) && inputs.get(4).equals("--target")
          && inputs.get(6).equals("to") && isValidDateInput(inputs.get(7))) {
        return CopyType.COPY_ALL_EVENT;
      }
    }
    else if (inputs.size() == 10) {
      if (inputs.get(1).equals("events") && inputs.get(2).equals("between")
          && isValidDateInput(inputs.get(3)) && inputs.get(4).equals("and")
          && isValidDateInput(inputs.get(5)) && inputs.get(6).equals("--target")
          && inputs.get(8).equals("to") && isValidDateInput(inputs.get(9)) ) {
        return CopyType.COPY_ALL_EVENT_INTERVAL;
      }
    }

    return CopyType.ILLEGAL;
  }
}
