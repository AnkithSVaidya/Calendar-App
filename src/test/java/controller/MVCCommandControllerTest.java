package controller;

import org.junit.Before;
import org.junit.Test;


import java.awt.desktop.AboutEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import controller.commands.MockCalendar;
import controller.commands.MockCalendarManager;
import model.AbstractEvent;
import model.CalendarManager;
import model.Event;
import model.ICalendar;
import model.ICalendarManager;
import view.CalendarView;
import view.EventDetails;
import view.IView;

import static org.junit.Assert.*;

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

    assertTrue(mockCalLog.toString().contains("Export"));
    assertTrue(mockViewLog.toString().contains("showExportPopup"));
    assertTrue(mockViewLog.toString().contains("Export"));

    String expected = "Calendar successfully exported to: exported.csv";
    assertEquals(expected, msg);
  }

}