package gui;

import constants.UIConstants;
import dao.UserDao;
import domain.Role;
import domain.User;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import javax.swing.*;

public class RegisterGUI extends JFrame {
    private JFrame frame;
    private JOptionPane jOptionPane;

    public RegisterGUI() {
        init();
    }

    private void init() {
        frame = new JFrame();
        frame.setTitle("Registry");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setResizable(false);
        frame.getContentPane().setLayout(null);
        frame.setSize(500,500);
        frame.setLocationRelativeTo(null);
        jOptionPane = new JOptionPane();

        addComponents();
    }

    private void addComponents(){
        createLabel("name:", 50);
        createLabel("username:", 110);
        createLabel("password:", 170);
        createLabel("confirm password:", 230);
        createLabel("email:", 290);

        JTextField nameTF = createTextField(50);
        JTextField usernameTF = createTextField(110);
        JPasswordField passwordTF = createPasswordField(170);
        JPasswordField repeatedPasswordTF = createPasswordField(230);
        JTextField emailTF = createTextField(290);

        JButton registerButton = new JButton("register");
        registerButton.setBounds(190, 400, 120, 20);
        registerButton.setFont((new Font("Dialog", Font.BOLD, 15)));
        frame.add(registerButton);

        JCheckBox checkBox = new JCheckBox("registered as an Admin");
        checkBox.setBounds(150, 350, 200, 20);
        frame.add(checkBox);

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UserDao userDao = new UserDao();

                String name = nameTF.getText();
                String username = usernameTF.getText();
                String password = String.valueOf(passwordTF.getPassword());
                String repeatedPassword = String.valueOf(repeatedPasswordTF.getPassword());
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
                frame.removeNotify();
            }
        });
    }

    private boolean validateInput(String username, String password, String repeatedPassword) throws SQLException, ClassNotFoundException {
        if (username.equals("")) {
            JOptionPane.showMessageDialog(null, " username is empty! ",
                    "Account", JOptionPane.ERROR_MESSAGE);
            new RegisterGUI();
            return false;
        }

        if (password.equals("")) {
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

    private void createLabel(String text, int y) {
        JLabel label = new JLabel(text);
        label.setBounds(UIConstants.REGISTER_LABEL_X_AXIS, y, UIConstants.REGISTER_LABEL_WIDTH, UIConstants.REGISTER_LABEL_HEIGHT);
        label.setFont(new Font("Dialog", Font.BOLD, 15));
        frame.add(label);
    }

    private JTextField createTextField(int y) {
        JTextField textField = new JTextField();
        textField.setBounds(UIConstants.REGISTER_FIELD_X_AXIS, y, UIConstants.REGISTER_FIELD_WIDTH, UIConstants.REGISTER_FIELD_HEIGHT);
        textField.setFont(new Font("Dialog", Font.BOLD, 15));
        frame.add(textField);
        return textField;
    }

    private JPasswordField createPasswordField(int y) {
        JPasswordField passwordField = new JPasswordField();
        passwordField.setBounds(UIConstants.REGISTER_FIELD_X_AXIS, y, UIConstants.REGISTER_FIELD_WIDTH, UIConstants.REGISTER_FIELD_HEIGHT);
        passwordField.setFont(new Font("Dialog", Font.BOLD, 15));
        frame.add(passwordField);
        return passwordField;
    }
}
