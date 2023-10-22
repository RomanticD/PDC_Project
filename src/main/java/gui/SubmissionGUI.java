package gui;

import domain.Assignment;
import domain.User;
import dao.impl.SubmissionDao;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;

@Slf4j
public class SubmissionGUI extends JFrame {
    private final User user;
    private final SubmissionDao submissionDao = new SubmissionDao();
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
    private JTextArea submissionContent;
    private JTextArea assignmentContent;

    public SubmissionGUI(User user, Assignment assignment) {

        this.user = user;

        backButton.addActionListener(e -> {
            SubmissionGUI.this.dispose();
            new SelectAssignmentGUI(user);
        });

        clearButton.addActionListener(e -> {
            submissionContent.setText("");
            log.info("Clear Button clicked...");
        });

        submitButton.addActionListener(e -> {
            submissionDao.insertSubmission(submissionContent.getText(), assignment, user);
        });

        setContentPane(panel1);
        setTitle("Submission Page");
        setSize(500, 500);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        setLocationRelativeTo(null);
    }
}
