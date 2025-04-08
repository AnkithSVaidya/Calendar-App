package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import model.ICalendarManager;
import view.IButtonPopups;
import view.IView;

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



    switch (e.getActionCommand()) {
      //read from the input textfield
      case "Export Calendar":
        System.out.println("export actoin");
        System.out.println(e.getActionCommand());

        break;

      case "Import Calendar":
        System.out.println("import action");
        break;

      case "Create Calendar":
        view.createCalendarPopup(this);
        command = view.getCalendarCommandList();

        if (!command.get(0).equals("close")) {
          model.createCalendar(command.get(1), command.get(2));
          System.out.println("create cal action");
        }

        break;

      case "Create Event":
        command = view.getCalendarCommandList();

        System.out.println("create single event");
        break;

      case "Create All Day Event":
        command = view.getCalendarCommandList();

        System.out.println("create all day event");
        break;


      case "Create Recurring Event":
        command = view.getCalendarCommandList();
        System.out.println(command);

        System.out.println("create recurring event");
        break;

//      case "Day Button Clicked":
//        // This will be triggered when a day is clicked in the calendar
//        LocalDate date = (LocalDate) e.getSource();
//        view.showDayPopup(date, this);
//        break;
//
//      case "Create Event in Day":
//        // This will be triggered when "Create Event" is clicked in DayPopup
//        LocalDate eventDate = (LocalDate) e.getSource();
//        view.showCreateEventPopup(eventDate, this);
//        break;
//
//      case "Submit Event":
//        // This will be triggered when event details are submitted
//        command = view.getCalendarCommandList();
//        // Process the event creation command
//        System.out.println("Creating event with details: " + command);
//        // Add your event creation logic here
//        break;


      case "Show Event":
        System.out.println("show events");
        break;

      case "Exit Button":
        System.exit(0);
        break;
    }

    view.setCalendars(model.getAllCalendarsMap(), model.getCurrentCalendar().getName());
    view.refresh();
  }

}
