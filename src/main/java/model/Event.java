package model;

import java.time.LocalDateTime;
import java.time.temporal.Temporal;

/**
 * Represents a concrete event with a specific time range or an all-day event.
 * Extends {@link AbstractEvent} to provide end-time management capabilities.
 */
public class Event extends AbstractEvent {
  private Temporal end;

  /**
   * Constructs a timed event with specified start and end times.
   *
   * @param title       the title of the event
   * @param start       the start date and time of the event
   * @param end         the end date and time of the event
   * @param description a description of the event
   * @param location    where the event will take place
   * @param isPublic    {@code true} if the event is publicly visible
   */
  public Event(String title,
               LocalDateTime start,
               LocalDateTime end,
               String description,
               String location,
               boolean isPublic) {
    super(title, start, description, location, isPublic);
    this.end = end;
  }

  /**
   * Constructs an all-day event (no specified end time).
   *
   * @param title       the title of the event
   * @param start       the start date (time component is ignored for all-day events)
   * @param description a description of the event
   * @param location    where the event will take place
   * @param isPublic    {@code true} if the event is publicly visible
   */
  public Event(String title,
               LocalDateTime start,
               String description,
               String location,
               boolean isPublic) {
    super(title, start, description, location, isPublic);
    this.end = null;
  }

  /**
   * {@inheritDoc}
   * @return the end time of the event, or {@code null} for all-day events
   */
  @Override
  public LocalDateTime getEnd() {
    return (LocalDateTime) end;
  }

  /**
   * Updates the end time of the event.
   * Can be used to convert an all-day event to a timed event by setting a specific end time.
   *
   * @param end the new end date and time for the event
   */
  public void setEnd(LocalDateTime end) {

    this.end = end;
  }
}
