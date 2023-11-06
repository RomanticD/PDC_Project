package gui.sub.success;

import constants.UIConstants;
import domain.Course;
import domain.User;
import domain.enums.CourseDetailPageFrom;
import gui.course.CourseDetailGUI;
import javax.swing.*;
import java.awt.event.ActionEvent;

public class SelectOrQuitCourseSuccessGUI extends BaseSuccessGUI {
    public SelectOrQuitCourseSuccessGUI(JFrame frame, User user, Course course, CourseDetailPageFrom courseDetailPageFrom) {
        super(frame, UIConstants.CHANGE_INFO_GUI_IMAGE, "Success!", "Return to Course Page", (ActionEvent e) -> {
            new CourseDetailGUI(course, user, courseDetailPageFrom).setVisible(true);
        });
    }
}
