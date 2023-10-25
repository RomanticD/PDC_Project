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
    private final SubmissionDao submissionDao = new SubmissionDao();
    private JPanel submissionPanel;
    private JToolBar toolBar1;
    private JButton backButton;
    private JComboBox<String> comboBox;
    private JButton clearButton;
    private JButton submitButton;
    private JPanel mainPanel;
    private JLabel assignmentLabel;
    private JLabel programmeLabel;
    private JTextArea submissionContent;
    private JTextArea assignmentContent;
    private JTable submissionTable;
    private JLabel historyLabel;
    private JButton deleteButton;
    private JPanel assignmentCard;
    private JLabel nullContentLabel;
    private JButton checkHistoryButton;

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
                FrameUtil.showConfirmation(SubmissionGUI.this, user, "Submit successfully!");
            } else {
                FrameUtil.showConfirmation(SubmissionGUI.this, user, "Something wrong!");
            }
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

        setContentPane(submissionPanel);
        setTitle("Submit your assignment or manage your history");
        setSize(750, 600);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        setLocationRelativeTo(null);
    }

    private static class TimestampRenderer extends DefaultTableCellRenderer {
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
