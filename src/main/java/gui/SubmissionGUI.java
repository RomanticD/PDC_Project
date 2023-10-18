package gui;

import domain.Assignment;
import domain.User;
import dao.impl.SubmissionDao;

import javax.swing.*;

public class SubmissionGUI extends JFrame {
    private final User user;
    private JPanel panel1;
    private JToolBar toolBar1;
    private JButton backButton;
    private JComboBox comboBox1;
    private JComboBox comboBox2;
    private JButton clearButton;
    private JButton submitButton;
    private JPanel panel2;
    private JLabel assignmentLabel;
    private JLabel programmeLabel;
    private JTextArea textArea1;
    private JTextArea textArea2;

    public SubmissionGUI(User user) {
        this.user = user;
        setContentPane(panel1);
        setTitle("welcome");
        setSize(500, 500);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        setLocationRelativeTo(null);

        SubmissionDao submissionDao = new SubmissionDao();
        Assignment assignment = new Assignment();
        assignment.setAssignmentID(1);

        backButton.addActionListener(e -> {
            new MainGUI(user);
            SubmissionGUI.this.dispose();
        });

        submitButton.addActionListener(e -> {
            submissionDao.insertSubmission(textArea1.getText(), assignment, user);
        });
    }
}
