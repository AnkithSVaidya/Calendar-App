package view;

import java.awt.event.ActionListener;
import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import model.Calendar;

/**
 * Interface that represents the view of the application. The view will initialize a calendar view.
 */
public interface IView {

  /**
   * Method to make the main frame visible
   */
  void makeVisible();

  /**
   * Method to set the command listeners.
   * @param actionEvent - The ActionListener, probably the controller.
   */
  void setCommandButtonListener(ActionListener actionEvent);

  /**
   * Method to get the current command list for the controller to read. List contains user inputs
   * and other important arguements for building stuff with the model.
   * @return - Returns a list of commands.
   */
  List<String> getCalendarCommandList();

  /**
   * Method to set the commands after a user has filled out a modal. Will reset beforehand.
   * @param commandList - A list of commands to set in the main class.
   */
  void setCalendarCommandList(List<String> commandList);

  void showErrorMessage(String error);

  /**
   * Method to refresh the frame.
   */
  void refresh();

  /**
   * Method to set the calendars from the controller. This method is supposed to be called after
   * every communication with the controller.
   * @param calMap - Map of Calendar Name,
   * @param currentCal
   */
  void setCalendars(Map<String, Calendar> calMap, String currentCal);

//  void setEvents(List<EventDetails> eventDetails, String currentCal);

  /**
   * Function to
   * @param eventDetails
   */
//  void setActiveCalendarEvents(List<EventDetails> eventDetails);

  String getActiveCalendar();

  LocalDate getActiveDate();

  void setActiveDateVarOnly(LocalDate date);

  /**
   * Method to show a create calendar popup on button click.
   * @param listener - Action listener.
   */
  void createCalendarPopup(ActionListener listener);

  /**
   * Method to show the main popup for other action buttons for a day.
   * @param date - The current selected day.
   * @param listener - Action listener.
   */
  void showDayPopup(LocalDate date, ActionListener listener);

  /**
   * Method to show create event popup on button click.
   * @param date - The current selected day.
   * @param listener - Action listener.
   */
  void showCreateEventPopup(LocalDate date, ActionListener listener);

  /**
   * Method to show create all day event popup on click.
   * @param date - The current day.
   * @param listener - Action listener.
   */
  void showCreateAllDayEventPopup(LocalDate date, ActionListener listener);

  /**
   * Method to show a recurring event popup on click.
   * @param date - The current day.
   * @param listener - Action listener.
   */
  void showRecurringEventPopup(LocalDate date, ActionListener listener);

  /**
   * Method to show edit event popup on click.
   * @param date - The current day.
   * @param listener - Action listener.
   */
  void showEditEventPopup(LocalDate date, ActionListener listener);

  File showImportPopup(ActionListener listener);

  String showExportPopup();

  /**
   * Method to show a edit recurring event popup.
   * @param date - The current day.
   * @param listener - Action listener.
   */
  void showEditRecurringEventPopup(LocalDate date, ActionListener listener);

  void setAllCalendarEvents(Map<String, List<EventDetails>> detailsPerMap);
  }
