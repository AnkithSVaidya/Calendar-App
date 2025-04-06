package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;

/**
 * ICalendarManager interface represents a Calendar manager. A calendar manager contains a list
 * of calendars and functionality to create, edit, and copy events to the calendars.
 */
public interface ICalendarManager {

  /**
   * Method to create a new calendar.
   * @param name - The name for the new calendar.
   * @param timezone - The timezone for the new calendar.
   * @return - Returns true on create success, false otherwise.
   */
  boolean createCalendar(String name, String timezone);

  /**
   * Method to edit a property in a calendar.
   * @param calendarName - The calendar to edit.
   * @param property - The property to edit.
   * @param newValue - The new value for the property.
   * @return - Returns true on success, false otherwise.
   */
  boolean editCalendar(String calendarName, String property, String newValue);

  /**
   * Method to swap between the current calendar in use.
   * @param name - The name of the calendar.
   * @return - Returns true on success, false otherwise.
   */
  boolean useCalendar(String name);

  /**
   * Gets the current active calendar.
   * @return - Returns the active calendar.
   */
  ICalendar getCurrentCalendar();

  /**
   * Method to get a specific calendar by name.
   * @param name - The name of the calendar.
   * @return - Returns the calendar wanted.
   */
  ICalendar getCalendar(String name);

  /**
   * Method to get all the calendars created.
   * @return - Returns a list of all the calendars in the manager.
   */
  Collection<Calendar> getAllCalendars();

  /**
   * Method to return the name/calendar map.
   * @return - Returns name as keys and calendar as values.
   */
  Map<String, Calendar> getAllCalendarsMap();

  /**
   * Method to copy a single event from the active calendar to another calendar.
   * @param eventName - The name of the event.
   * @param eventStart - The start of the event.
   * @param targetCalendarName - The copy destination calendar.
   * @param newTargetStart - The new start of the event.
   * @return - Returns true on completion, false otherwise.
   */
  boolean copyEvent(String eventName, LocalDateTime eventStart,
                             String targetCalendarName, LocalDateTime newTargetStart);

  /**
   * Method to copy all events on a date to a target calendar.
   * @param sourceDate - The date of the events.
   * @param targetCalendarName - The copy destination calendar.
   * @param targetDate - The new date.
   * @return - Returns true on success, false otherwise.
   */
  boolean copyEventsOn(LocalDate sourceDate, String targetCalendarName, LocalDate targetDate);

  /**
   * Method to copy all events in a date range.
   * @param sourceStartDate - Start date.
   * @param sourceEndDate - End date.
   * @param targetCalendarName - The copy destination calendar.
   * @param targetStartDate - The new date.
   * @return - Returns true on success, false otherwise.
   */
  boolean copyEventsBetween(LocalDate sourceStartDate, LocalDate sourceEndDate,
                                     String targetCalendarName, LocalDate targetStartDate);

}
