package gui;

import com.formdev.flatlaf.FlatClientProperties;
import service.PreferenceService;
import service.UserService;
import service.dao.PreferenceDao;
import service.dao.UserDao;
import domain.Preference;
import domain.User;
import domain.enums.Role;
import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;
import util.FrameUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

@Slf4j
public class LoginGUI extends JPanel {
    private User user;
    private String username;
    private String password;
    private final UserService userService = new UserDao();
    private final PreferenceService preferenceService = new PreferenceDao();

    public LoginGUI(User user) {
        this.user = user;
        init();
    }

    private void init() {
        setLayout(new MigLayout("fill,insets 20", "[center]", "[center]"));

        if (user.getUserId() == 0) {
            txtUsername = new JTextField();
            txtPassword = new JPasswordField();
            chRememberMe = new JCheckBox("Remember me");
        } else {
            Preference preferenceByUserId = preferenceService.getPreferenceByUserId(user.getUserId());
            if (preferenceByUserId != null) {
                chRememberMe = new JCheckBox("Remember me", preferenceByUserId.isRememberMe());
                if (chRememberMe.isSelected()){
                    txtUsername = new JTextField(preferenceByUserId.getStoredUsername());
                    txtPassword = new JPasswordField(preferenceByUserId.getStoredPassword());
                }else{
                    txtUsername = new JTextField();
                    txtPassword = new JPasswordField();
                }
            } else {
                txtUsername = new JTextField();
                txtPassword = new JPasswordField();
                chRememberMe = new JCheckBox("Remember me");
            }
        }

        cmdLogin = new JButton("Login");
        JPanel panel = new JPanel(new MigLayout("wrap,fillx,insets 35 45 30 45", "fill,250:280"));
        panel.putClientProperty(FlatClientProperties.STYLE, "" +
                "arc:20;" +
                "[light]background:darken(@background,3%);" +
                "[dark]background:lighten(@background,3%)");

        txtPassword.putClientProperty(FlatClientProperties.STYLE, "" +
                "showRevealButton:true");
        cmdLogin.putClientProperty(FlatClientProperties.STYLE, "" +
                "[light]background:darken(@background,10%);" +
                "[dark]background:lighten(@background,10%);" +
                "borderWidth:0;" +
                "focusWidth:0;" +
                "innerFocusWidth:0");

        txtUsername.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your username");
        txtPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your password");

        JLabel lbTitle = new JLabel("Welcome back!");
        JLabel description = new JLabel("Please sign in to access your account");
        lbTitle.putClientProperty(FlatClientProperties.STYLE, "" +
                "font:bold +10");
        description.putClientProperty(FlatClientProperties.STYLE, "" +
                "[light]foreground:lighten(@foreground,30%);" +
                "[dark]foreground:darken(@foreground,30%)");

        ActionListener loginListener = e -> performLoginAction(txtUsername, txtPassword);
        cmdLogin.addActionListener(loginListener);

        panel.add(lbTitle);
        panel.add(description);
        panel.add(new JLabel("Username"), "gapy 8");
        panel.add(txtUsername);
        panel.add(new JLabel("Password"), "gapy 8");
        panel.add(txtPassword);
        panel.add(chRememberMe, "grow 0");
        panel.add(cmdLogin, "gapy 10");
        panel.add(createSignupLabel(), "gapy 10");
        add(panel);
    }

    /**
     * Performs the login action using the provided username field and password field.
     *
     * @param usernameField the field for the username input.
     * @param passwordField the field for the password input.
     */
    private void performLoginAction(JTextField usernameField, JPasswordField passwordField) {
        boolean rememberMe = chRememberMe.isSelected();
        username = usernameField.getText();
        password = passwordField.getText();

        this.user = userService.getUserByUsername(username);
        if (user == null) {
            FrameUtil.showErrorDialog("Account Do Not Exist!");
            usernameField.setText("");
            passwordField.setText("");
        }

        if (hasCurrentUserPreferenceStored()){
            if (rememberMe){
                setRememberStatusToTrue(username, password);
            }else{
                setRememberStatusToFalse(username, password);
            }
        }else if (rememberMe){
            storeNewPreference(username, password);
        }

        ResultSet rs = userService.validateUser(username, password);
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

                JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
                frame.dispose();
                new MainGUI(currentUser);
                log.info("SUCCESSFULLY LOGIN! Current User: " + currentUser.getName());
            } else {
                log.error("LOGIN FAILED(check your password and username))");
                FrameUtil.showErrorDialog("Username or password is incorrect!");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            log.error("LOGIN FAILED(sql Exception) ");
        }
    }

    /**
     * Stores a new user preference with the provided username and password.
     *
     * @param username the username to be stored as a preference.
     * @param password the password to be stored as a preference.
     */
    private void storeNewPreference(String username, String password) {
        if (user.getUserId() != 0){
            Preference preference = Preference.builder()
                    .userId(user.getUserId())
                    .storedUsername(username)
                    .storedPassword(password)
                    .rememberMe(true)
                    .build();

            preferenceService.setNewUserPreference(preference);
        }
    }

    /**
     * Sets the remember status to false for the provided username and password.
     *
     * @param username the username for which the remember status is to be set.
     * @param password the password for which the remember status is to be set.
     */
    private void setRememberStatusToFalse(String username, String password) {
        setRememberStatus(username, password, false);
    }

    /**
     * Sets the remember status to true for the provided username and password.
     *
     * @param username the username for which the remember status is to be set.
     * @param password the password for which the remember status is to be set.
     */
    private void setRememberStatusToTrue(String username, String password) {
        setRememberStatus(username, password, true);
    }

    /**
     * Sets the remember status for the provided username and password with the given status.
     *
     * @param username the username for which the remember status is to be set.
     * @param password the password for which the remember status is to be set.
     * @param status the boolean value to set as the remember status.
     */
    private void setRememberStatus(String username, String password, boolean status) {
        Preference preference = Preference.builder()
                .userId(user.getUserId())
                .storedUsername(username)
                .storedPassword(password)
                .rememberMe(status)
                .build();

        preferenceService.updatePreference(preference);
    }

    /**
     * Checks if the current user's preference is stored.
     *
     * @return true if the current user's preference is stored, false otherwise.
     */
    private boolean hasCurrentUserPreferenceStored() {
        if (user.getUserId() == 0){
            return false;
        }else {
            Preference preferenceByUserId = preferenceService.getPreferenceByUserId(user.getUserId());
            if (preferenceByUserId == null){
                return false;
            } else {
                return true;
            }
        }
    }

    /**
     * Creates a label for signing up.
     *
     * @return a component representing the sign-up label.
     */
    private Component createSignupLabel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        panel.putClientProperty(FlatClientProperties.STYLE, "" +
                "background:null");
        JButton cmdRegister = new JButton("<html><a href=\"#\">Sign up</a></html>");
        cmdRegister.putClientProperty(FlatClientProperties.STYLE, "" +
                "border:3,3,3,3");
        cmdRegister.setContentAreaFilled(false);
        cmdRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cmdRegister.addActionListener(e -> {
            FrameUtil.disposeCurrentFrameAndCreateNewFrame("Registration", this, new RegisterGUI());
        });
        JLabel label = new JLabel("Don't have an account ?");
        label.putClientProperty(FlatClientProperties.STYLE, "" +
                "[light]foreground:lighten(@foreground,30%);" +
                "[dark]foreground:darken(@foreground,30%)");
        panel.add(label);
        panel.add(cmdRegister);
        return panel;
    }


    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JCheckBox chRememberMe;
    private JButton cmdLogin;
}
