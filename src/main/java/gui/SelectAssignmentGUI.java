package gui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import dao.AssignmentDaoInterface;
import dao.CourseDaoInterface;
import dao.impl.AssignmentDao;
import domain.User;
import dao.impl.CourseDao;
import util.FrameUtil;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
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

            if (Objects.equals(selectedAssignment, null)) {
                FrameUtil.showConfirmation(SelectAssignmentGUI.this, "You haven't selected any assignment!");
                new SelectAssignmentGUI(user);
            } else {
                if (user.isAdmin()) {
                    SelectAssignmentGUI.this.dispose();
                    new CorrectOrCheckGUI(user, assignmentDao.getAssignmentByAssignmentAndCourseName(selectedAssignment, selectedCourse));
                } else {
                    SelectAssignmentGUI.this.dispose();
                    new SubmissionGUI(user, assignmentDao.getAssignmentByAssignmentAndCourseName(selectedAssignment, selectedCourse));
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

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panel = new JPanel();
        panel.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        final JToolBar toolBar1 = new JToolBar();
        panel.add(toolBar1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 20), null, 0, false));
        backButton = new JButton();
        backButton.setBackground(new Color(-16777216));
        backButton.setText("Back");
        toolBar1.add(backButton);
        explainLabel = new JLabel();
        explainLabel.setText("Some explains");
        toolBar1.add(explainLabel);
        final Spacer spacer1 = new Spacer();
        toolBar1.add(spacer1);
        deleteButton = new JButton();
        deleteButton.setBackground(new Color(-15526864));
        deleteButton.setForeground(new Color(-11517211));
        deleteButton.setHorizontalAlignment(0);
        deleteButton.setText("Delete");
        toolBar1.add(deleteButton);
        createNewButton = new JButton();
        createNewButton.setText("New");
        toolBar1.add(createNewButton);
        checkButton = new JButton();
        checkButton.setHorizontalAlignment(0);
        checkButton.setText("Check");
        checkButton.setVerticalAlignment(0);
        checkButton.setVerticalTextPosition(0);
        toolBar1.add(checkButton);
        correctButton = new JButton();
        correctButton.setText("Correct");
        toolBar1.add(correctButton);
        pnlList = new JPanel();
        pnlList.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel.add(pnlList, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        pnlList.setBorder(BorderFactory.createTitledBorder(null, "", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        coursePane = new JScrollPane();
        pnlList.add(coursePane, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        courseList = new JList();
        final DefaultListModel defaultListModel1 = new DefaultListModel();
        courseList.setModel(defaultListModel1);
        courseList.putClientProperty("List.isFileList", Boolean.FALSE);
        coursePane.setViewportView(courseList);
        assignmentPane = new JScrollPane();
        pnlList.add(assignmentPane, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        assignmentList = new JList();
        assignmentPane.setViewportView(assignmentList);
        courses = new JLabel();
        courses.setText("Your courses");
        pnlList.add(courses, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        assignments = new JLabel();
        assignments.setText("Your assignments");
        pnlList.add(assignments, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel;
    }

}
