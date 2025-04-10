package controller.commands;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import model.ICalendar;
import model.ICalendarManager;

/**
 * This class represents a Edit Command. It is desiggned to be executed based on user input onto
 * the calendar.
 */
public class CommandEdit extends ICommand {

  // Event
  private String property = "";
  private String newPropertyValue = "";
  private String eventName = "";
  private IDateTimeArguments dt = null;

  // Calendar
  private String calName;


  private enum Type {
    EDIT_ALL_EVENTS,
    EDIT_ALL_EVENTS_DT,
    EDIT_EVENT,
    EDIT_CAL,
    ILLEGAL
  }

  private enum PropertyType {
    SUBJECT,
    DESC,
    LOCATION,
    START,
    END,
    ISPUBLIC
  }

  private Type editType;

  /**
   * Constructor method for the Command Edit.
   * @param inputs - User inputs from terminal.
   */
  CommandEdit(List<String> inputs) {

    this.editType = isInputValid(inputs);

    switch (this.editType) {
      case EDIT_ALL_EVENTS:
        this.property = inputs.get(2);
        this.eventName = inputs.get(3);
        this.newPropertyValue = inputs.get(4);
        break;

      case EDIT_ALL_EVENTS_DT:
        this.property = inputs.get(2);
        this.eventName = inputs.get(3);
        this.dt = new FromDTToDT(inputs.get(5), LocalDateTime.MAX.toString());
        this.newPropertyValue = inputs.get(7);
        break;

      case EDIT_EVENT:
        this.property = inputs.get(2);
        this.eventName = inputs.get(3);
        this.dt = new FromDTToDT(inputs.get(5), inputs.get(7));
        this.newPropertyValue = inputs.get(9);
        break;

      case EDIT_CAL:
        this.property = inputs.get(5);
        this.newPropertyValue = inputs.get(6);
        this.calName = inputs.get(3);
        break;

      default:
        throw new IllegalArgumentException("Illegal Edit Command.");
    }

  }

  /**
   * Method to execute different command based on what kind of edit is input.
   * @param calManager - The initialized calendar.
   */
  @Override
  void executeCommand(ICalendarManager calManager) {

    ICalendar cal = calManager.getCurrentCalendar();
    if (cal == null) {
      throw new IllegalStateException("No calendar found.");
    }

    boolean success = true;
    switch (this.editType) {
      case EDIT_EVENT:
        List<LocalDateTime> range = ((FromDTToDT) this.dt).getDateTimeRange();
        success = cal.editEvent(this.property, this.eventName, range.get(0), range.get(1),
            this.newPropertyValue);
        break;

      case EDIT_ALL_EVENTS:
        success = cal.editAllEvents(this.property, this.eventName, this.newPropertyValue);
        break;

      case EDIT_ALL_EVENTS_DT:
        List<LocalDateTime> range2 = ((FromDTToDT) this.dt).getDateTimeRange();
        success = cal.editEvents(this.property, this.eventName, range2.get(0), this.newPropertyValue);
        break;

      case EDIT_CAL:
        success = calManager.editCalendar(this.calName, this.property, this.newPropertyValue);
        break;

      default:
        throw new IllegalArgumentException("Invalid edit type.");
    }

    if (!success) {
      throw new IllegalArgumentException("Failed to edit property.");
    }
  }

  /**
   * Method to check if user input is valid.
   * @param inputs - List of user inputs.
   * @return - Boolean verify.
   */
  private Type isInputValid(List<String> inputs) {

    // edit events <property> <eventName> <NewPropertyValue>
    if (inputs.size() == 5) {
      if (inputs.get(0).equals("edit") && inputs.get(1).equals("events")
          && isValidProperty(inputs.get(2))) {
        return Type.EDIT_ALL_EVENTS;
      }
    }
    // edit events <property> <eventName> from <dateStringTtimeString> with <NewPropertyValue>
    else if (inputs.size() == 8) {
      if (inputs.get(0).equals("edit") && inputs.get(1).equals("events")
          && isValidProperty(inputs.get(2))
          && inputs.get(4).equals("from") && isValidDateTimeInput(inputs.get(5))
          && inputs.get(6).equals("with")) {
        return Type.EDIT_ALL_EVENTS_DT;
      }
    }
    // edit event <property> <eventName> from <dateStringTtimeString> to
    // <dateStringTtimeString> with <NewPropertyValue>
    else if (inputs.size() == 10) {
      if (inputs.get(0).equals("edit") && inputs.get(1).equals("event")
          && isValidProperty(inputs.get(2))
          && inputs.get(4).equals("from") && isValidDateTimeInput(inputs.get(5))
          && inputs.get(6).equals("to")
          && isValidDateTimeInput(inputs.get(7)) && inputs.get(8).equals("with")) {
        return Type.EDIT_EVENT;
      }
    }
    else if (inputs.size() == 7) {
      if (inputs.get(1).equals("calendar") && inputs.get(2).equals("--name")
          && inputs.get(4).equals("--property") && isValidCalendarProperty(inputs.get(5))) {
        return Type.EDIT_CAL;
      }
    }

    return Type.ILLEGAL;
  }

  /**
   * Method to verify if a event property is valid.
   * @param inputProperty - User input of a property.
   * @return - Boolean verify.
   */
  private boolean isValidProperty(String inputProperty) {
    String[] validProperties = {"subject", "description" , "location", "start", "end", "ispublic"};
    List<String> validPropertyArr = Arrays.asList(validProperties);
    return validPropertyArr.contains(inputProperty);
  }

  /**
   * Method to verify if a calendar property is valid..
   * @param inputProperty - User input of a property.
   * @return - Boolean verify.
   */
  private boolean isValidCalendarProperty(String inputProperty) {
    String[] validProperties = {"name", "timezone"};
    List<String> validPropertyArr = Arrays.asList(validProperties);
    return validPropertyArr.contains(inputProperty);
  }
}
