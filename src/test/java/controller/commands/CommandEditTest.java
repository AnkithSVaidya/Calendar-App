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
 * JUnit tests for edit command.
 */
public class CommandEditTest {
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
  public void testBasicEdit1() {
    InputStream in = new ByteArrayInputStream(("create calendar --name cal1 " +
        "--timezone America/New_York\nuse calendar " +
        "--name cal1\ncreate event event1 from 2025-03-01T08:07 to 2025-03-01T09:10 --desc desc1 " +
        "--location loc1 public\nedit event location event1 from 2025-03-01T08:07 to " +
        "2025-03-01T09:10 with newLoc\nq").getBytes());

    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(bytes);

    Controller controller = new Controller(in, out);
    mockCal = new MockCalendar(mockCalLog, 1111);
    mockCalManager = new MockCalendarManager(mockCalManagerLog, mockCal);
    controller.controllerGo(mockCalManager);

    assertTrue(mockCalLog.toString().contains("event1"));
    assertTrue(mockCalLog.toString().contains("desc1"));
    assertTrue(mockCalLog.toString().contains("newLoc"));
    assertTrue(mockCalLog.toString().contains("true"));
  }

  @Test
  public void testBasicEdit2() {
    InputStream in = new ByteArrayInputStream(("create calendar --name cal1 " +
        "--timezone America/New_York\nuse calendar " +
        "--name cal1\ncreate event event12 from 2025-03-11T08:00 to 2025-03-11T09:00 " +
        "--desc d2 --location l2 private\nedit events description event12 newDesc\nq").getBytes());

    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(bytes);

    Controller controller = new Controller(in, out);
    mockCal = new MockCalendar(mockCalLog, 1111);
    mockCalManager = new MockCalendarManager(mockCalManagerLog, mockCal);
    controller.controllerGo(mockCalManager);

    assertTrue(mockCalLog.toString().contains("event12"));
    assertTrue(mockCalLog.toString().contains("newDesc"));
    assertTrue(mockCalLog.toString().contains("l2"));
    assertTrue(mockCalLog.toString().contains("false"));
  }

  @Test
  public void testBasicEdit3() {
    InputStream in = new ByteArrayInputStream(("create calendar --name cal1 " +
        "--timezone America/New_York\nuse calendar " +
        "--name cal1\ncreate event event2R from 2025-03-01T10:07 to 2025-03-01T11:12 repeats " +
        "MWF for 4 times\nedit events subject event2R from 2025-03-03T10:07 " +
        "with newSubject\nq").getBytes());

    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(bytes);

    Controller controller = new Controller(in, out);
    mockCal = new MockCalendar(mockCalLog, 1111);
    mockCalManager = new MockCalendarManager(mockCalManagerLog, mockCal);
    controller.controllerGo(mockCalManager);

    assertTrue(mockCalLog.toString().contains("newSubject"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEditInvalidProperty() {
    InputStream in = new ByteArrayInputStream(("create calendar --name cal1 " +
        "--timezone America/New_York\nuse calendar " +
        "--name cal1\ncreate event event1 from 2025-03-01T08:07 to 2025-03-01T09:10 " +
        "--desc desc1 --location loc1 public\nedit event invalid event1 from " +
        "2025-03-01T08:07 to 2025-03-01T09:10 with newLoc\nq").getBytes());

    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(bytes);

    Controller controller = new Controller(in, out);
    mockCal = new MockCalendar(mockCalLog, 1111);
    mockCalManager = new MockCalendarManager(mockCalManagerLog, mockCal);
    controller.controllerGo(mockCalManager);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEditInvalidCommand() {
    InputStream in = new ByteArrayInputStream(("create calendar --name cal1 " +
        "--timezone America/New_York\nuse calendar " +
        "--name cal1\ncreate event event1 from 2025-03-01T08:07 to 2025-03-01T09:10 " +
        "--desc desc1 --location loc1 public\nedit invalid location event1 from " +
        "2025-03-01T08:07 to 2025-03-01T09:10 with newLoc\nq").getBytes());

    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(bytes);

    Controller controller = new Controller(in, out);
    mockCal = new MockCalendar(mockCalLog, 1111);
    mockCalManager = new MockCalendarManager(mockCalManagerLog, mockCal);
    controller.controllerGo(mockCalManager);
  }

  @Test
  public void testBasicEditCalendar1() {
    InputStream in = new ByteArrayInputStream(("create calendar --name cal1 " +
        "--timezone America/New_York\nuse calendar " +
        "--name cal1\ncreate event event1 from 2025-03-01T08:07 to 2025-03-01T09:10 --desc desc1 " +
        "--location loc1 public\nedit calendar --name cal1 " +
        "--property name NEWNAME\nq").getBytes());

    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(bytes);

    Controller controller = new Controller(in, out);
    mockCal = new MockCalendar(mockCalLog, 1111);
    mockCalManager = new MockCalendarManager(mockCalManagerLog, mockCal);
    controller.controllerGo(mockCalManager);

    assertTrue(mockCalManagerLog.toString().contains("editCalendar"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testBasicEditInvalidProperty() {
    InputStream in = new ByteArrayInputStream(("create calendar --name cal1 " +
        "--timezone America/New_York\nuse calendar " +
        "--name cal1\ncreate event event1 from 2025-03-01T08:07 to 2025-03-01T09:10 --desc desc1 " +
        "--location loc1 public\nedit calendar --name cal1 " +
        "--property ILLEGAL NEWNAME\nq").getBytes());

    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(bytes);

    Controller controller = new Controller(in, out);
    mockCal = new MockCalendar(mockCalLog, 1111);
    mockCalManager = new MockCalendarManager(mockCalManagerLog, mockCal);
    controller.controllerGo(mockCalManager);
  }
}