package controller;

import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import model.Calendar;
import view.EventDetails;
import view.IView;

public class MockView implements IView {

  private final StringBuilder log;
  List<String> commandList;

  public MockView(StringBuilder log) {
    this.log = log;
    this.commandList = new ArrayList<>();
  }

  @Override
  public void makeVisible() {

  }

  @Override
  public void setCommandButtonListener(ActionListener actionEvent) {

  }

  @Override
  public List<String> getCalendarCommandList() {
    log.append("commandList");
    log.append(" ");
    return this.commandList;
  }

  @Override
  public void setCalendarCommandList(List<String> commandList) {
    this.commandList = commandList;
  }

  @Override
  public void refresh() {

  }

  @Override
  public void setCalendars(Map<String, Calendar> calMap, String currentCal) {

  }

  @Override
  public String getActiveCalendar() {
    return "";
  }

  @Override
  public LocalDate getActiveDate() {
    return null;
  }

  @Override
  public void createCalendarPopup() {
    log.append("createCalendarPopup");
    log.append(" ");
  }

  @Override
  public void showDayPopup(LocalDate date, ActionListener listener) {
    log.append("showDayPopup");
    log.append(" ");
  }

  @Override
  public void showCreateEventPopup(LocalDate date, ActionListener listener) {
    log.append("showCreateEventPopup");
    log.append(" ");
  }

  @Override
  public void showCreateAllDayEventPopup(LocalDate date, ActionListener listener) {
    log.append("showCreateAllDayEventPopup");
    log.append(" ");
  }

  @Override
  public void showRecurringEventPopup(LocalDate date, ActionListener listener) {
    log.append("showRecurringEventPopup");
    log.append(" ");
  }

  @Override
  public void showEditEventPopup(LocalDate date, ActionListener listener) {
    log.append("showEditEventPopup");
    log.append(" ");
  }

  @Override
  public File showImportPopup() {

    log.append("showImportPopup");
    log.append(" ");

    try {
      // Create a temporary file
      File tempFile = File.createTempFile("test-import", ".csv");
      tempFile.deleteOnExit();

      return tempFile;
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }

  }

  @Override
  public String showExportPopup() {
    log.append("showExportPopup");
    log.append(" ");
    return "export";
  }

  @Override
  public void showEditRecurringEventPopup(LocalDate date, ActionListener listener) {
    log.append("showEditRecurringEventPopup");
    log.append(" ");
  }

  @Override
  public void setAllCalendarEvents(Map<String, List<EventDetails>> detailsPerMap) {

  }

  @Override
  public void showErrorMessage(String error) {
    log.append("errorMessage");
    log.append(" ");
  }

  @Override
  public void showSuccessMessage(String success) {
    log.append("successMessage");
    log.append(" ");
  }
}
