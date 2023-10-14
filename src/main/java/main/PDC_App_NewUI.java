package main;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import gui.Login;
import manager.FormsManager;

import javax.swing.*;
import java.awt.*;

public class PDC_App_NewUI extends JFrame {

    public PDC_App_NewUI() {
        init();
    }

    private void init() {
        setTitle("PDC Project Group 18");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(new Dimension(400, 420));
        setLocationRelativeTo(null);
        setContentPane(new Login());
        FormsManager.getInstance().initApplication(this);
    }

    public static void main(String[] args) {
        FlatRobotoFont.install();
        FlatLaf.registerCustomDefaultsSource("raven.themes");
        UIManager.put("defaultFont", new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 13));
        FlatMacDarkLaf.setup();
        EventQueue.invokeLater(() -> new PDC_App_NewUI().setVisible(true));
    }
}
