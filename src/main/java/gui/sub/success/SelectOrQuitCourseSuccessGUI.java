package gui.sub.success;

import constants.UIConstants;
import domain.Course;
import domain.User;
import domain.enums.CourseDetailPageFrom;
import gui.sub.BackgroundPanel;
import gui.sub.CourseDetailGUI;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class SelectOrQuitCourseSuccessGUI {
    private JFrame frame;

    private final User user;

    private final Course course;

    private final CourseDetailPageFrom courseDetailPageFrom;

    public SelectOrQuitCourseSuccessGUI(User user, Course course, CourseDetailPageFrom courseDetailPageFrom) {
        this.course = course;
        this.user = user;
        this.courseDetailPageFrom = courseDetailPageFrom;
        init();
    }

    private JPanel getBackgroundPanel() {
        try {
            BufferedImage backgroundImage = ImageIO.read(new File(UIConstants.CHANGE_INFO_GUI_IMAGE));
            return new BackgroundPanel(backgroundImage);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void init() {
        frame = new JFrame();
        frame.setSize(230, 200);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel backgroundPanel = getBackgroundPanel();
        backgroundPanel.setLayout(new BorderLayout());

        JLabel successMessage = new JLabel("Success!", SwingConstants.CENTER);
        successMessage.setFont(new Font("Dialog", Font.BOLD, 18));

        backgroundPanel.add(successMessage, BorderLayout.CENTER);

        JButton returnButton = new JButton("Return to Course Page");
        returnButton.setFont(new Font("Dialog", Font.BOLD, 15));
        returnButton.addActionListener((ActionEvent e) -> {
            new CourseDetailGUI(course, user, courseDetailPageFrom).setVisible(true);
            frame.dispose();
        });
        returnButton.setPreferredSize(new Dimension(50, 50));

        backgroundPanel.add(returnButton, BorderLayout.SOUTH);

        frame.add(backgroundPanel);

        frame.setVisible(true);
    }
}
