package model;

import java.time.LocalDateTime;
import java.time.temporal.Temporal;

/**
 * Represents an abstract event with common properties like title,
 * start time, description, location, and visibility.
 * Concrete subclasses must implement specific
 * event behaviors (e.g., recurring events).
 */
public abstract class AbstractEvent {
  protected String title;
  protected Temporal start;
  protected String description;
  protected String location;
  protected boolean isPublic;

  /**
   * Constructs an AbstractEvent with the specified details.
   *
   * @param title       the title of the event
   * @param start       the start time of the event
   * @param description a description of the event
   * @param location    the location of the event
   * @param isPublic    whether the event is publicly visible
   */
  public AbstractEvent(String title,
                       LocalDateTime start,
                       String description,
                       String location,
                       boolean isPublic) {
    this.title = title;
    this.start = start;
    this.description = description;
    this.location = location;
    this.isPublic = isPublic;
  }

  /**
   * Returns the title of the event.
   *
   * @return the title of the event
   */
  public String getTitle() {
    return title;
  }

  /**
   * Returns the start date and time of the event.
   *
   * @return the start time of the event
   */
  public LocalDateTime getStart() {
    return (LocalDateTime) start;
  }

  /**
   * Returns the description of the event.
   *
   * @return the event description
   */
  public String getDescription() {

    return description;
  }

  /**
   * Returns the location where the event will take place.
   *
   * @return the event location
   */
  public String getLocation() {

    return location;
  }

  /**
   * Indicates whether the event is public or private.
   *
   * @return {@code true} if the event is public, {@code false} otherwise
   */
  public boolean isPublic() {

    return isPublic;
  }

  /**
   * Sets the title of the event.
   *
   * @param title the new title for the event
   */
  public void setTitle(String title) {

    this.title = title;
  }

  /**
   * Sets the start date and time of the event.
   *
   * @param start the new start time for the event
   */
  public void setStart(LocalDateTime start) {
    this.start = start;
  }

  /**
   * Sets the description of the event.
   *
   * @param description the new description for the event
   */
  public void setDescription(String description) {

    this.description = description;
  }

  /**
   * Sets the location where the event will take place.
   *
   * @param location the new location for the event
   */
  public void setLocation(String location) {

    this.location = location;
  }

  /**
   * Sets the visibility of the event to public or private.
   *
   * @param isPublic {@code true} to make the event public, {@code false} to make it private
   */
  public void setPublic(boolean isPublic) {

    this.isPublic = isPublic;
  }

  /**
   * Returns the end time of the event. Concrete subclasses must implement this method
   * to define the specific end time logic (e.g., recurring events, all-day events).
   *
   * @return the end time of the event, or {@code null} for all-day events
   */
  public abstract LocalDateTime getEnd();

  /**
   * Checks whether this event conflicts with another event.
   * Conflict happens when the first event starts before the second event ends and
   * the second event starts before the first event ends.
   * If an event has a null end time, it is treated as an all-day event (spanning one day).
   */
  public boolean conflictsWith(AbstractEvent other) {
    LocalDateTime otherStart = (LocalDateTime) other.start;
    LocalDateTime thisStart = (LocalDateTime) this.start;

    // Get the end times, treating all-day events as ending at the end of their day
    LocalDateTime end1 = this.getEnd() != null ? this.getEnd() : thisStart.plusDays(1).withHour(0).withMinute(0).withSecond(0);
    LocalDateTime end2 = other.getEnd() != null ? other.getEnd() : otherStart.plusDays(1).withHour(0).withMinute(0).withSecond(0);

    // Events conflict if one starts before the other ends
    return thisStart.isBefore(end2) && otherStart.isBefore(end1);
  }

  /**
   * Returns a string representation of the event in the format:
   * <pre>
   * "Event: {title} at {start} [to {end} | (All Day)] [, Location: {location}]"
   * </pre>
   * - "{to {end}" is included if the event has a defined end time.
   * - "{ (All Day)" is shown if the event has no end time (all-day event).
   * - "Location: {location}" is included if the location is non-null and non-empty.
   *
   * @return a formatted string describing the event
   */
  @Override
  public String toString() {
    String info = "Event: " + title + " at " + start;
    if (getEnd() != null) {
      info += " to " + getEnd();
    } else {
      info += " (All Day)";
    }
    if (location != null && !location.isEmpty()) {
      info += ", Location: " + location;
    }
    return info;
  }
}
