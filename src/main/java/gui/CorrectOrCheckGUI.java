package gui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import dao.UserDaoInterface;
import dao.impl.SubmissionDao;
import dao.impl.UserDao;
import domain.Assignment;
import domain.Submission;
import domain.User;
import util.FrameUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.List;

public class CorrectOrCheckGUI extends JFrame {
    private JButton backButton;
    private JButton operationButton;
    private JLabel concreteScores;
    private JLabel scoresLabel;
    private JLabel submissionContentLabel;
    private JLabel cardLabel;
    private JTextArea assignmentContent;
    private JTable submissionTable;
    private JTextArea evaluationContent;
    private JLabel nullLabel;
    private JTextArea submissionContent;
    private JLabel submissionLabel;
    private JPanel evaluationPanel;
    private JPanel cardPanel;
    private JScrollPane submissionPanel;
    private JPanel mainPanel;
    private JSpinner scoresSpinner;
    private JPanel scoresPanel;
    private JButton clearButton;

    SubmissionDao submissionDao = new SubmissionDao();

    CardLayout cardLayout = (CardLayout) cardPanel.getLayout();

    CardLayout evaluationLayout = (CardLayout) evaluationPanel.getLayout();

    CardLayout scoresLayout = (CardLayout) scoresPanel.getLayout();

    // Click the Correct button in ManageAssignmentGUI
    public CorrectOrCheckGUI(User user, Assignment assignment) {
        setTitle("Correct an assignment");
        cardLabel.setText("Submissions:");
        cardLayout.show(cardPanel, "submissionCard");
        UserDaoInterface userDao = new UserDao();

        DefaultTableModel tableModel = new DefaultTableModel();
        submissionTable.setModel(tableModel);
        tableModel.addColumn("StudentID");
        tableModel.addColumn("Name");
        tableModel.addColumn("Time");
        tableModel.addColumn("Order");
        tableModel.addColumn("Status");

        List<Submission> submissionList = submissionDao.getSubmissionsFromAssignment(assignment);
        for (Submission submission : submissionList) {
            Object[] rowData = {
                    submission.getStudentID(),
                    userDao.getUserById(submission.getStudentID()).getUsername(),
                    submission.getSubmissionTime(),
                    submission.getSubmissionOrder(),
                    submission.getSubmissionStatus()
            };
            tableModel.addRow(rowData);
        }
        TableColumn timeColumn = submissionTable.getColumnModel().getColumn(2);
        timeColumn.setCellRenderer(new SubmissionGUI.TimestampRenderer());

        submissionContent.setEditable(false);


        submissionTable.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = submissionTable.getSelectedRow();

            if (selectedRow != -1) {
                int studentID = (int) tableModel.getValueAt(selectedRow, 0);
                int submissionOrder = (int) tableModel.getValueAt(selectedRow, 3);
                Submission submission = submissionDao.getSubmissionFromTwoIDsAndOrder(assignment.getAssignmentID(), studentID, submissionOrder);
                submissionContent.setText(submission.getSubmissionContent());
            }
        });

        operationButton.setText("Correct");
        operationButton.addActionListener(e -> {
            int selectedRow = submissionTable.getSelectedRow();

            if (selectedRow >= 0) {
                int studentID = (int) tableModel.getValueAt(selectedRow, 0);
                int submissionOrder = (int) tableModel.getValueAt(selectedRow, 3);
                Submission submission = submissionDao.getSubmissionFromTwoIDsAndOrder(assignment.getAssignmentID(), studentID, submissionOrder);
                submission.setEvaluation(evaluationContent.getText());
                submission.setScores((int) scoresSpinner.getValue());

                if (submissionDao.correctSubmission(submission)) {
                    FrameUtil.showConfirmation(CorrectOrCheckGUI.this, "Correct successfully");
                } else {
                    FrameUtil.showConfirmation(CorrectOrCheckGUI.this, "Something wrong");
                }
            } else {
                FrameUtil.showConfirmation(CorrectOrCheckGUI.this, "You haven't select any submission!");
            }
            new CorrectOrCheckGUI(user, assignment);
        });

        backButton.addActionListener(e -> {
            CorrectOrCheckGUI.this.dispose();
            new SelectAssignmentGUI(user);
        });

        clearButton.addActionListener(e -> {
            evaluationContent.setText("");
        });

        scoresLayout.show(scoresPanel, "selectScoresCard");
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(0, 0, 100, 1);
        scoresSpinner.setModel(spinnerModel);
        JFormattedTextField scoresField = ((JSpinner.DefaultEditor) scoresSpinner.getEditor()).getTextField();
        FrameUtil.numericInputListener(scoresField);

        setContentPane(mainPanel);
        setSize(800, 500);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        setLocationRelativeTo(null);
    }

    // Click the Check button in SubmissionGUI
    public CorrectOrCheckGUI(User user, Assignment assignment, int submissionOrder) {
        setTitle("Check out your assignment");
        cardLabel.setText(assignment.getAssignmentName());
        cardLayout.show(cardPanel, "assignmentCard");
        assignmentContent.setText(assignment.getAssignmentContent());
        assignmentContent.setEditable(false);
        clearButton.setVisible(false);

        Submission submission = submissionDao.getSubmissionFromTwoIDsAndOrder(assignment.getAssignmentID(), user.getUserId(), submissionOrder);
        submissionContent.setText(submission.getSubmissionContent());
        submissionContent.setEditable(false);

        operationButton.setText("Delete");
        if (submission.getSubmissionStatus().matches("Already corrected")) {
            operationButton.setVisible(false);
        }
        operationButton.addActionListener(e -> {
            if (submissionDao.deleteSubmission(submission)) {
                FrameUtil.showConfirmation(CorrectOrCheckGUI.this, "Delete successfully!");
            } else {
                FrameUtil.showConfirmation(CorrectOrCheckGUI.this, "Something wrong!");
            }
            new SubmissionGUI(user, assignment);
        });

        if (submission.getEvaluation() == null || submission.getEvaluation().isEmpty()) {
            evaluationLayout.show(evaluationPanel, "nullCard");
        } else {
            evaluationLayout.show(evaluationPanel, "evaluationCard");
            evaluationContent.setText(submission.getEvaluation());
            evaluationContent.setEditable(false);
        }

        backButton.addActionListener(e -> {
            CorrectOrCheckGUI.this.dispose();
            new SubmissionGUI(user, assignment);
        });

        scoresLayout.show(scoresPanel, "concreteScoresCard");
        if (submission.getScores() == -1) {
            concreteScores.setText("No scores yet");
        } else {
            concreteScores.setText(Integer.toString(submission.getScores()));
        }

        setContentPane(mainPanel);
        setSize(600, 500);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        setLocationRelativeTo(null);
    }

}
