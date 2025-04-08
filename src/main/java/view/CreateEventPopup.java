package view;

import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

public class CreateEventPopup implements IButtonPopups {
  private JPanel createCalPanel;
  private List<String> commandList;
  private JPanel createSingleEventPanel;

  CreateEventPopup(IView view, JFrame mainFrame, LocalDate date) {
    commandList = new ArrayList<>();

    JTextField eventNameField = new JTextField(10);
    JTextField fromDTField = new JTextField(10);
    JTextField toDTField = new JTextField(10);

    createSingleEventPanel = new JPanel();
    createSingleEventPanel.setLayout(new GridLayout(0, 1));
    JLabel instructions = new JLabel("Please input datetimes in <dateStringTtimeString> format.");
    createSingleEventPanel.add(instructions);

    createSingleEventPanel.add(new JLabel("Event Name:"));
    createSingleEventPanel.add(eventNameField);
    createSingleEventPanel.add(new JLabel("From Time:"));
    createSingleEventPanel.add(fromDTField);
    createSingleEventPanel.add(new JLabel("To Time:"));
    createSingleEventPanel.add(toDTField);

    int result = JOptionPane.showConfirmDialog(mainFrame, createSingleEventPanel,
        "Create New Event", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

    if (result == JOptionPane.OK_OPTION) {
      String name = eventNameField.getText();
      String fromTime = fromDTField.getText();
      String toTime = toDTField.getText();

      if (name.isEmpty() || fromTime.isEmpty() || toTime.isEmpty()) {
        commandList.add("close");
        view.setCalendarCommandList(commandList);
        JOptionPane.showMessageDialog(mainFrame, "Please fill out all fields");
      }
      else {
        commandList.add("create_event");
        commandList.add(name);
        commandList.add(fromTime);
        commandList.add(toTime);
        commandList.add(date.toString());

        view.setCalendarCommandList(commandList);

        JOptionPane.showMessageDialog(mainFrame, "Creating Event " + name +
            " from " + fromTime + " to " + toTime);
      }
    }
    else {
      commandList.add("close");
      view.setCalendarCommandList(commandList);
    }
  }

  @Override
  public void setCommandButtonListener(ActionListener actionEvent) {

  }

  @Override
  public List<String> getCalendarCommandList() {
    return List.of();
  }
}