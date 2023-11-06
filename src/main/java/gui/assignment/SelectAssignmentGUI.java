package gui.assignment;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import domain.Course;
import gui.AdminGUI;
import gui.MainGUI;
import service.AssignmentService;
import service.CourseService;
import service.dao.AssignmentDao;
import domain.Assignment;
import domain.User;
import service.dao.CourseDao;
import util.FrameUtil;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static util.FrameUtil.getRoundedBorder;

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
    private JButton showButton;

    public SelectAssignmentGUI(User user) {
        // In this GUI, you can select an assignment from selected courses to submit or arrange.
        if (user.isAdmin()) {
            explainLabel.setText(" Select the assignment in the course or create a new one");
        } else {
            explainLabel.setText(" Select an assignment, completing and submitting it. Let's go!");
            correctButton.setText("Select");
            checkButton.setVisible(false);
            createNewButton.setVisible(false);
            deleteButton.setVisible(false);
            showButton.setVisible(false);
        }

        AssignmentService assignmentService = new AssignmentDao();
        CourseService courseService = new CourseDao();

        DefaultListModel<String> courseListModel = new DefaultListModel<>();
        DefaultListModel<String> assignmentListModel = new DefaultListModel<>();

        // Get the courseList and convert it to courseNameList showed in courseList
        List<String> selectedCourseNames = courseService.getCourseNames(courseService.getCourseByUser(user));

        for (String assignmentName : selectedCourseNames) {
            courseListModel.addElement(assignmentName);
        }

        // Set the model for the lists
        courseList.setModel(courseListModel);
        coursePane.setBorder(getRoundedBorder());
        assignmentList.setModel(assignmentListModel);

        // According to the selected course, show the assignments of that
        courseList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                // Retrieve the selected course
                String selectedCourse = courseList.getSelectedValue();

                // Convert the selectedCourse to assignmentNames.
                List<String> assignmentNames = assignmentService.getAssignmentNamesByCourseID(courseService.getCourseIDByName(selectedCourse));

                // Clear the assignmentListModel
                assignmentListModel.clear();
                concreteTime.setText("No deadline");
                correctButton.setEnabled(false);

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
                Assignment assignment = assignmentService.getAssignmentByAssignmentAndCourseName(selectedAssignment, selectedCourse);

                correctButton.setEnabled(assignment.getDeadLine() == null || assignment.getDeadLine().after(new Date()));
                if (assignment.getDeadLine() != null) {
                    Date deadline = assignment.getDeadLine();

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    String formattedDeadline = dateFormat.format(deadline);
                    concreteTime.setText(formattedDeadline);
                } else {
                    concreteTime.setText("No deadline");
                }
            }
        });

        backButton.addActionListener(e -> {
            if (user.isAdmin()) {
                SelectAssignmentGUI.this.dispose();
                new AdminGUI(user);
            } else {
                SelectAssignmentGUI.this.dispose();
                new MainGUI(user);
            }
        });

        deleteButton.addActionListener(e -> {
            String selectedCourse = courseList.getSelectedValue();
            String selectedAssignment = assignmentList.getSelectedValue();

            if (Objects.equals(selectedAssignment, null)) {
                FrameUtil.showConfirmation(SelectAssignmentGUI.this, "You haven't selected any assignment!");
            } else {
                if (assignmentService.deleteAssignment(assignmentService.getAssignmentByAssignmentAndCourseName(selectedAssignment, selectedCourse))) {
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
                new ManageAssignmentGUI(user, assignmentService.getAssignmentByAssignmentAndCourseName(selectedAssignment, selectedCourse));
            }
        });

        correctButton.addActionListener(e -> {
            String selectedCourse = courseList.getSelectedValue();
            String selectedAssignment = assignmentList.getSelectedValue();
            Assignment assignment = assignmentService.getAssignmentByAssignmentAndCourseName(selectedAssignment, selectedCourse);

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

        showButton.setText("Show All");
        final boolean[] showAll = {true};
        showButton.addActionListener(e -> {
            courseListModel.clear();
            if (showAll[0]) {
                for (Course course : courseService.getAllCourses()) {
                    courseListModel.addElement(course.getCourseName());
                }
                showButton.setText("Show Yours");
                courses.setText("All courses:");
            } else {
                for (String course : selectedCourseNames) {
                    courseListModel.addElement(course);
                }
                showButton.setText("Show All");
                courses.setText("Your courses:");
            }
            showAll[0] = !showAll[0];
        });

        setContentPane(panel);
        setTitle("Select your assignment");
        setSize(800, 600);
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
        backButton.setBackground(new Color(-2104859));
        backButton.setEnabled(true);
        Font backButtonFont = this.$$$getFont$$$("JetBrains Mono", Font.BOLD, 16, backButton.getFont());
        if (backButtonFont != null) backButton.setFont(backButtonFont);
        backButton.setForeground(new Color(-15526864));
        backButton.setText("Back");
        toolBar1.add(backButton);
        explainLabel = new JLabel();
        Font explainLabelFont = this.$$$getFont$$$("Droid Sans Mono Dotted", Font.PLAIN, 14, explainLabel.getFont());
        if (explainLabelFont != null) explainLabel.setFont(explainLabelFont);
        explainLabel.setForeground(new Color(-2238126));
        explainLabel.setText("Some explains");
        toolBar1.add(explainLabel);
        final Spacer spacer1 = new Spacer();
        toolBar1.add(spacer1);
        deleteButton = new JButton();
        deleteButton.setBackground(new Color(-2104859));
        Font deleteButtonFont = this.$$$getFont$$$("JetBrains Mono", Font.BOLD, 16, deleteButton.getFont());
        if (deleteButtonFont != null) deleteButton.setFont(deleteButtonFont);
        deleteButton.setForeground(new Color(-15526864));
        deleteButton.setHorizontalAlignment(0);
        deleteButton.setText("Delete");
        toolBar1.add(deleteButton);
        createNewButton = new JButton();
        createNewButton.setBackground(new Color(-2104859));
        Font createNewButtonFont = this.$$$getFont$$$("JetBrains Mono", Font.BOLD, 16, createNewButton.getFont());
        if (createNewButtonFont != null) createNewButton.setFont(createNewButtonFont);
        createNewButton.setForeground(new Color(-15526864));
        createNewButton.setText("New");
        toolBar1.add(createNewButton);
        checkButton = new JButton();
        checkButton.setBackground(new Color(-2104859));
        Font checkButtonFont = this.$$$getFont$$$("JetBrains Mono", Font.BOLD, 16, checkButton.getFont());
        if (checkButtonFont != null) checkButton.setFont(checkButtonFont);
        checkButton.setForeground(new Color(-15526864));
        checkButton.setHorizontalAlignment(0);
        checkButton.setText("Check");
        checkButton.setVerticalAlignment(0);
        checkButton.setVerticalTextPosition(0);
        toolBar1.add(checkButton);
        correctButton = new JButton();
        correctButton.setBackground(new Color(-2104859));
        Font correctButtonFont = this.$$$getFont$$$("JetBrains Mono", Font.BOLD, 16, correctButton.getFont());
        if (correctButtonFont != null) correctButton.setFont(correctButtonFont);
        correctButton.setForeground(new Color(-15526864));
        correctButton.setText("Correct");
        toolBar1.add(correctButton);
        pnlList = new JPanel();
        pnlList.setLayout(new GridLayoutManager(3, 5, new Insets(0, 0, 0, 0), -1, -1));
        panel.add(pnlList, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        pnlList.setBorder(BorderFactory.createTitledBorder(null, "", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        coursePane = new JScrollPane();
        coursePane.setBackground(new Color(-2104859));
        coursePane.setForeground(new Color(-2104859));
        pnlList.add(coursePane, new GridConstraints(1, 0, 2, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(175, -1), new Dimension(200, -1), null, 0, false));
        courseList = new JList();
        courseList.setBackground(new Color(-6837066));
        Font courseListFont = this.$$$getFont$$$("Droid Sans Mono", Font.PLAIN, 16, courseList.getFont());
        if (courseListFont != null) courseList.setFont(courseListFont);
        courseList.setForeground(new Color(-16777216));
        final DefaultListModel defaultListModel1 = new DefaultListModel();
        courseList.setModel(defaultListModel1);
        courseList.setSelectionForeground(new Color(-11737629));
        courseList.putClientProperty("List.isFileList", Boolean.FALSE);
        coursePane.setViewportView(courseList);
        assignmentPane = new JScrollPane();
        pnlList.add(assignmentPane, new GridConstraints(1, 3, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(250, -1), new Dimension(400, -1), null, 0, false));
        assignmentList = new JList();
        assignmentList.setBackground(new Color(-6837066));
        Font assignmentListFont = this.$$$getFont$$$("Droid Sans Mono", Font.PLAIN, 16, assignmentList.getFont());
        if (assignmentListFont != null) assignmentList.setFont(assignmentListFont);
        assignmentList.setForeground(new Color(-16777216));
        assignmentList.setSelectionForeground(new Color(-11737629));
        assignmentPane.setViewportView(assignmentList);
        courses = new JLabel();
        Font coursesFont = this.$$$getFont$$$("Droid Sans Mono", Font.PLAIN, 16, courses.getFont());
        if (coursesFont != null) courses.setFont(coursesFont);
        courses.setText("Your courses:");
        pnlList.add(courses, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        assignments = new JLabel();
        Font assignmentsFont = this.$$$getFont$$$("Droid Sans Mono", Font.PLAIN, 16, assignments.getFont());
        if (assignmentsFont != null) assignments.setFont(assignmentsFont);
        assignments.setText("Assignments:");
        pnlList.add(assignments, new GridConstraints(0, 3, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        deadLineLabel = new JLabel();
        deadLineLabel.setBackground(new Color(-4474633));
        Font deadLineLabelFont = this.$$$getFont$$$("Droid Sans Mono", Font.PLAIN, 18, deadLineLabel.getFont());
        if (deadLineLabelFont != null) deadLineLabel.setFont(deadLineLabelFont);
        deadLineLabel.setForeground(new Color(-1727412));
        deadLineLabel.setText("DeadLine:");
        pnlList.add(deadLineLabel, new GridConstraints(2, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(50, 100), null, 0, false));
        concreteTime = new JLabel();
        Font concreteTimeFont = this.$$$getFont$$$("Droid Sans Mono Dotted", Font.PLAIN, 16, concreteTime.getFont());
        if (concreteTimeFont != null) concreteTime.setFont(concreteTimeFont);
        concreteTime.setText("No deadline");
        pnlList.add(concreteTime, new GridConstraints(2, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(125, -1), null, 0, false));
        showButton = new JButton();
        showButton.setBackground(new Color(-2104859));
        Font showButtonFont = this.$$$getFont$$$("JetBrains Mono", Font.BOLD, 16, showButton.getFont());
        if (showButtonFont != null) showButton.setFont(showButtonFont);
        showButton.setForeground(new Color(-15526864));
        showButton.setText("Show");
        pnlList.add(showButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        pnlList.add(spacer2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        Font font = new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
        boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
        Font fontWithFallback = isMac ? new Font(font.getFamily(), font.getStyle(), font.getSize()) : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
        return fontWithFallback instanceof FontUIResource ? fontWithFallback : new FontUIResource(fontWithFallback);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel;
    }

}
