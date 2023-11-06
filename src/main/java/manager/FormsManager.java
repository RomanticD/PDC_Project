package manager;

import com.formdev.flatlaf.extras.FlatAnimatedLafChange;
import app.PDC_App;
import lombok.NoArgsConstructor;

import javax.swing.*;
import java.awt.*;

@NoArgsConstructor
public class FormsManager {
    private PDC_App app;
    private static FormsManager instance;

    public static FormsManager getInstance() {
        if (instance == null) {
            instance = new FormsManager();
        }
        return instance;
    }

    public void initApplication(PDC_App app) {
        this.app = app;
    }

    public void showForm(JComponent form) {
        EventQueue.invokeLater(() -> {
            FlatAnimatedLafChange.showSnapshot();
            app.setContentPane(form);
            app.revalidate();
            app.repaint();
            FlatAnimatedLafChange.hideSnapshotWithAnimation();
        });
    }
}
