package gui.sub.success;

import gui.sub.BackgroundPanel;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Slf4j
public class BaseSuccessGUI {
    private final JFrame frame;
    private final String backgroundPath;
    private final String successMessage;
    private final String returnButtonMessage;

    private final ActionListener returnButtonAction;

    public BaseSuccessGUI(String backgroundPath, String successMessage, String returnButtonMessage, ActionListener returnButtonAction) {
        this.frame = new JFrame();
        this.backgroundPath = backgroundPath;
        this.successMessage = successMessage;
        this.returnButtonMessage = returnButtonMessage;
        this.returnButtonAction = returnButtonAction;
        init();
    }

    private JPanel getBackgroundPanel() {
        try {
            BufferedImage backgroundImage = ImageIO.read(new File(backgroundPath));
            return new BackgroundPanel(backgroundImage);
        } catch (IOException e) {
            log.error("Loading image error: " + e.getMessage());
        }
        return null;
    }

    private void init() {
        frame.setSize(230, 200);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setLayout(new BorderLayout());

        JPanel backgroundPanel = getBackgroundPanel();
        backgroundPanel.setLayout(new BorderLayout());

        JLabel successMessageLabel = new JLabel(successMessage, SwingConstants.CENTER);
        successMessageLabel.setFont(new Font("Dialog", Font.BOLD, 18));

        log.info(successMessage);

        backgroundPanel.add("Center", successMessageLabel);

        JButton returnButton = new JButton(returnButtonMessage);
        returnButton.setFont(new Font("Dialog", Font.BOLD, 15));;
        returnButton.addActionListener(returnButtonAction);
        returnButton.addActionListener(e -> {
            frame.dispose();
        });

        returnButton.setPreferredSize(new Dimension(50, 50));

        backgroundPanel.add("South", returnButton);

        frame.add(backgroundPanel);
    }
}

