package model;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages a collection of events and provides functionality to add, query,
 * edit, and export events.
 */
public class Calendar implements ICalendar {
  private String name;
  private ZoneId timezone;
  private List<Event> events;

  /**
   * Constructs a Calendar with the given unique name and timezone.
   *
   * @param name     unique calendar name
   * @param tzString timezone in IANA format (e.g., "America/New_York")
   */
  public Calendar(String name, String tzString) {
    this.name = name;
    this.timezone = ZoneId.of(tzString);
    events = new ArrayList<>();
  }

  /**
   * Overloaded no-argument constructor that provides default values.
   * This is used for backwards compatibility with existing code.
   */
  public Calendar() {
    this("Default", "America/New_York");
  }

  // --- Getters and setters for calendar properties ---

  public String getName() {
    return name;
  }

  public void setName(String newName) {
    this.name = newName;
  }

  public ZoneId getTimezone() {
    return timezone;
  }

  public void setTimezone(String tzString) {
    this.timezone = ZoneId.of(tzString);
  }

  public List<Event> getAllEventsList() {
    return this.events;
  }

  /**
   * Adds an event to the calendar, optionally auto-declining if conflicts exist.
   *
   * @param event       the event to add (must be a concrete {@link Event})
   * @param autoDecline if {@code true}, skips adding conflicting events
   */
  @Override
  public void addEvent(AbstractEvent event, boolean autoDecline) {
    if (event instanceof Event) {
      Event singleEvent = (Event) event;
      boolean conflictExists = false;
      for (Event existing : events) {
        if (existing.conflictsWith(singleEvent)) {
          conflictExists = true;
          break;
        }
      }
      // If a conflict exists and autoDecline is requested, do not add the event.
      if (conflictExists && autoDecline) {
        return;
      }
      // Otherwise, add the event (even if it conflicts).
      events.add(singleEvent);
    }
  }

  /**
   * Adds all generated instances of a recurring event to the calendar.
   *
   * @param recurringEvent the recurring event to expand
   * @param autoDecline    if {@code true}, skips adding conflicting instances
   */
  @Override
  public void addRecurringEvent(RecurringEvent recurringEvent, boolean autoDecline) {
    List<Event> generatedEvents = recurringEvent.generateEvents();
    for (Event event : generatedEvents) {
      addEvent(event, autoDecline);
    }
  }

  /**
   * Retrieves events occurring on a specific date, including multi-day events spanning the date.
   *
   * @param date the date to query
   * @return a list of events active on the specified date
   */
  @Override
  public List<AbstractEvent> getEventsOnDate(LocalDate date) {
    List<AbstractEvent> result = new ArrayList<>();
    for (Event event : events) {
      LocalDateTime eventEnd = event.getEnd()
          != null ? event.getEnd() : event.getStart().plusDays(1);
      if ((event.getStart().toLocalDate().equals(date)) ||
          (event.getStart().toLocalDate().isBefore(date)
              && eventEnd.toLocalDate().isAfter(date))) {
        result.add(event);
      }
    }
    return result;
  }

  /**
   * Retrieves events overlapping with a specified time range.
   *
   * @param start the start of the time range (inclusive)
   * @param end   the end of the time range (inclusive)
   * @return a list of events overlapping the range
   */
  @Override
  public List<AbstractEvent> getEventsBetween(LocalDateTime start, LocalDateTime end) {
    List<AbstractEvent> result = new ArrayList<>();
    for (Event event : events) {
      // Handle regular events with datetime range.
      if (event.getEnd() != null) {
        if (!event.getStart().isAfter(end) && !event.getEnd().isBefore(start)) {
          result.add(event);
        }
      }
      // Handle all day events.
      else {
        if (!event.getStart().isAfter(end) && !event.getStart().isBefore(start)) {
          result.add(event);
        }
      }
    }
    return result;
  }

  /**
   * Checks if the calendar has any event scheduled at a specific time.
   *
   * @param time the timestamp to check
   * @return {@code true} if an event exists at the specified time, {@code false} otherwise
   */
  @Override
  public boolean isBusyAt(LocalDateTime time) {
    for (Event event : events) {
      LocalDateTime eventEnd = event.getEnd()
          != null ? event.getEnd() : event.getStart().plusDays(1);
      if (!event.getStart().isAfter(time) && !eventEnd.isBefore(time)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Exports all events to a CSV file in the specified format.
   *
   * @param filename the output file path
   * @return the filename of the generated CSV
   * @throws IOException if an I/O error occurs during writing
   */
  @Override
  public String exportToCSV(String filename) throws IOException {
    StringBuilder csv = new StringBuilder("Subject, Start Date, Start Time, End Date, " +
        "End Time, All Day Event, Description, Location, Private\n");
    for (Event event : events) {
      boolean isAllDay = (event.getEnd() == null);
      LocalDate endDate = null;
      LocalTime endTime = null;
      LocalDateTime end = event.getEnd();
      if (end != null) {
        endDate = end.toLocalDate();
        endTime = end.toLocalTime();
      }
      csv.append(event.getTitle()).append(",")
          .append(event.getStart().toLocalDate()).append(",")
          .append(event.getStart().toLocalTime()).append(",")
          .append((endDate != null ? endDate : "")).append(",")
          .append((endTime != null ? endTime : "")).append(",")
          .append(isAllDay).append(",")
          .append(event.getDescription()).append(",")
          .append(event.getLocation()).append(",")
          .append(!event.isPublic()).append("\n");
    }
    try (FileWriter writer = new FileWriter(Paths.get(filename).toFile())) {
      writer.write(csv.toString());
    }
    return filename;
  }

  /**
   * Edits a specific event identified by its title, start time, and end time.
   *
   * @param property  the event property to modify ("subject", "description", or "location")
   * @param eventName the title of the event to edit
   * @param startTime the original start time of the event
   * @param endTime   the original end time of the event
   * @param newValue  the new value for the specified property
   * @return {@code true} if the event was found and modified, {@code false} otherwise
   */
  @Override
  public boolean editEvent(String property,
                           String eventName,
                           LocalDateTime startTime,
                           LocalDateTime endTime,
                           String newValue) {
    for (Event event : events) {
      if (event.getTitle().equals(eventName)
          && event.getStart().equals(startTime)
          && ((event.getEnd() == null && endTime == null) || (event.getEnd() != null
          && event.getEnd().equals(endTime)))) {
        applyEdit(event, property, newValue);
        return true;
      }
    }
    return false;
  }

  /**
   * Edits all events with a matching title and start time.
   *
   * @param property  the event property to modify
   * @param eventName the title of the events to edit
   * @param startTime the start time of the events to edit
   * @param newValue  the new value for the specified property
   * @return {@code true} if any events were modified
   */
  @Override
  public boolean editEvents(String property,
                            String eventName,
                            LocalDateTime startTime,
                            String newValue) {
    boolean modified = false;
    for (Event event : events) {
      if (event.getTitle().equals(eventName) && event.getStart().equals(startTime)) {
        applyEdit(event, property, newValue);
        modified = true;
      }
    }
    return modified;
  }

  /**
   * Edits all events with a matching title, regardless of time.
   *
   * @param property  the event property to modify
   * @param eventName the title of the events to edit
   * @param newValue  the new value for the property
   * @return {@code true} if any matching events were modified
   */
  @Override
  public boolean editAllEvents(String property, String eventName, String newValue) {
    boolean modified = false;
    for (Event event : events) {
      if (event.getTitle().equals(eventName)) {
        applyEdit(event, property, newValue);
        modified = true;
      }
    }
    return modified;
  }

  /**
   * Applies edits to an event based on the specified property.
   *
   * @param event    the event to modify
   * @param property the property to update ("subject", "description", or "location")
   * @param newValue the new value for the property
   * @throws IllegalArgumentException for invalid property names
   */
  /**
   * Applies edits to an event based on the specified property.
   *
   * @param event    the event to modify
   * @param property the property to update
   * @param newValue the new value for the property
   * @throws IllegalArgumentException for invalid property names or values
   */
  private void applyEdit(Event event, String property, String newValue) {
    switch (property.toLowerCase()) {
      case "subject":
        event.setTitle(newValue);
        break;
      case "description":
        event.setDescription(newValue);
        break;
      case "location":
        event.setLocation(newValue);
        break;
      case "start":
        try {
          LocalDateTime newStart = LocalDateTime.parse(newValue);
          // If this event has an end time, maintain the duration
          if (event.getEnd() != null) {
            long durationSeconds = java.time.Duration.between(
                event.getStart(), event.getEnd()).getSeconds();
            event.setStart(newStart);
            event.setEnd(newStart.plusSeconds(durationSeconds));
          } else {
            event.setStart(newStart);
          }
        } catch (Exception e) {
          throw new IllegalArgumentException("Invalid start time format: " + newValue);
        }
        break;
      case "end":
        try {
          LocalDateTime newEnd = LocalDateTime.parse(newValue);
          // Validate that end is after start
          if (newEnd.isBefore(event.getStart())) {
            throw new IllegalArgumentException("End time must be after start time");
          }
          event.setEnd(newEnd);
        } catch (IllegalArgumentException e) {
          throw e;
        } catch (Exception e) {
          throw new IllegalArgumentException("Invalid end time format: " + newValue);
        }
        break;
      case "ispublic":
        // Handle boolean property
        try {
          boolean isPublic = Boolean.parseBoolean(newValue);
          event.setPublic(isPublic);
        } catch (Exception e) {
          throw new IllegalArgumentException("Invalid boolean value for isPublic: " + newValue);
        }
        break;
      default:
        throw new IllegalArgumentException("Invalid property: " + property);
    }
  }
}
