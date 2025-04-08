package view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.List;

import javax.swing.*;

public class DayPopup extends JDialog implements IButtonPopups {

  LocalDate date;
  JButton createEventButton;

  DayPopup(IView view, LocalDate date, JFrame mainFrame) {
    this.date = date;

    JFrame popup = new JFrame("New Popup Window");
    popup.setSize(300, 150);
    popup.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // just closes this popup
    popup.setLayout(new FlowLayout());

    createEventButton = new JButton("Create Event");
    createEventButton.setActionCommand("Create Event");

    JLabel label = new JLabel("This is a new window! " + date.toString());
    JButton closeBtn = new JButton("Close");
    closeBtn.addActionListener(e -> popup.dispose());

    popup.add(label);
    popup.add(createEventButton);
    popup.add(closeBtn);
    popup.setVisible(true);
  }

  @Override
  public void setCommandButtonListener(ActionListener actionEvent) {
    createEventButton.addActionListener(actionEvent);
  }


  @Override
  public List<String> getCalendarCommandList() {
    return List.of();
  }
}
