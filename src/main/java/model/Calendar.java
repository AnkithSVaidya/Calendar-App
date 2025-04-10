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
  public void addEvent(AbstractEvent event, boolean autoDecline)
      throws IllegalStateException {
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
        throw new IllegalStateException("Event conflict detected");
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
  public void addRecurringEvent(RecurringEvent recurringEvent,
                                boolean autoDecline)
      throws IllegalStateException {
    List<Event> generatedEvents = recurringEvent.generateEvents();

    // First check for conflicts across all instances
    if (autoDecline) {
      for (Event event : generatedEvents) {
        for (Event existing : events) {
          if (existing.conflictsWith(event)) {
            throw new IllegalStateException("Recurring event conflicts with existing event: " +
                existing.getTitle() + " at " + existing.getStart());
          }
        }
      }
    }

    // If no conflicts, add all events
    for (Event event : generatedEvents) {
      // Use false for autoDecline since we've already checked for conflicts
      addEvent(event, false);
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
   * Imports events from a CSV file in Google Calendar format.
   *
   * @param filename the input file path
   * @return the number of events successfully imported
   * @throws IOException              if an I/O error occurs during reading
   * @throws IllegalArgumentException if the file format is invalid
   */
  @Override
  public int importFromCSV(String filename) throws IOException, IllegalArgumentException {
    List<String> lines = java.nio.file.Files.readAllLines(Paths.get(filename));

    // Verify CSV has header row
    if (lines.isEmpty()) {
      throw new IllegalArgumentException("CSV file is empty");
    }

    // Process header row to identify column indices
    String headerLine = lines.get(0);
    String[] headers = parseCSVLine(headerLine);

    // Map column indices
    int subjectIdx = findColumnIndex(headers, "Subject");
    int startDateIdx = findColumnIndex(headers, "Start Date");
    int startTimeIdx = findColumnIndex(headers, "Start Time");
    int endDateIdx = findColumnIndex(headers, "End Date");
    int endTimeIdx = findColumnIndex(headers, "End Time");
    int allDayIdx = findColumnIndex(headers, "All Day Event");
    int descriptionIdx = findColumnIndex(headers, "Description");
    int locationIdx = findColumnIndex(headers, "Location");
    int privateIdx = findColumnIndex(headers, "Private");

    // Verify essential columns exist
    if (subjectIdx == -1 || startDateIdx == -1) {
      throw new IllegalArgumentException("CSV file missing required columns (Subject, Start Date)");
    }

    // Skip header row and process data rows
    int importedCount = 0;

    for (int i = 1; i < lines.size(); i++) {
      String line = lines.get(i);
      if (line.trim().isEmpty()) {
        continue;
      }

      // Parse CSV line
      String[] fields = parseCSVLine(line);

      try {
        String title = getFieldValue(fields, subjectIdx, "");
        String startDateStr = getFieldValue(fields, startDateIdx, "");
        String startTimeStr = getFieldValue(fields, startTimeIdx, "00:00:00");
        String endDateStr = getFieldValue(fields, endDateIdx, startDateStr);
        String endTimeStr = getFieldValue(fields, endTimeIdx, "23:59:59");
        String allDayStr = getFieldValue(fields, allDayIdx, "FALSE");
        String description = getFieldValue(fields, descriptionIdx, "");
        String location = getFieldValue(fields, locationIdx, "");
        String privateStr = getFieldValue(fields, privateIdx, "FALSE");

        // Parse boolean fields
        boolean isAllDay = Boolean.parseBoolean(allDayStr.trim());
        boolean isPrivate = Boolean.parseBoolean(privateStr.trim());
        boolean isPublic = !isPrivate;

        // Parse date/time fields
        LocalDate startDate = parseDate(startDateStr);
        LocalTime startTime = parseTime(startTimeStr);
        LocalDateTime start = LocalDateTime.of(startDate, startTime);

        // Create appropriate event
        Event event;
        if (isAllDay) {
          event = new Event(title, start, description, location, isPublic);
        } else {
          LocalDate endDate = parseDate(endDateStr);
          LocalTime endTime = parseTime(endTimeStr);
          LocalDateTime end = LocalDateTime.of(endDate, endTime);

          event = new Event(title, start, end, description, location, isPublic);
        }

        addEvent(event, true);
        importedCount++;
      } catch (Exception e) {
        System.err.println("Error parsing event at line " + (i + 1) + ": " + e.getMessage());
      }
    }

    return importedCount;
  }

  /**
   * Finds the index of a column in the CSV header.
   *
   * @param headers    Array of header strings
   * @param columnName Name of column to find
   * @return Index of column or -1 if not found
   */
  private int findColumnIndex(String[] headers, String columnName) {
    for (int i = 0; i < headers.length; i++) {
      if (headers[i].trim().equalsIgnoreCase(columnName)) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Safely gets a field value from the fields array with bounds checking.
   *
   * @param fields       Array of field values
   * @param index        Index to retrieve
   * @param defaultValue Default value if index is out of bounds
   * @return Field value or default
   */
  private String getFieldValue(String[] fields, int index, String defaultValue) {
    if (index >= 0 && index < fields.length) {
      return fields[index];
    }
    return defaultValue;
  }

  /**
   * Parses a date string in various common formats.
   *
   * @param dateStr Date string to parse
   * @return LocalDate object
   */
  private LocalDate parseDate(String dateStr) {
    dateStr = dateStr.trim();
    try {
      // Try standard ISO format (yyyy-MM-dd)
      return LocalDate.parse(dateStr);
    } catch (Exception e) {
      try {
        // Try MM/dd/yyyy format
        String[] parts = dateStr.split("/");
        if (parts.length == 3) {
          int month = Integer.parseInt(parts[0]);
          int day = Integer.parseInt(parts[1]);
          int year = Integer.parseInt(parts[2]);
          return LocalDate.of(year, month, day);
        }
      } catch (Exception e2) {
        // Fall through to next attempt
      }

      try {
        // Try dd/MM/yyyy format
        String[] parts = dateStr.split("/");
        if (parts.length == 3) {
          int day = Integer.parseInt(parts[0]);
          int month = Integer.parseInt(parts[1]);
          int year = Integer.parseInt(parts[2]);
          return LocalDate.of(year, month, day);
        }
      } catch (Exception e3) {
        // Fall through to next attempt
      }

      throw new IllegalArgumentException("Unable to parse date: " + dateStr);
    }
  }

  /**
   * Parses a time string in various common formats.
   *
   * @param timeStr Time string to parse
   * @return LocalTime object
   */
  private LocalTime parseTime(String timeStr) {
    timeStr = timeStr.trim();
    try {
      // Try standard ISO format (HH:mm:ss)
      return LocalTime.parse(timeStr);
    } catch (Exception e) {
      try {
        // Try HH:mm format
        if (timeStr.matches("\\d{1,2}:\\d{2}")) {
          return LocalTime.parse(timeStr + ":00");
        }
      } catch (Exception e2) {
        // Fall through to next attempt
      }

      try {
        // Try 12-hour format with AM/PM
        if (timeStr.toLowerCase().contains("am") ||
            timeStr.toLowerCase().contains("pm")) {
          String[] parts = timeStr.split(" ");
          String time = parts[0];
          boolean isPM = parts[1].toLowerCase().contains("pm");

          String[] timeParts = time.split(":");
          int hour = Integer.parseInt(timeParts[0]);
          int minute = timeParts.length > 1 ? Integer.parseInt(timeParts[1]) : 0;
          int second = timeParts.length > 2 ? Integer.parseInt(timeParts[2]) : 0;

          if (isPM && hour < 12) {
            hour += 12;
          } else if (!isPM && hour == 12) {
            hour = 0;
          }

          return LocalTime.of(hour, minute, second);
        }
      } catch (Exception e3) {
        // Fall through to default
      }

      // Default to midnight
      return LocalTime.MIDNIGHT;
    }
  }


  /**
   * Parses a CSV line properly handling quoted fields.
   *
   * @param line The CSV line to parse
   * @return Array of field values
   */
  private String[] parseCSVLine(String line) {
    List<String> fields = new ArrayList<>();
    StringBuilder currentField = new StringBuilder();
    boolean inQuotes = false;

    for (char c : line.toCharArray()) {
      if (c == '\"') {
        inQuotes = !inQuotes;
      } else if (c == ',' && !inQuotes) {
        fields.add(currentField.toString());
        currentField.setLength(0);
      } else {
        currentField.append(c);
      }
    }

    // Add the last field
    fields.add(currentField.toString());

    return fields.toArray(new String[0]);

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

        String temp = newValue;

        // Handle start and end times for each recurring event.
        if (property.equals("start")) {
          LocalDateTime currentStartDT = event.getStart();
          LocalDate currentStartDate = currentStartDT.toLocalDate();

          LocalTime newTime = LocalTime.parse(temp);
          LocalDateTime newDateTime = LocalDateTime.of(currentStartDate, newTime);
          temp = newDateTime.toString();
        } else if (property.equals("end")) {
          LocalDateTime currentEndDT = event.getEnd();
          LocalDate currentEndDate = currentEndDT.toLocalDate();

          LocalTime newTime = LocalTime.parse(temp);
          LocalDateTime newDateTime = LocalDateTime.of(currentEndDate, newTime);
          temp = newDateTime.toString();
        }

        applyEdit(event, property, temp);
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
  public boolean editEvents(String property, String eventName,
                            LocalDateTime startTime, String newValue) {
    // edit events subject event2R from 2025-03-03T10:07 with newSubject
    boolean modified = false;
    LocalTime initialEventStart = startTime.toLocalTime();


    for (Event event : events) {
      LocalTime targetStart = event.getStart().toLocalTime();

      if (event.getTitle().equals(eventName) && targetStart.equals(initialEventStart)
          && startTime.isBefore(event.getStart()) || startTime.isEqual(event.getStart())) {

        String temp = newValue;

        // Handle start and end times for each recurring event.
        if (property.equals("start")) {
          LocalDateTime currentStartDT = event.getStart();
          LocalDate currentStartDate = currentStartDT.toLocalDate();

          LocalTime newTime = LocalTime.parse(temp);
          LocalDateTime newDateTime = LocalDateTime.of(currentStartDate, newTime);
          temp = newDateTime.toString();
        } else if (property.equals("end")) {
          LocalDateTime currentEndDT = event.getEnd();
          LocalDate currentEndDate = currentEndDT.toLocalDate();

          LocalTime newTime = LocalTime.parse(temp);
          LocalDateTime newDateTime = LocalDateTime.of(currentEndDate, newTime);
          temp = newDateTime.toString();
        }

        applyEdit(event, property, temp);
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
          // Check that the new start is before the end (if end exists)
          if (event.getEnd() != null && newStart.isAfter(event.getEnd())) {
            throw new IllegalArgumentException("Start time must be before end time");
          }
          event.setStart(newStart);
        } catch (IllegalArgumentException e) {
          throw e;
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
