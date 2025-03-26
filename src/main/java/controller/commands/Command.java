package controller.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import model.ICalendarManager;

/**
 * A class to parse commands. This class will take in an input from the user and call a command
 * class based on the desired command.
 */
public class Command {

  private ICommand command;

  /**
   * Constructor method for Command. Will initialise the proper ICommand based on the user input.
   * @param rawInput - The user input.
   */
  public Command(String rawInput) {

    String[] parseInput = rawInput.split(" ");
    List<String> parseInputList = new ArrayList<>(Arrays.asList(parseInput));

    regularCommands(parseInputList);
  }

  /**
   * This method will execute a command based on whichever ICommand extending class is initialized.
   * @param calendarManager - The calendar from main.
   */
  public void execute(ICalendarManager calendarManager) {
    command.executeCommand(calendarManager);
  }

  /**
   * This method parses the input and initializes the desired ICommand extending class.
   * @param parseInput - The user input as a list.
   */
  private void regularCommands(List<String> parseInput) {
    switch (parseInput.get(0)) {
      case "create":
        command = new CommandCreate(parseInput);
        break;

      case "edit":
        command = new CommandEdit(parseInput);
        break;

      case "print":
        command = new CommandPrint(parseInput);
        break;

      case "export":
        command = new CommandExport(parseInput);
        break;

      case "show":
        command = new CommandShow(parseInput);
        break;

      case "copy":
        command = new CommandCopy(parseInput);
        break;

      case "use":
        command = new CommandUse(parseInput);
        break;

      default:
        throw new IllegalArgumentException("Invalid command.");
    }
  }
}
