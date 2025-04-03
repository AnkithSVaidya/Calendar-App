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

import static org.junit.Assert.assertEquals;

/**
 * JUnit tests for show command.
 */
public class CommandShowTest {
  StringBuilder mockCalLog;
  StringBuilder mockCalManagerLog;
  IController controller;


  @Before
  public void setUp() {
    mockCalLog = new StringBuilder();
    mockCalManagerLog = new StringBuilder();
  }

  @Test
  public void testBasicShow1() throws IOException {
    InputStream in = new ByteArrayInputStream(("create calendar --name cal1 " +
        "--timezone America/New_York\nuse calendar " +
        "--name cal1\ncreate event event1 from 2025-03-01T08:07 to 2025-03-01T09:10 " +
        "--desc desc1 --location loc1 public\nshow status on 2025-03-01T08:10\nq").getBytes());

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
        "Busy." + System.lineSeparator() +
        "Please enter a command." + System.lineSeparator()
        +
        "Quitting program." + System.lineSeparator();

    assertEquals(expectedTerminalPrint, bytes.toString());
  }

  @Test
  public void testBasicShow2() throws IOException {
    InputStream in = new ByteArrayInputStream(("create calendar --name cal1 " +
        "--timezone America/New_York\nuse calendar " +
        "--name cal1\ncreate event event1 from 2025-03-01T08:07 to " +
        "2025-03-01T09:10 --desc desc1 --location loc1 " +
        "public\nshow status on 2025-03-01T06:10\nq").getBytes());

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
        "Available." + System.lineSeparator()
        +
        "Please enter a command." + System.lineSeparator()
        +
        "Quitting program." + System.lineSeparator();

    assertEquals(expectedTerminalPrint, bytes.toString());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testShowFail1() throws IOException {
    InputStream in = new ByteArrayInputStream(("create calendar --name cal1 " +
        "--timezone America/New_York\nuse calendar " +
        "--name cal1\ncreate event event1 from 2025-03-01T08:07 " +
        "to 2025-03-01T09:10 --desc desc1 --location loc1 " +
        "public\nshow on 2025-03-01T06:10\nq").getBytes());

    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(bytes);

    System.setOut(out);

    controller = new InteractiveController(in, out, new CalendarManager());
    controller.controllerGo();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testShowFail2() throws IOException {
    InputStream in = new ByteArrayInputStream(("create calendar --name cal1 " +
        "--timezone America/New_York\nuse calendar " +
        "--name cal1\ncreate event event1 from 2025-03-01T08:07 to 2025-03-01T09:10 " +
        "--desc desc1 --location loc1 public\nshow illegal on 2025-03-01T06:10\nq").getBytes());

    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(bytes);

    System.setOut(out);
    controller = new InteractiveController(in, out, new CalendarManager());
    controller.controllerGo();
  }
}