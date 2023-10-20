package gui;

import domain.Course;
import domain.User;
import dao.impl.CourseDao;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class AssignmentGUI extends JFrame{
    private JPanel panel;
    private JButton backButton;
    private JButton arrangeButton;
    private JList<String> courseList;
    private JList<String> assignmentList;
    private JScrollPane coursesPane;
    private JScrollPane assignmentsPane;
    private JPanel pnlList;

    public AssignmentGUI(User user){
        CourseDao courseDao = new CourseDao();

        DefaultListModel<String> courseListModel = new DefaultListModel<>();

        List<String> CourseNames = getCourseNames(courseDao.getCourseByUser(user));
        // Get the courseList and convert it to courseNameList

        for (String assignmentName : CourseNames) {
            courseListModel.addElement(assignmentName);
        }
        courseList.setModel(courseListModel);

        setContentPane(panel);
        setTitle("Welcome");
        setSize(500, 500);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        setLocationRelativeTo(null);
    }

    // Transform the courseList to courseName
    private static List<String> getCourseNames(List<Course> courseList){
        List<String> courseNames = new ArrayList<>();

        for (Course course : courseList) {
            courseNames.add(course.getCourseName());
        }

        return courseNames;
    }
}
