package gui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.toedter.calendar.JDateChooser;
import dao.AssignmentDaoInterface;
import dao.CourseDaoInterface;
import dao.impl.AssignmentDao;
import dao.impl.CourseDao;
import domain.Assignment;
import domain.User;
import util.FrameUtil;

import javax.swing.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.MaskFormatter;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import java.util.Date;

public class ManageAssignmentGUI extends JFrame {
    private JTextArea nameText;
    private JTextArea contentText;
    private JButton backButton;
    private JButton arrangeButton;
    private JLabel assignmentName;
    private JLabel assignmentContent;
    private JPanel mainPanel;
    private JList<String> courseList;
    private JTextArea formerContentArea;
    private JLabel cardContent;
    private JPanel cardPanel;
    private JLabel nullLabel;
    private JScrollPane courseListPane;
    private JSpinner deadlineSpinner;
    private JLabel deadlineLabel;
    private JDateChooser dateChooser;
    private final CardLayout cardLayout = (CardLayout) cardPanel.getLayout();

    AssignmentDaoInterface assignmentDao = new AssignmentDao();

    // Click the Check button in SelectAssignmentGui
    public ManageAssignmentGUI(User user, Assignment assignment) {
        $$$setupUI$$$();
        nameText.setEditable(false);
        formerContentArea.setEditable(false);
        courseList.setVisible(false);
        nameText.setText(assignment.getAssignmentName());

        arrangeButton.addActionListener(e -> {
            assignment.setAssignmentContent(contentText.getText());

            if (assignmentDao.updateAssignment(assignment)) {
                FrameUtil.showConfirmation(ManageAssignmentGUI.this, "Arrange successfully!");
                new SelectAssignmentGUI(user);
            } else {
                FrameUtil.showConfirmation(ManageAssignmentGUI.this, "Something wrong!");
                new ManageAssignmentGUI(user, assignment);
            }
        });

        backButton.addActionListener(e -> {
            ManageAssignmentGUI.this.dispose();
            new SelectAssignmentGUI(user);
        });

        cardContent.setText("Former assignment content:");
        cardLayout.show(cardPanel, "formerContentCard");
        if (assignment.getAssignmentContent() == null || assignment.getAssignmentContent().isEmpty()) {
            cardLayout.show(cardPanel, "nullLabelCard");
        } else {
            formerContentArea.setText(assignment.getAssignmentContent());
        }

        setContentPane(mainPanel);
        setTitle("Alter your assignment content");
        setSize(600, 500);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        setLocationRelativeTo(null);
    }

    // Click the New button in SelectAssignmentGui
    public ManageAssignmentGUI(User user) {
        AssignmentDaoInterface assignmentDao = new AssignmentDao();
        CourseDaoInterface courseDao = new CourseDao();
        DefaultListModel<String> courseListModel = new DefaultListModel<>();

        $$$setupUI$$$();
        List<String> CourseNames = courseDao.getCourseNames(courseDao.getCourseByUser(user));
        for (String assignmentName : CourseNames) {
            courseListModel.addElement(assignmentName);
        }
        courseList.setModel(courseListModel);

        dateChooser.setDateFormatString("yy-MM-dd");
        // Create a SpinnerDateModel for the time spinner
        SpinnerDateModel timeModel = new SpinnerDateModel(new Date(), null, null, Calendar.MINUTE);

        deadlineSpinner.setModel(timeModel);
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(deadlineSpinner, "HH:mm");
        deadlineSpinner.setEditor(timeEditor);

        arrangeButton.addActionListener(e -> {
            // Get the selected date
            Date selectedDate = dateChooser.getDate();
            // Get the selected time
            Date selectedTime = (Date) deadlineSpinner.getValue();
            // Combine the date and time to create the deadline
            Date deadline = combineDateAndTime(selectedDate, selectedTime);

            Assignment newAssignment = Assignment.builder()
                    .assignmentContent(contentText.getText())
                    .assignmentName(nameText.getText())
                    .courseID(courseDao.getCourseIDByName(courseList.getSelectedValue()))
                    .build();

            if (newAssignment.getAssignmentName() == null || newAssignment.getAssignmentName().isEmpty()) {
                FrameUtil.showConfirmation(ManageAssignmentGUI.this, "Assignment name can't be null");
                new ManageAssignmentGUI(user);
            } else {
                if (assignmentDao.insertAssignment(newAssignment)) {
                    FrameUtil.showConfirmation(ManageAssignmentGUI.this, "Create successfully!");
                    new SelectAssignmentGUI(user);
                } else {
                    FrameUtil.showConfirmation(ManageAssignmentGUI.this, "The assignment hasn't been selected or already existed");
                    new ManageAssignmentGUI(user);
                }
            }
        });

        backButton.addActionListener(e -> {
            ManageAssignmentGUI.this.dispose();
            new SelectAssignmentGUI(user);
        });

        cardContent.setText("Your selected courses:");
        cardLayout.show(cardPanel, "courseListCard");

        setContentPane(mainPanel);
        setTitle("Create an assignment");
        setSize(600, 500);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        setLocationRelativeTo(null);
    }

    private Date combineDateAndTime(Date date, Date time) {
        Calendar calendarDate = Calendar.getInstance();
        calendarDate.setTime(date);

        Calendar calendarTime = Calendar.getInstance();
        calendarTime.setTime(time);

        calendarDate.set(Calendar.HOUR_OF_DAY, calendarTime.get(Calendar.HOUR_OF_DAY));
        calendarDate.set(Calendar.MINUTE, calendarTime.get(Calendar.MINUTE));

        return calendarDate.getTime();
    }

    private void createUIComponents() {
        this.dateChooser = new JDateChooser();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        final JToolBar toolBar1 = new JToolBar();
        mainPanel.add(toolBar1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 20), null, 0, false));
        backButton = new JButton();
        backButton.setText("Back");
        toolBar1.add(backButton);
        final Spacer spacer1 = new Spacer();
        toolBar1.add(spacer1);
        arrangeButton = new JButton();
        arrangeButton.setEnabled(true);
        arrangeButton.setText("Arrange");
        toolBar1.add(arrangeButton);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(6, 3, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(panel1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(225, -1), null, null, 0, false));
        assignmentName = new JLabel();
        assignmentName.setText("Assignment name:");
        panel1.add(assignmentName, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(225, 17), null, 0, false));
        assignmentContent = new JLabel();
        assignmentContent.setText("Write the assignment content:");
        panel1.add(assignmentContent, new GridConstraints(0, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(250, 17), null, 0, false));
        cardPanel = new JPanel();
        cardPanel.setLayout(new CardLayout(0, 0));
        panel1.add(cardPanel, new GridConstraints(3, 0, 3, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(225, 250), new Dimension(-1, 100), new Dimension(-1, 250), 0, false));
        formerContentArea = new JTextArea();
        cardPanel.add(formerContentArea, "formerContentCard");
        nullLabel = new JLabel();
        nullLabel.setHorizontalAlignment(0);
        nullLabel.setHorizontalTextPosition(0);
        nullLabel.setText("No content");
        cardPanel.add(nullLabel, "nullLabelCard");
        courseListPane = new JScrollPane();
        cardPanel.add(courseListPane, "courseListCard");
        courseList = new JList();
        courseList.putClientProperty("List.isFileList", Boolean.FALSE);
        courseListPane.setViewportView(courseList);
        final JToolBar toolBar2 = new JToolBar();
        cardPanel.add(toolBar2, "Card1");
        nameText = new JTextArea();
        panel1.add(nameText, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(-1, 50), new Dimension(225, 137), new Dimension(-1, 150), 0, false));
        cardContent = new JLabel();
        cardContent.setText("Card content");
        panel1.add(cardContent, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        deadlineSpinner = new JSpinner();
        panel1.add(deadlineSpinner, new GridConstraints(5, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(50, -1), null, 0, false));
        contentText = new JTextArea();
        contentText.setText("");
        panel1.add(contentText, new GridConstraints(1, 1, 3, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(250, 150), null, 0, false));
        deadlineLabel = new JLabel();
        deadlineLabel.setText("Choose your deadline:");
        panel1.add(deadlineLabel, new GridConstraints(4, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel1.add(dateChooser, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(125, -1), null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

}
