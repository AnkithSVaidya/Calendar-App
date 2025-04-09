package view;

import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.*;

public class CreateRecurringEventPopup extends JFrame {

  private JPanel createRecurringEventPanel;
  private List<String> commandList;


  CreateRecurringEventPopup(IView view, JFrame mainFrame, LocalDate date) {
    // create event --autoDecline <eventName> from <dateStringTtimeString>
    // to <dateStringTtimeString> repeats <weekdays> for <N> times
    commandList = new ArrayList<>();

    JTextField eventNameField = new JTextField(10);
    JTextField fromDTField = new JTextField(10);
    JTextField toDTField = new JTextField(10);
    JTextField nField = new JTextField(10);
    JTextField descField = new JTextField(10);
    JTextField locField = new JTextField(10);
    JCheckBox isPublicField = new JCheckBox();

    createRecurringEventPanel = new JPanel();
    createRecurringEventPanel.setLayout(new GridLayout(0, 1));
    JLabel instructions = new JLabel("Please input datetimes in hh:mm format.");
    createRecurringEventPanel.add(instructions);

    createRecurringEventPanel.add(new JLabel("Event Name:"));
    createRecurringEventPanel.add(eventNameField);
    createRecurringEventPanel.add(new JLabel("From Time:"));
    createRecurringEventPanel.add(fromDTField);
    createRecurringEventPanel.add(new JLabel("To Time:"));
    createRecurringEventPanel.add(toDTField);
    createRecurringEventPanel.add(new JLabel("Times:"));
    createRecurringEventPanel.add(nField);
    createRecurringEventPanel.add(new JLabel("Description:"));
    createRecurringEventPanel.add(descField);
    createRecurringEventPanel.add(new JLabel("Location:"));
    createRecurringEventPanel.add(locField);
    createRecurringEventPanel.add(new JLabel("Public:"));
    createRecurringEventPanel.add(isPublicField);


    JPanel weekdayPanel = new JPanel();
    weekdayPanel.setLayout(new GridLayout(0, 4));

    // MTWRFSU.
    String[] days = {"M", "T", "W", "R", "F", "S", "U"};
    Map<String, JCheckBox> dayCheckboxes = new HashMap<>();

    for (String day : days) {
      JCheckBox checkBox = new JCheckBox(day);
      dayCheckboxes.put(day, checkBox);
      weekdayPanel.add(checkBox);
    }

    createRecurringEventPanel.add(weekdayPanel);

    int result = JOptionPane.showConfirmDialog(mainFrame, createRecurringEventPanel,
        "Create New Recurring Event", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);


    if (result == JOptionPane.OK_OPTION) {

      String name = eventNameField.getText();
      String fromDateTime = fromDTField.getText();
      String toDateTime = toDTField.getText();
      String nTimes = nField.getText();
      String desc = descField.getText();
      String loc = locField.getText();
      String isPublicString = Boolean.toString(isPublicField.isSelected());

      List<String> selectedDays = new ArrayList<>();
      for (String day : days) {
        if (dayCheckboxes.get(day).isSelected()) {
          selectedDays.add(day);
        }
      }
      String weekdays = String.join("", selectedDays);

      if (name.isEmpty() || nTimes.isEmpty() || fromDateTime.isEmpty() || toDateTime.isEmpty()
          || weekdays.isEmpty()) {
        commandList.add("close");
        view.setCalendarCommandList(commandList);
        JOptionPane.showMessageDialog(mainFrame, "Please fill out all fields");
      }
      else {
        commandList.add("create_recurring_event"); // 0
        commandList.add(name); // 1
        commandList.add(fromDateTime); // 2
        commandList.add(toDateTime); // 3
        commandList.add(weekdays); // 4
        commandList.add(nTimes); // 5
        commandList.add(date.toString()); // 6
        commandList.add(desc); // 7
        commandList.add(loc); // 8
        commandList.add(isPublicString); // 9


        view.setCalendarCommandList(commandList);
      }
    }
    else {
      commandList.add("close");
      view.setCalendarCommandList(commandList);
    }
  }
}
