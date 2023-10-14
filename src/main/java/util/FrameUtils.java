package util;

import javax.swing.*;

public class FrameUtils {

    public static void disposeCurrentFrameAndCreateNewFrame(String title, JPanel currentPanel, JPanel newPanel) {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(currentPanel);
        frame.dispose();

        JFrame newFrame = new JFrame(title);
        newFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        newFrame.add(newPanel);
        newFrame.pack();
        newFrame.setLocationRelativeTo(null);
        newFrame.setVisible(true);
    }

    public static void disposeCurrentFrameAndCreateNewFrame(String title, JFrame currentFrame, JPanel newPanel) {
        currentFrame.dispose();

        JFrame newFrame = new JFrame(title);
        newFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        newFrame.add(newPanel);
        newFrame.pack();
        newFrame.setLocationRelativeTo(null);
        newFrame.setVisible(true);
    }
}
