package gui;

import dao.AssignmentDaoInterface;
import dao.CourseDaoInterface;
import dao.impl.AssignmentDao;
import dao.impl.CourseDao;
import domain.Assignment;
import domain.User;
import util.FrameUtil;

import javax.swing.*;
import java.awt.*;
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
    private JTextArea formerContentArea;
    private JLabel cardContent;
    private JPanel cardPanel;
    private final CardLayout cardLayout = (CardLayout) cardPanel.getLayout();

    AssignmentDaoInterface assignmentDao =  new AssignmentDao();

    // Click the Select button
    public ArrangementGUI(User user, Assignment assignment){
        nameText.setEditable(false);
        formerContentArea.setEditable(false);
        courseList.setVisible(false);
        nameText.setText(assignment.getAssignmentName());

        arrangeButton.addActionListener(e -> {
            assignment.setAssignmentContent(contentText.getText());

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

        cardContent.setText("Former assignment content:");
        cardLayout.show(cardPanel, "formerContentCard");
        formerContentArea.setText(assignment.getAssignmentContent());

        setContentPane(mainPanel);
        setTitle("Arrange your assignment.");
        setSize(600, 500);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        setLocationRelativeTo(null);
    }

    // Click the New button
    public ArrangementGUI(User user){
        AssignmentDaoInterface assignmentDao = new AssignmentDao();
        CourseDaoInterface courseDao = new CourseDao();
        DefaultListModel<String> courseListModel = new DefaultListModel<>();

        List<String> CourseNames = courseDao.getCourseNames(courseDao.getCourseByUser(user));
        for (String assignmentName : CourseNames) {
            courseListModel.addElement(assignmentName);
        }
        courseList.setModel(courseListModel);

        arrangeButton.addActionListener(e -> {
            Assignment newAssignment = Assignment.builder()
                    .assignmentContent(contentText.getText())
                    .assignmentName(nameText.getText())
                    .courseID(courseDao.getCourseIDByName(courseList.getSelectedValue()))
                    .build();

            if(newAssignment.getAssignmentName() == null || newAssignment.getAssignmentName().isEmpty()){
                FrameUtil.showConfirmation(ArrangementGUI.this, user, "Assignment name can't be null");
            } else {
                if(assignmentDao.insertAssignment(newAssignment)){
                    FrameUtil.showConfirmation(ArrangementGUI.this, user, "Create successfully!");
                } else {
                    FrameUtil.showConfirmation(ArrangementGUI.this, user, "The assignment hasn't been selected or already existed");
                }
            }
        });

        backButton.addActionListener(e -> {
            ArrangementGUI.this.dispose();
            new SelectAssignmentGUI(user);
        });

        cardContent.setText("Your selected courses:");
        cardLayout.show(cardPanel, "courseListCard");

        setContentPane(mainPanel);
        setTitle("Create an assignment.");
        setSize(600, 500);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        setLocationRelativeTo(null);
    }
}
