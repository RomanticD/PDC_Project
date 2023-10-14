package manager;

import com.formdev.flatlaf.extras.FlatAnimatedLafChange;
import main.PDC_App_NewUI;

import javax.swing.*;
import java.awt.*;

public class FormsManager {
    private PDC_App_NewUI PDCAppNewUI;
    private static FormsManager instance;

    public static FormsManager getInstance() {
        if (instance == null) {
            instance = new FormsManager();
        }
        return instance;
    }

    private FormsManager() {

    }

    public void initApplication(PDC_App_NewUI PDCAppNewUI) {
        this.PDCAppNewUI = PDCAppNewUI;
    }

    public void showForm(JComponent form) {
        EventQueue.invokeLater(() -> {
            FlatAnimatedLafChange.showSnapshot();
            PDCAppNewUI.setContentPane(form);
            PDCAppNewUI.revalidate();
            PDCAppNewUI.repaint();
            FlatAnimatedLafChange.hideSnapshotWithAnimation();
        });
    }
}
