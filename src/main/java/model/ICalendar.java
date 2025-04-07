package model;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;


/**
 * Defines the contract for calendar implementations.
 * that manage events and recurring events.
 */
public interface ICalendar {
  /**
   * Adds a single event to the calendar with optional conflict checking.
   *
   * @param event       the event to add.
   * @param autoDecline if {@code true}, automatically
   *                    declines to add conflicting events.
   */
  void addEvent(AbstractEvent event, boolean autoDecline);

  /**
   * Expands and adds instances of a recurring event to the calendar.
   *
   * @param recurringEvent the recurring event pattern
   *                       to generate instances from.
   * @param autoDecline    if {@code true}, skips adding conflicting instances.
   */

  void addRecurringEvent(RecurringEvent recurringEvent, boolean autoDecline);

  /**
   * Retrieves events occurring on a specific date, including:
   * - Events starting on that date.
   * - All-day events for that date.
   * - Multi-day events spanning the date.
   *
   * @param date the target date to query.
   * @return list of active events for the specified date.
   */
  List<AbstractEvent> getEventsOnDate(LocalDate date);


  /**
   * Finds events overlapping with a time range (inclusive).
   *
   * @param start start of the time range (inclusive).
   * @param end   end of the time range (inclusive).
   * @return list of events overlapping the specified range.
   */
  List<AbstractEvent> getEventsBetween(LocalDateTime start, LocalDateTime end);


  /**
   * Checks for event conflicts at a specific moment.
   *
   * @param time the timestamp to check.
   * @return {@code true} if any event is scheduled during this time, {@code false} otherwise.
   */
  boolean isBusyAt(LocalDateTime time);

  /**
   * Exports calendar events to CSV format with columns:
   * Subject, Start Date, Start Time, End Date, End Time,
   * All Day Event, Description, Location, Private.
   *
   * @param filename output file path.
   * @return the provided filename.
   * @throws IOException if file writing fails.
   */
  String exportToCSV(String filename) throws IOException;


  /**
   * Edits a specific event identified by exact match of title, start time, and end time.
   *
   * @param property  field to modify (subject/description/location)
   * @param eventName title of event to edit
   * @param startTime original start time of event
   * @param endTime   original end time of event
   * @param newValue  new value for the specified property
   * @return {@code true} if event was found and modified
   */
  boolean editEvent(String property,
                    String eventName,
                    LocalDateTime startTime,
                    LocalDateTime endTime,
                    String newValue);


  /**
   * Edits all events matching both title and start time.
   *
   * @param property  field to modify
   * @param eventName title of events to edit
   * @param startTime start time filter for events
   * @param newValue  new value for the property
   * @return {@code true} if any matching events were modified
   */
  boolean editEvents(String property,
                     String eventName,
                     LocalDateTime startTime,
                     String newValue);


  /**
   * Edits all events with matching title, regardless of time.
   *
   * @param property  field to modify
   * @param eventName title of events to edit
   * @param newValue  new value for the property
   * @return {@code true} if any matching events were modified
   */
  boolean editAllEvents(String property, String eventName, String newValue);


  /**
   * Method to return the calendar timezone.
   * @return - Return zone id of timezone.
   */
  ZoneId getTimezone();

  /**
   * Method to return all events associated with the calendar.
   * @return - List of events.
   */
  List<Event> getAllEventsList();


  /**
   * Method to get the name of the calendar.
   * @return - Returns string name.
   */
  String getName();
}
