package controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Objects;
import java.util.Scanner;

import controller.commands.Command;
import model.ICalendarManager;

public class InteractiveController implements IController {
  final InputStream in;
  final PrintStream out;
  ICalendarManager calendarManager;

  public InteractiveController(InputStream in, PrintStream out, ICalendarManager calManager) {
    this.calendarManager = calManager;
    this.in = in;
    this.out = out;
  }

  @Override
  public void controllerGo() throws IOException {
    Objects.requireNonNull(this.calendarManager);

    System.out.println("Please enter a command.");

    Scanner scan = new Scanner(this.in);

    while (true) {
      String command = scan.nextLine();

      if (command.equals("q")) {
        System.out.println("Quitting program.");
        return;
      }

      try {
        Command c = new Command(command);

        c.execute(this.calendarManager);

        System.out.println("Please enter a command.");
      }
      catch (IllegalArgumentException e) {
        throw new IllegalArgumentException();
      }
    }
  }
}
