package gui;

import com.formdev.flatlaf.FlatClientProperties;
import dao.UserDaoInterface;
import dao.impl.UserDao;
import domain.User;
import domain.enums.Role;
import lombok.extern.slf4j.Slf4j;
import manager.FormsManager;
import net.miginfocom.swing.MigLayout;
import util.FrameUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

@Slf4j
public class Login extends JPanel {
    private String username;
    private String password;
    private final UserDaoInterface userDao = new UserDao();

    public Login() {
        init();
    }

    private void init() {
        setLayout(new MigLayout("fill,insets 20", "[center]", "[center]"));
        txtUsername = new JTextField();
        txtPassword = new JPasswordField();
        chRememberMe = new JCheckBox("Remember me");
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

        txtUsername.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your username or email");
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

                JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
                frame.dispose();
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
            FrameUtils.disposeCurrentFrameAndCreateNewFrame("New Registration", this, new Register());
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
