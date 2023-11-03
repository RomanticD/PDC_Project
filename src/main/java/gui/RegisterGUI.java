package gui;

import com.formdev.flatlaf.FlatClientProperties;
import constants.UIConstants;
import dao.UserService;
import dao.impl.UserDao;
import domain.User;
import domain.enums.Role;
import gui.sub.PasswordStrengthStatus;
import gui.sub.success.RegisterSuccessGUI;
import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;
import util.FrameUtil;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

@Slf4j
public class RegisterGUI extends JPanel {
    private final UserService userService = new UserDao();

    public RegisterGUI() {
        init();
    }

    private void init() {
        setLayout(new MigLayout("fill,insets 20", "[center]", "[center]"));
        txtFirstName = new JTextField();
        txtLastName = new JTextField();
        txtUsername = new JTextField();
        txtPassword = new JPasswordField();
        txtEmail = new JTextField();
        txtConfirmPassword = new JPasswordField();
        cmdRegister = new JButton("Sign Up");
        passwordStrengthStatus = new PasswordStrengthStatus();

        JPanel panel = new JPanel(new MigLayout("wrap,fillx,insets 35 45 30 45", "[fill,360]"));
        panel.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc:20;" +
                "[light]background:darken(@background,3%);" +
                "[dark]background:lighten(@background,3%)");

        txtFirstName.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "First name");
        txtLastName.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Last name");
        txtUsername.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your username");
        txtEmail.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your email");
        txtPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your password");
        txtConfirmPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Re-enter your password");
        txtPassword.putClientProperty(FlatClientProperties.STYLE, "" +
                "showRevealButton:true");
        txtConfirmPassword.putClientProperty(FlatClientProperties.STYLE, "" +
                "showRevealButton:true");

        cmdRegister.putClientProperty(FlatClientProperties.STYLE, "" +
                "[light]background:darken(@background,10%);" +
                "[dark]background:lighten(@background,10%);" +
                "borderWidth:0;" +
                "focusWidth:0;" +
                "innerFocusWidth:0");

        JLabel lbTitle = new JLabel("Welcome to PDC Project Management System!");
        JLabel description = new JLabel("Check out our group's work. Join us now and revolutionize your workflow!");
        lbTitle.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:bold +10");
        description.putClientProperty(FlatClientProperties.STYLE, "" +
                "[light]foreground:lighten(@foreground,30%);" +
                "[dark]foreground:darken(@foreground,30%)");

        passwordStrengthStatus.initPasswordField(txtPassword);

        cmdRegister.addActionListener(e -> {
            String name = txtFirstName.getText() + " " + txtLastName.getText();
            String username = txtUsername.getText();
            String password = String.valueOf(txtPassword.getPassword());
            String repeatedPassword = String.valueOf(txtConfirmPassword.getPassword());
            String email = txtEmail.getText();
            Boolean isAdmin = isSelectedRoleAdmin();

            User newUser = User.builder()
                    .name(name)
                    .username(username)
                    .password(password)
                    .email(email)
                    .build();

            if (isAdmin) {
                newUser.setRole(Role.ADMIN);
            }else{
                newUser.setRole(Role.USER);
            }

            try {
                if (validateInput(username, password, repeatedPassword)) {
                    if (!userService.isUserExists(username)) {
                        if (userService.createUser(newUser)) {
                            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
                            frame.dispose();
                            new RegisterSuccessGUI(new JFrame());
                        }
                    } else {
                        FrameUtil.showErrorDialog("The account has existed!");

                        log.warn("Account Existed!");
                        FrameUtil.disposeCurrentFrameAndCreateNewFrame("New Registration", this, new RegisterGUI());
                    }
                }
            } catch (SQLException | ClassNotFoundException ex) {
                ex.printStackTrace();
            }
            RegisterGUI.this.removeNotify();
        });


        panel.add(lbTitle);
        panel.add(description);
        panel.add(new JLabel("Full Name"), "gapy 10");
        panel.add(txtFirstName, "split 2");
        panel.add(txtLastName);
        panel.add(new JLabel("System Role"), "gapy 8");
        panel.add(createRolePanel());
        panel.add(new JSeparator(), "gapy 5 5");
        panel.add(new JLabel("Username"));
        panel.add(txtUsername, "wrap");
        panel.add(new JLabel("Email"));
        panel.add(txtEmail, "wrap");
        panel.add(new JLabel("Password"), "gapy 8");
        panel.add(txtPassword);
        panel.add(passwordStrengthStatus,"gapy 0");
        panel.add(new JLabel("Confirm Password"), "gapy 0");
        panel.add(txtConfirmPassword);
        panel.add(cmdRegister, "gapy 20");
        panel.add(createLoginLabel(), "gapy 10");
        add(panel);
    }

    /**
     * Validates the input for username, password, and repeated password.
     *
     * @param username the username to be validated.
     * @param password the password to be validated.
     * @param repeatedPassword the repeated password to be validated.
     * @return true if the input is valid, false otherwise.
     * @throws SQLException if a database access error occurs.
     * @throws ClassNotFoundException if the class cannot be located.
     */
    private boolean validateInput(String username, String password, String repeatedPassword) throws SQLException, ClassNotFoundException {
        if (username.trim().isEmpty()) {
            FrameUtil.showErrorDialog("username is empty!");
            log.warn("Invalid Input Username");
            FrameUtil.disposeCurrentFrameAndCreateNewFrame("New Registration", this, new RegisterGUI());
            return false;
        }

        if (password.trim().isEmpty()) {
            log.warn("Password is empty!");
            FrameUtil.showErrorDialog("Invalid Input Password");
            FrameUtil.disposeCurrentFrameAndCreateNewFrame("New Registration", this, new RegisterGUI());
            return false;
        }

        if (!password.equals(repeatedPassword)) {
            log.warn("Passwords do not match！");
            FrameUtil.showErrorDialog("Password No Match!");
            FrameUtil.disposeCurrentFrameAndCreateNewFrame("New Registration", this, new RegisterGUI());
            return false;
        }
        return true;
    }

    /**
     * Creates a panel for selecting the role.
     *
     * @return a component representing the role panel.
     */
    private Component createRolePanel() {
        JPanel panel = new JPanel(new MigLayout("insets 0"));
        panel.putClientProperty(FlatClientProperties.STYLE, "" +
                "background:null");
        jrUser = new JRadioButton("USER");
        jrAdmin = new JRadioButton("ADMIN");
        groupRole = new ButtonGroup();
        groupRole.add(jrUser);
        groupRole.add(jrAdmin);
        jrUser.setSelected(true);
        panel.add(jrUser);
        panel.add(jrAdmin);
        return panel;
    }

    /**
     * Creates a label for login.
     *
     * @return a component representing the login label.
     */
    private Component createLoginLabel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        panel.putClientProperty(FlatClientProperties.STYLE, "" +
                "background:null");
        JButton cmdLogin = new JButton("<html><a href=\"#\">Sign in here</a></html>");
        cmdLogin.putClientProperty(FlatClientProperties.STYLE, "" +
                "border:3,3,3,3");
        cmdLogin.setContentAreaFilled(false);
        cmdLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cmdLogin.addActionListener(e -> {
            FrameUtil.disposeCurrentFrameAndCreateNewFrame(UIConstants.APP_NAME, RegisterGUI.this, new LoginGUI(new User()));
        });
        JLabel label = new JLabel("Already have an account ?");
        label.putClientProperty(FlatClientProperties.STYLE, "" +
                "[light]foreground:lighten(@foreground,30%);" +
                "[dark]foreground:darken(@foreground,30%)");
        panel.add(label);
        panel.add(cmdLogin);
        return panel;
    }

    public boolean isSelectedRoleAdmin() {
        return jrAdmin.isSelected();
    }

    private JTextField txtFirstName;
    private JTextField txtLastName;
    private JRadioButton jrUser;
    private JRadioButton jrAdmin;
    private JTextField txtUsername;
    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirmPassword;
    private ButtonGroup groupRole;
    private JButton cmdRegister;
    private PasswordStrengthStatus passwordStrengthStatus;
}
