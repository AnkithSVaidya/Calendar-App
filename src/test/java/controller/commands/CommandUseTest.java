package controller.commands;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import controller.Controller;
import model.ICalendar;
import model.ICalendarManager;

import static org.junit.Assert.assertTrue;

/**
 * JUnit tests for Use Calendar command.
 */
public class CommandUseTest {
  ICalendar mockCal;
  ICalendarManager mockCalManager;
  StringBuilder mockCalLog;
  StringBuilder mockCalManagerLog;

  @Before
  public void setUp() {
    mockCalLog = new StringBuilder();
    mockCalManagerLog = new StringBuilder();
  }

  @Test
  public void testBasicUseCommand() {
    InputStream in = new ByteArrayInputStream(("create calendar --name cal1 " +
        "--timezone America/New_York\nuse calendar --name cal1\nq").getBytes());

    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(bytes);

    Controller controller = new Controller(in, out);

    mockCal = new MockCalendar(mockCalLog, 1111);
    mockCalManager = new MockCalendarManager(mockCalManagerLog, mockCal);

    controller.controllerGo(mockCalManager);

    assertTrue(mockCalManagerLog.toString().contains("useCalendar"));
  }
}