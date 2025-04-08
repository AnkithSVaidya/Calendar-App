package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import model.ICalendarManager;
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
        // pop up the dialog, user types name + tz
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

        //todo call model
        System.out.println(command);
        System.out.println("create single event" + view.getActiveDate());
        break;

      case "Create All Day Event":
        view.showCreateAllDayEventPopup(date, this);
        command = view.getCalendarCommandList();

        // todo: call model
        System.out.println(command);
        System.out.println("create all day event");
        break;

      case "Create Recurring Event":
        view.showRecurringEventPopup(date, this);
        command = view.getCalendarCommandList();

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

    view.setCalendars(model.getAllCalendarsMap(), model.getCurrentCalendar().getName());
    view.refresh();
  }

}
