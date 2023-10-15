package util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

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

    public static void showDialog(String message){
        Object[] options = {"OK"};
        JOptionPane.showOptionDialog(null, message,
                "Account Exists", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
                null, options, options[0]);
    }

    public static void addBackButton(JPanel containerPanel, SpringLayout layout, JFrame currentFrame, Class<? extends JFrame> backToFrameClass, Object... args) {
        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Dialog", Font.BOLD, 15));
        layout.putConstraint(SpringLayout.WEST, backButton, 5, SpringLayout.WEST, containerPanel);
        layout.putConstraint(SpringLayout.NORTH, backButton, 5, SpringLayout.NORTH, containerPanel);
        backButton.addActionListener((ActionEvent e) -> {
            currentFrame.dispose();
            try {
                Class[] argClasses = new Class[args.length];
                for (int i = 0; i < args.length; i++) {
                    argClasses[i] = args[i].getClass();
                }
                Constructor<? extends JFrame> constructor = backToFrameClass.getConstructor(argClasses);
                JFrame backToFrame;
                if (args.length == 0) {
                    backToFrame = backToFrameClass.getDeclaredConstructor().newInstance();
                } else {
                    backToFrame = constructor.newInstance(args);
                }
                backToFrame.setVisible(true);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e1) {
                e1.printStackTrace();
            }
        });
        containerPanel.add(backButton);
    }

    public static void addBackButtonWithCustomAction(JPanel containerPanel, SpringLayout layout, ActionListener customAction) {
        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Dialog", Font.BOLD, 15));
        layout.putConstraint(SpringLayout.WEST, backButton, 5, SpringLayout.WEST, containerPanel);
        layout.putConstraint(SpringLayout.NORTH, backButton, 5, SpringLayout.NORTH, containerPanel);
        backButton.addActionListener(customAction);
        containerPanel.add(backButton);
    }
}
