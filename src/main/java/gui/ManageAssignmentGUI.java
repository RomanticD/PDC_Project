package gui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.toedter.calendar.JDateChooser;
import domain.Course;
import service.AssignmentService;
import service.CourseService;
import service.dao.AssignmentDao;
import service.dao.CourseDao;
import domain.Assignment;
import domain.User;
import util.FrameUtil;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.util.Calendar;
import java.util.List;

import java.util.Date;
import java.util.Locale;

import static util.FrameUtil.getRoundedBorder;
import static util.FrameUtil.numericInputListener;

public class ManageAssignmentGUI extends JFrame {
    private JTextArea nameText;
    private JTextArea contentText;
    private JButton backButton;
    private JButton createButton;
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
    private JPanel contentPanel;
    private JButton alterTimeButton;
    private JButton alterContentButton;
    private JPanel deadlinePanel;
    private JButton showButton;


    AssignmentService assignmentService = new AssignmentDao();

    // Click the Check button in SelectAssignmentGui
    public ManageAssignmentGUI(User user, Assignment assignment) {
        $$$setupUI$$$();
        CardLayout cardLayout = (CardLayout) cardPanel.getLayout();
        contentPanel.setBorder(getRoundedBorder());
        createButton.setVisible(false);

        nameText.setEditable(false);
        formerContentArea.setEditable(false);
        courseList.setVisible(false);
        nameText.setText(assignment.getAssignmentName());

        dateChooser.setDateFormatString("yyyy-MM-dd");
        Font customFont = new Font("Droid Sans Mono", Font.BOLD, 16);
        dateChooser.setFont(customFont);
        dateChooser.setForeground(Color.WHITE);
        createTimeSpinner(hourSpinner, minuteSpinner);

        alterTimeButton.addActionListener(e -> {
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

            assignment.setDeadLine(deadline);

            if (assignmentService.updateAssignment(assignment)) {
                FrameUtil.showConfirmation(ManageAssignmentGUI.this, "Alter successfully!");
            } else {
                FrameUtil.showConfirmation(ManageAssignmentGUI.this, "Something wrong!");
            }
            new ManageAssignmentGUI(user, assignment);
        });

        alterContentButton.addActionListener(e -> {
            assignment.setAssignmentContent(contentText.getText());

            if (assignmentService.updateAssignment(assignment)) {
                FrameUtil.showConfirmation(ManageAssignmentGUI.this, "Alter successfully!");
            } else {
                FrameUtil.showConfirmation(ManageAssignmentGUI.this, "Something wrong!");
            }
            new ManageAssignmentGUI(user, assignment);
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
        setSize(755, 525);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        setLocationRelativeTo(null);
    }

    // Click the New button in SelectAssignmentGui
    public ManageAssignmentGUI(User user) {
        $$$setupUI$$$();

        alterContentButton.setVisible(false);
        alterTimeButton.setVisible(false);
        contentPanel.setBorder(getRoundedBorder());
        Color customColor = new Color(200, 200, 200);
        nameText.setBackground(customColor);

        AssignmentService assignmentService = new AssignmentDao();
        CourseService courseService = new CourseDao();
        DefaultListModel<String> courseListModel = new DefaultListModel<>();

        CardLayout cardLayout = (CardLayout) cardPanel.getLayout();
        List<String> courseNames = courseService.getCourseNames(courseService.getCourseByUser(user));
        for (String assignmentName : courseNames) {
            courseListModel.addElement(assignmentName);
        }
        courseList.setModel(courseListModel);

        dateChooser.setDateFormatString("yyyy-MM-dd");
        Font customFont = new Font("Droid Sans Mono", Font.BOLD, 16);
        dateChooser.setFont(customFont);
        dateChooser.setForeground(Color.WHITE);
        createTimeSpinner(hourSpinner, minuteSpinner);

        createButton.addActionListener(e -> {
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
                    .courseID(courseService.getCourseIDByName(courseList.getSelectedValue()))
                    .deadLine(deadline)
                    .build();

            if (newAssignment.getAssignmentName() == null || newAssignment.getAssignmentName().isEmpty()) {
                FrameUtil.showConfirmation(ManageAssignmentGUI.this, "Assignment name can't be null");
                new ManageAssignmentGUI(user);
            } else {
                if (assignmentService.insertAssignment(newAssignment)) {
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

        showButton.setText("Show All");
        final boolean[] showAll = {true};
        showButton.addActionListener(e -> {
            courseListModel.clear();
            if (showAll[0]) {
                for (Course course : courseService.getAllCourses()) {
                    courseListModel.addElement(course.getCourseName());
                }
                showButton.setText("Show Yours");
                cardContent.setText("All courses:");
            } else {
                for (String course : courseNames) {
                    courseListModel.addElement(course);
                }
                showButton.setText("Show All");
                cardContent.setText("Your selected courses:");
            }
            showAll[0] = !showAll[0];
        });

        setContentPane(mainPanel);
        setTitle("Create an assignment");
        setSize(675, 500);
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
        backButton.setBackground(new Color(-2104859));
        Font backButtonFont = this.$$$getFont$$$("JetBrains Mono", Font.BOLD, 16, backButton.getFont());
        if (backButtonFont != null) backButton.setFont(backButtonFont);
        backButton.setForeground(new Color(-15526864));
        backButton.setText("Back");
        toolBar1.add(backButton);
        final Spacer spacer1 = new Spacer();
        toolBar1.add(spacer1);
        createButton = new JButton();
        createButton.setBackground(new Color(-2104859));
        createButton.setEnabled(true);
        Font createButtonFont = this.$$$getFont$$$("JetBrains Mono", Font.BOLD, 16, createButton.getFont());
        if (createButtonFont != null) createButton.setFont(createButtonFont);
        createButton.setForeground(new Color(-15526864));
        createButton.setText("Create");
        toolBar1.add(createButton);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(5, 6, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(panel1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(225, -1), null, null, 0, false));
        assignmentName = new JLabel();
        Font assignmentNameFont = this.$$$getFont$$$("Droid Sans Mono", Font.PLAIN, 18, assignmentName.getFont());
        if (assignmentNameFont != null) assignmentName.setFont(assignmentNameFont);
        assignmentName.setText("Assignment name:");
        panel1.add(assignmentName, new GridConstraints(0, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(225, 17), null, 0, false));
        cardPanel = new JPanel();
        cardPanel.setLayout(new CardLayout(0, 0));
        cardPanel.setBackground(new Color(-16777216));
        cardPanel.setForeground(new Color(-16777216));
        panel1.add(cardPanel, new GridConstraints(3, 0, 2, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(250, 250), new Dimension(-1, 100), new Dimension(-1, 250), 0, false));
        formerContentArea = new JTextArea();
        formerContentArea.setBackground(new Color(-6837066));
        Font formerContentAreaFont = this.$$$getFont$$$("Monaco", Font.PLAIN, 16, formerContentArea.getFont());
        if (formerContentAreaFont != null) formerContentArea.setFont(formerContentAreaFont);
        formerContentArea.setForeground(new Color(-15526864));
        cardPanel.add(formerContentArea, "formerContentCard");
        nullLabel = new JLabel();
        nullLabel.setBackground(new Color(-16777216));
        Font nullLabelFont = this.$$$getFont$$$("Droid Sans Mono Slashed", Font.BOLD | Font.ITALIC, 20, nullLabel.getFont());
        if (nullLabelFont != null) nullLabel.setFont(nullLabelFont);
        nullLabel.setHorizontalAlignment(0);
        nullLabel.setHorizontalTextPosition(0);
        nullLabel.setText("No content");
        cardPanel.add(nullLabel, "nullLabelCard");
        courseListPane = new JScrollPane();
        courseListPane.setBackground(new Color(-6837066));
        Font courseListPaneFont = this.$$$getFont$$$("Droid Sans Mono", Font.PLAIN, 15, courseListPane.getFont());
        if (courseListPaneFont != null) courseListPane.setFont(courseListPaneFont);
        courseListPane.setForeground(new Color(-1727412));
        cardPanel.add(courseListPane, "courseListCard");
        courseList = new JList();
        courseList.setBackground(new Color(-6837066));
        Font courseListFont = this.$$$getFont$$$("Droid Sans Mono", Font.PLAIN, 15, courseList.getFont());
        if (courseListFont != null) courseList.setFont(courseListFont);
        courseList.setForeground(new Color(-16777216));
        courseList.putClientProperty("List.isFileList", Boolean.FALSE);
        courseListPane.setViewportView(courseList);
        nameText = new JTextArea();
        nameText.setBackground(new Color(-6837066));
        Font nameTextFont = this.$$$getFont$$$("Monaco", Font.PLAIN, 16, nameText.getFont());
        if (nameTextFont != null) nameText.setFont(nameTextFont);
        nameText.setForeground(new Color(-15526864));
        panel1.add(nameText, new GridConstraints(1, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(-1, 50), new Dimension(225, 137), null, 0, false));
        cardContent = new JLabel();
        Font cardContentFont = this.$$$getFont$$$("Droid Sans Mono", Font.PLAIN, 18, cardContent.getFont());
        if (cardContentFont != null) cardContent.setFont(cardContentFont);
        cardContent.setText("Card content");
        panel1.add(cardContent, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        contentPanel = new JPanel();
        contentPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPanel.setBackground(new Color(-15526864));
        panel1.add(contentPanel, new GridConstraints(1, 3, 3, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        contentText = new JTextArea();
        contentText.setBackground(new Color(-3618616));
        Font contentTextFont = this.$$$getFont$$$("Monaco", Font.PLAIN, 16, contentText.getFont());
        if (contentTextFont != null) contentText.setFont(contentTextFont);
        contentText.setForeground(new Color(-15526864));
        contentText.setText("");
        contentPanel.add(contentText, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(250, 150), null, 0, false));
        assignmentContent = new JLabel();
        Font assignmentContentFont = this.$$$getFont$$$("Droid Sans Mono", Font.PLAIN, 18, assignmentContent.getFont());
        if (assignmentContentFont != null) assignmentContent.setFont(assignmentContentFont);
        assignmentContent.setText("Write the assignment content:");
        panel1.add(assignmentContent, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(173, 17), null, 0, false));
        alterContentButton = new JButton();
        alterContentButton.setBackground(new Color(-2104859));
        Font alterContentButtonFont = this.$$$getFont$$$("JetBrains Mono", Font.BOLD, 16, alterContentButton.getFont());
        if (alterContentButtonFont != null) alterContentButton.setFont(alterContentButtonFont);
        alterContentButton.setForeground(new Color(-15526864));
        alterContentButton.setText("Alter");
        panel1.add(alterContentButton, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel1.add(spacer2, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        deadlinePanel = new JPanel();
        deadlinePanel.setLayout(new GridLayoutManager(3, 4, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(deadlinePanel, new GridConstraints(4, 3, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        deadlineLabel = new JLabel();
        Font deadlineLabelFont = this.$$$getFont$$$("Droid Sans Mono", Font.PLAIN, 18, deadlineLabel.getFont());
        if (deadlineLabelFont != null) deadlineLabel.setFont(deadlineLabelFont);
        deadlineLabel.setText("Choose your deadline:");
        deadlinePanel.add(deadlineLabel, new GridConstraints(0, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        dateLabel = new JLabel();
        Font dateLabelFont = this.$$$getFont$$$("Droid Sans Mono", Font.PLAIN, 14, dateLabel.getFont());
        if (dateLabelFont != null) dateLabel.setFont(dateLabelFont);
        dateLabel.setText("date:");
        deadlinePanel.add(dateLabel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(173, 17), null, 0, false));
        deadlinePanel.add(dateChooser, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(100, -1), new Dimension(173, 30), null, 0, false));
        hourLabel = new JLabel();
        Font hourLabelFont = this.$$$getFont$$$("Droid Sans Mono", Font.PLAIN, 14, hourLabel.getFont());
        if (hourLabelFont != null) hourLabel.setFont(hourLabelFont);
        hourLabel.setText("hour(0-23h):");
        deadlinePanel.add(hourLabel, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        alterTimeButton = new JButton();
        alterTimeButton.setBackground(new Color(-2104859));
        Font alterTimeButtonFont = this.$$$getFont$$$("JetBrains Mono", Font.BOLD, 16, alterTimeButton.getFont());
        if (alterTimeButtonFont != null) alterTimeButton.setFont(alterTimeButtonFont);
        alterTimeButton.setForeground(new Color(-15526864));
        alterTimeButton.setText("Alter");
        deadlinePanel.add(alterTimeButton, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(112, 30), null, 0, false));
        minuteLabel = new JLabel();
        Font minuteLabelFont = this.$$$getFont$$$("Droid Sans Mono", Font.PLAIN, 14, minuteLabel.getFont());
        if (minuteLabelFont != null) minuteLabel.setFont(minuteLabelFont);
        minuteLabel.setText("minute(0-59m):");
        deadlinePanel.add(minuteLabel, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(86, 17), null, 0, false));
        hourSpinner = new JSpinner();
        Font hourSpinnerFont = this.$$$getFont$$$("Droid Sans Mono", Font.BOLD, 16, hourSpinner.getFont());
        if (hourSpinnerFont != null) hourSpinner.setFont(hourSpinnerFont);
        deadlinePanel.add(hourSpinner, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(75, 30), null, 0, false));
        minuteSpinner = new JSpinner();
        Font minuteSpinnerFont = this.$$$getFont$$$("Droid Sans Mono", Font.BOLD, 16, minuteSpinner.getFont());
        if (minuteSpinnerFont != null) minuteSpinner.setFont(minuteSpinnerFont);
        deadlinePanel.add(minuteSpinner, new GridConstraints(2, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(75, 30), null, 0, false));
        timeLabel = new JLabel();
        Font timeLabelFont = this.$$$getFont$$$("Droid Sans Mono", Font.BOLD, 16, timeLabel.getFont());
        if (timeLabelFont != null) timeLabel.setFont(timeLabelFont);
        timeLabel.setText(":");
        deadlinePanel.add(timeLabel, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, new Dimension(5, -1), 0, false));
        showButton = new JButton();
        showButton.setBackground(new Color(-2104859));
        Font showButtonFont = this.$$$getFont$$$("JetBrains Mono", Font.BOLD, 16, showButton.getFont());
        if (showButtonFont != null) showButton.setFont(showButtonFont);
        showButton.setForeground(new Color(-15526864));
        showButton.setText("Show");
        panel1.add(showButton, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        panel1.add(spacer3, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
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
        return mainPanel;
    }

}
