package model;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.time.ZoneId;
import java.util.List;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * JUnit Test case for CalendarManager Class.
 */
public class CalendarManagerTest {
  private CalendarManager manager;
  private Event event1;
  private Event event2;

  @Before
  public void setUp() {
    manager = new CalendarManager();
    event1 = new Event("Meeting",
        LocalDateTime.of(2025, 3, 10, 10, 0),
        LocalDateTime.of(2025, 3, 10, 11, 0),
        "Project discussion", "Room A", true);
    event2 = new Event("Lunch",
        LocalDateTime.of(2025, 3, 10, 12, 0),
        LocalDateTime.of(2025, 3, 10, 13, 0),
        "Team lunch", "Cafeteria", false);
  }

  @Test
  public void testCreateCalendarSuccessfully() {
    assertTrue(manager.createCalendar("Work", "America/New_York"));
    assertNotNull(manager.getCalendar("Work"));
  }

  @Test
  public void testCreateCalendarFailsForDuplicateName() {
    assertTrue(manager.createCalendar("Work", "America/New_York"));
    assertFalse(manager.createCalendar("Work", "America/Los_Angeles"));
  }

  @Test
  public void testCreateCalendarFailsForInvalidTimezone() {
    assertFalse(manager.createCalendar("Work", "Invalid/Timezone"));
  }

  @Test
  public void testEditCalendarNameSuccessfully() {
    manager.createCalendar("Work", "America/New_York");
    assertTrue(manager.editCalendar("Work", "name", "Office"));
    assertNull(manager.getCalendar("Work"));
    assertNotNull(manager.getCalendar("Office"));
  }

  @Test
  public void testEditCalendarNameFailsForDuplicateName() {
    manager.createCalendar("Work", "America/New_York");
    manager.createCalendar("Office", "America/Chicago");
    assertFalse(manager.editCalendar("Work", "name", "Office"));
  }

  @Test
  public void testEditCalendarFailsForNonExistingCalendar() {
    assertFalse(manager.editCalendar("NonExistent", "name", "NewName"));
  }

  @Test
  public void testEditCalendarTimezoneSuccessfully() {
    manager.createCalendar("Work", "America/New_York");
    assertTrue(manager.editCalendar("Work", "timezone", "Europe/London"));
  }

  @Test
  public void testEditCalendarFailsForInvalidTimezone() {
    manager.createCalendar("Work", "America/New_York");
    assertFalse(manager.editCalendar("Work", "timezone", "Invalid/Zone"));
  }

  @Test
  public void testEditCalendarInvalidProperty() {
    manager.createCalendar("Work", "America/New_York");
    // Passing an unrecognized property should return false.
    assertFalse(manager.editCalendar("Work", "color", "Blue"));
  }


  @Test
  public void testEditCalendarUpdatesCurrentCalendar() {
    manager.createCalendar("Work", "America/New_York");
    manager.useCalendar("Work");
    assertTrue(manager.editCalendar("Work", "name", "Office"));
    // Check that the old name is removed and new name exists.
    assertNull(manager.getCalendar("Work"));
    Calendar updated = (Calendar) manager.getCalendar("Office");
    assertNotNull(updated);
    // Verify that the current calendar is the one with the new name.
    assertSame(updated, manager.getCurrentCalendar());
  }


  @Test
  public void testUseCalendarSuccessfully() {
    manager.createCalendar("Work", "America/New_York");
    assertTrue(manager.useCalendar("Work"));
    assertNotNull(manager.getCurrentCalendar());
  }

  @Test
  public void testUseCalendarFailsForNonExistingCalendar() {
    assertFalse(manager.useCalendar("NonExistent"));
  }

  @Test
  public void testGetCalendarReturnsNullForNonExistingCalendar() {
    assertNull(manager.getCalendar("NonExistent"));
  }

  @Test
  public void testGetAllCalendars() {
    manager.createCalendar("Work", "America/New_York");
    manager.createCalendar("Personal", "Asia/Kolkata");
    Collection<Calendar> calendars = manager.getAllCalendars();
    assertEquals(2, calendars.size());
  }

  @Test
  public void testCopyEventFailsWhenNoCurrentCalendar() {
    manager.createCalendar("Work", "America/New_York");
    assertFalse(manager.copyEvent("Meeting", event1.getStart(), "Work", event1.getStart()));
  }

  @Test
  public void testCopyEventFailsForNonExistingTargetCalendar() {
    manager.createCalendar("Work", "America/New_York");
    manager.useCalendar("Work");
    assertFalse(manager.copyEvent("Meeting", event1.getStart(), "NonExistent", event1.getStart()));
  }

  @Test
  public void testCopyEventFailsForNonExistingEvent() {
    manager.createCalendar("Work", "America/New_York");
    manager.createCalendar("Personal", "Asia/Kolkata");
    manager.useCalendar("Work");
    assertFalse(manager.copyEvent("FakeEvent", event1.getStart(), "Personal", event1.getStart()));
  }

  @Test
  public void testCopyEventSuccessfully() {
    manager.createCalendar("Work", "America/New_York");
    manager.createCalendar("Personal", "Asia/Kolkata");
    manager.useCalendar("Work");

    manager.getCurrentCalendar().addEvent(event1, false);
    assertTrue(manager.copyEvent("Meeting", event1.getStart(), "Personal", event1.getStart()));

    Calendar targetCal = (Calendar) manager.getCalendar("Personal");
    assertNotNull(targetCal);
    assertEquals(1, targetCal.getEventsOnDate(event1.getStart().toLocalDate()).size());
  }

  @Test
  public void testCopyEventFailsDueToConflict() {
    manager.createCalendar("Source", "America/New_York");
    manager.createCalendar("Target", "America/New_York");
    manager.useCalendar("Source");

    // Add event to source calendar.
    manager.getCurrentCalendar().addEvent(event1, false);
    // In target calendar, add an event at the time where the copied event would start.
    Calendar targetCal = (Calendar) manager.getCalendar("Target");
    Event conflictEvent = new Event("Conflict", event1.getStart(),
        event1.getStart().plusHours(1), "Conflict", "Room X", true);
    targetCal.addEvent(conflictEvent, false);

    // Copy should fail due to conflict.
    assertFalse(manager.copyEvent("Meeting", event1.getStart(), "Target", event1.getStart()));
  }

  @Test
  public void testCopyEventsOnFailsDueToAllConflicts() {
    manager.createCalendar("Source", "America/New_York");
    manager.createCalendar("Target", "America/New_York");
    manager.useCalendar("Source");

    // Add two events on the same date in the source calendar.
    manager.getCurrentCalendar().addEvent(event1, false);
    manager.getCurrentCalendar().addEvent(event2, false);

    // In target calendar, add events that conflict with both events.
    Calendar targetCal = (Calendar) manager.getCalendar("Target");
    LocalDate targetDate = event1.getStart().toLocalDate();
    Event conflict1 = new Event("Conflict1",
        LocalDateTime.of(targetDate,
            event1.getStart().toLocalTime()),
        LocalDateTime.of(targetDate,
            event1.getStart().toLocalTime().plusHours(1)),
        "Conflict",
        "Room X",
        true);
    Event conflict2 = new Event("Conflict2",
        LocalDateTime.of(targetDate, event2.getStart().toLocalTime()),
        LocalDateTime.of(targetDate,
            event2.getStart().toLocalTime().plusHours(1)),
        "Conflict", "Room X", true);
    targetCal.addEvent(conflict1, false);
    targetCal.addEvent(conflict2, false);

    // Attempting to copy should result in no events being copied.
    assertFalse(manager.copyEventsOn(event1.getStart().toLocalDate(),
        "Target", targetDate));
  }

  @Test
  public void testCopyEventsBetweenFailsWhenNoEvents() {
    manager.createCalendar("Source", "America/New_York");
    manager.createCalendar("Target", "America/New_York");
    manager.useCalendar("Source");

    // Do not add any events to source.
    assertFalse(manager.copyEventsBetween(
        LocalDate.of(2025, 3, 10),
        LocalDate.of(2025, 3, 11),
        "Target",
        LocalDate.of(2025, 3, 12)
    ));
  }

  @Test
  public void testCopyEventsOnDateSuccessfully() {
    manager.createCalendar("Work", "America/New_York");
    manager.createCalendar("Personal", "Asia/Kolkata");
    manager.useCalendar("Work");

    manager.getCurrentCalendar().addEvent(event1, false);
    manager.getCurrentCalendar().addEvent(event2, false);

    assertTrue(manager.copyEventsOn(event1.getStart().toLocalDate(),
        "Personal",
        event1.getStart().toLocalDate()));

    Calendar targetCal = (Calendar) manager.getCalendar("Personal");
    assertNotNull(targetCal);
    assertEquals(2,
        targetCal.getEventsOnDate(event1.getStart().toLocalDate()).size());
  }

  @Test
  public void testCopyEventsOnDateFailsWhenNoEvents() {
    manager.createCalendar("Work", "America/New_York");
    manager.createCalendar("Personal", "Asia/Kolkata");
    manager.useCalendar("Work");

    assertFalse(manager.copyEventsOn(LocalDate.of(2025,
            3,
            15),
        "Personal",
        LocalDate.of(2025, 3, 15)));
  }

  @Test
  public void testCopyEventsBetweenSuccessfully() {
    manager.createCalendar("Work", "America/New_York");
    manager.createCalendar("Personal", "Asia/Kolkata");
    manager.useCalendar("Work");

    manager.getCurrentCalendar().addEvent(event1, false);
    manager.getCurrentCalendar().addEvent(event2, false);

    assertTrue(manager.copyEventsBetween(
        LocalDate.of(2025, 3, 10),
        LocalDate.of(2025, 3, 11),
        "Personal",
        LocalDate.of(2025, 3, 12)
    ));

    Calendar targetCal = (Calendar) manager.getCalendar("Personal");
    assertNotNull(targetCal);
    assertEquals(2, targetCal.getEventsOnDate(
        LocalDate.of(2025,
            3,
            12)).size());
  }

  @Test
  public void testEditCalendarTimezone() {
    manager.createCalendar("Default", "America/New_York");
    manager.useCalendar("Default");

    ICalendar cal = manager.getCurrentCalendar();
    cal.addEvent(event1, false);

    // Change timezone.
    boolean result = manager.editCalendar("Default",
        "timezone",
        "Asia/Kolkata");
    assertTrue(result);


    List<Event> l = cal.getAllEventsList();
    assertEquals(1, l.size());
  }

  @Test
  public void testEditCalendarTimezone2() {
    manager.createCalendar("Default", "America/New_York");
    manager.useCalendar("Default");

    ICalendar cal = manager.getCurrentCalendar();
    cal.addEvent(event1, false);

    // Change timezone.
    boolean result = manager.editCalendar("Default",
        "timezone",
        "Asia/Kolkata");
    assertTrue(result);

    List<Event> l = cal.getAllEventsList();
    Event e1 = l.get(0);

    assertEquals(LocalDateTime.of(2025, 3, 10, 19, 30), e1.getStart());
    assertEquals(LocalDateTime.of(2025, 3, 10, 20, 30), e1.getEnd());
  }

  @Test
  public void testEditCalendarInvalidPropertyFails() {
    manager.createCalendar("Default", "America/New_York");
    manager.useCalendar("Default");

    // Attempt to edit using a property that is not supported
    boolean result = manager.editCalendar("Default",
        "invalidProperty",
        "SomeValue");
    assertFalse("Editing should fail for an unsupported property", result);

    // Verify that the calendar is still accessible under the same name
    assertNotNull("Calendar 'Default' should still exist",
        manager.getCalendar("Default"));
  }

  @Test
  public void testEditCalendarNameToExistingCalendarFails() {
    manager.createCalendar("Calendar1", "America/New_York");
    manager.createCalendar("Calendar2", "America/Los_Angeles");

    // Set Calendar1 as in-use
    manager.useCalendar("Calendar1");

    // Try renaming Calendar1 to "Calendar2" (which already exists)
    boolean result = manager.editCalendar("Calendar1",
        "name",
        "Calendar2");
    assertFalse("Renaming to an already existing name should fail", result);

    // Verify that Calendar2 still exists (unchanged)
    assertNotNull("Calendar2 should still exist",
        manager.getCalendar("Calendar2"));

    // Verify that Calendar1 still exists under its original name.
    assertNotNull("Calendar1 should still exist with its original name",
        manager.getCalendar("Calendar1"));
  }


  @Test
  public void testUseCalendarFailsForNullInput() {
    assertFalse(manager.useCalendar(null));
  }


  @Test
  public void testGetCurrentCalendarReturnsNullWhenNotSet() {
    assertNull(manager.getCurrentCalendar());
  }

  @Test
  public void testCopyEventFailsDueToTargetConflict() {
    manager.createCalendar("Source", "America/New_York");
    manager.createCalendar("Target", "America/New_York");
    manager.useCalendar("Source");

    // Add event to source calendar
    manager.getCurrentCalendar().addEvent(event1, false);

    // Add conflicting event to target calendar
    Calendar targetCal = (Calendar) manager.getCalendar("Target");
    Event conflictEvent = new Event(
        "Conflict",
        event1.getStart(),
        event1.getEnd(),
        "Conflict Description",
        "Room X",
        true
    );
    targetCal.addEvent(conflictEvent, false);

    // Attempt to copy should fail due to conflict
    assertFalse(manager.copyEvent(event1.getTitle(),
        event1.getStart(),
        "Target", event1.getStart()));
  }

  @Test
  public void testCopyEventsBetweenFailsForNonExistingTargetCalendar() {
    manager.createCalendar("Source", "America/New_York");
    manager.useCalendar("Source");

    assertFalse(manager.copyEventsBetween(
        LocalDate.of(2025, 3, 10),
        LocalDate.of(2025, 3, 11),
        "NonExistent",
        LocalDate.of(2025, 3, 12)
    ));
  }


  @Test
  public void testCopyEventsOnFailsForNonExistingTargetCalendar() {
    manager.createCalendar("Source", "America/New_York");
    manager.useCalendar("Source");

    assertFalse(manager.copyEventsOn(
        LocalDate.of(2025, 3, 10),
        "NonExistent",
        LocalDate.of(2025, 3, 10)
    ));
  }

  @Test
  public void testCopyEventsOnWhenNoCurrentCalendarExists() {
    // We need a fresh manager where no calendar has been set as current
    CalendarManager freshManager = new CalendarManager();

    // Try to copy events when no current calendar exists
    boolean result = freshManager.copyEventsOn(
        LocalDate.of(2025, 3, 10),
        "TargetCalendar",
        LocalDate.of(2025, 3, 15)
    );

    // This should fail because currentCalendar is null
    assertFalse("Should fail when no current calendar exists", result);
  }


  @Test
  public void testCopyEventsBetweenWhenNoCurrentCalendarExists() {
    // We need a fresh manager where no calendar has been set as current
    CalendarManager freshManager = new CalendarManager();

    // Try to copy events when no current calendar exists
    boolean result = freshManager.copyEventsBetween(
        LocalDate.of(2025, 3, 10),
        LocalDate.of(2025, 3, 12),
        "TargetCalendar",
        LocalDate.of(2025, 3, 15)
    );

    // This should fail because currentCalendar is null
    assertFalse("Should fail when no current calendar exists", result);
  }


  @Test
  public void testCopyEventWhenNoCurrentCalendarExists() {
    // We need a fresh manager where no calendar has been set as current
    CalendarManager freshManager = new CalendarManager();

    // Try to copy an event when no current calendar exists
    boolean result = freshManager.copyEvent(
        "MeetingTitle",
        LocalDateTime.of(2025, 3, 10, 10, 0),
        "TargetCalendar",
        LocalDateTime.of(2025, 3, 15, 10, 0)
    );

    // This should fail because currentCalendar is null
    assertFalse("Should fail when no current calendar exists", result);
  }

  @Test
  public void testEditCalendarNamePersistsInternallyAfterRename() {
    // Create a calendar
    manager.createCalendar("Work", "America/New_York");

    // Edit the calendar name
    assertTrue(manager.editCalendar("Work", "name", "Office"));

    // Try using the calendar with its new name
    assertTrue(manager.useCalendar("Office"));

    // Add an event to verify the renamed calendar functions correctly
    Event testEvent = new Event("Test Meeting",
        LocalDateTime.of(2025, 3, 10, 10, 0),
        LocalDateTime.of(2025, 3, 10, 11, 0),
        "Testing renamed calendar", "Room A", true);
    manager.getCurrentCalendar().addEvent(testEvent, false);

    // Verify the event exists in the renamed calendar
    assertEquals(1, manager.getCurrentCalendar().getEventsOnDate(
        LocalDate.of(
            2025,
            3,
            10)).size());

    // Try retrieving the event by title to verify calendar functionality
    LocalDateTime eventStart = LocalDateTime.of(
        2025,
        3,
        10,
        10,
        0);
    AbstractEvent foundEvent = null;
    for (AbstractEvent event :
        manager.getCurrentCalendar().getEventsBetween(eventStart, eventStart)) {
      if (event.getTitle().equals("Test Meeting")) {
        foundEvent = event;
        break;
      }
    }
    assertNotNull("Event should be found in renamed calendar", foundEvent);
  }


  @Test
  public void testEditCurrentCalendarNamePreservesCalendarFunctionality() {
    // Create a calendar and set it as current
    manager.createCalendar("Work", "America/New_York");
    manager.useCalendar("Work");

    // Add an event to the calendar before renaming
    Event firstEvent = new Event("First Meeting",
        LocalDateTime.of(2025, 3, 10, 10, 0),
        LocalDateTime.of(2025, 3, 10, 11, 0),
        "Before rename", "Room A", true);
    manager.getCurrentCalendar().addEvent(firstEvent, false);

    // Store a reference to verify it's the same object later
    ICalendar calendarBeforeRename = manager.getCurrentCalendar();

    // Edit the calendar name
    assertTrue(manager.editCalendar("Work", "name", "Office"));

    // Verify we're still using the same calendar object
    assertSame(calendarBeforeRename, manager.getCurrentCalendar());

    // Verify the previously added event still exists in the current calendar
    assertEquals(1, manager.getCurrentCalendar().
        getEventsOnDate(
            LocalDate.of(
                2025,
                3,
                10)).size());

    // Add another event after renaming to verify functionality
    Event secondEvent = new Event("Second Meeting",
        LocalDateTime.of(2025, 3, 10, 14, 0),
        LocalDateTime.of(2025, 3, 10, 15, 0),
        "After rename", "Room B", true);
    manager.getCurrentCalendar().addEvent(secondEvent, false);

    // Verify both events exist in the current calendar
    assertEquals(2, manager.getCurrentCalendar().
        getEventsOnDate(LocalDate.of(2025,
            3,
            10)).size());
  }


  @Test
  public void testEditNonCurrentCalendarDoesNotAffectCurrentCalendar() {
    // Create two calendars
    manager.createCalendar("Work", "America/New_York");
    manager.createCalendar("Personal", "America/Los_Angeles");

    // Set Personal as the current calendar
    manager.useCalendar("Personal");
    ICalendar personalCalendar = manager.getCurrentCalendar();

    // Add an event to the current calendar
    Event personalEvent = new Event("Personal Meeting",
        LocalDateTime.of(2025, 3, 10, 10, 0),
        LocalDateTime.of(2025, 3, 10, 11, 0),
        "Personal calendar event", "Home", true);
    manager.getCurrentCalendar().addEvent(personalEvent, false);

    // Edit the Work calendar (not the current one)
    assertTrue(manager.editCalendar("Work", "name", "Office"));

    // Verify the current calendar is still Personal
    assertSame(personalCalendar, manager.getCurrentCalendar());

    // Verify the event still exists in the current calendar
    assertEquals(1, manager.getCurrentCalendar().getEventsOnDate(
        LocalDate.of(
            2025,
            3,
            10)).size());
  }


  @Test
  public void testEditCalendarNameUpdatesInternally() {
    // Create a calendar
    manager.createCalendar("Work", "America/New_York");

    // Edit the calendar's name
    assertTrue(manager.editCalendar("Work", "name", "Office"));

    // Verify the new name exists in the calendars map
    assertNotNull(manager.getCalendar("Office"));

    // Verify the old name no longer exists
    assertNull(manager.getCalendar("Work"));

    // Verify that the calendar object retrieved by the new name has the correct name internally
    Calendar updatedCalendar = (Calendar) manager.getCalendar("Office");
    assertEquals("Office", updatedCalendar.getName());
  }


  @Test
  public void testEditCalendarTimezoneUpdatesInternally() {
    // Create a calendar
    manager.createCalendar("Work", "America/New_York");

    // Edit the calendar's timezone
    assertTrue(manager.editCalendar("Work",
        "timezone",
        "Europe/London"));

    // Verify that the calendar object retrieved has the correct timezone internally
    Calendar updatedCalendar = (Calendar) manager.getCalendar("Work");

    // Use ZoneId.of to create the expected object for comparison
    // Or alternatively, convert both to strings for comparison
    assertEquals(ZoneId.of("Europe/London"), updatedCalendar.getTimezone());
  }


}
