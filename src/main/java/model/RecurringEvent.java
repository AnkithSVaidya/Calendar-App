package model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;


/**
 * Represents a recurring event that repeats on specified days of the week,
 * either for a fixed number of occurrences or until a specified end date.
 */
public class RecurringEvent extends AbstractEvent {
  // base end time (each instance must start and finish on the same day)
  private LocalDateTime end;
  // days of the week on which the event repeats
  private Set<DayOfWeek> recurrenceDays;
  // if non-null, limits the number of recurrences
  private Integer occurrences;
  // if non-null, recurrences continue until this date (inclusive)
  private LocalDate until;


  /**
   * Constructs a recurring event with a fixed number of occurrences.
   *
   * @param title          the title of the event
   * @param start          the start date and time of the first occurrence
   * @param end            the end date and time of individual occurrences
   * @param description    event description
   * @param location       event location
   * @param isPublic       visibility status
   * @param recurrenceDays set of days of the week when the event repeats
   * @param occurrences    maximum number of occurrences
   *                       (mutually exclusive with 'until')
   */
  public RecurringEvent(String title,
                        LocalDateTime start,
                        LocalDateTime end,
                        String description,
                        String location,
                        boolean isPublic,
                        Set<DayOfWeek> recurrenceDays,
                        int occurrences) {
    super(title, start, description, location, isPublic);
    this.end = end;
    this.recurrenceDays = recurrenceDays;
    this.occurrences = occurrences * 7;
    this.until = null;
  }

  /**
   * Constructs a recurring event that repeats until a specified end date.
   *
   * @param title          the title of the event
   * @param start          the start date and time of the first occurrence
   * @param end            the end date and time of individual occurrences
   * @param description    event description
   * @param location       event location
   * @param isPublic       visibility status
   * @param recurrenceDays set of days of the week when the event repeats
   * @param until          final date for occurrences (inclusive,
   *                       mutually exclusive with 'occurrences')
   */
  public RecurringEvent(String title,
                        LocalDateTime start,
                        LocalDateTime end,
                        String description,
                        String location,
                        boolean isPublic,
                        Set<DayOfWeek> recurrenceDays,
                        LocalDate until) {
    super(title, start, description, location, isPublic);
    this.end = end;
    this.recurrenceDays = recurrenceDays;
    this.until = until;
    this.occurrences = null;
  }

  /**
   * {@inheritDoc}
   * For recurring events, this returns the end time of <b>individual occurrences</b>.
   * If {@code null}, each generated event is treated as an all-day event.
   *
   * @return the end time of individual occurrences, or {@code null} for all-day events
   */

  @Override
  public LocalDateTime getEnd() {
    return end;
  }

  /**
   * Returns the days of the week when this event recurs.
   *
   * @return set of recurrence days
   */
  public Set<DayOfWeek> getRecurrenceDays() {
    return recurrenceDays;
  }

  /**
   * Returns the maximum number of occurrences if defined.
   *
   * @return number of occurrences, or {@code null} if using end date
   */
  public Integer getOccurrences() {
    return occurrences;
  }

  /**
   * Returns the end date for recurrences if defined.
   *
   * @return final recurrence date, or {@code null} if using occurrence count
   */
  public LocalDate getUntil() {
    return until;
  }

  /**
   * Generates concrete event instances based on recurrence rules.
   * Events are created for each matching recurrence day until either:
   * <ul>
   *   <li>The specified number of occurrences is reached</li>
   *   <li>The specified end date is passed</li>
   * </ul>
   *
   * @return list of generated event instances
   * @throws IllegalStateException if neither occurrences nor end date are specified
   */
  public List<Event> generateEvents() {

    // Ensuring that recurrence is bounded.
    if (occurrences == null && until == null) {
      throw new IllegalStateException("Recurring event must have "
          +
          "either a fixed number of occurrences or an end date.");
    }
    List<Event> events = new ArrayList<>();
    // If no recurrence days are specified, return an empty list immediately.
    if (recurrenceDays == null || recurrenceDays.isEmpty()) {
      return events;
    }
    LocalDateTime localStart = (LocalDateTime) start;
    LocalDate currentDate = localStart.toLocalDate();
    LocalTime currentTime = localStart.toLocalTime();
    int count = 0;
    while (true) {
      if (recurrenceDays.contains(currentDate.getDayOfWeek())) {

        LocalDateTime eventStart = LocalDateTime.of(currentDate, currentTime);

        if (end == null) {
          events.add(new Event(title, eventStart, description, location, isPublic));

        } else {
          LocalDateTime eventEnd = LocalDateTime.of(currentDate, end.toLocalTime());
          events.add(new Event(title, eventStart, eventEnd, description, location, isPublic));
        }


        count++;
        if (occurrences != null && count >= occurrences) {
          break;
        }
      }
      currentDate = currentDate.plusDays(1);
      if (until != null && currentDate.isAfter(until)) {
        break;
      }
    }
    return events;
  }

}
