package gui;

import dao.AssignmentDaoInterface;
import dao.CourseDaoInterface;
import dao.impl.AssignmentDao;
import domain.User;
import dao.impl.CourseDao;
import util.FrameUtil;

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
    private JButton deleteButton;

    public SelectAssignmentGUI(User user){
        // In this GUI, you can select an assignment from selected courses to submit or arrange.
        if(!user.isAdmin()){
            createNewButton.setVisible(false);
            deleteButton.setVisible(false);
        }

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
                List<String> assignmentNames = assignmentDao.getAssignmentNamesByCourseID(courseDao.getCourseIDByName(selectedCourse));

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

        deleteButton.addActionListener(e -> {
            String selectedCourse = courseList.getSelectedValue();
            String selectedAssignment = assignmentList.getSelectedValue();
            if(Objects.equals(selectedAssignment, null)){
                FrameUtil.showConfirmation(SelectAssignmentGUI.this, user, "You haven't selected any assignment!");
            } else {
                if(assignmentDao.deleteAssignment(assignmentDao.getAssignmentByAssignmentAndCourseName(selectedAssignment, selectedCourse))){
                    FrameUtil.showConfirmation(SelectAssignmentGUI.this, user, "Delete successfully!");
                } else {
                    FrameUtil.showConfirmation(SelectAssignmentGUI.this, user, "Something wrong or the assignment doesn't exist");
                }
            }
        });

        createNewButton.addActionListener(e -> {
            SelectAssignmentGUI.this.dispose();
            new ArrangementGUI(user);
        });

        selectButton.addActionListener(e -> {
            String selectedCourse = courseList.getSelectedValue();
            String selectedAssignment = assignmentList.getSelectedValue();
            if(Objects.equals(selectedAssignment, null)){
                FrameUtil.showConfirmation(SelectAssignmentGUI.this, user, "You haven't selected any assignment");
            } else {
                if (user.isAdmin()){
                    SelectAssignmentGUI.this.dispose();
                    new ArrangementGUI(user, assignmentDao.getAssignmentByAssignmentAndCourseName(selectedAssignment, selectedCourse));
                } else {
                    SelectAssignmentGUI.this.dispose();
                    new SubmissionGUI(user, assignmentDao.getAssignmentByAssignmentAndCourseName(selectedAssignment, selectedCourse));
                }
            }
        });

        setContentPane(panel);
        setTitle("Select your assignment");
        setSize(500, 500);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        setLocationRelativeTo(null);
    }
}
