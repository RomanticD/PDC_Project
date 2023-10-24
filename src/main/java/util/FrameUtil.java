package util;

import domain.User;
import gui.DirectoryCopyUI;
import gui.SelectAssignmentGUI;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

@Slf4j
public class FrameUtil {

    /**
     * Disposes the current frame and creates a new frame with the specified title and panel.
     * @param title Title of the new frame.
     * @param currentPanel Current panel to be disposed.
     * @param newPanel New panel to be added to the new frame.
     */
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

    /**
     * Disposes the current frame and creates a new frame with the specified title and panel.
     * @param title Title of the new frame.
     * @param currentFrame Current frame to be disposed.
     * @param newPanel New panel to be added to the new frame.
     */
    public static void disposeCurrentFrameAndCreateNewFrame(String title, JFrame currentFrame, JPanel newPanel) {
        currentFrame.dispose();

        JFrame newFrame = new JFrame(title);
        newFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        newFrame.add(newPanel);
        newFrame.pack();
        newFrame.setLocationRelativeTo(null);
        newFrame.setVisible(true);
    }

    /**
     * Displays a dialog with the specified message.
     * @param message Message to be displayed in the dialog.
     */
    public static void showDialog(String message){
        Object[] options = {"OK"};
        JOptionPane.showOptionDialog(null, message,
                "Warning", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
                null, options, options[0]);
    }

    /**
     * Adds a back button to the container panel that, when clicked, disposes the current frame and creates a new frame
     * specified by the provided class and arguments.
     *
     * @param containerPanel   Container panel to which the back button will be added.
     * @param layout           Layout of the container panel.
     * @param currentFrame     Current frame to be disposed.
     * @param backToFrameClass Class of the frame to be created when the back button is clicked.
     * @param args             Arguments to be passed to the constructor of the frame being created.
     */
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
                log.error("Error occurred when adding BackButton: " + e1.getMessage());
            }
        });
        containerPanel.add(backButton);
    }

    /**
     * Adds a back button to the container panel with a specified action to be performed when clicked.
     * @param containerPanel Container panel to which the back button will be added.
     * @param layout Layout of the container panel.
     * @param customAction Custom action to be performed when the back button is clicked.
     */
    public static void addBackButtonWithCustomAction(JPanel containerPanel, SpringLayout layout, ActionListener customAction) {
        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Dialog", Font.BOLD, 15));
        layout.putConstraint(SpringLayout.WEST, backButton, 5, SpringLayout.WEST, containerPanel);
        layout.putConstraint(SpringLayout.NORTH, backButton, 5, SpringLayout.NORTH, containerPanel);
        backButton.addActionListener(customAction);
        containerPanel.add(backButton);
    }

    public static void showConfirmation(JFrame GUI, User user, String message){
        int option = JOptionPane.showOptionDialog(
                GUI,   // Parent component (this JFrame)
                message, // Message
                "Confirmation",  // Title
                JOptionPane.YES_NO_OPTION,  // Option type
                JOptionPane.QUESTION_MESSAGE, // Message type
                null,  // Icon (null for default)
                new String[] {"OK"}, // Custom button text
                "OK" // Default button text
        );

        if (option == 0) {
            // User clicked "Yes"
            log.info("User clicked Yes");
            GUI.dispose();
            new SelectAssignmentGUI(user);
        }
    }

    public static JButton addUploadedButton(JPanel containerPanel){
        JButton uploadButton = new JButton("Back");
        uploadButton.setFont(new Font("Dialog", Font.BOLD, 15));
        uploadButton.addActionListener(e -> {
            new DirectoryCopyUI().setVisible(true);
        });
        containerPanel.add(uploadButton);
        return uploadButton;
    }
}
