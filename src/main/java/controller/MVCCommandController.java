package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import controller.commands.Command;
import controller.commands.CommandCreate;
import controller.commands.ICommand;
import model.ICalendar;
import model.ICalendarManager;
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


  public String processCommand(String command) {
    StringBuilder output = new StringBuilder();
    Scanner s = new Scanner(command);

    System.out.println(command);
    Command cmd = null;
    String inputs;


    while (s.hasNext()) {
      String in = s.next();

      switch (in) {
        case "create_calendar":

          // create calendar --name cal1 --timezone America/New_York.
          inputs = "create calendar --name " + s.next() + " --timezone " + s.next();
          cmd = new Command(inputs);

//          model.createCalendar(s.next(), s.next());
          System.out.println("create cal");

          break;

        case "use_calendar":
          System.out.println("using cal: " + s.next());
          inputs = "use calendar --name " + s.next();
          cmd = new Command(inputs);
//          model.useCalendar(s.next());
          break;
        case "edit_event":
//          cmd = new Turn(s.nextDouble());
          break;
        case "export_calendar":
//          cmd = new Square(s.nextDouble());
          System.out.println("exporting in controller");
          break;
//        case "koch":
//          cmd = new Koch(s.nextDouble(), s.nextInt());
//          break;
//        case "save":
//          cmd = new Save();
//          break;
//        case "retrieve":
//          cmd = new Retrieve();
//          break;
        default:
          System.out.println("unknown cmd ");
          output.append(String.format("Unknown command %s", in));
          cmd = null;
          break;
      }

      if (cmd != null) {
        System.out.println("execute");
        cmd.execute(this.model);
        output.append("Successfully executed: " + command);
      }
    }

    return output.toString();
  }

//  @Override
//  public void actionPerformed(ActionEvent e) {
//
//    System.out.println("action Performed.");
//
//    String command = view.getCalendarCommand();
//    String status;
//
//    try {
//      status = processCommand(command);
//    } catch (Exception ex) {
//      view.showErrorMessage(ex.getMessage());
//    }
//
//    view.setCalendars(model.getAllCalendarsMap(), model.getCurrentCalendar().getName());
//
//    view.refresh();
//  }

  @Override
  public void actionPerformed(ActionEvent e) {

    // Retrieve active calendar.
    String activeCal = view.getActiveCalendar();
    model.useCalendar(activeCal);
    List<String> command;

    switch (e.getActionCommand()) {
      //read from the input textfield
      case "Export Calendar":
        System.out.println("export actoin");
//        String text = view.getInputString();
//        //send text to the model
//        model.setString(text);
//
//        //clear input textfield
//        view.clearInputString();
//        //finally echo the string in view
//        text = model.getString();
//        view.setEchoOutput(text);

        break;

      case "Import Calendar":
        System.out.println("import action");
        break;

      case "Create Cal":
        command = view.getCalendarCommandList();

        model.createCalendar(command.get(1), command.get(2));

        System.out.println("create cal action");
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

      case "Exit Button":
        System.exit(0);
        break;
    }

    view.setCalendars(model.getAllCalendarsMap(), model.getCurrentCalendar().getName());
    view.refresh();
  }

}
