package gui;

import constants.UIConstants;
import gui.sub.ChangeInfoGUI;
import domain.User;
import gui.sub.BackgroundPanel;
import lombok.extern.slf4j.Slf4j;
import util.FrameUtil;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Slf4j
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
//        JPanel backgroundPanel = new JPanel();
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

        FrameUtil.addBackButton(panel, springLayout, ProfileGUI.this, MainGUI.class, user);

        String[] labels = {"Name:", "Username:", "E-mail:", "Role:"};
        JTextArea[] textAreas = {
                createJTextArea(user.getName()),
                createJTextArea(user.getUsername()),
                createJTextArea(user.getEmail()),
                createJTextArea(user.getRole().toString())
        };

        addChangeFieldButtons(springLayout, panel);

        for (int i = 0; i < labels.length; i++) {
            JLabel label = createJLabel(labels[i]);
            JTextArea textArea = textAreas[i];

            springLayout.putConstraint(SpringLayout.WEST, label, 35, SpringLayout.WEST, panel);
            springLayout.putConstraint(SpringLayout.NORTH, label, 50 + 60 * i, SpringLayout.NORTH, panel);
            panel.add(label);

            springLayout.putConstraint(SpringLayout.WEST, textArea, 150, SpringLayout.WEST, panel);
            springLayout.putConstraint(SpringLayout.EAST, textArea, -35, SpringLayout.EAST, panel);
            springLayout.putConstraint(SpringLayout.NORTH, textArea, 53 + 60 * i, SpringLayout.NORTH, panel);
            panel.add(textArea);
        }

        getContentPane().add(panel);
    }

    /**
     * Creates an information change button with the specified parameters.
     *
     * @param springLayout the SpringLayout to be used for setting constraints.
     * @param panel the JPanel where the button will be added.
     * @param labelText the text to be displayed on the button.
     * @param yOffset the offset in the y-direction from the top of the panel.
     * @return the JButton created for information change.
     */
    private JButton createInfoChangeButton(SpringLayout springLayout, JPanel panel, String labelText, int yOffset) {
        JButton infoChangeButton = createJButton();
        infoChangeButton.setFont(new Font("Dialog", Font.PLAIN, 15));
        springLayout.putConstraint(SpringLayout.WEST, infoChangeButton, 370, SpringLayout.WEST, panel);
        springLayout.putConstraint(SpringLayout.EAST, infoChangeButton, -35, SpringLayout.EAST, panel);
        springLayout.putConstraint(SpringLayout.NORTH, infoChangeButton, yOffset, SpringLayout.NORTH, panel);
        infoChangeButton.addActionListener(e -> {
            log.info("Change " + labelText + " Button Clicked");
            ProfileGUI.this.dispose();
            new ChangeInfoGUI(labelText, user).setVisible(true);
        });
        panel.add(infoChangeButton);
        return infoChangeButton;
    }

    /**
     * Adds change field buttons to the panel with the specified SpringLayout.
     *
     * @param springLayout the SpringLayout for setting constraints.
     * @param panel the JPanel where the buttons will be added.
     */
    private void addChangeFieldButtons(SpringLayout springLayout, JPanel panel) {
        createInfoChangeButton(springLayout, panel, "Name", 50);
        createInfoChangeButton(springLayout, panel, "Username", 110);
        createInfoChangeButton(springLayout, panel, "Email", 170);

        JButton changePasswordButton = new JButton("Change Password");
        changePasswordButton.setFont(new Font("Dialog", Font.BOLD, 15));
        springLayout.putConstraint(SpringLayout.WEST, changePasswordButton, 160, SpringLayout.WEST, panel);
        springLayout.putConstraint(SpringLayout.EAST, changePasswordButton, -160, SpringLayout.EAST, panel);
        springLayout.putConstraint(SpringLayout.NORTH, changePasswordButton, 290, SpringLayout.NORTH, panel);
        springLayout.putConstraint(SpringLayout.SOUTH, changePasswordButton, -10, SpringLayout.SOUTH, panel);
        changePasswordButton.addActionListener(e -> {
            log.info("Change Password Button Clicked");
            ProfileGUI.this.dispose();
            new ChangeInfoGUI("Password", user).setVisible(true);
        });
        panel.add(changePasswordButton);
    }

    private JTextArea createJTextArea(String text) {
        JTextArea textArea = new JTextArea(text);
        textArea.setFont(new Font("Dialog", Font.PLAIN, 15));
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        textArea.setLineWrap(true);
        textArea.setEditable(false);
        textArea.setOpaque(false);
        return textArea;
    }

    private JButton createJButton() {
        JButton changeButton = new JButton("change");
        changeButton.setFont(new Font("Dialog", Font.PLAIN, 18));
        changeButton.setOpaque(false);
        return changeButton;
    }

    private JLabel createJLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Dialog", Font.BOLD, 18));
        return label;
    }
}
