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
    JTextField propertyField = new JTextField(10);
    JTextField fromTimeField = new JTextField(10);
    JTextField toTimeField = new JTextField(10);
    JTextField newPropertyField = new JTextField(10);


    editEventsPanel = new JPanel();
    editEventsPanel.setLayout(new GridLayout(0, 1));
    JLabel instructions = new JLabel("Edit Recurring events starting today.");
    editEventsPanel.add(instructions);

    editEventsPanel.add(new JLabel("Event Name:"));
    editEventsPanel.add(eventNameField);

    editEventsPanel.add(new JLabel("Edit Property:"));
    editEventsPanel.add(propertyField);

    editEventsPanel.add(new JLabel("From Time:"));
    editEventsPanel.add(fromTimeField);

    editEventsPanel.add(new JLabel("New Property Value:"));
    editEventsPanel.add(newPropertyField);

    int result = JOptionPane.showConfirmDialog(mainFrame, editEventsPanel,
        "Edit an Event", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

    if (result == JOptionPane.OK_OPTION) {
      String name = eventNameField.getText();
      String property = propertyField.getText();
      String fromTime = fromTimeField.getText();
      String newValue = newPropertyField.getText();

      if (name.isEmpty()) {
        commandList.add("close");
        view.setCalendarCommandList(commandList);
        JOptionPane.showMessageDialog(mainFrame, "Please fill out all fields");
      }
      else {
        commandList.add("edit_recurring_events");
        commandList.add(name);
        commandList.add(property);
        commandList.add(fromTime);
        commandList.add(newValue);
        commandList.add(date.toString());

        view.setCalendarCommandList(commandList);

        JOptionPane.showMessageDialog(mainFrame, "Editing Recurring Events " + name +
            " Property: " + property + " to value: " + newValue);
      }
    }
    else {
      commandList.add("close");
      view.setCalendarCommandList(commandList);
    }
  }
}
