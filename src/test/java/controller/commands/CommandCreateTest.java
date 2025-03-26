package controller.commands;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.time.DateTimeException;

import controller.Controller;
import model.ICalendar;
import model.ICalendarManager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * JUnit tests for create command.
 */
public class CommandCreateTest {
  ICalendar mockCal;
  ICalendarManager mockCalManager;
  StringBuilder mockCalLog;
  StringBuilder mockCalManagerLog;

  @Before
  public void setUp() {
    mockCalLog = new StringBuilder();
    mockCalManagerLog = new StringBuilder();

  }

  @Test(expected = IllegalArgumentException.class)
  public void testBasicCreateToSmall() {
    InputStream in = new ByteArrayInputStream(("create calendar --name cal1 " +
        "--timezone America/New_York\nuse calendar " +
        "--name cal1\ncreate event event1\nq").getBytes());

    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(bytes);

    Controller controller = new Controller(in, out);

    mockCal = new MockCalendar(mockCalLog, 1111);
    mockCalManager = new MockCalendarManager(mockCalManagerLog, mockCal);

    controller.controllerGo(mockCalManager);
  }

  @Test
  public void testBasicCreate1() {

    InputStream in = new ByteArrayInputStream(("create calendar --name cal1 " +
        "--timezone America/New_York\nuse calendar --name cal1\ncreate event event1 from " +
        "2025-03-01T08:07 to 2025-03-01T09:10 --desc desc1 --location loc1 public\nq").getBytes());

    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(bytes);

    Controller controller = new Controller(in, out);
    mockCal = new MockCalendar(mockCalLog, 1111);
    mockCalManager = new MockCalendarManager(mockCalManagerLog, mockCal);
    controller.controllerGo(mockCalManager);

    assertTrue(mockCalLog.toString().contains("event1"));
    assertTrue(mockCalLog.toString().contains("desc1"));
    assertTrue(mockCalLog.toString().contains("loc1"));
    assertTrue(mockCalLog.toString().contains("true"));
  }

  @Test
  public void testBasicCreateTxtFile() throws IOException {

    InputStream in = new ByteArrayInputStream(("test").getBytes());
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(bytes);

    Controller controller = new Controller(in, out);
    mockCal = new MockCalendar(mockCalLog, 1111);
    mockCalManager = new MockCalendarManager(mockCalManagerLog, mockCal);

    controller.controllerGo(mockCalManager, new File("res/test_commands.txt"));

    assertTrue(mockCalLog.toString().contains("event1"));
    assertTrue(mockCalLog.toString().contains("desc1"));
    assertTrue(mockCalLog.toString().contains("loc1"));
    assertTrue(mockCalLog.toString().contains("true"));
  }

  @Test(expected = IOException.class)
  public void testBasicCreateTxtFileFail() throws IOException {

    InputStream in = new ByteArrayInputStream((" ").getBytes());
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(bytes);

    Controller controller = new Controller(in, out);
    mockCal = new MockCalendar(mockCalLog, 1111);
    mockCalManager = new MockCalendarManager(mockCalManagerLog, mockCal);

    controller.controllerGo(mockCalManager, new File("res/test_commands.pdf"));
  }

  @Test(expected = AssertionError.class)
  public void testBasicCreateTxtFileFail2() throws IOException {

    InputStream in = new ByteArrayInputStream(("").getBytes());
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(bytes);

    Controller controller = new Controller(in, out);

    mockCal = new MockCalendar(mockCalLog, 1111);
    mockCalManager = new MockCalendarManager(mockCalManagerLog, mockCal);

    controller.controllerGo(mockCalManager, new File("res/command_invalid.txt"));

    assertTrue(mockCalLog.toString().contains("event1"));
    assertTrue(mockCalLog.toString().contains("desc1"));
    assertTrue(mockCalLog.toString().contains("loc1"));
    assertTrue(mockCalLog.toString().contains("true"));
  }

  // Autodecline no longer supported. Test will fail.
  @Test(expected = IllegalArgumentException.class)
  public void testBasicCreate1AutoDecline() {

    InputStream in = new ByteArrayInputStream(("create calendar --name cal1 " +
        "--timezone America/New_York\nuse calendar --name cal1\ncreate event " +
        "--autoDecline event1 from " +
        "2025-03-01T08:07 to 2025-03-01T09:10 --desc desc1 --location loc1 public\nq").getBytes());

    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(bytes);

    Controller controller = new Controller(in, out);

    mockCal = new MockCalendar(mockCalLog, 1111);
    mockCalManager = new MockCalendarManager(mockCalManagerLog, mockCal);

    controller.controllerGo(mockCalManager);
  }

  @Test
  public void testBasicCreate2() {

    InputStream in = new ByteArrayInputStream(("create calendar --name cal1 " +
        "--timezone America/New_York\nuse calendar --name cal1\ncreate event event12 " +
        "from 2025-03-11T08:00 to 2025-03-11T09:00 --desc d2 --location l2 private\nq").getBytes());

    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(bytes);

    Controller controller = new Controller(in, out);

    mockCal = new MockCalendar(mockCalLog, 1111);
    mockCalManager = new MockCalendarManager(mockCalManagerLog, mockCal);

    controller.controllerGo(mockCalManager);

    assertTrue(mockCalLog.toString().contains("event12"));
    assertTrue(mockCalLog.toString().contains("d2"));
    assertTrue(mockCalLog.toString().contains("l2"));
    assertTrue(mockCalLog.toString().contains("false"));
  }

  // Auto decline will now fail.
  @Test(expected = IllegalArgumentException.class)
  public void testBasicCreate2AutoDecline() {

    InputStream in = new ByteArrayInputStream(("create calendar --name cal1 " +
        "--timezone America/New_York\nuse calendar --name cal1\ncreate event " +
        "--autoDecline event12 " +
        "from 2025-03-11T08:00 to 2025-03-11T09:00 --desc d2 --location l2 private\nq").getBytes());

    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(bytes);

    Controller controller = new Controller(in, out);

    mockCal = new MockCalendar(mockCalLog, 1111);
    mockCalManager = new MockCalendarManager(mockCalManagerLog, mockCal);

    controller.controllerGo(mockCalManager);
  }

  @Test
  public void testBasicCreate3() {

    InputStream in = new ByteArrayInputStream(("create calendar --name cal1 " +
        "--timezone America/New_York\nuse calendar --name cal1\ncreate event event2R from " +
        "2025-03-01T10:07 to 2025-03-01T11:12 repeats MWF for 4 times\nq").getBytes());

    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(bytes);

    Controller controller = new Controller(in, out);


    mockCal = new MockCalendar(mockCalLog, 1111);
    mockCalManager = new MockCalendarManager(mockCalManagerLog, mockCal);

    controller.controllerGo(mockCalManager);

    assertTrue(mockCalLog.toString().contains("event2R"));
    String str = mockCalLog.toString();
    String[] parts = str.split(" ");
    assertEquals(4, parts.length);
  }

  @Test
  public void testBasicCreate4() {

    InputStream in = new ByteArrayInputStream(("create calendar --name cal1 " +
        "--timezone America/New_York\nuse calendar --name cal1\ncreate event event3U " +
        "from 2025-03-01T12:03 to 2025-03-01T13:14 " +
        "repeats TR until 2025-03-14T12:00\nq").getBytes());

    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(bytes);

    Controller controller = new Controller(in, out);

    mockCal = new MockCalendar(mockCalLog, 1111);
    mockCalManager = new MockCalendarManager(mockCalManagerLog, mockCal);

    controller.controllerGo(mockCalManager);

    assertTrue(mockCalLog.toString().contains("event3U"));
  }

  @Test
  public void testBasicCreate5() {
    InputStream in = new ByteArrayInputStream(("create calendar --name cal1 " +
        "--timezone America/New_York\nuse calendar --name cal1\ncreate event event4Allday " +
        "on 2025-03-11T14:03\nq").getBytes());

    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(bytes);

    Controller controller = new Controller(in, out);
    mockCal = new MockCalendar(mockCalLog, 1111);
    mockCalManager = new MockCalendarManager(mockCalManagerLog, mockCal);

    controller.controllerGo(mockCalManager);

    assertTrue(mockCalLog.toString().contains("event4Allday"));
  }

  @Test
  public void testBasicCreate6() {
    InputStream in = new ByteArrayInputStream(("create calendar --name cal1 " +
        "--timezone America/New_York\nuse calendar --name cal1\ncreate event event4All " +
        "on 2025-03-02T12:03\nq").getBytes());

    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(bytes);

    Controller controller = new Controller(in, out);
    mockCal = new MockCalendar(mockCalLog, 1111);
    mockCalManager = new MockCalendarManager(mockCalManagerLog, mockCal);
    controller.controllerGo(mockCalManager);

    assertTrue(mockCalLog.toString().contains("event4All"));
  }

  @Test
  public void testBasicCreate7() {
    InputStream in = new ByteArrayInputStream(("create calendar --name cal1 " +
        "--timezone America/New_York\nuse calendar --name cal1\ncreate event " +
        "event5RepeatN on 2025-03-15 repeats SU for 2 times\nq").getBytes());

    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(bytes);

    Controller controller = new Controller(in, out);
    mockCal = new MockCalendar(mockCalLog, 1111);
    mockCalManager = new MockCalendarManager(mockCalManagerLog, mockCal);
    controller.controllerGo(mockCalManager);

    assertTrue(mockCalLog.toString().contains("event5RepeatN"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testBasicCreateInvalidN1() {
    InputStream in = new ByteArrayInputStream(("create calendar --name cal1 " +
        "--timezone America/New_York\nuse calendar --name cal1\ncreate event event5RepeatN " +
        "on 2025-03-15 repeats SU for X times\nq").getBytes());

    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(bytes);

    Controller controller = new Controller(in, out);
    mockCal = new MockCalendar(mockCalLog, 1111);
    mockCalManager = new MockCalendarManager(mockCalManagerLog, mockCal);
    controller.controllerGo(mockCalManager);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testBasicCreateInvalidN2() {
    InputStream in = new ByteArrayInputStream(("create calendar --name cal1 " +
        "--timezone America/New_York\nuse calendar --name cal1\ncreate event event2R from " +
        "2025-03-01T10:07 to 2025-03-01T11:12 repeats MWF for =+*% times\nq").getBytes());

    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(bytes);

    Controller controller = new Controller(in, out);
    mockCal = new MockCalendar(mockCalLog, 1111);
    mockCalManager = new MockCalendarManager(mockCalManagerLog, mockCal);
    controller.controllerGo(mockCalManager);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testBasicCreateInvalidN3() {
    InputStream in = new ByteArrayInputStream(("create calendar --name cal1 " +
        "--timezone America/New_York\nuse calendar --name cal1\ncreate event event5RepeatN " +
        "on 2025-03-15 repeats SU for 2 es\nq").getBytes());

    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(bytes);

    Controller controller = new Controller(in, out);
    mockCal = new MockCalendar(mockCalLog, 1111);
    mockCalManager = new MockCalendarManager(mockCalManagerLog, mockCal);
    controller.controllerGo(mockCalManager);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testBasicCreateInvalidWeekday() {
    InputStream in = new ByteArrayInputStream(("create calendar --name cal1 " +
        "--timezone America/New_York\nuse calendar --name cal1\ncreate event event5RepeatN " +
        "on 2025-03-15 repeats XYZ for 2 times\nq").getBytes());

    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(bytes);

    Controller controller = new Controller(in, out);
    mockCal = new MockCalendar(mockCalLog, 1111);
    mockCalManager = new MockCalendarManager(mockCalManagerLog, mockCal);
    controller.controllerGo(mockCalManager);
  }

  @Test
  public void testBasicCreate8() {
    InputStream in = new ByteArrayInputStream(("create calendar --name cal1 " +
        "--timezone America/New_York\nuse calendar --name cal1\ncreate event event6Alldayrepeat " +
        "on 2025-03-12 repeats WF until 2025-03-25\nq").getBytes());

    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(bytes);

    Controller controller = new Controller(in, out);
    mockCal = new MockCalendar(mockCalLog, 1111);
    mockCalManager = new MockCalendarManager(mockCalManagerLog, mockCal);
    controller.controllerGo(mockCalManager);

    assertTrue(mockCalLog.toString().contains("event6Alldayrepeat"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testBasicCreateIllegalArgument() {
    InputStream in = new ByteArrayInputStream(("create calendar --name cal1 " +
        "--timezone America/New_York\nuse calendar --name cal1\ncreate illegal " +
        "event4All on 2025-03-02T12:03\nq").getBytes());

    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(bytes);

    Controller controller = new Controller(in, out);
    mockCal = new MockCalendar(mockCalLog, 1111);
    mockCalManager = new MockCalendarManager(mockCalManagerLog, mockCal);
    controller.controllerGo(mockCalManager);
  }

  @Test(expected = DateTimeException.class)
  public void testCreateInvalidRange() {
    InputStream in = new ByteArrayInputStream(("create calendar --name cal1 " +
        "--timezone America/New_York\nuse calendar --name cal1\ncreate event event1 from " +
        "2025-03-02T08:07 to 2025-03-01T09:10\nq").getBytes());

    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(bytes);

    Controller controller = new Controller(in, out);
    mockCal = new MockCalendar(mockCalLog, 1111);
    mockCalManager = new MockCalendarManager(mockCalManagerLog, mockCal);
    controller.controllerGo(mockCalManager);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateInvalidDateTime() {
    InputStream in = new ByteArrayInputStream(("create calendar --name cal1 " +
        "--timezone America/New_York\nuse calendar --name cal1\ncreate event event1 from " +
        "2025-03-01T08:07 to 2025-03-99T09:10\nq").getBytes());

    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(bytes);

    Controller controller = new Controller(in, out);
    mockCal = new MockCalendar(mockCalLog, 1111);
    mockCalManager = new MockCalendarManager(mockCalManagerLog, mockCal);
    controller.controllerGo(mockCalManager);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateInvalidTime() {
    InputStream in = new ByteArrayInputStream(("create calendar --name cal1 " +
        "--timezone America/New_York\nuse calendar --name cal1\ncreate event event1 from " +
        "2025-03-01T08:07 to 2025-03-01T99:10\nq").getBytes());

    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(bytes);

    Controller controller = new Controller(in, out);
    mockCal = new MockCalendar(mockCalLog, 1111);
    mockCalManager = new MockCalendarManager(mockCalManagerLog, mockCal);
    controller.controllerGo(mockCalManager);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateInvalidDate() {
    InputStream in = new ByteArrayInputStream(("create calendar --name cal1 " +
        "--timezone America/New_York\nuse calendar --name cal1\ncreate event event5RepeatN on " +
        "2025-99-15 repeats SU for 2 times\nq").getBytes());

    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(bytes);

    Controller controller = new Controller(in, out);
    mockCal = new MockCalendar(mockCalLog, 1111);
    mockCalManager = new MockCalendarManager(mockCalManagerLog, mockCal);
    controller.controllerGo(mockCalManager);
  }
}