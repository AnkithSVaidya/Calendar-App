package view;

import java.awt.*;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import javax.swing.*;

public class CalendarView extends JFrame implements IView {
  private JButton exportButton;
  private JButton importButton;
  private JButton createCalendarButton;
  private JPanel buttonPanelBottom;
  private JPanel buttonPanelTop;

  private JPanel calendarContainerPanel;
  private JPanel calendarPanel;
  private JLabel monthLabel;

  private YearMonth currentMonth;

  public CalendarView() {
    super();
    this.setTitle("Calendar App");
    this.setSize(1000, 500);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    currentMonth = YearMonth.now();

    // Bottom buttons
    buttonPanelBottom = new JPanel();
    buttonPanelBottom.setLayout(new FlowLayout());
    this.add(buttonPanelBottom, BorderLayout.SOUTH);

    exportButton = new JButton("Export Calendar");
    buttonPanelBottom.add(exportButton);

    importButton = new JButton("Import Calendar");
    buttonPanelBottom.add(importButton);

    //Top Buttons
    buttonPanelTop = new JPanel();
    buttonPanelTop.setLayout(new FlowLayout());
    this.add(buttonPanelTop, BorderLayout.NORTH);

    createCalendarButton = new JButton("Create Calendar");
    buttonPanelTop.add(createCalendarButton);

    String[] petStrings = { "Bird", "Cat", "Dog", "Rabbit", "Pig" };

    JComboBox petList = new JComboBox(petStrings);
    petList.setSelectedIndex(4);
    buttonPanelTop.add(petList);

    // Calendar grid.
    calendarContainerPanel = new JPanel();
    calendarContainerPanel.setLayout(new BorderLayout());
    this.add(calendarContainerPanel, BorderLayout.CENTER);

    monthLabel = new JLabel("", JLabel.CENTER);
    updateMonthLabel();
//    calendarContainerPanel.add(monthLabel, BorderLayout.NORTH);

    JPanel navPanel = new JPanel();
    JButton prevButton = new JButton("<");
    JButton nextButton = new JButton(">");

    prevButton.addActionListener(e -> showPreviousMonth());
    nextButton.addActionListener(e -> showNextMonth());

    navPanel.add(prevButton);
    navPanel.add(monthLabel);
    navPanel.add(nextButton);

    calendarContainerPanel.add(navPanel, BorderLayout.NORTH);

    calendarPanel = new JPanel(new GridLayout(6, 7));  // 6 rows, 7 columns
    calendarContainerPanel.add(calendarPanel, BorderLayout.CENTER);

    // Display the current month calendar
    showCalendar();

  }

  @Override
  public void makeVisible() {
    this.setVisible(true);
  }

  @Override
  public void setCommandButtonListener(ActionListener actionEvent) {
    importButton.addActionListener(actionEvent);
  }

  private void updateMonthLabel() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
    monthLabel.setText(currentMonth.format(formatter));
  }

  // Method to show the calendar for the current month
  private void showCalendar() {
    calendarPanel.removeAll();  // Clear previous calendar grid

    // Get the first day of the month and the number of days in the month
    LocalDate firstDayOfMonth = currentMonth.atDay(1);
    int daysInMonth = currentMonth.lengthOfMonth();

    // Get the day of the week for the first day of the month
    int startDayOfWeek = firstDayOfMonth.getDayOfWeek().getValue();  // 1 = Monday, 7 = Sunday

    // Add empty labels for days before the start of the month
    for (int i = 1; i < startDayOfWeek; i++) {
      calendarPanel.add(new JLabel(""));
    }

    // Add buttons for each day of the month
    for (int day = 1; day <= daysInMonth; day++) {
      JButton dayButton = new JButton(String.valueOf(day));
//      dayButton.addActionListener(e -> showDayDetails(day));
      calendarPanel.add(dayButton);
    }

    // Add empty labels for remaining spaces to complete the grid
    int remainingDays = (startDayOfWeek + daysInMonth - 1) % 7;
    for (int i = remainingDays; i < 6; i++) {
      calendarPanel.add(new JLabel(""));
    }

    // Revalidate and repaint the panel
    calendarPanel.revalidate();
    calendarPanel.repaint();
  }

  private void showPreviousMonth() {
    currentMonth = currentMonth.minusMonths(1);
    updateMonthLabel();
    showCalendar();
  }

  private void showNextMonth() {
    currentMonth = currentMonth.plusMonths(1);
    updateMonthLabel();
    showCalendar();
  }
}
