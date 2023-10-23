package gui;

import dao.impl.AssignmentDao;
import domain.Course;
import domain.User;
import dao.impl.CourseDao;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SelectAssignmentGUI extends JFrame{
    private JPanel panel;
    private JButton backButton;
    private JButton selectButton;
    private JList<String> courseList;
    private JList<String> assignmentList;
    private JScrollPane coursePane;
    private JScrollPane assignmentPane;
    private JPanel pnlList;
    private JLabel courses;
    private JLabel assignments;

    public SelectAssignmentGUI(User user){
        // In this GUI, you can select an assignment from selected courses to submit or arrange.

        AssignmentDao assignmentDao = new AssignmentDao();
        CourseDao courseDao = new CourseDao();
        DefaultListModel<String> courseListModel = new DefaultListModel<>();
        DefaultListModel<String> assignmentListModel = new DefaultListModel<>();

        // Get the courseList and convert it to courseNameList showed in courseList
        List<String> CourseNames = getCourseNames(courseDao.getCourseByUser(user));

        for (String assignmentName : CourseNames) {
            courseListModel.addElement(assignmentName);
        }

        // Set the model for the lists
        courseList.setModel(courseListModel);
        assignmentList.setModel(assignmentListModel);

        // According to the selected course, show the assignments of that
        courseList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
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
            }
        });

        selectButton.addActionListener(e -> {
            String selectedAssignment = assignmentList.getSelectedValue();
            if(Objects.equals(selectedAssignment, null)){
                int option = JOptionPane.showOptionDialog(
                        SelectAssignmentGUI.this,   // Parent component (this JFrame)
                        "You haven't selected the assignment", // Message
                        "Confirmation",  // Title
                        JOptionPane.YES_NO_OPTION,  // Option type
                        JOptionPane.QUESTION_MESSAGE, // Message type
                        null,  // Icon (null for default)
                        new String[] {"OK"}, // Custom button text
                        "OK" // Default button text
                );

                if (option == 0) {
                    // User clicked "Yes"
                    // Add your code here to handle "Yes" option
                    System.out.println("User clicked 'Yes'");
                }
            } else {
                SelectAssignmentGUI.this.dispose();
                new SubmissionGUI(user, assignmentDao.getAssignmentByAssignmentName(selectedAssignment));
            }
        });

        backButton.addActionListener(e -> {
            SelectAssignmentGUI.this.dispose();
            new MainGUI(user);
        });

        setContentPane(panel);
        setTitle("Select your assignment");
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
