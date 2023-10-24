package test;

import javax.swing.*;

import static util.FrameUtil.addUploadedButton;

public class ButtonTest {
    public static void main(String[] args) {
        JPanel panel = new JPanel();
        panel.add(addUploadedButton());
        JFrame frame = new JFrame();
        frame.add(panel);
        frame.setTitle("test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setSize(500, 370);






    }
}
