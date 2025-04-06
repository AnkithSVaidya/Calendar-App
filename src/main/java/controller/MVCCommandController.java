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
    view.setCalendars(model.getAllCalendarsMap());
  }

  @Override
  public void controllerGo() {
    this.view.setCommandButtonListener(this);
    this.view.makeVisible();
  }


  public String processCommand(String command) {
    StringBuilder output = new StringBuilder();
    Scanner s = new Scanner(command);
    Command cmd = null;
    String inputs;


    while (s.hasNext()) {
      String in = s.next();

      switch (in) {
        case "create_calendar":

          // create calendar --name cal1 --timezone America/New_York.
          inputs = "create calendar --name " + s.next() + " --timezone " + s.next();
          cmd = new Command(inputs);
          break;

        case "create_event":
//          cmd = new Trace(s.nextDouble());
          break;
        case "edit_event":
//          cmd = new Turn(s.nextDouble());
          break;
//        case "square":
//          cmd = new Square(s.nextDouble());
//          break;
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
          output.append(String.format("Unknown command %s", in));
          cmd = null;
          break;
      }

      if (cmd != null) {
        cmd.execute(this.model);
        output.append("Successfully executed: " + command);
      }
    }


    return output.toString();
  }

  @Override
  public void actionPerformed(ActionEvent e) {

    System.out.println("action Performed.");

    String command = view.getCalendarCommand();
    String status;

    try {
      status = processCommand(command);
    } catch (Exception ex) {
      view.showErrorMessage(ex.getMessage());
    }

    view.setCalendars(model.getAllCalendarsMap());

    view.refresh();
  }

}
