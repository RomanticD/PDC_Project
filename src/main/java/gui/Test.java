package gui;

import javax.swing.*;

public class Test extends JFrame {
    public static void main(String[] args) {
        // Create a frame
        JFrame frame = new JFrame("Assignment List");

        // Create a DefaultListModel to store assignment names
        DefaultListModel<String> listModel = new DefaultListModel<>();

        // Assume you have a method getAssignmentNames() to retrieve assignment names
        // Replace this with your actual method call
        // Example data:
        String[] assignmentNames = getAssignmentNames();

        // Populate the list model with assignment names
        for (String assignmentName : assignmentNames) {
            listModel.addElement(assignmentName);
        }

        // Create a JList with the populated list model
        JList<String> assignmentList = new JList<>(listModel);

        // Create a JScrollPane to add the JList for scrolling
        JScrollPane scrollPane = new JScrollPane(assignmentList);

        // Add the JScrollPane to the frame
        frame.add(scrollPane);

        // Set frame properties (size, close operation, etc.)
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    // Replace this method with your actual method to retrieve assignment names from the database
    private static String[] getAssignmentNames() {
        // Simulated data for demonstration purposes
        return new String[]{"Assignment 1", "Assignment 2", "Assignment 3"};
    }
}
