package view;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * This interface represents a collection of text information for an event. Used to store text data
 * to show in the UI.
 */
public interface IDetails {

  /**
   * Method to print a formatted string of the event's details.
   * @return - Returns a formatted string.
   */
  String getDetailsPrintable();

  /**
   * Getter for event name.
   * @return - String name.
   */
  String getName();

  /**
   * Getter for description.
   * @return - String description.
   */
  String getDescription();

  /**
   * Getter for location.
   * @return - String location.
   */
  String getLocation();

  /**
   * Getter for isPublic.
   * @return - Returns boolean is public.
   */
  Boolean getIsPublic();

  /**
   * Getter for start time.
   * @return - Returns localtime start time.
   */
  LocalTime getStartTime();

  /**
   * Getter for end time.
   * @return - Returns localtime end time.
   */
  LocalTime getEndTime();

  /**
   * Getter for event date.
   * @return - Returns localdate date.
   */
  LocalDate getDate();
}
