package view;

import java.awt.event.ActionListener;
import java.util.Map;

import model.Calendar;

public interface IView {

  void makeVisible();

  void setCommandButtonListener(ActionListener actionEvent);

  String getCalendarCommand();

  void showErrorMessage(String error);

  void refresh();

  void setCalendars(Map<String, Calendar> calMap);
}
