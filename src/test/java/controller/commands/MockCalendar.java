package controller.commands;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import model.AbstractEvent;
import model.Event;
import model.ICalendar;
import model.RecurringEvent;

/**
 * Class to represent a mock calendar used for testing.
 */
public class MockCalendar implements ICalendar {
  AbstractEvent event;
  private StringBuilder log;

  String subject;
  String location;
  String desc;
  Boolean isPublic;

  /**
   * Constructor to build a mock calendar.
   * @param log - Log used in tests.
   * @param uniqueCode - Unique code.
   */
  public MockCalendar(StringBuilder log,int uniqueCode) {
    this.log = log;
  }

  @Override
  public void addEvent(AbstractEvent event, boolean autoDecline) {
    this.event = event;
    this.subject = event.getTitle();
    this.location = event.getLocation();
    this.desc = event.getDescription();
    this.isPublic = event.isPublic();
    log.append(this.subject);
    log.append(" ");
    log.append(this.location);
    log.append(" ");
    log.append(this.desc);
    log.append(" ");
    log.append(this.isPublic);
    log.append(" ");
    if (event.getEnd() == null) {
      log.append("AllDay");
    }
    else {
      log.append("AddEvent");
    }
  }

  @Override
  public void addRecurringEvent(RecurringEvent recurringEvent, boolean autoDecline) {
    List<Event> generatedEvents = recurringEvent.generateEvents();
    for (Event event : generatedEvents) {
      log.append(event.getTitle());
      log.append(" ");
    }
    log.append("addRecurringEvent");
    log.append(" ");
  }

  @Override
  public List<AbstractEvent> getEventsOnDate(LocalDate date) {
    log.append("getEventsOnDate");
    log.append(" ");
    return List.of();
  }

  @Override
  public List<AbstractEvent> getEventsBetween(LocalDateTime start, LocalDateTime end) {
    log.append("getEventsBetween");
    log.append(" ");
    return List.of();
  }

  @Override
  public boolean isBusyAt(LocalDateTime time) {
    log.append("isBusyAt");
    log.append(" ");
    return false;
  }

  @Override
  public String exportToCSV(String filename) throws IOException {
    log.append("Export");
    log.append(" ");
    return "exported";
  }

  @Override
  public int importFromCSV(String filename) throws IOException, IllegalArgumentException {
    log.append("Import");
    log.append(" ");
    return 1;
  }

  @Override
  public boolean editEvent(String property, String eventName, LocalDateTime startTime,
                           LocalDateTime endTime, String newValue) {
    editHelper(property, newValue);
    log.append("editEvent");
    log.append(" ");
    return true;
  }

  @Override
  public boolean editEvents(String property, String eventName, LocalDateTime startTime,
                            String newValue) {
    editHelper(property, newValue);
    log.append("editEvents");
    log.append(" ");
    return true;
  }

  @Override
  public boolean editAllEvents(String property, String eventName, String newValue) {
    editHelper(property, newValue);
    log.append("editAllEvents");
    log.append(" ");
    return true;
  }

  @Override
  public ZoneId getTimezone() {
    return null;
  }

  @Override
  public List<Event> getAllEventsList() {
    return List.of();
  }

  @Override
  public String getName() {
    return "";
  }

  protected void editHelper(String property, String newValue) {
    switch (property) {
      case "description":
        log.append(" ");
        log.append(newValue);
        break;

      case "subject":
        log.append(" ");
        log.append(newValue);
        break;

      case "location":
        log.append(" ");
        log.append(newValue);
        break;

      default:
        break;
    }
  }
}
