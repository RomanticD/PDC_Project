package gui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import dao.AssignmentDaoInterface;
import dao.CourseDaoInterface;
import dao.impl.AssignmentDao;
import domain.Assignment;
import domain.User;
import dao.impl.CourseDao;
import util.FrameUtil;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class SelectAssignmentGUI extends JFrame {
    private JPanel panel;
    private JButton backButton;
    private JButton checkButton;
    private JList<String> courseList;
    private JList<String> assignmentList;
    private JScrollPane coursePane;
    private JScrollPane assignmentPane;
    private JPanel pnlList;
    private JLabel courses;
    private JLabel assignments;
    private JButton createNewButton;
    private JButton deleteButton;
    private JLabel explainLabel;
    private JButton correctButton;
    private JLabel deadLineLabel;
    private JLabel concreteTime;

    public SelectAssignmentGUI(User user) {
        // In this GUI, you can select an assignment from selected courses to submit or arrange.
        if (user.isAdmin()) {
            explainLabel.setText("Correct, Alter, New or Delete assignments.");
        } else {
            explainLabel.setText("Select an assignment, completing and submitting it. Let's go!");
            correctButton.setText("Select");
            checkButton.setVisible(false);
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
                concreteTime.setText("No deadline");

                for (String assignmentName : assignmentNames) {
                    assignmentListModel.addElement(assignmentName);
                }

                assignmentList.setModel(assignmentListModel);
            }
        });

        correctButton.setEnabled(false);
        assignmentList.addListSelectionListener(e -> {
            String selectedCourse = courseList.getSelectedValue();
            String selectedAssignment = assignmentList.getSelectedValue();
            if (selectedCourse != null && selectedAssignment != null) {
                Assignment assignment = assignmentDao.getAssignmentByAssignmentAndCourseName(selectedAssignment, selectedCourse);

                correctButton.setEnabled(assignment.getDeadLine() == null || !assignment.getDeadLine().after(new Date()));
                if (assignment.getDeadLine() != null) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String formattedDeadline = dateFormat.format(assignment.getDeadLine());
                    concreteTime.setText(formattedDeadline);
                } else {
                    concreteTime.setText("No deadline");
                }
            }
        });

        backButton.addActionListener(e -> {
            SelectAssignmentGUI.this.dispose();
            new MainGUI(user);
        });

        deleteButton.addActionListener(e -> {
            String selectedCourse = courseList.getSelectedValue();
            String selectedAssignment = assignmentList.getSelectedValue();

            if (Objects.equals(selectedAssignment, null)) {
                FrameUtil.showConfirmation(SelectAssignmentGUI.this, "You haven't selected any assignment!");
            } else {
                if (assignmentDao.deleteAssignment(assignmentDao.getAssignmentByAssignmentAndCourseName(selectedAssignment, selectedCourse))) {
                    FrameUtil.showConfirmation(SelectAssignmentGUI.this, "Delete successfully!");
                } else {
                    FrameUtil.showConfirmation(SelectAssignmentGUI.this, "Something wrong or the assignment doesn't exist");
                }
            }
            new SelectAssignmentGUI(user);
        });

        createNewButton.addActionListener(e -> {
            SelectAssignmentGUI.this.dispose();
            new ManageAssignmentGUI(user);
        });

        checkButton.addActionListener(e -> {
            String selectedCourse = courseList.getSelectedValue();
            String selectedAssignment = assignmentList.getSelectedValue();

            if (Objects.equals(selectedAssignment, null)) {
                FrameUtil.showConfirmation(SelectAssignmentGUI.this, "You haven't selected any assignment!");
                new SelectAssignmentGUI(user);
            } else {
                SelectAssignmentGUI.this.dispose();
                new ManageAssignmentGUI(user, assignmentDao.getAssignmentByAssignmentAndCourseName(selectedAssignment, selectedCourse));
            }
        });

        correctButton.addActionListener(e -> {
            String selectedCourse = courseList.getSelectedValue();
            String selectedAssignment = assignmentList.getSelectedValue();
            Assignment assignment = assignmentDao.getAssignmentByAssignmentAndCourseName(selectedAssignment, selectedCourse);

            if (Objects.equals(selectedAssignment, null)) {
                FrameUtil.showConfirmation(SelectAssignmentGUI.this, "You haven't selected any assignment!");
                new SelectAssignmentGUI(user);
            } else {
                if (user.isAdmin()) {
                    SelectAssignmentGUI.this.dispose();
                    new CorrectOrCheckGUI(user, assignment);
                } else {
                    SelectAssignmentGUI.this.dispose();
                    new SubmissionGUI(user, assignment);
                }
            }
        });

        setContentPane(panel);
        setTitle("Select your assignment");
        setSize(600, 500);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        setLocationRelativeTo(null);
    }

}
