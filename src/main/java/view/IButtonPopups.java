package view;


import java.awt.event.ActionListener;
import java.util.List;

public interface IButtonPopups {

  void setCommandButtonListener(ActionListener actionEvent);

  List<String> getCalendarCommandList();

}
