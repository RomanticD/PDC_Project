package gui;

import constants.UIConstants;
import dao.UserDaoInterface;
import dao.impl.UserDao;
import domain.enums.Role;
import domain.User;
import gui.sub.BackgroundPanel;
import lombok.extern.slf4j.Slf4j;

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

@Slf4j
public class LoginGUI extends JFrame {
    private String username;
    private String password;
    private final UserDaoInterface userDao;

    public LoginGUI(){
        super(UIConstants.APP_NAME);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(UIConstants.LOGIN_FRAME_SIZE[0], UIConstants.LOGIN_FRAME_SIZE[1]);
        setLocationRelativeTo(null);
        this.userDao = new UserDao();

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

        ActionListener loginListener = e -> performLoginAction(usernameField, passwordField);

        // adding action listeners for pressing enter key
        usernameField.addActionListener(loginListener);
        passwordField.addActionListener(loginListener);

        // login button
        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Dialog", Font.BOLD, 18));
        springLayout.putConstraint(SpringLayout.WEST, loginButton, 75, SpringLayout.WEST, loginPanel);
        springLayout.putConstraint(SpringLayout.NORTH, loginButton, 200, SpringLayout.NORTH, loginPanel);
        loginButton.addActionListener(loginListener);
        loginPanel.add(loginButton);

        // register button
        JButton registerButton = new JButton("Register");
        registerButton.setFont(new Font("Dialog", Font.BOLD, 18));
        springLayout.putConstraint(SpringLayout.EAST, registerButton, -75, SpringLayout.EAST, loginPanel);
        springLayout.putConstraint(SpringLayout.NORTH, registerButton, 200, SpringLayout.NORTH, loginPanel);
        registerButton.addActionListener((ActionEvent e) -> {
            new RegisterGUI().setVisible(true);
            LoginGUI.this.dispose();
        });
        loginPanel.add(registerButton);

        this.getContentPane().add(loginPanel);
    }

    private void performLoginAction(JTextField usernameField, JPasswordField passwordField) {
        username = usernameField.getText();
        password = passwordField.getText();

        ResultSet rs = userDao.validateUser(username, password);
        try {
            if (rs.next()) {
                User currentUser = User.builder()
                        .name(rs.getString("name"))
                        .username(rs.getString("username"))
                        .email(rs.getString("email"))
                        .password(rs.getString("password"))
                        .role(Objects.equals(rs.getString("role"), "ADMIN") ? Role.ADMIN : Role.USER)
                        .userId(rs.getInt("user_id"))
                        .build();

                LoginGUI.this.removeNotify();
                new MainGUI(currentUser);
                log.info("SUCCESSFULLY LOGIN! Current User: " + currentUser.getName());
            } else {
                JOptionPane pane = new JOptionPane("username or password incorrect!");
                JDialog dialog = pane.createDialog("Warning");
                dialog.setFont(new Font("Dialog", Font.BOLD, 18));
                log.error("LOGIN FAILED(check your password and username))");
                dialog.setVisible(true);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            log.error("LOGIN FAILED(sql Exception) ");
        }
    }
}