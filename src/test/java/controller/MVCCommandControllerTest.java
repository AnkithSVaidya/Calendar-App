package controller;

import org.junit.Test;


import java.awt.desktop.AboutEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import model.AbstractEvent;
import model.Event;
import view.EventDetails;

import static org.junit.Assert.*;

public class MVCCommandControllerTest {

  @Test
  public void sandbox() {

    LocalDateTime now = LocalDateTime.now();
    LocalDateTime nextTime = now.plusHours(2);

    Event event = new Event("titleTest", now, nextTime, "desc", "loc", true);

    EventDetails d = parseEventToEventDetail(event);
    System.out.println(d.returnTest());

  }

  public EventDetails parseEventToEventDetail(AbstractEvent event) {

    LocalTime startDT = event.getStart().toLocalTime();
    LocalTime endDT = event.getEnd().toLocalTime();
    LocalDate d = event.getStart().toLocalDate();


    return new EventDetails(event.getTitle(), event.getDescription(), event.getLocation(),
        event.isPublic(), startDT, endDT, d);
  }


}