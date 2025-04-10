package view;

import java.awt.GridLayout;

import java.time.DateTimeException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTextField;
import javax.swing.JFrame;
import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

/**
 * Class to create a calendar popup.
 */
public class CreateCalendarPopup extends JFrame {

  /**
   * Method to show create calendar popup.
   * @param view - The view.
   * @param mainFrame - The main frame.
   */
  public CreateCalendarPopup(IView view, JFrame mainFrame) {
    JTextField nameField     = new JTextField(10);
    JTextField timezoneField = new JTextField(10);

    JPanel createCalPanel = new JPanel(new GridLayout(0,1));
    createCalPanel.add(new JLabel("Calendar Name:"));
    createCalPanel.add(nameField);
    createCalPanel.add(Box.createVerticalStrut(15));
    createCalPanel.add(new JLabel("Timezone:"));
    createCalPanel.add(timezoneField);

    int result = JOptionPane.showConfirmDialog(
        mainFrame,
        createCalPanel,
        "Create New Calendar",
        JOptionPane.OK_CANCEL_OPTION,
        JOptionPane.PLAIN_MESSAGE
    );

    List<String> commandList = new ArrayList<>();
    if (result == JOptionPane.OK_OPTION) {
      String name     = nameField.getText().trim();
      String timezone = timezoneField.getText().trim();

      // 1) basic empty‐field check
      if (name.isEmpty() || timezone.isEmpty()) {
        JOptionPane.showMessageDialog(
            mainFrame,
            "Please fill out all fields",
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
        commandList.add("close");
      }
      else {
        // 2) optional quick timezone format check
        try {
          ZoneId.of(timezone);
          // OK, hand off to controller
          commandList.add("create_calendar");
          commandList.add(name);
          commandList.add(timezone);
        } catch (DateTimeException dte) {
          JOptionPane.showMessageDialog(
              mainFrame,
              "Invalid timezone format",
              "Error",
              JOptionPane.ERROR_MESSAGE
          );
          commandList.add("close");
        }
      }
    } else {
      commandList.add("close");
    }

    // push it back into the view
    view.setCalendarCommandList(commandList);
  }
}
