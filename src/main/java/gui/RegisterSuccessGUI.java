package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class RegisterSuccessGUI {
    private JFrame frame;

    public RegisterSuccessGUI() {
        init();
    }

    private void init() {
        frame = new JFrame();
        frame.setSize(230, 200);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setLayout(new BorderLayout());
        JLabel successMessage = new JLabel("Register Successfully!", SwingConstants.CENTER);
        successMessage.setFont(new Font("Dialog", Font.BOLD, 18));

        frame.getContentPane().add("Center", successMessage);

        JButton returnButton = new JButton("Return to Login Page");
        returnButton.setFont(new Font("Dialog", Font.BOLD, 18));;
        returnButton.addActionListener((ActionEvent e) -> {
            new LoginGUI().setVisible(true);
            frame.removeNotify();
        });
        returnButton.setPreferredSize(new Dimension(50, 50));
        frame.getContentPane().add("South", returnButton);
    }
}
