package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
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
import javax.swing.JOptionPane;

public class MVCCommandController implements IController, ActionListener {
  private ICalendarManager model;
  private IView view;

  public MVCCommandController(ICalendarManager model, IView view) {
    this.model = model;
    this.view = view;

    // Initialize default calendar.
    try {
      model.createCalendar("default", ZoneId.systemDefault().toString());
      model.useCalendar("default");
    } catch (Exception e) {
      e.printStackTrace();
      // If calendar creation fails, you could exit or show an error.
    }
    view.setCalendars(model.getAllCalendarsMap(), model.getCurrentCalendar().getName());
//    view.setActiveCalendarEvents(new ArrayList<>());

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
      System.out.println("Export Calendar action");
      // (Export logic here...)
    }
    else if ("Import Calendar".equals(actionCommand)) {
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
          JOptionPane.showMessageDialog(null,
              "Calendar '" + name + "' created with timezone '" + timezone + "'.",
              "Success", JOptionPane.INFORMATION_MESSAGE);
        }
      } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null,
            ex.getMessage(),
            "Error", JOptionPane.ERROR_MESSAGE);
      }
    }
    else if ("Create Event".equals(actionCommand)) {
      try {
        view.showCreateEventPopup(date, this);
        command = view.getCalendarCommandList();
        // Assume command format: ["create_event", title, fromTime, toTime, dateString]
        LocalDateTime startDateTime = buildDateTimeFromString(command.get(4), command.get(2));
        LocalDateTime endDateTime = buildDateTimeFromString(command.get(4), command.get(3));
        Event event = new Event(command.get(1), startDateTime, endDateTime, "desc", "loc", true);
        model.getCurrentCalendar().addEvent(event, true);
        System.out.println("Created event for date: " + date);
      } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null,
            "Error creating event: " + ex.getMessage(),
            "Error", JOptionPane.ERROR_MESSAGE);
      }
    }
    else if ("Create All Day Event".equals(actionCommand)) {
      try {
        view.showCreateAllDayEventPopup(date, this);
        command = view.getCalendarCommandList();
        // Assume command format: ["create_all_day_event", title, dateString, ...]
        LocalDate localDate = LocalDate.parse(command.get(2));
        LocalDateTime localDateTime = LocalDateTime.of(localDate, LocalTime.NOON);
        Event allDayEvent = new Event(command.get(1), localDateTime, "desc", "loc", true);
        model.getCurrentCalendar().addEvent(allDayEvent, true);
        System.out.println("Created all-day event.");
      } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null,
            "Error creating all-day event: " + ex.getMessage(),
            "Error", JOptionPane.ERROR_MESSAGE);
      }
    }
    else if ("Create Recurring Event".equals(actionCommand)) {
      try {
        view.showRecurringEventPopup(date, this);
        command = view.getCalendarCommandList();
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
        LocalDateTime startDateTimeRec = buildDateTimeFromString(command.get(6), command.get(2));
        LocalDateTime endDateTimeRec = buildDateTimeFromString(command.get(6), command.get(3));
        RecurringEvent recurringEvent = new RecurringEvent(command.get(1), startDateTimeRec,
            endDateTimeRec, "desc", "loc", true, recurrenceDays, 5);
        model.getCurrentCalendar().addRecurringEvent(recurringEvent, true);
        System.out.println("Created recurring event.");
      } catch (Exception ex) {
        ex.printStackTrace();
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
        System.out.println("Day options chosen.");
      } catch(Exception ex) {
        ex.printStackTrace();
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

//
//      List<Event> currentEventList = model.getCurrentCalendar().getAllEventsList();
//      List<EventDetails> eventDetailsList = new ArrayList<>();
//      for (Event event : currentEventList) {
//        eventDetailsList.add(parseEventToEventDetail(event));
//      }
//      view.setActiveCalendarEvents(eventDetailsList);

      view.setAllCalendarEvents(eventDetailsMap);
      view.setCalendars(model.getAllCalendarsMap(), model.getCurrentCalendar().getName());
      view.refresh();
    } catch(Exception ex) {
      ex.printStackTrace();
      System.err.println("Error updating view: " + ex.getMessage());
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

  private EventDetails parseEventToEventDetail(Event event) {
    LocalTime startDT = event.getStart().toLocalTime();
    LocalTime endDT = event.getEnd() != null ? event.getEnd().toLocalTime() : null;
    LocalDate d = event.getStart().toLocalDate();
    return new EventDetails(event.getTitle(), event.getDescription(), event.getLocation(),
        event.isPublic(), startDT, endDT, d);
  }

  private LocalDateTime buildDateTimeFromString(String date, String time) {
    LocalDate d = LocalDate.parse(date);
    LocalTime t = LocalTime.parse(time);
    return LocalDateTime.of(d, t);
  }
}
