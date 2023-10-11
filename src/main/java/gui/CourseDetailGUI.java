package gui;

import constants.UIConstants;
import domain.Course;
import domain.User;
import gui.sub.BackgroundPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class CourseDetailGUI extends JFrame {
    private Course course;

    private User user;

    public CourseDetailGUI(Course course, User user) {
        this.setTitle(course.getCourseName());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setResizable(false);
        this.setSize(500, 450);
        this.setLocationRelativeTo(null);
        this.course = course;
        this.user = user;

        JPanel backgroundPanel = getBackgroundPanel(UIConstants.COURSE_DETAIL_BACKGROUND_IMAGE);
        if (backgroundPanel != null) {
            addComponents(backgroundPanel);
        }
    }

    private JPanel getBackgroundPanel(String backgroundImagePath) {
        try {
            BufferedImage backgroundImage = ImageIO.read(new File(backgroundImagePath));
            return new BackgroundPanel(backgroundImage);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void addComponents(JPanel panel) {
        SpringLayout springLayout = new SpringLayout();
        panel.setLayout(springLayout);

        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Dialog", Font.BOLD, 15));
        springLayout.putConstraint(SpringLayout.WEST, backButton, 5, SpringLayout.WEST, panel);
        springLayout.putConstraint(SpringLayout.NORTH, backButton, 5, SpringLayout.NORTH, panel);
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CourseDetailGUI.this.dispose();
                new CourseGUI(user);
            }
        });
        panel.add(backButton);

        String[] labels = {"Course ID:", "Course Name:", "Course Instructor:", "Course Description:"};
        JTextArea[] textAreas = {
                createJTextArea(String.valueOf(course.getCourseID())),
                createJTextArea(course.getCourseName()),
                createJTextArea(course.getInstructor()),
                createJTextArea(course.getCourseDescription())
        };

        for (int i = 0; i < labels.length; i++) {
            JLabel label = createJLabel(labels[i]);
            JTextArea textArea = textAreas[i];

            springLayout.putConstraint(SpringLayout.WEST, label, 35, SpringLayout.WEST, panel);
            springLayout.putConstraint(SpringLayout.NORTH, label, 50 + 60 * i, SpringLayout.NORTH, panel);
            panel.add(label);

            springLayout.putConstraint(SpringLayout.WEST, textArea, 230, SpringLayout.WEST, panel);
            springLayout.putConstraint(SpringLayout.EAST, textArea, -35, SpringLayout.EAST, panel);
            springLayout.putConstraint(SpringLayout.NORTH, textArea, 50 + 60 * i, SpringLayout.NORTH, panel);
            panel.add(textArea);
        }

        getContentPane().add(panel);
    }

    private JTextArea createJTextArea(String text) {
        JTextArea textArea = new JTextArea(text);
        textArea.setFont(new Font("Dialog", Font.BOLD, 18));
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        textArea.setEditable(false);
        textArea.setOpaque(false);
        return textArea;
    }

    private JLabel createJLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Dialog", Font.BOLD, 18));
        return label;
    }
}
