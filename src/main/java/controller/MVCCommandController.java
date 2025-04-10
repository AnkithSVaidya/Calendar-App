package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import model.Calendar;
import model.Event;
import model.ICalendarManager;
import model.RecurringEvent;
import view.EventDetails;
import view.IView;

/**
 * Controller class for a calendar application with a model and view.
 */
public class MVCCommandController implements IController, ActionListener {
  private ICalendarManager model;
  private IView view;

  /**
   * Constructor method for controller. Will initialize default calendar.
   * @param model - Model of the application.
   * @param view - View of the application.
   */
  public MVCCommandController(ICalendarManager model, IView view) {
    this.model = model;
    this.view = view;

    // Initialize default calendar.
    try {
      model.createCalendar("default", ZoneId.systemDefault().toString());
      model.useCalendar("default");
    } catch (Exception e) {
      e.printStackTrace();
    }
    view.setCalendars(model.getAllCalendarsMap(), model.getCurrentCalendar().getName());
    view.setAllCalendarEvents(new HashMap<String, List<EventDetails>>());
  }

  @Override
  public void controllerGo() {
    this.view.setCommandButtonListener(this);
    this.view.makeVisible();
  }


  @Override
  public void actionPerformed(ActionEvent e) {
    String actionCommand = e.getActionCommand();
    System.out.println("Action command received: " + actionCommand);

    // Preliminary setup.
    String activeCal = view.getActiveCalendar();
    model.useCalendar(activeCal);
    LocalDate date = view.getActiveDate();
    String status;

    try {
      status = processCommand(actionCommand, date);

      if (status.equals("exit")) {
        System.exit(0);
      }
      else if (!status.equals("options")) {
        view.showSuccessMessage(status);
      }

    } catch (Exception ex) {
      view.showErrorMessage(ex.getMessage());
    }


    // Now update the view.
    try {
      Map<String, Calendar> calList = model.getAllCalendarsMap();
      Map<String, List<EventDetails>> eventDetailsMap = new HashMap<>();

      // For each calendar, get all events.
      for (Map.Entry<String, Calendar> entry : calList.entrySet()) {
        String calendarName = entry.getKey();
        Calendar calendar = entry.getValue();

        // For each event in that list, turn it into a EventDetail to send to view.
        List<EventDetails> eventDetailsList = new ArrayList<>();
        for (Event event : calendar.getAllEventsList()) {
          EventDetails details = parseEventToEventDetail(event);
          eventDetailsList.add(details);
        }

        // Add calendar name, then the event detail list to the new map.
        eventDetailsMap.put(calendarName, eventDetailsList);
      }

      view.setAllCalendarEvents(eventDetailsMap);
      view.setCalendars(model.getAllCalendarsMap(), model.getCurrentCalendar().getName());
      view.refresh();
    } catch (Exception ex) {
      view.showErrorMessage(ex.getMessage());
    }
  }

  /**
   * Method to process an action command.
   * @param actionCommand - Action command from the view.
   * @param date - Active date.
   * @return - Returns message on success or fail.
   * @throws IOException - IOException for file.
   */
  public String processCommand(String actionCommand, LocalDate date) throws IOException {
    String message = "";
    List<String> command = new ArrayList<>();

    // Export calendar.
    if ("Export Calendar".equals(actionCommand)) {
      String fileName = view.showExportPopup();
      if (fileName != null && !fileName.trim().isEmpty()) {
        // Add .csv extension if not already present
        if (!fileName.toLowerCase().endsWith(".csv")) {
          fileName = fileName + ".csv";
        }

        // Export the calendar
        String exportedFile = model.getCurrentCalendar().exportToCSV(fileName);

        message = "Calendar successfully exported to: " + exportedFile + ".csv";
      }
    }
    // Import calendar.
    else if ("Import Calendar".equals(actionCommand)) {
      File f = view.showImportPopup();
      if (f != null) {
        System.out.println("Importing from: " + f.getName());

        // Verify it's a CSV file.
        if (!f.getName().toLowerCase().endsWith(".csv")) {
          throw new IllegalArgumentException("File must be a CSV file");
        }

        // Import the calendar.
        int count = model.getCurrentCalendar().importFromCSV(f.getAbsolutePath());
        message = "Successfully imported " + count + " events.";
      }
    }
    // Create calendar.
    else if ("Create Calendar".equals(actionCommand)) {
      view.createCalendarPopup();
      command = view.getCalendarCommandList();

      if (!command.isEmpty() && "create_calendar".equals(command.get(0))) {
        String name = command.get(1);
        String timezone = command.get(2);
        model.createCalendar(name, timezone);
        message = "Calendar '" + name + "' created with timezone '" + timezone + "'.";
      }
    }
    else if ("Create Event".equals(actionCommand)) {
      view.showCreateEventPopup(date, this);
      command = view.getCalendarCommandList();

      // Assume command format: ["create_event", title, fromTime, toTime, dateString]
      if (!command.isEmpty() && "create_event".equals(command.get(0))) {
        LocalDateTime startDateTime = buildDateTimeFromString(command.get(4), command.get(2));
        LocalDateTime endDateTime = buildDateTimeFromString(command.get(4), command.get(3));
        boolean isPublic = Boolean.parseBoolean(command.get(7));

        Event event = new Event(command.get(1), startDateTime, endDateTime, command.get(5),
            command.get(6), isPublic);
        model.getCurrentCalendar().addEvent(event, true);

        message = "Creating Event " + command.get(1) + " on " + command.get(4) + " from "
            + command.get(2) + " to " + command.get(3);
      }
    }
    else if ("Create All Day Event".equals(actionCommand)) {
      view.showCreateAllDayEventPopup(date, this);
      command = view.getCalendarCommandList();

      if (!command.isEmpty() && "create_all_day_event".equals(command.get(0))) {
        // Assume command format: ["create_all_day_event", title, dateString, ...]
        LocalDate localDate = LocalDate.parse(command.get(2));
        LocalDateTime localDateTime = LocalDateTime.of(localDate, LocalTime.NOON);
        boolean isPublic = Boolean.parseBoolean(command.get(5));

        Event allDayEvent = new Event(command.get(1), localDateTime, command.get(3),
            command.get(4), isPublic);
        model.getCurrentCalendar().addEvent(allDayEvent, true);

        message = "Creating All Day Event " + command.get(1) +
            " on " + command.get(2);
      }
    }
    else if ("Create Recurring Event".equals(actionCommand)) {
      view.showRecurringEventPopup(date, this);
      command = view.getCalendarCommandList();

      if (!command.isEmpty() && "create_recurring_event".equals(command.get(0))) {
        // Assume command format: ["create_recurring_event", title, fromTime,
        // toTime, daysString, extraDateString]
        //  {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        List<String> selectedDaysList = new ArrayList<>(Arrays.asList(command.get(4).split(", ")));
        Set<DayOfWeek> recurrenceDays = new HashSet<>();
        for (String dayChar : selectedDaysList) {
          switch (dayChar) {
            case "Mon" : recurrenceDays.add(DayOfWeek.MONDAY);
              break;
            case "Tue" : recurrenceDays.add(DayOfWeek.TUESDAY);
              break;
            case "Wed" : recurrenceDays.add(DayOfWeek.WEDNESDAY);
              break;
            case "Thu" : recurrenceDays.add(DayOfWeek.THURSDAY);
              break;
            case "Fri" : recurrenceDays.add(DayOfWeek.FRIDAY);
              break;
            case "Sat" : recurrenceDays.add(DayOfWeek.SATURDAY);
              break;
            case "Sun" : recurrenceDays.add(DayOfWeek.SUNDAY);
              break;
            default:
              break;
          }
        }
        int num = Integer.parseInt(command.get(5));
        boolean isPublic = Boolean.parseBoolean(command.get(9));
        LocalDateTime startDateTimeRec = buildDateTimeFromString(command.get(6), command.get(2));
        LocalDateTime endDateTimeRec = buildDateTimeFromString(command.get(6), command.get(3));

        RecurringEvent recurringEvent = new RecurringEvent(command.get(1), startDateTimeRec,
            endDateTimeRec, command.get(7), command.get(8), isPublic, recurrenceDays, num);

        model.getCurrentCalendar().addRecurringEvent(recurringEvent, true);

        message = "Creating Recurring Event " + command.get(1) + " on " + command.get(4)
            + " over " + command.get(5) + " times.";
      }
    }
    else if ("Edit Event".equals(actionCommand)) {
      view.showEditEventPopup(date, this);
      command = view.getCalendarCommandList();
      if (!command.isEmpty() && "edit_event".equals(command.get(0))) {
        // Correct order: ["edit_event", eventName, property, fromISO, toISO, newValue]
        String eventName = command.get(1);
        String property = command.get(2).toLowerCase();

        LocalDateTime from = buildDateTimeFromString(command.get(6), command.get(3));
        LocalDateTime to = buildDateTimeFromString(command.get(6), command.get(4));
        String newValue = command.get(5);

        boolean ok = model.getCurrentCalendar().editEvent(property, eventName, from, to, newValue);

        message = "Edited event " + eventName + " Property: " + property + " to "
            + newValue + ".";
      }
    }
    else if ("Edit Events".equals(actionCommand)) {
      view.showEditRecurringEventPopup(date, this);
      command = view.getCalendarCommandList();
      if (!command.isEmpty() && "edit_recurring_events".equals(command.get(0))) {
        // command, name, property, new val, current date, start time.
        // String[] properties = {"subject", "description", "location", "start", "end", "ispublic"};
        String eventName = command.get(1);
        String property = command.get(2).toLowerCase();
        LocalDateTime dt = buildDateTimeFromString(command.get(4), command.get(5));

        String newValue = command.get(3);
        boolean ok = model.getCurrentCalendar().editEvents(property, eventName, dt, command.get(3));

        message = "Editing All Events " + eventName +
            " Property: " + property + " to value: "
                + newValue + " starting from " + command.get(4);
      }
    }
    else if ("Day Options".equals(actionCommand)) {
      view.showDayPopup(date, this);
      message = "options";
    }
    else if ("Exit Button".equals(actionCommand)) {
      message = "exit";
    }

    return message;
  }


  /**
   * Helper method to parse an event into an event detail for the view to use.
   * @param event - Event object.
   * @return - Returns an EventDetail.
   */
  private EventDetails parseEventToEventDetail(Event event) {
    LocalTime startDT = event.getStart().toLocalTime();
    LocalTime endDT = event.getEnd() != null ? event.getEnd().toLocalTime() : null;
    LocalDate d = event.getStart().toLocalDate();
    return new EventDetails(event.getTitle(), event.getDescription(), event.getLocation(),
        event.isPublic(), startDT, endDT, d);
  }

  /**
   * Helper method to parse a date and a time into a datetime.
   * @param date - String date.
   * @param time - String time.
   * @return - Returns a LocalDateTime object of the combined fields.
   * @throws DateTimeParseException - If invalid inputs, throw error.
   */
  private LocalDateTime buildDateTimeFromString(String date, String time)
          throws DateTimeParseException {
    try {
      LocalDate d = LocalDate.parse(date);
      LocalTime t = LocalTime.parse(time);
      return LocalDateTime.of(d, t);
    } catch (DateTimeParseException e) {
      throw new DateTimeParseException("Invalid time format", "", 0);
    }
  }
}
