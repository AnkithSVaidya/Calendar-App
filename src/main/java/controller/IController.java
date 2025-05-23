package controller;

import java.io.IOException;

/**
 * Main controller interface. Initializes two different go functions based on desired mode.
 */
public interface IController {


  /**
   * Method to start the controller.
   * @throws IOException - Throws IOException on file input/output errors.
   */
  void controllerGo() throws IOException;
}
