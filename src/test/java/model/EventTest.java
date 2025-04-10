package model;

import org.junit.Test;
import org.junit.Before;


import java.time.LocalDateTime;


import static org.junit.Assert.*;

/**
 * JUnit Test case for Event Class with high mutation coverage.
 */
public class EventTest {

  private Event timedEvent;
  private Event allDayEvent;
  private Event otherTimedEvent;

  @Before
  public void setUp() {
    // Event with start and end time
    timedEvent = new Event("Meeting",
        LocalDateTime.of(2025, 3, 10, 10, 0),
        LocalDateTime.of(2025, 3, 10, 11, 0),
        "Project discussion", "Room A", true);

    // All-day event (no end time)
    allDayEvent = new Event("Holiday",
        LocalDateTime.of(2025, 3, 15, 0, 0),
        null,
        "National holiday", "City", false);

    // Another timed event for conflict testing
    otherTimedEvent = new Event("Workshop",
        LocalDateTime.of(2025, 3, 10, 10, 30),
        LocalDateTime.of(2025, 3, 10, 11, 30),
        "Technical workshop", "Room B", true);
  }

  @Test
  public void testTimedEventConstructor() {
    assertEquals("Meeting", timedEvent.getTitle());
    assertEquals(LocalDateTime.of(2025, 3, 10, 10, 0), timedEvent.getStart());
    assertEquals(LocalDateTime.of(2025, 3, 10, 11, 0), timedEvent.getEnd());
    assertEquals("Project discussion", timedEvent.getDescription());
    assertEquals("Room A", timedEvent.getLocation());
    assertTrue(timedEvent.isPublic());
  }

  @Test
  public void testAllDayEventConstructor() {
    assertEquals("Holiday", allDayEvent.getTitle());
    assertEquals(LocalDateTime.of(2025, 3, 15, 0, 0), allDayEvent.getStart());
    assertNull("End should be null for all-day event", allDayEvent.getEnd());
    assertEquals("National holiday", allDayEvent.getDescription());
    assertEquals("City", allDayEvent.getLocation());
    assertFalse(allDayEvent.isPublic());
  }

  @Test
  public void testSetTitle() {
    timedEvent.setTitle("Updated Meeting");
    assertEquals("Updated Meeting", timedEvent.getTitle());
  }

  @Test
  public void testSetStart() {
    LocalDateTime newStart = LocalDateTime.of(2025, 3, 10, 9, 30);
    timedEvent.setStart(newStart);
    assertEquals(newStart, timedEvent.getStart());
  }

  @Test
  public void testSetEnd() {
    LocalDateTime newEnd = LocalDateTime.of(2025, 3, 10, 11, 30);
    timedEvent.setEnd(newEnd);
    assertEquals(newEnd, timedEvent.getEnd());
  }

  @Test
  public void testSetEndForAllDayEvent() {
    // Convert all-day event to timed event
    LocalDateTime newEnd = LocalDateTime.of(2025, 3, 15, 23, 59);
    allDayEvent.setEnd(newEnd);
    assertEquals(newEnd, allDayEvent.getEnd());
  }

  @Test
  public void testSetDescription() {
    timedEvent.setDescription("Updated discussion");
    assertEquals("Updated discussion", timedEvent.getDescription());
  }

  @Test
  public void testSetLocation() {
    timedEvent.setLocation("Conference Room");
    assertEquals("Conference Room", timedEvent.getLocation());
  }

  @Test
  public void testSetPublic() {
    timedEvent.setPublic(false);
    assertFalse(timedEvent.isPublic());

    timedEvent.setPublic(true);
    assertTrue(timedEvent.isPublic());
  }

  @Test
  public void testConflictsWithOverlapping() {
    // timedEvent: 2025-03-10 10:00-11:00
    // otherTimedEvent: 2025-03-10 10:30-11:30
    assertTrue("Events with overlapping times should conflict",
        timedEvent.conflictsWith(otherTimedEvent));

    // Test reverse direction
    assertTrue("Conflict should be detected in both directions",
        otherTimedEvent.conflictsWith(timedEvent));
  }

  @Test
  public void testConflictsWithNonOverlapping() {
    // Create a non-overlapping event
    Event nonOverlappingEvent = new Event("Later Meeting",
        LocalDateTime.of(2025, 3, 10, 13, 0),
        LocalDateTime.of(2025, 3, 10, 14, 0),
        "Later discussion", "Room C", true);

    assertFalse("Non-overlapping events should not conflict",
        timedEvent.conflictsWith(nonOverlappingEvent));

    // Test reverse direction
    assertFalse("Non-conflict should be detected in both directions",
        nonOverlappingEvent.conflictsWith(timedEvent));
  }

  @Test
  public void testConflictsWithAdjacentEvents() {
    // Event that ends exactly when timedEvent starts
    Event justBeforeEvent = new Event("Before Meeting",
        LocalDateTime.of(2025, 3, 10, 9, 0),
        LocalDateTime.of(2025, 3, 10, 10, 0),
        "Earlier discussion", "Room D", true);

    // Event that starts exactly when timedEvent ends
    Event justAfterEvent = new Event("After Meeting",
        LocalDateTime.of(2025, 3, 10, 11, 0),
        LocalDateTime.of(2025, 3, 10, 12, 0),
        "Later discussion", "Room E", true);

    // Adjacent events should not conflict per the implementation
    assertFalse("Events that touch but don't overlap should not conflict",
        timedEvent.conflictsWith(justBeforeEvent));
    assertFalse("Events that touch but don't overlap should not conflict",
        timedEvent.conflictsWith(justAfterEvent));
  }

  @Test
  public void testConflictsWithAllDayEvent() {
    // Create an event on the same day as the all-day event
    Event sameDay = new Event("Same Day Event",
        LocalDateTime.of(2025, 3, 15, 10, 0),
        LocalDateTime.of(2025, 3, 15, 11, 0),
        "During all-day event", "Room F", true);

    // Test if the implementation considers all-day events to conflict
    // with regular events on the same day
    boolean conflicts = allDayEvent.conflictsWith(sameDay);

    // Note: This assertion depends on your implementation's behavior
    // If all-day events can coexist with timed events, this should be false
    // If all-day events block the entire day, this should be true
    // Adjust the expected result based on your implementation
    assertTrue("All-day event should conflict with events on same day", conflicts);

    // Test the reverse
    assertTrue("Events on same day should conflict with all-day event",
        sameDay.conflictsWith(allDayEvent));
  }

  @Test
  public void testConflictsWithAllDayEvents() {
    // Another all-day event on the same day
    Event anotherAllDay = new Event("Another Holiday",
        LocalDateTime.of(2025, 3, 15, 0, 0),
        null,
        "Another all-day event", "Everywhere", true);

    // Test if two all-day events on the same day conflict
    boolean conflicts = allDayEvent.conflictsWith(anotherAllDay);

    // This depends on your implementation - adjust as needed
    assertTrue("Two all-day events on same day should conflict", conflicts);
  }

  @Test
  public void testConflictsWithDifferentDays() {
    // Create an event on a different day
    Event differentDay = new Event("Different Day",
        LocalDateTime.of(2025, 3, 16, 10, 0),
        LocalDateTime.of(2025, 3, 16, 11, 0),
        "Day after all-day", "Room G", true);

    assertFalse("Events on different days should not conflict",
        allDayEvent.conflictsWith(differentDay));
    assertFalse("Events on different days should not conflict",
        differentDay.conflictsWith(allDayEvent));
  }

  @Test
  public void testToString() {
    String timedEventString = timedEvent.toString();

    // Check that toString contains key information
    assertTrue("toString should contain event title",
        timedEventString.contains("Meeting"));
    assertTrue("toString should contain event start time",
        timedEventString.contains(timedEvent.getStart().toString()));
    assertTrue("toString should contain event end time",
        timedEventString.contains(timedEvent.getEnd().toString()));
    assertTrue("toString should contain location",
        timedEventString.contains("Room A"));

    // Check all-day event toString
    String allDayString = allDayEvent.toString();
    assertTrue("All-day event toString should indicate it's all day",
        allDayString.contains("All Day"));
    assertTrue("All-day event toString should contain location",
        allDayString.contains("City"));
  }

  @Test
  public void testConflictsWithSelf() {
    // An event should conflict with itself
    assertTrue("An event should conflict with itself",
        timedEvent.conflictsWith(timedEvent));
  }

  @Test
  public void testToStringWithNullLocation() {
    // Create an event with null location
    Event nullLocationEvent = new Event("No Location",
        LocalDateTime.of(2025, 3, 20, 10, 0),
        LocalDateTime.of(2025, 3, 20, 11, 0),
        "Event with null location", null, true);

    String result = nullLocationEvent.toString();

    // Should not contain "Location: null"
    assertFalse("toString should not include location when null",
        result.contains("Location: null"));

    // But should still contain other details
    assertTrue("toString should still contain event title",
        result.contains("No Location"));
  }

  @Test
  public void testToStringWithEmptyLocation() {
    // Create an event with empty location
    Event emptyLocationEvent = new Event("Empty Location",
        LocalDateTime.of(2025, 3, 20, 10, 0),
        LocalDateTime.of(2025, 3, 20, 11, 0),
        "Event with empty location", "", true);

    String result = emptyLocationEvent.toString();

    // Should not contain "Location:"
    assertFalse("toString should not include location section when empty",
        result.contains("Location:"));

    // But should still contain other details
    assertTrue("toString should still contain event title",
        result.contains("Empty Location"));
  }

  @Test
  public void testConflictsWithEventExactBoundaries() {
    // Event that exactly spans timedEvent
    Event exactSameEvent = new Event("Exact Same Time",
        LocalDateTime.of(2025, 3, 10, 10, 0), // Same start
        LocalDateTime.of(2025, 3, 10, 11, 0), // Same end
        "Same time frame", "Room H", false);

    assertTrue("Events with identical time boundaries should conflict",
        timedEvent.conflictsWith(exactSameEvent));
  }

  @Test
  public void testConflictsWithContainedEvent() {
    // Event entirely contained within timedEvent
    Event containedEvent = new Event("Contained Event",
        LocalDateTime.of(2025, 3, 10, 10, 15),
        LocalDateTime.of(2025, 3, 10, 10, 45),
        "Inside another event", "Room I", true);

    assertTrue("Contained event should conflict with container event",
        timedEvent.conflictsWith(containedEvent));
    assertTrue("Container event should conflict with contained event",
        containedEvent.conflictsWith(timedEvent));
  }
}