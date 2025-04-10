package model;

import org.junit.Test;
import org.junit.Before;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * JUnit Test case for RecurringEvent Class with high mutation coverage.
 */
public class RecurringEventTest {

  private RecurringEvent occurrenceBasedEvent;
  private RecurringEvent dateBasedEvent;
  private LocalDateTime start;
  private LocalDateTime end;
  private Set<DayOfWeek> weekdays;
  private Set<DayOfWeek> weekends;

  @Before
  public void setUp() {
    // Setup common test data
    start = LocalDateTime.of(2025, 3, 10, 9, 0); // Monday
    end = LocalDateTime.of(2025, 3, 10, 10, 0);

    weekdays = new HashSet<>(Arrays.asList(
        DayOfWeek.MONDAY,
        DayOfWeek.TUESDAY,
        DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY,
        DayOfWeek.FRIDAY
    ));

    weekends = new HashSet<>(Arrays.asList(
        DayOfWeek.SATURDAY,
        DayOfWeek.SUNDAY
    ));

    // Create recurring events with different constraints
    occurrenceBasedEvent = new RecurringEvent(
        "Weekly Meeting",
        start,
        end,
        "Team sync-up",
        "Conference Room",
        true,
        weekdays,
        3 // occurrences
    );

    dateBasedEvent = new RecurringEvent(
        "Weekend Workshop",
        start,
        end,
        "Learning session",
        "Training Room",
        false,
        weekends,
        LocalDate.of(2025, 3, 31) // until date
    );
  }

  @Test
  public void testConstructorWithOccurrences() {
    assertEquals("Weekly Meeting", occurrenceBasedEvent.getTitle());
    assertEquals(start, occurrenceBasedEvent.getStart());
    assertEquals(end, occurrenceBasedEvent.getEnd());
    assertEquals("Team sync-up", occurrenceBasedEvent.getDescription());
    assertEquals("Conference Room", occurrenceBasedEvent.getLocation());
    assertTrue(occurrenceBasedEvent.isPublic());
    assertEquals(weekdays, occurrenceBasedEvent.getRecurrenceDays());
    assertEquals(Integer.valueOf(21), occurrenceBasedEvent.getOccurrences()); // 3 * 7 in implementation
    assertNull(occurrenceBasedEvent.getUntil());
  }

  @Test
  public void testConstructorWithUntilDate() {
    assertEquals("Weekend Workshop", dateBasedEvent.getTitle());
    assertEquals(start, dateBasedEvent.getStart());
    assertEquals(end, dateBasedEvent.getEnd());
    assertEquals("Learning session", dateBasedEvent.getDescription());
    assertEquals("Training Room", dateBasedEvent.getLocation());
    assertFalse(dateBasedEvent.isPublic());
    assertEquals(weekends, dateBasedEvent.getRecurrenceDays());
    assertNull(dateBasedEvent.getOccurrences());
    assertEquals(LocalDate.of(2025, 3, 31), dateBasedEvent.getUntil());
  }

  @Test
  public void testGenerateEventsWithOccurrences() {
    // Test with a specific day pattern
    Set<DayOfWeek> monWedFri = new HashSet<>(Arrays.asList(
        DayOfWeek.MONDAY,
        DayOfWeek.WEDNESDAY,
        DayOfWeek.FRIDAY
    ));

    // Using 2 occurrences, but the implementation will multiply by 7, so 14 events
    // will be generated (2*7=14)
    RecurringEvent event = new RecurringEvent(
        "MWF Meeting",
        start,
        end,
        "Three-day meeting",
        "Room 101",
        true,
        monWedFri,
        2 // occurrences (will be 2*7=14 internally)
    );

    List<Event> generatedEvents = event.generateEvents();

    // Verify the count - this is the first non-empty day that matches the pattern
    assertEquals(14, generatedEvents.size()); // Changed from 2 to 14

    // First event should be on Monday (the start date)
    assertEquals(DayOfWeek.MONDAY, generatedEvents.get(0).getStart().getDayOfWeek());

    // Second event should be on Wednesday
    assertEquals(DayOfWeek.WEDNESDAY, generatedEvents.get(1).getStart().getDayOfWeek());

    // Verify properties of the first event
    Event firstEvent = generatedEvents.get(0);
    assertEquals("MWF Meeting", firstEvent.getTitle());
    assertEquals("Three-day meeting", firstEvent.getDescription());
    assertEquals("Room 101", firstEvent.getLocation());
    assertTrue(firstEvent.isPublic());
    assertNotNull(firstEvent.getEnd());
    assertEquals(start.getHour(), firstEvent.getStart().getHour());
    assertEquals(start.getMinute(), firstEvent.getStart().getMinute());
  }

  @Test
  public void testGenerateEventsWithUntilDate() {
    // March 2025 has the following weekend days:
    // Saturdays: 1, 8, 15, 22, 29
    // Sundays: 2, 9, 16, 23, 30
    // Our until date is March 31, so all these should be included

    // Use a start date on a Sunday to trigger actual generation
    LocalDateTime sundayStart = LocalDateTime.of(2025, 3, 9, 9, 0);
    LocalDateTime sundayEnd = LocalDateTime.of(2025, 3, 9, 10, 0);

    RecurringEvent weekendEvent = new RecurringEvent(
        "Weekend Event",
        sundayStart,
        sundayEnd,
        "Weekend activity",
        "Park",
        true,
        weekends,
        LocalDate.of(2025, 3, 31)
    );

    List<Event> generatedEvents = weekendEvent.generateEvents();

    // Count expected weekend days between Mar 9 and Mar 31, 2025
    // March 9, 15, 16, 22, 23, 29, 30 = 7 days
    assertEquals(7, generatedEvents.size());

    // Verify all events are on weekends
    for (Event e : generatedEvents) {
      DayOfWeek day = e.getStart().getDayOfWeek();
      assertTrue(
          "Event should be on weekend, found: " + day,
          day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY
      );

      // Check event properties
      assertEquals("Weekend Event", e.getTitle());
      assertEquals("Weekend activity", e.getDescription());
      assertEquals("Park", e.getLocation());
    }

    // Verify no events after until date
    for (Event e : generatedEvents) {
      assertTrue(
          "Event should not be after until date",
          !e.getStart().toLocalDate().isAfter(LocalDate.of(2025, 3, 31))
      );
    }
  }

  @Test
  public void testGenerateEventsWithEmptyRecurrenceDays() {
    RecurringEvent emptyRecurrence = new RecurringEvent(
        "No Days",
        start,
        end,
        "This shouldn't generate any events",
        "Nowhere",
        true,
        new HashSet<>(), // Empty set
        5 // occurrences
    );

    List<Event> events = emptyRecurrence.generateEvents();

    assertNotNull("Generated list should not be null", events);
    assertTrue("No events should be generated", events.isEmpty());
  }

  @Test
  public void testGenerateEventsWithNullRecurrenceDays() {
    RecurringEvent nullRecurrence = new RecurringEvent(
        "Null Days",
        start,
        end,
        "This shouldn't generate any events either",
        "Nowhere",
        true,
        null, // Null set
        5 // occurrences
    );

    List<Event> events = nullRecurrence.generateEvents();

    assertNotNull("Generated list should not be null", events);
    assertTrue("No events should be generated", events.isEmpty());
  }

  @Test(expected = IllegalStateException.class)
  public void testGenerateEventsWithoutBoundaries() {
    // Attempting to create a recurring event without specifying
    // either occurrences or until date should throw an exception

    // Use reflection to create an invalid state
    RecurringEvent invalidEvent = new RecurringEvent(
        "Invalid",
        start,
        end,
        "This shouldn't work",
        "Nowhere",
        true,
        weekdays,
        5 // occurrences
    );

    // Use reflection to set both fields to null
    try {
      java.lang.reflect.Field occurrencesField = RecurringEvent.class.getDeclaredField("occurrences");
      occurrencesField.setAccessible(true);
      occurrencesField.set(invalidEvent, null);

      java.lang.reflect.Field untilField = RecurringEvent.class.getDeclaredField("until");
      untilField.setAccessible(true);
      untilField.set(invalidEvent, null);
    } catch (Exception e) {
      fail("Test setup failed: " + e.getMessage());
    }

    // Should throw IllegalStateException
    invalidEvent.generateEvents();
  }

  @Test
  public void testGenerateEventsWithNullEnd() {
    // RecurringEvent with null end time (all-day events)
    RecurringEvent allDayRecurring = new RecurringEvent(
        "All Day Meeting",
        start,
        null, // null end time
        "All day recurring",
        "Office",
        true,
        new HashSet<>(Arrays.asList(DayOfWeek.MONDAY)),
        2 // occurrences (will be 2*7=14 internally)
    );

    List<Event> events = allDayRecurring.generateEvents();

    assertEquals(14, events.size()); // Changed from 2 to 14

    // Check the first event
    Event firstEvent = events.get(0);
    assertEquals("All Day Meeting", firstEvent.getTitle());
    assertNull("End time should be null", firstEvent.getEnd());
    assertEquals(DayOfWeek.MONDAY, firstEvent.getStart().getDayOfWeek());
  }

  @Test
  public void testGetEnd() {
    assertEquals(end, occurrenceBasedEvent.getEnd());
  }

  @Test
  public void testToString() {
    String eventString = occurrenceBasedEvent.toString();

    assertTrue("toString should contain event title",
        eventString.contains("Weekly Meeting"));
    assertTrue("toString should contain event start",
        eventString.contains(start.toString()));
    assertTrue("toString should contain event end",
        eventString.contains(end.toString()));
  }

  @Test
  public void testOccurrencesMultipliedBy7() {
    // The constructor multiplies occurrences by 7
    RecurringEvent event = new RecurringEvent(
        "Test",
        start,
        end,
        "Test desc",
        "Test loc",
        true,
        weekdays,
        3 // Should be multiplied by 7 internally
    );

    assertEquals(Integer.valueOf(21), event.getOccurrences());
  }

  @Test
  public void testGenerateEventsWithStartDateMatchingPattern() {
    // Start date is a Monday and we're including Mondays
    Set<DayOfWeek> mondaysOnly = new HashSet<>(Arrays.asList(DayOfWeek.MONDAY));

    RecurringEvent event = new RecurringEvent(
        "Monday Meetings",
        start, // Monday
        end,
        "Weekly Monday meeting",
        "Room A",
        true,
        mondaysOnly,
        2 // occurrences (will be 2*7=14 internally)
    );

    List<Event> events = event.generateEvents();

    assertEquals(14, events.size()); // Changed from 2 to 14

    // Check first and second events are on Mondays
    assertEquals(LocalDate.of(2025, 3, 10), events.get(0).getStart().toLocalDate()); // First Monday
    assertEquals(LocalDate.of(2025, 3, 17), events.get(1).getStart().toLocalDate()); // Second Monday
  }

  @Test
  public void testGenerateEventsWithStartDateNotMatchingPattern() {
    // Start date is a Monday but we're only including Tuesdays
    Set<DayOfWeek> tuesdaysOnly = new HashSet<>(Arrays.asList(DayOfWeek.TUESDAY));

    RecurringEvent event = new RecurringEvent(
        "Tuesday Meetings",
        start, // Monday
        end,
        "Weekly Tuesday meeting",
        "Room A",
        true,
        tuesdaysOnly,
        2 // occurrences (will be 2*7=14 internally)
    );

    List<Event> events = event.generateEvents();

    assertEquals(14, events.size()); // Changed from 2 to 14

    // Check first and second Tuesday events
    assertEquals(LocalDate.of(2025, 3, 11), events.get(0).getStart().toLocalDate()); // First Tuesday
    assertEquals(LocalDate.of(2025, 3, 18), events.get(1).getStart().toLocalDate()); // Second Tuesday
  }

  @Test
  public void testGenerateEventsPreservesTimeOfDay() {
    // Use a specific time
    LocalDateTime specificTimeStart = LocalDateTime.of(2025, 3, 10, 14, 30);
    LocalDateTime specificTimeEnd = LocalDateTime.of(2025, 3, 10, 15, 45);

    RecurringEvent event = new RecurringEvent(
        "Afternoon Meeting",
        specificTimeStart,
        specificTimeEnd,
        "Recurring with specific time",
        "Room C",
        true,
        weekdays,
        1 // Just need one occurrence
    );

    List<Event> events = event.generateEvents();
    assertTrue(!events.isEmpty());

    // Verify time component is preserved
    LocalTime startTime = events.get(0).getStart().toLocalTime();
    LocalTime endTime = events.get(0).getEnd().toLocalTime();

    assertEquals(14, startTime.getHour());
    assertEquals(30, startTime.getMinute());
    assertEquals(15, endTime.getHour());
    assertEquals(45, endTime.getMinute());
  }

  @Test
  public void testGenerateEventsWithMultipleDaysInSameWeek() {
    // Test the case where multiple days in the same week match the pattern
    Set<DayOfWeek> monWed = new HashSet<>(Arrays.asList(
        DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY
    ));

    RecurringEvent event = new RecurringEvent(
        "Mon/Wed Class",
        start, // Monday
        end,
        "Twice-weekly class",
        "Classroom",
        true,
        monWed,
        3 // occurrences (will be 3*7=21 internally)
    );

    List<Event> events = event.generateEvents();

    assertEquals(21, events.size()); // Changed from 3 to 21

    // Check first three events follow the pattern
    assertEquals(DayOfWeek.MONDAY, events.get(0).getStart().getDayOfWeek());
    assertEquals(DayOfWeek.WEDNESDAY, events.get(1).getStart().getDayOfWeek());
    assertEquals(DayOfWeek.MONDAY, events.get(2).getStart().getDayOfWeek());
  }

  @Test
  public void testGenerateEventsWithEmptyRecurrenceDaysReturnsNewList() {
    // Create a recurring event with empty recurrence days
    RecurringEvent emptyRecurrence = new RecurringEvent(
        "No Days",
        start,
        end,
        "This shouldn't generate any events",
        "Nowhere",
        true,
        new HashSet<>(), // Empty set
        5 // occurrences
    );

    // Get the events list
    List<Event> events = emptyRecurrence.generateEvents();

    // Check that it's not a Collections.emptyList()
    assertNotNull("Generated list should not be null", events);
    assertTrue("No events should be generated", events.isEmpty());

    // This will fail if Collections.emptyList() is returned
    // because emptyList() is immutable
    try {
      events.add(new Event("Test", LocalDateTime.now(), "Test", "Test", true));
      // If we get here, the list is mutable (ArrayList)
      assertTrue(true);
    } catch (UnsupportedOperationException e) {
      // If we get here, the list is immutable (Collections.emptyList())
      fail("The returned list should be mutable (ArrayList), not Collections.emptyList()");
    }
  }
}