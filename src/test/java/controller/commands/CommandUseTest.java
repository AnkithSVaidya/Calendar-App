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

import static org.junit.Assert.assertTrue;

/**
 * JUnit tests for Use Calendar command.
 */
public class CommandUseTest {
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
  public void testBasicUseCommand() throws IOException {
    InputStream in = new ByteArrayInputStream(("create calendar --name cal1 " +
        "--timezone America/New_York\nuse calendar --name cal1\nq").getBytes());

    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(bytes);

    controller = new InteractiveController(in, out, mockCalManager);
    controller.controllerGo();

    assertTrue(mockCalManagerLog.toString().contains("useCalendar"));
  }
}