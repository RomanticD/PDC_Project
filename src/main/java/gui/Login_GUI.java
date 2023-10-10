package gui;

import constants.UIConstants;
import dao.UserDao;
import domain.Role;
import domain.User;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class Login_GUI extends JFrame {
    private JFrame frame;
    private String username;
    private String password;

    public Login_GUI(){
        super(UIConstants.APP_NAME);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(UIConstants.FRAME_SIZE[0], UIConstants.FRAME_SIZE[1]);
        setLocationRelativeTo(null);
        frame = this;

        addComponents(Objects.requireNonNull(getBackgroundPanel()));
    }

    /**
     * Get current view's panel
     * @return Panel with background
     */
    private JPanel getBackgroundPanel() {
        try {
            BufferedImage backgroundImage = ImageIO.read(new File(UIConstants.LOGIN_BACKGROUND_IMAGE));
            return new BackgroundPanel(backgroundImage);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static class BackgroundPanel extends JPanel {
        private final BufferedImage backgroundImage;

        public BackgroundPanel(BufferedImage backgroundImage) {
            this.backgroundImage = backgroundImage;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }

    private void addComponents(JPanel loginPanel){
        SpringLayout springLayout = new SpringLayout();
        loginPanel.setLayout(springLayout);

        // username
        JLabel usernameLabel = new JLabel("Username: ");
        usernameLabel.setFont(new Font("Dialog", Font.BOLD, 18));

        JTextField usernameField = new JTextField(UIConstants.LOGIN_TEXT_FIELD_SIZE);
        usernameField.setFont(new Font("Dialog", Font.BOLD, 18));

        springLayout.putConstraint(SpringLayout.WEST, usernameLabel, 35, SpringLayout.WEST, loginPanel);
        springLayout.putConstraint(SpringLayout.NORTH, usernameLabel, 85, SpringLayout.NORTH, loginPanel);
        springLayout.putConstraint(SpringLayout.WEST, usernameField, 135, SpringLayout.WEST, loginPanel);
        springLayout.putConstraint(SpringLayout.EAST, usernameField, -35, SpringLayout.EAST, loginPanel);
        springLayout.putConstraint(SpringLayout.NORTH, usernameField, 85, SpringLayout.NORTH, loginPanel);

        loginPanel.add(usernameLabel);
        loginPanel.add(usernameField);

        // password
        JLabel passwordLabel = new JLabel("Password: ");
        passwordLabel.setFont(new Font("Dialog", Font.BOLD, 18));

        JPasswordField passwordField = new JPasswordField(UIConstants.LOGIN_TEXT_FIELD_SIZE);
        passwordField.setFont(new Font("Dialog", Font.BOLD, 18));

        springLayout.putConstraint(SpringLayout.WEST, passwordLabel, 35, SpringLayout.WEST, loginPanel);
        springLayout.putConstraint(SpringLayout.NORTH, passwordLabel, 135, SpringLayout.NORTH, loginPanel);
        springLayout.putConstraint(SpringLayout.WEST, passwordField, 135, SpringLayout.WEST, loginPanel);
        springLayout.putConstraint(SpringLayout.EAST, passwordField, -35, SpringLayout.EAST, loginPanel);
        springLayout.putConstraint(SpringLayout.NORTH, passwordField, 135, SpringLayout.NORTH, loginPanel);

        loginPanel.add(passwordLabel);
        loginPanel.add(passwordField);

        // login button
        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Dialog", Font.BOLD, 18));
        springLayout.putConstraint(SpringLayout.WEST, loginButton, 75, SpringLayout.WEST, loginPanel);
        springLayout.putConstraint(SpringLayout.NORTH, loginButton, 200, SpringLayout.NORTH, loginPanel);
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                username = usernameField.getText();
                password = passwordField.getText();

                UserDao userDao = new UserDao();
                ResultSet rs = userDao.validateUser(username, password);
                try {
                    if (rs.next()) {
                        User currentUser = new User();
                        currentUser.setName(rs.getString("name"));
                        currentUser.setUsername(rs.getString("username"));
                        currentUser.setEmail(rs.getString("email"));
                        currentUser.setPassword(rs.getString("password"));
                        currentUser.setRole(rs.getString("role") == "ADMIN" ? Role.ADMIN : Role.USER);
                        frame.removeNotify();
                        new Main_GUI(currentUser);
                        System.out.println("SUCCESSFULLY LOGIN!");
                    } else {
                        JOptionPane pane = new JOptionPane("username or password incorrect!");
                        JDialog dialog = pane.createDialog("Warning");
                        System.out.println("LOGIN FAILED(check your password and username))");
                        dialog.setVisible(true);
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    System.out.println("LOGIN FAILED(sql Exception) ");
                }
            }
        });
        loginPanel.add(loginButton);

        // register button
        JButton registerButton = new JButton("Register");
        registerButton.setFont(new Font("Dialog", Font.BOLD, 18));
        springLayout.putConstraint(SpringLayout.EAST, registerButton, -75, SpringLayout.EAST, loginPanel);
        springLayout.putConstraint(SpringLayout.NORTH, registerButton, 200, SpringLayout.NORTH, loginPanel);
        registerButton.addActionListener((ActionEvent e) -> {
            new Register_GUI();
            frame.dispose();
        });
        loginPanel.add(registerButton);

        frame.getContentPane().add(loginPanel);
    }
}