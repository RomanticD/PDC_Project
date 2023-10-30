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
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.MaskFormatter;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import java.util.Date;

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
    private JSpinner deadlineSpinner;
    private JLabel deadlineLabel;
    private JDateChooser dateChooser;
    private final CardLayout cardLayout = (CardLayout) cardPanel.getLayout();

    AssignmentDaoInterface assignmentDao =  new AssignmentDao();

    // Click the Check button in SelectAssignmentGui
    public ManageAssignmentGUI(User user, Assignment assignment){
        nameText.setEditable(false);
        formerContentArea.setEditable(false);
        courseList.setVisible(false);
        nameText.setText(assignment.getAssignmentName());

        arrangeButton.addActionListener(e -> {
            assignment.setAssignmentContent(contentText.getText());

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
    }
}
