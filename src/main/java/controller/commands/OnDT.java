package controller.commands;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * This class represents a event on a certain DateTime or Date.
 */
public class OnDT extends IDateTimeArguments {

  LocalDateTime dt;
  LocalDate d;

  /**
   * Constructor method to create an on date. Will initialize a datetime and a date.
   * @param input - Date from user input.
   */
  protected OnDT(String input) {
    this.dt = parseInputToLocalDT(input);
    this.d = this.dt.toLocalDate();
  }

  /**
   * Method to return the event's LocalDateTime.
   * @return - Returns DateTime.
   */
  protected LocalDateTime getDt() {
    return this.dt;
  }

  /**
   * Method to return aa LocalDate.
   * @return - Returns only the date part.
   */
  protected LocalDate getD() {
    return this.d;
  }
}
