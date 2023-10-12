package gui;

import constants.UIConstants;
import domain.User;
import gui.sub.BackgroundPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ProfileGUI extends JFrame {
    private User user;

    public ProfileGUI(User user) {
        this.setTitle(user.getName() + "'s Profile");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setResizable(false);
        this.setSize(500, 370);
        this.setLocationRelativeTo(null);
        this.user = user;

        JPanel backgroundPanel = getBackgroundPanel();
        if (backgroundPanel != null) {
            addComponents(backgroundPanel);
        }
    }

    private JPanel getBackgroundPanel() {
        try {
            BufferedImage backgroundImage = ImageIO.read(new File(UIConstants.PROFILE_BACKGROUND_IMAGE));
            return new BackgroundPanel(backgroundImage);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void addComponents(JPanel panel) {
        SpringLayout springLayout = new SpringLayout();
        panel.setLayout(springLayout);

        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Dialog", Font.BOLD, 15));
        springLayout.putConstraint(SpringLayout.WEST, backButton, 5, SpringLayout.WEST, panel);
        springLayout.putConstraint(SpringLayout.NORTH, backButton, 5, SpringLayout.NORTH, panel);
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ProfileGUI.this.dispose();
                new MainGUI(user);
            }
        });
        panel.add(backButton);

        String[] labels = {"Name:", "Username:", "E-mail:", "Role:"};
        JTextArea[] textAreas = {
                createJTextArea(user.getName()),
                createJTextArea(user.getUsername()),
                createJTextArea(user.getEmail()),
                createJTextArea(user.getRole().toString())
        };

        for (int i = 0; i < labels.length; i++) {
            JLabel label = createJLabel(labels[i]);
            JTextArea textArea = textAreas[i];
            JButton changeButton = new JButton("Change");


            springLayout.putConstraint(SpringLayout.WEST, label, 100, SpringLayout.WEST, panel);
            springLayout.putConstraint(SpringLayout.NORTH, label, 50 + 60 * i, SpringLayout.NORTH, panel);
            panel.add(label);

            springLayout.putConstraint(SpringLayout.WEST, textArea, 220, SpringLayout.WEST, panel);
            springLayout.putConstraint(SpringLayout.EAST, textArea, -35, SpringLayout.EAST, panel);
            springLayout.putConstraint(SpringLayout.NORTH, textArea, 50 + 60 * i, SpringLayout.NORTH, panel);
            panel.add(textArea);

            changeButton.setFont(new Font("Dialog", Font.PLAIN, 15));
            springLayout.putConstraint(SpringLayout.WEST, changeButton, 370, SpringLayout.WEST, panel);
            springLayout.putConstraint(SpringLayout.EAST, changeButton, -35, SpringLayout.EAST, panel);
            springLayout.putConstraint(SpringLayout.NORTH, changeButton, 50 + 60 * i, SpringLayout.NORTH, panel);
            panel.add(changeButton);
        }

        JButton changePasswordButton = new JButton("Change Password");
        changePasswordButton.setFont(new Font("Dialog", Font.BOLD, 15));
        springLayout.putConstraint(SpringLayout.WEST, changePasswordButton, 160, SpringLayout.WEST, panel);
        springLayout.putConstraint(SpringLayout.EAST, changePasswordButton, -160, SpringLayout.EAST, panel);
        springLayout.putConstraint(SpringLayout.NORTH, changePasswordButton, 290, SpringLayout.NORTH, panel);
        springLayout.putConstraint(SpringLayout.SOUTH, changePasswordButton, -10, SpringLayout.SOUTH, panel);
        panel.add(changePasswordButton);

        getContentPane().add(panel);
    }

    private JTextArea createJTextArea(String text) {
        JTextArea textArea = new JTextArea(text);
        textArea.setFont(new Font("Dialog", Font.PLAIN, 18));
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        textArea.setEditable(false);
        textArea.setOpaque(false);
        return textArea;
    }

    private JLabel createJLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Dialog", Font.BOLD, 18));
        return label;
    }
}
