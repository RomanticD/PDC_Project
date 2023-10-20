package gui;

import domain.Assignment;

import javax.swing.*;

public class AssignmentGUI extends JFrame{
    private JPanel panel;
    private JButton backButton;
    private JButton arrangeButton;
    private JList<String> coursesList;
    private JList<String> assignmentsList;
    private JScrollPane coursesPane;
    private JScrollPane assignmentsPane;
    private JPanel pnlList;

    public AssignmentGUI(Assignment assignment){
        DefaultListModel<String> listModel = new DefaultListModel<>();
        String[] assignmentNames = getAssignmentNames();
        // Populate the list model with assignment names
        for (String assignmentName : assignmentNames) {
            listModel.addElement(assignmentName);
        }
        coursesList.setModel(listModel);

        setContentPane(panel);
        setTitle("Welcome");
        setSize(500, 500);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        setLocationRelativeTo(null);
    }

    // Replace this method with your actual method to retrieve assignment names from the database
    private static String[] getAssignmentNames() {
        // Simulated data for demonstration purposes
        return new String[]{"Assignment 1", "Assignment 2", "Assignment 3"};
    }
}
