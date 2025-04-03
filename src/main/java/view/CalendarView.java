package view;

import javax.swing.*;

public class CalendarView extends JFrame implements IView {


  public CalendarView() {
    super();
    this.setTitle("Calendar");
    this.setSize(500, 500);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }
}
