package view;

import java.awt.GridLayout;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTextField;
import javax.swing.JFrame;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

/**
 * Class to show edit event popup.
 */
public class EditEventPopup extends JFrame {

  /**
   * Constructor method to build the popup frame
   * @param view - The view.
   * @param mainFrame - The views main frame.
   * @param date - The active date.
   */
  public EditEventPopup(IView view, JFrame mainFrame, LocalDate date) {
    List<String> commandList = new ArrayList<>();

    JTextField eventNameField = new JTextField(10);

    // Use dropdown with EXACT property names that Calendar.applyEdit supports
    String[] properties = {"subject", "description", "location", "start", "end", "ispublic"};
    JComboBox<String> propertyBox = new JComboBox<>(properties);

    JTextField fromTimeField = new JTextField(10);
    JTextField toTimeField = new JTextField(10);
    JTextField newPropertyField = new JTextField(10);

    JPanel editEventPanel = new JPanel(new GridLayout(0, 1));

    JLabel instructions = new JLabel("Edit a single event on " + date.toString());
    JLabel formatNote = new JLabel("Please input times in hh:mm format.");

    editEventPanel.add(instructions);
    editEventPanel.add(formatNote);

    editEventPanel.add(new JLabel("Event Name:"));
    editEventPanel.add(eventNameField);

    editEventPanel.add(new JLabel("Property to Edit:"));
    editEventPanel.add(propertyBox);

    editEventPanel.add(new JLabel("From Time:"));
    editEventPanel.add(fromTimeField);

    editEventPanel.add(new JLabel("To Time:"));
    editEventPanel.add(toTimeField);

    editEventPanel.add(new JLabel("New Value:"));
    editEventPanel.add(newPropertyField);

    int result = JOptionPane.showConfirmDialog(mainFrame, editEventPanel,
        "Edit an Event", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

    if (result == JOptionPane.OK_OPTION) {
      String eventName = eventNameField.getText().trim();
      String property = (String) propertyBox.getSelectedItem(); // Get selected property
      String fromTime = fromTimeField.getText().trim();
      String toTime = toTimeField.getText().trim();
      String newValue = newPropertyField.getText().trim();

      if (eventName.isEmpty() || property == null ||
              fromTime.isEmpty() || toTime.isEmpty() || newValue.isEmpty()) {
        commandList.add("close");
        JOptionPane.showMessageDialog(mainFrame, "Please fill out all fields");
      } else {
        // Build the command list in the order expected by the controller.
        commandList.add("edit_event"); // 0
        commandList.add(eventName); // 1
        commandList.add(property); // 2
        commandList.add(fromTime); // 3
        commandList.add(toTime); // 4
        commandList.add(newValue); // 5
        commandList.add(date.toString()); // 6
      }
      // push back to controller in either case:
      view.setCalendarCommandList(commandList);
    } else {
      commandList.add("close");
      view.setCalendarCommandList(commandList);
    }
  }
}