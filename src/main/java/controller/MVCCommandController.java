package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import model.Event;
import model.ICalendarManager;
import model.RecurringEvent;
import view.EventDetails;
import view.IButtonPopups;
import view.IView;


import javax.swing.JOptionPane;

public class MVCCommandController implements IController, ActionListener {

  private ICalendarManager model;
  private IView view;

  public MVCCommandController(ICalendarManager model, IView view) {
    this.model = model;
    this.view = view;

    // Initialize default calendar.
    model.createCalendar("default", ZoneId.systemDefault().toString());
    model.useCalendar("default");
    view.setCalendars(model.getAllCalendarsMap(), model.getCurrentCalendar().getName());
    List<EventDetails> eventDetailsList = new ArrayList<>();
    view.setActiveCalendarEvents(eventDetailsList);
  }

  @Override
  public void controllerGo() {
    this.view.setCommandButtonListener(this);
    this.view.makeVisible();
  }

  @Override
  public void actionPerformed(ActionEvent e) {

    IButtonPopups popup;
    // Retrieve active calendar.
    String activeCal = view.getActiveCalendar();
    model.useCalendar(activeCal);
    List<String> command;
    String actionCommand = e.getActionCommand();

    LocalDate date = view.getActiveDate();

    // Run behaviors based on action command.
    switch (actionCommand) {
      case "Export Calendar":
        System.out.println("export actoin");
        System.out.println(e.getActionCommand());

        break;

      case "Import Calendar":
        System.out.println("import action");
        break;

      case "Create Calendar":
        // pop up the dialog, user types name + tz.
        view.createCalendarPopup(this);
        List<String> cmd = view.getCalendarCommandList();

        // only proceed if they didn't hit “Cancel” or fail the popup’s own checks
        if (!cmd.isEmpty() && cmd.get(0).equals("create_calendar")) {
          String name     = cmd.get(1);
          String timezone = cmd.get(2);

          try {
            boolean created = model.createCalendar(name, timezone);
            if (created) {
              JOptionPane.showMessageDialog(
                  null,
                  "Calendar '" + name + "' created with timezone '" + timezone + "'.",
                  "Success",
                  JOptionPane.INFORMATION_MESSAGE
              );
            } else {
              // duplicate‐name case
              JOptionPane.showMessageDialog(
                  null,
                  "A calendar named '" + name + "' already exists.",
                  "Error",
                  JOptionPane.ERROR_MESSAGE
              );
            }
          } catch (IllegalArgumentException ex) {
            // invalid‐timezone case (thrown by your model)
            JOptionPane.showMessageDialog(
                null,
                ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
          }
        }
        break;

      case "Create Event":
        view.showCreateEventPopup(date, this);
        command = view.getCalendarCommandList();

        LocalDateTime startDateTime = buildDateTimeFromString(command.get(4), command.get(2));
        LocalDateTime endDateTime = buildDateTimeFromString(command.get(4), command.get(3));

        Event event = new Event(command.get(1), startDateTime, endDateTime, "desc", "loc", true);
        model.getCurrentCalendar().addEvent(event, true);

        System.out.println(command);
        System.out.println("create single event" + view.getActiveDate());
        break;

      case "Create All Day Event":
        view.showCreateAllDayEventPopup(date, this);
        command = view.getCalendarCommandList();

        LocalDate localDate = LocalDate.parse(command.get(2));
        LocalTime localTime = LocalTime.NOON;
        LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);

        Event allDayEvent = new Event(command.get(1), localDateTime, "desc", "loc", true);
        model.getCurrentCalendar().addEvent(allDayEvent, true);

        System.out.println(command);
        System.out.println("create all day event");
        break;

      case "Create Recurring Event":
        view.showRecurringEventPopup(date, this);
        command = view.getCalendarCommandList();

        // {"M", "T", "W", "R", "F", "S", "U"};
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

        LocalDateTime startDateTime1 = buildDateTimeFromString(command.get(6), command.get(2));
        LocalDateTime endDateTime1 = buildDateTimeFromString(command.get(6), command.get(3));

        RecurringEvent recurringEvent = new RecurringEvent(command.get(1), startDateTime1,
            endDateTime1, "desc", "loc", true, recurrenceDays, 5);

        model.getCurrentCalendar().addRecurringEvent(recurringEvent, true);

        // call model
        System.out.println(command);
        System.out.println("create recurring event");
        break;

      case "Edit Event":
        view.showEditEventPopup(date, this);
        command = view.getCalendarCommandList();


        // call model
        System.out.println(command);
        System.out.println("edit single event");
        break;

      case "Edit Events":
        view.showEditRecurringEventPopup(date, this);
        command = view.getCalendarCommandList();

//        model.getCurrentCalendar().editEvents(command.get(1));
        // call model
        System.out.println(command);
        System.out.println("edit recurring event");
        break;


      case "Day Options":
        view.showDayPopup(date, this);

        System.out.println("day options");
        break;

      case "Exit Button":
        System.exit(0);
        break;
    }

    List<Event> currentEventList = model.getCurrentCalendar().getAllEventsList();
    List<EventDetails> eventDetailsList = new ArrayList<>();

    for (Event event : currentEventList) {
        EventDetails details = parseEventToEventDetail(event);
        eventDetailsList.add(details);
    }

    // Update Active events and reset calendars.
    view.setActiveCalendarEvents(eventDetailsList);
    view.setCalendars(model.getAllCalendarsMap(), model.getCurrentCalendar().getName());
    view.refresh();
  }

  private EventDetails parseEventToEventDetail(Event event) {

    LocalTime startDT = event.getStart().toLocalTime();
    LocalTime endDT = event.getEnd().toLocalTime();
    LocalDate d = event.getStart().toLocalDate();

    return new EventDetails(event.getTitle(), event.getDescription(), event.getLocation(),
        event.isPublic(), startDT, endDT, d);
  }

  private LocalDateTime buildDateTimeFromString(String date, String time) {
    LocalDate d = LocalDate.parse(date);
    LocalTime startTime = LocalTime.parse(time);

    return LocalDateTime.of(d, startTime);
  }

}
