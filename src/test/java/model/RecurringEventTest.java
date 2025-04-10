package model;

import org.junit.Test;

import java.lang.reflect.Field;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * JUnit Test case for RecurringEvent Class.
 */
public class RecurringEventTest {


  @Test
  public void testGenerateEventsWithOccurrences() {
    Set<DayOfWeek> days = new HashSet<>();
    days.add(DayOfWeek.MONDAY);
    days.add(DayOfWeek.WEDNESDAY);

    // Starting on a Monday
    LocalDateTime start = LocalDateTime.of(2025, 3, 10, 9, 0);
    LocalDateTime end = LocalDateTime.of(2025, 3, 10, 10, 0);

    RecurringEvent recurring = new RecurringEvent("Class", start, end,
        "Weekly class", "Room 101", true, days, 3);
    List<Event> events = recurring.generateEvents();

    // Expect exactly 3 events on days Monday or Wednesday
    assertEquals(3, events.size());
    for (Event event : events) {
      assertTrue(days.contains(event.getStart().getDayOfWeek()));
      // Each event should start and end on the same day.
      // (When end is non-null, the event is built using the two-argument constructor for end.)
      assertEquals(event.getStart().toLocalDate(), event.getEnd().toLocalDate());
    }
  }

  @Test
  public void testGenerateEventsWithUntil() {
    Set<DayOfWeek> days = new HashSet<>();
    days.add(DayOfWeek.FRIDAY);

    // Starting on a day that is a Friday (per test comment)
    LocalDateTime start = LocalDateTime.of(2025, 3, 6, 16, 0);
    LocalDateTime end = LocalDateTime.of(2025, 3, 6, 17, 0);
    LocalDate untilDate = LocalDate.of(2025, 3, 20);

    RecurringEvent recurring = new RecurringEvent("Yoga",
        start, end, "Evening Yoga", "Gym", false, days, untilDate);
    List<Event> events = recurring.generateEvents();

    // March 6 and March 13 are Fridays; March 20, 2025 is a Thursday so it should not be included.
    assertEquals(2, events.size());
    for (Event event : events) {
      assertEquals(DayOfWeek.FRIDAY, event.getStart().getDayOfWeek());
    }
  }

  @Test
  public void testGenerateEventsEmptyRecurrenceDays() {
    // Edge case: if no recurrence days are specified, no events should be generated.
    Set<DayOfWeek> days = new HashSet<>();
    LocalDateTime start = LocalDateTime.of(2025, 3, 10, 9, 0);
    LocalDateTime end = LocalDateTime.of(2025, 3, 10, 10, 0);
    RecurringEvent recurring = new RecurringEvent("Empty",
        start, end, "No days", "Nowhere",
        true, days, 3);
    List<Event> events = recurring.generateEvents();
    assertEquals(0, events.size());
  }


  @Test(expected = IllegalStateException.class)
  public void testGenerateEventsNoBoundaries() throws Exception {
    // Test that generateEvents throws an exception if both occurrences and until are null.
    Set<DayOfWeek> days = new HashSet<>();
    days.add(DayOfWeek.TUESDAY);

    LocalDateTime start = LocalDateTime.of(2025, 3, 11, 9, 0);
    LocalDateTime end = LocalDateTime.of(2025, 3, 11, 10, 0);
    // Use a constructor that sets a boundary (here, occurrences is provided)
    RecurringEvent recurring = new RecurringEvent(
        "Test No Boundaries",
        start, end,
        "No boundaries",
        "Room X",
        true,
        days,
        2
    );
    // Now force both occurrences and until to null using reflection.
    Field occField = RecurringEvent.class.getDeclaredField("occurrences");
    occField.setAccessible(true);
    occField.set(recurring, null);
    Field untilField = RecurringEvent.class.getDeclaredField("until");
    untilField.setAccessible(true);
    untilField.set(recurring, null);

    // This call should throw an IllegalStateException.
    recurring.generateEvents();
  }

  @Test
  public void testGenerateEventsWithNullRecurrenceDays() {
    // Test that if recurrenceDays is null then generateEvents returns an empty list.
    LocalDateTime start = LocalDateTime.of(2025, 3, 10, 9, 0);
    LocalDateTime end = LocalDateTime.of(2025, 3, 10, 10, 0);
    RecurringEvent recurring = new RecurringEvent("Null Days",
        start, end, "No recurrence days", "Nowhere",
        true, null, 3);
    List<Event> events = recurring.generateEvents();
    assertNotNull(events);
    assertTrue(events.isEmpty());
  }

  @Test
  public void testGenerateEventsWithNullEnd() {
    // Test the branch where end is null.
    // In this branch the event is created using the one-argument Event constructor.
    // (Assuming that in such cases, the Event's getEnd() returns null.)
    Set<DayOfWeek> days = new HashSet<>(Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.FRIDAY));
    // Starting on a Monday
    LocalDateTime start = LocalDateTime.of(2025, 3, 10, 9, 0);
    // Pass null for end
    RecurringEvent recurring = new RecurringEvent("No End",
        start, null, "Starts with no end",
        "Room 102", false, days, 2);
    List<Event> events = recurring.generateEvents();
    assertEquals(2, events.size());
    for (Event event : events) {
      assertTrue(days.contains(event.getStart().getDayOfWeek()));
      // For events created in the end==null branch, we expect getEnd() to be null.
      assertNull("Expected null end for event created with null end", event.getEnd());
    }
  }

  @Test
  public void testGettersOccurrences() {
    // Test the getters on a RecurringEvent defined using fixed occurrences.
    Set<DayOfWeek> days = new HashSet<>();
    days.add(DayOfWeek.TUESDAY);
    LocalDateTime start = LocalDateTime.of(2025, 3, 11, 8, 30);
    LocalDateTime end = LocalDateTime.of(2025, 3, 11, 9, 30);
    RecurringEvent recurring = new RecurringEvent("Getter Occurrences",
        start, end, "Recurring", "Hall",
        true, days, 5);
    assertEquals(end, recurring.getEnd());
    assertEquals(days, recurring.getRecurrenceDays());
    assertEquals(Integer.valueOf(5), recurring.getOccurrences());
    assertNull("Expected until to be null for fixed occurrences event",
        recurring.getUntil());
  }

  @Test
  public void testGettersUntil() {
    // Test the getters on a RecurringEvent defined using an end date.
    Set<DayOfWeek> days = new HashSet<>();
    days.add(DayOfWeek.SATURDAY);
    LocalDateTime start = LocalDateTime.of(2025, 3, 14, 18, 0);
    LocalDateTime end = LocalDateTime.of(2025, 3, 14, 19, 0);
    LocalDate untilDate = LocalDate.of(2025, 4, 1);
    RecurringEvent recurring = new RecurringEvent("Getter Until",
        start, end, "Weekend event", "Park",
        false, days, untilDate);
    assertEquals(end, recurring.getEnd());
    assertEquals(days, recurring.getRecurrenceDays());
    assertNull("Expected occurrences to be null for until-based event",
        recurring.getOccurrences());
    assertEquals(untilDate, recurring.getUntil());
  }

  @Test
  public void testGenerateEventsReturnsModifiableEmptyList() {
    // Test that the empty list returned when recurrenceDays is null is modifiable
    // (i.e., it's the ArrayList initialized in the method, not Collections.emptyList())
    LocalDateTime start = LocalDateTime.of(2025, 3, 10, 9, 0);
    LocalDateTime end = LocalDateTime.of(2025, 3, 10, 10, 0);
    RecurringEvent recurring = new RecurringEvent("Modifiable List",
        start,
        end,
        "Test modifiable",
        "Nowhere", true,
        null,
        3);
    List<Event> events = recurring.generateEvents();

    // This would throw UnsupportedOperationException if it were Collections.emptyList()
    try {
      Event dummyEvent = new Event("Test", start,
          "Description", "Location",
          true);
      events.add(dummyEvent);
      assertEquals(1, events.size());
    } catch (UnsupportedOperationException e) {
      fail("The returned list should be modifiable (ArrayList), not Collections.emptyList()");
    }
  }


}