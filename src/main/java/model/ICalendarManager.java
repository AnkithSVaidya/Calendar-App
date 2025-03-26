package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;

public interface ICalendarManager {

  boolean createCalendar(String name, String timezone);

  boolean editCalendar(String calendarName, String property, String newValue);

  boolean useCalendar(String name);

  ICalendar getCurrentCalendar();

  ICalendar getCalendar(String name);

  Collection<Calendar> getAllCalendars();

  boolean copyEvent(String eventName, LocalDateTime eventStart,
                             String targetCalendarName, LocalDateTime newTargetStart);

  boolean copyEventsOn(LocalDate sourceDate, String targetCalendarName, LocalDate targetDate);

  boolean copyEventsBetween(LocalDate sourceStartDate, LocalDate sourceEndDate,
                                     String targetCalendarName, LocalDate targetStartDate);

}
