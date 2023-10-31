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
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.ParseException;
import java.util.Calendar;
import java.util.List;

import java.util.Date;

import static util.FrameUtil.numericInputListener;

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
    private JSpinner hourSpinner;
    private JLabel deadlineLabel;
    private JDateChooser dateChooser;
    private JSpinner minuteSpinner;
    private JLabel timeLabel;
    private JLabel hourLabel;
    private JLabel minuteLabel;
    private JLabel dateLabel;
    private final CardLayout cardLayout = (CardLayout) cardPanel.getLayout();

    AssignmentDaoInterface assignmentDao = new AssignmentDao();

    // Click the Check button in SelectAssignmentGui
    public ManageAssignmentGUI(User user, Assignment assignment) {
        $$$setupUI$$$();
        nameText.setEditable(false);
        formerContentArea.setEditable(false);
        courseList.setVisible(false);
        nameText.setText(assignment.getAssignmentName());

        createTimeSpinner(hourSpinner, minuteSpinner);

        arrangeButton.addActionListener(e -> {
            Date selectedDate = dateChooser.getDate();
            int selectedHour = (int) hourSpinner.getValue();
            int selectedMinute = (int) minuteSpinner.getValue();

            Date selectedTime = getTime(selectedHour, selectedMinute);
            if (selectedDate == null) {
                FrameUtil.showConfirmation(ManageAssignmentGUI.this, "The date is not selected");
                new ManageAssignmentGUI(user);
                return;
            }
            Date deadline = combineDateAndTime(selectedDate, selectedTime);

            assignment.setAssignmentContent(contentText.getText());
            assignment.setDeadLine(deadline);

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

        dateChooser.setDateFormatString("yyyy-MM-dd");

        // Create a SpinnerDateModel for the time spinner
        SpinnerDateModel timeModel = new SpinnerDateModel(new Date(), null, null, Calendar.MINUTE);

        hourSpinner.setModel(timeModel);
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(hourSpinner, "HH:mm");
        hourSpinner.setEditor(timeEditor);

        arrangeButton.addActionListener(e -> {
            // Get the selected date
            Date selectedDate = dateChooser.getDate();
            int selectedHour = (int) hourSpinner.getValue();
            int selectedMinute = (int) minuteSpinner.getValue();

            Date selectedTime = getTime(selectedHour, selectedMinute);
            if (selectedDate == null) {
                FrameUtil.showConfirmation(ManageAssignmentGUI.this, "The date is not selected");
                new ManageAssignmentGUI(user);
                return;
            }
            Date deadline = combineDateAndTime(selectedDate, selectedTime);

            Assignment newAssignment = Assignment.builder()
                    .assignmentContent(contentText.getText())
                    .assignmentName(nameText.getText())
                    .courseID(courseDao.getCourseIDByName(courseList.getSelectedValue()))
                    .deadLine(deadline)
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

    public static Date getTime(int selectedHour, int selectedMinute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
        calendar.set(Calendar.MINUTE, selectedMinute);
        calendar.set(Calendar.SECOND, 0); // Optional, set to 0 if you want to clear seconds
        calendar.set(Calendar.MILLISECOND, 0); // Optional, set to 0 if you want to clear milliseconds

        return calendar.getTime();
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
        dateChooser.setMinSelectableDate(new Date());
        dateChooser.setDateFormatString("yyyy-MM-dd");
    }

    private void createTimeSpinner(JSpinner hourSpinner, JSpinner minuteSpinner) {
        // Create a spinner for hours (00-23)
        SpinnerModel hourModel = new SpinnerNumberModel(0, 0, 23, 1);
        hourSpinner.setModel(hourModel);
        JFormattedTextField hourTextField = ((JSpinner.DefaultEditor) hourSpinner.getEditor()).getTextField();

        // Create a spinner for minutes (00-59)
        SpinnerModel minuteModel = new SpinnerNumberModel(0, 0, 59, 1);
        minuteSpinner.setModel(minuteModel);
        JFormattedTextField minuteTextField = ((JSpinner.DefaultEditor) minuteSpinner.getEditor()).getTextField();

        // Restrict input to numeric values only for hours and minutes
        numericInputListener(hourTextField);
        numericInputListener(minuteTextField);
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
        panel1.setLayout(new GridLayoutManager(7, 5, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(panel1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(225, -1), null, null, 0, false));
        assignmentName = new JLabel();
        assignmentName.setText("Assignment name:");
        panel1.add(assignmentName, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(225, 17), null, 0, false));
        assignmentContent = new JLabel();
        assignmentContent.setText("Write the assignment content:");
        panel1.add(assignmentContent, new GridConstraints(0, 1, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(250, 17), null, 0, false));
        cardPanel = new JPanel();
        cardPanel.setLayout(new CardLayout(0, 0));
        panel1.add(cardPanel, new GridConstraints(3, 0, 4, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(225, 250), new Dimension(-1, 100), new Dimension(-1, 250), 0, false));
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
        hourSpinner = new JSpinner();
        panel1.add(hourSpinner, new GridConstraints(6, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(80, 30), null, 0, false));
        contentText = new JTextArea();
        contentText.setText("");
        panel1.add(contentText, new GridConstraints(1, 1, 3, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(250, 150), null, 0, false));
        panel1.add(dateChooser, new GridConstraints(6, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(150, 30), null, 0, false));
        minuteSpinner = new JSpinner();
        panel1.add(minuteSpinner, new GridConstraints(6, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(80, 30), null, 0, false));
        timeLabel = new JLabel();
        timeLabel.setText(":");
        panel1.add(timeLabel, new GridConstraints(6, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, new Dimension(5, -1), 0, false));
        hourLabel = new JLabel();
        hourLabel.setText("hour(0-23h):");
        panel1.add(hourLabel, new GridConstraints(5, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        minuteLabel = new JLabel();
        minuteLabel.setText("minute(0-59m):");
        panel1.add(minuteLabel, new GridConstraints(5, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        deadlineLabel = new JLabel();
        deadlineLabel.setText("Choose your deadline:");
        panel1.add(deadlineLabel, new GridConstraints(4, 1, 1, 4, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        dateLabel = new JLabel();
        dateLabel.setText("date:");
        panel1.add(dateLabel, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }
}
