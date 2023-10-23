package gui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MyGUI extends JFrame {
    public MyGUI() {
        // Initialize your JFrame
        setTitle("Confirmation Dialog Example");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create a button
        JButton confirmButton = new JButton("Show Confirmation Dialog");

        // Add an ActionListener to the button
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int option = JOptionPane.showOptionDialog(
                        MyGUI.this,   // Parent component (this JFrame)
                        "Are you sure?", // Message
                        "Confirmation",  // Title
                        JOptionPane.YES_NO_OPTION,  // Option type
                        JOptionPane.QUESTION_MESSAGE, // Message type
                        null,  // Icon (null for default)
                        new String[] {"Yes"}, // Custom button text
                        "Yes" // Default button text
                );

                if (option == 0) {
                    // User clicked "Yes"
                    // Add your code here to handle "Yes" option
                    System.out.println("User clicked 'Yes'");
                }
            }
        });

        // Add the button to your JFrame
        JPanel panel = new JPanel();
        panel.add(confirmButton);
        add(panel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MyGUI gui = new MyGUI();
                gui.setVisible(true);
            }
        });
    }
}

