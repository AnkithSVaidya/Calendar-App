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
import model.ICalendar;
import model.ICalendarManager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * JUnit tests for export command.
 */
public class CommandExportTest {
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
  public void testBasicExport1() throws IOException {
    InputStream in = new ByteArrayInputStream(("create calendar --name cal1 " +
        "--timezone America/New_York\nuse calendar " +
        "--name cal1\ncreate event event1 from 2025-03-01T08:07 to 2025-03-01T09:10 " +
        "--desc desc1 --location loc1 public\nexport cal test.csv\nq").getBytes());

    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(bytes);

    System.setOut(out);

    controller = new InteractiveController(in, out, mockCalManager);
    controller.controllerGo();

    assertTrue(mockCalLog.toString().contains("Export"));


    String expectedTerminalPrint =
        "Please enter a command." + System.lineSeparator()
        +
        "Please enter a command." + System.lineSeparator()
        +
        "Please enter a command." + System.lineSeparator()
        +
        "Please enter a command." + System.lineSeparator()
        +
        "Export Filename: " + System.lineSeparator()
        +
        "Please enter a command." + System.lineSeparator()
        +
        "Quitting program." + System.lineSeparator();

    assertEquals(expectedTerminalPrint, bytes.toString());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testBasicExportFail1() throws IOException {
    InputStream in = new ByteArrayInputStream(("create calendar --name cal1 " +
        "--timezone America/New_York\nuse calendar " +
        "--name cal1\ncreate event event1 from 2025-03-01T08:07 to 2025-03-01T09:10 " +
        "--desc desc1 --location loc1 public\nexport illegal test.csv\nq").getBytes());

    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(bytes);
    controller = new InteractiveController(in, out, mockCalManager);
    controller.controllerGo();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testBasicExportFail2() throws IOException {
    InputStream in = new ByteArrayInputStream(("create calendar --name cal1 " +
        "--timezone America/New_York\nuse calendar " +
        "--name cal1\ncreate event event1 from 2025-03-01T08:07 to 2025-03-01T09:10 " +
        "--desc desc1 --location loc1 public\nexport cal\nq").getBytes());

    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(bytes);
    controller = new InteractiveController(in, out, mockCalManager);
    controller.controllerGo();
  }


  @Test(expected = IllegalArgumentException.class)
  public void testBasicExportFail3() throws IOException {
    InputStream in = new ByteArrayInputStream(("create calendar --name cal1 " +
        "--timezone America/New_York\nuse calendar " +
        "--name cal1\ncreate event event1 from 2025-03-01T08:07 to 2025-03-01T09:10 " +
        "--desc desc1 --location loc1 public\nexport cal test.pdf\nq").getBytes());

    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(bytes);

    controller = new InteractiveController(in, out, mockCalManager);
    controller.controllerGo();
  }
}