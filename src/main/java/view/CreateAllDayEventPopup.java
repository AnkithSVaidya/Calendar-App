package view;

import java.awt.*;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

public class CreateAllDayEventPopup extends JFrame {

  private JPanel createAllDayEventPanel;
  private List<String> commandList;


  CreateAllDayEventPopup(IView view, JFrame mainFrame, LocalDate date) {
    // create event --autoDecline <eventName> on <dateStringTtimeString>
    commandList = new ArrayList<>();
    JTextField eventNameField = new JTextField(10);
    JTextField descField = new JTextField(10);
    JTextField locField = new JTextField(10);
    JCheckBox isPublicField = new JCheckBox();

    createAllDayEventPanel = new JPanel();
    createAllDayEventPanel.setLayout(new GridLayout(0, 1));
    JLabel instructions = new JLabel("Please input a name for your all day event.");
    createAllDayEventPanel.add(instructions);

    createAllDayEventPanel.add(new JLabel("Event Name:"));
    createAllDayEventPanel.add(eventNameField);
    createAllDayEventPanel.add(new JLabel("Description:"));
    createAllDayEventPanel.add(descField);
    createAllDayEventPanel.add(new JLabel("Location:"));
    createAllDayEventPanel.add(locField);
    createAllDayEventPanel.add(new JLabel("Public:"));
    createAllDayEventPanel.add(isPublicField);

    int result = JOptionPane.showConfirmDialog(mainFrame, createAllDayEventPanel,
        "Create New All Day Event", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

    if (result == JOptionPane.OK_OPTION) {
      String name = eventNameField.getText();
      String desc = descField.getText();
      String loc = locField.getText();
      String isPublicString = Boolean.toString(isPublicField.isSelected());

      if (name.isEmpty()) {
        commandList.add("close");
        view.setCalendarCommandList(commandList);
        JOptionPane.showMessageDialog(mainFrame, "Please fill out all fields");
      }
      else {
        commandList.add("create_all_day_event"); // 0
        commandList.add(name); // 1
        commandList.add(date.toString()); // 2
        commandList.add(desc); // 3
        commandList.add(loc); // 4
        commandList.add(isPublicString); // 5

        view.setCalendarCommandList(commandList);
      }
    }
    else {
      commandList.add("close");
      view.setCalendarCommandList(commandList);
    }
  }
}
