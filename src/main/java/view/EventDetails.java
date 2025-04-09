package view;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * This class represents a collection of details for an event. Just text items for showing in the
 * UI.
 */
public class EventDetails implements IDetails {

  private String eventName;
  private String description;
  private String location;
  private boolean isPublic;
  private LocalTime startTime;
  private LocalTime endTime;
  private LocalDate date;

  /**
   * Conctructor for EventDetails.
   * @param name - Name of event.
   * @param description - Description of event.
   * @param location - Location of event.
   * @param isPublic - Is public or not.
   * @param startTime - Start time of event.
   * @param endTime - End time of event.
   * @param date - The date of the event.
   */
  public EventDetails(String name, String description, String location, boolean isPublic,
                      LocalTime startTime, LocalTime endTime, LocalDate date) {
    this.eventName = name;
    this.description = description;
    this.location = location;
    this.isPublic = isPublic;
    this.startTime = startTime;
    this.endTime = endTime;
    this.date = date;
  }

  /**
   * Method to print the event details for the UI.
   * @return - Returns a formatted string of event details.
   */
  @Override
  public String getDetailsPrintable() {
    String timeDisplay = (endTime == null) ? "All Day" : startTime + " - " + endTime;

    String details = "-" + eventName + " " + timeDisplay + " Loc: " + location
        + " Desc: " + description + System.lineSeparator();

    return details;
  }

  @Override
  public String getName() {
    return this.eventName;
  }

  @Override
  public String getDescription() {
    return this.description;
  }

  @Override
  public String getLocation() {
    return this.location;
  }

  @Override
  public Boolean getIsPublic() {
    return this.isPublic;
  }

  @Override
  public LocalTime getStartTime() {
    return this.startTime;
  }

  @Override
  public LocalTime getEndTime() {
    return this.endTime;
  }

  @Override
  public LocalDate getDate() {
    return this.date;
  }
}
