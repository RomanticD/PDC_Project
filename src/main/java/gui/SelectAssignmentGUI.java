package gui;

import dao.AssignmentDaoInterface;
import dao.CourseDaoInterface;
import dao.impl.AssignmentDao;
import domain.User;
import dao.impl.CourseDao;

import javax.swing.*;
import java.awt.event.ActionListener;
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
    private JButton createNewButton;

    public SelectAssignmentGUI(User user){
        // In this GUI, you can select an assignment from selected courses to submit or arrange.

        AssignmentDaoInterface assignmentDao = new AssignmentDao();
        CourseDaoInterface courseDao = new CourseDao();
        DefaultListModel<String> courseListModel = new DefaultListModel<>();
        DefaultListModel<String> assignmentListModel = new DefaultListModel<>();

        // Get the courseList and convert it to courseNameList showed in courseList
        List<String> CourseNames = courseDao.getCourseNames(courseDao.getCourseByUser(user));

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

        backButton.addActionListener(e -> {
            SelectAssignmentGUI.this.dispose();
            new MainGUI(user);
        });

        if(user.isAdmin()){
            createNewButton.addActionListener(e -> {
                SelectAssignmentGUI.this.dispose();
                new ArrangementGUI(user);
            });
        } else {
            createNewButton.setVisible(false);
        }

        ActionListener selectButtonActionListener = e -> {
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
                    System.out.println("User clicked 'Yes'");
                    this.dispose();
                    new SelectAssignmentGUI(user);
                }
            } else {
                if (user.isAdmin()){
                    SelectAssignmentGUI.this.dispose();
                    new ArrangementGUI(user, assignmentDao.getAssignmentByAssignmentName(selectedAssignment));
                } else {
                    SelectAssignmentGUI.this.dispose();
                    new SubmissionGUI(user, assignmentDao.getAssignmentByAssignmentName(selectedAssignment));
                }
            }
        };
        selectButton.addActionListener(selectButtonActionListener);

        setContentPane(panel);
        setTitle("Select your assignment");
        setSize(500, 500);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        setLocationRelativeTo(null);
    }
}
