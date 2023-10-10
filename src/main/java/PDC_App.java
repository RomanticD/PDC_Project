import gui.Login_GUI;

import java.awt.*;

public class PDC_App {
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Login_GUI().setVisible(true);
            }
        });
    }
}
