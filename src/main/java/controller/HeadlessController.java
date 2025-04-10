package controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import controller.commands.Command;
import model.ICalendarManager;

/**
 * This class represents a headless controller. Use to run the calendar app with an input file.
 */
public class HeadlessController implements IController {

  final Readable in;
  final Appendable out;
  ICalendarManager calendarManager;
  File f;

  /**
   * Constructor method for Headless Controller. Initializes model and IO.
   * @param in - Readable in.
   * @param out - Apendable out.
   * @param calManager - ICalendar manager model.
   * @param f - File name.
   */
  public HeadlessController(Readable in, Appendable out, ICalendarManager calManager, File f) {
    this.calendarManager = calManager;
    this.f = f;
    this.in = in;
    this.out = out;
  }

  @Override
  public void controllerGo() throws IOException {
    try (BufferedReader reader = new BufferedReader(new FileReader(this.f))) {
      String line;
      while ((line = reader.readLine()) != null) {

        // Execute command.
        try {
          Command c = new Command(line);
          c.execute(this.calendarManager);
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
