package controller.commands;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.Arrays;
import java.util.List;

/**
 * Abstract class for DateTime arguments.
 */
public abstract class IDateTimeArguments {

  /**
   * Gets DateTime for either OnDT class or FromDT class.
   * @return
   */
  protected LocalDateTime getDt() {
    return null;
  }

  /**
   * Gets LocalDate from implementing classes.
   * @return
   */
  protected LocalDate getD() {
    return null;
  }

  static List<DateTimeFormatter> formatters = Arrays.asList(
      DateTimeFormatter.ISO_LOCAL_DATE_TIME,
      DateTimeFormatter.ISO_LOCAL_DATE
  );

  /**
   * Turns a Date formatted dateString and turns it into a LocalDateTime.
   * @param input - The user input string.
   * @return - Returns the parsed LocalDateTime.
   * @throws DateTimeParseException - Throws an exception if unable to parse.
   */
  protected static LocalDateTime parseInputToLocalDT(String input) throws DateTimeParseException {

    // Loop through each type of format and check if the input matches. If the input doesn't match,
    // then throw error.
    for (DateTimeFormatter formatter : formatters) {
      try {

        // Create a new formatter. If the input is only a date, will set the time to default 0
        DateTimeFormatter f = new DateTimeFormatterBuilder()
            .append(formatter)
            .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
            .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
            .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
            .toFormatter();

        return LocalDateTime.parse(input, f);
      } catch (DateTimeParseException ignored) {
      }
    }

    throw new DateTimeParseException("Could not parse datetime.", input, 0);
  }


}
