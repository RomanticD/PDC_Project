package gui;

import dao.SubmissionDaoInterface;
import domain.Assignment;
import domain.Submission;
import domain.User;
import dao.impl.SubmissionDao;
import util.FrameUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.text.*;
import java.util.List;
import java.util.Date;

public class SubmissionGUI extends JFrame {
    private final SubmissionDaoInterface submissionDao = new SubmissionDao();
    private JPanel submissionPanel;
    private JToolBar toolBar1;
    private JButton backButton;
    private JButton clearButton;
    private JButton submitButton;
    private JPanel mainPanel;
    private JLabel assignmentLabel;
    private JLabel programmeLabel;
    private JTextArea submissionContent;
    private JTextArea assignmentContent;
    private JTable submissionTable;
    private JLabel historyLabel;
    private JPanel assignmentCard;
    private JLabel nullContentLabel;
    private JButton checkHistoryButton;
    private JButton uploadButton;

    public SubmissionGUI(User user, Assignment assignment) {
        SubmissionDaoInterface submissionDao = new SubmissionDao();

        assignmentLabel.setText(assignment.getAssignmentName());
        assignmentContent.setEditable(false);

        CardLayout cardLayout = (CardLayout) assignmentCard.getLayout();
        if(assignment.getAssignmentContent() == null || assignment.getAssignmentContent().isEmpty()){
            cardLayout.show(assignmentCard, "nullContentCard");
        } else {
            cardLayout.show(assignmentCard, "contentCard");
            assignmentContent.setText(assignment.getAssignmentContent());
        }

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
                FrameUtil.showConfirmation(SubmissionGUI.this, "Submit successfully!");
            } else {
                FrameUtil.showConfirmation(SubmissionGUI.this, "Something wrong!");
            }
            new SubmissionGUI(user, assignment);
        });

        DefaultTableModel tableModel = new DefaultTableModel();
        submissionTable.setModel(tableModel);
        tableModel.addColumn("Order");
        tableModel.addColumn("Time");
        tableModel.addColumn("Status");
        TableColumn orderColumn = submissionTable.getColumnModel().getColumn(0);
        orderColumn.setPreferredWidth(5);
        orderColumn = submissionTable.getColumnModel().getColumn(1);
        orderColumn.setPreferredWidth(80);

        String[] rowName = {
                "Order",
                "Time",
                "Status"
        };
        tableModel.addRow(rowName);

        List<Submission> submissionList = submissionDao.getSubmissionsOfOneAssignmentAndStudent(assignment.getAssignmentID(), user.getUserId());
        for (Submission submission : submissionList) {
            Object[] rowData = {
                    submission.getSubmissionOrder(),
                    submission.getSubmissionTime(),
                    submission.getSubmissionStatus()

            };
            tableModel.addRow(rowData);
        }
        TableColumn timeColumn = submissionTable.getColumnModel().getColumn(1);
        timeColumn.setCellRenderer(new TimestampRenderer());

        checkHistoryButton.addActionListener(e -> {
            int selectedRow = submissionTable.getSelectedRow();
            if (selectedRow >= 1) {
                SubmissionGUI.this.dispose();
                int submissionOrder = (int)tableModel.getValueAt(selectedRow, 0);
                new CorrectOrCheckGUI(user, assignment, submissionOrder);
            } else {
                FrameUtil.showConfirmation(SubmissionGUI.this, "You haven't select any submission!");
                new SubmissionGUI(user, assignment);
            }
        });

        uploadButton.addActionListener(FrameUtil.uploadAction());

        setContentPane(submissionPanel);
        setTitle("Submit your assignment or manage your history");
        setSize(750, 600);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        setLocationRelativeTo(null);
    }

    public static class TimestampRenderer extends DefaultTableCellRenderer {
        private final SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd HH:mm:ss");

        @Override
        protected void setValue(Object value) {
            if (value instanceof Date) {
                value = sdf.format(value); // Format the timestamp as desired
            }
            super.setValue(value);
        }
    }
}
