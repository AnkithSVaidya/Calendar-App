package controller.commands;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents an event's datetime range.
 */
public class FromDTToDT extends IDateTimeArguments {

  LocalDateTime fromDT;
  LocalDateTime toDT;

  /**
   * Constructor method to initialize the start and end datetimes.
   * @param fromDT - Start date in dateStringTtimeString format.
   * @param toDT - End date in dateStringTtimeString format.
   */
  protected FromDTToDT(String fromDT, String toDT) {
    this.fromDT = LocalDateTime.parse(fromDT);
    this.toDT = LocalDateTime.parse(toDT);

    if (!isValidRange()) {
      throw new DateTimeException("Illegal range input.");
    }
  }

  /**
   * Method to return the datetime range as an array.
   * @return - An array where start is at index 0 and end is at index 1.
   */
  protected List<LocalDateTime> getDateTimeRange() {
    List<LocalDateTime> dateTimes = new ArrayList<>();
    dateTimes.add(this.fromDT);
    dateTimes.add(this.toDT);
    return dateTimes;
  }

  /**
   * Method to validate that the range is valid.
   * @return - Boolean true if valid range, false if not.
   */
  private boolean isValidRange() {
    return this.fromDT.isBefore(this.toDT);
  }
}
