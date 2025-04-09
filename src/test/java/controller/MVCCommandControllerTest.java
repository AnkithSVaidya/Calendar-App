package controller;

import org.junit.Test;


import java.awt.desktop.AboutEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import model.AbstractEvent;
import model.CalendarManager;
import model.Event;
import model.ICalendarManager;
import view.CalendarView;
import view.EventDetails;
import view.IView;

import static org.junit.Assert.*;

public class MVCCommandControllerTest {

  @Test
  public void sandbox() {

    IView v = new CalendarView();
    ICalendarManager m = new CalendarManager();
    IController c = new MVCCommandController(m, v);

  }




}