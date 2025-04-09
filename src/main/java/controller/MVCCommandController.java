package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
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

import javax.swing.*;

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
    List<String> command = new ArrayList<>();

    // Process based on the action command.
    if ("Export Calendar".equals(actionCommand)) {
      try {
        String fileName = view.showExportPopup();
        fileName = fileName + ".csv";
        model.getCurrentCalendar().exportToCSV(fileName);
        System.out.println("Export Filename: " + fileName);
      }
      catch (IOException err) {
        view.showErrorMessage(err.getMessage());
      }

      System.out.println("Export Calendar action");
      // (Export logic here...)
    }
    else if ("Import Calendar".equals(actionCommand)) {

      File f = view.showImportPopup(this);

      System.out.println(f.getName());

      System.out.println("Import Calendar action");
      // (Import logic here...)
    }
    else if ("Create Calendar".equals(actionCommand)) {
      try {
        view.createCalendarPopup(this);
        command = view.getCalendarCommandList();

        if (!command.isEmpty() && "create_calendar".equals(command.get(0))) {
          String name = command.get(1);
          String timezone = command.get(2);
          model.createCalendar(name, timezone);

          String message = "Calendar '" + name + "' created with timezone '" + timezone + "'.";

          view.showSuccessMessage(message);
        }
      } catch (Exception ex) {
        view.showErrorMessage(ex.getMessage());
      }
    }
    else if ("Create Event".equals(actionCommand)) {
      try {
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

          String message = "Creating Event " + command.get(1) + " on " + command.get(4) + " from "
              + command.get(2) + " to " + command.get(3);
          view.showSuccessMessage(message);
        }
      } catch (DateTimeException | IllegalStateException | IllegalArgumentException ex) {
        view.showErrorMessage(ex.getMessage());
      }
    }
    else if ("Create All Day Event".equals(actionCommand)) {
      try {
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

          String message = "Creating All Day Event " + command.get(1) +
              " on " + command.get(2);

          view.showSuccessMessage(message);
        }

      } catch (Exception ex) {
        view.showErrorMessage(ex.getMessage());
      }
    }
    else if ("Create Recurring Event".equals(actionCommand)) {
      try {
        view.showRecurringEventPopup(date, this);
        command = view.getCalendarCommandList();

        if (!command.isEmpty() && "create_recurring_event".equals(command.get(0))) {
          // Assume command format: ["create_recurring_event", title, fromTime, toTime, daysString, extraDateString]
          Set<DayOfWeek> recurrenceDays = new HashSet<>();
          for (char dayChar : command.get(4).toCharArray()) {
            switch (dayChar) {
              case 'M': recurrenceDays.add(DayOfWeek.MONDAY); break;
              case 'T': recurrenceDays.add(DayOfWeek.TUESDAY); break;
              case 'W': recurrenceDays.add(DayOfWeek.WEDNESDAY); break;
              case 'R': recurrenceDays.add(DayOfWeek.THURSDAY); break;
              case 'F': recurrenceDays.add(DayOfWeek.FRIDAY); break;
              case 'S': recurrenceDays.add(DayOfWeek.SATURDAY); break;
              case 'U': recurrenceDays.add(DayOfWeek.SUNDAY); break;
            }
          }
          int num = Integer.parseInt(command.get(5));
          boolean isPublic = Boolean.parseBoolean(command.get(9));
          LocalDateTime startDateTimeRec = buildDateTimeFromString(command.get(6), command.get(2));
          LocalDateTime endDateTimeRec = buildDateTimeFromString(command.get(6), command.get(3));

          RecurringEvent recurringEvent = new RecurringEvent(command.get(1), startDateTimeRec,
              endDateTimeRec, command.get(7), command.get(8), isPublic, recurrenceDays, num);

          try {
            model.getCurrentCalendar().addRecurringEvent(recurringEvent, true);
            JOptionPane.showMessageDialog(null, "Creating Recurring Event " + command.get(1) +
                " on " + command.get(4) + " over " + command.get(5) + " times.");
          } catch (IllegalStateException ex) {
            // This is likely a conflict error
            JOptionPane.showMessageDialog(null,
                "Cannot create recurring event: Conflicts with existing event. " + ex.getMessage(),
                "Scheduling Conflict", JOptionPane.ERROR_MESSAGE);
          }
        }
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(null,
            "Error creating recurring event: " + ex.getMessage(),
            "Error", JOptionPane.ERROR_MESSAGE);
      }
    }
    else if ("Edit Event".equals(actionCommand)) {
      try {
        view.showEditEventPopup(date, this);
        command = view.getCalendarCommandList();
        if (!command.isEmpty() && "edit_event".equals(command.get(0))) {
          // Correct order: ["edit_event", eventName, property, fromISO, toISO, newValue]
          String eventName = command.get(1);
          String property = command.get(2).toLowerCase();
          LocalDateTime from = LocalDateTime.parse(command.get(3));
          LocalDateTime to = LocalDateTime.parse(command.get(4));
          String newValue = command.get(5);

          if (!isValidProperty(property)) {
            throw new IllegalArgumentException("Invalid property: " + property);
          }
          boolean ok = model.getCurrentCalendar().editEvent(property, eventName, from, to, newValue);
          JOptionPane.showMessageDialog(null,
              ok ? "Changes applied to event." : "Failed to edit event.",
              ok ? "Success" : "Error",
              ok ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
        }
      } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null,
            "Error editing event: " + ex.getMessage(),
            "Error", JOptionPane.ERROR_MESSAGE);
      }
    }
    else if ("Edit Events".equals(actionCommand)) {
      try {
        view.showEditRecurringEventPopup(date, this);
        command = view.getCalendarCommandList();
        if (!command.isEmpty() && "edit_events".equals(command.get(0))) {
          // Expected command order: ["edit_events", property, eventName, fromISO, newValue]
          String property = command.get(1).toLowerCase();
          String eventName = command.get(2);
          LocalDateTime fromTime = LocalDateTime.parse(command.get(3));
          String newValue = command.get(4);
          if (!isValidProperty(property)) {
            throw new IllegalArgumentException("Invalid property: " + property);
          }
          boolean ok = model.getCurrentCalendar().editEvents(property, eventName, fromTime, newValue);
          JOptionPane.showMessageDialog(null,
              ok ? "Changes applied to all events." : "Failed to edit events.",
              ok ? "Success" : "Error",
              ok ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
        }
      } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null,
            "Error editing events: " + ex.getMessage(),
            "Error", JOptionPane.ERROR_MESSAGE);
      }
    }
    else if ("Day Options".equals(actionCommand)) {
      try {
        view.showDayPopup(date, this);
      } catch(Exception ex) {
        view.showErrorMessage(ex.getMessage());
      }
    }
    else if ("Exit Button".equals(actionCommand)) {
      System.exit(0);
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
    } catch(Exception ex) {
      view.showErrorMessage(ex.getMessage());
    }
  }

  private boolean isValidProperty(String property) {
    String[] validProperties = {"subject", "description", "location", "start", "end", "ispublic"};
    for (String valid : validProperties) {
      if (valid.equals(property)) {
        return true;
      }
    }
    return false;
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
  private LocalDateTime buildDateTimeFromString(String date, String time) throws DateTimeParseException {
    try {
      LocalDate d = LocalDate.parse(date);
      LocalTime t = LocalTime.parse(time);
      return LocalDateTime.of(d, t);
    } catch (DateTimeParseException e) {
      throw new DateTimeParseException("Invalid time format", "", 0);
    }
  }
}
