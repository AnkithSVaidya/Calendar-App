package view;

import java.awt.event.ActionListener;
import java.time.LocalDate;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.BoxLayout;

/**
 * DayPopup class builds a day popup pane. This pane includes buttons to create event, create all
 * day event, creat recurring event, edit event, edit recurring event
 */
public class DayPopup extends JDialog implements IButtonPopups {

  LocalDate date;
  JButton createEventButton;
  JButton createAllDayEventButton;
  JButton createRecurringEventButton;
  JButton editSingleEventButton;
  JButton editEventsButton;

  /**
   * Constructor method for day options popup.
   * @param date - Date of selected day.
   */
  DayPopup(LocalDate date) {
    this.date = date;

    JFrame popup = new JFrame("Options for date: " + date.toString());
    popup.setSize(200, 300);
    popup.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Closes this popup only.
    popup.setLayout(new BoxLayout(popup.getContentPane(), BoxLayout.Y_AXIS));

    // Create single event button.
    createEventButton = new JButton("Create Event");
    createEventButton.setActionCommand("Create Event");

    // Create all day event button.
    createAllDayEventButton = new JButton("Create All Day Event");
    createAllDayEventButton.setActionCommand("Create All Day Event");

    // Create recurring event button.
    createRecurringEventButton = new JButton("Create Recurring Event");
    createRecurringEventButton.setActionCommand("Create Recurring Event");

    // Edit event button.
    editSingleEventButton = new JButton("Edit Event");
    editSingleEventButton.setActionCommand("Edit Event");

    // Edit multiple event button.
    editEventsButton = new JButton("Edit Events");
    editEventsButton.setActionCommand("Edit Events");

    JButton closeBtn = new JButton("Close");
    closeBtn.addActionListener(e -> popup.dispose());

    popup.add(createEventButton);
    popup.add(createAllDayEventButton);
    popup.add(createRecurringEventButton);
    popup.add(editSingleEventButton);
    popup.add(editEventsButton);
    popup.add(closeBtn);
    popup.setVisible(true);
  }

  @Override
  public void setCommandButtonListener(ActionListener actionEvent) {
    createEventButton.addActionListener(actionEvent);
    createAllDayEventButton.addActionListener(actionEvent);
    createRecurringEventButton.addActionListener(actionEvent);
    editSingleEventButton.addActionListener(actionEvent);
    editEventsButton.addActionListener(actionEvent);
  }

}
