package gui;

import constants.UIConstants;
import dao.UserDao;
import domain.Role;
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
import java.sql.SQLException;
import java.util.Objects;

public class RegisterGUI extends JFrame {
    private JPanel panel;
    private SpringLayout springLayout;

    public RegisterGUI(){
        super(UIConstants.APP_NAME);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(500,450);
        setLocationRelativeTo(null);
        this.panel = Objects.requireNonNull(getBackgroundPanel());
        this.springLayout = new SpringLayout();
        panel.setLayout(springLayout);

        addComponents(panel);
    }

    /**
     * Get current view's panel
     * @return Panel with background
     */
    private JPanel getBackgroundPanel() {
        try {
            BufferedImage backgroundImage = ImageIO.read(new File(UIConstants.REGISTER_BACKGROUND_IMAGE));
            return new BackgroundPanel(backgroundImage);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void addComponents(JPanel panel){

        JTextField nameTF = addLabelAndField("name", 50, panel);
        JTextField usernameTF = addLabelAndField("username", 110, panel);
        JPasswordField passwordTF = addPasswordField("password", 170, panel);
        JPasswordField repeatPasswordTF = addPasswordField("repeat password", 230, panel);
        JTextField emailTF = addLabelAndField("email", 290, panel);

        JCheckBox checkBox = new JCheckBox("registered as an Admin");
        springLayout.putConstraint(SpringLayout.WEST, checkBox, 150, SpringLayout.WEST, panel);
        springLayout.putConstraint(SpringLayout.EAST, checkBox, -150, SpringLayout.EAST, panel);
        springLayout.putConstraint(SpringLayout.NORTH, checkBox, 330, SpringLayout.NORTH, panel);
        panel.add(checkBox);

        JButton registerButton = addButton("Register", 190, 190, 370, panel);
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UserDao userDao = new UserDao();

                String name = nameTF.getText();
                String username = usernameTF.getText();
                String password = String.valueOf(passwordTF.getPassword());
                String repeatedPassword = String.valueOf(repeatPasswordTF.getPassword());
                String email = emailTF.getText();
                Boolean isAdmin = checkBox.isSelected();

                User newUser = new User();
                newUser.setName(name);
                newUser.setUsername(username);
                newUser.setPassword(password);
                newUser.setEmail(email);

                if (isAdmin) {
                    newUser.setRole(Role.ADMIN);
                }else{
                    newUser.setRole(Role.USER);
                }

                try {
                    if (validateInput(username, password, repeatedPassword)) {
                        if (!userDao.isUserExists(username)) {
                            if (userDao.createUser(newUser)) {
                                new RegisterSuccessGUI();
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, " The account has existed!",
                                    " The account has existed! ", JOptionPane.ERROR_MESSAGE);
                            new RegisterGUI();
                        }
                    } else {
                    }
                } catch (SQLException | ClassNotFoundException ex) {
                }
                RegisterGUI.this.removeNotify();
            }
        });

        JButton backButton = addButton("back", 5, 420, 5, panel);
        backButton.addActionListener(e -> {
            RegisterGUI.this.dispose();
            new LoginGUI().setVisible(true);
        });


        this.getContentPane().add(panel);
    }

    private boolean validateInput(String username, String password, String repeatedPassword) throws SQLException, ClassNotFoundException {
        if (username.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, " username is empty! ",
                    "Account", JOptionPane.ERROR_MESSAGE);
            new RegisterGUI();
            return false;
        }

        if (password.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Password is empty!",
                    "Password is empty", JOptionPane.ERROR_MESSAGE);
            new RegisterGUI();
            return false;
        }

        if (!password.equals(repeatedPassword)) {
            JOptionPane.showMessageDialog(null, " Passwords do not match！",
                    " The password is inconsistent ", JOptionPane.ERROR_MESSAGE);
            new RegisterGUI();
            return false;
        }
        return true;
    }

    public JButton addButton(String name, int leadPadding, int trailPadding, int topPadding, JPanel relativePanel){
        JButton button = new JButton(name);
        button.setFont(new Font("Dialog", Font.BOLD, 18));
        springLayout.putConstraint(SpringLayout.WEST, button, leadPadding, SpringLayout.WEST, panel);
        springLayout.putConstraint(SpringLayout.EAST, button, -trailPadding, SpringLayout.EAST, panel);
        springLayout.putConstraint(SpringLayout.NORTH, button, topPadding, SpringLayout.NORTH, panel);
        panel.add(button);

        return button;
    }

    private JTextField addLabelAndField(String labelName, int topPadding, JPanel relativePanel){
        JLabel label = new JLabel(labelName + ": ");
        label.setFont(new Font("Dialog", Font.BOLD, 18));

        JTextField field = new JTextField(UIConstants.LOGIN_TEXT_FIELD_SIZE);
        field.setFont(new Font("Dialog", Font.BOLD, 18));

        springLayout.putConstraint(SpringLayout.WEST, label, 35, SpringLayout.WEST, relativePanel);
        springLayout.putConstraint(SpringLayout.NORTH, label, topPadding, SpringLayout.NORTH, relativePanel);
        springLayout.putConstraint(SpringLayout.WEST, field, 235, SpringLayout.WEST, relativePanel);
        springLayout.putConstraint(SpringLayout.EAST, field, -35, SpringLayout.EAST, relativePanel);
        springLayout.putConstraint(SpringLayout.NORTH, field, topPadding, SpringLayout.NORTH, relativePanel);

        panel.add(label);
        panel.add(field);

        return field;
    }

    private JPasswordField addPasswordField(String labelName, int topPadding, JPanel relativePanel){
        JLabel label = new JLabel(labelName + ": ");
        label.setFont(new Font("Dialog", Font.BOLD, 18));

        JPasswordField field = new JPasswordField(UIConstants.LOGIN_TEXT_FIELD_SIZE);
        field.setFont(new Font("Dialog", Font.BOLD, 18));

        springLayout.putConstraint(SpringLayout.WEST, label, 35, SpringLayout.WEST, relativePanel);
        springLayout.putConstraint(SpringLayout.NORTH, label, topPadding, SpringLayout.NORTH, relativePanel);
        springLayout.putConstraint(SpringLayout.WEST, field, 235, SpringLayout.WEST, relativePanel);
        springLayout.putConstraint(SpringLayout.EAST, field, -35, SpringLayout.EAST, relativePanel);
        springLayout.putConstraint(SpringLayout.NORTH, field, topPadding, SpringLayout.NORTH, relativePanel);

        panel.add(label);
        panel.add(field);

        return field;
    }

}