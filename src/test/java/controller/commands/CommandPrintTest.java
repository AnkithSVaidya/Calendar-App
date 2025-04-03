package controller.commands;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import controller.IController;
import controller.InteractiveController;
import model.CalendarManager;
import model.ICalendar;
import model.ICalendarManager;

import static org.junit.Assert.assertEquals;

/**
 * JUnit tests for print command.
 */
public class CommandPrintTest {
  ICalendar mockCal;
  ICalendarManager mockCalManager;
  IController controller;
  StringBuilder mockCalLog;
  StringBuilder mockCalManagerLog;

  @Before
  public void setUp() {
    mockCalLog = new StringBuilder();
    mockCalManagerLog = new StringBuilder();
    mockCal = new MockCalendar(mockCalLog, 1111);
    mockCalManager = new MockCalendarManager(mockCalManagerLog, mockCal);
  }


  @Test
  public void testBasicPrint1() throws IOException {
    InputStream in = new ByteArrayInputStream(("create calendar --name cal1 " +
        "--timezone America/New_York\nuse calendar " +
        "--name cal1\ncreate event event1 from 2025-03-01T08:07 to 2025-03-01T09:10 " +
        "--desc desc1 --location loc1 public\nprint events on 2025-03-01\nq").getBytes());

    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(bytes);

    System.setOut(out);

    controller = new InteractiveController(in, out, new CalendarManager());
    controller.controllerGo();

    String expectedTerminalPrint = "Please enter a command." + System.lineSeparator()
        +
        "Please enter a command." + System.lineSeparator()
        +
        "Please enter a command." + System.lineSeparator()
        +
        "Please enter a command." + System.lineSeparator()
        +
        "Printing event(s)." + System.lineSeparator()
        +
        "-Event: event1 at 2025-03-01T08:07 to 2025-03-01T09:10, Location: loc1"
        +
        System.lineSeparator()
        +
        "Please enter a command." + System.lineSeparator()
        +
        "Quitting program." + System.lineSeparator();

    assertEquals(expectedTerminalPrint, bytes.toString());
  }

  @Test
  public void testBasicPrint2() throws IOException {
    InputStream in = new ByteArrayInputStream(("create calendar --name cal1 " +
        "--timezone America/New_York\nuse calendar " +
        "--name cal1\ncreate event event1 from 2025-03-01T08:07 to 2025-03-01T09:10 " +
        "--desc desc1 --location loc1 public\nprint events from " +
        "2025-03-01T07:00 to 2025-03-01T16:00\nq").getBytes());

    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(bytes);

    System.setOut(out);

    controller = new InteractiveController(in, out, new CalendarManager());
    controller.controllerGo();

    String expectedTerminalPrint = "Please enter a command." + System.lineSeparator()
        +
        "Please enter a command." + System.lineSeparator()
        +
        "Please enter a command." + System.lineSeparator()
        +
        "Please enter a command." + System.lineSeparator()
        +
        "Printing event(s)." + System.lineSeparator()
        +
        "-Event: event1 at 2025-03-01T08:07 to 2025-03-01T09:10, Location: loc1"
        +
        System.lineSeparator() +
        "Please enter a command." + System.lineSeparator()
        +
        "Quitting program." + System.lineSeparator();

    assertEquals(expectedTerminalPrint, bytes.toString());
  }

  @Test
  public void testPrintNoEvents() throws IOException {
    InputStream in = new ByteArrayInputStream(("create calendar --name cal1 " +
        "--timezone America/New_York\nuse calendar " +
        "--name cal1\ncreate event event1 from 2025-03-01T08:07 to 2025-03-01T09:10 " +
        "--desc desc1 --location loc1 public\nprint events on 2025-03-08\nq").getBytes());

    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(bytes);

    System.setOut(out);

    controller = new InteractiveController(in, out, new CalendarManager());
    controller.controllerGo();

    String expectedTerminalPrint = "Please enter a command." + System.lineSeparator()
        +
        "Please enter a command." + System.lineSeparator()
        +
        "Please enter a command." + System.lineSeparator()
        +
        "Please enter a command." + System.lineSeparator()
        +
        "Printing event(s)." + System.lineSeparator()
        +
        "Please enter a command." + System.lineSeparator()
        +
        "Quitting program." + System.lineSeparator();

    assertEquals(expectedTerminalPrint, bytes.toString());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPrintFail1() throws IOException {
    InputStream in = new ByteArrayInputStream(("create calendar --name cal1 " +
        "--timezone America/New_York\nuse calendar " +
        "--name cal1\ncreate event event1 from 2025-03-01T08:07 to 2025-03-01T09:10 " +
        "--desc desc1 --location loc1 public\nprint events on 2025-03-01 illegal\nq").getBytes());

    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(bytes);

    System.setOut(out);

    controller = new InteractiveController(in, out, new CalendarManager());
    controller.controllerGo();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPrintFail2() throws IOException {
    InputStream in = new ByteArrayInputStream(("create calendar --name cal1 " +
        "--timezone America/New_York\nuse calendar " +
        "--name cal1\ncreate event event1 from 2025-03-01T08:07 to 2025-03-01T09:10 " +
        "--desc desc1 --location loc1 public\nprint events illegal 2025-03-01\nq").getBytes());

    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(bytes);

    System.setOut(out);

    controller = new InteractiveController(in, out, new CalendarManager());
    controller.controllerGo();
  }
}