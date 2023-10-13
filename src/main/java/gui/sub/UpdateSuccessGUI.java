package gui.sub;

import constants.UIConstants;
import gui.ProfileGUI;
import domain.User;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class UpdateSuccessGUI {
    private JFrame frame;

    private final User user;

    public UpdateSuccessGUI(User user) {
        this.user = user;
        init();
    }

    private JPanel getBackgroundPanel() {
        try {
            BufferedImage backgroundImage = ImageIO.read(new File(UIConstants.CHANGE_INFO_GUI_IMAGE));
            return new BackgroundPanel(backgroundImage);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void init() {
        frame = new JFrame();
        frame.setSize(230, 200);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel backgroundPanel = getBackgroundPanel();
        backgroundPanel.setLayout(new BorderLayout());

        JLabel successMessage = new JLabel("Update Successfully!", SwingConstants.CENTER);
        successMessage.setFont(new Font("Dialog", Font.BOLD, 18));

        backgroundPanel.add(successMessage, BorderLayout.CENTER);

        JButton returnButton = new JButton("Return to Profile Page");
        returnButton.setFont(new Font("Dialog", Font.BOLD, 15));
        returnButton.addActionListener((ActionEvent e) -> {
            new ProfileGUI(user).setVisible(true);
            frame.dispose();
        });
        returnButton.setPreferredSize(new Dimension(50, 50));

        backgroundPanel.add(returnButton, BorderLayout.SOUTH);

        frame.add(backgroundPanel);

        frame.setVisible(true);
    }
}
