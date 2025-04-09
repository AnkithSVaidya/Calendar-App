package controller.commands;

import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import model.AbstractEvent;
import model.Event;
import model.ICalendar;
import model.ICalendarManager;
import model.RecurringEvent;

/**
 * This class represents a Create Command. It is designed to be executed based on user input to
 * create a new event(s) and add it to the calendar.
 */
public class CommandCreate extends ICommand {

  private boolean autoDecline = false;
  private String eventName;
  private IDateTimeArguments dt;
  Set<DayOfWeek> recurrenceDays = new HashSet<>();
  private int n;
  private IDateTimeArguments untilDate;

  private String description;
  private String location;
  private boolean isPublic;

  // Create calendar variables.
  private String calName;
  private ZoneId timezone;

  private enum EventType {
    SINGLE_EVENT,
    RECURRING_REPEAT_N_TIMES,
    RECURRING_UNTIL_DATE,
    SINGLE_ALL_DAY,
    RECURRING_ALL_DAY_REPEAT_N,
    RECURRING_ALL_DAY_UNTIL,
    CREATE_CAL
  }

  private EventType type;

  /**
   * Constructor method for Create Command.
   * @param inputs - User inputs from terminal.
   */
  CommandCreate(List<String> inputs) {

    if (inputs.size() < 5) {
      throw new IllegalArgumentException("Illegal input for create command.");
    }

    // Parse optional location, description, and public/private fields.
    for (int i = 0; i < inputs.size(); i++) {
      if (inputs.get(i).equals("--desc")) {
        String desc = inputs.get(i + 1);
        this.description = desc;

        inputs.remove(i + 1);
        inputs.remove(i);
        break;
      }
    }

    for (int i = 0; i < inputs.size(); i++) {
      if (inputs.get(i).equals("--location")) {
        String loc = inputs.get(i + 1);
        this.location = loc;

        inputs.remove(i + 1);
        inputs.remove(i);
        break;
      }
    }

    for (int i = 0; i < inputs.size(); i++) {
      if (inputs.get(i).equals("public")) {
        this.isPublic = true;
        inputs.remove(i);
        break;
      }
      else if (inputs.get(i).equals("private")) {
        this.isPublic = false;
        inputs.remove(i);
        break;
      }
    }

    if (!isInputValid(inputs)) {
      throw new IllegalArgumentException("Illegal input for create command.");
    }

    int arrSize = inputs.size();

    if (arrSize == 7) {
      this.eventName = inputs.get(2);
      this.dt = new FromDTToDT(inputs.get(4), inputs.get(6));
      this.type = EventType.SINGLE_EVENT;
    }
    else if (arrSize == 8) {
      this.autoDecline = true;
      this.eventName = inputs.get(3);
      this.dt = new FromDTToDT(inputs.get(5), inputs.get(7));
      this.type = EventType.SINGLE_EVENT;
    }
    else if (arrSize == 12) {
      if (inputs.get(arrSize - 1).equals("times")) {
        this.eventName = inputs.get(2);
        this.dt = new FromDTToDT(inputs.get(4), inputs.get(6));
        this.n = Integer.parseInt(inputs.get(10));
        this.type = EventType.RECURRING_REPEAT_N_TIMES;
      }
      else {
        this.eventName = inputs.get(3);
        this.dt = new FromDTToDT(inputs.get(5), inputs.get(7));
        this.untilDate = new OnDT(inputs.get(11));
        this.type = EventType.RECURRING_UNTIL_DATE;
      }
    }
    else if (arrSize == 13) {
      this.eventName = inputs.get(3);
      this.dt = new FromDTToDT(inputs.get(5), inputs.get(7));
      this.n = Integer.parseInt(inputs.get(11));
      this.type = EventType.RECURRING_REPEAT_N_TIMES;
    }
    else if (arrSize == 11) {
      this.eventName = inputs.get(2);
      this.dt = new FromDTToDT(inputs.get(4), inputs.get(6));
      this.untilDate = new OnDT(inputs.get(10));
      this.type = EventType.RECURRING_UNTIL_DATE;
    }
    else if (arrSize == 5) {
      this.eventName = inputs.get(2);
      this.dt = new OnDT(inputs.get(4));
      this.type = EventType.SINGLE_ALL_DAY;
    }
    else if (arrSize == 6) {

      if (inputs.get(1).equals("event")) {
        this.autoDecline = true;
        this.eventName = inputs.get(3);
        this.dt = new OnDT(inputs.get(5));
        this.type = EventType.SINGLE_ALL_DAY;
      }
      else if (inputs.get(1).equals("calendar")) {
        this.calName = inputs.get(3);
        this.timezone = ZoneId.of(inputs.get(5));
        this.type = EventType.CREATE_CAL;
      }

    }
    else if (arrSize == 10) {
      this.eventName = inputs.get(2);
      this.dt = new OnDT(inputs.get(4));
      this.n = Integer.parseInt(inputs.get(8));
      this.type = EventType.RECURRING_ALL_DAY_REPEAT_N;
    }
    else if (arrSize == 9) {
      this.eventName = inputs.get(2);
      this.dt = new OnDT(inputs.get(4));
      this.untilDate = new OnDT(inputs.get(8));
      this.type = EventType.RECURRING_ALL_DAY_UNTIL;
    }
  }

  /**
   * Method to execute a create command based on the user inputs.
   * @param calManager - Calendar from main app.
   */
  @Override
  public void executeCommand(ICalendarManager calManager) {
    List<LocalDateTime> range;
    AbstractEvent event = null;

    boolean success = true;
    // Create new event based on parameters.
    switch (this.type) {
      case SINGLE_EVENT:
        range = ((FromDTToDT) this.dt).getDateTimeRange();
        event = new Event(this.eventName, range.get(0), range.get(1),
            this.description, this.location, this.isPublic);

        break;

      case RECURRING_REPEAT_N_TIMES:
        range = ((FromDTToDT) this.dt).getDateTimeRange();

        event = new RecurringEvent(this.eventName, range.get(0), range.get(1), this.description,
            this.location, this.isPublic, this.recurrenceDays, this.n);
        break;

      case RECURRING_UNTIL_DATE:
        range = ((FromDTToDT) this.dt).getDateTimeRange();

        event = new RecurringEvent(this.eventName, range.get(0), range.get(1), this.description,
            this.location, this.isPublic, this.recurrenceDays, this.untilDate.getD());
        break;

      case SINGLE_ALL_DAY:
        event = new Event(this.eventName, this.dt.getDt(), this.description, this.location,
            this.isPublic);

        break;

      case RECURRING_ALL_DAY_REPEAT_N:
        event = new RecurringEvent(this.eventName, this.dt.getDt(), null, this.description,
            this.location, this.isPublic, this.recurrenceDays, this.n);

        break;

      case RECURRING_ALL_DAY_UNTIL:
        event = new RecurringEvent(this.eventName, this.dt.getDt(), null, this.description,
            this.location, this.isPublic, this.recurrenceDays, this.untilDate.getD());

        break;

      case CREATE_CAL:
        success = calManager.createCalendar(this.calName, this.timezone.toString());
        break;

      default:
        break;
    }

    if (!success) {
      System.out.println("Failed to create calendar");
      throw new IllegalStateException("Calendar not created due to name conflict");
    }

    ICalendar cal = calManager.getCurrentCalendar();

    if (cal == null) {
      throw new IllegalStateException("Calendar not created and/or set.");
    }

    try {
      if (this.type != EventType.CREATE_CAL) {
        // Add event to calendar.
        if (event instanceof Event) {
          cal.addEvent(event, true);
        }
        else {
          cal.addRecurringEvent((RecurringEvent) event, true);
        }
      }
    }
    catch (IllegalStateException e) {
      System.out.println(e.getMessage());
    }
  }

  /**
   * Method to check if the user input is valid or not.
   * @param inputs - User input from terminal.
   * @return - Boolean to verify.
   */
  private boolean isInputValid(List<String> inputs) {
    boolean valid = false;

    int arrSize = inputs.size();

    // create event [--autoDecline] <eventName> from
    // <dateStringTtimeString> to <dateStringTtimeString>
    if (arrSize == 7) {
      if (inputs.get(0).equals("create") && inputs.get(1).equals("event")
          && inputs.get(3).equals("from")
          && isValidDateTimeInput(inputs.get(4)) && inputs.get(5).equals("to")
          && isValidDateTimeInput(inputs.get(6))) {
        valid = true;
      }
    }
    // create event --autoDecline <eventName> from
    // <dateStringTtimeString> to <dateStringTtimeString> repeats <weekdays> for <N> times
    else if (arrSize == 12) {
      if (inputs.get(0).equals("create") && inputs.get(1).equals("event")
          && inputs.get(3).equals("from")
          && isValidDateTimeInput(inputs.get(4)) && inputs.get(5).equals("to")
          && isValidDateTimeInput(inputs.get(6)) && inputs.get(7).equals("repeats")
          && isValidWeekdayInput(inputs.get(8)) && (inputs.get(9).equals("for")
          && isValidN(inputs.get(10))
          && inputs.get(11).equals("times"))) {
        valid = true;
      }
    }
    else if (arrSize == 11) {
      if (inputs.get(0).equals("create") && inputs.get(1).equals("event")
          && inputs.get(3).equals("from")
          && isValidDateTimeInput(inputs.get(4)) && inputs.get(5).equals("to")
          && isValidDateTimeInput(inputs.get(6)) && inputs.get(7).equals("repeats")
          && isValidWeekdayInput(inputs.get(8)) && (inputs.get(9).equals("until")
          && isValidDateTimeInput(inputs.get(10)))) {
        valid = true;
      }
    }
    else if (arrSize == 5) {
      if (inputs.get(0).equals("create") && inputs.get(1).equals("event")
          && inputs.get(3).equals("on")
          && isValidDateTimeInput(inputs.get(4))) {
        valid = true;
      }
    }
    else if (arrSize == 6) {
      if (inputs.get(1).equals("calendar") && inputs.get(2).equals("--name")
          && inputs.get(4).equals("--timezone") && isValidTimezone(inputs.get(5))) {
        valid = true;
      }
    }
    else if (arrSize == 10) {
      if (inputs.get(0).equals("create") && inputs.get(1).equals("event")
          && inputs.get(3).equals("on")
          && isValidDateInput(inputs.get(4)) && inputs.get(5).equals("repeats")
          && isValidWeekdayInput(inputs.get(6)) && inputs.get(7).equals("for")
          && isValidN(inputs.get(8))
          && inputs.get(9).equals("times")) {
        valid = true;
      }
    }
    else if (arrSize == 9) {
      if (inputs.get(0).equals("create") && inputs.get(1).equals("event")
          && inputs.get(3).equals("on")
          && isValidDateInput(inputs.get(4)) && inputs.get(5).equals("repeats")
          && isValidWeekdayInput(inputs.get(6)) && inputs.get(7).equals("until")
          && isValidDateInput(inputs.get(8))) {
        valid = true;
      }
    }

    return valid;
  }

  /**
   * Helper method to check if a weekday input is valid. Also adds to map.
   * @param weekdays - Weekdays in MTWRFSU format.
   * @return = Boolean to verify.
   */
  private boolean isValidWeekdayInput(String weekdays) {

    for (char c : weekdays.toUpperCase().toCharArray()) {
      switch (c) {
        case 'M':
          recurrenceDays.add(DayOfWeek.MONDAY);
          break;
        case 'T':
          recurrenceDays.add(DayOfWeek.TUESDAY);
          break;
        case 'W':
          recurrenceDays.add(DayOfWeek.WEDNESDAY);
          break;

        case 'R':
          recurrenceDays.add(DayOfWeek.THURSDAY);
          break;

        case 'F':
          recurrenceDays.add(DayOfWeek.FRIDAY);
          break;
        case 'S':
          recurrenceDays.add(DayOfWeek.SATURDAY);
          break;
        case 'U':
          recurrenceDays.add(DayOfWeek.SUNDAY);
          break;
        default:
          throw new IllegalArgumentException("Invalid character for day of the week");
      }
    }
    return true;
  }

  /**
   * Helper method to check if N is a valid input.
   * @param inputN - Numerical string N from user input.
   * @return - Boolean to verify.
   */
  private boolean isValidN(String inputN) {

    try {
      int n = Integer.parseInt(inputN);
    }
    catch (NumberFormatException e) {
      throw new NumberFormatException("Illegal N value");
    }

    return true;
  }

  private boolean isValidTimezone(String inputTimezone) {
    try {
      ZoneId zone = ZoneId.of(inputTimezone);
      return true;
    }
    catch (DateTimeException e) {
      throw new DateTimeException("Invalid timezone");
    }
  }
}
