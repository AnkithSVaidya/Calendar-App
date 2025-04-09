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

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import javax.swing.DefaultComboBoxModel;

import model.Calendar;
import model.Event;

public class CalendarView extends JFrame implements IView {
  private JFrame frame;
  private JFrame showEventsFrame;
  private JPanel calendarPanel;
  private JPanel topButtons;
  private JLabel monthLabel;
  private JComboBox<String> calendarDropdown;

  private Map<String, Color> calendars;
  private List<String> calendarNameList;

  private IButtonPopups currentPopup;

  private Map<LocalDate, List<String>> events;
  private Map<LocalDate, List<EventDetails>> eventDetailsList;

  private Map<LocalDate, List<EventDetails>> activeEventDetailsList;
  private Map<String, List<EventDetails>> detailsForEachCalendarList;


  private YearMonth currentMonth;
  private String selectedCalendar;
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

  private List<String> commandList;
  private Map<String, Calendar> calendarsMap;
  private LocalDate activeDate;

  private JButton prevButton;
  private JButton nextButton;
  private List<JButton> dayButtonList;

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

    topPanel = new JPanel();
    topPanel.setLayout(new GridLayout(0, 1));
    topButtons = new JPanel();

    createCalButtonNew = new JButton("Create Calendar");
    createCalButtonNew.setActionCommand("Create Calendar");

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

    eventOptionsButton = new JButton("Options");
    eventOptionsButton.setActionCommand("Day Options");
    activeInfo.add(eventOptionsButton);
    topPanel.add(activeInfo);


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

    dayButtonList = new ArrayList<>();

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
    DayPopup dayPopup = new DayPopup(this, date, frame);
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

  public void createCalendarPopup(ActionListener listener) {
    currentPopup = new CreateCalendarPopup(this, frame);
    currentPopup.setCommandButtonListener(listener);
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

  @Override
  public void setActiveDateVarOnly(LocalDate date) {
    this.activeDate  = date;
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

        String timeDisplay = (e.getEndTime() == null) ? "All Day" : e.getStartTime() + " - " + e.getEndTime();

        String details = "-" + e.getName() + " " + timeDisplay + " Loc: " + e.getLocation()
            + " Desc: " + e.getDescription() + System.lineSeparator();

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

//  @Override
//  public void setEvents(List<EventDetails> eventDetails, String currentCal) {
//    // Clear the existing map for a fresh start
//    eventDetailsList.clear();
//
//    for (EventDetails details : eventDetails) {
//      LocalDate date = details.getDate();
//      eventDetailsList.putIfAbsent(date, new ArrayList<>());
//      eventDetailsList.get(date).add(details);
//    }
//  }



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
  public void refresh() {
    frame.revalidate();
    frame.repaint();
  }

}
