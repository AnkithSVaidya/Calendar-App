package view;

import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

public class EditEventPopup extends JFrame {
  private JPanel editEventPanel;
  private List<String> commandList;

  EditEventPopup(IView view, JFrame mainFrame, LocalDate date) {
    // edit event <property> <eventName> from <dateStringTtimeString>
    // to <dateStringTtimeString> with <NewPropertyValue>

    commandList = new ArrayList<>();
    JTextField eventNameField = new JTextField(10);
    JTextField propertyField = new JTextField(10);
    JTextField fromTimeField = new JTextField(10);
    JTextField toTimeField = new JTextField(10);
    JTextField newPropertyField = new JTextField(10);


    editEventPanel = new JPanel();
    editEventPanel.setLayout(new GridLayout(0, 1));
    JLabel instructions = new JLabel("Edit a single event.");
    editEventPanel.add(instructions);

    editEventPanel.add(new JLabel("Event Name:"));
    editEventPanel.add(eventNameField);

    editEventPanel.add(new JLabel("Edit Property:"));
    editEventPanel.add(propertyField);

    editEventPanel.add(new JLabel("From Time:"));
    editEventPanel.add(fromTimeField);

    editEventPanel.add(new JLabel("To Time:"));
    editEventPanel.add(toTimeField);

    editEventPanel.add(new JLabel("New Property Value:"));
    editEventPanel.add(newPropertyField);

    int result = JOptionPane.showConfirmDialog(mainFrame, editEventPanel,
        "Edit an Event", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

    if (result == JOptionPane.OK_OPTION) {
      String name = eventNameField.getText();
      String property = propertyField.getText();
      String fromTime = fromTimeField.getText();
      String toTime = toTimeField.getText();
      String newValue = newPropertyField.getText();

      if (name.isEmpty()) {
        commandList.add("close");
        view.setCalendarCommandList(commandList);
        JOptionPane.showMessageDialog(mainFrame, "Please fill out all fields");
      }
      else {
        commandList.add("edit_event");
        commandList.add(name);
        commandList.add(property);
        commandList.add(fromTime);
        commandList.add(toTime);
        commandList.add(newValue);
        commandList.add(date.toString());

        view.setCalendarCommandList(commandList);

        JOptionPane.showMessageDialog(mainFrame, "Editing Event " + name +
            " Property: " + property + " to value: " + newValue);
      }
    }
    else {
      commandList.add("close");
      view.setCalendarCommandList(commandList);
    }
  }

}
