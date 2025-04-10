package controller;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import controller.commands.MockCalendar;
import controller.commands.MockCalendarManager;
import model.ICalendar;
import model.ICalendarManager;
import view.IView;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * JUnit tests to test the mvc controller.
 */
public class MVCCommandControllerTest {

  ICalendar mockCal;
  ICalendarManager mockCalManager;
  IView mockView;
  MVCCommandController controller;
  StringBuilder mockCalLog;
  StringBuilder mockCalManagerLog;
  StringBuilder mockViewLog;

  @Before
  public void setUp() {
    mockCalLog = new StringBuilder();
    mockCalManagerLog = new StringBuilder();
    mockViewLog = new StringBuilder();
    mockCal = new MockCalendar(mockCalLog, 1111);
    mockCalManager = new MockCalendarManager(mockCalManagerLog, mockCal);
    mockView = new MockView(mockViewLog);
  }

  @Test
  public void testExportFileCommand() throws IOException {
    String testCommand = "Export Calendar";
    LocalDate today = LocalDate.now();
    controller = new MVCCommandController(mockCalManager, mockView);
    String msg = controller.processCommand(testCommand, today);

    assertTrue(mockCalLog.toString().contains("Export"));
    assertTrue(mockViewLog.toString().contains("showExportPopup"));
    assertTrue(mockViewLog.toString().contains("Export"));

    String expected = "Calendar successfully exported to: exported.csv";
    assertEquals(expected, msg);
  }

  @Test
  public void testImportFileCommand() throws IOException {
    String testCommand = "Import Calendar";
    LocalDate today = LocalDate.now();
    controller = new MVCCommandController(mockCalManager, mockView);
    String msg = controller.processCommand(testCommand, today);

    assertTrue(mockCalLog.toString().contains("Import"));
    assertTrue(mockViewLog.toString().contains("showImportPopup"));

    String expected = "Successfully imported 1 events.";
    assertEquals(expected, msg);
  }

  @Test
  public void testCreateCalendarCommand() throws IOException {
    String testCommand = "Create Calendar";
    LocalDate today = LocalDate.now();
    controller = new MVCCommandController(mockCalManager, mockView);
    String msg = controller.processCommand(testCommand, today);

    assertTrue(mockCalManagerLog.toString().contains("createCalendar"));
    assertTrue(mockViewLog.toString().contains("createCalendarPopup"));
  }

  @Test
  public void testCreateEventCommand() throws IOException {
    String testCommand = "Create Event";
    LocalDate today = LocalDate.now();

    // ["create_event", title, fromTime, toTime, dateString]
    List<String> commandList = List.of("create_event", "testName" , "14:30", "15:30",
            today.toString(), "desc", "loc", "true");
    mockView.setCalendarCommandList(commandList);

    controller = new MVCCommandController(mockCalManager, mockView);
    String msg = controller.processCommand(testCommand, today);

    assertTrue(mockCalLog.toString().contains("AddEvent"));
    assertTrue(mockViewLog.toString().contains("showCreateEventPopup"));

    String expected = "Creating Event " + commandList.get(1)
            + " on " + commandList.get(4) + " from "
            + commandList.get(2) + " to " + commandList.get(3);
    assertEquals(expected, msg);
  }

  @Test
  public void testCreateAllDayEvent() throws IOException {
    String testCommand = "Create All Day Event";
    LocalDate today = LocalDate.now();

    // ["create_all_day_event", title, dateString, ...]
    List<String> commandList = List.of("create_all_day_event", "testName",
            today.toString(), "desc", "loc", "true");
    mockView.setCalendarCommandList(commandList);

    controller = new MVCCommandController(mockCalManager, mockView);
    String msg = controller.processCommand(testCommand, today);

    assertTrue(mockCalLog.toString().contains("AllDay"));
    assertTrue(mockViewLog.toString().contains("showCreateAllDayEventPopup"));

    String expected = "Creating All Day Event " + commandList.get(1) +
            " on " + commandList.get(2);
    assertEquals(expected, msg);
  }

  @Test
  public void testCreateRecurringEvent() throws IOException {
    String testCommand = "Create Recurring Event";
    LocalDate today = LocalDate.now();

    // ["create_recurring_event", title, fromTime, toTime, daysString, extraDateString]
    // {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
    List<String> commandList = List.of("create_recurring_event", "testName",
            "14:30", "15:30", "Mon, Tue, Wed", "2", today.toString(), "desc", "loc", "true");
    mockView.setCalendarCommandList(commandList);

    controller = new MVCCommandController(mockCalManager, mockView);
    String msg = controller.processCommand(testCommand, today);

    assertTrue(mockCalLog.toString().contains("addRecurringEvent"));
    assertTrue(mockViewLog.toString().contains("showRecurringEventPopup"));

    String expected = "Creating Recurring Event " + commandList.get(1) + " on " + commandList.get(4)
            + " over " + commandList.get(5) + " times.";
    assertEquals(expected, msg);
  }

  @Test
  public void testEditEvent() throws IOException {
    String testCommand = "Edit Event";
    LocalDate today = LocalDate.now();


    // ["edit_event", eventName, property, from, to, newValue, date]
    List<String> commandList = List.of("edit_event", "testName",
            "subject", "14:30", "15:30", "newSubject", today.toString());
    mockView.setCalendarCommandList(commandList);

    controller = new MVCCommandController(mockCalManager, mockView);
    String msg = controller.processCommand(testCommand, today);

    assertTrue(mockCalLog.toString().contains("editEvent"));
    assertTrue(mockViewLog.toString().contains("showEditEventPopup"));

    String expected = "Edited event " + commandList.get(1)
            + " Property: " + commandList.get(2) + " to "
            + commandList.get(5) + ".";
    assertEquals(expected, msg);
  }

  @Test
  public void testEditMultipleEvents() throws IOException {
    String testCommand = "Edit Events";
    LocalDate today = LocalDate.now();

    List<String> commandList = List.of("edit_recurring_events", "testName",
            "subject", "newSubject", today.toString(), "14:30");
    mockView.setCalendarCommandList(commandList);

    controller = new MVCCommandController(mockCalManager, mockView);
    String msg = controller.processCommand(testCommand, today);

    assertTrue(mockCalLog.toString().contains("editEvents"));
    assertTrue(mockViewLog.toString().contains("showEditRecurringEventPopup"));

    String expected = "Editing All Events " + commandList.get(1) +
            " Property: " + commandList.get(2) + " to value: " + commandList.get(3) +
            " starting from " + commandList.get(4);
    assertEquals(expected, msg);
  }

  @Test
  public void testDayOptionsCommand() throws IOException {
    String testCommand = "Day Options";
    LocalDate today = LocalDate.now();

    controller = new MVCCommandController(mockCalManager, mockView);
    String msg = controller.processCommand(testCommand, today);

    assertTrue(mockViewLog.toString().contains("showDayPopup"));
    String expected = "options";
    assertEquals(expected, msg);
  }

  @Test
  public void testExitCommand() throws IOException {
    String testCommand = "Exit Button";
    LocalDate today = LocalDate.now();

    controller = new MVCCommandController(mockCalManager, mockView);
    String msg = controller.processCommand(testCommand, today);

    String expected = "exit";
    assertEquals(expected, msg);
  }
}