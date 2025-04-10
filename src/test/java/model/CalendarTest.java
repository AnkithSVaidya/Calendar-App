package model;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * JUnit Test case for Calendar Class.
 */
public class CalendarTest {

  private Calendar calendar;
  private Event event1;
  private Event event2;
  private Event overlappingEvent;


  @Before
  public void setUp() {
    calendar = new Calendar();
    event1 = new Event("Meeting",
        LocalDateTime.of(2025, 3, 10, 10, 0),
        LocalDateTime.of(2025, 3, 10, 11, 0),
        "Project discussion", "Room A", true);
    event2 = new Event("Lunch",
        LocalDateTime.of(2025, 3, 10, 12, 0),
        LocalDateTime.of(2025, 3, 10, 13, 0),
        "Team lunch", "Cafeteria", false);
    overlappingEvent = new Event("Overlap",
        LocalDateTime.of(2025, 3, 10, 10, 30),
        LocalDateTime.of(2025, 3, 10, 11, 30),
        "Overlapping meeting", "Room B", true);
  }

//  @Test
//  public void testAddEventSuccessfully() {
//    calendar.addEvent(event1, false);
//    List<AbstractEvent> events = calendar.getEventsOnDate(LocalDate.of(2025,
//        3, 10));
//    assertEquals(1, events.size());
//    assertEquals("Meeting", events.get(0).getTitle());
//  }
//
//  @Test
//  public void testAddEventConflictWithAutoDecline() {
//    calendar.addEvent(event1, false);
//    // With auto-decline, the conflicting event is ignored
//    calendar.addEvent(overlappingEvent, true);
//    List<AbstractEvent> events = calendar.getEventsOnDate(LocalDate.of(2025,
//        3, 10));
//    assertEquals(1, events.size());
//  }
//
//  @Test
//  public void testAddEventConflictWithoutAutoDecline() {
//    calendar.addEvent(event1, false);
//    // Without auto-decline, even conflicting events are added.
//    calendar.addEvent(overlappingEvent, false);
//    List<AbstractEvent> events = calendar.getEventsOnDate(LocalDate.of(2025,
//        3,
//        10));
//    assertEquals(2, events.size());
//  }
//
//  // Dummy AbstractEvent that is NOT an instance of Event.
//  private class DummyEvent extends AbstractEvent {
//    public DummyEvent(LocalDateTime start) {
//      // Call AbstractEvent's parameterized constructor:
//      super("Dummy", start, "", "", true);
//    }
//
//    @Override
//    public LocalDateTime getEnd() {
//      return getStart().plusHours(1);
//    }
//  }
//
//  @Test
//  public void testAddNonEvent() {
//    DummyEvent dummy = new DummyEvent(LocalDateTime.of(2025, 3,
//        10, 9, 0));
//    calendar.addEvent(dummy, false);
//    // Since dummy is not an instance of Event, it should not be added.
//    List<AbstractEvent> events = calendar.getEventsOnDate(LocalDate.of(2025,
//        3, 10));
//    assertEquals(0, events.size());
//  }
//
//  @Test
//  public void testGetEventsBetween() {
//    calendar.addEvent(event1, false);
//    calendar.addEvent(event2, false);
//    List<AbstractEvent> events = calendar.getEventsBetween(
//        LocalDateTime.of(2025, 3, 10, 9, 0),
//        LocalDateTime.of(2025, 3, 10, 14, 0));
//    assertEquals(2, events.size());
//  }
//
//  @Test
//  public void testGetEventsOnDateForMultiDayEvent() {
//    // Create an event that spans multiple days.
//    Event multiDay = new Event("Conference",
//        LocalDateTime.of(2025, 3, 9, 9, 0),
//        LocalDateTime.of(2025, 3, 11, 17, 0),
//        "Annual Conference", "Hall", true);
//    calendar.addEvent(multiDay, false);
//    // Querying for a day in the middle should return the event.
//    List<AbstractEvent> events = calendar.getEventsOnDate(LocalDate.of(2025,
//        3, 10));
//    assertEquals(1, events.size());
//    assertEquals("Conference", events.get(0).getTitle());
//  }
//
//  @Test
//  public void testGetEventsBetweenWithAllDayEvent() {
//    // Create an all-day event (end is null).
//    Event allDay = new Event("Holiday",
//        LocalDateTime.of(2025, 3, 15, 0, 0),
//        null,  // indicates all-day event
//        "National holiday", "City", false);
//    calendar.addEvent(allDay, false);
//    List<AbstractEvent> events = calendar.getEventsBetween(
//        LocalDateTime.of(2025, 3, 14, 0, 0),
//        LocalDateTime.of(2025, 3, 16, 0, 0));
//    assertEquals(1, events.size());
//    assertEquals("Holiday", events.get(0).getTitle());
//  }
//
//  @Test
//  public void testIsBusyAt() {
//    calendar.addEvent(event1, false);
//    assertTrue(calendar.isBusyAt(LocalDateTime.of(2025, 3,
//        10, 10, 30)));
//    assertFalse(calendar.isBusyAt(LocalDateTime.of(2025, 3,
//        10, 9, 30)));
//  }
//
//  @Test
//  public void testIsBusyAtForAllDayEvent() {
//    // Test isBusyAt for an all-day event.
//    Event allDay = new Event("Holiday",
//        LocalDateTime.of(2025, 3, 15, 0, 0),
//        null,
//        "National holiday", "City", false);
//    calendar.addEvent(allDay, false);
//    // Should be busy during the day.
//    assertTrue(calendar.isBusyAt(LocalDateTime.of(2025, 3,
//        15, 12, 0)));
//    // Should not be busy before the event starts.
//    assertFalse(calendar.isBusyAt(LocalDateTime.of(2025, 3,
//        14, 23, 59)));
//  }
//
//  @Test
//  public void testExportToCSV() throws IOException {
//    calendar.addEvent(event1, false);
//    calendar.addEvent(event2, false);
//    String filename = "test_calendar.csv";
//    String exportedFilename = calendar.exportToCSV(filename);
//    File file = new File(exportedFilename);
//    assertTrue(file.exists());
//    String content = new String(Files.readAllBytes(file.toPath()));
//    // Adjust header check if needed to match your CSV implementation.
//    assertTrue(content.contains("Subject, Start Date, Start Time, End Date, "
//        +
//        "End Time, All Day Event, Description, Location, Private"));
//    assertTrue(content.contains("Meeting"));
//    assertTrue(content.contains("Lunch"));
//    // Cleanup test file
//    file.delete();
//  }
//
//  @Test
//  public void testExportToCSVForAllDayEvent() throws IOException {
//    Event allDay = new Event("Holiday",
//        LocalDateTime.of(2025, 3, 15, 0, 0),
//        null,
//        "National holiday", "City", false);
//    calendar.addEvent(allDay, false);
//    String filename = "test_calendar_all_day.csv";
//    String exportedFilename = calendar.exportToCSV(filename);
//    File file = new File(exportedFilename);
//    assertTrue(file.exists());
//    String content = new String(Files.readAllBytes(file.toPath()));
//    // Check that the CSV contains the event with empty End Date and End Time fields.
//    String[] lines = content.split("\n");
//    boolean found = false;
//    for (String line : lines) {
//      if (line.startsWith("Holiday")) {
//        String[] fields = line.split(",");
//        // The CSV header is: Subject, Start Date, Start Time, End Date,
//        // End Time, All Day Event, Description, Location, Private
//        // For an all-day event, End Date and End Time should be empty.
//        assertTrue(fields[3].trim().isEmpty());
//        assertTrue(fields[4].trim().isEmpty());
//        found = true;
//        break;
//      }
//    }
//    assertTrue(found);
//    file.delete();
//  }
//
//  @Test
//  public void testEditEvent() {
//    calendar.addEvent(event1, false);
//    boolean edited = calendar.editEvent("description", "Meeting",
//        event1.getStart(), event1.getEnd(), "Updated description");
//    assertTrue(edited);
//    List<AbstractEvent> events = calendar.getEventsOnDate(
//        event1.getStart().toLocalDate()
//    );
//    assertEquals("Updated description", events.get(0).getDescription());
//  }
//
//  @Test(expected = IllegalArgumentException.class)
//  public void testEditEventWithInvalidProperty() {
//    calendar.addEvent(event1, false);
//    calendar.editEvent("invalid", "Meeting", event1.getStart(),
//        event1.getEnd(), "New Value");
//  }
//
//  @Test
//  public void testEditEvents() {
//    calendar.addEvent(event1, false);
//    // Add a duplicate event for batch editing.
//    Event duplicate = new Event("Meeting", event1.getStart(), event1.getEnd(),
//        "Desc", "Room A", true);
//    calendar.addEvent(duplicate, false);
//    boolean edited = calendar.editEvents("location",
//        "Meeting", event1.getStart(), "New Room");
//    assertTrue(edited);
//    List<AbstractEvent> events = calendar.getEventsOnDate(
//        event1.getStart().toLocalDate());
//    for (AbstractEvent e : events) {
//      assertEquals("New Room", e.getLocation());
//    }
//  }
//
//  @Test(expected = IllegalArgumentException.class)
//  public void testEditEventsWithInvalidProperty() {
//    calendar.addEvent(event1, false);
//    calendar.editEvents("invalid", "Meeting",
//        event1.getStart(), "New Value");
//  }
//
//  @Test
//  public void testEditAllEvents() {
//    calendar.addEvent(event1, false);
//    Event duplicate = new Event("Meeting", event1.getStart(),
//        event1.getEnd(), "Desc", "Room A", true);
//    calendar.addEvent(duplicate, false);
//    boolean edited = calendar.editAllEvents("subject",
//        "Meeting", "Team Meeting");
//    assertTrue(edited);
//    List<AbstractEvent> events = calendar.getEventsOnDate(
//        event1.getStart().toLocalDate());
//    for (AbstractEvent e : events) {
//      assertEquals("Team Meeting", e.getTitle());
//    }
//  }
//
//  @Test(expected = IllegalArgumentException.class)
//  public void testEditAllEventsWithInvalidProperty() {
//    calendar.addEvent(event1, false);
//    calendar.editAllEvents("invalid",
//        "Meeting",
//        "New Value");
//  }
//
//  @Test
//  public void testEditEventNotFound() {
//    boolean result = calendar.editEvent("description", "NonExisting",
//        LocalDateTime.of(2025, 3, 10, 10, 0),
//        LocalDateTime.of(2025, 3, 10, 11, 0),
//        "New Desc");
//    assertFalse(result);
//  }
//
//  @Test
//  public void testEditEventsNotFound() {
//    boolean result = calendar.editEvents("location",
//        "NonExisting",
//        LocalDateTime.of(2025, 3, 10,
//            10, 0), "New Room");
//    assertFalse(result);
//  }
//
//  @Test
//  public void testEditAllEventsNotFound() {
//    boolean result = calendar.editAllEvents("subject",
//        "NonExisting", "New Title");
//    assertFalse(result);
//  }
//
//
//
//  @Test
//  public void testExportToCSVWithMixedEvents() throws IOException {
//    // Add a regular event
//    calendar.addEvent(event1, false);
//
//    // Add an all-day event (with null end)
//    Event allDayEvent = new Event("All-Day Meeting",
//        LocalDateTime.of(2025, 3, 15, 0, 0),
//        null,
//        "Full day workshop", "Conference Room", true);
//    calendar.addEvent(allDayEvent, false);
//
//    String filename = "test_mixed_events.csv";
//    String exportedFilename = calendar.exportToCSV(filename);
//    File file = new File(exportedFilename);
//
//    try {
//      assertTrue(file.exists());
//      String content = new String(Files.readAllBytes(file.toPath()));
//
//      // Verify regular event details (non-null end)
//      assertTrue(content.contains("Meeting," + event1.getStart().toLocalDate() + ","
//          +
//          event1.getStart().toLocalTime() + ","
//          +
//          event1.getEnd().toLocalDate() + ","
//          +
//          event1.getEnd().toLocalTime() + ",false"));
//
//      // Verify all-day event details (null end)
//      assertTrue(content.contains("All-Day Meeting,"
//          +
//          allDayEvent.getStart().toLocalDate() + ","
//          +
//          allDayEvent.getStart().toLocalTime() + ",,,true"));
//    } finally {
//      file.delete();
//    }
//  }
//
//  @Test
//  public void testExportToCSVTernaryOperators() throws IOException {
//    // Add a regular event
//    calendar.addEvent(event1, false);
//
//    // Add an all-day event (with null end)
//    Event allDayEvent = new Event("All-Day Meeting",
//        LocalDateTime.of(2025, 3, 15, 0, 0),
//        null,
//        "Full day workshop", "Conference Room", true);
//    calendar.addEvent(allDayEvent, false);
//
//    String filename = "test_ternary_operators.csv";
//    String exportedFilename = calendar.exportToCSV(filename);
//    File file = new File(exportedFilename);
//
//    try {
//      assertTrue(file.exists());
//      String content = new String(Files.readAllBytes(file.toPath()));
//
//      // The key is to verify the exact format with and without endTime values
//      // This will detect if the ternary operator is negated
//
//      // For event with non-null end time, check that time value appears in output
//      assertTrue("CSV should include the end time for regular events",
//          content.contains(event1.getEnd().toLocalTime().toString()));
//
//      // Make sure the end time appears in the right position (5th column)
//      String[] lines = content.split("\n");
//      for (String line : lines) {
//        if (line.startsWith("Meeting")) {
//          String[] parts = line.split(",");
//          assertEquals("End time should be in column 5",
//              event1.getEnd().toLocalTime().toString(), parts[4].trim());
//        }
//      }
//
//      // For all-day event, verify empty string appears in end time position
//      for (String line : lines) {
//        if (line.startsWith("All-Day")) {
//          String[] parts = line.split(",");
//          assertEquals("End time should be empty for all-day events",
//              "", parts[4].trim());
//        }
//      }
//    } finally {
//      file.delete();
//    }
//  }
//
//  @Test
//  public void testExportToCSVPrivateField() throws IOException {
//    // Event1 is private (isPublic = true), so Private column should be false
//    calendar.addEvent(event1, false);
//    // Event2 is not private (isPublic = false), so Private column should be true
//    calendar.addEvent(event2, false);
//    String filename = "test_private_field.csv";
//    String exportedFilename = calendar.exportToCSV(filename);
//    File file = new File(exportedFilename);
//    try {
//      String content = new String(Files.readAllBytes(file.toPath()));
//      String[] lines = content.split("\n");
//      // Skip header line
//      for (int i = 1; i < lines.length; i++) {
//        String line = lines[i].trim();
//        if (line.startsWith("Meeting")) {
//          assertTrue(line.endsWith("false"));
//        } else if (line.startsWith("Lunch")) {
//          assertTrue(line.endsWith("true"));
//        }
//      }
//    } finally {
//      file.delete();
//    }
//  }
//
//
//  @Test
//  public void testAddRecurringEvent() {
//    // Create a RecurringEvent with valid parameters
//    RecurringEvent recurringEvent = new RecurringEvent(
//        "Team Meeting", // title
//        LocalDateTime.of(2025, 3, 10,
//            9, 0),
//        LocalDateTime.of(2025, 3, 10, 10, 0),
//        "Weekly sync",
//        "Conference Room",
//        true, // isPublic
//        Set.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY), // Recur on Mondays and Tuesdays
//        2 // occurrences (generate 2 events)
//    );
//
//    calendar.addRecurringEvent(recurringEvent, false);
//
//    // Verify events are generated on 2025-03-10 (Monday) and 2025-03-11 (Tuesday)
//    List<AbstractEvent> eventsDay10 = calendar.getEventsOnDate(LocalDate.of(2025,
//        3, 10));
//    List<AbstractEvent> eventsDay11 = calendar.getEventsOnDate(LocalDate.of(2025,
//        3, 11));
//
//    assertEquals(1, eventsDay10.size());
//    assertEquals(1, eventsDay11.size());
//    assertEquals("Team Meeting", eventsDay10.get(0).getTitle());
//    assertEquals("Team Meeting", eventsDay11.get(0).getTitle());
//  }
//
//  @Test
//  public void testEditEventExactMatch() {
//    calendar.addEvent(event1, false);
//
//    boolean edited = calendar.editEvent(
//        "description",
//        "Meeting", // Must match event1's title
//        event1.getStart(), // Exact start time
//        event1.getEnd(), // Exact end time
//        "New Description"
//    );
//
//    assertTrue(edited);
//    assertEquals("New Description", event1.getDescription());
//  }
//
//  @Test
//  public void testEditEventNoMatch() {
//    // Add an event to the calendar
//    calendar.addEvent(event1, false);
//
//    // Try to edit an event that does NOT exist (wrong title, start, or end time)
//    boolean edited = calendar.editEvent(
//        "description",
//        "Non-Existent Event", // Wrong title
//        event1.getStart(),
//        event1.getEnd(),
//        "New Description"
//    );
//
//    assertFalse(edited); // Should return false
//  }
//
//  @Test
//  public void testSetters() {
//    // Existing tests
//    event1.setTitle("Team Meeting");
//    assertEquals("Team Meeting", event1.getTitle());
//
//    LocalDateTime newEnd = event1.getEnd().plusMinutes(15);
//    event1.setEnd(newEnd);
//    assertEquals(newEnd, event1.getEnd());
//
//    // NEW: Test setters from AbstractEvent
//    // Test setStart
//    LocalDateTime newStart = LocalDateTime.of(2025, 3,
//        11, 10, 0);
//    event1.setStart(newStart);
//    assertEquals(newStart, event1.getStart());
//
//    // Test setDescription
//    event1.setDescription("Updated Discussion");
//    assertEquals("Updated Discussion", event1.getDescription());
//
//    // Test setLocation
//    event1.setLocation("Room 2");
//    assertEquals("Room 2", event1.getLocation());
//
//    // Test setPublic
//    event1.setPublic(false);
//    assertFalse(event1.isPublic());
//  }
//
//  @Test
//  public void testConflictsWithTwoAllDayEvents() {
//    // All-day event on 2025-03-10
//    Event allDayEvent1 = new Event(
//        "Holiday",
//        LocalDateTime.of(2025, 3, 10, 0, 0),
//        null,
//        "Public Holiday",
//        "City",
//        false
//    );
//
//    // Another all-day event overlapping on 2025-03-10
//    Event allDayEvent2 = new Event(
//        "Festival",
//        LocalDateTime.of(2025, 3, 10, 0, 0),
//        null,
//        "Celebration",
//        "Park",
//        false
//    );
//
//    assertTrue(allDayEvent1.conflictsWith(allDayEvent2)); // Should conflict
//  }
//
//
//  @Test
//  public void testEditEventFailsWithCorrectTitleButWrongStartTime() {
//    // Create and add an event
//    Event testEvent = new Event("Meeting",
//        LocalDateTime.of(2025, 3, 10, 10, 0),
//        LocalDateTime.of(2025, 3, 10, 11, 0),
//        "Project discussion", "Room A", true);
//    calendar.addEvent(testEvent, false);
//
//    // Try to edit with correct title but different start time
//    LocalDateTime differentStartTime = LocalDateTime.of(2025, 3, 10, 9, 0); // 1 hour earlier
//    boolean result = calendar.editEvent("description",
//        "Meeting",
//        differentStartTime,
//        testEvent.getEnd(),
//        "Updated description");
//
//    // Should fail because start times don't match
//    assertFalse("Edit should fail when start time doesn't match", result);
//    assertEquals("Project discussion", testEvent.getDescription()); // Description should not change
//  }
//
//
//  @Test
//  public void testEditEventFailsWithCorrectTitleAndStartButWrongEndTime() {
//    // Create and add an event with a specific end time
//    Event testEvent = new Event("Meeting",
//        LocalDateTime.of(2025, 3, 10, 10, 0),
//        LocalDateTime.of(2025, 3, 10, 11, 0),
//        "Project discussion", "Room A", true);
//    calendar.addEvent(testEvent, false);
//
//    // Try to edit with correct title and start time but wrong end time
//    LocalDateTime differentEndTime = LocalDateTime.of(2025, 3, 10, 12, 0); // 1 hour later
//    boolean result = calendar.editEvent("description",
//        "Meeting",
//        testEvent.getStart(),
//        differentEndTime,
//        "Updated description");
//
//    // Should fail because end times don't match
//    assertFalse("Edit should fail when end time doesn't match", result);
//    assertEquals("Project discussion", testEvent.getDescription()); // Description should not change
//  }
//
//
//  @Test
//  public void testEditAllDayEventWithNullEndTime() {
//    // Create and add an all-day event (null end time)
//    Event allDayEvent = new Event("Holiday",
//        LocalDateTime.of(2025, 3, 15, 0, 0),
//        null, // All-day event has null end time
//        "National holiday", "City", false);
//    calendar.addEvent(allDayEvent, false);
//
//    // Try to edit with correct title, start time, and null end time
//    boolean result = calendar.editEvent("description",
//        "Holiday",
//        allDayEvent.getStart(),
//        null,
//        "Updated holiday description");
//
//    // Should succeed
//    assertTrue("Edit should succeed when all parameters match", result);
//    assertEquals("Updated holiday description", allDayEvent.getDescription());
//  }
//
//
//  @Test
//  public void testEditAllDayEventFailsWithNonNullEndTime() {
//    // Create and add an all-day event (null end time)
//    Event allDayEvent = new Event("Holiday",
//        LocalDateTime.of(2025, 3, 15, 0, 0),
//        null, // All-day event has null end time
//        "National holiday", "City", false);
//    calendar.addEvent(allDayEvent, false);
//
//    // Try to edit with correct title and start time but non-null end time
//    LocalDateTime someEndTime = LocalDateTime.of(2025, 3, 15, 23, 59);
//    boolean result = calendar.editEvent("description",
//        "Holiday",
//        allDayEvent.getStart(),
//        someEndTime,
//        "Updated holiday description");
//
//    // Should fail because end times don't match (null vs non-null)
//    assertFalse("Edit should fail when null end time is compared with non-null", result);
//    assertEquals("National holiday", allDayEvent.getDescription()); // Description should not change
//  }
//
//
//  @Test
//  public void testEditRegularEventFailsWithNullEndTime() {
//    // Create and add a regular event with non-null end time
//    Event regularEvent = new Event("Meeting",
//        LocalDateTime.of(2025, 3, 10, 10, 0),
//        LocalDateTime.of(2025, 3, 10, 11, 0),
//        "Project discussion", "Room A", true);
//    calendar.addEvent(regularEvent, false);
//
//    // Try to edit with correct title and start time but null end time
//    boolean result = calendar.editEvent("description",
//        "Meeting",
//        regularEvent.getStart(),
//        null, // Passing null end time for a regular event
//        "Updated description");
//
//    // Should fail because end times don't match (non-null vs null)
//    assertFalse("Edit should fail when non-null end time is compared with null", result);
//    assertEquals("Project discussion", regularEvent.getDescription());
//  }




}
