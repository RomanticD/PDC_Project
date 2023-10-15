package gui.sub.success;

import constants.UIConstants;
import domain.User;
import gui.LoginGUI;
import gui.sub.BackgroundPanel;
import lombok.extern.slf4j.Slf4j;
import util.FrameUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Slf4j
public class RegisterSuccessGUI {
    private JFrame frame;

    public RegisterSuccessGUI() {
        init();
    }

    private JPanel getBackgroundPanel() {
        try {
            BufferedImage backgroundImage = ImageIO.read(new File(UIConstants.LOGIN_BACKGROUND_IMAGE));
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
        frame.setVisible(true);
        frame.setLayout(new BorderLayout());

        JPanel backgroundPanel = getBackgroundPanel();
        backgroundPanel.setLayout(new BorderLayout());

        JLabel successMessage = new JLabel("Register Successfully!", SwingConstants.CENTER);
        successMessage.setFont(new Font("Dialog", Font.BOLD, 18));

        log.info("Register Successfully!");

        backgroundPanel.add("Center", successMessage);

        JButton returnButton = new JButton("Return to Login Page");
        returnButton.setFont(new Font("Dialog", Font.BOLD, 15));;
        returnButton.addActionListener((ActionEvent e) -> {
            FrameUtils.disposeCurrentFrameAndCreateNewFrame("PDC Project Group 18", frame, new LoginGUI(new User()));

            frame.removeNotify();
        });
        returnButton.setPreferredSize(new Dimension(50, 50));

        backgroundPanel.add("South", returnButton);

        frame.add(backgroundPanel);
    }
}
