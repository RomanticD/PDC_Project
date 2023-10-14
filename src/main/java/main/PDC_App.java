package main;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import gui.LoginGUI;
import manager.FormsManager;

import javax.swing.*;
import java.awt.*;

public class PDC_App extends JFrame {

    public PDC_App() {
        init();
    }

    private void init() {
        setTitle("PDC Project Group 18");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(new Dimension(400, 420));
        setLocationRelativeTo(null);
        setContentPane(new LoginGUI());
        FormsManager.getInstance().initApplication(this);
    }

    public static void main(String[] args) {
        FlatRobotoFont.install();
        FlatLaf.registerCustomDefaultsSource("raven.themes");
        UIManager.put("defaultFont", new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 13));
        FlatMacDarkLaf.setup();
        EventQueue.invokeLater(() -> new PDC_App().setVisible(true));
    }
}
