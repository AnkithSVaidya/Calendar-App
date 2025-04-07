package view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.*;

import model.Calendar;

public class CalendarView extends JFrame implements IView {
  private JFrame frame;
  private JPanel calendarPanel;
  private JPanel topButtons;
  private JLabel monthLabel;
  private JComboBox<String> calendarDropdown;

  private Map<String, Color> calendars;
  private List<String> calendarNameList;

  private IButtonPopups currentPopup;

  private Map<LocalDate, List<String>> events;
  private YearMonth currentMonth;
  private String selectedCalendar;
  private JButton exportButton;
  private JButton importButton;
  private JPanel topPanel;
  private JPanel calendarStuffPanel;
  private JPanel bottomPanel;
  private JPanel bottomPanelButtons;
  private JPanel createCalPanel;
  private JPanel createEventPanel;
  private JPanel createSingleEventPanel;
  private JPanel createRecurringEventPanel;
  private JPanel createAllDayEventPanel;
  private JButton createReal;
  private JButton exitButton;
  private JButton createEventButton;
  private JButton createCalButtonNew;
  private JButton createAllDayEventButton;
  private JButton createRecurringEventButton;
  private JLabel colorChooserDisplay;
  private JLabel activeCalLabel;
  private JLabel activeDateLabel;

  private String commandString;
  private List<String> commandList;
  private Map<String, Calendar> calendarsMap;
  private LocalDate activeDate;
  private ActionListener actionListener;
  private ActionListener createCalListener;

  public CalendarView() {
    frame = new JFrame("Calendar App");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(1000, 750);
    frame.setLayout(new BorderLayout());

    currentMonth = YearMonth.now();
    calendars = new HashMap<>();
    events = new HashMap<>();
    commandList = new ArrayList<>();

    calendars.put("default", Color.GRAY);
    selectedCalendar = "default";

    topPanel = new JPanel();
    topPanel.setLayout(new GridLayout(0, 1));
    topButtons = new JPanel();
    createEventButton = new JButton("Create Event");
    createAllDayEventButton = new JButton("Create All Day Event");
    createRecurringEventButton = new JButton("Create Recurring Event");
    createCalButtonNew = new JButton("Create Calendar");
    createCalButtonNew.setActionCommand("Create Calendar");

    topButtons.add(createEventButton);
    topButtons.add(createAllDayEventButton);
    topButtons.add(createRecurringEventButton);
    topButtons.add(createCalButtonNew);
    topPanel.add(topButtons, BorderLayout.EAST);

    JPanel activeInfo = new JPanel();
    activeInfo.setLayout(new GridLayout(0, 1));

    activeCalLabel = new JLabel("Active Calendar: " + this.getActiveCalendar());
    activeInfo.add(activeCalLabel);

    // Default set active date to today.
    activeDate = LocalDate.now();
    activeDateLabel = new JLabel("Active Date: " + this.getActiveDate());
    activeInfo.add(activeDateLabel);
    topPanel.add(activeInfo);


    calendarStuffPanel = new JPanel();
    JButton prevButton = new JButton("<");
    JButton nextButton = new JButton(">");
    monthLabel = new JLabel();
    calendarDropdown = new JComboBox<>(calendars.keySet().toArray(new String[0]));
    calendarStuffPanel.add(prevButton);
    calendarStuffPanel.add(monthLabel);
    calendarStuffPanel.add(nextButton);
    calendarStuffPanel.add(calendarDropdown);

    topPanel.add(calendarStuffPanel, BorderLayout.SOUTH);

    frame.add(topPanel, BorderLayout.NORTH);

    calendarPanel = new JPanel();
    frame.add(calendarPanel, BorderLayout.CENTER);

    prevButton.addActionListener(e -> changeMonth(-1));
    nextButton.addActionListener(e -> changeMonth(1));
    calendarDropdown.addActionListener(e -> changeCalendar());

    // Bottom stuff.
    bottomPanel = new JPanel();

    bottomPanelButtons = new JPanel();
    bottomPanelButtons.setLayout(new BorderLayout());
    exportButton = new JButton("Export Calendar");
    exportButton.setActionCommand("Export Calendar");

    importButton = new JButton("Import Calendar");
    importButton.setActionCommand("Import Calendar");
    bottomPanelButtons.add(exportButton, BorderLayout.WEST);
    bottomPanelButtons.add(importButton, BorderLayout.EAST);

    exitButton = new JButton("Exit");
    exitButton.setActionCommand("Exit Button");
    bottomPanelButtons.add(exitButton);

    bottomPanel.add(bottomPanelButtons);
    frame.add(bottomPanel, BorderLayout.SOUTH);

    updateCalendar();
  }

  @Override
  public void makeVisible() {
    frame.setVisible(true);
  }

  @Override
  public void setCommandButtonListener(ActionListener actionEvent) {
    createEventButton.addActionListener(e -> createEventPane(actionEvent));
    createRecurringEventButton.addActionListener(e -> createRecurringEventPane(actionEvent));
    createAllDayEventButton.addActionListener(e -> createAllDayEventPane(actionEvent));
//    createCalButton.addActionListener(e -> createCalendarPane(actionEvent));
    exportButton.addActionListener(actionEvent);
    importButton.addActionListener(actionEvent);
    exitButton.addActionListener(actionEvent);

    createCalButtonNew.addActionListener(actionEvent);
  }

  @Override
  public String getCalendarCommand() {
    String command = this.commandString;

    // Reset command for next time.
    this.commandString = "";
    return command;
  }

  @Override
  public List<String> getCalendarCommandList() {
    ArrayList<String> copiedList = new ArrayList<>(commandList);
    commandList.clear();
    return copiedList;
  }

  @Override
  public void setCalendarCommandList(List<String> commandList) {
    this.commandList.clear();
    this.commandList = commandList;
  }

  private void updateCalendar() {
    calendarPanel.removeAll();
    calendarPanel.setLayout(new GridLayout(0, 7));
    monthLabel.setText(currentMonth.getMonth() + " " + currentMonth.getYear());
    calendarPanel.setBackground(calendars.get(selectedCalendar));

    for (int day = 1; day <= currentMonth.lengthOfMonth(); day++) {
      LocalDate date = currentMonth.atDay(day);
      JButton dayButton = new JButton(String.valueOf(day));
      dayButton.addActionListener(e -> setActiveDate(date));

      calendarPanel.add(dayButton);
    }

    this.refresh();
  }

  private void changeMonth(int offset) {
    currentMonth = currentMonth.plusMonths(offset);
    updateCalendar();
  }

  private void changeCalendar() {
    selectedCalendar = (String) calendarDropdown.getSelectedItem();
    activeCalLabel.setText("Active Calendar: " + getActiveCalendar());
    updateCalendar();
  }

  private void setActiveDate(LocalDate d) {
    this.activeDate  = d;
    activeDateLabel.setText("Active Date: " + getActiveDate());
    updateCalendar();
  }

  private void showEvents(LocalDate date) {
    List<String> dayEvents = events.getOrDefault(date, new ArrayList<>());

    String eventList = dayEvents.isEmpty() ? "No events" : String.join("\n", dayEvents);
    String newEvent = JOptionPane.showInputDialog(frame, "Events on " + date + ":\n"
        + eventList + "\n\nAdd new event:");

    if (newEvent != null && !newEvent.trim().isEmpty()) {
      dayEvents.add(newEvent);
      events.put(date, dayEvents);
    }
  }

  private void createEventPane(ActionListener e) {
    // create event --autoDecline <eventName> from
    // <dateStringTtimeString> to <dateStringTtimeString>
    JTextField eventNameField = new JTextField(10);
    JTextField fromDTField = new JTextField(10);
    JTextField toDTField = new JTextField(10);

    createSingleEventPanel = new JPanel();
    createSingleEventPanel.setLayout(new GridLayout(0, 1));
    JLabel instructions = new JLabel("Please input datetimes in <dateStringTtimeString> format.");
    createSingleEventPanel.add(instructions);

    createSingleEventPanel.add(new JLabel("Event Name:"));
    createSingleEventPanel.add(eventNameField);
    createSingleEventPanel.add(new JLabel("From Time:"));
    createSingleEventPanel.add(fromDTField);
    createSingleEventPanel.add(new JLabel("To Time:"));
    createSingleEventPanel.add(toDTField);

    int result = JOptionPane.showConfirmDialog(frame, createSingleEventPanel,
        "Create New Event", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

    if (result == JOptionPane.OK_OPTION) {

      String name = eventNameField.getText();
      String fromDateTime = fromDTField.getText();
      String toDateTime = toDTField.getText();

      commandList.add("create_event");
      commandList.add(name);
      commandList.add(fromDateTime);
      commandList.add(toDateTime);
      commandList.add(activeDate.toString());

      JOptionPane.showMessageDialog(frame, "Creating Event " + name +
          " from " + fromDateTime + " to " + toDateTime);

      // After inputs are recorded, trigger action listener so command get the string.
      e.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED,"Create Event"));
    }
  }

  private void createAllDayEventPane(ActionListener e) {
    // create event --autoDecline <eventName> on <dateStringTtimeString>
    JTextField eventNameField = new JTextField(10);

    createAllDayEventPanel = new JPanel();
    createAllDayEventPanel.setLayout(new GridLayout(0, 1));
    JLabel instructions = new JLabel("Please input a name for your all day event.");
    createAllDayEventPanel.add(instructions);

    createAllDayEventPanel.add(new JLabel("Event Name:"));
    createAllDayEventPanel.add(eventNameField);

    int result = JOptionPane.showConfirmDialog(frame, createAllDayEventPanel,
        "Create New All Day Event", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

    if (result == JOptionPane.OK_OPTION) {

      String name = eventNameField.getText();

      commandList.add("create_all_day_event");
      commandList.add(name);
      commandList.add(activeDate.toString());

      JOptionPane.showMessageDialog(frame, "Creating All Day Event " + name +
          " on " + activeDate.toString());

      // After inputs are recorded, trigger action listener so command get the string.
      e.actionPerformed(new ActionEvent(this,
          ActionEvent.ACTION_PERFORMED,"Create All Day Event"));
    }
  }

  private void createRecurringEventPane(ActionListener e) {
    // create event --autoDecline <eventName> from <dateStringTtimeString>
    // to <dateStringTtimeString> repeats <weekdays> for <N> times
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

    int result = JOptionPane.showConfirmDialog(frame, createRecurringEventPanel,
        "Create New Recurring Event", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

    if (result == JOptionPane.OK_OPTION) {
      String name = eventNameField.getText();
      String fromDateTime = fromDTField.getText();
      String toDateTime = toDTField.getText();
      String nTimes = nField.getText();

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
      commandList.add(weekdays);
      commandList.add(nTimes);

      JOptionPane.showMessageDialog(frame, "Creating Recurring Event " + name +
          " from " + fromDateTime + " to " + toDateTime + " on "
          + weekdays + ", " + nTimes + " times");

      // After inputs are recorded, trigger action listener so command get the string.
      e.actionPerformed(new ActionEvent(this,
          ActionEvent.ACTION_PERFORMED,"Create Recurring Event"));
    }
  }





  private void exportCalendar() {
    commandString = "export_calendar " + selectedCalendar;
  }

  public void setCalendars(Map<String, Calendar> calMap, String currentCal) {
    selectedCalendar = currentCal;

    calendarsMap = calMap;
    calendars.clear();

    for (String key : calMap.keySet()) {
      calendars.put(key, Color.GRAY);
    }

    calendarDropdown.removeAll();

    // Refresh dropdown after calendar creation.
    DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>(calendars.keySet().toArray(new String[0]));
    calendarDropdown.setModel(model);

    this.refresh();
  }

  public void testPopup(ActionListener listener) {
    currentPopup = new CreateCalendarPopup(this, frame);
    currentPopup.setCommandButtonListener(listener);
  }



  @Override
  public void setActiveCalendarEvents() {

  }

  public String getActiveCalendar() {
    return this.selectedCalendar;
  }

  public LocalDate getActiveDate() {
    return this.activeDate;
  }


  @Override
  public void showErrorMessage(String error) {
    JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
  }

  @Override
  public void refresh() {
    frame.revalidate();
    frame.repaint();
  }

}
