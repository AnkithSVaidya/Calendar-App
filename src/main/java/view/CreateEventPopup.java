package view;

import java.awt.GridLayout;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

public class CreateEventPopup extends JFrame {
  private List<String> commandList;
  private JPanel createSingleEventPanel;

  CreateEventPopup(IView view, JFrame mainFrame, LocalDate date) {
    commandList = new ArrayList<>();

    JTextField eventNameField = new JTextField(10);
    JTextField fromDTField = new JTextField(10);
    JTextField toDTField = new JTextField(10);
    JTextField descField = new JTextField(10);
    JTextField locField = new JTextField(10);
    JCheckBox isPublicField = new JCheckBox();

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
    createSingleEventPanel.add(new JLabel("Description:"));
    createSingleEventPanel.add(descField);
    createSingleEventPanel.add(new JLabel("Location:"));
    createSingleEventPanel.add(locField);
    createSingleEventPanel.add(new JLabel("Public:"));
    createSingleEventPanel.add(isPublicField);

    int result = JOptionPane.showConfirmDialog(mainFrame, createSingleEventPanel,
        "Create New Event", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

    if (result == JOptionPane.OK_OPTION) {
      String name = eventNameField.getText();
      String fromTime = fromDTField.getText();
      String toTime = toDTField.getText();
      String desc = descField.getText();
      String loc = locField.getText();
      String isPublicString = Boolean.toString(isPublicField.isSelected());

      if (name.isEmpty() || fromTime.isEmpty() || toTime.isEmpty()) {
        commandList.add("close");
        view.setCalendarCommandList(commandList);
        JOptionPane.showMessageDialog(mainFrame, "Please fill out all fields");
      }
      else {
        commandList.add("create_event"); // 0
        commandList.add(name); // 1
        commandList.add(fromTime); // 2
        commandList.add(toTime); // 3
        commandList.add(date.toString()); // 4
        commandList.add(desc); // 5
        commandList.add(loc); // 6
        commandList.add(isPublicString); // 7

        view.setCalendarCommandList(commandList);
      }
    }
    else {
      commandList.add("close");
      view.setCalendarCommandList(commandList);
    }
  }
}