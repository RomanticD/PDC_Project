package manager;

import com.formdev.flatlaf.extras.FlatAnimatedLafChange;
import app.PDC_App;

import javax.swing.*;
import java.awt.*;

public class FormsManager {
    private PDC_App PDCAppNewUI;
    private static FormsManager instance;

    public static FormsManager getInstance() {
        if (instance == null) {
            instance = new FormsManager();
        }
        return instance;
    }

    private FormsManager() {

    }

    public void initApplication(PDC_App PDCAppNewUI) {
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
