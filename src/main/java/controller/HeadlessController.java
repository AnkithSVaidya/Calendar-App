package controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import controller.commands.Command;
import model.ICalendarManager;

public class HeadlessController implements IController {

//  final InputStream in;
//  final PrintStream out;
  final Readable in;
  final Appendable out;
  ICalendarManager calendarManager;
  File f;

  public HeadlessController(Readable in, Appendable out, ICalendarManager calManager, File f) {
    this.calendarManager = calManager;
    this.f = f;
    this.in = in;
    this.out = out;
  }

  @Override
  public void controllerGo() throws IOException {
    System.out.println("here");

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
