package gui;

import dao.impl.SubmissionDao;
import domain.Assignment;
import domain.Submission;
import domain.User;
import util.FrameUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.List;

public class CorrectOrCheckGUI extends JFrame{
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

    SubmissionDao submissionDao = new SubmissionDao();

    CardLayout cardLayout = (CardLayout) cardPanel.getLayout();

    CardLayout evaluationLayout = (CardLayout) evaluationPanel.getLayout();

    // Click the Correct button in ManageAssignmentGUI
    public CorrectOrCheckGUI(User user, Assignment assignment){
        setTitle("Correct an assignment");
        cardLabel.setText("Submissions:");
        cardLayout.show(cardPanel, "submissionCard");

        DefaultTableModel tableModel = new DefaultTableModel();
        submissionTable.setModel(tableModel);
        tableModel.addColumn("StudentID");
        tableModel.addColumn("Name");
        tableModel.addColumn("Time");
        tableModel.addColumn("Order");
        tableModel.addColumn("Status");
        String[] rowName = {
                "StudentID",
                "Name",
                "Time",
                "Order",
                "Status"
        };
        tableModel.addRow(rowName);

        List<Submission> submissionList = submissionDao.getSubmissionsOfOneAssignmentAndStudent(assignment.getAssignmentID(), user.getUserId());
        for (Submission submission : submissionList) {
            Object[] rowData = {
                    submission.getStudentID(),
                    user.getUsername(),
                    submission.getSubmissionTime(),
                    submission.getSubmissionOrder(),
                    submission.getSubmissionStatus()
            };
            tableModel.addRow(rowData);
        }
        TableColumn timeColumn = submissionTable.getColumnModel().getColumn(2);
        timeColumn.setCellRenderer(new SubmissionGUI.TimestampRenderer());

        operationButton.setText("Correct");

        backButton.addActionListener(e -> {
            CorrectOrCheckGUI.this.dispose();
            new ManageAssignmentGUI(user);
        });

        setContentPane(mainPanel);
        setSize(800, 500);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        setLocationRelativeTo(null);
    }

    // Click the Check button in SubmissionGUI
    public CorrectOrCheckGUI(User user, Assignment assignment, int submissionOrder){
        setTitle("Check out your assignment");
        cardLabel.setText(assignment.getAssignmentName());
        cardLayout.show(cardPanel, "assignmentCard");
        assignmentContent.setText(assignment.getAssignmentContent());

        Submission submission = submissionDao.getSubmissionFromTwoIDsAndOrder(assignment.getAssignmentID(), user.getUserId(), submissionOrder);
        submissionContent.setText(submission.getSubmissionContent());

        operationButton.setText("Delete");
        operationButton.addActionListener(e -> {
            if(submissionDao.deleteSubmission(submission)){
                FrameUtil.showConfirmation(CorrectOrCheckGUI.this, "Delete successfully!");
            } else {
                FrameUtil.showConfirmation(CorrectOrCheckGUI.this, "Something wrong!");
            }
            new SubmissionGUI(user, assignment);
        });

        if(submission.getEvaluation() == null || submission.getEvaluation().isEmpty()){
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

        setContentPane(mainPanel);
        setSize(600, 500);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        setLocationRelativeTo(null);
    }
}
