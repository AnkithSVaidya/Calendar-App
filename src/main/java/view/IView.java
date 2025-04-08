package view;

import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import controller.IController;
import model.Calendar;

public interface IView {

  void makeVisible();

  void setCommandButtonListener(ActionListener actionEvent);

  String getCalendarCommand();

  List<String> getCalendarCommandList();
  void setCalendarCommandList(List<String> commandList);

  void showErrorMessage(String error);

  void refresh();

  void setCalendars(Map<String, Calendar> calMap, String currentCal);

  void setActiveCalendarEvents();

  String getActiveCalendar();

  LocalDate getActiveDate();

  public void createCalendarPopup(ActionListener listener);

  void showDayPopup(LocalDate date, ActionListener listener);

  void showCreateEventPopup(LocalDate date, ActionListener listener);


}
