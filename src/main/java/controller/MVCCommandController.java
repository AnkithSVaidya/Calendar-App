package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import model.ICalendarManager;
import view.IView;

public class MVCCommandController implements IController, ActionListener {

  private ICalendarManager model;
  private IView view;

  public MVCCommandController(ICalendarManager model, IView view) {
    this.model = model;
    this.view = view;
  }

  @Override
  public void controllerGo() {
    this.view.setCommandButtonListener(this);
    this.view.makeVisible();
  }

  @Override
  public void actionPerformed(ActionEvent e) {

  }

}
