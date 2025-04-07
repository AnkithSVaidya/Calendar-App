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

  private Map<LocalDate, List<String>> events;
  private YearMonth currentMonth;
  private String selectedCalendar;
  private JButton createCalButton;
  private JButton exportButton;
  private JButton importButton;
  private JPanel topPanel;
  private JPanel calendarStuffPanel;
  private JPanel bottomPanel;
  private JPanel bottomPanelButtons;
  private JPanel createCalPanel;
  private JButton createReal;
  private JButton exitButton;
  private JLabel colorChooserDisplay;
  JLabel activeCalLabel;

  private String commandString;
  private List<String> commandList;
  private Map<String, Calendar> calendarsMap;
  private ActionListener actionListener;
  private ActionListener createCalListener;

  public CalendarView() {
    frame = new JFrame("Calendar App");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(1000, 500);
    frame.setLayout(new BorderLayout());

    currentMonth = YearMonth.now();
    calendars = new HashMap<>();
    events = new HashMap<>();
    commandList = new ArrayList<>();

    calendars.put("default", Color.GRAY);
    selectedCalendar = "default";

    topPanel = new JPanel();
    topPanel.setLayout(new BorderLayout());
    topButtons = new JPanel();

    createCalButton = new JButton("Create Calendar");
//    createCalButton.addActionListener();

    topButtons.add(createCalButton);
    topPanel.add(topButtons, BorderLayout.NORTH);

    activeCalLabel = new JLabel("Active Calendar: " + this.getActiveCalendar());
    activeCalLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    topPanel.add(activeCalLabel);


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
//    this.actionListener = actionEvent;
//    calendarDropdown.addActionListener(actionEvent);
//    createCalPanel.addActionListener(actionEvent);
//    test.addActionListener(actionEvent);
    createCalButton.addActionListener(e -> createCalendarPane(actionEvent));
    exportButton.addActionListener(actionEvent);
    importButton.addActionListener(actionEvent);
    exitButton.addActionListener(actionEvent);
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

  private void updateCalendar() {
    calendarPanel.removeAll();
    calendarPanel.setLayout(new GridLayout(0, 7));
    monthLabel.setText(currentMonth.getMonth() + " " + currentMonth.getYear());
    calendarPanel.setBackground(calendars.get(selectedCalendar));

    for (int day = 1; day <= currentMonth.lengthOfMonth(); day++) {
      LocalDate date = currentMonth.atDay(day);
      JButton dayButton = new JButton(String.valueOf(day));
      dayButton.addActionListener(e -> showEvents(date));
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

    System.out.println("current cal: " + selectedCalendar);
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

  private void createCalendarPane(ActionListener e) {
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

    int result = JOptionPane.showConfirmDialog(frame, createCalPanel,
        "Create New Calendar", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

    if (result == JOptionPane.OK_OPTION) {

      String name = nameField.getText();
      String timezone = timezoneField.getText();

      commandList.add("create_calendar");
      commandList.add(name);
      commandList.add(timezone);

      commandString = "create_calendar " + name + " " + timezone;
      System.out.println("Created calendar: " + name + " (" + timezone + ")");

      JOptionPane.showMessageDialog(frame, "Calendar '" + name +
          "' created with timezone '" + timezone + "'");

      // After inputs are recorded, trigger action listener so command get the string.
      e.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED,"Create Cal"));
//      actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED,
//          "Create Cal"));

//      createCalListener.setActionCommand("Echo Button");
    }
  }

  private void createEventAction(String calName, String timezone) {

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

  public String getActiveCalendar() {
    return this.selectedCalendar;
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
