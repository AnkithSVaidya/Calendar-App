package controller.commands;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import model.Calendar;
import model.ICalendar;
import model.ICalendarManager;

/**
 * Mock CalendarManager class used for testing.
 */
public class MockCalendarManager implements ICalendarManager {

  private StringBuilder log;
  private ICalendar mockCalendar;

  /**
   * Constructor method for a MockCalendarManager. Initializes the log and ICalendar.
   * @param log - Log to access in test class.
   * @param mockCalendar - Calendar Manager class. Either a mock or a regular one.
   */
  MockCalendarManager(StringBuilder log, ICalendar mockCalendar) {
    this.log = log;
    this.mockCalendar = mockCalendar;
  }

  @Override
  public boolean createCalendar(String name, String timezone) {
    log.append("createCalendar");
    log.append(" ");
    return true;
  }

  @Override
  public boolean editCalendar(String calendarName, String property, String newValue) {
    log.append("editCalendar");
    log.append(" ");
    return true;
  }

  @Override
  public boolean useCalendar(String name) {
    log.append("useCalendar");
    log.append(" ");
    return true;
  }

  @Override
  public ICalendar getCurrentCalendar() {
    log.append("getCurrentCalendar");
    log.append(" ");
    return this.mockCalendar;
  }

  @Override
  public ICalendar getCalendar(String name) {
    return null;
  }

  @Override
  public Collection<Calendar> getAllCalendars() {
    return List.of();
  }

  @Override
  public boolean copyEvent(String eventName, LocalDateTime eventStart,
                           String targetCalendarName, LocalDateTime newTargetStart) {
    log.append("copyEvent");
    log.append(" ");
    return true;
  }

  @Override
  public boolean copyEventsOn(LocalDate sourceDate,
                              String targetCalendarName, LocalDate targetDate) {
    log.append("copyEventsOn");
    log.append(" ");
    return true;
  }

  @Override
  public boolean copyEventsBetween(LocalDate sourceStartDate, LocalDate sourceEndDate,
                                   String targetCalendarName, LocalDate targetStartDate) {
    log.append("copyEventsBetween");
    log.append(" ");
    return true;
  }
}
