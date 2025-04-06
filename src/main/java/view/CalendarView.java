package view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
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
  private Map<String, Color> calendars2;

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

  private String commandString;
  private Map<String, Calendar> calendarsMap;
  private ActionListener actionListener;

  public CalendarView() {
    frame = new JFrame("Calendar App");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(1000, 500);
    frame.setLayout(new BorderLayout());

    currentMonth = YearMonth.now();
    calendars = new HashMap<>();
    events = new HashMap<>();
    calendars.put("Work", Color.BLUE);
    calendars.put("Personal", Color.GREEN);
    calendars.put("Holidays", Color.RED);


    selectedCalendar = "Work";

    topPanel = new JPanel();
    topPanel.setLayout(new BorderLayout());
    topButtons = new JPanel();

    createCalButton = new JButton("Create Calendar");
    createCalButton.addActionListener(e -> createCalendarPane());
    topButtons.add(createCalButton);
    topPanel.add(topButtons, BorderLayout.NORTH);


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
    importButton = new JButton("Import Calendar");
    bottomPanelButtons.add(exportButton, BorderLayout.WEST);
    bottomPanelButtons.add(importButton, BorderLayout.EAST);
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
    this.actionListener = actionEvent;
//    createCalPanel.addActionListener(actionEvent);
//    test.addActionListener(actionEvent);
  }

  @Override
  public String getCalendarCommand() {
    String command = this.commandString;

    // Reset command for next time.
    this.commandString = "";
    return command;
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

    frame.revalidate();
    frame.repaint();
  }

  private void changeMonth(int offset) {
    currentMonth = currentMonth.plusMonths(offset);
    updateCalendar();
  }

  private void changeCalendar() {
    selectedCalendar = (String) calendarDropdown.getSelectedItem();
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

  private void createCalendarPane() {
    JTextField nameField = new JTextField(10);
    JTextField timezoneField = new JTextField(10);

    // Panel to hold the fields
    createCalPanel = new JPanel();
    createCalPanel.add(new JLabel("Calendar Name:"));
    createCalPanel.add(nameField);
    createCalPanel.add(Box.createVerticalStrut(15)); // space between fields
    createCalPanel.add(new JLabel("Timezone:"));
    createCalPanel.add(timezoneField);

    createReal = new JButton("Create");
    createCalPanel.add(createReal);

    int result = JOptionPane.showConfirmDialog(frame, createCalPanel,
        "Create New Calendar", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

    if (result == JOptionPane.OK_OPTION) {
      String name = nameField.getText();
      String timezone = timezoneField.getText();

      this.commandString = "create_calendar " + name + " " + timezone;
      System.out.println("Created calendar: " + name + " (" + timezone + ")");

      JOptionPane.showMessageDialog(frame, "Calendar '" + name +
          "' created with timezone '" + timezone + "'");

    }

    // After inputs are recorded, trigger action listener so command get the string.
    actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED,
        "create_calendar"));
  }

  public void setCalendars(Map<String, Calendar> calMap) {
    calendarsMap = calMap;
    calendars.clear();

    for (String key : calMap.keySet()) {
      calendars.put(key, Color.MAGENTA);
    }

    calendarDropdown.removeAll();

    // Refresh dropdown after calendar creation.
    DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>(calendars.keySet().toArray(new String[0]));
    calendarDropdown.setModel(model);
    frame.revalidate();
    frame.repaint();
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
