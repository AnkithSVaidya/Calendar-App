package view;

import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

public class CreateCalendarPopup extends JFrame implements IButtonPopups {
  private JPanel createCalPanel;
  private List<String> commandList;

  CreateCalendarPopup(IView view, JFrame mainFrame) {
    commandList = new ArrayList<>();

    JTextField nameField = new JTextField(10);
    JTextField timezoneField = new JTextField(10);

    // Panel to hold the fields
    createCalPanel = new JPanel();
    createCalPanel.setLayout(new GridLayout(0, 1));

    createCalPanel.add(new JLabel("Calendar Name:"));
    createCalPanel.add(nameField);
    createCalPanel.add(Box.createVerticalStrut(15));
    createCalPanel.add(new JLabel("Timezone:"));
    createCalPanel.add(timezoneField);

    int result = JOptionPane.showConfirmDialog(mainFrame, createCalPanel,
        "Create New Calendar", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

    if (result == JOptionPane.OK_OPTION) {
      String name = nameField.getText();
      String timezone = timezoneField.getText();

      if (name.isEmpty() || timezone.isEmpty()) {
        commandList.add("close");
        view.setCalendarCommandList(commandList);
        JOptionPane.showMessageDialog(mainFrame, "Please fill out all fields");
      }
      else {
        commandList.add("create_calendar");
        commandList.add(name);
        commandList.add(timezone);

        view.setCalendarCommandList(commandList);

        JOptionPane.showMessageDialog(mainFrame, "Calendar '" + name +
            "' created with timezone '" + timezone + "'");
      }
    }
    else {
      commandList.add("close");
      view.setCalendarCommandList(commandList);
    }
  }

  @Override
  public void setCommandButtonListener(ActionListener actionEvent) {
//    submitButton.addActionListener(actionEvent);
  }

  @Override
  public List<String> getCalendarCommandList() {
    return List.of();
  }
}
