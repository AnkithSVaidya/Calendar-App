package view;

import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import model.Calendar;

public interface IView {

  void makeVisible();

  void setCommandButtonListener(ActionListener actionEvent);

  String getCalendarCommand();

  List<String> getCalendarCommandList();

  void showErrorMessage(String error);

  void refresh();

  void setCalendars(Map<String, Calendar> calMap, String currentCal);

  String getActiveCalendar();

  LocalDate getActiveDate();
}
