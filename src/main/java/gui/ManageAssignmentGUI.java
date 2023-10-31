package gui;

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

public class ManageAssignmentGUI extends JFrame{
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

    AssignmentDaoInterface assignmentDao =  new AssignmentDao();

    // Click the Check button in SelectAssignmentGui
    public ManageAssignmentGUI(User user, Assignment assignment){
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

            if(assignmentDao.updateAssignment(assignment)){
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
        if(assignment.getAssignmentContent() == null || assignment.getAssignmentContent().isEmpty()){
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
    public ManageAssignmentGUI(User user){
        AssignmentDaoInterface assignmentDao = new AssignmentDao();
        CourseDaoInterface courseDao = new CourseDao();
        DefaultListModel<String> courseListModel = new DefaultListModel<>();

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

            if(newAssignment.getAssignmentName() == null || newAssignment.getAssignmentName().isEmpty()){
                FrameUtil.showConfirmation(ManageAssignmentGUI.this, "Assignment name can't be null");
                new ManageAssignmentGUI(user);
            } else {
                if(assignmentDao.insertAssignment(newAssignment)){
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

    private void createUIComponents(){
        this.dateChooser = new JDateChooser();
        dateChooser.setMinSelectableDate(new Date());
        dateChooser.setDateFormatString("yyyy-MM-dd");
    }

    private void createTimeSpinner(JSpinner hourSpinner, JSpinner minuteSpinner){
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
}
