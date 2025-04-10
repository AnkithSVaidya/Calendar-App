package view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.*;

import model.Calendar;

public class CalendarView extends JFrame implements IView {
  // JFrame variables.
  private JFrame frame;
  private JFrame showEventsFrame;
  private JPanel calendarPanel;
  private JPanel topButtons;
  private JLabel monthLabel;
  private JComboBox<String> calendarDropdown;
  private IButtonPopups currentPopup;
  private JButton exportButton;
  private JButton importButton;
  private JPanel topPanel;
  private JPanel calendarStuffPanel;
  private JPanel bottomPanel;
  private JPanel bottomPanelButtons;
  private JButton exitButton;
  private JButton createCalButtonNew;
  private JLabel activeCalLabel;
  private JLabel activeDateLabel;
  private JButton eventOptionsButton;
  private JButton prevButton;
  private JButton nextButton;

  private Map<String, Color> calendars;
  private Map<LocalDate, List<String>> events;
  private Map<LocalDate, List<EventDetails>> eventDetailsList;
  private Map<String, List<EventDetails>> detailsForEachCalendarList;
  private YearMonth currentMonth;
  private String selectedCalendar;
  private List<String> commandList;
  private LocalDate activeDate;


  public CalendarView() {
    frame = new JFrame("Calendar App");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(1000, 750);
    frame.setLayout(new BorderLayout());

    currentMonth = YearMonth.now();
    calendars = new HashMap<>();
    events = new HashMap<>();
    eventDetailsList = new HashMap<>();
    detailsForEachCalendarList = new HashMap<>();
    commandList = new ArrayList<>();

    calendars.put("default", Color.GRAY);
    selectedCalendar = "default";

    // Main top stuff.
    topPanel = new JPanel();
    topPanel.setLayout(new BorderLayout());

    // Active Info for lables.
    JPanel activeInfo = new JPanel();
    activeInfo.setLayout(new BoxLayout(activeInfo, BoxLayout.Y_AXIS));

    activeCalLabel = new JLabel("Active Calendar: " + this.getActiveCalendar());
    activeInfo.add(activeCalLabel);

    // Default set active date to today.
    activeDate = LocalDate.now();
    activeDateLabel = new JLabel("Active Date: " + this.getActiveDate());
    activeInfo.add(activeDateLabel);

    topPanel.add(activeInfo, BorderLayout.WEST);

    // Buttons panel
    JPanel buttonInfo = new JPanel();
    buttonInfo.setLayout(new BoxLayout(buttonInfo, BoxLayout.Y_AXIS));

    createCalButtonNew = new JButton("Create Calendar");
    createCalButtonNew.setActionCommand("Create Calendar");
    createCalButtonNew.setPreferredSize(new Dimension(150, 30));
    buttonInfo.add(createCalButtonNew);

    eventOptionsButton = new JButton("Day Options");
    eventOptionsButton.setActionCommand("Day Options");
    eventOptionsButton.setPreferredSize(new Dimension(150, 30));
    buttonInfo.add(eventOptionsButton);

    topPanel.add(buttonInfo, BorderLayout.EAST);


    // Calendar arrows and selector.
    calendarStuffPanel = new JPanel();
    prevButton = new JButton("<");
    nextButton = new JButton(">");
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
    prevButton.setActionCommand("Calendar Update");
    nextButton.setActionCommand("Calendar Update");
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
    exportButton.addActionListener(actionEvent);
    importButton.addActionListener(actionEvent);
    exitButton.addActionListener(actionEvent);
    createCalButtonNew.addActionListener(actionEvent);
    eventOptionsButton.addActionListener(actionEvent);
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

    // Then set the active calendar events.
    List<EventDetails> eventsDetails = detailsForEachCalendarList.getOrDefault(selectedCalendar, new ArrayList<>());
    setActiveCalendarEvents(eventsDetails);

    for (int day = 1; day <= currentMonth.lengthOfMonth(); day++) {
      LocalDate date = currentMonth.atDay(day);
      JButton dayButton = new JButton(String.valueOf(day));
      dayButton.addActionListener(e -> showEvents(date));
      calendarPanel.add(dayButton);
    }

    this.refresh();
  }

  @Override
  public void showDayPopup(LocalDate date, ActionListener listener) {
    DayPopup dayPopup = new DayPopup(date);
    dayPopup.setCommandButtonListener(listener);
  }

  @Override
  public void showCreateEventPopup(LocalDate date, ActionListener listener) {
    JFrame eventPopup = new CreateEventPopup(this, frame, date);
  }

  @Override
  public void showCreateAllDayEventPopup(LocalDate date, ActionListener listener) {
    JFrame popup = new CreateAllDayEventPopup(this, frame, date);
  }

  @Override
  public void showRecurringEventPopup(LocalDate date, ActionListener listener) {
    JFrame popup = new CreateRecurringEventPopup(this, frame, date);
  }

  @Override
  public void showEditEventPopup(LocalDate date, ActionListener listener) {
    JFrame popup = new EditEventPopup(this, frame, date);
  }

  @Override
  public void showEditRecurringEventPopup(LocalDate date, ActionListener listener) {
    JFrame popup = new EditRecurringEventPopup(this, frame, date);
  }

  @Override
  public File showImportPopup() {

    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    int result = fileChooser.showOpenDialog(frame);

    // Get the selected file.
    if (result == JFileChooser.APPROVE_OPTION) {
      File f = fileChooser.getSelectedFile();
      return f;
    } else {
      System.out.println("No file selected");
      return null;
    }
  }

  @Override
  public String showExportPopup() {
    String fileName = JOptionPane.showInputDialog(this, "Enter a file name. " +
        "Do not include file type.", "Export File Name", JOptionPane.PLAIN_MESSAGE);

    if (fileName != null && !fileName.trim().isEmpty()) {
      JOptionPane.showMessageDialog(this, "You have chosen to export the file as: " + fileName);
      System.out.println("Export file name: " + fileName);
    } else {
      JOptionPane.showMessageDialog(this, "No file name entered.");
    }

    return fileName;
  }

  public void createCalendarPopup() {
    JFrame popup = new CreateCalendarPopup(this, frame);
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

  private void showEvents(LocalDate date) {
    // Update selected date.
    this.activeDate  = date;
    activeDateLabel.setText("Active Date: " + getActiveDate());

    List<EventDetails> dayEvents = new ArrayList<>();
    dayEvents = getEventDetailsOnDate(date);
    StringBuilder eventListBuilder = new StringBuilder();

    if (dayEvents.isEmpty()) {
      eventListBuilder.append("No events");
    }
    else {
      for (int i = 0; i < dayEvents.size(); i++) {
        EventDetails e = dayEvents.get(i);

        String details = e.getDetailsPrintable();
        eventListBuilder.append(details);
      }
    }

    JOptionPane.showMessageDialog(frame, "Events on " + date + ":\n" + eventListBuilder);
  }

  private List<EventDetails> getEventDetailsOnDate(LocalDate date) {
    return eventDetailsList.getOrDefault(date, new ArrayList<>());
  }


  public void setCalendars(Map<String, Calendar> calMap, String currentCal) {
    selectedCalendar = currentCal;
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


  private void setActiveCalendarEvents(List<EventDetails> eventDetails) {
    eventDetailsList.clear();

    for (EventDetails details : eventDetails) {
      LocalDate date = details.getDate();
      eventDetailsList.putIfAbsent(date, new ArrayList<>());
      eventDetailsList.get(date).add(details);
    }

    this.refresh();
  }

  public void setAllCalendarEvents(Map<String, List<EventDetails>> detailsPerMap) {
    detailsForEachCalendarList = detailsPerMap;

    List<EventDetails> eventsDetails = detailsForEachCalendarList.getOrDefault(selectedCalendar, new ArrayList<>());
    setActiveCalendarEvents(eventsDetails);
  }

  @Override
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
  public void showSuccessMessage(String message) {
    JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
  }

  @Override
  public void refresh() {
    frame.revalidate();
    frame.repaint();
  }

}
