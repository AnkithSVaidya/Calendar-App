# PDP Assignment 5
1. A list of changes to the design of your program, along with a brief justification of each. 
   1. Implemented ICalendarManager interface and CalendarManager class in model. These serve as the
   new way to support multiple calendars. 
      1. We chose to create a new separate interface for this
      functionality as it seemed like the simplest way to not have to change any code in the model.
   2. The controller now uses the ICalendarManager as parameters for commands instead of the single
   ICalendar interface. The CalendarApp main method will now initialize a CalendarManager instead of
   a single Calendar.
      1. We had to change this due to creating the new manager interface for the model. The commands
      still work the same, except now they execute on the currently used calendar.

2. Instructions on how to run your program (using the jar file from the terminal).
   1. Please run in terminal using either:
      1. java -jar assignment_5_app.jar --mode interactive. Interactive mode will allow a user to 
      enter commands line by line. Note that invalid commands will throw an error and exit the
      program.
      2. java -jar assignment_5_app.jar --mode headless commands.txt. Headless mode will allow a
      user to submit a list of commands in a text file. This input will accept the relative path of
      the file.
3. Which features work and which do not.
   1. All features from the current assignment and last assignment work.
      1. create calendar
      2. edit calendar, will adjust events to new timezone
      3. use calendar
      4. copy single event
      5. copy events on, will adjust to new timezone
      6. copy events between, will adjust to new timezone
4. A rough distribution of which team member contributed to which parts of the assignment. 
   1. Austin Lee was in charge of the controller aspect.
   2. Ankith Vaidya was in charge of the model aspect.
5. Anything else you need us to know when we grade.
   1. The test_commands.txt file is used in the controller testing portion to test the headless
   mode inputs.