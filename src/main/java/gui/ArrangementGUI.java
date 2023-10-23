package gui;

import dao.AssignmentDaoInterface;
import dao.CourseDaoInterface;
import dao.impl.AssignmentDao;
import dao.impl.CourseDao;
import domain.Assignment;
import domain.User;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
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
        arrangeButton.setVisible(true);
        nameText.setText(assignment.getAssignmentName());

        arrangeButton.addActionListener(e -> {
            assignmentDao.updateAssignment(assignment.getAssignmentName(), assignment.getCourseID(),contentText.getText());
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
        arrangeButton.setVisible(true);

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
            assignmentDao.insertAssignment(nameText.getText(), courseDao.getCourseIDByName(courseList.getSelectedValue()), contentText.getText());
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
