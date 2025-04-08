package view;

import java.time.LocalDate;
import java.time.LocalTime;

public class EventDetails implements IDetails {

  private String eventName;
  private String description;
  private String location;
  private boolean isPublic;
  private LocalTime startTime;
  private LocalTime endTime;
  private LocalDate date;

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

  public String returnTest() {
    String s = eventName + " " + description + " " + location + " " + isPublic
        + " " + startTime.toString() + " " + endTime + " " + date.toString();
    return s;
  }

  @Override
  public String getName() {
    return this.eventName;
  }

  public String getDescription() {
    return this.description;
  }

  public String getLocation() {
    return this.location;
  }

  public Boolean getIsPublic() {
    return this.isPublic;
  }

  public LocalTime getStartTime() {
    return this.startTime;
  }

  public LocalTime getEndTime() {
    return this.endTime;
  }

  public LocalDate getDate() {
    return this.date;
  }
}
