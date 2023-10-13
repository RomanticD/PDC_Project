import gui.LoginGUI;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;

@Slf4j
public class PDC_App {
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                log.info("Application Started.....");
                log.info("Group 18 Members: Junhua Di, Yicheng Wang, Yuliang Sun");
                new LoginGUI().setVisible(true);
            }
        });
    }
}
