package util;

import domain.User;
import gui.sub.UploadGUI;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
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
     * Displays a dialog with the specified ERROR message.
     * @param message Message to be displayed in the dialog.
     */
    public static void showErrorDialog(String message){
        Object[] options = {"OK"};
        JOptionPane.showOptionDialog(null, message,
                "Warning", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE,
                null, options, options[0]);
    }

    /**
     * Displays a dialog with the specified SUCCESS message.
     * @param message Message to be displayed in the dialog.
     */
    public static void showSuccessDialog(String message){
        Object[] options = {"OK"};
        JOptionPane.showOptionDialog(null, message,
                "Success", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE ,
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

    /**
     * Display a confirmation dialog with a custom message and handle user's response.
     * @param GUI The JFrame on which the confirmation dialog will be displayed.
     * @param message The message to be displayed in the confirmation dialog.
     */
    public static void showConfirmation(JFrame GUI, String message){
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
        }
    }

    /**
     * Create and configure a JButton for uploading files, and attach an ActionListener to it.
     * When the button is clicked, it opens a UploadGUI for file uploading.
     * @return The configured JButton for file uploading.
     */
    public static JButton addUploadedButton(User user){
        JButton uploadButton = new JButton("upload");
        uploadButton.setFont(new Font("Dialog", Font.BOLD, 15));
        uploadButton.addActionListener(e -> {
            new UploadGUI(user).setVisible(true);
        });
        return uploadButton;
    }


    /**
     * Creates an ActionListener that opens the UploadGUI when an action is performed.
     *
     * @return An ActionListener for opening the UploadGUI.
     */
    public static ActionListener uploadAction(User user) {
        return e -> new UploadGUI(user).setVisible(true);
    }

    /**
     * This method ensures that only numeric input is allowed in the provided JFormattedTextField.
     *
     * @param textField The JFormattedTextField in which numeric input is enforced.
     */
    public static void numericInputListener(JFormattedTextField textField) {
        KeyListener numericInputListener = new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c)) {
                    e.consume();
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        };

        textField.addKeyListener(numericInputListener);
    }

    /**
     * This code defines a method to obtain a custom CompoundBorder with rounded corners and padding.
     * The CompoundBorder combines an EmptyBorder for padding and a LineBorder with rounded corners.
     * <p>
     * The resulting border can be applied to Swing components for a visually appealing appearance.
     * @return The custom CompoundBorder with rounded corners and padding.
     */
    public static CompoundBorder getRoundedBorder(){
        return new CompoundBorder(
                new EmptyBorder(10, 10, 10, 10), // Padding
                BorderFactory.createLineBorder(Color.BLACK, 1) // Border
        ) {
            @Override
            public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                g.fillRoundRect(x, y, width - 1, height - 1, 20, 20);
            }
        };
    }

    /**
     * Creates an ActionListener for downloading files specified by the array of file paths.
     * If the array is empty, an error dialog is displayed indicating that no files have been uploaded.
     * Otherwise, the method attempts to open each file using the Desktop class.
     * If the file exists and the Desktop is supported, the file is opened; otherwise, an error is logged.
     * @param userUploadedFilePath an array of file paths representing the files uploaded by the user
     * @return an ActionListener for handling the download action
     */
    public static ActionListener handleDownloadAction(String[] userUploadedFilePath){
        log.info("try to open the file your uploaded...");
        return e -> {
            if (userUploadedFilePath.length == 0){
                showErrorDialog("Student haven't uploaded any file!");
            } else {
                for (String filepath : userUploadedFilePath) {
                    log.info("opening file " + filepath);
                    File file = new File(filepath);
                    if (Desktop.isDesktopSupported()) {
                        Desktop desktop = Desktop.getDesktop();
                        if (file.exists()) {
                            try {
                                desktop.open(file);
                            } catch (IOException ex) {
                                log.error(ex.getMessage());
                            }
                        }
                    }
                }
            }
        };
    }
}
