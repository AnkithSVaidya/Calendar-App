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
 * JUnit tests for copy command.
 */
public class CommandCopyTest {
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
  public void testBasicCopy1() {
    InputStream in = new ByteArrayInputStream(("create calendar --name cal1 --timezone " +
        "America/New_York\n" +
        "use calendar --name cal1\n" +
        "create calendar --name cal2 --timezone America/Denver\n" +
        "create event event1 from " +
        "2025-03-01T08:07 to 2025-03-01T09:10 --desc desc1 --location loc1 public\n" +
        "copy event event1 on 2025-03-01T08:07 --target cal2 to 2025-03-01T10:07\n" +
        "q").getBytes());

    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(bytes);

    Controller controller = new Controller(in, out);
    mockCal = new MockCalendar(mockCalLog, 1111);
    mockCalManager = new MockCalendarManager(mockCalManagerLog, mockCal);
    controller.controllerGo(mockCalManager);

    assertTrue(mockCalManagerLog.toString().contains("copyEvent"));
  }

  @Test
  public void testBasicCopy2() {
    InputStream in = new ByteArrayInputStream(("create calendar --name cal1 --timezone " +
        "America/New_York\n" +
        "use calendar --name cal1\n" +
        "create calendar --name cal2 --timezone America/Denver\n" +
        "create event event1 from " +
        "2025-03-01T08:07 to 2025-03-01T09:10 --desc desc1 --location loc1 public\n" +
        "copy events on 2025-03-01 --target cal2 to 2025-03-02\n" +
        "q").getBytes());

    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(bytes);

    Controller controller = new Controller(in, out);
    mockCal = new MockCalendar(mockCalLog, 1111);
    mockCalManager = new MockCalendarManager(mockCalManagerLog, mockCal);
    controller.controllerGo(mockCalManager);

    assertTrue(mockCalManagerLog.toString().contains("copyEventsOn"));
  }

  @Test
  public void testBasicCopy3() {
    InputStream in = new ByteArrayInputStream(("create calendar --name cal1 --timezone " +
        "America/New_York\n" +
        "use calendar --name cal1\n" +
        "create calendar --name cal2 --timezone America/Denver\n" +
        "create event event1 from " +
        "2025-03-01T08:07 to 2025-03-01T09:10 --desc desc1 --location loc1 public\n" +
        "copy events between 2025-03-01 and 2025-03-03 --target cal2 to 2025-03-05\n" +
        "q").getBytes());

    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(bytes);

    Controller controller = new Controller(in, out);
    mockCal = new MockCalendar(mockCalLog, 1111);
    mockCalManager = new MockCalendarManager(mockCalManagerLog, mockCal);
    controller.controllerGo(mockCalManager);
    assertTrue(mockCalManagerLog.toString().contains("copyEventsBetween"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testIllegalCopy1() {
    InputStream in = new ByteArrayInputStream(("create calendar --name cal1 --timezone " +
        "America/New_York\n" +
        "use calendar --name cal1\n" +
        "create calendar --name cal2 --timezone America/Denver\n" +
        "create event event1 from " +
        "2025-03-01T08:07 to 2025-03-01T09:10 --desc desc1 --location loc1 public\n" +
        "copy ILLEGAL event1 on 2025-03-01T08:07 --target cal2 to 2025-03-01T10:07\n" +
        "q").getBytes());

    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(bytes);

    Controller controller = new Controller(in, out);
    mockCal = new MockCalendar(mockCalLog, 1111);
    mockCalManager = new MockCalendarManager(mockCalManagerLog, mockCal);
    controller.controllerGo(mockCalManager);
  }
}