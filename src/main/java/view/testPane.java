package view;

import javax.swing.*;

public class testPane extends JFrame {


  testPane() {
    super();
    this.setTitle("Turtles!");
    this.setSize(500, 500);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    JOptionPane.showMessageDialog(null, "This is a test popup!",
        "Test", JOptionPane.INFORMATION_MESSAGE);
  }
}
