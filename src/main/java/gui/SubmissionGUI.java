package gui;

import domain.Assignment;
import domain.Submission;
import domain.User;
import dao.impl.SubmissionDao;
import util.FrameUtil;

import javax.swing.*;

public class SubmissionGUI extends JFrame {
    private final SubmissionDao submissionDao = new SubmissionDao();
    private JPanel panel1;
    private JToolBar toolBar1;
    private JButton backButton;
    private JComboBox<String> comboBox;
    private JButton clearButton;
    private JButton submitButton;
    private JPanel panel2;
    private JLabel assignmentLabel;
    private JLabel programmeLabel;
    private JTextArea submissionContent;
    private JTextArea assignmentContent;
    private JTable submissionTable;
    private JLabel historyLabel;
    private JButton deleteButton;

    public SubmissionGUI(User user, Assignment assignment) {
        assignmentContent.setEditable(false);
        assignmentContent.setText(assignment.getAssignmentContent());

        backButton.addActionListener(e -> {
            SubmissionGUI.this.dispose();
            new SelectAssignmentGUI(user);
        });

        clearButton.addActionListener(e -> {
            submissionContent.setText("");
        });

        submitButton.addActionListener(e -> {
            Submission newSubmission = Submission.builder()
                    .submissionContent(submissionContent.getText())
                    .assignmentID(assignment.getAssignmentID())
                    .studentID(user.getUserId())
                    .build();

            if(submissionDao.insertSubmission(newSubmission)){
                FrameUtil.showConfirmation(SubmissionGUI.this, user, "Submit successfully!");
            } else {
                FrameUtil.showConfirmation(SubmissionGUI.this, user, "Something wrong!");
            }
        });

        setContentPane(panel1);
        setTitle("Submit your assignment or delete your history.");
        setSize(500, 500);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        setLocationRelativeTo(null);
    }
}
