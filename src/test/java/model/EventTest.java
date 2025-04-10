package model;


import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.Before;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * JUnit Test case for Event Class.
 */
public class EventTest {

  private Event event1;
  private Event event2;
  private Event allDayEvent;

  @Before
  public void setUp() {
    // Event with defined start and end.
    LocalDateTime start1 = LocalDateTime.of(LocalDate.now(),
        LocalTime.of(10, 0));
    LocalDateTime end1 = LocalDateTime.of(LocalDate.now(),
        LocalTime.of(11, 0));
    event1 = new Event("Meeting", start1, end1,
        "Discuss project", "Room 1", true);

    // Overlapping event: starts at 10:30, ends at 11:30.
    LocalDateTime start2 = LocalDateTime.of(LocalDate.now(),
        LocalTime.of(10, 30));
    LocalDateTime end2 = LocalDateTime.of(LocalDate.now(),
        LocalTime.of(11, 30));
    event2 = new Event("Meeting", start2, end2,
        "Discuss project", "Room 1", true);

    // All-day event: using constructor that sets end to null.
    LocalDateTime startAllDay = LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT);
    allDayEvent = new Event("Holiday", startAllDay,
        "All day event", "Home", false);
  }

  @Test
  public void testGetters() {
    assertEquals("Meeting", event1.getTitle());
    assertNotNull(event1.getStart());
    assertNotNull(event1.getEnd());
    assertEquals("Discuss project", event1.getDescription());
    assertEquals("Room 1", event1.getLocation());
    assertTrue(event1.isPublic());
  }

  @Test
  public void testSetters() {
    event1.setTitle("Team Meeting");
    assertEquals("Team Meeting", event1.getTitle());
    LocalDateTime newEnd = event1.getEnd().plusMinutes(15);
    event1.setEnd(newEnd);
    assertEquals(newEnd, event1.getEnd());
  }

  @Test
  public void testConflictsWithOverlapping() {
    // event1: 10:00-11:00, event2: 10:30-11:30; they should conflict.
    assertTrue(event1.conflictsWith(event2));
  }

  @Test
  public void testConflictsWithNonOverlapping() {
    LocalDateTime start3 = LocalDateTime.of(LocalDate.now(),
        LocalTime.of(12, 0));
    LocalDateTime end3 = LocalDateTime.of(LocalDate.now(),
        LocalTime.of(13, 0));
    Event event3 = new Event("Lunch", start3, end3,
        "Team lunch", "Cafeteria", true);
    assertFalse(event1.conflictsWith(event3));
  }

  @Test
  public void testConflictsWithAllDay() {
    // Assume that an all-day event conflicts with any event on the same day.
    assertTrue(event1.conflictsWith(allDayEvent));
  }

  @Test
  public void testToStringWithLocationAndEndTime() {
    Event event = new Event(
        "Conference",
        LocalDateTime.of(2025, 3, 10, 9, 0),
        LocalDateTime.of(2025, 3, 10, 17, 0),
        "Tech Conference",
        "Hall A",
        true
    );
    assertTrue(event.toString().contains("Location: Hall A"));
    assertTrue(event.toString().contains("to 2025-03-10T17:00"));
  }

  @Test
  public void testToStringForAllDayEvent() {
    Event allDayEvent = new Event(
        "Holiday",
        LocalDateTime.of(2025, 3, 15, 0, 0),
        null, // All-day event
        "Public Holiday",
        "City",
        false
    );
    assertTrue(allDayEvent.toString().contains("(All Day)"));
  }

  @Test
  public void testToStringWithoutLocation() {
    Event event = new Event(
        "Meeting",
        LocalDateTime.of(2025, 3, 10, 10, 0),
        LocalDateTime.of(2025, 3, 10, 11, 0),
        "Team Sync",
        "", // Empty location
        true
    );
    assertFalse(event.toString().contains("Location:"));
  }

  @Test
  public void testConflictsWithNonOverlappingEvents() {
    Event event1 = new Event(
        "Event A",
        LocalDateTime.of(2025, 3, 10, 9, 0),
        LocalDateTime.of(2025, 3, 10, 10, 0),
        "Desc",
        "Room 1",
        true
    );
    Event event2 = new Event(
        "Event B",
        LocalDateTime.of(2025, 3, 10, 10, 0),
        LocalDateTime.of(2025, 3, 10, 11, 0),
        "Desc",
        "Room 1",
        true
    );
    assertFalse(event1.conflictsWith(event2));
  }


  @Test
  public void testToStringWithNullLocation() {
    Event event = new Event(
        "Workshop",
        LocalDateTime.of(2025, 3, 10, 14, 0),
        LocalDateTime.of(2025, 3, 10, 16, 0),
        "Workshop Desc",
        null, // Explicitly set location to null
        true
    );
    assertFalse(event.toString().contains("Location:"));
  }

  @Test
  public void testConflictsWithAdjacentAllDayEvent() {
    // Regular event: 10:00-11:00
    Event regularEvent = new Event(
        "Meeting",
        LocalDateTime.of(2025, 3, 10, 10, 0),
        LocalDateTime.of(2025, 3, 10, 11, 0),
        "Desc",
        "Room 1",
        true
    );

    // All-day event starts at midnight of the next day
    Event allDayEvent = new Event(
        "Holiday",
        LocalDateTime.of(2025, 3, 11, 0, 0), // Starts when regularEvent ends + 1 day
        null,
        "Desc",
        "City",
        false
    );

    assertFalse(regularEvent.conflictsWith(allDayEvent));
  }


}
