package controller;

import java.awt.event.ActionListener;
import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import model.Calendar;
import view.EventDetails;
import view.IView;

public class MockView implements IView {

  private StringBuilder log;

  public MockView(StringBuilder log) {
    this.log = log;
  }

  @Override
  public void makeVisible() {

  }

  @Override
  public void setCommandButtonListener(ActionListener actionEvent) {

  }

  @Override
  public List<String> getCalendarCommandList() {
    return List.of();
  }

  @Override
  public void setCalendarCommandList(List<String> commandList) {

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
  public void createCalendarPopup(ActionListener listener) {

  }

  @Override
  public void showDayPopup(LocalDate date, ActionListener listener) {

  }

  @Override
  public void showCreateEventPopup(LocalDate date, ActionListener listener) {

  }

  @Override
  public void showCreateAllDayEventPopup(LocalDate date, ActionListener listener) {

  }

  @Override
  public void showRecurringEventPopup(LocalDate date, ActionListener listener) {

  }

  @Override
  public void showEditEventPopup(LocalDate date, ActionListener listener) {

  }

  @Override
  public File showImportPopup(ActionListener listener) {
    return null;
  }

  @Override
  public String showExportPopup() {
    return "";
  }

  @Override
  public void showEditRecurringEventPopup(LocalDate date, ActionListener listener) {

  }

  @Override
  public void setAllCalendarEvents(Map<String, List<EventDetails>> detailsPerMap) {

  }

  @Override
  public void showErrorMessage(String error) {

  }

  @Override
  public void showSuccessMessage(String success) {

  }
}
