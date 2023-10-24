package gui.sub;

import constants.UIConstants;
import dao.CourseSelectionDaoInterface;
import dao.impl.CourseSelectionDao;
import domain.Course;
import domain.User;
import domain.enums.CourseDetailPageFrom;
import gui.CourseGUI;
import gui.UserCoursesGUI;
import gui.sub.success.SelectOrQuitCourseSuccessGUI;
import lombok.extern.slf4j.Slf4j;
import util.FrameUtil;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;

@Slf4j
public class CourseDetailGUI extends JFrame {
    private Course course;
    private final User user;

    private final CourseDetailPageFrom courseDetailPageFrom;
    private final CourseSelectionDaoInterface courseSelectionDao;

    public CourseDetailGUI(Course course, User user, CourseDetailPageFrom courseDetailPageFrom) {
        this.setTitle(course.getCourseName());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setResizable(false);
        this.setSize(700, 470);
        this.setLocationRelativeTo(null);
        this.course = course;
        this.user = user;
        this.courseDetailPageFrom = courseDetailPageFrom;
        this.courseSelectionDao = new CourseSelectionDao();

        JPanel backgroundPanel = getBackgroundPanel();
//        JPanel backgroundPanel = new JPanel();
        if (backgroundPanel != null) {
            addComponents(backgroundPanel);
        }
    }

    private JPanel getBackgroundPanel() {
        try {
            BufferedImage backgroundImage = ImageIO.read(new File(UIConstants.COURSE_DETAIL_BACKGROUND_IMAGE));
            return new BackgroundPanel(backgroundImage);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void addComponents(JPanel panel) {
        boolean userEnrolled = courseSelectionDao.checkIfUserEnrolled(user, course);

        SpringLayout springLayout = new SpringLayout();
        panel.setLayout(springLayout);

        ActionListener backToSpecificGUI = e -> backToSpecificGUI();
        FrameUtil.addBackButtonWithCustomAction(panel, springLayout, backToSpecificGUI);

        Date selectionDate = courseSelectionDao.getCourseSelectionDate(course, user);
        String formattedDate = selectionDate != null ? String.valueOf(selectionDate) : "No Selection Date";

        String[] labels = {"Course ID:", "Course Name:", "Course Instructor:", "Course Description:", "Enrolled:", "Select Date"};
        JTextArea[] textAreas = {
                createJTextArea(String.valueOf(course.getCourseID())),
                createJTextArea(course.getCourseName()),
                createJTextArea(course.getInstructor()),
                createJTextArea(course.getCourseDescription()),
                createJTextArea(userEnrolled ? "Enrolled" : "Not Enrolled"),
                createJTextArea(formattedDate)
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

        ActionListener selectButtonListener = e -> performSelecting();
        ActionListener quitButtonListener = e -> performQuit();

        if (userEnrolled){
            JButton quitCourseButton = new JButton("Quit");
            quitCourseButton.setFont(new Font("Dialog", Font.PLAIN, 15));
            springLayout.putConstraint(SpringLayout.WEST, quitCourseButton, 250, SpringLayout.WEST, panel);
            springLayout.putConstraint(SpringLayout.EAST, quitCourseButton, -250, SpringLayout.EAST, panel);
            springLayout.putConstraint(SpringLayout.NORTH, quitCourseButton, 410, SpringLayout.NORTH, panel);
            quitCourseButton.addActionListener(quitButtonListener);
            panel.add(quitCourseButton);
        }else{
            JButton addCourseButton = new JButton("Select");
            addCourseButton.setFont(new Font("Dialog", Font.PLAIN, 15));
            springLayout.putConstraint(SpringLayout.WEST, addCourseButton, 250, SpringLayout.WEST, panel);
            springLayout.putConstraint(SpringLayout.EAST, addCourseButton, -250, SpringLayout.EAST, panel);
            springLayout.putConstraint(SpringLayout.NORTH, addCourseButton, 410, SpringLayout.NORTH, panel);
            addCourseButton.addActionListener(selectButtonListener);
            panel.add(addCourseButton);
        }

        getContentPane().add(panel);
    }

    private void backToSpecificGUI() {
        CourseDetailGUI.this.dispose();
        switch (courseDetailPageFrom){
            case COURSE_PAGE: new CourseGUI(user);
                break;
            case USER_COURSE_PAGE: new UserCoursesGUI(user);
                break;
            default:
                break;
        }
    }

    private void performQuit() {
        boolean quit = quitCourse();
        if (quit){
            CourseDetailGUI.this.dispose();
            new SelectOrQuitCourseSuccessGUI(new JFrame(), user, course, courseDetailPageFrom);
            log.info("Successfully quit course. Course name:  " + course.getCourseName());
        } else {
            FrameUtil.showErrorDialog("Failed to quit!");
            log.error("quit course:  " + course.getCourseName() + " Failed!");
        }
    }

    private void performSelecting() {
        if (userEnrolledBefore()){
            boolean success = reenrolledUserInCourse();
            if (success){
                CourseDetailGUI.this.dispose();
                new SelectOrQuitCourseSuccessGUI(new JFrame(), user, course, courseDetailPageFrom);
                log.info("Successfully re-enrolled user in course. Course name:  " + course.getCourseName());
            } else {
                FrameUtil.showErrorDialog("Failed to re-enrolled!");
                log.error("Re-enrolled user in course. Course name:  " + course.getCourseName() + " Failed!");
            }
        } else {
            boolean success = setUserEnrolled();
            if (success){
                CourseDetailGUI.this.dispose();
                new SelectOrQuitCourseSuccessGUI(new JFrame(), user, course, courseDetailPageFrom);
                log.info("Successfully selecting course. Course name: " + course.getCourseName());
            } else {
                FrameUtil.showErrorDialog("Failed to select!");
                log.error("Selecting course. Course name:  " + course.getCourseName() + " Failed!");
            }
        }
    }

    private boolean quitCourse() {
        return courseSelectionDao.setUnselectedToSelectionStatus(user, course);
    }

    private boolean reenrolledUserInCourse() {
        return courseSelectionDao.reenrolled(user, course);
    }

    private boolean setUserEnrolled() {
        return courseSelectionDao.newUserEnrolledRecord(user, course);
    }

    private boolean userEnrolledBefore() {
        return courseSelectionDao.selectionStatusEqualsUnselected(user, course);
    }

    private JTextArea createJTextArea(String text) {
        JTextArea textArea = new JTextArea(text);
        textArea.setFont(new Font("Dialog", Font.PLAIN, 18));
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
