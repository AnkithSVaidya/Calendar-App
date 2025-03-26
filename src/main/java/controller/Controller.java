package controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Objects;
import java.util.Scanner;

import controller.commands.Command;
import model.ICalendar;
import model.ICalendarManager;

/**
 * Main controller class. Is responsible for I/O in the go method.
 */
public class Controller implements CalendarController {
  final InputStream in;
  final PrintStream out;
  ICalendar currentCalendar;

  /**
   * Constructor method for Controller.
   * @param in - User input (from terminal).
   * @param out - Print output (into terminal).
   */
  public Controller(InputStream in, PrintStream out) {
    this.in = in;
    this.out = out;
  }

  /**
   * Method to start the application. Asks the user to choose a mode, headless or interactive,
   * and then will ask the user to input commands or take commands from a file.
   * @param calendarManager - The calendar manager from the main method.
   */
  public void controllerGo(ICalendarManager calendarManager) throws IllegalArgumentException {


    Objects.requireNonNull(calendarManager);

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

        c.execute(calendarManager);

        System.out.println("Please enter a command.");
      }
      catch (IllegalArgumentException e) {
        throw new IllegalArgumentException();
      }
    }
  }

  /**
   * Go method for headless mode with a file.
   * @param calManager - Calendar manager object from main.
   * @param txt - File object in txt format.
   */
  public void controllerGo(ICalendarManager calManager, File txt) throws IOException {

    try (BufferedReader reader = new BufferedReader(new FileReader(txt))) {
      String line;
      while ((line = reader.readLine()) != null) {

        // Execute command.
        try {
          Command c = new Command(line);
          c.execute(calManager);
        }
        catch (IllegalArgumentException | IllegalStateException e) {
          return;
        }

      }
    } catch (IOException e) {
      throw new IOException();
    }
  }
}
