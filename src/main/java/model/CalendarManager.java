package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages multiple calendars, ensuring unique names,
 * maintaining the current calendar context, and providing methods to
 * create, edit, and copy events across calendars.
 */
public class CalendarManager implements ICalendarManager {
  private Map<String, ICalendar> calendars;
  private ICalendar currentCalendar;

  /**
   * Constructs a new CalendarManager instance.
   * This constructor initializes the internal calendars map to an empty hashmap
   * and sets the currentCalendar to null.
   */
  public CalendarManager() {
    calendars = new HashMap<>();
    currentCalendar = null;
  }

  /**
   * Creates a new calendar with a unique name and specified timezone.
   *
   * @param name     unique name for the calendar
   * @param timezone IANA timezone string (e.g., "America/New_York")
   * @return true if calendar was created; false if name is not unique or timezone invalid.
   */
  public boolean createCalendar(String name, String timezone) {
    if (calendars.containsKey(name)) {
      return false; // Calendar name must be unique.
    }

    try {
      // Validate timezone.
      ZoneId.of(timezone);
    } catch (Exception e) {
      // Instead of throwing, return false to indicate failure
      throw new IllegalArgumentException("Wrong format or Wrong Timezone given, please enter the timezone in correct format"+e);
    }

    ICalendar cal = new Calendar(name, timezone);
    calendars.put(name, cal);

    if (this.currentCalendar == null) {
      this.currentCalendar = cal;
    }

    return true;
  }

  /**
   * Edits an existing calendar's property.
   *
   * @param calendarName the name of the calendar to edit
   * @param property     property to edit ("name" or "timezone")
   * @param newValue     new value for the property
   * @return true if the calendar was updated; false otherwise.
   */
  public boolean editCalendar(String calendarName, String property, String newValue) {
    ICalendar cal = calendars.get(calendarName);
    if (cal == null) {
      return false;
    }

    if (property.equalsIgnoreCase("name")) {
      // Check if new name is unique.
      if (calendars.containsKey(newValue)) {
        return false; // Don't throw exception, just return false
      }

      // Remove old entry, update, and reinsert.
      calendars.remove(calendarName);
      ((Calendar)cal).setName(newValue);
      calendars.put(newValue, cal);

      return true;
    } else if (property.equalsIgnoreCase("timezone")) {
      ZoneId newZoneId;
      try {
        newZoneId = ZoneId.of(newValue);
      } catch (Exception e) {
        return false; // Invalid timezone
      }

      ZoneId currZone = cal.getTimezone();

      ((Calendar)cal).setTimezone(newValue);

      // Swap all the current events datetime to the new timezone.
      List<Event> eventList = cal.getAllEventsList();

      for (Event e : eventList) {
        LocalDateTime newEventDateTime = e.getStart().atZone(currZone)
            .withZoneSameInstant(newZoneId)
            .toLocalDateTime();

        LocalDateTime newEnd = null;
        if (e.getEnd() != null) {
          long durationSeconds = java.time.Duration.between(e.getStart(),
              e.getEnd()).getSeconds();
          newEnd = newEventDateTime.plusSeconds(durationSeconds);
        }

        e.setStart(newEventDateTime);
        e.setEnd(newEnd);
      }

      return true;
    }
    return false;
  }

  /**
   * Sets the current calendar context.
   *
   * @param name name of the calendar to use
   * @return true if calendar exists and is set; false otherwise.
   */
  public boolean useCalendar(String name) {
    ICalendar cal = calendars.get(name);
    if (cal == null) {
      return false;
    }
    currentCalendar = cal;
    return true;
  }

  /**
   * Retrieves the current calendar.
   *
   * @return current Calendar instance or null if not set.
   */
  public ICalendar getCurrentCalendar() {
    return currentCalendar;
  }

  /**
   * Retrieves a calendar by name.
   *
   * @param name the calendar's name.
   * @return Calendar instance or null if not found.
   */
  public ICalendar getCalendar(String name) {
    return calendars.get(name);
  }

  /**
   * Returns all calendars.
   *
   * @return collection of all Calendar objects.
   */
  @SuppressWarnings("unchecked")
  public Collection<Calendar> getAllCalendars() {
    // Need to cast since we're storing ICalendars but interface requires Calendar
    return (Collection<Calendar>)(Collection<?>) calendars.values();
  }

  /**
   * Method to return the name/calendar map.
   *
   * @return - Returns name as keys and calendar as values.
   */
  @SuppressWarnings("unchecked")
  public Map<String, Calendar> getAllCalendarsMap() {
    // Need to cast since we're storing ICalendars but interface requires Calendar
    return (Map<String, Calendar>)(Map<?, ?>) calendars;
  }

  /**
   * Copies a single event identified by its name and start time from the current calendar
   * to the target calendar, placing it at the specified new start time.
   * The new event is created in the target calendar's timezone.
   *
   * @param eventName          the event title to copy.
   * @param eventStart         the start time of the event to copy in current calendar's local time.
   * @param targetCalendarName the name of the target calendar.
   * @param newTargetStart     the desired start time in the target calendar's local time.
   * @return true if event was successfully copied; false if event not found or conflict exists.
   */
  public boolean copyEvent(String eventName, LocalDateTime eventStart, String targetCalendarName,
                           LocalDateTime newTargetStart) {
    if (currentCalendar == null) {
      return false;
    }
    ICalendar targetCal = calendars.get(targetCalendarName);
    if (targetCal == null) {
      return false;
    }

    // Find the event in the current calendar.
    AbstractEvent toCopy = null;
    for (AbstractEvent event : currentCalendar.getEventsBetween(eventStart, eventStart)) {
      if (event.getTitle().equals(eventName) && event.getStart().equals(eventStart)) {
        toCopy = event;
        break;
      }
    }
    if (toCopy == null) {
      return false;
    }

    // Convert times if necessary.
    ZoneId sourceZone = currentCalendar.getTimezone();
    ZoneId targetZone = targetCal.getTimezone();

    // Calculate the difference between original event start and end (if end exists).
    LocalDateTime newStart = newTargetStart;

    LocalDateTime newEnd = null;
    if (toCopy.getEnd() != null) {
      long durationSeconds = java.time.Duration.between(toCopy.getStart(),
          toCopy.getEnd()).getSeconds();
      newEnd = newStart.plusSeconds(durationSeconds);
    }

    // Create a new event instance in target calendar.
    Event copiedEvent;
    if (newEnd == null) {
      copiedEvent = new Event(toCopy.getTitle(), newStart, toCopy.getDescription(),
          toCopy.getLocation(), toCopy.isPublic());
    } else {
      copiedEvent = new Event(toCopy.getTitle(), newStart, newEnd, toCopy.getDescription(),
          toCopy.getLocation(), toCopy.isPublic());
    }

    // Check for conflict in target calendar.
    if (targetCal.isBusyAt(newStart)) {
      return false;
    }
    targetCal.addEvent(copiedEvent, true);
    return true;
  }

  /**
   * Copies all events scheduled on a given date from the current calendar to the target calendar.
   * The times are adjusted to the target calendar's timezone.
   *
   * @param sourceDate         the date (in current calendar's local time) whose events
   *                           are to be copied.
   * @param targetCalendarName the target calendar name.
   * @param targetDate         the date (in target calendar's local time) that will correspond
   *                           to the source date.
   * @return true if at least one event was copied successfully; false otherwise.
   */
  public boolean copyEventsOn(LocalDate sourceDate, String targetCalendarName,
                              LocalDate targetDate) {
    if (currentCalendar == null) {
      return false;
    }
    ICalendar targetCal = calendars.get(targetCalendarName);
    if (targetCal == null) {
      return false;
    }
    boolean copiedAtLeastOne = false;
    for (AbstractEvent event : currentCalendar.getEventsOnDate(sourceDate)) {

      // Calculate the time difference between event start and the source date start.
      LocalDateTime eventStart = event.getStart();

      // Calculate the new time in the new timezone.
      ZoneId currZone = currentCalendar.getTimezone();
      ZoneId newZone = targetCal.getTimezone();

      LocalDateTime newEventDateTime = event.getStart().atZone(currZone)
          .withZoneSameInstant(newZone)
          .toLocalDateTime();

      // Add the time to the date.
      LocalDateTime newStart = LocalDateTime.of(targetDate, newEventDateTime.toLocalTime());

      boolean success = copyEvent(event.getTitle(), eventStart, targetCalendarName, newStart);
      if (success) {
        copiedAtLeastOne = true;
      }
    }
    return copiedAtLeastOne;
  }

  /**
   * Copies all events between two dates (inclusive) from the current calendar
   * to the target calendar.
   * The events are shifted so that the interval in the target calendar starts at targetStartDate.
   *
   * @param sourceStartDate    the start date of the source interval.
   * @param sourceEndDate      the end date of the source interval.
   * @param targetCalendarName the target calendar name.
   * @param targetStartDate    the date in the target calendar that corresponds
   *                           to the start of the interval.
   * @return true if at least one event was copied successfully; false otherwise.
   */
  public boolean copyEventsBetween(LocalDate sourceStartDate, LocalDate sourceEndDate,
                                   String targetCalendarName, LocalDate targetStartDate) {
    if (currentCalendar == null) {
      return false;
    }

    // Check if dates are out of order
    if (sourceStartDate.isAfter(sourceEndDate)) {
      return false; // Return false instead of throwing exception
    }

    ICalendar targetCal = calendars.get(targetCalendarName);
    if (targetCal == null) {
      return false;
    }

    boolean copiedAtLeastOne = false;
    // For simplicity, assume we copy each day individually.
    LocalDate date = sourceStartDate;
    while (!date.isAfter(sourceEndDate)) {
      boolean success = copyEventsOn(date, targetCalendarName,
          targetStartDate.plusDays(date.toEpochDay() - sourceStartDate.toEpochDay()));
      if (success) {
        copiedAtLeastOne = true;
      }
      date = date.plusDays(1);
    }
    return copiedAtLeastOne;
  }
}
