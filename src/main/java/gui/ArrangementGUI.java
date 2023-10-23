package gui;

import dao.AssignmentDaoInterface;
import dao.CourseDaoInterface;
import dao.impl.AssignmentDao;
import dao.impl.CourseDao;
import domain.Assignment;
import domain.User;
import util.FrameUtil;

import javax.swing.*;
import java.util.List;

public class ArrangementGUI extends JFrame{
    private JTextArea nameText;
    private JTextArea contentText;
    private JButton backButton;
    private JButton arrangeButton;
    private JLabel assignmentName;
    private JLabel assignmentContent;
    private JPanel mainPanel;
    private JList<String> courseList;

    AssignmentDaoInterface assignmentDao =  new AssignmentDao();

    // Click the select button
    public ArrangementGUI(User user, Assignment assignment){
        nameText.setEditable(false);
        courseList.setVisible(false);
        nameText.setText(assignment.getAssignmentName());

        arrangeButton.addActionListener(e -> {
            if(assignmentDao.updateAssignment(assignment)){
                FrameUtil.showConfirmation(ArrangementGUI.this, user, "Arrange successfully!");
            } else {
                FrameUtil.showConfirmation(ArrangementGUI.this, user, "Something wrong!");
            }
        });

        backButton.addActionListener(e -> {
            ArrangementGUI.this.dispose();
            new SelectAssignmentGUI(user);
        });

        setContentPane(mainPanel);
        setTitle("Arrange your assignment");
        setSize(600, 500);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        setLocationRelativeTo(null);
    }

    // Click the new button
    public ArrangementGUI(User user){
        AssignmentDaoInterface assignmentDao = new AssignmentDao();
        CourseDaoInterface courseDao = new CourseDao();
        DefaultListModel<String> courseListModel = new DefaultListModel<>();

        List<String> CourseNames = courseDao.getCourseNames(courseDao.getCourseByUser(user));

        for (String assignmentName : CourseNames) {
            courseListModel.addElement(assignmentName);
        }

        // Set the model for the lists
        courseList.setModel(courseListModel);

        arrangeButton.addActionListener(e -> {
            Assignment newAssignment = Assignment.builder()
                    .assignmentContent(contentText.getText())
                    .assignmentName(nameText.getText())
                    .courseID(courseDao.getCourseIDByName(courseList.getSelectedValue()))
                    .build();
            if(assignmentDao.insertAssignment(newAssignment)){
                FrameUtil.showConfirmation(ArrangementGUI.this, user, "Create successfully!");
            }else {
                FrameUtil.showConfirmation(ArrangementGUI.this, user, "Something wrong or the assignment has already existed");
            }
        });

        backButton.addActionListener(e -> {
            ArrangementGUI.this.dispose();
            new SelectAssignmentGUI(user);
        });

        setContentPane(mainPanel);
        setTitle("Create an assignment");
        setSize(600, 500);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        setLocationRelativeTo(null);
    }
}
