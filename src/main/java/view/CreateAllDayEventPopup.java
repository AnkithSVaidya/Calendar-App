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

    createAllDayEventPanel = new JPanel();
    createAllDayEventPanel.setLayout(new GridLayout(0, 1));
    JLabel instructions = new JLabel("Please input a name for your all day event.");
    createAllDayEventPanel.add(instructions);

    createAllDayEventPanel.add(new JLabel("Event Name:"));
    createAllDayEventPanel.add(eventNameField);

    int result = JOptionPane.showConfirmDialog(mainFrame, createAllDayEventPanel,
        "Create New All Day Event", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

    if (result == JOptionPane.OK_OPTION) {
      String name = eventNameField.getText();

      if (name.isEmpty()) {
        commandList.add("close");
        view.setCalendarCommandList(commandList);
        JOptionPane.showMessageDialog(mainFrame, "Please fill out all fields");
      }
      else {
        commandList.add("create_all_day_event");
        commandList.add(name);
        commandList.add(date.toString());

        view.setCalendarCommandList(commandList);

        JOptionPane.showMessageDialog(mainFrame, "Creating All Day Event " + name +
            " on " + date.toString());
      }
    }
    else {
      commandList.add("close");
      view.setCalendarCommandList(commandList);
    }
  }
}
