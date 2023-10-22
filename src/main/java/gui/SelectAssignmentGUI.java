package gui;

import dao.AssignmentDaoInterface;
import dao.CourseDaoInterface;
import dao.impl.AssignmentDao;
import domain.Course;
import domain.User;
import dao.impl.CourseDao;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class SelectAssignmentGUI extends JFrame{
    private JPanel panel;
    private JButton backButton;
    private JButton operationButton;
    private JList<String> courseList;
    private JList<String> assignmentList;
    private JScrollPane coursePane;
    private JScrollPane assignmentPane;
    private JPanel pnlList;


    public SelectAssignmentGUI(User user){
        AssignmentDaoInterface assignmentDao = new AssignmentDao();
        CourseDaoInterface courseDao = new CourseDao();
        DefaultListModel<String> courseListModel = new DefaultListModel<>();
        DefaultListModel<String> assignmentListModel = new DefaultListModel<>();

        String buttonText = user.isAdmin() ? "Arrange" : "Submit";
        operationButton.setText(buttonText);

        ActionListener OperationButtonActionListener = e -> {
            if (user.isAdmin()){
                //button action when user is admin
            } else {
                //button action when user is NOT admin
            }
        };

        operationButton.addActionListener(OperationButtonActionListener);


        // Get the courseList and convert it to courseNameList showed in courseList
        List<String> CourseNames = getCourseNames(courseDao.getCourseByUser(user));

        for (String assignmentName : CourseNames) {
            courseListModel.addElement(assignmentName);
        }

        // Set the model for the lists
        courseList.setModel(courseListModel);
        assignmentList.setModel(assignmentListModel);

        // According to the selected course, show the assignments of that
        courseList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                // Retrieve the selected course
                String selectedCourse = courseList.getSelectedValue();

                // Convert the selectedCourse to assignmentNames.
                List<String> assignmentNames = assignmentDao.getAssignmentNameByCourseID(courseDao.getCourseIDByName(selectedCourse));

                // Clear the assignmentListModel
                assignmentListModel.clear();

                for (String assignmentName : assignmentNames) {
                    assignmentListModel.addElement(assignmentName);
                }

                assignmentList.setModel(assignmentListModel);
            }
        });

        assignmentList.addListSelectionListener(e -> {
            SelectAssignmentGUI.this.dispose();
            String selectedAssignment = assignmentList.getSelectedValue();
            new SubmissionGUI(user, assignmentDao.getAssignmentByAssignmentName(selectedAssignment));
        });

        backButton.addActionListener(e -> {
            SelectAssignmentGUI.this.dispose();
            new MainGUI(user);
        });

        setContentPane(panel);
        setTitle("Course and Assignment");
        setSize(500, 500);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        setLocationRelativeTo(null);
    }

    /**
     * Retrieves the names of courses from the provided list of Course objects.
     *
     * @param courseList The list of Course objects from which to retrieve the names.
     * @return A List containing the names of the courses.
     */
    private static List<String> getCourseNames(List<Course> courseList){
        List<String> courseNames = new ArrayList<>();
        for (Course course : courseList) {
            courseNames.add(course.getCourseName());
        }
        return courseNames;
    }
}
