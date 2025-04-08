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

      if (name.isEmpty()) {
        commandList.add("close");
        view.setCalendarCommandList(commandList);
        JOptionPane.showMessageDialog(mainFrame, "Please fill out all fields");
      }
      else {


        List<String> selectedDays = new ArrayList<>();
        for (String day : days) {
          if (dayCheckboxes.get(day).isSelected()) {
            selectedDays.add(day);
          }
        }
        String weekdays = String.join("", selectedDays);

        commandList.add("create_recurring_event");
        commandList.add(name);
        commandList.add(fromDateTime);
        commandList.add(toDateTime);
        commandList.add(weekdays);
        commandList.add(nTimes);
        commandList.add(date.toString());

        view.setCalendarCommandList(commandList);

        JOptionPane.showMessageDialog(mainFrame, "Creating Recurring Event " + name +
            " from " + fromDateTime + " to " + toDateTime + " on " + weekdays + " over "
            + nTimes +" times.");
      }
    }
    else {
      commandList.add("close");
      view.setCalendarCommandList(commandList);
    }
  }
}
