package view;

import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

public class EditRecurringEventPopup extends JFrame {
  private JPanel editEventsPanel;
  private List<String> commandList;

  EditRecurringEventPopup(IView view, JFrame mainFrame, LocalDate date) {
    // edit events <property> <eventName> from <dateStringTtimeString> with <NewPropertyValue>
    commandList = new ArrayList<>();
    JTextField eventNameField = new JTextField(10);
    JTextField fromTimeField = new JTextField(10);
    JTextField newPropertyField = new JTextField(10);

    String[] properties = {"subject", "description", "location", "start", "end", "ispublic"};
    JComboBox<String> propertyBox = new JComboBox<>(properties);


    editEventsPanel = new JPanel();
    editEventsPanel.setLayout(new GridLayout(0, 1));
    JLabel instructions = new JLabel("Please input times in hh:mm format.");
    editEventsPanel.add(instructions);

    editEventsPanel.add(new JLabel("Event Name:"));
    editEventsPanel.add(eventNameField);

    editEventsPanel.add(new JLabel("Event Start Time:"));
    editEventsPanel.add(fromTimeField);

    editEventsPanel.add(new JLabel("Property to Edit:"));
    editEventsPanel.add(propertyBox);

    editEventsPanel.add(new JLabel("New Property Value:"));
    editEventsPanel.add(newPropertyField);

    int result = JOptionPane.showConfirmDialog(mainFrame, editEventsPanel,
        "Edit Multiple Events", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

    if (result == JOptionPane.OK_OPTION) {
      String name = eventNameField.getText();
      String property = (String) propertyBox.getSelectedItem();
      String newValue = newPropertyField.getText();
      String startTime = fromTimeField.getText();

      if (name.isEmpty()) {
        commandList.add("close");
        view.setCalendarCommandList(commandList);
        JOptionPane.showMessageDialog(mainFrame, "Please fill out all fields");
      }
      else {
        commandList.add("edit_recurring_events"); // 0
        commandList.add(name); // 1
        commandList.add(property); // 2
        commandList.add(newValue); // 3
        commandList.add(date.toString()); // 4
        commandList.add(startTime); // 5

        view.setCalendarCommandList(commandList);
      }
    }
    else {
      commandList.add("close");
      view.setCalendarCommandList(commandList);
    }
  }
}
