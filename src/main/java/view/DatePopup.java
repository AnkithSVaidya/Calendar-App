package view;

import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.swing.*;

public class DatePopup implements IButtonPopups {

  LocalDate date;

  DatePopup(IView view, LocalDate date, JFrame mainFrame) {
    this.date = date;

  }


  @Override
  public void setCommandButtonListener(ActionListener actionEvent) {

  }

  @Override
  public List<String> getCalendarCommandList() {
    return List.of();
  }
}
