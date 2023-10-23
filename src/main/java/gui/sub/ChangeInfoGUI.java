package gui.sub;

import constants.UIConstants;
import dao.UserDaoInterface;
import dao.impl.UserDao;
import domain.User;
import gui.LoginGUI;
import gui.ProfileGUI;
import gui.sub.success.UpdateSuccessGUI;
import lombok.extern.slf4j.Slf4j;
import util.FrameUtil;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

@Slf4j
public class ChangeInfoGUI extends JFrame {
    private final String infoToModify;
    private final UserDaoInterface userDao;
    private User user;

    public ChangeInfoGUI(String infoToModify, User user){
        super("Change " + infoToModify);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, infoToModify.equals("Password") ? 350 : 300);
        setLocationRelativeTo(null);
        this.user = user;
        this.userDao = new UserDao();
        this.infoToModify = infoToModify;

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

        FrameUtil.addBackButton(panel, springLayout, ChangeInfoGUI.this, ProfileGUI.class, user);

        if (!this.infoToModify.equals("Password")){
            JLabel label = new JLabel("Enter new " + infoToModify + " :");
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

            JButton confirmButton = new JButton("Confirm Modify");
            confirmButton.setFont(new Font("Dialog", Font.BOLD, 18));
            springLayout.putConstraint(SpringLayout.WEST, confirmButton, 100, SpringLayout.WEST, panel);
            springLayout.putConstraint(SpringLayout.EAST, confirmButton, -100, SpringLayout.EAST, panel);
            springLayout.putConstraint(SpringLayout.NORTH, confirmButton, 200, SpringLayout.NORTH, panel);
            confirmButton.addActionListener(e -> {
                log.info("Changing " + user.getName() + "'s " + infoToModify);

                switch (infoToModify){
                    case "Name": ModifyName(newField.getText());
                        break;
                    case "Username": ModifyUsername(newField.getText());
                        break;
                    case "Email": ModifyEmail(newField.getText());
                        break;
                    default:
                        break;
                }
            });
            panel.add(confirmButton);
        } else {
            JLabel oldPasswordLabel = new JLabel("Current password:");
            JLabel newPasswordLabel = new JLabel("New password:");
            JLabel repeatNewPasswordLabel = new JLabel("Repeat new password:");
            oldPasswordLabel.setFont(new Font("Dialog", Font.BOLD, 18));
            newPasswordLabel.setFont(new Font("Dialog", Font.BOLD, 18));
            repeatNewPasswordLabel.setFont(new Font("Dialog", Font.BOLD, 18));

            JPasswordField oldPasswordField = new JPasswordField();
            JPasswordField newPasswordField = new JPasswordField();
            JPasswordField repeatPasswordField = new JPasswordField();
            oldPasswordField.setFont(new Font("Dialog", Font.BOLD, 18));
            newPasswordField.setFont(new Font("Dialog", Font.BOLD, 18));
            repeatPasswordField.setFont(new Font("Dialog", Font.BOLD, 18));

            // Add oldPasswordLabel
            springLayout.putConstraint(SpringLayout.WEST, oldPasswordLabel, 35, SpringLayout.WEST, panel);
            springLayout.putConstraint(SpringLayout.NORTH, oldPasswordLabel, 35, SpringLayout.NORTH, panel);

            // Add oldPasswordField
            springLayout.putConstraint(SpringLayout.WEST, oldPasswordField, 35, SpringLayout.WEST, panel);
            springLayout.putConstraint(SpringLayout.EAST, oldPasswordField, -35, SpringLayout.EAST, panel);
            springLayout.putConstraint(SpringLayout.NORTH, oldPasswordField, 10, SpringLayout.SOUTH, oldPasswordLabel);

            // Add newPasswordLabel
            springLayout.putConstraint(SpringLayout.WEST, newPasswordLabel, 35, SpringLayout.WEST, panel);
            springLayout.putConstraint(SpringLayout.NORTH, newPasswordLabel, 10, SpringLayout.SOUTH, oldPasswordField);

            // Add newPasswordField
            springLayout.putConstraint(SpringLayout.WEST, newPasswordField, 35, SpringLayout.WEST, panel);
            springLayout.putConstraint(SpringLayout.EAST, newPasswordField, -35, SpringLayout.EAST, panel);
            springLayout.putConstraint(SpringLayout.NORTH, newPasswordField, 10, SpringLayout.SOUTH, newPasswordLabel);

            // Add repeatNewPasswordLabel
            springLayout.putConstraint(SpringLayout.WEST, repeatNewPasswordLabel, 35, SpringLayout.WEST, panel);
            springLayout.putConstraint(SpringLayout.NORTH, repeatNewPasswordLabel, 10, SpringLayout.SOUTH, newPasswordField);

            // Add repeatPasswordField
            springLayout.putConstraint(SpringLayout.WEST, repeatPasswordField, 35, SpringLayout.WEST, panel);
            springLayout.putConstraint(SpringLayout.EAST, repeatPasswordField, -35, SpringLayout.EAST, panel);
            springLayout.putConstraint(SpringLayout.NORTH, repeatPasswordField, 10, SpringLayout.SOUTH, repeatNewPasswordLabel);

            JButton confirmButton = new JButton("Confirm Modify");
            confirmButton.setFont(new Font("Dialog", Font.BOLD, 18));
            springLayout.putConstraint(SpringLayout.WEST, confirmButton, 100, SpringLayout.WEST, panel);
            springLayout.putConstraint(SpringLayout.EAST, confirmButton, -100, SpringLayout.EAST, panel);
            springLayout.putConstraint(SpringLayout.NORTH, confirmButton, 50, SpringLayout.NORTH, repeatPasswordField);
            confirmButton.addActionListener(e -> {
                log.info("Changing " + user.getName() + "'s " + infoToModify);
                if (validateUserInput(oldPasswordField, newPasswordField, repeatPasswordField)){
                    ModifyPassword(String.valueOf(newPasswordField.getPassword()));
                }
            });

            panel.add(oldPasswordLabel);
            panel.add(oldPasswordField);
            panel.add(newPasswordLabel);
            panel.add(newPasswordField);
            panel.add(repeatNewPasswordLabel);
            panel.add(repeatPasswordField);
            panel.add(confirmButton);
        }

        this.getContentPane().add(panel);
    }

    private void ModifyPassword(String newPassword) {
        this.user = userDao.updateUserPassword(user, newPassword);
        FrameUtil.showDialog("Successfully Changed Your Password!");
        ChangeInfoGUI.this.dispose();
        FrameUtil.disposeCurrentFrameAndCreateNewFrame("PDC Project Group 18", ChangeInfoGUI.this, new LoginGUI(user));
    }

    private boolean validateUserInput(JPasswordField oldPasswordField, JPasswordField newPasswordField, JPasswordField repeatPasswordField) {
        String oldPassword = String.valueOf(oldPasswordField.getPassword());
        String newPassword = String.valueOf(newPasswordField.getPassword());
        String repeatedNewPassword = String.valueOf(repeatPasswordField.getPassword());

        if (!user.getPassword().equals(oldPassword)) {
            FrameUtil.showDialog("Current password is incorrect!");
            ChangeInfoGUI.this.dispose();
            new ChangeInfoGUI("Password", user).setVisible(true);
            return false;
        } else if (!newPassword.equals(repeatedNewPassword)){
            FrameUtil.showDialog("New passwords and repeated one do not match!");
            ChangeInfoGUI.this.dispose();
            new ChangeInfoGUI("Password", user).setVisible(true);
            return false;
        }
        return true;
    }

    private void ModifyEmail(String newEmail) {
        if (validateInput(newEmail)){
            User updatedUser = userDao.updateUserEmail(user, newEmail);
            ChangeInfoGUI.this.dispose();
            new UpdateSuccessGUI(updatedUser);
        }else{
            FrameUtil.showDialog("Invalid Input!");
        }
    }

    private void ModifyUsername(String newUsername) {
        if (validateInput(newUsername) && !userDao.isUserExists(newUsername)){
            User updatedUser = userDao.updateUserUsername(user, newUsername);
            ChangeInfoGUI.this.dispose();
            new UpdateSuccessGUI(updatedUser);
        }else if (userDao.isUserExists(newUsername)){
            FrameUtil.showDialog("Username Exists, Please Change a New One!");
        } else {
            FrameUtil.showDialog("Invalid Input!");
        }
    }

    private void ModifyName(String newName) {
        if (validateInput(newName)){
            User updatedUser = userDao.updateUserName(user, newName);
            ChangeInfoGUI.this.dispose();
            new UpdateSuccessGUI(updatedUser);
        }else{
            FrameUtil.showDialog("Invalid Input!");
        }
    }

    private Boolean validateInput(String text){
        return !text.trim().isEmpty();
    }
}
