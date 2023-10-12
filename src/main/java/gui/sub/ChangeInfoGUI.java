package gui.sub;

import constants.UIConstants;
import dao.UserDaoInterface;
import dao.impl.UserDao;
import domain.User;
import gui.ProfileGUI;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class ChangeInfoGUI extends JFrame {
    private final String title;
    private final UserDaoInterface userDao;
    private final User user;

    public ChangeInfoGUI(String title, User user){
        super("Change " + title);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        this.user = user;
        this.userDao = new UserDao();
        this.title = title;

        addComponents(Objects.requireNonNull(getBackgroundPanel()));
    }

    /**
     * Get current view's panel
     * @return Panel with background
     */
    private JPanel getBackgroundPanel() {
        try {
            BufferedImage backgroundImage = ImageIO.read(new File(UIConstants.CHANGE_INFO_GUI_IMAGE));
            return new BackgroundPanel(backgroundImage);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void addComponents(JPanel panel){
        SpringLayout springLayout = new SpringLayout();
        panel.setLayout(springLayout);

        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Dialog", Font.BOLD, 15));
        springLayout.putConstraint(SpringLayout.WEST, backButton, 5, SpringLayout.WEST, panel);
        springLayout.putConstraint(SpringLayout.NORTH, backButton, 5, SpringLayout.NORTH, panel);
        backButton.addActionListener(e -> {
            ChangeInfoGUI.this.dispose();
            new ProfileGUI(user);
        });
        panel.add(backButton);

        JLabel label = new JLabel("Enter new " + title + " :");
        label.setFont(new Font("Dialog", Font.BOLD, 18));

        JTextField newField = new JTextField(UIConstants.LOGIN_TEXT_FIELD_SIZE);
        newField.setFont(new Font("Dialog", Font.BOLD, 18));

        springLayout.putConstraint(SpringLayout.WEST, label, 35, SpringLayout.WEST, panel);
        springLayout.putConstraint(SpringLayout.NORTH, label, 95, SpringLayout.NORTH, panel);
        springLayout.putConstraint(SpringLayout.WEST, newField, 35, SpringLayout.WEST, panel);
        springLayout.putConstraint(SpringLayout.EAST, newField, -35, SpringLayout.EAST, panel);
        springLayout.putConstraint(SpringLayout.NORTH, newField, 125, SpringLayout.NORTH, panel);

        panel.add(label);
        panel.add(newField);

        JButton loginButton = new JButton("Confirm Modify");
        loginButton.setFont(new Font("Dialog", Font.BOLD, 18));
        springLayout.putConstraint(SpringLayout.WEST, loginButton, 100, SpringLayout.WEST, panel);
        springLayout.putConstraint(SpringLayout.EAST, loginButton, -100, SpringLayout.EAST, panel);
        springLayout.putConstraint(SpringLayout.NORTH, loginButton, 200, SpringLayout.NORTH, panel);
        panel.add(loginButton);

        this.getContentPane().add(panel);
    }
}
